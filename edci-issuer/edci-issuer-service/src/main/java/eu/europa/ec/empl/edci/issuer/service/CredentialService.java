package eu.europa.ec.empl.edci.issuer.service;

import eu.europa.ec.empl.edci.constants.ControlledListConcept;
import eu.europa.ec.empl.edci.constants.EDCIParameter;
import eu.europa.ec.empl.edci.constants.MessageKeys;
import eu.europa.ec.empl.edci.datamodel.model.EuropassCredentialDTO;
import eu.europa.ec.empl.edci.datamodel.model.base.CredentialHolderDTO;
import eu.europa.ec.empl.edci.datamodel.model.dataTypes.SchemaLocation;
import eu.europa.ec.empl.edci.datamodel.model.verifiable.presentation.EuropassPresentationDTO;
import eu.europa.ec.empl.edci.datamodel.validation.ValidationError;
import eu.europa.ec.empl.edci.datamodel.validation.ValidationResult;
import eu.europa.ec.empl.edci.dss.constants.DSSConstants;
import eu.europa.ec.empl.edci.exception.EDCIException;
import eu.europa.ec.empl.edci.exception.EDCIRestException;
import eu.europa.ec.empl.edci.exception.clientErrors.EDCIBadRequestException;
import eu.europa.ec.empl.edci.exception.clientErrors.EDCINotFoundException;
import eu.europa.ec.empl.edci.factory.ConsumerFactory;
import eu.europa.ec.empl.edci.issuer.common.constants.EDCIIssuerConstants;
import eu.europa.ec.empl.edci.issuer.common.constants.EDCIIssuerMessages;
import eu.europa.ec.empl.edci.issuer.common.constants.Parameter;
import eu.europa.ec.empl.edci.issuer.common.constants.XLS;
import eu.europa.ec.empl.edci.issuer.common.model.*;
import eu.europa.ec.empl.edci.issuer.common.model.signature.DSSTimestampDTO;
import eu.europa.ec.empl.edci.issuer.common.model.signature.SignatureBytesDTO;
import eu.europa.ec.empl.edci.issuer.common.model.signature.SignatureNexuDTO;
import eu.europa.ec.empl.edci.issuer.common.model.signature.SignatureParametersDTO;
import eu.europa.ec.empl.edci.issuer.entity.specs.AssessmentSpecDAO;
import eu.europa.ec.empl.edci.issuer.entity.specs.EuropassCredentialSpecDAO;
import eu.europa.ec.empl.edci.issuer.mapper.CredentialMapper;
import eu.europa.ec.empl.edci.issuer.service.dss.DSSSignatureDocumentForm;
import eu.europa.ec.empl.edci.issuer.service.dss.DSSSignatureUtils;
import eu.europa.ec.empl.edci.issuer.service.dss.DSSTimestamp;
import eu.europa.ec.empl.edci.issuer.service.dss.SigningService;
import eu.europa.ec.empl.edci.issuer.service.spec.AssessmentSpecService;
import eu.europa.ec.empl.edci.issuer.service.spec.ControlledListsOldService;
import eu.europa.ec.empl.edci.issuer.service.spec.EuropassCredentialSpecService;
import eu.europa.ec.empl.edci.issuer.service.spec.LearningAchievementSpecService;
import eu.europa.ec.empl.edci.issuer.util.FileUtil;
import eu.europa.ec.empl.edci.issuer.utils.EuropassCredentialDAOUtils;
import eu.europa.ec.empl.edci.service.ControlledListCommonsService;
import eu.europa.ec.empl.edci.service.EDCIMailService;
import eu.europa.ec.empl.edci.service.EDCIMessageService;
import eu.europa.ec.empl.edci.service.EDCIValidationService;
import eu.europa.ec.empl.edci.util.*;
import eu.europa.esig.dss.enumerations.*;
import eu.europa.esig.dss.model.DSSDocument;
import eu.europa.esig.dss.model.ToBeSigned;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItem;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.LocaleUtils;
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
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.inject.Inject;
import javax.xml.bind.DatatypeConverter;
import javax.xml.bind.JAXBException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Service
public class CredentialService {
    private static final Logger logger = Logger.getLogger(CredentialService.class);

    @Autowired
    private EDCIMessageService edciMessageService;

    @Autowired
    private IssuerConfigService issuerConfigService;

    @Autowired
    private EDCICredentialModelUtil edciCredentialModelUtil;

    @Autowired
    private XmlUtil xmlUtil;

    @Autowired
    private Validator validator;

    @Autowired
    private SigningService signingService;

    @Inject
    private FileUtil fileUtil;

    @Autowired
    private CertificateService certificateService;

    @Autowired
    private EuropassCredentialSpecService credentialSpecService;

    @Autowired
    private LearningAchievementSpecService learningAchievementSpecService;

    @Autowired
    private AssessmentSpecService assessmentSpecService;

    @Autowired
    private FileService fileService;

    @Autowired
    private CredentialMapper credentialMapper;

    @Autowired
    private ConsumerFactory consumerFactory;

    @Autowired
    private ControlledListsOldService controlledListsService;

    @Autowired
    private EDCIValidationService edciValidationService;

    @Autowired
    private WalletResourceUtil walletResourceUtil;

    @Autowired
    private MockFactoryUtil mockFactoryUtil;

    @Autowired
    private ControlledListCommonsService controlledListCommonsService;

    @Autowired
    private EDCIMailService edciMailService;

    @Autowired
    private EuropassCredentialDAOUtils europassCredentialDAOUtils;

    /**
     * ISSUING METHODS
     **/

    public CredentialFileDTO uploadCredentials(MultipartFile file, String locale) {
        List<EuropassCredentialDTO> eups;
        try {
            if (this.isCredentialSigned(file.getBytes()))
                throw new EDCIBadRequestException("upload.credential.already.signed").addDescription("Already signed Credential");

            eups = fileService.getEuropassCredentialsFromUploadFile(file);
        } catch (Exception e) {
            throw new EDCIBadRequestException(MessageKeys.Exception.BadRquest.UPLOAD_CREDENTIAL_BAD_FORMAT).setCause(e);
        }

        this.preProcessCredentials(eups);
        this.postProcessCredentials(eups);

        try {
            fileService.createXMLFiles(eups.toArray(new EuropassCredentialDTO[0]));
        } catch (Exception e) {
            throw new EDCIBadRequestException(MessageKeys.Exception.Global.GLOBAL_ERROR_CREATING_FILE);
        }

        List<CredentialDTO> credentialDTOS = credentialMapper.europassToDTOList(eups, locale);

        CredentialFileDTO f = new CredentialFileDTO();
        f.setCredentials(credentialDTOS);
        f.setValid(true);
        return f;
    }

    //ToDo -> Change naming of FileDTO (IssuingDTO?)
    public CredentialFileDTO issueCredentials(IssueBuildCredentialDTO issueBuildCredentialDTO, String locale) {
        List<EuropassCredentialDTO> credentials = new ArrayList<>();

        EuropassCredentialSpecDAO credentialDAO = credentialSpecService.find(issueBuildCredentialDTO.getCredential());

        if (credentialDAO == null) {
            throw new EDCINotFoundException().addDescription("Credential with oid [" + issueBuildCredentialDTO.getCredential() + "] not found");
        }
        //Get credential-assigned assessments
        Set<Long> credentialAssessmentIds = credentialSpecService.getCredentialAssessments(issueBuildCredentialDTO.getCredential()).stream().map(AssessmentSpecDAO::getPk).collect(Collectors.toSet());
        //Check that assessments make sense, afterwards in validationconsumer, will validate that all necessary score fields are filled.
        issueBuildCredentialDTO.getRecipients().forEach(r -> {
            if (r.getAssessmentGrades() != null) {
                //checking that all assessments in issueBuildCredentialDTO exist, map those to Ids to check coherence afterwards, if any assessment is missing from BD an exception is thrown
                Set<Long> issueBuildAssessmentIds = assessmentSpecService.retrieveEntities(true, r.getAssessmentGrades().keySet()).stream().map(AssessmentSpecDAO::getPk).collect(Collectors.toSet());
                //checking that all assessments in issueBuildCredentialDTO Belong to the credential
                issueBuildAssessmentIds.stream().forEach(id -> {
                    if (!credentialAssessmentIds.contains(id)) {
                        AssessmentSpecDAO assessmentSpecDAO = assessmentSpecService.find(id);
                        throw new EDCIBadRequestException(EDCIIssuerMessages.ERROR_UNRELATED_ASSESSMENT, assessmentSpecDAO.getDefaultTitle());
                    }
                });
            }
        });

        for (RecipientDataDTO recipient : issueBuildCredentialDTO.getRecipients()) {
            try {
                Locale localeFromCredential = europassCredentialDAOUtils.guessCredentialLocale(credentialDAO);
                EuropassCredentialDTO credAux = credentialMapper.toDTO(credentialDAO, recipient,
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

        //ToDo-> Unify Pre/Pro processing for both excel and CredentialBuilder
        this.preProcessCredentials(credentials);
        this.postProcessCredentials(credentials);

        //make xml
        try {
            credentialsGen = fileService.createXMLFiles(credentials.toArray(new EuropassCredentialDTO[0]));
        } catch (IOException e) {
            logger.error("[E] - Error creating xml credentials");
            fileDTO.setValid(false);
        }

        List<CredentialDTO> europassToDTOList = credentialMapper.europassToDTOList(credentialsGen.stream().map(cred -> cred.getCredential()).collect(Collectors.toList()),
                locale);
        fileDTO.setCredentials(europassToDTOList);

        return fileDTO;
    }

    /**
     * CREDENTIAL MANAGEMENT AND PROCESSING METHODS
     **/

    public void preProcessCredentials(List<EuropassCredentialDTO> europassCredentialDTOS) {
        this.getAndExecuteConsumers(europassCredentialDTOS, true);
    }

    public void postProcessCredentials(List<EuropassCredentialDTO> europassCredentialDTOS) {
        this.getAndExecuteConsumers(europassCredentialDTOS, false);
    }

    public void getAndExecuteConsumers(List<EuropassCredentialDTO> europassCredentialDTOS, boolean preProcess) {
        try {
            Set<Consumer> consumers = consumerFactory.getEDCIConsumers(Class.forName(XLS.EQUIVALENCE.ROOT_CLASS.getValue()), preProcess);
            for (Consumer consumer : consumers) {
                europassCredentialDTOS.stream().forEach(consumer);
            }
        } catch (ClassNotFoundException e) {
            logger.error(String.format("No consumers were found for class %s , no processing actions will be done", XLS.EQUIVALENCE.ROOT_CLASS.getValue()));
        }
    }

    public StatusDTO deleteCredentials(String uuid) {
        String sessionId = RequestContextHolder.currentRequestAttributes().getSessionId();
        StatusDTO statusDTO = new StatusDTO();
        try {
            Files.delete(Paths.get(fileUtil.getCredentialFileAbsolutePath(uuid, sessionId)));
            statusDTO.setStatus(true);
        } catch (IOException e) {
            statusDTO.setStatus(false);
            throw new EDCIException().addDescription("Error deleting xml file").setCause(e);
        }
        return statusDTO;
    }

    public ResponseEntity<byte[]> downloadFile(String uuid) {
        String sessionId = RequestContextHolder.currentRequestAttributes().getSessionId();
        File file = new File(fileUtil.getCredentialFileAbsolutePath(uuid, sessionId));
        byte[] bytes = null;
        if (file.exists()) {
            try {
                bytes = Files.readAllBytes(Paths.get(fileUtil.getCredentialFileAbsolutePath(uuid, sessionId)));
            } catch (IOException e) {
                throw new EDCIException().addDescription(String.format("Credential with uuid %s cannot be readed", uuid));
            }
        } else {
            throw new EDCINotFoundException().addDescription(String.format("Credential with uuid %s was not found", uuid));
        }

        return new ResponseEntity<byte[]>(bytes, prepareHttpHeadersForCredentialDownload(fileUtil.getFileName(uuid)), HttpStatus.OK);
    }

    /**
     * SEALING METHODS
     **/
    //ToDo -> Exception Handling
    public List<SignatureBytesDTO> getSignatureBytes(SignatureParametersDTO signatureParametersDTO) {
        String sessionId = RequestContextHolder.currentRequestAttributes().getSessionId();
        List<SignatureBytesDTO> signatureBytes = new ArrayList<>();

        ExecutorService executor = Executors.newFixedThreadPool(issuerConfigService.getInteger("signatureBytes.num.threads", Runtime.getRuntime().availableProcessors()));

        for (String uuid : signatureParametersDTO.getUuids()) {

            ContextAwareRunnable run = new ContextAwareRunnable(() -> {

                try {

                    Thread.currentThread().setName("signatureBytes."+uuid);

                    // HARDCODE PARAMS
                    DSSSignatureDocumentForm form = new DSSSignatureDocumentForm();
                    form.setContainerType(null);
                    form.setSignatureForm(SignatureForm.XAdES);
                    form.setSignaturePackaging(SignaturePackaging.ENVELOPED);
                    form.setSignatureLevel(SignatureLevel.XAdES_BASELINE_LT);
                    form.setDigestAlgorithm(DigestAlgorithm.SHA256);

                    form.setBase64Certificate(signatureParametersDTO.getResponse().getCertificate());
                    form.setBase64CertificateChain(signatureParametersDTO.getResponse().getCertificateChain());
                    form.setEncryptionAlgorithm(EncryptionAlgorithm.RSA);
                    Date signingDate = new Date();
                    form.setSigningDate(signingDate);

                    form.setAddContentTimestamp(true);

                    DSSTimestamp dssTimestamp = null;
                    DSSTimestampDTO dssTimestampDTO = new DSSTimestampDTO();

                    String filePath = fileUtil.getCredentialFileAbsolutePath(uuid, sessionId);

                    CredentialHolderDTO credHolder = null;
                    try {
                        credHolder = edciCredentialModelUtil.fromFile(new File(filePath));
                    } catch (Exception e) {
                        logger.error("Error getting the credential from the file", e);
                        throw new EDCIException(e);
                    }

                    if (credHolder instanceof EuropassPresentationDTO) {
                        logger.error("We're not expecting a EuropassPresentationDTO here");
                    } else if (EDCIIssuerConstants.CREDENTIAL_TYPE_EUROPASS_PRESENTATION.equals(signatureParametersDTO.getPresentation())
                            && credHolder instanceof EuropassCredentialDTO) {
                        EuropassPresentationDTO presentation = new EuropassPresentationDTO();
                        presentation.setVerifiableCredential((EuropassCredentialDTO) credHolder);
                        presentation.setType(edciCredentialModelUtil.getTypeCode(EuropassPresentationDTO.class, ControlledListConcept.VERIFICATION_TYPE_MANDATED_ISSUE.getUrl()));
                        presentation.setId(URI.create(presentation.getPrefix(presentation).concat(UUID.randomUUID().toString())));

                        try {
                            fileService.createXMLFiles(presentation);
                        } catch (IOException e) {
                            logger.error("Error creating EuropassPresentation XML file", e);
                            throw new EDCIException(e);
                        }
                    }

                    Map<String, String> decodedCertificate = certificateService.getCertificateInfo(EDCIIssuerConstants.CERTIFICATE_BEGIN_MARKER + signatureParametersDTO.getResponse().getCertificate() + EDCIIssuerConstants.CERTIFICATE_END_MARKER);
                    List<String> certificateErrorFields = certificateService.overwriteCertificateFields(filePath, decodedCertificate);

                    form.setDocumentToSign(new File(filePath));

                    boolean valid = certificateErrorFields.isEmpty();
                    String errorMsg = null;
                    if (!valid) {
                        errorMsg = edciMessageService.getMessage("issuer.eSeal.certificate.field.signature.errors.msg") + "\n";
                        errorMsg += certificateErrorFields.stream().collect(Collectors.joining("\n"));
                    }

                    if (form.isAddContentTimestamp()) {
                        dssTimestamp = DSSSignatureUtils.fromTimestampToken(signingService.getContentTimestamp(form,
                                EDCIIssuerConstants.CREDENTIAL_TYPE_EUROPASS_PRESENTATION.equals(signatureParametersDTO.getPresentation()) ? DSSConstants.VERIFIABLE_PRESENTATION_XPATH_LOCATION : DSSConstants.EUROPASS_CREDENTIAL_XPATH_LOCATION));
                        dssTimestampDTO = new DSSTimestampDTO(dssTimestamp.getBase64Timestamp(), dssTimestamp.getCanonicalizationMethod(), dssTimestamp.getType());
                        form.setContentTimestamp(dssTimestamp);
                    }

                    ToBeSigned dataToSign = signingService.getDataToSign(form,
                            Boolean.parseBoolean(issuerConfigService.getDatabaseConfigurationValue(EDCIIssuerConstants.CONFIG_PROPERTY_ALLOW_QSEALS_ONLY)),
                            EDCIIssuerConstants.CREDENTIAL_TYPE_EUROPASS_PRESENTATION.equals(signatureParametersDTO.getPresentation()) ? DSSConstants.VERIFIABLE_PRESENTATION_XPATH_LOCATION : DSSConstants.EUROPASS_CREDENTIAL_XPATH_LOCATION);
                    signatureBytes.add(new SignatureBytesDTO(uuid, DatatypeConverter.printBase64Binary(dataToSign.getBytes()), signingDate, dssTimestampDTO, certificateService.getCertificateReplaceMsg(decodedCertificate), valid, errorMsg));

                } catch (Exception e) {
//                    String errorMsg = null;
//                    if (e instanceof EDCIException) {
//                        errorMsg = edciMessageService.getMessage(((EDCIException) e).getMessageKey(), ((EDCIException) e).getMessageArgs());
//                    } else {
//                        errorMsg = edciMessageService.getMessage("global.internal.error");
//                    }
                    logger.error("Error signing credential " + uuid, e);
//                    signatureBytes.add(new SignatureBytesDTO(uuid, null, null, null, null, false, errorMsg));
                }

            }, RequestContextHolder.currentRequestAttributes());

            executor.submit(run);

        }

        executor.shutdown();
        try {
            executor.awaitTermination(issuerConfigService.get("signatureBytes.timeout.minutes.threads", Long.class, 120L), TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            throw new EDCIException(e).addDescription("Timeout while getting the signature bytes of the credentials");
        }

        return signatureBytes;
    }

    public List<CredentialDTO> signCredential(List<SignatureNexuDTO> signatureNexuDTOS) {
        //Get Signature Values from F/E
        String sessionId = RequestContextHolder.currentRequestAttributes().getSessionId();
        List<CredentialDTO> credentialDTOS = Collections.synchronizedList(new ArrayList<CredentialDTO>());

        ExecutorService executor = Executors.newFixedThreadPool(issuerConfigService.getInteger("signCredential.num.threads", Runtime.getRuntime().availableProcessors()));

        for (SignatureNexuDTO signatureNexuDTO : signatureNexuDTOS) {

            ContextAwareRunnable run = new ContextAwareRunnable(
            () -> {

                try {

                    Thread.currentThread().setName("signCredential."+signatureNexuDTO.getUuid());

                    //Prepare Signature Parameters
                    DSSSignatureDocumentForm form = new DSSSignatureDocumentForm();
                    form.setContainerType(null);

                    form.setSigningDate(signatureNexuDTO.getDate());

                    if (signatureNexuDTO.getDssTimestampDTO() != null) {
                        form.setContentTimestamp(new DSSTimestamp(signatureNexuDTO.getDssTimestampDTO().getBase64Timestamp(), signatureNexuDTO.getDssTimestampDTO().getCanonicalizationMethod(), signatureNexuDTO.getDssTimestampDTO().getType()));
                    }
                    form.setSignatureForm(SignatureForm.XAdES);
                    form.setSignaturePackaging(SignaturePackaging.ENVELOPED);
                    form.setSignatureLevel(SignatureLevel.XAdES_BASELINE_LT);
                    form.setBase64Certificate(signatureNexuDTO.getResponse().getCertificate());
                    form.setBase64CertificateChain(signatureNexuDTO.getResponse().getCertificateChain());
                    form.setBase64SignatureValue(signatureNexuDTO.getResponse().getSignatureValue());
                    form.setEncryptionAlgorithm(EncryptionAlgorithm.forName(signatureNexuDTO.getResponse().getSignatureAlgorithm().split("_")[0]));
                    form.setDigestAlgorithm(DigestAlgorithm.forName(signatureNexuDTO.getResponse().getSignatureAlgorithm().split("_")[1]));
                    String filePath = fileUtil.getCredentialFileAbsolutePath(signatureNexuDTO.getUuid(), sessionId);
                    File file = new File(filePath);
                    form.setDocumentToSign(file);

                    //Sign Document and save result in filePath
                    try {

                        String xpathLocation = null;
                        if (EDCIIssuerConstants.CREDENTIAL_TYPE_EUROPASS_PRESENTATION.equals(signatureNexuDTO.getPresentation())) {
                            xpathLocation = DSSConstants.VERIFIABLE_PRESENTATION_XPATH_LOCATION;
                        } else {
                            xpathLocation = DSSConstants.EUROPASS_CREDENTIAL_XPATH_LOCATION;
                        }

                        DSSDocument signedDocument = signingService.signDocument(form, xpathLocation);
                        if (validator.notEmpty(signedDocument)) {
                            Files.deleteIfExists(Paths.get(filePath));
                            signedDocument.save(filePath);
                        }
                    } catch (Exception e) {
                        if (Boolean.valueOf(issuerConfigService.getDatabaseConfigurationValue(EDCIIssuerConstants.CONFIG_PROPERTY_ALLOW_QSEALS_ONLY))) {
                            // Just throw exception when using QSeals
                            throw new EDCIException().setCause(e);
                        }
                        logger.error("error signing credential", e);
                    }


                    //Check for errors and XSD Validation passing for all credentials from all flows
                    CredentialDTO credentialDTO = signatureNexuDTO.getCredential();
                    credentialDTO.setSealed(true);

                    ValidationResult xsdValidation = this.validateCredentialtoXSD(credentialDTO,
                            EDCIIssuerConstants.CREDENTIAL_TYPE_EUROPASS_PRESENTATION.equals(signatureNexuDTO.getPresentation()) ? EuropassPresentationDTO.class : EuropassCredentialDTO.class);
                    if (!xsdValidation.isValid()) {
                        credentialDTO.setSealed(false);
                        credentialDTO.setSealingErrors(edciValidationService.getLocalizedMessages(xsdValidation));
                    }

                    credentialDTOS.add(credentialDTO);

                } catch (Exception e) {
//                    String errorMsg = null;
//                    if (e instanceof EDCIException) {
//                        errorMsg = edciMessageService.getMessage(((EDCIException) e).getMessageKey(), ((EDCIException) e).getMessageArgs());
//                    } else {
//                        errorMsg = edciMessageService.getMessage("global.internal.error");
//                    }
                    logger.error("Error signing credential " + signatureNexuDTO.getUuid(), e);
                }

            }, RequestContextHolder.currentRequestAttributes());

            executor.submit(run);

        }

        executor.shutdown();
        try {
            executor.awaitTermination(issuerConfigService.get("signCredential.timeout.minutes.threads", Long.class, 120L), TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            throw new EDCIException(e).addDescription("Timeout while signing the credentials");
        }

        return credentialDTOS;
    }


    /**
     * SEND METHODSbir
     **/

    public List<CredentialDTO> sendCredentials(List<CredentialDTO> credentialDTOS) {

        ExecutorService executor = Executors.newFixedThreadPool(issuerConfigService.getInteger("sendCredential.num.threads", 2));

        ContextAwareRunnable emailIssuerSend = new ContextAwareRunnable(() -> {
            Thread.currentThread().setName("sendCredential.issuer");
            sendEmailCredentials(credentialDTOS);
        }, RequestContextHolder.currentRequestAttributes());

        executor.submit(emailIssuerSend);

        ContextAwareRunnable emailWalletSend = new ContextAwareRunnable(() -> {
            Thread.currentThread().setName("sendCredential.wallet");
            sendWalletCredentials(credentialDTOS);
        }, RequestContextHolder.currentRequestAttributes());

        executor.submit(emailWalletSend);

        executor.shutdown();
        try {
            executor.awaitTermination(issuerConfigService.get("sendCredential.timeout.minutes.threads", Long.class, 30L), TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            throw new EDCIException(e).addDescription("Timeout while ending the credentials");
        }

        return credentialDTOS;
    }

    private void sendWalletCredentials(List<CredentialDTO> credentialDTOs) {
        for (CredentialDTO credentialDTO : credentialDTOs) {
            credentialDTO.setReceived(true);
            List<String> receivedErrors = credentialDTO.getReceivedErrors();

            boolean sendByEmail = issuerConfigService.getBoolean("wallet.send.by.email.enabled", false);

            if (!validator.isEmpty(credentialDTO.getWalletAddress()) || (!validator.isEmpty(credentialDTO.getEmail()) && sendByEmail)) {
                String sessionId = RequestContextHolder.currentRequestAttributes().getSessionId();
                File file = new File(fileUtil.getCredentialFileAbsolutePath(credentialDTO.getUuid(), sessionId));
                FileItem fileItem = null;
                try {
                    fileItem = new DiskFileItem("_credentialXML", Files.probeContentType(file.toPath()), false, file.getName(), (int) file.length(), file.getParentFile());
                    InputStream input = new FileInputStream(file);
                    OutputStream os = fileItem.getOutputStream();
                    IOUtils.copy(input, os);
                    // Or faster..
                    // IOUtils.copy(new FileInputStream(file), fileItem.getOutputStream());
                } catch (IOException ex) {
                    // do something.
                }

                MultipartFile multipartFile = new CommonsMultipartFile(fileItem);

                try {

                    String walletApiUrl = null;

                    if (!validator.isEmpty(credentialDTO.getWalletAddress())) {
                        walletApiUrl = issuerConfigService.getString(EDCIIssuerConstants.CONFIG_PROPERTY_WALLET_API_URL)
                                .concat(issuerConfigService.getString(EDCIIssuerConstants.CONFIG_PROPERTY_WALLET_ADD_PATH)).replace(Parameter.WALLET_USER_ID,
                                        credentialDTO.getWalletAddress().substring(credentialDTO.getWalletAddress().lastIndexOf('/') + 1));
                    } else {
                        walletApiUrl = issuerConfigService.getString(EDCIIssuerConstants.CONFIG_PROPERTY_WALLET_API_URL)
                                .concat(issuerConfigService.getString(EDCIIssuerConstants.CONFIG_PROPERTY_WALLET_ADD_EMAIL_PATH)).replace(Parameter.WALLET_USER_EMAIL,
                                        credentialDTO.getEmail());
                    }

                    walletResourceUtil.doWalletPostRequest(walletApiUrl, multipartFile, EDCIParameter.WALLET_ADD_CREDENTIAL_XML, CredentialUploadResponseDTO.class, MediaType.APPLICATION_JSON, false);

                } catch (NullPointerException e) {
                    credentialDTO.setReceived(false);
                    receivedErrors.add(edciMessageService.getMessage(new EDCIException().getMessageKey()));
                } catch (EDCIRestException e) {
                    //Error Recieved from wallet, (already translated) must be passed to VIEW.
                    credentialDTO.setReceived(false);
                    receivedErrors.add(edciMessageService.getMessage(EDCIIssuerMessages.ERROR_SEND_WALLET, credentialDTO.getEmail()));
                } catch (Exception e) {
                    //Todo-> Add generic message
                    e.printStackTrace();
                    receivedErrors.add(edciMessageService.getMessage(EDCIIssuerMessages.GLOBAL_INTERNAL_ERROR));
                    credentialDTO.setReceived(false);
                }

            } else {
                receivedErrors.add(edciMessageService.getMessage(EDCIIssuerMessages.ERROR_NO_WALLET_FOUND_XLS));
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
            if (!validator.isEmpty(toEmail)) {

                String credentialFilePath = fileUtil.getCredentialFileAbsolutePath(credentialDTO);
                byte[] fileBytes = null;
                try {
                    fileBytes = Files.readAllBytes(Paths.get(credentialFilePath));
                } catch (IOException e) {
                    logger.error("Could not read attachment " + credentialFilePath);
                }
                Map<String, String> wildCards = new HashMap<String, String>();

                CredentialHolderDTO europassCredentialDTO = null;
                //Use current locale for default
                String locale = LocaleContextHolder.getLocale().toString();
                String studentName = credentialDTO.getStudentName();
                String issuerName = credentialDTO.getIssuerName();
                String course = credentialDTO.getCourse();
                String fileName = "";
                String subject = edciMessageService.getMessage(locale, EDCIIssuerMessages.MAIL_SUBJECT_YOUR, course);
                //Try to extract from original XML, the primary language labels for email.
                try {
                    europassCredentialDTO = fileUtil.getCredentialHolderFromFile(credentialDTO);
                    //if credential can be read, guess credential locale and replace messages
                    locale = edciCredentialModelUtil.guessCredentialLocale(europassCredentialDTO.getCredential()).toString();
                    studentName = europassCredentialDTO.getCredential().getCredentialSubject().getFullName().getLocalizedStringOrAny(locale);
                    issuerName = europassCredentialDTO.getIssuer().getPreferredName().getLocalizedStringOrAny(locale);
                    course = europassCredentialDTO.getCredential().getTitle().getLocalizedStringOrAny(locale);
                    subject = edciMessageService.getMessage(LocaleUtils.toLocale(locale), EDCIIssuerMessages.MAIL_SUBJECT_YOUR, course);
                    fileName = edciCredentialModelUtil.getEncodedFileName(europassCredentialDTO.getCredential(), locale);
                } catch (JAXBException | IOException e) {
                    logger.error("could not recover full europass model from file" + e.getMessage(), e);
                }

                wildCards.put(EDCIIssuerConstants.MAIL_WILDCARD_SUBJECT, studentName);
                wildCards.put(EDCIIssuerConstants.MAIL_WILDCARD_ISSUER, issuerName);
                wildCards.put(EDCIIssuerConstants.MAIL_WILDCARD_TITLE, course);
                wildCards.put(EDCIIssuerConstants.MAIL_WILDCARD_VIEWERURL, issuerConfigService.getString("viewer.url"));

                try {
                    edciMailService.sendTemplatedEmail(EDCIIssuerConstants.MAIL_TEMPLATES_DIRECTORY, EDCIIssuerConstants.MAIL_ISSUED_TEMPLATE, subject, wildCards, Arrays.asList(toEmail), locale, fileBytes, fileName);
                    credentialDTO.setSent(true);
                } catch (EDCIException e) {
                    logger.error(String.format("Error sending email, %s", e.getDescription()), e);
                    credentialDTO.getSendErrors().add(edciMessageService.getMessage(EDCIIssuerMessages.ERROR_SEND_EMAIL, toEmail));
                }

            } else {
                sendErrors.add(edciMessageService.getMessage(EDCIIssuerMessages.ERROR_NO_EMAIL_FOUND_XLS));
            }
            credentialDTO.setSendErrors(sendErrors);
        }
    }

    //UTILITY METHODS

    private HttpHeaders prepareHttpHeadersForCredentialDownload(String fileName) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set(HttpHeaders.CONTENT_TYPE, eu.europa.ec.empl.edci.constants.MediaType.APPLICATION_XML_VALUE);
        httpHeaders.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"");
        return httpHeaders;
    }

//VALIDATION METHODS

    /**
     * Validates the XML credential with the corresponing XSD. PENDING
     *
     * @param credentialDTO the credential XML file
     * @return true if valid, false otherwise
     */
    public ValidationResult validateCredentialtoXSD(CredentialDTO credentialDTO, Class<? extends CredentialHolderDTO> clazz) {
        ValidationResult validationResult = new ValidationResult();
        validationResult.setValid(false);

        File file = new File(fileUtil.getCredentialFileAbsolutePath(credentialDTO));
        SchemaLocation schemaLocation = new SchemaLocation(edciCredentialModelUtil.getSchemaLocation(clazz, credentialDTO.getType()));

        //If the CLElement described was not found in the DataBase, return invalid profile error
        if (schemaLocation == null) {
            validationResult.addValidationError(new ValidationError(edciMessageService.getMessage(MessageKeys.Exception.BadRquest.UPLOAD_INVALID_PROFILE, credentialDTO.getType())));
            return validationResult;
        }

        try {
            ValidationResult xsdValidation = xmlUtil.isValid(file, schemaLocation, clazz);

            if (xsdValidation.isValid()) {
                validationResult.setValid(true);
            } else {
                validationResult.addValidationError(new ValidationError(MessageKeys.Exception.BadRquest.UPLOAD_CREDENTIAL_BAD_FORMAT));
                validationResult.getValidationErrors().addAll(xsdValidation.getValidationErrors());
            }

        } catch (Exception e) {
            validationResult.addValidationError(new ValidationError(MessageKeys.Exception.BadRquest.UPLOAD_CREDENTIAL_BAD_FORMAT));
            logger.error(e);
        }

        return validationResult;
    }

    /**
     * Checks if the XMl is Signed. This does not validate the signature!!
     *
     * @param europassCredentialXML the credential
     * @return true if valid, false otherwise
     * @throws ParserConfigurationException error parsing
     * @throws SAXException                 error parsing
     * @throws IOException                  error parsing
     */
    public boolean isCredentialSigned(byte[] europassCredentialXML) throws
            ParserConfigurationException, SAXException, IOException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setIgnoringComments(true);
        factory.setNamespaceAware(true);
        DocumentBuilder parser = factory.newDocumentBuilder();

        Document document = parser.parse(new ByteArrayInputStream(europassCredentialXML));

        NodeList numCredentials = document.getElementsByTagNameNS("*", "europassCredential");
        NodeList credentialsSigned = document.getElementsByTagNameNS("*", "Signature");

        return credentialsSigned.getLength() > 0 && numCredentials.getLength() == credentialsSigned.getLength();

    }

    //TODO remove this when test
    public byte[] getTestCredential() {
        byte[] bytes = null;
        try {
            bytes = xmlUtil.toByteArray(mockFactoryUtil.createNexusTestMockMultipleCredentialsDTO());
        } catch (Exception e) {
            throw new EDCIException();
        }
        return bytes;
    }

}
