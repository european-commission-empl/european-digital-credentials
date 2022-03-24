package eu.europa.ec.empl.edci.issuer.service.open;

import eu.europa.ec.empl.edci.constants.*;
import eu.europa.ec.empl.edci.datamodel.model.base.CredentialHolderDTO;
import eu.europa.ec.empl.edci.datamodel.model.dataTypes.SchemaLocation;
import eu.europa.ec.empl.edci.datamodel.model.verifiable.presentation.EuropassPresentationDTO;
import eu.europa.ec.empl.edci.datamodel.validation.ValidationResult;
import eu.europa.ec.empl.edci.dss.constants.DSSConstants;
import eu.europa.ec.empl.edci.dss.service.DSSEDCICertificateService;
import eu.europa.ec.empl.edci.dss.service.DSSEDCISignService;
import eu.europa.ec.empl.edci.exception.ApiErrorMessage;
import eu.europa.ec.empl.edci.exception.EDCIException;
import eu.europa.ec.empl.edci.exception.EDCIRestException;
import eu.europa.ec.empl.edci.exception.clientErrors.EDCIBadRequestException;
import eu.europa.ec.empl.edci.issuer.common.constants.EDCIIssuerMessageKeys;
import eu.europa.ec.empl.edci.issuer.common.constants.IssuerConfig;
import eu.europa.ec.empl.edci.issuer.common.constants.Parameter;
import eu.europa.ec.empl.edci.issuer.common.model.CredentialUploadResponseDTO;
import eu.europa.ec.empl.edci.issuer.common.model.open.PublicBatchSealingDTO;
import eu.europa.ec.empl.edci.issuer.common.model.open.PublicSealAndSendDTO;
import eu.europa.ec.empl.edci.issuer.common.model.open.PublicSealingDTO;
import eu.europa.ec.empl.edci.issuer.service.EDCIExecutorService;
import eu.europa.ec.empl.edci.issuer.service.IssuerConfigService;
import eu.europa.ec.empl.edci.issuer.service.IssuerFileService;
import eu.europa.ec.empl.edci.issuer.util.CertificateUtils;
import eu.europa.ec.empl.edci.issuer.util.FileUtil;
import eu.europa.ec.empl.edci.model.EDCIByteArrayMultiPartFile;
import eu.europa.ec.empl.edci.service.EDCIFileService;
import eu.europa.ec.empl.edci.service.EDCIMailService;
import eu.europa.ec.empl.edci.service.EDCIMessageService;
import eu.europa.ec.empl.edci.util.EDCICredentialModelUtil;
import eu.europa.ec.empl.edci.util.Validator;
import eu.europa.ec.empl.edci.util.WalletResourceUtil;
import eu.europa.ec.empl.edci.util.XmlUtil;
import eu.europa.esig.dss.model.DSSDocument;
import org.apache.commons.io.FileUtils;
import org.apache.http.entity.ContentType;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.xml.sax.SAXException;

import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class CredentialPublicService {

    private Logger logger = Logger.getLogger(CredentialPublicService.class);

    @Autowired
    private EDCIFileService edciFileService;

    @Autowired
    private FileUtil fileUtil;

    @Autowired
    private IssuerFileService fileService;

    @Autowired
    private IssuerConfigService issuerConfigService;

    @Autowired
    private DSSEDCISignService dssedciSignService;

    @Autowired
    private DSSEDCICertificateService dssedciCertificateService;

    @Autowired
    private CertificateUtils certificateUtils;

    @Autowired
    private EDCICredentialModelUtil edciCredentialModelUtil;

    @Autowired
    private EDCIMailService edciMailService;

    @Autowired
    private XmlUtil xmlUtil;

    @Autowired
    private WalletResourceUtil walletResourceUtil;

    @Autowired
    private EDCIMessageService edciMessageService;

    @Autowired
    private EDCIExecutorService edciExecutorService;

    @Autowired
    private Validator validator;

    /**
     * Submits tasks for sealing credentials with local sealing. If an error is detected on one of the credentials, none will be sent.
     *
     * @param publicBatchSealingDTO The set of files and the local cert password
     * @return error messages or null if there are no errors
     */
    public ApiErrorMessage doBatchSealAndSendCredentials(PublicBatchSealingDTO publicBatchSealingDTO) {
        ApiErrorMessage apiErrorMessage = null;
        //Check certificate
        String certPath = this.getIssuerConfigService().getString(EDCIConfig.DSS.CERT_PATH);
        if (!this.getDssedciCertificateService().checkCertificate(certPath, publicBatchSealingDTO.getPassword())) {
            throw new EDCIException(HttpStatus.INTERNAL_SERVER_ERROR, ErrorCode.CANNOT_READ_LOCAL_CERTIFICATE, EDCIMessageKeys.Exception.DSS.CANNOT_READ_LOCAL_CERTIFICATE);
        }
        int maximumConsecutiveFailures = this.getIssuerConfigService().getInteger(IssuerConfig.Issuer.MAX_CONSECUTIVE_ERRORS_BATCH_SEALING, 5);
        int maximumTotalFailures = this.getIssuerConfigService().getInteger(IssuerConfig.Issuer.MAX_ERRORS_BATCH_SEALING, 15);
        int consecutiveFailures = 0;
        int totalFailures = 0;
        // Check credential format, if any credential has a bad format stop process
        for (int i = 0; i < publicBatchSealingDTO.getFiles().length; i++) {
            if (consecutiveFailures >= maximumConsecutiveFailures || totalFailures >= maximumTotalFailures) {
                throw new EDCIException().addDescription(String.format("The maximum number of consecutive failures(%d) or total amount of failures(%d) has been exceeded", maximumConsecutiveFailures, maximumTotalFailures));
            }
            try {
                CredentialHolderDTO credentialHolderDTO = this.readAndCheckCredential(publicBatchSealingDTO.getFiles()[i]);
                if (credentialHolderDTO == null) throw new EDCIBadRequestException();
                consecutiveFailures = 0;
            } catch (EDCIBadRequestException e) {
                consecutiveFailures++;
                totalFailures++;
                if (apiErrorMessage == null) apiErrorMessage = new ApiErrorMessage();
                apiErrorMessage.setMessage(this.getEdciMessageService().getMessage(EDCIMessageKeys.Exception.BadRquest.UPLOAD_CREDENTIAL_NOT_READABLE));
                apiErrorMessage.setCode(e.getCode().toString());
                apiErrorMessage.putAffectedAsset(publicBatchSealingDTO.getFiles()[i].getOriginalFilename(), this.getEdciMessageService().getMessage(e.getMessageKey()));
            }
        }
        //If no errors, proceed with batch sealing
        if (apiErrorMessage == null) {
            String executorName = "localSealBatchExecutor";
            int numThreads = this.getIssuerConfigService().getInteger(IssuerConfig.Threads.LOCAL_BATCH_SIGN_NUM_THREADS, Runtime.getRuntime().availableProcessors());
            EDCIExecutorService edciExecutorService = new EDCIExecutorService();
            edciExecutorService.createExecutor(executorName, numThreads);
            for (int i = 0; i < publicBatchSealingDTO.getFiles().length; i++) {
                String fileFolder = this.getFileUtil().getCredentialPublicFolderName();
                String fileName = publicBatchSealingDTO.getFiles()[i].getOriginalFilename() + new Date().getTime() + EDCIConstants.XML.EXTENSION_XML;
                this.createTemporalFile(fileName, fileFolder, publicBatchSealingDTO.getFiles()[i]);
                edciExecutorService.submitTask(executorName, () -> {
                    String filePath = fileFolder.concat(fileName);
                    try {
                        MultipartFile credentialFile = new EDCIByteArrayMultiPartFile(fileName, Files.readAllBytes(Paths.get(filePath)), ContentType.APPLICATION_XML);
                        this.doLocalSignAndSendCredential(new PublicSealingDTO(credentialFile, publicBatchSealingDTO.getPassword(), publicBatchSealingDTO.isSignOnBehalf()));
                        Files.deleteIfExists(Paths.get(filePath));
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
        }


        return apiErrorMessage;
    }

    /**
     * Do sign an europass credential using local certificate and download sealed bytes
     *
     * @param publicSealingDTO the signature request, with file and cert password
     * @return the signed bytes
     */
    public byte[] doLocalSignAndDownloadCredential(PublicSealingDTO publicSealingDTO) {
        //read cred
        CredentialHolderDTO credentialHolderDTO = this.readAndCheckCredential(publicSealingDTO.getFile());
        return this.doLocalSignCredential(publicSealingDTO, credentialHolderDTO.getClass());
    }

    /**
     * Do sign an europass credential using local certificate and sent it to the wallet address defined inside,
     *
     * @param publicSealingDTO A credential and the password
     * @return the viewer URL
     */
    public PublicSealAndSendDTO doLocalSignAndSendCredential(PublicSealingDTO publicSealingDTO) {
        PublicSealAndSendDTO responseDTO = new PublicSealAndSendDTO();
        //read cred
        CredentialHolderDTO credentialHolderDTO = this.readAndCheckCredential(publicSealingDTO.getFile());
        //email/walletAddress check
        String email = this.getEdciCredentialModelUtil().getSubjectFirstEmail(credentialHolderDTO);
        String walletAddress = this.getEdciCredentialModelUtil().getSubjectFirstWalletAddress(credentialHolderDTO);
        boolean sendTemporal = this.getIssuerConfigService().getBoolean(IssuerConfig.Issuer.SEALING_API_SEND_TEMPORAL);
        if ((email == null && walletAddress == null) || (!sendTemporal && walletAddress == null)) {
            throw new EDCIBadRequestException(HttpStatus.BAD_REQUEST, ErrorCode.PUBLIC_CREDENTIAL_NO_CONTACT, EDCIIssuerMessageKeys.PUBLIC_CRED_NO_CONTACT);
        } else {
            //Get Bytess and create multipart file
            byte[] credentialBytes = this.doLocalSignCredential(publicSealingDTO, credentialHolderDTO.getClass());
            MultipartFile credentialFile = new EDCIByteArrayMultiPartFile(publicSealingDTO.getFile().getOriginalFilename(), credentialBytes, ContentType.APPLICATION_XML);
            String walletApiURL = "";
            //Decide wich wallet endpoint (mail/walletAddress) is going to be used, build apiURL
            if (walletAddress != null) {
                walletApiURL = this.getIssuerConfigService().getString(IssuerConfig.Issuer.WALLET_API_URL)
                        .concat(this.getIssuerConfigService().getString(IssuerConfig.Issuer.WALLET_ADD_PATH))
                        .replace(Parameter.WALLET_USER_ID, walletAddress);
            } else {
                walletApiURL = this.getIssuerConfigService().getString(IssuerConfig.Issuer.WALLET_API_URL)
                        .concat(this.getIssuerConfigService().getString(IssuerConfig.Issuer.WALLET_ADD_EMAIL_PATH))
                        .replace(Parameter.WALLET_USER_EMAIL, email);
            }
            //Send credentials to the wallet
            try {
                CredentialUploadResponseDTO credentialUploadResponseDTO = this.getWalletResourceUtil().doWalletPostRequest(walletApiURL, credentialFile, EDCIParameter.WALLET_ADD_CREDENTIAL_XML, CredentialUploadResponseDTO.class, MediaType.APPLICATION_JSON, false);
                responseDTO.setViewerURL(credentialUploadResponseDTO.getViewerURL());
            } catch (EDCIRestException e) {
                throw new EDCIBadRequestException(e.getHttpStatus(), e.getCode(), "EDCI-Wallet: " + e.getMessage());
            } catch (Exception e) {
                logger.error(String.format("Could not send credential to wallet %s", e.getMessage()), e);
                throw new EDCIException(HttpStatus.INTERNAL_SERVER_ERROR, ErrorCode.CANNOT_SEND_CONFIGURED_WALLET, EDCIIssuerMessageKeys.Exception.CANNOT_SEND_CONFIGURED_WALLET, walletApiURL);
            }
        }

        return responseDTO;
    }

    /**
     * Seals a single credential
     *
     * @param publicSealingDTO the credential and password
     * @param type             the type of the file (credential/presentation)
     * @return Signed bytes
     */
    private byte[] doLocalSignCredential(PublicSealingDTO publicSealingDTO, Class type) {
        //Get temporal file path
        String fileFolder = this.getFileUtil().getCredentialPublicFolderName();
        String fileName = publicSealingDTO.getFile().getOriginalFilename() + new Date().getTime() + EDCIConstants.XML.EXTENSION_XML;
        String filePath = fileFolder.concat(fileName);
        //Create temporal file and folders if they don't exist
        File credFile = this.createTemporalFile(fileName, fileFolder, publicSealingDTO.getFile());
        //Get Local Cert path
        String certPath = this.getIssuerConfigService().getString(EDCIConfig.DSS.CERT_PATH);
        String xPath = DSSConstants.EUROPASS_CREDENTIAL_XPATH_LOCATION;
        //if required, convert to verifiable presentation
        if (publicSealingDTO.isSignOnBehalf()) {
            try {
                CredentialHolderDTO credentialHolderDTO = this.getEdciCredentialModelUtil().fromFile(credFile);
                EuropassPresentationDTO europassPresentationDTO = this.getEdciCredentialModelUtil().createPresentation(credentialHolderDTO.getCredential());
                credFile = this.getFileService().createXMLFile(europassPresentationDTO, filePath);
                xPath = DSSConstants.VERIFIABLE_PRESENTATION_XPATH_LOCATION;
            } catch (IOException | JAXBException e) {
                logger.error(String.format("Error converting credential %s to Verifiable presentation", credFile.getPath()));
                throw new EDCIException(HttpStatus.INTERNAL_SERVER_ERROR, ErrorCode.CANNOT_SIGN_ON_BEHALF, EDCIMessageKeys.Exception.DSS.CANNOT_SIGN_ON_BEHALF);
            }
        }

        //overwrite certificate fields
        Map<String, String> certInfo = this.getDssedciCertificateService().getCertificateInfo(certPath, publicSealingDTO.getCertPassword());
        List<String> certificateErrors = this.getCertificateUtils().overwriteCertificateFields(filePath, certInfo, filePath);
        if (!certificateErrors.isEmpty()) {
            throw new EDCIException(HttpStatus.INTERNAL_SERVER_ERROR, ErrorCode.CANNOT_READ_LOCAL_CERTIFICATE, EDCIMessageKeys.Exception.DSS.CANNOT_READ_LOCAL_CERTIFICATE);
        }
        //Validate VS XSD
        try {
            SchemaLocation schemaLocation = this.getXmlUtil().getUniqueSchemaLocation(FileUtils.readFileToByteArray(credFile));
            ValidationResult xsdValidation = this.getXmlUtil().isValid(credFile, schemaLocation, type);
            //IF credential is not valid at this point, throw exception
            if (!xsdValidation.isValid()) {
                EDCIBadRequestException e = new EDCIBadRequestException(HttpStatus.BAD_REQUEST, ErrorCode.CREDENTIAL_INVALID_FORMAT, EDCIMessageKeys.Exception.BadRquest.UPLOAD_CREDENTIAL_BAD_FORMAT);
                e.addDescription(String.join(EDCIConstants.StringPool.STRING_SLASH + EDCIConstants.StringPool.STRING_SPACE, xsdValidation.getErrorMessages()));
                throw e;
            }
        } catch (IOException | SAXException | ParserConfigurationException | XPathExpressionException e) {
            throw new EDCIException();
        }
        //sign XML Credential
        DSSDocument dssDocument = this.getDssedciSignService().signXMLDocument(filePath, certPath, publicSealingDTO.getCertPassword(), xPath, false);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        //Write to output stream array
        try {
            dssDocument.writeTo(byteArrayOutputStream);
        } catch (IOException e) {
            logger.error("Could not write resulting signed bytes to output stream", e);
            throw new EDCIException(HttpStatus.INTERNAL_SERVER_ERROR, ErrorCode.CANNOT_WRITE_SIGNED_CRED, EDCIMessageKeys.Exception.DSS.CANNOT_WRITE_SIGNED_CRED);
        }
        //Delete temporal File
        File temporalFile = this.getEdciFileService().getOrCreateFile(filePath);
        if (!temporalFile.delete()) {
            logger.error(String.format("ERROR DELETING TEMPORAL FILE, CHECK %s", fileFolder));
        }
        return byteArrayOutputStream.toByteArray();
    }

    /**
     * Reads a credential multipart File, throws EDCIBadRequestException if it has an invalid format or it is already signed
     *
     * @param file
     * @return
     */
    private CredentialHolderDTO readAndCheckCredential(MultipartFile file) {
        CredentialHolderDTO credentialHolderDTO;
        byte[] unsignedBytes;
        //Read uploaded credential and check for signature
        try {
            credentialHolderDTO = this.getEdciCredentialModelUtil().fromByteArray(file.getBytes());
            unsignedBytes = file.getBytes();
            if (this.getDssedciSignService().isCredentialSigned(unsignedBytes)) {
                throw new EDCIBadRequestException(HttpStatus.BAD_REQUEST, ErrorCode.CREDENTIAL_ALREADY_SIGNED, EDCIIssuerMessageKeys.Sealing.CREDENTIAL_ALREADY_SIGNED);
            }
        } catch (JAXBException | IOException | ParserConfigurationException | SAXException e) {
            logger.error("Could not read uploaded credential", e);
            throw new EDCIBadRequestException(HttpStatus.BAD_REQUEST, ErrorCode.CREDENTIAL_NOT_READABLE, EDCIMessageKeys.Exception.BadRquest.UPLOAD_CREDENTIAL_NOT_READABLE);
        }
        return credentialHolderDTO;
    }

    private File createTemporalFile(String fileName, String folderPath, MultipartFile file) {
        String filePath = folderPath.concat(fileName);
        try {
            File temporalFile = this.getEdciFileService().getOrCreateFile(filePath);
            Files.createDirectories(Paths.get(folderPath));
            if (temporalFile.createNewFile()) {
                file.transferTo(temporalFile);
            } else {
                throw new EDCIException();
            }
            return temporalFile;
        } catch (IOException e) {
            EDCIBadRequestException edciException = new EDCIBadRequestException(HttpStatus.BAD_REQUEST,
                    ErrorCode.PUBLIC_CREDENTIAL_CANNOT_CREATE,
                    EDCIIssuerMessageKeys.CREDENTIAL_FILE_NOT_FOUND,
                    filePath);
            logger.error(String.format("Could not create temporal credential %s [%s]", folderPath.concat(fileName), e.getMessage()), e);
            throw edciException;
        }
    }

    public FileUtil getFileUtil() {
        return fileUtil;
    }

    public void setFileUtil(FileUtil fileUtil) {
        this.fileUtil = fileUtil;
    }

    public DSSEDCISignService getDssedciSignService() {
        return dssedciSignService;
    }

    public void setDssedciSignService(DSSEDCISignService dssedciSignService) {
        this.dssedciSignService = dssedciSignService;
    }

    public IssuerConfigService getIssuerConfigService() {
        return issuerConfigService;
    }

    public void setIssuerConfigService(IssuerConfigService issuerConfigService) {
        this.issuerConfigService = issuerConfigService;
    }

    public EDCICredentialModelUtil getEdciCredentialModelUtil() {
        return edciCredentialModelUtil;
    }

    public void setEdciCredentialModelUtil(EDCICredentialModelUtil edciCredentialModelUtil) {
        this.edciCredentialModelUtil = edciCredentialModelUtil;
    }

    public CertificateUtils getCertificateUtils() {
        return certificateUtils;
    }

    public void setCertificateUtils(CertificateUtils certificateUtils) {
        this.certificateUtils = certificateUtils;
    }

    public DSSEDCICertificateService getDssedciCertificateService() {
        return dssedciCertificateService;
    }

    public void setDssedciCertificateService(DSSEDCICertificateService dssedciCertificateService) {
        this.dssedciCertificateService = dssedciCertificateService;
    }

    public XmlUtil getXmlUtil() {
        return xmlUtil;
    }

    public void setXmlUtil(XmlUtil xmlUtil) {
        this.xmlUtil = xmlUtil;
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
}
