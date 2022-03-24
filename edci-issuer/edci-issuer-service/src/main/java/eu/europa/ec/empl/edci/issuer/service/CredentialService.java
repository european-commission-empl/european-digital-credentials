package eu.europa.ec.empl.edci.issuer.service;

import eu.europa.ec.empl.edci.constants.EDCIConfig;
import eu.europa.ec.empl.edci.constants.EDCIConstants;
import eu.europa.ec.empl.edci.constants.EDCIMessageKeys;
import eu.europa.ec.empl.edci.constants.EDCIParameter;
import eu.europa.ec.empl.edci.datamodel.model.EuropassCredentialDTO;
import eu.europa.ec.empl.edci.datamodel.model.base.CredentialHolderDTO;
import eu.europa.ec.empl.edci.datamodel.model.dataTypes.SchemaLocation;
import eu.europa.ec.empl.edci.datamodel.model.verifiable.presentation.EuropassPresentationDTO;
import eu.europa.ec.empl.edci.datamodel.validation.ValidationError;
import eu.europa.ec.empl.edci.datamodel.validation.ValidationResult;
import eu.europa.ec.empl.edci.dss.constants.DSSConstants;
import eu.europa.ec.empl.edci.dss.model.signature.*;
import eu.europa.ec.empl.edci.dss.service.DSSEDCICertificateService;
import eu.europa.ec.empl.edci.dss.service.DSSEDCISignService;
import eu.europa.ec.empl.edci.exception.EDCIException;
import eu.europa.ec.empl.edci.exception.EDCIRestException;
import eu.europa.ec.empl.edci.exception.clientErrors.EDCIBadRequestException;
import eu.europa.ec.empl.edci.exception.clientErrors.EDCINotFoundException;
import eu.europa.ec.empl.edci.factory.ConsumerFactory;
import eu.europa.ec.empl.edci.issuer.common.constants.*;
import eu.europa.ec.empl.edci.issuer.common.model.*;
import eu.europa.ec.empl.edci.issuer.entity.specs.AssessmentSpecDAO;
import eu.europa.ec.empl.edci.issuer.entity.specs.EuropassCredentialSpecDAO;
import eu.europa.ec.empl.edci.issuer.mapper.CredentialMapper;
import eu.europa.ec.empl.edci.issuer.service.spec.AssessmentSpecService;
import eu.europa.ec.empl.edci.issuer.service.spec.EuropassCredentialSpecService;
import eu.europa.ec.empl.edci.issuer.util.CertificateUtils;
import eu.europa.ec.empl.edci.issuer.util.FileUtil;
import eu.europa.ec.empl.edci.issuer.utils.EuropassCredentialDAOUtils;
import eu.europa.ec.empl.edci.service.EDCIFileService;
import eu.europa.ec.empl.edci.service.EDCIMailService;
import eu.europa.ec.empl.edci.service.EDCIMessageService;
import eu.europa.ec.empl.edci.service.EDCIValidationService;
import eu.europa.ec.empl.edci.util.*;
import eu.europa.esig.dss.enumerations.DigestAlgorithm;
import eu.europa.esig.dss.model.DSSDocument;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItem;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;
import org.xml.sax.SAXException;

import javax.inject.Inject;
import javax.xml.bind.DatatypeConverter;
import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Service
public class CredentialService {
    private static final Logger logger = Logger.getLogger(CredentialService.class);

    @Autowired
    private EDCIFileService edciFileService;

    @Autowired
    private EDCIMessageService edciMessageService;

    @Autowired
    private DSSEDCISignService dssedciSignService;

    @Autowired
    private DSSEDCICertificateService dssedciCertificateService;

    @Autowired
    private IssuerConfigService issuerConfigService;

    @Autowired
    private EDCICredentialModelUtil edciCredentialModelUtil;

    @Autowired
    private XmlUtil xmlUtil;

    @Autowired
    private Validator validator;

    @Inject
    private FileUtil fileUtil;

    @Autowired
    private CertificateUtils certificateUtils;

    @Autowired
    private EuropassCredentialSpecService credentialSpecService;

    @Autowired
    private AssessmentSpecService assessmentSpecService;

    @Autowired
    private IssuerFileService fileService;

    @Autowired
    private CredentialMapper credentialMapper;

    @Autowired
    private ConsumerFactory consumerFactory;

    @Autowired
    private EDCIValidationService edciValidationService;

    @Autowired
    private WalletResourceUtil walletResourceUtil;

    @Autowired
    private MockFactoryUtil mockFactoryUtil;

    @Autowired
    private EDCIMailService edciMailService;

    @Autowired
    private EuropassCredentialDAOUtils europassCredentialDAOUtils;

    @Autowired
    private EDCIExecutorService edciExecutorService;

    @Autowired
    private DiplomaUtils diplomaUtils;

    @Autowired
    private ImageUtil imageUtil;

    /**
     * Used to Issue credentials uploaded in "XML" format (credentials are in the file separated by a certain identifier, and are split before parsing).
     *
     * @param file   the credential file
     * @param locale locale to be used when mapping response DTO
     * @return the DTO information of the Credentials inside the file
     */
    public CredentialFileDTO uploadCredentials(MultipartFile file, String locale) {
        List<EuropassCredentialDTO> eups;
        try {
            //Do not allow already signed credentials
            if (this.dssedciSignService.isCredentialSigned(file.getBytes()))
                throw new EDCIBadRequestException(EDCIIssuerMessageKeys.Sealing.CREDENTIAL_ALREADY_SIGNED).addDescription("Already signed Credential");
            //split credentials
            eups = this.getFileService().getEuropassCredentialsFromUploadFile(file);
        } catch (IOException | ParserConfigurationException | SAXException e) {
            throw new EDCIBadRequestException(EDCIMessageKeys.Exception.BadRquest.UPLOAD_CREDENTIAL_BAD_FORMAT).setCause(e);
        }

        if (eups.isEmpty()) {
            throw new EDCIBadRequestException(EDCIMessageKeys.Exception.BadRquest.UPLOAD_CREDENTIAL_BAD_FORMAT).addDescription("No valid credential found inside the upload XML");
        }

        //Pre and post process the credentials
        this.preProcessCredentials(eups);
        this.postProcessCredentials(eups);

        try {
            //create XML Files
            this.getFileService().createXMLFiles(eups.toArray(new EuropassCredentialDTO[0]));
        } catch (Exception e) {
            throw new EDCIBadRequestException(EDCIMessageKeys.Exception.Global.GLOBAL_ERROR_CREATING_FILE);
        }
        //Map and return DTOs
        List<CredentialDTO> credentialDTOS = this.getCredentialMapper().europassToDTOList(eups, locale);
        CredentialFileDTO f = new CredentialFileDTO();
        f.setCredentials(credentialDTOS);
        f.setValid(true);
        return f;
    }

    /**
     * Creates and issues credentials based on a credential spec, and the personal recipient data inside IssueBuildCredentialDTO. Credential must exist and all gradeable items should be graded
     *
     * @param issueBuildCredentialDTO The Build issuing information with the credential spec id and the recipient information
     * @param locale                  the locale to use when mapping response
     * @return the DTO information of the resulting credentials
     */
    public CredentialFileDTO issueCredentials(IssueBuildCredentialDTO issueBuildCredentialDTO, String locale) {
        List<EuropassCredentialDTO> credentials = new ArrayList<>();
        EuropassCredentialSpecDAO credentialDAO = this.getCredentialSpecService().find(issueBuildCredentialDTO.getCredential());
        if (credentialDAO == null) {
            //The Credential Spec does not exist
            throw new EDCINotFoundException().addDescription("Credential with oid [" + issueBuildCredentialDTO.getCredential() + "] not found");
        }
        //Get credential-assigned assessments
        Set<Long> credentialAssessmentIds = this.getCredentialSpecService().getCredentialAssessments(issueBuildCredentialDTO.getCredential()).stream().map(AssessmentSpecDAO::getPk).collect(Collectors.toSet());
        //Check that assessments make sense, afterwards in validationconsumer, will validate that all necessary score fields are filled.
        issueBuildCredentialDTO.getRecipients().forEach(r -> {
            if (r.getAssessmentGrades() != null) {
                //checking that all assessments in issueBuildCredentialDTO exist, map those to Ids to check coherence afterwards, if any assessment is missing from BD an exception is thrown
                Set<Long> issueBuildAssessmentIds = this.getAssessmentSpecService().retrieveEntities(true, r.getAssessmentGrades().keySet()).stream().map(AssessmentSpecDAO::getPk).collect(Collectors.toSet());
                //checking that all assessments in issueBuildCredentialDTO Belong to the credential
                issueBuildAssessmentIds.stream().forEach(id -> {
                    if (!credentialAssessmentIds.contains(id)) {
                        AssessmentSpecDAO assessmentSpecDAO = this.getAssessmentSpecService().find(id);
                        throw new EDCIBadRequestException(EDCIIssuerMessageKeys.ERROR_UNRELATED_ASSESSMENT, assessmentSpecDAO.getDefaultTitle());
                    }
                });
            }
        });
        //Map the Recipient information into the credential, including grades
        for (RecipientDataDTO recipient : issueBuildCredentialDTO.getRecipients()) {
            try {
                //Guess locale from credentials defined language
                Locale localeFromCredential = this.getEuropassCredentialDAOUtils().guessCredentialLocale(credentialDAO);
                EuropassCredentialDTO credAux = this.getCredentialMapper().toDTO(credentialDAO, recipient,
                        recipient.getAssessmentGrades(), localeFromCredential.toString());
                credentials.add(credAux);
            } catch (Exception e) {
                logger.error("Error parsing to EuropassCredentialDTO", e);
            }
        }
        if (credentials.isEmpty()) {
            throw new EDCIException("issuer.issuer.credential.unable.error");
        }
        CredentialFileDTO fileDTO = new CredentialFileDTO();
        fileDTO.setValid(true);
        List<CredentialHolderDTO> credentialsGen = null;
        //Process  and create the credentials
        this.preProcessCredentials(credentials);
        this.postProcessCredentials(credentials);
        try {
            credentialsGen = this.getFileService().createXMLFiles(credentials.toArray(new EuropassCredentialDTO[0]));
        } catch (IOException e) {
            logger.error("[E] - Error creating xml credentials");
            fileDTO.setValid(false);
        }
        //map the credential DTO information
        List<CredentialDTO> europassToDTOList = this.getCredentialMapper().europassToDTOList(credentialsGen.stream().map(cred -> cred.getCredential()).collect(Collectors.toList()),
                locale);
        fileDTO.setCredentials(europassToDTOList);
        return fileDTO;
    }

    /**
     * CREDENTIAL MANAGEMENT AND PROCESSING METHODS
     **/

    /**
     * Executes Processing consumers that can be executed with shared object references.
     *
     * @param europassCredentialDTOS the credentials to be processed
     */
    public void preProcessCredentials(List<EuropassCredentialDTO> europassCredentialDTOS) {
        this.getAndExecuteConsumers(europassCredentialDTOS, true);
    }

    /**
     * Executes Processing consumers that can't be executed with shared object references.
     *
     * @param europassCredentialDTOS the credentials to be processed
     */
    public void postProcessCredentials(List<EuropassCredentialDTO> europassCredentialDTOS) {
        this.getAndExecuteConsumers(europassCredentialDTOS, false);
    }

    /**
     * Searches in the consumer Factory for the Europass Credential root class and executes them, it is used separetely for pre or post processing consumers.
     *
     * @param europassCredentialDTOS
     * @param preProcess
     */
    public void getAndExecuteConsumers(List<EuropassCredentialDTO> europassCredentialDTOS, boolean preProcess) {
        try {
            Set<Consumer> consumers = this.getConsumerFactory().getEDCIConsumers(Class.forName(XLS.EQUIVALENCE.ROOT_CLASS.getValue()), preProcess);
            for (Consumer consumer : consumers) {
                europassCredentialDTOS.stream().forEach(consumer);
            }
        } catch (ClassNotFoundException e) {
            logger.error(String.format("No consumers were found for class %s , no processing actions will be done", XLS.EQUIVALENCE.ROOT_CLASS.getValue()));
        }
    }

    /**
     * Delete a temporal credential
     *
     * @param uuid the uuid of the credential
     * @return the resulting status
     */
    public StatusDTO deleteCredentials(String uuid) {
        String sessionId = RequestContextHolder.currentRequestAttributes().getSessionId();
        StatusDTO statusDTO = new StatusDTO();
        try {
            Files.delete(Paths.get(this.getFileUtil().getCredentialFileAbsolutePath(uuid, sessionId)));
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
        String sessionId = RequestContextHolder.currentRequestAttributes().getSessionId();
        File file = this.getEdciFileService().getOrCreateFile(this.getFileUtil().getCredentialFileAbsolutePath(uuid, sessionId));
        byte[] bytes = null;
        if (file.exists()) {
            try {
                bytes = Files.readAllBytes(Paths.get(this.getFileUtil().getCredentialFileAbsolutePath(uuid, sessionId)));
            } catch (IOException e) {
                throw new EDCIException().addDescription(String.format("Credential with uuid %s cannot be readed", uuid));
            }
        } else {
            throw new EDCINotFoundException().addDescription(String.format("Credential with uuid %s was not found", uuid));
        }

        return new ResponseEntity<byte[]>(bytes, prepareHttpHeadersForCredentialDownload(this.getFileUtil().getFileName(uuid)), HttpStatus.OK);
    }

    // #################################### SIGNING METHODS ####################################

    /**
     * Seal the Credentials using a locally stored cert, requires a password for it.
     *
     * @param localSignatureRequestDTOS Mapped request from front-end
     * @return List of CredentialDTOS with sealing status/errors
     */
    public List<CredentialDTO> signFromLocalCert(LocalSignatureRequestDTO localSignatureRequestDTOS) {

        List<CredentialDTO> credentialDTOS = Collections.synchronizedList(new ArrayList<CredentialDTO>());
        //Crate executor
        String executorName = "signFromLocalCertExecutor";
        int numThreads = this.getIssuerConfigService().getInteger(IssuerConfig.Threads.SIGNATURE_BYTES_NUM_THREADS, Runtime.getRuntime().availableProcessors());
        Long timeOutInMinutes = this.getIssuerConfigService().get(IssuerConfig.Threads.SIGNATURE_BYTES_TIMEOUT_MINUTES_THREADS, Long.class, 120L);
        this.getEdciExecutorService().createExecutor(executorName, numThreads);
        String certPath = this.getIssuerConfigService().getString(EDCIConfig.DSS.CERT_PATH);
        String sessionId = RequestContextHolder.currentRequestAttributes().getSessionId();
        Map<String, String> decodedCertificate = this.getDssedciCertificateService().getCertificateInfo(certPath, localSignatureRequestDTOS.getCertPassword());
        for (CredentialDTO credentialDTO : localSignatureRequestDTOS.getCredentialDTO()) {
            //get Cert/cred paths
            this.getEdciExecutorService().submitTask(executorName, () -> {
                credentialDTO.setSealed(false);
                credentialDTO.setSealingErrors(new ArrayList<String>());
                String credPath = this.getFileUtil().getCredentialFileAbsolutePath(credentialDTO.getUuid());
                //Sign and save cred file
                try {
                    String filePath = this.getFileUtil().getCredentialFileAbsolutePath(credentialDTO.getUuid(), sessionId);
                    this.getCertificateUtils().overwriteCertificateFields(filePath, decodedCertificate, null);
                    DSSDocument signedDocument = this.getDssedciSignService().signXMLCredential(credPath, certPath, localSignatureRequestDTOS.getCertPassword(), false);
                    if (this.getValidator().notEmpty(signedDocument)) {
                        Files.deleteIfExists(Paths.get(filePath));
                        signedDocument.save(filePath);
                    }
                    credentialDTO.setSealed(true);
                } catch (EDCIException e) {
                    String cause = e.getCause() != null ? e.getCause().getMessage() : "";
                    logger.error(String.format("[E] - Error signing credential %s / %s : %s", credPath, e.getMessage(), cause), e);
                    credentialDTO.setSealed(false);
                    credentialDTO.getSealingErrors().add(this.getEdciMessageService().getMessage(e.getMessageKey(), e.getMessageArgs()));
                } catch (Exception e) {
                    //Error during sealing process, set error for frontend
                    String cause = e.getCause() != null ? e.getCause().getMessage() : "";
                    logger.error(String.format("[E] - Error signing credential %s / %s : %s", credPath, e.getMessage(), cause), e);
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
     * @param signatureParametersDTO The Signature parameters from the certificate
     * @return the generated bytes and timestamps
     */
    public List<SignatureBytesDTO> getSignatureBytes(SignatureParametersDTO signatureParametersDTO) {
        long start = 0;
        if (logger.isDebugEnabled()) {
            start = System.currentTimeMillis();
        }
        logger.debug(String.format("Getting Signature Bytes of %d credentials", signatureParametersDTO.getUuids().size()));
        String sessionId = RequestContextHolder.currentRequestAttributes().getSessionId();
        List<SignatureBytesDTO> signatureBytes = new ArrayList<>();
        String executorName = "signatureBytesExecutor";
        int numThreads = this.getIssuerConfigService().getInteger(IssuerConfig.Threads.SIGNATURE_BYTES_NUM_THREADS, Runtime.getRuntime().availableProcessors());
        Long timeOutMinutes = this.getIssuerConfigService().get(IssuerConfig.Threads.SIGNATURE_BYTES_TIMEOUT_MINUTES_THREADS, Long.class, 120L);
        this.getEdciExecutorService().createExecutor(executorName, numThreads);
        EDCIException lastException = new EDCIException();
        for (String uuid : signatureParametersDTO.getUuids()) {
            this.getEdciExecutorService().submitTask(executorName, () -> {
                try {
                    //Get filepath and read credential file
                    String filePath = this.getFileUtil().getCredentialFileAbsolutePath(uuid, sessionId);
                    CredentialHolderDTO credHolder = null;
                    try {
                        credHolder = this.getEdciCredentialModelUtil().fromFile(this.getEdciFileService().getOrCreateFile(filePath));
                    } catch (Exception e) {
                        logger.error("Error getting the credential from the file", e);
                        throw new EDCIException(e);
                    }
                    this.getFileService().convertToPresentation(credHolder, signatureParametersDTO.getPresentation());
                    //Get Certificate fields and overwrite credential with those fields
                    Map<String, String> decodedCertificate = this.getDssedciCertificateService().getCertificateInfo(EDCIConstants.Certificate.CERTIFICATE_BEGIN_MARKER + signatureParametersDTO.getResponse().getCertificate() + EDCIConstants.Certificate.CERTIFICATE_END_MARKER);
                    List<String> certificateErrorFields = this.getCertificateUtils().overwriteCertificateFields(filePath, decodedCertificate, null);
                    logger.debug("Certificate values: " + String.join(",", decodedCertificate.values()));
                    boolean valid = certificateErrorFields.isEmpty();
                    String errorMsg = null;
                    if (!valid) {
                        errorMsg = this.getEdciMessageService().getMessage("issuer.eSeal.certificate.field.signature.errors.msg") + "\n";
                        errorMsg += certificateErrorFields.stream().collect(Collectors.joining("\n"));
                        logger.debug("Certificate error fields found: " + errorMsg);
                    }
                    String xPathLocation = EDCIConstants.Certificate.CREDENTIAL_TYPE_EUROPASS_PRESENTATION.equals(signatureParametersDTO.getPresentation()) ? DSSConstants.VERIFIABLE_PRESENTATION_XPATH_LOCATION : DSSConstants.EUROPASS_CREDENTIAL_XPATH_LOCATION;
                    boolean allowQsealsOnly = this.getIssuerConfigService().getBoolean(IssuerConfig.Issuer.ALLOW_QSEALS_ONLY, false);

                    //Generate Signing Bytes
                    logger.debug("Getting TimeStamped DataToSign... ");
                    DSSEDCIToBeSignedDTO dssedciToBeSignedDTO = this.getDssedciSignService().getTimeStampedDataToSign(signatureParametersDTO, filePath, xPathLocation, allowQsealsOnly, this.getDssedciSignService().getSignatureLevel());
                    logger.debug("EDCI To Be Signed DTO is null : " + validator.isEmpty(dssedciToBeSignedDTO));
                    //generate DSSTimestampDTO
                    logger.debug("Getting Content Timestamp from signature... ");
                    DSSTimestamp dssTimestamp = dssedciToBeSignedDTO.getSignatureDocumentForm().getContentTimestamp();
                    DSSTimestampDTO dssTimestampDTO = new DSSTimestampDTO(dssTimestamp.getBase64Timestamp(), dssTimestamp.getCanonicalizationMethod(), dssTimestamp.getType());
                    //Add the resulting signature bytes to the DTO list as a new SignatureBytesDTO based on ToBeSigned and DSSTimestamp data
                    signatureBytes.add(new SignatureBytesDTO(uuid, DatatypeConverter.printBase64Binary(dssedciToBeSignedDTO.getBytes()), dssedciToBeSignedDTO.getSigningDate(), dssTimestampDTO, certificateUtils.getCertificateReplaceMsg(decodedCertificate), valid, errorMsg));
                } catch (EDCIException e) {
                    logger.error("Error generating signature bytes for credential " + uuid, e);
                    lastException.setCause(e);
                } catch (Exception e) {
                    //Catch any unexpected exception
                    logger.error("Error generating signature bytes for credential " + uuid, e);
                }

            });
        }
        this.getEdciExecutorService().shutdownAndAwaitTermination(executorName, timeOutMinutes);
        if (logger.isDebugEnabled()) {
            long end = System.currentTimeMillis();
            logger.debug(String.format("Getting signature bytes for %d credentials took %d seconds", signatureParametersDTO.getUuids().size(), (end - start) / 1000));
        }
        //Check for the ONLY_QSEAL exception in any thread and throw it for ExceptionControllerAdvice handler
        if (lastException.getCause() != null && lastException.getCause().getMessage().equals(EDCIMessageKeys.Exception.DSS.CERTIFICATE_NOT_QSEAL_ERROR)) {
            throw (EDCIException) lastException.getCause();
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
        logger.debug(String.format("Signing %d credentials", signatureNexuDTOS.size()));
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
                    //Get file Path, digest algorithm and xpath location
                    String filePath = this.getFileUtil().getCredentialFileAbsolutePath(signatureNexuDTO.getUuid(), sessionId);
                    DigestAlgorithm digestAlgorithm = DigestAlgorithm.forName(issuerConfigService.getString(EDCIConfig.DSS.DIGEST_ALGORITHM_NAME));
                    String xpathLocation = EDCIConstants.Certificate.CREDENTIAL_TYPE_EUROPASS_PRESENTATION.equals(signatureNexuDTO.getPresentation()) ? DSSConstants.VERIFIABLE_PRESENTATION_XPATH_LOCATION : DSSConstants.EUROPASS_CREDENTIAL_XPATH_LOCATION;
                    //Sign the credential file
                    this.doSignCredentialFile(signatureNexuDTO, filePath, xpathLocation);
                    //Check for errors and XSD Validation passing for all credentials from all flows
                    CredentialDTO credentialDTO = signatureNexuDTO.getCredential();
                    credentialDTO.setSealed(true);
                    Class credentialClass = EDCIConstants.Certificate.CREDENTIAL_TYPE_EUROPASS_PRESENTATION.equals(signatureNexuDTO.getPresentation()) ? EuropassPresentationDTO.class : EuropassCredentialDTO.class;
                    ValidationResult xsdValidation = this.validateCredentialtoXSD(credentialDTO, credentialClass);
                    if (!xsdValidation.isValid()) {
                        credentialDTO.setSealed(false);
                        credentialDTO.setSealingErrors(this.getEdciValidationService().getLocalizedMessages(xsdValidation));
                    }

                    credentialDTOS.add(credentialDTO);
                } catch (Exception e) {
                    logger.error("Error signing credential " + signatureNexuDTO.getUuid(), e);
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

    /**
     * Sign an already created credential file
     *
     * @param signatureNexuDTO Parameters from nexu
     * @param filePath         file path
     * @param xpathLocation    xpath location for signature
     */
    public void doSignCredentialFile(SignatureNexuDTO signatureNexuDTO, String filePath, String xpathLocation) {
        try {
            DSSDocument signedDocument = this.getDssedciSignService().signDocument(signatureNexuDTO, filePath, xpathLocation, this.getDssedciSignService().getSignatureLevel());
            if (this.getValidator().notEmpty(signedDocument)) {
                Files.deleteIfExists(Paths.get(filePath));
                signedDocument.save(filePath);
            }
        } catch (Exception e) {
            if (this.getIssuerConfigService().getBoolean(IssuerConfig.Issuer.ALLOW_QSEALS_ONLY, false)) {
                // Just throw exception when using QSeals
                throw new EDCIException().setCause(e);
            }
            logger.error("error signing credential", e);
        }
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
                File file = this.getEdciFileService().getOrCreateFile(this.getFileUtil().getCredentialFileAbsolutePath(credentialDTO.getUuid(), sessionId));
                FileItem fileItem = null;
                try {
                    fileItem = new DiskFileItem("_credentialXML", Files.probeContentType(file.toPath()), false, file.getName(), (int) file.length(), file.getParentFile());
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
                        walletApiUrl = this.getIssuerConfigService().getString(IssuerConfig.Issuer.WALLET_API_URL)
                                .concat(this.getIssuerConfigService().getString(IssuerConfig.Issuer.WALLET_ADD_EMAIL_PATH)).replace(Parameter.WALLET_USER_EMAIL,
                                        credentialDTO.getEmail());
                        walletApiUrl = walletApiUrl.concat("?sendEmail=" + sendByEmail);
                    }

                    this.getWalletResourceUtil().doWalletPostRequest(walletApiUrl, multipartFile, EDCIParameter.WALLET_ADD_CREDENTIAL_XML, CredentialUploadResponseDTO.class, MediaType.APPLICATION_JSON, false);

                } catch (NullPointerException e) {
                    credentialDTO.setReceived(false);
                    receivedErrors.add(this.getEdciMessageService().getMessage(new EDCIException().getMessageKey()));
                } catch (EDCIRestException e) {
                    //Error Recieved from wallet, (already translated) must be passed to VIEW.
                    credentialDTO.setReceived(false);
                    receivedErrors.add(this.getEdciMessageService().getMessage(EDCIIssuerMessageKeys.ERROR_SEND_WALLET,
                            !StringUtils.isEmpty(credentialDTO.getWalletAddress()) ? credentialDTO.getWalletAddress() : credentialDTO.getEmail()));
                } catch (Exception e) {
                    //Todo-> Add generic message
                    e.printStackTrace();
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
            if (!this.getValidator().isEmpty(toEmail)) {

                String credentialFilePath = this.getFileUtil().getCredentialFileAbsolutePath(credentialDTO);
                byte[] fileBytes = null;
                try {
                    fileBytes = Files.readAllBytes(Paths.get(credentialFilePath));
                } catch (IOException e) {
                    logger.error("Could not read attachment " + credentialFilePath);
                }
                Map<String, String> wildCards = new HashMap<String, String>();

                CredentialHolderDTO europassCredentialDTO = null;
                //Use current locale for default
                Locale locale = LocaleContextHolder.getLocale();
                String studentName = credentialDTO.getStudentName();
                String issuerName = credentialDTO.getIssuerName();
                String course = credentialDTO.getCourse();
                String fileName = "";
                String subject = this.getEdciMessageService().getMessage(locale, EDCIIssuerMessageKeys.MAIL_SUBJECT_YOUR, course);
                byte[] diplomaThumbnail = null;

                //Try to extract from original XML, the primary language labels for email.
                try {
                    europassCredentialDTO = this.getFileUtil().getCredentialHolderFromFile(credentialDTO);
                    try {
                        diplomaThumbnail = diplomaUtils.generateDiplomaImage(europassCredentialDTO);
                    } catch (Exception e) {
                        logger.error(String.format("Could not generate diploma thumbnail for credential %s", credentialDTO.getUuid()));
                    }
                    //if credential can be read, guess credential locale and replace messages
                    locale = this.getEdciCredentialModelUtil().guessCredentialLocale(europassCredentialDTO.getCredential());
                    studentName = europassCredentialDTO.getCredential().getCredentialSubject().getFullName().getLocalizedStringOrAny(locale.toString());
                    issuerName = europassCredentialDTO.getIssuer().getPreferredName().getLocalizedStringOrAny(locale.toString());
                    course = europassCredentialDTO.getCredential().getTitle().getLocalizedStringOrAny(locale.toString());
                    subject = this.getEdciMessageService().getMessage(locale, EDCIIssuerMessageKeys.MAIL_SUBJECT_YOUR, course);
                    fileName = this.getEdciCredentialModelUtil().getEncodedFileName(europassCredentialDTO.getCredential(), locale.toString());
                } catch (JAXBException | IOException e) {
                    logger.error("could not recover full europass model from file" + e.getMessage(), e);
                }


                wildCards.put(IssuerConstants.MAIL_WILDCARD_SUBJECT, studentName);
                wildCards.put(IssuerConstants.MAIL_WILDCARD_ISSUER, issuerName);
                wildCards.put(IssuerConstants.MAIL_WILDCARD_TITLE, course);
                wildCards.put(IssuerConstants.MAIL_WILDCARD_VIEWERURL, issuerConfigService.getString("viewer.url"));

                try {

                    this.getEdciMailService().sendTemplatedEmail(IssuerConstants.MAIL_TEMPLATES_DIRECTORY, IssuerConstants.MAIL_ISSUED_TEMPLATE, subject, wildCards, Arrays.asList(toEmail), locale.toString(), fileBytes, fileName, diplomaThumbnail);
                    credentialDTO.setSent(true);
                } catch (EDCIException e) {
                    logger.error(String.format("Error sending email, %s", e.getDescription()), e);
                    credentialDTO.getSendErrors().add(edciMessageService.getMessage(EDCIIssuerMessageKeys.ERROR_SEND_EMAIL, toEmail));
                }

            } else {
                sendErrors.add(this.getEdciMessageService().getMessage(EDCIIssuerMessageKeys.ERROR_NO_EMAIL_FOUND_XLS));
            }
            credentialDTO.setSendErrors(sendErrors);
        }
    }

    // #################################### UTILITY METHODS ####################################

    private HttpHeaders prepareHttpHeadersForCredentialDownload(String fileName) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set(HttpHeaders.CONTENT_TYPE, eu.europa.ec.empl.edci.constants.MediaType.APPLICATION_XML_VALUE);
        httpHeaders.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"");
        return httpHeaders;
    }

    // #################################### VALIDATION METHODS ####################################

    /**
     * Validates the XML credential with the corresponding XSD, returns the unlocalized validation result
     *
     * @param credentialDTO the credential XML file
     * @param clazz         a Class that extends CredentialHOlder
     * @return the Validation result
     */
    public ValidationResult validateCredentialtoXSD(CredentialDTO credentialDTO, Class<? extends CredentialHolderDTO> clazz) {
        ValidationResult validationResult = new ValidationResult();
        validationResult.setValid(false);

        File file = this.getEdciFileService().getOrCreateFile(this.getFileUtil().getCredentialFileAbsolutePath(credentialDTO));
        SchemaLocation schemaLocation = new SchemaLocation(this.getEdciCredentialModelUtil().getSchemaLocation(clazz, credentialDTO.getType()));

        //If the CLElement described was not found in the DataBase, return invalid profile error
        if (schemaLocation == null) {
            validationResult.addValidationError(new ValidationError(this.getEdciMessageService().getMessage(EDCIMessageKeys.Exception.BadRquest.UPLOAD_INVALID_PROFILE, credentialDTO.getType())));
            return validationResult;
        }

        try {
            ValidationResult xsdValidation = this.getXmlUtil().isValid(file, schemaLocation, clazz);

            if (xsdValidation.isValid()) {
                validationResult.setValid(true);
            } else {
                validationResult.addValidationError(new ValidationError(EDCIMessageKeys.Exception.BadRquest.UPLOAD_CREDENTIAL_BAD_FORMAT));
                validationResult.getValidationErrors().addAll(xsdValidation.getValidationErrors());
            }

        } catch (Exception e) {
            validationResult.addValidationError(new ValidationError(EDCIMessageKeys.Exception.BadRquest.UPLOAD_CREDENTIAL_BAD_FORMAT));
            logger.error(e);
        }

        return validationResult;
    }

    //TODO remove this when test
    public byte[] getTestCredential() {
        byte[] bytes = null;
        try {
            bytes = this.getMockFactoryUtil().createNexusTestCredentialXML().getBytes(StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new EDCIException();
        }
        return bytes;
    }


    public DSSEDCISignService getDssedciSignService() {
        return dssedciSignService;
    }

    public void setDssedciSignService(DSSEDCISignService dssedciSignService) {
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

    public DSSEDCICertificateService getDssedciCertificateService() {
        return dssedciCertificateService;
    }

    public void setDssedciCertificateService(DSSEDCICertificateService dssedciCertificateService) {
        this.dssedciCertificateService = dssedciCertificateService;
    }

    public EDCICredentialModelUtil getEdciCredentialModelUtil() {
        return edciCredentialModelUtil;
    }

    public void setEdciCredentialModelUtil(EDCICredentialModelUtil edciCredentialModelUtil) {
        this.edciCredentialModelUtil = edciCredentialModelUtil;
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

    public XmlUtil getXmlUtil() {
        return xmlUtil;
    }

    public void setXmlUtil(XmlUtil xmlUtil) {
        this.xmlUtil = xmlUtil;
    }

    public CertificateUtils getCertificateUtils() {
        return certificateUtils;
    }

    public void setCertificateUtils(CertificateUtils certificateUtils) {
        this.certificateUtils = certificateUtils;
    }

    public EuropassCredentialSpecService getCredentialSpecService() {
        return credentialSpecService;
    }

    public void setCredentialSpecService(EuropassCredentialSpecService credentialSpecService) {
        this.credentialSpecService = credentialSpecService;
    }

    public AssessmentSpecService getAssessmentSpecService() {
        return assessmentSpecService;
    }

    public void setAssessmentSpecService(AssessmentSpecService assessmentSpecService) {
        this.assessmentSpecService = assessmentSpecService;
    }

    public ConsumerFactory getConsumerFactory() {
        return consumerFactory;
    }

    public void setConsumerFactory(ConsumerFactory consumerFactory) {
        this.consumerFactory = consumerFactory;
    }

    public EDCIValidationService getEdciValidationService() {
        return edciValidationService;
    }

    public void setEdciValidationService(EDCIValidationService edciValidationService) {
        this.edciValidationService = edciValidationService;
    }

    public WalletResourceUtil getWalletResourceUtil() {
        return walletResourceUtil;
    }

    public void setWalletResourceUtil(WalletResourceUtil walletResourceUtil) {
        this.walletResourceUtil = walletResourceUtil;
    }

    public MockFactoryUtil getMockFactoryUtil() {
        return mockFactoryUtil;
    }

    public void setMockFactoryUtil(MockFactoryUtil mockFactoryUtil) {
        this.mockFactoryUtil = mockFactoryUtil;
    }

    public EDCIMailService getEdciMailService() {
        return edciMailService;
    }

    public void setEdciMailService(EDCIMailService edciMailService) {
        this.edciMailService = edciMailService;
    }

    public EuropassCredentialDAOUtils getEuropassCredentialDAOUtils() {
        return europassCredentialDAOUtils;
    }

    public void setEuropassCredentialDAOUtils(EuropassCredentialDAOUtils europassCredentialDAOUtils) {
        this.europassCredentialDAOUtils = europassCredentialDAOUtils;
    }

    public EDCIFileService getEdciFileService() {
        return edciFileService;
    }

    public void setEdciFileService(EDCIFileService edciFileService) {
        this.edciFileService = edciFileService;
    }
}
