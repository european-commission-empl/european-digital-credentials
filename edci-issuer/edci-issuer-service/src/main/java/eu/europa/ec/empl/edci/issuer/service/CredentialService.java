package eu.europa.ec.empl.edci.issuer.service;

import com.apicatalog.jsonld.JsonLdError;
import eu.europa.ec.empl.edci.constants.*;
import eu.europa.ec.empl.edci.context.ConsumerContext;
import eu.europa.ec.empl.edci.datamodel.AttachmentView;
import eu.europa.ec.empl.edci.datamodel.jsonld.model.EuropeanDigitalCredentialDTO;
import eu.europa.ec.empl.edci.datamodel.jsonld.model.ShaclValidator2017;
import eu.europa.ec.empl.edci.datamodel.jsonld.model.dataTypes.ConceptDTO;
import eu.europa.ec.empl.edci.datamodel.validation.ValidationError;
import eu.europa.ec.empl.edci.datamodel.validation.ValidationResult;
import eu.europa.ec.empl.edci.dss.constants.ESealConfig;
import eu.europa.ec.empl.edci.dss.exception.ESealException;
import eu.europa.ec.empl.edci.dss.model.signature.SignatureBytesDTO;
import eu.europa.ec.empl.edci.dss.service.certificate.ESealCertificateService;
import eu.europa.ec.empl.edci.dss.service.signature.ESealSignService;
import eu.europa.ec.empl.edci.dss.service.validation.ESealValidationService;
import eu.europa.ec.empl.edci.dss.service.validation.JadesValidationService;
import eu.europa.ec.empl.edci.exception.EDCIException;
import eu.europa.ec.empl.edci.exception.EDCIRestException;
import eu.europa.ec.empl.edci.exception.clientErrors.EDCIBadRequestException;
import eu.europa.ec.empl.edci.exception.clientErrors.EDCINotFoundException;
import eu.europa.ec.empl.edci.factory.ConsumerFactory;
import eu.europa.ec.empl.edci.issuer.common.constants.*;
import eu.europa.ec.empl.edci.issuer.common.model.*;
import eu.europa.ec.empl.edci.issuer.mapper.CredentialMapper;
import eu.europa.ec.empl.edci.issuer.util.CertificateUtils;
import eu.europa.ec.empl.edci.issuer.util.CredentialServiceUtil;
import eu.europa.ec.empl.edci.issuer.util.DiplomaUtils;
import eu.europa.ec.empl.edci.issuer.util.FileUtil;
import eu.europa.ec.empl.edci.service.ControlledListCommonsService;
import eu.europa.ec.empl.edci.service.EDCIFileService;
import eu.europa.ec.empl.edci.service.EDCIMailService;
import eu.europa.ec.empl.edci.service.EDCIMessageService;
import eu.europa.ec.empl.edci.util.*;
import eu.europa.esig.dss.model.DSSDocument;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItem;
import org.apache.commons.io.IOUtils;
import org.apache.commons.validator.routines.EmailValidator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import javax.inject.Inject;
import java.io.*;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Service
public class CredentialService {
    private static final Logger logger = LogManager.getLogger(CredentialService.class);

    @Autowired
    private EDCIFileService edciFileService;

    @Autowired
    private ShaclInternal shaclInternal;

    @Autowired
    private EDCIMessageService edciMessageService;

    @Autowired
    private ESealSignService dssedciSignService;

    @Autowired
    private ESealCertificateService dssedciCertificateService;

    @Autowired
    private ESealValidationService eSealValidationService;

    @Autowired
    private IssuerConfigService issuerConfigService;

    @Autowired
    private CredentialUtil credentialUtil;

    @Autowired
    private ZipUtil zipUtil;

    @Autowired
    private Validator validator;

    @Inject
    private FileUtil fileUtil;

    @Autowired
    private CertificateUtils certificateUtils;

    @Autowired
    private IssuerFileService fileService;

    @Autowired
    private CredentialMapper credentialMapper;

    @Autowired
    private JsonLdUtil jsonLdUtil;

    @Autowired
    private ConsumerFactory consumerFactory;

    @Autowired
    private WalletResourceUtil walletResourceUtil;

    @Autowired
    private EDCIMailService edciMailService;

    @Autowired
    private EDCIValidationUtil edciValidationUtil;

    @Autowired
    private EDCIExecutorService edciExecutorService;

    @Autowired
    private DiplomaUtils diplomaUtils;

    @Autowired
    private ControlledListCommonsService controlledListCommonsService;

    @Autowired
    private CredentialServiceUtil credentialServiceUtil;

    @Autowired
    private JadesValidationService jadesValidationService;

    /**
     * Used to Issue credentials uploaded in JSON format inside of a file (credentials must follow EuropeanDigitalCredentialsUploadDTO structure:
     * <p>
     * {
     * "credential" : { %EuropeanDigitalCredential }
     * "deliveryDetails" : {
     * "walletAddress" : "123456abc"
     * }
     * }
     *
     * @param files the credential files
     * @return the DTO information of the Credentials inside the file
     */
    public List<EuropeanDigitalCredentialUploadResultDTO> obtainCredentials(MultipartFile[] files) {
        return obtainCredentials(files, -1, -1);
    }

    /**
     * Used to Issue credentials uploaded in JSON format inside of a file (credentials must follow EuropeanDigitalCredentialsUploadDTO structure:
     * <p>
     * {
     * "credential" : { %EuropeanDigitalCredential }
     * "deliveryDetails" : {
     * "walletAddress" : "123456abc"
     * }
     * }
     *
     * @param files the credential files
     * @return the DTO information of the Credentials inside the file
     */
    public List<EuropeanDigitalCredentialUploadResultDTO> obtainCredentials(MultipartFile[] files, int maximumConsecutiveFailures, int maximumTotalFailures) {

        List<EuropeanDigitalCredentialUploadResultDTO> europeanDigitalCredentialUploadDTOS = new ArrayList<>();

        int consecutiveFailures = 0;
        int totalFailures = 0;

        for (MultipartFile file : files) {
            EuropeanDigitalCredentialUploadResultDTO edcur = null;
            try {
                //Do not frame uploaded "credentials", as these files are still plain JSON.
                edcur = this.getJsonLdUtil().unMarshall(file.getBytes(), EuropeanDigitalCredentialUploadResultDTO.class);
                if (this.getESealValidationService().isSigned(file.getBytes())) {
                    logger.error("Error, uploaded credential is sealed");
                    edcur.setSigned(true);
                }
            } catch (IOException e) {
                logger.error("Error parsing uploaded JSON credential", e);
                edcur.setBadFormatDesc(file.getOriginalFilename()
                        .concat(DataModelConstants.StringPool.STRING_HYPHEN)
                        .concat(e.getMessage()));
                edcur.setBadFormat(true);
            }

            edcur.setFileName(file.getOriginalFilename());

            europeanDigitalCredentialUploadDTOS.add(edcur);

            if (maximumConsecutiveFailures > 0 && maximumTotalFailures > 0) {

                if (consecutiveFailures >= maximumConsecutiveFailures || totalFailures >= maximumTotalFailures) {
                    throw new EDCIException().addDescription(
                            String.format(this.getEdciMessageService().getMessage("exception.max.failures.reached.api"), maximumConsecutiveFailures, maximumTotalFailures,
                                    europeanDigitalCredentialUploadDTOS.stream().filter(cred -> cred.isSigned() || cred.isBadFormat())
                                            .map(EuropeanDigitalCredentialUploadResultDTO::getFileName).collect(Collectors.joining(","))));
                }

                if (edcur.isSigned() || edcur.isBadFormat()) {
                    consecutiveFailures++;
                    totalFailures++;
                } else {
                    consecutiveFailures = 0;
                }

            }

        }

        return europeanDigitalCredentialUploadDTOS;
    }

    /**
     * Used to Issue marshaled credentials uploaded in JSON format
     *
     * @param europeanDigitalCredentialUploadDTOS the credentials
     * @param locale                              locale to be used when mapping response DTO
     * @return the DTO information of the Credentials inside the file
     */
    public CredentialFileDTO uploadParsedCredentials(List<EuropeanDigitalCredentialUploadDTO> europeanDigitalCredentialUploadDTOS, String folder, String locale) {
        CredentialFileDTO f = new CredentialFileDTO();
        f.setValid(false);
        if (europeanDigitalCredentialUploadDTOS.isEmpty()) {
            throw new EDCIBadRequestException(EDCIMessageKeys.Exception.BadRquest.UPLOAD_CREDENTIAL_BAD_FORMAT).addDescription("No valid credential found inside the upload XML");
        }

        this.doProcessCredentials(europeanDigitalCredentialUploadDTOS
                .stream()
                .map(EuropeanDigitalCredentialUploadDTO::getCredential)
                .collect(Collectors.toList()), folder);

        //Map and return DTOs
        List<CredentialDTO> credentialDTOS = this.getCredentialMapper().toDTOList(europeanDigitalCredentialUploadDTOS, locale);
        f.setCredentials(credentialDTOS);
        f.setValid(true);
        return f;
    }

    /**
     * Creates and issues credentials based on a credential spec, and the personal recipient data. Credential must exist and all gradeable items should be graded
     *
     * @param credentials The credentials DTO built with the recipients information and the DAOs
     * @param locale      the locale to use when mapping response
     * @return the DTO information of the resulting credentials
     */
    public CredentialFileDTO issueCredentials(List<EuropeanDigitalCredentialUploadDTO> credentials, String locale) {
        CredentialFileDTO fileDTO = new CredentialFileDTO();
        fileDTO.setValid(false);
        if (credentials.isEmpty()) {
            throw new EDCIException("issuer.issuer.credential.unable.error");
        }
        List<EuropeanDigitalCredentialDTO> credentialsGen = credentials.stream().map(upload -> upload.getCredential()).collect(Collectors.toList());
        //Process  and create the credentials
        String sessionId = RequestContextHolder.currentRequestAttributes().getSessionId();
        this.doProcessCredentials(credentialsGen, fileUtil.getCredentialPrivateFolderName(sessionId));
        //map the credential DTO information
        fileDTO.setCredentials(this.getCredentialMapper().toDTOList(credentials, locale));
        fileDTO.setValid(true);
        return fileDTO;
    }

    /**
     * CREDENTIAL MANAGEMENT AND PROCESSING METHODS
     **/

    /**
     * Process Credentials, executing the consumer, validating the credential and creating the files.
     * If not valid, the Credential objects will be marked as invalid, and errors added to them.
     * Only creates files for credentials with valid profiles and types
     *
     * @param europeanDigitalCredentialDTOS the credentials to be processed
     */
    private void doProcessCredentials(List<EuropeanDigitalCredentialDTO> europeanDigitalCredentialDTOS, String folder) {
        //process credentials with consumers
        List<ConsumerContext> credentialContexts = europeanDigitalCredentialDTOS.stream().map(ConsumerContext::new).collect(Collectors.toList());
        this.getAndExecuteCredentialConsumers(credentialContexts);
        for (EuropeanDigitalCredentialDTO credentialDTO : europeanDigitalCredentialDTOS) {
            //Check credential types
            this.doCheckCredentialTypes(credentialDTO);
            this.doCheckCredentialProfiles(credentialDTO);
            //create and validate with SHACL JSONLD Files for valid(at this point) creds
            if (credentialDTO.isValid()) {
                this.doCreateAndValidateCredentialFile(credentialDTO, folder);
            } else {
                credentialDTO.setValid(false);
            }
        }
    }

    /**
     * Checks credential profiles, if anyone is not valid, the credential will be marked as invalid and errors will be added.
     *
     * @param credentialDTO the credential to be checked
     */
    private void doCheckCredentialProfiles(EuropeanDigitalCredentialDTO credentialDTO) {
        List<ConceptDTO> credentialProfiles = credentialDTO.getCredentialProfiles();
        if (credentialProfiles.isEmpty()) {
            credentialProfiles.add(this.getControlledListCommonsService().getDefaultCredentialProfile());
        } else {
            //Check that Credential Profiles do exist in CL and have internal equivalent
            List<ConceptDTO> invalidProfiles = credentialProfiles.stream().filter(conceptDTO -> !this.getControlledListCommonsService().isValidCredentialProfile(conceptDTO.getId())).collect(Collectors.toList());
            invalidProfiles = invalidProfiles.stream().filter(conceptDTO -> this.getShaclInternal().toInternal(conceptDTO.getId().toString()) == null).collect(Collectors.toList());
            if (!invalidProfiles.isEmpty()) {
                credentialDTO.setValid(false);
                invalidProfiles.forEach(conceptDTO -> {
                    credentialDTO.getValidationErrors().add(
                            this.getEdciMessageService().getMessage(EDCIIssuerMessageKeys.JSONLD.INVALID_PROFILE, conceptDTO.getId().toString())
                    );
                });
            }
        }
    }

    /**
     * Checks  credential types, If there are any invalid types, credential is marked as invalid and errors added.
     * Adds missing mandatory types
     *
     * @param credentialDTO the credential to be checked
     */
    private void doCheckCredentialTypes(EuropeanDigitalCredentialDTO credentialDTO) {
        List<String> types = credentialDTO.getType();
        boolean typeCheck = true;

        //Check that all types are allowed
        for (String type : types) {
            if (!this.getCredentialUtil().isAllowedType(type)) {
                typeCheck = false;
                credentialDTO.setValid(false);
                credentialDTO.getValidationErrors().add(
                        this.getEdciMessageService().getMessage(EDCIIssuerMessageKeys.JSONLD.INVALID_TYPE, type)
                );
            }
        }
        //Set types from Mandatory types
        credentialDTO.setType(CredentialUtil.MandatoryType.getTypes());
    }


    /**
     * Creates and Validates Credentials using internal SHACL files based on profiles. If any validation error is present in the report,
     * the credential is marked as invalid and errors are added.
     *
     * @param credentialDTO the credential to be created and validated
     */
    private void doCreateAndValidateCredentialFile(EuropeanDigitalCredentialDTO credentialDTO, String folder) {
        //Get Internal Shacl files from external URIs
        List<URI> credentialSchemaUris = this.getControlledListCommonsService()
                .getShaclURIsFromProfiles(credentialDTO.getCredentialProfiles());
        //Add Credential Schemas
        credentialDTO.setCredentialSchema(credentialSchemaUris.stream().map(uri -> {
            return new ShaclValidator2017(uri);
        }).collect(Collectors.toList()));

        //Create files
        File credentialFile = null;
        try {
            credentialFile = this.getFileService().createJSONLDFile(credentialDTO, folder);
        } catch (IOException | JsonLdError e) {
            throw new EDCIBadRequestException(EDCIMessageKeys.Exception.Global.GLOBAL_ERROR_CREATING_FILE).setCause(e).addDescription(e.getMessage());
        }
        //Get Internal Schema for validation
        Set<String> externalSchemas = credentialSchemaUris
                .stream().map(URI::toString)
                .collect(Collectors.toSet());
        Set<String> internalSchemas = new HashSet<>();

        for (String schema : externalSchemas) {
            List<String> internalList = this.getShaclInternal().toInternal(schema);
            if (internalList != null) {
                internalSchemas.addAll(internalList);
            }
        }

        try {
            //Read created file
            String credential = Files.readString(credentialFile.toPath());
            //Shacl validation using internal Schemas
            ValidationResult validationResult = this.getCredentialUtil().validateCredential(credential, internalSchemas);
            this.getEdciValidationUtil().loadLocalizedMessages(validationResult);
            if (!validationResult.isValid()) {
                credentialDTO.setValid(false);
                credentialDTO.getValidationErrors()
                        .addAll(validationResult.getValidationErrors().stream().
                                map(ValidationError::getErrorMessage).collect(Collectors.toList()));
            }
        } catch (IOException e) {
            throw new EDCIBadRequestException(EDCIMessageKeys.Exception.Global.GLOBAL_ERROR_CREATING_FILE).setCause(e).addDescription(e.getMessage());
        }
    }


    /**
     * Searches in the consumer Factory for the Europass Credential root class and executes them, it is used separetely for pre or post processing consumers.
     *
     * @param europassCredentialContext
     */
    private void getAndExecuteCredentialConsumers(List<ConsumerContext> europassCredentialContext) {
        Set<Consumer> consumers = this.getConsumerFactory().getEDCIConsumers(EuropeanDigitalCredentialDTO.class);
        for (ConsumerContext credentialContext : europassCredentialContext) {
            for (Consumer consumer : consumers) {
                try {
                    consumer.accept(credentialContext);
                } catch (EDCIException e) {
                    credentialContext.getCredential().setValid(false);
                    credentialContext.getCredential().getValidationErrors().add(this.getEdciMessageService().getMessage(e.getMessageKey(), e.getMessageArgs()));
                    logger.debug(
                            String.format("Error executing consumer %s for credential %s",
                                    consumer.getClass().getName(), credentialContext.getCredential().getId().toString()),
                            e);

                } catch (Exception e) {
                    credentialContext.getCredential().setValid(false);
                    credentialContext.getCredential().getValidationErrors().add(this.getEdciMessageService().getMessage(EDCIMessageKeys.Exception.Global.GLOBAL_INTERNAL_ERROR));
                    logger.error(
                            String.format("Error executing consumer %s for credential %s",
                                    consumer.getClass().getName(), credentialContext.getCredential().getId().toString()),
                            e);

                }
                if (!credentialContext.getCredential().isValid()) {
                    logger.debug(String.format("Credential %s is invalid at consumer %s",
                            credentialContext.getCredential().getId().toString(),
                            consumer.getClass().getName()));
                    break;
                }
            }
        }
    }

    /**
     * Delete a temporal credential
     *
     * @param uuid the uuid of the credential
     * @return the resulting status
     */
    public StatusDTO deleteCredentials(String uuid) {
        StatusDTO statusDTO = new StatusDTO();
        try {
            Files.delete(Paths.get(this.getFileUtil().getCredentialFileAbsolutePath(uuid)));
            statusDTO.setStatus(true);
        } catch (IOException e) {
            statusDTO.setStatus(false);
            throw new EDCIException().addDescription("Error deleting xml file").setCause(e);
        }
        return statusDTO;
    }

    /**
     * Downloads a temporal credential
     *
     * @param uuid the credential uuid
     * @return the credential file
     */
    public ResponseEntity<byte[]> downloadFile(String uuid) {
        File file = this.getEdciFileService().getOrCreateFile(this.getFileUtil().getCredentialFileAbsolutePath(uuid));
        byte[] bytes = null;
        if (file.exists()) {
            try {
                bytes = Files.readAllBytes(Paths.get(this.getFileUtil().getCredentialFileAbsolutePath(uuid)));
            } catch (IOException e) {
                throw new EDCIException().addDescription(String.format("Credential with uuid %s cannot be readed", uuid));
            }
        } else {
            throw new EDCINotFoundException().addDescription(String.format("Credential with uuid %s was not found", uuid));
        }

        return new ResponseEntity<byte[]>(bytes, this.getCredentialServiceUtil().prepareHttpHeadersForCredentialDownload(this.getFileUtil().getFileName(URI.create(uuid))), HttpStatus.OK);
    }

    public String getSessionId() {
        return RequestContextHolder.currentRequestAttributes().getSessionId();
    }

    /**
     * Downloads a temporal credentials zip
     *
     * @param uuids the selected credential uuids
     * @return the credential zip file
     */
    public ResponseEntity<byte[]> downloadZipFile(List<String> uuids) {
        String sessionId = getSessionId();
        String zipFile = "credentials_" + new SimpleDateFormat("yyyyMMdd_hhmm").format(new Date()) + ".zip";
        List<String> credentialList = uuids.stream().map(uuid -> this.getFileUtil().getFileName(URI.create(uuid))).collect(Collectors.toList());
        String sessionFolder = this.getFileUtil().getCredentialPrivateFolderName(sessionId);
        byte[] zipBytes = null;
        try {
            zipUtil.addfilesToZIP(sessionFolder, zipFile, credentialList);
            zipBytes = Files.readAllBytes(this.edciFileService.getOrCreateFile(getFileUtil().getCredentialPrivateFolderName(sessionId).concat(zipFile)).toPath());
        } catch (EDCIException e) {
            throw e;
        } catch (Exception e) {
            throw new EDCIException(e).addDescription("Error creating zip file of the issued credentials");
        }
        return new ResponseEntity<byte[]>(zipBytes, this.getCredentialServiceUtil().prepareHttpHeadersForDownload(zipFile, MediaType.APPLICATION_OCTET_STREAM_VALUE), HttpStatus.OK);
    }

    // #################################### SIGNING METHODS ####################################

    /**
     * Seal the Credentials using a locally stored cert, requires a password for it.
     *
     * @param credentialDTOList
     * @param certPassword
     * @param signOnBehalf
     * @param mandatedIssue
     * @return List of CredentialDTOS with sealing status/errors
     */
    public List<CredentialDTO> signFromLocalCert(List<CredentialDTO> credentialDTOList, String certPassword, String signOnBehalf, String fileFolder, String batchId, AttachmentView mandatedIssue) {

        List<CredentialDTO> credentialDTOS = Collections.synchronizedList(new ArrayList<CredentialDTO>());
        //Crate executor
        String executorName = "signFromLocalCertExecutor";
        int numThreads = this.getIssuerConfigService().getInteger(IssuerConfig.Threads.SIGNATURE_BYTES_NUM_THREADS, Runtime.getRuntime().availableProcessors());
        Long timeOutInMinutes = this.getIssuerConfigService().get(IssuerConfig.Threads.SIGNATURE_BYTES_TIMEOUT_MINUTES_THREADS, Long.class, 120L);
        this.getEdciExecutorService().createExecutor(executorName, numThreads);
        String certPath = this.getIssuerConfigService().getString(EDCIConfig.DSS.CERT_PATH);
        Map<String, String> decodedCertificate = this.getDssedciCertificateService().getCertificateInfo(certPath, certPassword);
        for (CredentialDTO credentialDTO : credentialDTOList) {
            //get Cert/cred paths
            this.getEdciExecutorService().submitTask(executorName, () -> {
                credentialDTO.setSealed(false);
                credentialDTO.setSealingErrors(new ArrayList<String>());
                String filePath = fileFolder.concat(getFileUtil().getFileName(URI.create(credentialDTO.getUuid())));
                //Sign and save cred file
                try {
                    this.getCertificateUtils().overwriteCertificateFields(mandatedIssue, filePath, decodedCertificate);
//                    String xPathLocation = EDCIConstants.Certificate.CREDENTIAL_TYPE_EUROPASS_PRESENTATION.equals(localSignatureRequestDTOS.getSignOnBehalf()) ? DSSConstants.VERIFIABLE_PRESENTATION_XPATH_LOCATION : DSSConstants.EUROPASS_CREDENTIAL_XPATH_LOCATION;
                    DSSDocument signedDocument = this.getDssedciSignService().signDocument(filePath, certPath, certPassword);
                    if (this.getValidator().notEmpty(signedDocument)) {
                        Files.deleteIfExists(Paths.get(filePath));
                        signedDocument.save(filePath);
                    }
                    credentialDTO.setSealed(true);
                    ValidationResult shaclValidation = this.getCredentialServiceUtil().validateCredentialtoSHACL(credentialDTO, batchId);
                    if (!shaclValidation.isValid()) {
                        credentialDTO.setSealed(false);
                        credentialDTO.setSealingErrors(shaclValidation.getDistinctErrorMessages());
                    }
                } catch (EDCIException e) {
                    String cause = e.getCause() != null ? e.getCause().getMessage() : "";
                    logger.error("[E] - Error signing credential {} / {} : {}", () -> filePath, () -> e.getMessage(), () -> cause, () -> e);
                    credentialDTO.setSealed(false);
                    credentialDTO.getSealingErrors().add(this.getEdciMessageService().getMessage(e.getMessageKey(), e.getMessageArgs()));
                } catch (Exception e) {
                    //Error during sealing process, set error for frontend
                    String cause = e.getCause() != null ? e.getCause().getMessage() : "";
                    logger.error("[E] - Error signing credential {} / {} : {}", () -> filePath, () -> e.getMessage(), () -> cause, () -> e);
                    credentialDTO.setSealed(false);
                    credentialDTO.getSealingErrors().add(this.getEdciMessageService().getMessage(EDCIIssuerMessageKeys.Sealing.SEAL_CREDENTIAL_KO));
                } finally {
                    credentialDTOS.add(credentialDTO);
                }
            });
        }

        this.getEdciExecutorService().shutdownAndAwaitTermination(executorName, timeOutInMinutes);
        return credentialDTOS;
    }

    /**
     * Generate the Signature Bytes for all the credentials inside signatureParametersDTO, this is used in combination of a Nexu frontend as the first step of signing.
     *
     * @return the generated bytes and timestamps
     */
    public List<SignatureBytesDTO> getSignatureBytes(List<String> uuidList, String certificate, List<String> certificateChain, AttachmentView mandatedFile) {

        long start = 0;
        if (logger.isDebugEnabled()) {
            start = System.currentTimeMillis();
        }

        logger.debug("Getting Signature Bytes of {} credentials", () -> uuidList.size());
        String sessionId = RequestContextHolder.currentRequestAttributes().getSessionId();
        List<SignatureBytesDTO> signatureBytes = new ArrayList<>();

        String executorName = "signatureBytesExecutor";
        int numThreads = this.getIssuerConfigService().getInteger(IssuerConfig.Threads.SIGNATURE_BYTES_NUM_THREADS, Runtime.getRuntime().availableProcessors());
        Long timeOutMinutes = this.getIssuerConfigService().get(IssuerConfig.Threads.SIGNATURE_BYTES_TIMEOUT_MINUTES_THREADS, Long.class, 120L);
        this.getEdciExecutorService().createExecutor(executorName, numThreads);
        EDCIException lastException = new EDCIException();
        Map<String, String> decodedCertificate = dssedciCertificateService.getCertificateInfo(
                EDCIConstants.Certificate.CERTIFICATE_BEGIN_MARKER + certificate + EDCIConstants.Certificate.CERTIFICATE_END_MARKER);

        for (String uuid : uuidList) {
            this.getEdciExecutorService().submitTask(executorName, () -> {
                String filePath = fileUtil.getCredentialFileAbsolutePath(uuid);
                SignatureBytesDTO credSignatureBytes = new SignatureBytesDTO();
                credSignatureBytes.setValid(true);
                try {
                    //We converted a credential to VP inside getSignatureBytes. In json-ld we are not doing that.
                    //We'll modifiy the credential's type, but we'll do thi outside the getSignatureBytes method. Prior to the call
                    //TODO: modify type of credential if needed HERE
                    certificateUtils.overwriteCertificateFields(mandatedFile, filePath, decodedCertificate);
                    String modalMsg = certificateUtils.getCertificateReplaceMsg(decodedCertificate);
                    credSignatureBytes = getDssedciSignService().getSignatureBytes(filePath, certificate, certificateChain);
                    credSignatureBytes.setWarningMsg(modalMsg);
                    credSignatureBytes.setValid(true);
                    //TODO: catch the error throw if only QSeals are allowed and abort all the process with the first encounter of the error
                } catch (EDCIException e) {
                    logger.error("Error generating signature bytes for credential " + uuid, e);
                    credSignatureBytes.setValid(false);
                    credSignatureBytes.setErrorMessage(this.getEdciMessageService().getMessage(e.getMessageKey(), e.getMessageArgs()));
                } catch (ESealException e) {
                    //Throws Exception
                    lastException.setCause(e);
                    throw e;
                } catch (IOException | ParseException | JsonLdError e) {
                    //Catch any unexpected exceptiond 
                    logger.error("Error generating signature bytes for credential " + uuid, e);
                    credSignatureBytes.setValid(false);
                    credSignatureBytes.setErrorMessage(this.getEdciMessageService().getMessage(ErrorCode.CREDENTIAL_NOT_READABLE.getLabelKey()));
                } finally {
                    signatureBytes.add(credSignatureBytes);
                }

            });
        }
        this.getEdciExecutorService().shutdownAndAwaitTermination(executorName, timeOutMinutes);
        if (logger.isDebugEnabled()) {
            long end = System.currentTimeMillis();
            logger.debug(String.format("Getting signature bytes for %d credentials took %d seconds", uuidList.size(), (end - start) / 1000));
        }
        //Check for the ONLY_QSEAL exception in any thread and throw it for ExceptionControllerAdvice handler
        if (lastException.getCause() != null && lastException.getCause().getMessage().equals(EDCIMessageKeys.Exception.DSS.CERTIFICATE_NOT_QSEAL_ERROR)) {
            ESealException eSealException = (ESealException) lastException.getCause();
            throw new EDCIException(eSealException.getMessageKey(), eSealException.getMessageArgs()).addDescription(eSealException.getDescription());
        }
        return signatureBytes;
    }


    /**
     * Signs a Credential XML using the information from the nexu response, this is used in conjunction with the nexu frontend as the second step of signing
     *
     * @param signatureNexuDTOS Prameters with the nexu signature bytes
     * @return the list of credentials and sealing errors
     */
    public List<CredentialDTO> signCredential(List<EDCIIssuerSignatureNexuDTO> signatureNexuDTOS) {
        long start = 0;
        if (logger.isDebugEnabled()) {
            start = System.currentTimeMillis();
        }
        logger.debug("Signing {} credentials", () -> signatureNexuDTOS.size());
        //Get Signature Values from F/E
        String sessionId = RequestContextHolder.currentRequestAttributes().getSessionId();
        List<CredentialDTO> credentialDTOS = Collections.synchronizedList(new ArrayList<CredentialDTO>());
        String executor = "signCredentialExecutor";
        int numThreads = this.getIssuerConfigService().getInteger(IssuerConfig.Threads.SIGN_CREDENTIAL_NUM_THREADS, Runtime.getRuntime().availableProcessors());
        Long timeOutInMinutes = this.getIssuerConfigService().get(IssuerConfig.Threads.SIGN_CREDENTIALS_TIMEOUT_MINUTES_THREADS, Long.class, 120L);
        this.getEdciExecutorService().createExecutor(executor, numThreads);
        for (EDCIIssuerSignatureNexuDTO signatureNexuDTO : signatureNexuDTOS) {
            this.getEdciExecutorService().submitTask(executor, () -> {

                try {
                    CredentialDTO credentialDTO = signatureNexuDTO.getCredential();
                    String filePath = this.getFileUtil().getCredentialFileAbsolutePath(signatureNexuDTO.getUuid());
                    DSSDocument signedDocument = getDssedciSignService().signDocument(filePath, signatureNexuDTO);
                    if (signedDocument != null) {
                        Files.deleteIfExists(Paths.get(filePath));
                        signedDocument.save(filePath);
                        credentialDTO.setSealed(true);
                    } else {
                        credentialDTO.setSealed(false);
                    }

                    //Class credentialClass = EDCIConstants.Certificate.CREDENTIAL_TYPE_EUROPASS_PRESENTATION.equals(signatureNexuDTO.getPresentation()) ? EuropassPresentationDTO.class : EuropassCredentialDTO.class;
                    ValidationResult shaclValidation = this.getCredentialServiceUtil().validateCredentialtoSHACL(credentialDTO, null);
                    this.getEdciValidationUtil().loadLocalizedMessages(shaclValidation);
                    if (!shaclValidation.isValid()) {
                        credentialDTO.setSealed(false);
                        credentialDTO.setSealingErrors(shaclValidation.getDistinctErrorMessages());
                    }
                    credentialDTOS.add(credentialDTO);
                } catch (Exception e) {
                    if (this.getIssuerConfigService().getBoolean(ESealConfig.Properties.ADV_QSEAL_ONLY, ESealConfig.Defaults.ADV_QSEAL_ONLY)) {
                        // Just throw exception when using QSeals
                        throw new EDCIException().setCause(e);
                    }
                    logger.error("error signing credential", e);
                }
            });
        }
        this.getEdciExecutorService().shutdownAndAwaitTermination(executor, timeOutInMinutes);
        if (logger.isDebugEnabled()) {
            long end = System.currentTimeMillis();
            logger.debug(String.format("Signing %d credentials took %d seconds", signatureNexuDTOS.size(), (end - start) / 1000));
        }
        return credentialDTOS;
    }

    // #################################### SENDING METHODS ####################################

    /**
     * Sends credentials by both available methods
     *
     * @param credentialDTOS
     * @return the credential dtos with the sending results
     */

    public List<CredentialDTO> sendCredentials(List<CredentialDTO> credentialDTOS) {

        String mailExecutorName = "sendMailExecutor";
        int mailNumThreads = this.getIssuerConfigService().getInteger(IssuerConfig.Threads.SEND_CREDENTIALS_NUM_THREADS, Runtime.getRuntime().availableProcessors());
        Long mailTimeOutInMinutes = this.getIssuerConfigService().get(IssuerConfig.Threads.SEND_CREDENTIAL_TIMEOUT_MINUTES_THREADS, Long.class, 120L);
        this.getEdciExecutorService().createExecutor(mailExecutorName, mailNumThreads);

        String walletExecutorName = "sendWalletExecutor";
        int walletNumThreads = this.getIssuerConfigService().getInteger(IssuerConfig.Threads.SEND_CREDENTIALS_NUM_THREADS, Runtime.getRuntime().availableProcessors());
        Long walletTimeOutInMinutes = this.getIssuerConfigService().get(IssuerConfig.Threads.SEND_CREDENTIAL_TIMEOUT_MINUTES_THREADS, Long.class, 120L);
        this.getEdciExecutorService().createExecutor(walletExecutorName, walletNumThreads);

        this.getEdciExecutorService().submitTask(mailExecutorName, () -> {
            sendEmailCredentials(credentialDTOS);
        });

        this.getEdciExecutorService().submitTask(walletExecutorName, () -> {
            sendWalletCredentials(credentialDTOS);
        });

        this.getEdciExecutorService().shutdownAndAwaitTermination(mailExecutorName, mailTimeOutInMinutes);
        this.getEdciExecutorService().shutdownAndAwaitTermination(walletExecutorName, walletTimeOutInMinutes);

        return credentialDTOS;
    }

    private void sendWalletCredentials(List<CredentialDTO> credentialDTOs) {
        for (CredentialDTO credentialDTO : credentialDTOs) {
            credentialDTO.setReceived(true);
            List<String> receivedErrors = credentialDTO.getReceivedErrors();

            boolean sendByEmail = this.getIssuerConfigService().getBoolean(IssuerConfig.Issuer.WALLET_SEND_EMAIL, true);

            if (!this.getValidator().isEmpty(credentialDTO.getWalletAddress()) || !this.getValidator().isEmpty(credentialDTO.getEmail())) {
                String sessionId = RequestContextHolder.currentRequestAttributes().getSessionId();
                File file = this.getEdciFileService().getOrCreateFile(this.getFileUtil().getCredentialFileAbsolutePath(credentialDTO.getUuid()));
                FileItem fileItem = null;
                try {
                    fileItem = new DiskFileItem("_credential", Files.probeContentType(file.toPath()), false, file.getName(), (int) file.length(), file.getParentFile());
                    InputStream input = new FileInputStream(file);
                    OutputStream os = fileItem.getOutputStream();
                    IOUtils.copy(input, os);
                } catch (IOException ex) {
                }

                MultipartFile multipartFile = new CommonsMultipartFile(fileItem);

                try {

                    String walletApiUrl = null;

                    if (!this.getValidator().isEmpty(credentialDTO.getWalletAddress())) {
                        walletApiUrl = this.getIssuerConfigService().getString(IssuerConfig.Issuer.WALLET_API_URL)
                                .concat(this.getIssuerConfigService().getString(IssuerConfig.Issuer.WALLET_ADD_PATH)).replace(Parameter.WALLET_USER_ID,
                                        credentialDTO.getWalletAddress().substring(credentialDTO.getWalletAddress().lastIndexOf('/') + 1));
                        walletApiUrl = walletApiUrl.concat("?sendEmail=" + sendByEmail);
                    } else {
                        if (!this.getValidator().isEmpty(credentialDTO.getEmail()) && !EmailValidator.getInstance().isValid(credentialDTO.getEmail().replaceFirst("mailto:", ""))) {
                            throw new EDCIException(this.getEdciMessageService().getMessage(EDCIIssuerMessageKeys.ERROR_INVALID_EMAIL_SEND_WALLET, credentialDTO.getStudentName()));
                        }
                        walletApiUrl = this.getIssuerConfigService().getString(IssuerConfig.Issuer.WALLET_API_URL)
                                .concat(this.getIssuerConfigService().getString(IssuerConfig.Issuer.WALLET_ADD_EMAIL_PATH)).replace(Parameter.WALLET_USER_EMAIL,
                                        credentialDTO.getEmail());
                        walletApiUrl = walletApiUrl.concat("?sendEmail=" + sendByEmail);
                    }

                    this.getWalletResourceUtil().doWalletPostRequest(walletApiUrl, multipartFile, EDCIParameter.WALLET_ADD_CREDENTIAL_XML, CredentialUploadResponseDTO.class, MediaType.APPLICATION_JSON, false);

                } catch (NullPointerException e) {
                    credentialDTO.setReceived(false);
                    receivedErrors.add(this.getEdciMessageService().getMessage(new EDCIException().getMessageKey()));
                } catch (EDCIException e) {
                    //Error Recieved from wallet, (already translated) must be passed to VIEW.
                    credentialDTO.setReceived(false);
                    receivedErrors.add(e.getMessage());
                } catch (EDCIRestException e) {
                    //Error Recieved from wallet, (already translated) must be passed to VIEW.
                    credentialDTO.setReceived(false);
                    receivedErrors.add(e.getMessage());
                } catch (Exception e) {
                    logger.error(e);
                    receivedErrors.add(this.getEdciMessageService().getMessage(EDCIIssuerMessageKeys.GLOBAL_INTERNAL_ERROR));
                    credentialDTO.setReceived(false);
                }

            } else {
                receivedErrors.add(this.getEdciMessageService().getMessage(EDCIIssuerMessageKeys.ERROR_NO_WALLET_FOUND_XLS));
                credentialDTO.setReceived(false);
            }
            credentialDTO.setReceivedErrors(receivedErrors);
        }

    }


    private void sendEmailCredentials(List<CredentialDTO> credentialDTOS) {
        for (CredentialDTO credentialDTO : credentialDTOS) {
            List<String> sendErrors = credentialDTO.getSendErrors();
            String toEmail = credentialDTO.getEmail();
            credentialDTO.setSent(false);
            if (!this.getValidator().isEmpty(toEmail) && !EmailValidator.getInstance().isValid(toEmail.replaceFirst("mailto:", ""))) {
                sendErrors.add(this.getEdciMessageService().getMessage(EDCIIssuerMessageKeys.ERROR_INVALID_EMAIL, toEmail));
            } else if (!this.getValidator().isEmpty(toEmail)) {

                String credentialFilePath = this.getFileUtil().getCredentialFileAbsolutePath(credentialDTO, null);
                byte[] fileBytes = null;
                try {
                    fileBytes = Files.readAllBytes(Paths.get(credentialFilePath));
                } catch (IOException e) {
                    logger.error("Could not read attachment " + credentialFilePath);
                }
                Map<String, String> wildCards = new HashMap<String, String>();

                EuropeanDigitalCredentialDTO europassCredentialDTO = null;
                //Use current locale for default1
                Locale locale = LocaleContextHolder.getLocale();
                String studentName = credentialDTO.getStudentName();
                String issuerName = credentialDTO.getIssuerName();
                String course = credentialDTO.getCourse();
                String fileName = "";
                String subject = this.getEdciMessageService().getMessage(locale, EDCIIssuerMessageKeys.MAIL_SUBJECT_YOUR, course);
                byte[] diplomaThumbnail = null;

                //Try to extract from original XML, the primary language labels for email.
                try {
                    europassCredentialDTO = this.getFileUtil().getCredentialFromFile(credentialDTO);
                    try {
                        diplomaThumbnail = diplomaUtils.getThumbnailImage(europassCredentialDTO);
                    } catch (Exception e) {
                        logger.error(String.format("Could not generate diploma thumbnail for credential %s", credentialDTO.getUuid()));
                    }
                    //if credential can be read, guess credential locale and replace messages
                    locale = this.getCredentialUtil().guessPrimaryLanguage(europassCredentialDTO);
                    studentName = this.getCredentialUtil().getAvailableName(europassCredentialDTO.getCredentialSubject(), locale.toString());
                    issuerName = MultilangFieldUtil.getLiteralStringOrAny(europassCredentialDTO.getIssuer().getLegalName(), locale.toString());
                    course = MultilangFieldUtil.getLiteralStringOrAny(europassCredentialDTO.getDisplayParameter().getTitle(), locale.toString());
                    subject = this.getEdciMessageService().getMessage(locale, EDCIIssuerMessageKeys.MAIL_SUBJECT_YOUR, course);
                    fileName = this.getCredentialUtil().getHumanReadableEncodedFileName(europassCredentialDTO, locale.toString());
                } catch (JsonLdError | IOException | ParseException e) {
                    logger.error("could not recover full europass model from file" + e.getMessage(), e);
                }

                wildCards.put(IssuerConstants.MAIL_WILDCARD_SUBJECT, studentName);
                wildCards.put(IssuerConstants.MAIL_WILDCARD_ISSUER, issuerName);
                wildCards.put(IssuerConstants.MAIL_WILDCARD_TITLE, course);
                wildCards.put(IssuerConstants.MAIL_WILDCARD_VIEWERURL, issuerConfigService.getString("viewer.url"));

                try {
                    this.getEdciMailService().sendTemplatedEmail(IssuerConstants.MAIL_TEMPLATES_DIRECTORY, IssuerConstants.MAIL_ISSUED_TEMPLATE, subject, wildCards, Arrays.asList(toEmail), locale.toString(), fileBytes, fileName, diplomaThumbnail);
                    credentialDTO.setSent(true);
                } catch (Exception e) {
                    logger.error(String.format("Error sending email, %s", e.getMessage()), e);
                    credentialDTO.getSendErrors().add(edciMessageService.getMessage(EDCIIssuerMessageKeys.ERROR_SEND_EMAIL, toEmail));
                }

            }

            credentialDTO.setSendErrors(sendErrors);
        }
    }

    public byte[] getTestCredential() {
        byte[] bytes = null;

        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream("seal_test_credential.json")) {
            bytes = inputStream.readAllBytes();
        } catch (IOException e) {
            throw new EDCIException(e).addDescription("Error obtaining bytes from Test Credential");
        }

        return bytes;
    }


    public ESealSignService getDssedciSignService() {
        return dssedciSignService;
    }

    public void setDssedciSignService(ESealSignService dssedciSignService) {
        this.dssedciSignService = dssedciSignService;
    }


    public EDCIExecutorService getEdciExecutorService() {
        return edciExecutorService;
    }

    public void setEdciExecutorService(EDCIExecutorService edciExecutorService) {
        this.edciExecutorService = edciExecutorService;
    }

    public IssuerConfigService getIssuerConfigService() {
        return issuerConfigService;
    }

    public void setIssuerConfigService(IssuerConfigService issuerConfigService) {
        this.issuerConfigService = issuerConfigService;
    }

    public FileUtil getFileUtil() {
        return fileUtil;
    }

    public void setFileUtil(FileUtil fileUtil) {
        this.fileUtil = fileUtil;
    }

    public EDCIMessageService getEdciMessageService() {
        return edciMessageService;
    }

    public void setEdciMessageService(EDCIMessageService edciMessageService) {
        this.edciMessageService = edciMessageService;
    }

    public ESealCertificateService getDssedciCertificateService() {
        return dssedciCertificateService;
    }

    public void setDssedciCertificateService(ESealCertificateService dssedciCertificateService) {
        this.dssedciCertificateService = dssedciCertificateService;
    }

    public IssuerFileService getFileService() {
        return fileService;
    }

    public void setFileService(IssuerFileService fileService) {
        this.fileService = fileService;
    }

    public Validator getValidator() {
        return validator;
    }

    public void setValidator(Validator validator) {
        this.validator = validator;
    }

    public CredentialMapper getCredentialMapper() {
        return credentialMapper;
    }

    public void setCredentialMapper(CredentialMapper credentialMapper) {
        this.credentialMapper = credentialMapper;
    }

    public CertificateUtils getCertificateUtils() {
        return certificateUtils;
    }

    public void setCertificateUtils(CertificateUtils certificateUtils) {
        this.certificateUtils = certificateUtils;
    }

    public ConsumerFactory getConsumerFactory() {
        return consumerFactory;
    }

    public void setConsumerFactory(ConsumerFactory consumerFactory) {
        this.consumerFactory = consumerFactory;
    }


    public WalletResourceUtil getWalletResourceUtil() {
        return walletResourceUtil;
    }

    public void setWalletResourceUtil(WalletResourceUtil walletResourceUtil) {
        this.walletResourceUtil = walletResourceUtil;
    }


    public EDCIMailService getEdciMailService() {
        return edciMailService;
    }

    public void setEdciMailService(EDCIMailService edciMailService) {
        this.edciMailService = edciMailService;
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

    public JsonLdUtil getJsonLdUtil() {
        return jsonLdUtil;
    }

    public void setJsonLdUtil(JsonLdUtil jsonLdUtil) {
        this.jsonLdUtil = jsonLdUtil;
    }

    public JadesValidationService getJadesValidationService() {
        return jadesValidationService;
    }

    public void setJadesValidationService(JadesValidationService jadesValidationService) {
        this.jadesValidationService = jadesValidationService;
    }

    public CredentialUtil getCredentialUtil() {
        return credentialUtil;
    }

    public void setCredentialUtil(CredentialUtil credentialUtil) {
        this.credentialUtil = credentialUtil;
    }

    public ControlledListCommonsService getControlledListCommonsService() {
        return controlledListCommonsService;
    }

    public void setControlledListCommonsService(ControlledListCommonsService controlledListCommonsService) {
        this.controlledListCommonsService = controlledListCommonsService;
    }

    public EDCIValidationUtil getEdciValidationUtil() {
        return edciValidationUtil;
    }

    public void setEdciValidationUtil(EDCIValidationUtil edciValidationUtil) {
        this.edciValidationUtil = edciValidationUtil;
    }

    public CredentialServiceUtil getCredentialServiceUtil() {
        return credentialServiceUtil;
    }

    public void setCredentialServiceUtil(CredentialServiceUtil credentialServiceUtil) {
        this.credentialServiceUtil = credentialServiceUtil;
    }

    public ShaclInternal getShaclInternal() {
        return shaclInternal;
    }

    public void setShaclInternal(ShaclInternal shaclInternal) {
        this.shaclInternal = shaclInternal;
    }
}
