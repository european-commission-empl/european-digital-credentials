package eu.europa.ec.empl.edci.issuer.service.open;

import eu.europa.ec.empl.edci.constants.*;
import eu.europa.ec.empl.edci.datamodel.upload.EuropeanDigitalCredentialUploadDTO;
import eu.europa.ec.empl.edci.dss.service.certificate.ESealCertificateService;
import eu.europa.ec.empl.edci.dss.service.signature.ESealSignService;
import eu.europa.ec.empl.edci.dss.service.validation.ESealValidationService;
import eu.europa.ec.empl.edci.exception.ApiErrorMessage;
import eu.europa.ec.empl.edci.exception.EDCIException;
import eu.europa.ec.empl.edci.exception.EDCIRestException;
import eu.europa.ec.empl.edci.exception.clientErrors.EDCIBadRequestException;
import eu.europa.ec.empl.edci.issuer.common.constants.EDCIIssuerMessageKeys;
import eu.europa.ec.empl.edci.issuer.common.constants.IssuerConfig;
import eu.europa.ec.empl.edci.issuer.common.constants.Parameter;
import eu.europa.ec.empl.edci.issuer.common.model.CredentialDTO;
import eu.europa.ec.empl.edci.issuer.common.model.CredentialFileDTO;
import eu.europa.ec.empl.edci.issuer.common.model.CredentialUploadResponseDTO;
import eu.europa.ec.empl.edci.issuer.common.model.EuropeanDigitalCredentialUploadResultDTO;
import eu.europa.ec.empl.edci.issuer.common.model.open.PublicBatchSealingDTO;
import eu.europa.ec.empl.edci.issuer.common.model.open.PublicSealAndSendDTO;
import eu.europa.ec.empl.edci.issuer.common.model.open.PublicSealingDTO;
import eu.europa.ec.empl.edci.issuer.service.CredentialService;
import eu.europa.ec.empl.edci.issuer.service.EDCIExecutorService;
import eu.europa.ec.empl.edci.issuer.service.IssuerConfigService;
import eu.europa.ec.empl.edci.issuer.service.IssuerFileService;
import eu.europa.ec.empl.edci.issuer.util.CertificateUtils;
import eu.europa.ec.empl.edci.issuer.util.FileUtil;
import eu.europa.ec.empl.edci.model.EDCIByteArrayMultiPartFile;
import eu.europa.ec.empl.edci.service.EDCIFileService;
import eu.europa.ec.empl.edci.service.EDCIMailService;
import eu.europa.ec.empl.edci.service.EDCIMessageService;
import eu.europa.ec.empl.edci.util.CredentialUtil;
import eu.europa.ec.empl.edci.util.Validator;
import eu.europa.ec.empl.edci.util.WalletResourceUtil;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.routines.EmailValidator;
import org.apache.http.entity.ContentType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CredentialPublicService {

    private Logger logger = LogManager.getLogger(CredentialPublicService.class);

    @Autowired
    private EDCIFileService edciFileService;

    @Autowired
    private FileUtil fileUtil;

    @Autowired
    private IssuerFileService fileService;

    @Autowired
    private IssuerConfigService issuerConfigService;

    @Autowired
    private ESealSignService dssedciSignService;

    @Autowired
    private ESealCertificateService dssedciCertificateService;

    @Autowired
    private ESealValidationService eSealValidationService;

    @Autowired
    private CertificateUtils certificateUtils;

    @Autowired
    private EDCIMailService edciMailService;

    @Autowired
    private WalletResourceUtil walletResourceUtil;

    @Autowired
    private EDCIMessageService edciMessageService;

    @Autowired
    private EDCIExecutorService edciExecutorService;

    @Autowired
    private CredentialService credentialService;

    @Autowired
    private CredentialUtil credentialUtil;

    @Autowired
    private Validator validator;

    /**
     * Submits tasks for sealing credentials with local sealing. If an error is detected on one of the credentials, none will be sent.
     *
     * @param publicBatchSealingDTO The set of files and the local cert password
     * @return error messages or null if there are no errors
     */
    public ApiErrorMessage doBatchSealAndSendCredentials(String batchId, PublicBatchSealingDTO publicBatchSealingDTO) {
        ApiErrorMessage apiErrorMessage = null;
        //Check certificate
        String certPath = this.getIssuerConfigService().getString(EDCIConfig.DSS.CERT_PATH);
        if (!this.getDssedciCertificateService().checkCertificate(certPath, publicBatchSealingDTO.getCertPassword())) {
            throw new EDCIException(HttpStatus.INTERNAL_SERVER_ERROR, ErrorCode.CANNOT_READ_LOCAL_CERTIFICATE, EDCIMessageKeys.Exception.DSS.CANNOT_READ_LOCAL_CERTIFICATE);
        }
        int maximumConsecutiveFailures = this.getIssuerConfigService().getInteger(IssuerConfig.Issuer.MAX_CONSECUTIVE_ERRORS_BATCH_SEALING, 5);
        int maximumTotalFailures = this.getIssuerConfigService().getInteger(IssuerConfig.Issuer.MAX_ERRORS_BATCH_SEALING, 15);

        List<EuropeanDigitalCredentialUploadResultDTO> europeanDigitalCredentialUploadDTOS =
                credentialService.obtainCredentials(publicBatchSealingDTO.getFiles(), maximumConsecutiveFailures, maximumTotalFailures);

        if (europeanDigitalCredentialUploadDTOS.stream().anyMatch(cred -> cred.isBadFormat() || cred.isSigned() || cred.isErrorAddress())) {

            apiErrorMessage = new ApiErrorMessage();

            apiErrorMessage.setMessage(this.getEdciMessageService().getMessage("upload.credentials.bad.format.or.signed"));

            List<EuropeanDigitalCredentialUploadResultDTO> badFormatFailures =
                    europeanDigitalCredentialUploadDTOS.stream().filter(EuropeanDigitalCredentialUploadResultDTO::isBadFormat).collect(Collectors.toList());

            for (EuropeanDigitalCredentialUploadResultDTO cred : badFormatFailures) {
                apiErrorMessage.putAffectedAsset(cred.getFileName(), this.getEdciMessageService().getMessage("upload.credential.bad.format"));
            }

            List<EuropeanDigitalCredentialUploadResultDTO> signedFailures =
                    europeanDigitalCredentialUploadDTOS.stream().filter(EuropeanDigitalCredentialUploadResultDTO::isSigned).collect(Collectors.toList());

            for (EuropeanDigitalCredentialUploadResultDTO cred : signedFailures) {
                apiErrorMessage.putAffectedAsset(cred.getFileName(), this.getEdciMessageService().getMessage("upload.credential.already.signed"));
            }

            List<EuropeanDigitalCredentialUploadResultDTO> addressFailures =
                    europeanDigitalCredentialUploadDTOS.stream().filter(EuropeanDigitalCredentialUploadResultDTO::isErrorAddress).collect(Collectors.toList());

            int limit = this.getIssuerConfigService().getInteger(DataModelConstants.Properties.DELIVERY_ADDRESS_LIMIT, 2);

            for (EuropeanDigitalCredentialUploadResultDTO cred : addressFailures) {
                apiErrorMessage.putAffectedAsset(cred.getFileName(), this.getEdciMessageService().getMessage(EDCIIssuerMessageKeys.Sealing.CREDENTIALS_DELIVERY_ADDRESS_ERROR, limit));
            }

        }

        CredentialFileDTO credentialFiles = credentialService.uploadParsedCredentials(europeanDigitalCredentialUploadDTOS.stream()
                        .map(edcu -> (EuropeanDigitalCredentialUploadDTO) edcu).collect(Collectors.toList()),
                this.getFileUtil().getCredentialPublicFolderName(batchId), LocaleContextHolder.getLocale().getLanguage());

        //If no errors, proceed with batch sealing
        if (apiErrorMessage == null) {
            String executorName = "localSealBatchExecutor";
            int numThreads = this.getIssuerConfigService().getInteger(IssuerConfig.Threads.LOCAL_BATCH_SIGN_NUM_THREADS, Runtime.getRuntime().availableProcessors());
            EDCIExecutorService edciExecutorService = new EDCIExecutorService();
            edciExecutorService.createExecutor(executorName, numThreads);
            for (CredentialDTO credential : credentialFiles.getCredentials()) {

                String fileFolder = this.getFileUtil().getCredentialPublicFolderName(batchId);
                String fileName = this.getFileUtil().getFileName(URI.create(credential.getUuid()));

                edciExecutorService.submitTask(executorName, () -> {
                    String filePath = fileFolder.concat(fileName);
                    try {
                        MultipartFile credentialFile = new EDCIByteArrayMultiPartFile(fileName, Files.readAllBytes(Paths.get(filePath)), ContentType.APPLICATION_XML);
                        this.localSignAndSendCredential(new PublicSealingDTO(credentialFile, filePath, credential, publicBatchSealingDTO.getCertPassword(),
                                publicBatchSealingDTO.isSignOnBehalf()), batchId);
                        Files.deleteIfExists(Paths.get(filePath));
                        File folder = new File(fileFolder);
                        if (folder.isDirectory() && folder.listFiles().length == 0) {
                            Files.deleteIfExists(Paths.get(fileFolder));
                        }
                    } catch (IOException e) {
                        EDCIBadRequestException edciException = new EDCIBadRequestException(HttpStatus.BAD_REQUEST,
                                ErrorCode.PUBLIC_CREDENTIAL_CANNOT_CREATE,
                                EDCIIssuerMessageKeys.CREDENTIAL_FILE_NOT_FOUND,
                                filePath);
                        logger.error(String.format("Could not create temporal credential %s while sending batch [%s]", filePath, e.getMessage()), e);
                        throw edciException;
                    }
                });

            }
            edciExecutorService.shutdown(executorName, false);
        }

        return apiErrorMessage;
    }

    /**
     * Submits tasks for sealing credentials with local sealing. If an error is detected on one of the credentials, none will be sent.
     *
     * @param publicBatchSealingDTO The set of files and the local cert password
     * @return error messages or null if there are no errors
     */
    public PublicSealAndSendDTO doSealAndSendCredentials(PublicSealingDTO publicBatchSealingDTO) {

        PublicSealAndSendDTO returnValue = new PublicSealAndSendDTO();
        String folderId = UUID.randomUUID().toString().replaceAll("[^A-Za-z0-9]", "");
        String fileFolder = this.getFileUtil().getCredentialPublicFolderName(folderId);

        try {
            //Check certificate
            String certPath = this.getIssuerConfigService().getString(EDCIConfig.DSS.CERT_PATH);
            if (!this.getDssedciCertificateService().checkCertificate(certPath, publicBatchSealingDTO.getCertPassword())) {
                throw new EDCIException(HttpStatus.INTERNAL_SERVER_ERROR, ErrorCode.CANNOT_READ_LOCAL_CERTIFICATE, EDCIMessageKeys.Exception.DSS.CANNOT_READ_LOCAL_CERTIFICATE);
            }
            int maximumConsecutiveFailures = this.getIssuerConfigService().getInteger(IssuerConfig.Issuer.MAX_CONSECUTIVE_ERRORS_BATCH_SEALING, 5);
            int maximumTotalFailures = this.getIssuerConfigService().getInteger(IssuerConfig.Issuer.MAX_ERRORS_BATCH_SEALING, 15);

            List<EuropeanDigitalCredentialUploadResultDTO> europeanDigitalCredentialUploadDTOS =
                    credentialService.obtainCredentials(Arrays.asList(publicBatchSealingDTO.getFile()).toArray(new MultipartFile[0]), maximumConsecutiveFailures, maximumTotalFailures);

            List<EuropeanDigitalCredentialUploadResultDTO> addressFailures =
                    europeanDigitalCredentialUploadDTOS.stream().filter(EuropeanDigitalCredentialUploadResultDTO::isErrorAddress).collect(Collectors.toList());

            int limit = this.getIssuerConfigService().getInteger(DataModelConstants.Properties.DELIVERY_ADDRESS_LIMIT, 2);

            if (addressFailures != null && !addressFailures.isEmpty()) {
                throw new EDCIException(EDCIIssuerMessageKeys.Sealing.CREDENTIALS_DELIVERY_ADDRESS_ERROR, String.valueOf(limit));
            }

            CredentialFileDTO credentialFiles = credentialService.uploadParsedCredentials(europeanDigitalCredentialUploadDTOS.stream()
                            .map(edcu -> (EuropeanDigitalCredentialUploadDTO) edcu).collect(Collectors.toList()),
                    fileFolder, LocaleContextHolder.getLocale().getLanguage());

            //If no errors, proceed with batch sealing
            if (credentialFiles.getCredentials() != null && !credentialFiles.getCredentials().isEmpty()) {

                CredentialDTO credential = credentialFiles.getCredentials().get(0);
                String fileName = this.getFileUtil().getFileName(URI.create(credential.getUuid()));

                String filePath = fileFolder.concat(fileName);
                try {
                    MultipartFile credentialFile = new EDCIByteArrayMultiPartFile(fileName, Files.readAllBytes(Paths.get(filePath)), ContentType.APPLICATION_XML);
                    PublicSealAndSendDTO sentCredential = this.localSignAndSendCredential(
                            new PublicSealingDTO(credentialFile, filePath, credential, publicBatchSealingDTO.getCertPassword(), publicBatchSealingDTO.isSignOnBehalf()), folderId);
                    returnValue.setViewerURL(sentCredential.getViewerURL());
                    Files.deleteIfExists(Paths.get(filePath));
                } catch (IOException e) {
                    EDCIBadRequestException edciException = new EDCIBadRequestException(HttpStatus.BAD_REQUEST,
                            ErrorCode.PUBLIC_CREDENTIAL_CANNOT_CREATE,
                            EDCIIssuerMessageKeys.CREDENTIAL_FILE_NOT_FOUND,
                            filePath);
                    logger.error(String.format("Could not create temporal credential %s while sending batch [%s]", filePath, e.getMessage()), e);
                    throw edciException;
                }
            }
        } finally {
            File folderFile = this.getEdciFileService().getOrCreateFile(fileFolder);
            try {
                FileUtils.deleteDirectory(folderFile);
            } catch (IOException e) {
                logger.error(e);
            }
        }

        return returnValue;
    }

    /**
     * Do sign an european digital credential using local certificate and download sealed bytes
     *
     * @param publicSealingDTO the signature request, with file and cert password
     * @param needsToBeSigned
     * @return the signed bytes
     */
    public byte[] doLocalSignOrCreateAndDownloadCredential(PublicSealingDTO publicSealingDTO, boolean needsToBeSigned) {

        byte[] returnBytes = null;
        String folderId = UUID.randomUUID().toString().replaceAll("[^A-Za-z0-9]", "");
        String fileFolder = this.getFileUtil().getCredentialPublicFolderName(folderId);

        try {

            //Check certificate
            if (needsToBeSigned) {
                String certPath = this.getIssuerConfigService().getString(EDCIConfig.DSS.CERT_PATH);
                if (!this.getDssedciCertificateService().checkCertificate(certPath, publicSealingDTO.getCertPassword())) {
                    throw new EDCIException(HttpStatus.INTERNAL_SERVER_ERROR, ErrorCode.CANNOT_READ_LOCAL_CERTIFICATE, EDCIMessageKeys.Exception.DSS.CANNOT_READ_LOCAL_CERTIFICATE);
                }
            }

            List<EuropeanDigitalCredentialUploadResultDTO> europeanDigitalCredentialUploadDTOS =
                    credentialService.obtainCredentials(Arrays.asList(publicSealingDTO.getFile()).toArray(new MultipartFile[0]), -1, -1);

            EuropeanDigitalCredentialUploadResultDTO result = europeanDigitalCredentialUploadDTOS.get(0);

            if (result.isBadFormat() || result.isSigned()) {
                throw new EDCIException("upload.credentials.bad.format.or.signed", result.getFileName());
            }

            if (result.isErrorAddress()) {
                int limit = this.getIssuerConfigService().getInteger(DataModelConstants.Properties.DELIVERY_ADDRESS_LIMIT, 2);

                throw new EDCIException(EDCIIssuerMessageKeys.Sealing.CREDENTIALS_DELIVERY_ADDRESS_ERROR_WITH_FILE, String.valueOf(limit), result.getFileName());
            }

            CredentialFileDTO credentialFiles = credentialService.uploadParsedCredentials(europeanDigitalCredentialUploadDTOS.stream()
                            .map(edcu -> (EuropeanDigitalCredentialUploadDTO) edcu).collect(Collectors.toList()),
                    fileFolder, LocaleContextHolder.getLocale().getLanguage());

            if (credentialFiles.getCredentials() != null && !credentialFiles.getCredentials().isEmpty()) {
                CredentialDTO credential = credentialFiles.getCredentials().get(0);
                fileFolder = this.getFileUtil().getCredentialPublicFolderName(folderId);
                String fileName = this.getFileUtil().getFileName(URI.create(credential.getUuid()));
                String filePath = fileFolder.concat(fileName);

                //Get Bytess and create multipart file
                if (needsToBeSigned) {
                    credentialService.signFromLocalCert(Arrays.asList(credential), publicSealingDTO.getCertPassword(),
                            Boolean.toString(publicSealingDTO.isSignOnBehalf()), fileFolder, folderId, null);
                }

                try {
                    returnBytes = Files.readAllBytes(Paths.get(filePath));
                } catch (IOException e) {
                    EDCIBadRequestException edciException = new EDCIBadRequestException(HttpStatus.BAD_REQUEST,
                            ErrorCode.PUBLIC_CREDENTIAL_CANNOT_CREATE,
                            EDCIIssuerMessageKeys.CREDENTIAL_FILE_NOT_FOUND,
                            filePath);
                    logger.error(String.format("Could not create temporal credential %s while sending batch [%s]", filePath, e.getMessage()), e);
                    throw edciException;
                }
            } else {
                throw new EDCIException();
            }

        } finally {
            File folderFile = this.getEdciFileService().getOrCreateFile(fileFolder);
            try {
                FileUtils.deleteDirectory(folderFile);
            } catch (IOException e) {
                logger.error(e);
            }
        }

        return returnBytes;

    }

    /**
     * Do sign an europass credential using local certificate and sent it to the wallet address and emails defined inside,
     *
     * @param publicSealingDTO A credential and the password
     * @return the viewer URL
     */
    protected PublicSealAndSendDTO localSignAndSendCredential(PublicSealingDTO publicSealingDTO, String batchId) {
        PublicSealAndSendDTO responseDTO = new PublicSealAndSendDTO();
        //email/walletAddress check
        int emailSize = publicSealingDTO.getCredential().getEmail().size();
        int walletSize = publicSealingDTO.getCredential().getWalletAddress().size();

        boolean sendTemporal = this.getIssuerConfigService().getBoolean(IssuerConfig.Issuer.SEALING_API_SEND_TEMPORAL);
        if ((emailSize == 0 && walletSize == 0) || (!sendTemporal && walletSize == 0)) {
            throw new EDCIBadRequestException(HttpStatus.BAD_REQUEST, ErrorCode.PUBLIC_CREDENTIAL_NO_CONTACT, EDCIIssuerMessageKeys.PUBLIC_CRED_NO_CONTACT);
        } else {
            String fileFolder = fileUtil.getCredentialPublicFolderName(batchId);
            //Get Bytes and create multipart file
            credentialService.signFromLocalCert(Arrays.asList(publicSealingDTO.getCredential()), publicSealingDTO.getCertPassword(),
                    Boolean.toString(publicSealingDTO.isSignOnBehalf()), fileFolder, batchId, null);
            byte[] credentialBytes = null;
            try {
                credentialBytes = fileUtil.getFileBytes(publicSealingDTO.getFilePath());
            } catch (Exception e) {
                throw new EDCIException();
            }
            MultipartFile credentialFile = new EDCIByteArrayMultiPartFile(publicSealingDTO.getFile().getOriginalFilename(), credentialBytes, ContentType.APPLICATION_XML);
            String walletApiURL = this.getIssuerConfigService().getString(IssuerConfig.Issuer.WALLET_API_URL)
                    .concat(this.getIssuerConfigService().getString(IssuerConfig.Issuer.WALLET_ADD_PATH));
            Map<String, String> parameters = new HashMap<>();

            for (String email : publicSealingDTO.getCredential().getEmail()) {
                try {
                    if (!this.getValidator().isEmpty(email) && !EmailValidator.getInstance().isValid(email.replaceFirst("mailto:", ""))) {
                        throw new EDCIException(this.getEdciMessageService().getMessage(EDCIIssuerMessageKeys.ERROR_INVALID_EMAIL, email.replaceFirst("mailto:", "")));
                    }
                    parameters.put(Parameter.WALLET_USER_EMAIL, email);
                    CredentialUploadResponseDTO credentialUploadResponseDTO = this.getWalletResourceUtil()
                            .doWalletPostRequest(walletApiURL, credentialFile, EDCIParameter.WALLET_ADD_CREDENTIAL,
                                    CredentialUploadResponseDTO.class, MediaType.APPLICATION_JSON, false, parameters);

                    if (credentialUploadResponseDTO.getViewerURL() != null && !StringUtils.isEmpty(credentialUploadResponseDTO.getViewerURL())) {
                        responseDTO.getViewerURL().add(credentialUploadResponseDTO.getViewerURL());
                    }
                } catch (EDCIRestException e) {
                    throw new EDCIBadRequestException(e.getHttpStatus(), e.getCode(), "EDCI-Wallet: " + e.getMessage());
                } catch (EDCIException e) {
                    throw e;
                } catch (Exception e) {
                    logger.error(String.format("Could not send credential to wallet %s", e.getMessage()), e);
                    throw new EDCIException(HttpStatus.INTERNAL_SERVER_ERROR, ErrorCode.CANNOT_SEND_CONFIGURED_WALLET, EDCIIssuerMessageKeys.Exception.CANNOT_SEND_CONFIGURED_WALLET, walletApiURL);
                }
                parameters.remove(Parameter.WALLET_USER_EMAIL);
            }

            for (String walletAddress : publicSealingDTO.getCredential().getWalletAddress()) {
                try {

                    parameters.put(Parameter.WALLET_ADDRESS, walletAddress);
                    CredentialUploadResponseDTO credentialUploadResponseDTO = this.getWalletResourceUtil()
                            .doWalletPostRequest(walletApiURL, credentialFile, EDCIParameter.WALLET_ADD_CREDENTIAL,
                                    CredentialUploadResponseDTO.class, MediaType.APPLICATION_JSON, false, parameters);

                    if (credentialUploadResponseDTO.getViewerURL() != null && !StringUtils.isEmpty(credentialUploadResponseDTO.getViewerURL())) {
                        responseDTO.getViewerURL().add(credentialUploadResponseDTO.getViewerURL());
                    }
                } catch (EDCIRestException e) {
                    throw new EDCIBadRequestException(e.getHttpStatus(), e.getCode(), "EDCI-Wallet: " + e.getMessage());
                } catch (Exception e) {
                    logger.error(String.format("Could not send credential to wallet %s", e.getMessage()), e);
                    throw new EDCIException(HttpStatus.INTERNAL_SERVER_ERROR, ErrorCode.CANNOT_SEND_CONFIGURED_WALLET, EDCIIssuerMessageKeys.Exception.CANNOT_SEND_CONFIGURED_WALLET, walletApiURL);
                }
                parameters.remove(Parameter.WALLET_ADDRESS);
            }
        }

        return responseDTO;
    }

    public FileUtil getFileUtil() {
        return fileUtil;
    }

    public void setFileUtil(FileUtil fileUtil) {
        this.fileUtil = fileUtil;
    }

    public ESealSignService getDssedciSignService() {
        return dssedciSignService;
    }

    public void setDssedciSignService(ESealSignService dssedciSignService) {
        this.dssedciSignService = dssedciSignService;
    }

    public IssuerConfigService getIssuerConfigService() {
        return issuerConfigService;
    }

    public void setIssuerConfigService(IssuerConfigService issuerConfigService) {
        this.issuerConfigService = issuerConfigService;
    }

    public CertificateUtils getCertificateUtils() {
        return certificateUtils;
    }

    public void setCertificateUtils(CertificateUtils certificateUtils) {
        this.certificateUtils = certificateUtils;
    }

    public ESealCertificateService getDssedciCertificateService() {
        return dssedciCertificateService;
    }

    public void setDssedciCertificateService(ESealCertificateService dssedciCertificateService) {
        this.dssedciCertificateService = dssedciCertificateService;
    }

    public EDCIMailService getEdciMailService() {
        return edciMailService;
    }

    public void setEdciMailService(EDCIMailService edciMailService) {
        this.edciMailService = edciMailService;
    }

    public WalletResourceUtil getWalletResourceUtil() {
        return walletResourceUtil;
    }

    public void setWalletResourceUtil(WalletResourceUtil walletResourceUtil) {
        this.walletResourceUtil = walletResourceUtil;
    }

    public EDCIExecutorService getEdciExecutorService() {
        return edciExecutorService;
    }

    public void setEdciExecutorService(EDCIExecutorService edciExecutorService) {
        this.edciExecutorService = edciExecutorService;
    }

    public Validator getValidator() {
        return validator;
    }

    public void setValidator(Validator validator) {
        this.validator = validator;
    }

    public EDCIMessageService getEdciMessageService() {
        return edciMessageService;
    }

    public void setEdciMessageService(EDCIMessageService edciMessageService) {
        this.edciMessageService = edciMessageService;
    }

    public IssuerFileService getFileService() {
        return fileService;
    }

    public void setFileService(IssuerFileService fileService) {
        this.fileService = fileService;
    }

    public EDCIFileService getEdciFileService() {
        return edciFileService;
    }

    public void setEdciFileService(EDCIFileService edciFileService) {
        this.edciFileService = edciFileService;
    }

    public ESealValidationService getESealValidationService() {
        return eSealValidationService;
    }

    public void setESealValidationService(ESealValidationService eSealValidationService) {
        this.eSealValidationService = eSealValidationService;
    }
}
