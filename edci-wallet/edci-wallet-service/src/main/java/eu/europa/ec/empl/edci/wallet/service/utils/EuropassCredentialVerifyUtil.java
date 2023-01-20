package eu.europa.ec.empl.edci.wallet.service.utils;

import eu.europa.ec.empl.edci.constants.ControlledListConcept;
import eu.europa.ec.empl.edci.constants.EDCIConfig;
import eu.europa.ec.empl.edci.constants.EDCIConstants;
import eu.europa.ec.empl.edci.constants.ErrorCode;
import eu.europa.ec.empl.edci.datamodel.model.EuropassCredentialDTO;
import eu.europa.ec.empl.edci.datamodel.model.base.CredentialHolderDTO;
import eu.europa.ec.empl.edci.datamodel.model.dataTypes.SchemaLocation;
import eu.europa.ec.empl.edci.datamodel.model.dataTypes.Text;
import eu.europa.ec.empl.edci.datamodel.model.verifiable.presentation.EuropassPresentationDTO;
import eu.europa.ec.empl.edci.datamodel.model.verifiable.presentation.VerificationCheckDTO;
import eu.europa.ec.empl.edci.datamodel.validation.ValidationResult;
import eu.europa.ec.empl.edci.dss.service.DSSEDCIValidationService;
import eu.europa.ec.empl.edci.exception.EDCIException;
import eu.europa.ec.empl.edci.service.ControlledListCommonsService;
import eu.europa.ec.empl.edci.service.EDCIMessageService;
import eu.europa.ec.empl.edci.service.QMSAccreditationsService;
import eu.europa.ec.empl.edci.util.ContextAwareRunnable;
import eu.europa.ec.empl.edci.util.EDCICredentialModelUtil;
import eu.europa.ec.empl.edci.util.XmlUtil;
import eu.europa.ec.empl.edci.wallet.service.WalletConfigService;
import eu.europa.esig.dss.diagnostic.CertificateWrapper;
import eu.europa.esig.dss.validation.CommonCertificateVerifier;
import eu.europa.esig.dss.validation.reports.Reports;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.multipart.MultipartFile;

import javax.xml.bind.JAXBException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
public class EuropassCredentialVerifyUtil {
    private static final Logger logger = LogManager.getLogger(EuropassCredentialVerifyUtil.class);

   /* private static final String FORMAT = "FORMAT";
    private static final String SEAL = "SEAL";
    private static final String OWNER = "OWNER";
    private static final String REVOCATION = "REVOCATION";
    private static final String ACCREDITATION = "ACCREDITATION";
    private static final String VALIDITY = "VALIDITY";*/

//    private static final String TYPE_FORMAT_URI = "http://data.europa.eu/europass/verificationType/format";
//    private static final String TYPE_SEAL_URI = "http://data.europa.eu/europass/verificationType/seal";
//    private static final String TYPE_OWNER_URI = "http://data.europa.eu/europass/verificationType/onwer";
//    private static final String TYPE_REVOCATION_URI = "http://data.europa.eu/europass/verificationType/revocation";
//    private static final String TYPE_ACREDITATION_URI = "http://data.europa.eu/europass/verificationType/acreditation";
//    private static final String TYPE_VALIDITY_URI = "http://data.europa.eu/europass/verificationType/validity";

//    private static final List<String> VERIFICATION_TYPES = Arrays.asList(TYPE_SEAL_URI, TYPE_FORMAT_URI, TYPE_OWNER_URI, TYPE_REVOCATION_URI, TYPE_ACREDITATION_URI, TYPE_VALIDITY_URI);

    private static final String STATUS_OK_URI = "http://data.europa.eu/europass/verificationStatus/ok";
    private static final String STATUS_KO_URI = "http://data.europa.eu/europass/verificationStatus/ko";
    private static final String STATUS_SKIPPED_URI = "http://data.europa.eu/europass/verificationStatus/skip";

    private static final int STATUS_CODE_OK = 0;
    private static final int STATUS_CODE_KO = 1;
    private static final int STATUS_CODE_SKIPPED = 2;

    @Autowired
    private EDCICredentialModelUtil edciCredentialModelUtil;

    @Autowired
    private DSSEDCIValidationService dssedciValidationService;

    @Autowired
    EDCIMessageService messageSource;

    @Autowired
    private CredentialUtil credentialUtil;

    @Autowired
    private XmlUtil xmlUtil;

    @Autowired
    private CommonCertificateVerifier certificateVerifier;

    @Autowired
    private WalletConfigService walletConfigService;

    @Autowired
    private ControlledListCommonsService controlledListCommonsService;

    @Autowired
    private QMSAccreditationsService qmsAccreditationsService;

    public List<VerificationCheckDTO> verifyCredential(byte[] xmlBytes) {

        CredentialHolderDTO credentialHolderDTO = null;
        try {

            credentialHolderDTO = edciCredentialModelUtil.fromByteArray(xmlBytes);

        } catch (IOException e) {
            logger.error(String.format("IOException: %s", e.getMessage()), e);
            throw new EDCIException(HttpStatus.BAD_REQUEST, ErrorCode.CREDENTIAL_NOT_READABLE, "wallet.xml.unreadable").setCause(e);
        } catch (JAXBException e) {
            logger.error(String.format("JAXBException: %s", e.getMessage()), e);
            throw new EDCIException(HttpStatus.BAD_REQUEST, ErrorCode.CREDENTIAL_NOT_READABLE, "wallet.xml.unreadable").setCause(e);
        }

        return verifyCredential(credentialHolderDTO, xmlBytes);

    }

    protected Date least(Date a, Date b) {
        return a == null ? b : (b == null ? a : (a.before(b) ? a : b));
    }

    public List<VerificationCheckDTO> verifyCredential(CredentialHolderDTO credential, byte[] xmlBytes) {

        VerificationCheckDTO[] credentialReport = new VerificationCheckDTO[6]; //It's an array to preserve the order
        List<Locale> localeEng = new ArrayList<>();
        localeEng.add(new Locale(EDCIConfig.Defaults.DEFAULT_LOCALE));
        List<Locale> locales = credential.getCredential().getAvailableLanguages() != null && !credential.getCredential().getAvailableLanguages().isEmpty() ?
                credential.getCredential().getAvailableLanguages().stream().map(lang -> new Locale(lang)).collect(Collectors.toList()) : localeEng;

        ExecutorService executor = Executors.newFixedThreadPool(walletConfigService.getInteger("verifyCredential.num.threads", 7));

        ContextAwareRunnable formatCheck = new ContextAwareRunnable(() -> {
            Thread.currentThread().setName("verifyCredential.formatCheck." + credential.getId());
            //TODO VP: see EDCI-1092
            //ControlledListConcept.VERIFICATION_CHECKS_FORMAT
            credentialReport[0] = buildFormatVerificationCheck(xmlBytes, locales); //TODO: fix me for credential only
        }, RequestContextHolder.currentRequestAttributes());

        executor.submit(formatCheck);

        ContextAwareRunnable verificationCheck = new ContextAwareRunnable(() -> {
            Thread.currentThread().setName("verifyCredential.verificationCheck." + credential.getId());
            //ControlledListConcept.VERIFICATION_CHECKS_SEAL
            credentialReport[1] = buildSealVerificationCheck(xmlBytes, locales);
        }, RequestContextHolder.currentRequestAttributes());

        executor.submit(verificationCheck);

//        ContextAwareRunnable ownerVerificationCheck = new ContextAwareRunnable(() -> {
//        Thread.currentThread().setName("verifyCredential.ownerVerificationCheck"+credential.getId());
        //ControlledListConcept.VERIFICATION_CHECKS_OWNER
        //credentialReport[2] = buildOwnerVerificationCheckV2(credentialVerifyRequestDTO, europassCredentialDTO.getCredentialSubject());
//        }, RequestContextHolder.currentRequestAttributes());
//
//        executor.submit(ownerVerificationCheck);

        ContextAwareRunnable revocationVerificationCheck = new ContextAwareRunnable(() -> {
            Thread.currentThread().setName("verifyCredential.revocationVerificationCheck." + credential.getId());
            //ControlledListConcept.VERIFICATION_CHECKS_REVOCATION
            credentialReport[3] = buildRevocationVerificationCheck(xmlBytes, locales);
        }, RequestContextHolder.currentRequestAttributes());

        executor.submit(revocationVerificationCheck);

        ContextAwareRunnable accredtiationVerificationCheck = new ContextAwareRunnable(() -> {
            Thread.currentThread().setName("verifyCredential.accredtiationVerificationCheck." + credential.getId());
            //ControlledListConcept.VERIFICATION_CHECKS_ACCREDITATION
            credentialReport[4] = buildAccredtiationVerificationCheck(xmlBytes, locales);
        }, RequestContextHolder.currentRequestAttributes());

        executor.submit(accredtiationVerificationCheck);

        ContextAwareRunnable validityVerificationCheck = new ContextAwareRunnable(() -> {
            Thread.currentThread().setName("verifyCredential.validityVerificationCheck." + credential.getId());
            //ControlledListConcept.VERIFICATION_CHECKS_EXPIRY
            credentialReport[5] = buildValidityVerificationCheck(least(credential.getExpirationDate(), credential.getCredential().getExpirationDate()), locales);
        }, RequestContextHolder.currentRequestAttributes());

        executor.submit(validityVerificationCheck);

        executor.shutdown();
        try {
            executor.awaitTermination(walletConfigService.get("verifyCredential.timeout.minutes.threads", Long.class, 5L), TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            throw new EDCIException(e).addDescription("Timeout while validating the credentials");
        }

        List<VerificationCheckDTO> credentialReportList = Arrays.asList(credentialReport).stream().filter(elem -> elem != null).collect(Collectors.toList());

        //Add custom validators
        if (credential instanceof EuropassPresentationDTO && ((EuropassPresentationDTO) credential).getVerifications() != null) {
            credentialReportList.addAll(((EuropassPresentationDTO) credential).getVerifications().stream()
                    .filter(verif -> ControlledListConcept.VERIFICATION_CHECKS_CUSTOM.getUrl().equals(verif.getType().getUri())).collect(Collectors.toList()));

        }

        return credentialReportList;

    }

    public List<VerificationCheckDTO> verifyCredential(MultipartFile file) throws IOException {
        return verifyCredential(file.getBytes());
    }

    //Validates against XSD, returns null if XSD is not available
    private Boolean validateFormat(byte[] bytes) {
        ValidationResult validationResult = new ValidationResult();
        try (InputStream is = new ByteArrayInputStream(bytes)) {
            SchemaLocation schemaLocation = xmlUtil.getUniqueSchemaLocation(bytes);
            if (schemaLocation != null && schemaLocation.getLocation() != null) {
                validationResult = xmlUtil.isValid(is, schemaLocation, EuropassCredentialDTO.class);
            } else {
                validationResult.setValid(false);
            }
        } catch (Exception e) {
            validationResult.setValid(false);
        }
        return validationResult.isValid();
    }

    private VerificationCheckDTO buildFormatVerificationCheck(byte[] bytes, List<Locale> locales) {

        VerificationCheckDTO verificationCheckDTO = new VerificationCheckDTO();

        Boolean isValid = validateFormat(bytes);
        ControlledListConcept statusCode;

        Text desc = new Text();
        if (isValid == null) {
            locales.forEach(locale -> desc.addContent(messageSource.getMessage(locale, "verify.check.unavailable"), locale.getLanguage()));
            statusCode = ControlledListConcept.VERIFICATION_STATUS_SKIPPED;
        } else {
            if (isValid) {
                locales.forEach(locale -> desc.addContent(messageSource.getMessage(locale, "verify.check.valid"), locale.getLanguage()));
                statusCode = ControlledListConcept.VERIFICATION_STATUS_OK;
            } else {
                locales.forEach(locale -> desc.addContent(messageSource.getMessage(locale, "verify.check.unreadable"), locale.getLanguage()));
                statusCode = ControlledListConcept.VERIFICATION_STATUS_ERROR;
            }
        }

        verificationCheckDTO.setDescription(desc);
        verificationCheckDTO.setStatus(controlledListCommonsService.searchConceptByConcept(statusCode, EDCIConfig.Defaults.DEFAULT_LOCALE));
        verificationCheckDTO.setType(controlledListCommonsService.searchConceptByConcept(ControlledListConcept.VERIFICATION_CHECKS_FORMAT));

        return verificationCheckDTO;
    }

   /* public VerificationCheckDTO buildAccreditationVerificationCheck(EuropassCredentialDTO europassCredentialDTO) {

        VerificationCheckDTO verificationCheckDTO = new VerificationCheckDTO();

        QMSAccreditations qmsAccreditations = null;
        Resource resource = null;
        JAXBContext jaxbContext = null;
        Text desc = null;

        try {
            boolean institutionalAccreditationExists = Math.random() < 0.7;
            if (institutionalAccreditationExists) {
                resource = new ClassPathResource(EDCIWalletConstants.INSTITUTIONAL_ACCREDITATION.concat(EDCIConstants.XML.EXTENSION_XML));
                jaxbContext = JAXBContext.newInstance(QMSAccreditations.class);
            } else {
                resource = new ClassPathResource(EDCIWalletConstants.QUALIFICATION_ACCREDITATION.concat(EDCIConstants.XML.EXTENSION_XML));
                jaxbContext = JAXBContext.newInstance(QMSAccreditations.class);
            }
        } catch (JAXBException e) {
            logger.error("Error unMarshalling qmsaccreditation response", e);
        }

        try {
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            qmsAccreditations = (QMSAccreditations) unmarshaller.unmarshal(resource.getInputStream());
        } catch (JAXBException e) {
            logger.error("Error unMarshalling qmsaccreditation response", e);
        } catch (IOException ioe) {
            logger.error("Error unMarshalling qmsaccreditation response", ioe);
        }

        try {
            logger.trace(new ObjectMapper().writeValueAsString(qmsAccreditations));
        } catch (JsonProcessingException e) {
            logger.error(e);
        }

        logger.trace("Accreditations length: " + qmsAccreditations.getAccreditation().size());
        for (QMSAccreditation qmsAccreditation : qmsAccreditations.getAccreditation()) {
            if (europassCredentialDTO.getIssuanceDate().after(qmsAccreditation.getIssuedDate()) && europassCredentialDTO.getIssuanceDate().before(qmsAccreditation.getExpiryDate())) {
                verificationCheckDTO.setStatus(controlledListCommonsService.searchConceptByConcept(ControlledListConcept.VERIFICATION_STATUS_OK, EDCIConfig.Defaults.DEFAULT_LOCALE));
                desc = new Text(messageSource.getMessage("verify.check.still.valid"), LocaleContextHolder.getLocale().getLanguage());
            } else {
                verificationCheckDTO.setStatus(controlledListCommonsService.searchConceptByConcept(ControlledListConcept.VERIFICATION_STATUS_ERROR, EDCIConfig.Defaults.DEFAULT_LOCALE));
                //TODO: What if cred.issueDate before accr.issueDate?
                desc = new Text(messageSource.getMessage("verify.check.expired"), LocaleContextHolder.getLocale().getLanguage());
            }
        }

        List<LearningAchievementDTO> learningAchievementDTOS = europassCredentialDTO.getCredentialSubject().getAchieved();

        try {
            logger.trace("Filtered qualification awards (deprecated) from achievements");
            logger.trace(new ObjectMapper().writeValueAsString(learningAchievementDTOS));
        } catch (JsonProcessingException e) {
            logger.error(e);
        }

        verificationCheckDTO.setType(controlledListCommonsService.searchConceptByConcept(ControlledListConcept.VERIFICATION_CHECKS_ACCREDITATION));
        verificationCheckDTO.setDescription(desc);
        return verificationCheckDTO;
    }*/

    private VerificationCheckDTO buildAccredtiationVerificationCheck(byte[] xmlBytes, List<Locale> locales) {
        VerificationCheckDTO verificationCheckDTO = new VerificationCheckDTO();
        verificationCheckDTO.setStatus(controlledListCommonsService.searchConceptByConcept(ControlledListConcept.VERIFICATION_STATUS_SKIPPED, EDCIConfig.Defaults.DEFAULT_LOCALE));
        verificationCheckDTO.setType(controlledListCommonsService.searchConceptByConcept(ControlledListConcept.VERIFICATION_CHECKS_ACCREDITATION));

        Text desc = new Text();
        locales.forEach(locale -> desc.addContent(messageSource.getMessage(locale, "verify.check.verification.skipped"), locale.getLanguage()));
        verificationCheckDTO.setDescription(desc);

        try {
            ValidationResult validationResult = this.getQmsAccreditationsService().isCoveredCredential(xmlBytes);
            if (validationResult != null && validationResult.isValid()) {
                Text okDesc = new Text();
                locales.forEach(locale -> okDesc.addContent(messageSource.getMessage(locale, "verify.check.accredited"), locale.getLanguage()));
                verificationCheckDTO.setStatus(controlledListCommonsService.searchConceptByConcept(ControlledListConcept.VERIFICATION_STATUS_OK));
                verificationCheckDTO.setDescription(okDesc);
            } else if (validationResult != null && !validationResult.isValid()) {
                Text koDesc = new Text();
                locales.forEach(locale -> koDesc.addContent(messageSource.getMessage(locale, "verify.check.not.accredited"), locale.getLanguage()));
                verificationCheckDTO.setStatus(controlledListCommonsService.searchConceptByConcept(ControlledListConcept.VERIFICATION_STATUS_ERROR));
                verificationCheckDTO.setDescription(koDesc);
            }


        } catch (JAXBException | IOException e) {
            logger.error("error while building accreditation check");
        }


        return verificationCheckDTO;
    }

    private VerificationCheckDTO buildRevocationVerificationCheck(byte[] xmlBytes, List<Locale> locales) {
        VerificationCheckDTO verificationCheckDTO = new VerificationCheckDTO();
        verificationCheckDTO.setStatus(controlledListCommonsService.searchConceptByConcept(ControlledListConcept.VERIFICATION_STATUS_SKIPPED, EDCIConfig.Defaults.DEFAULT_LOCALE));
        verificationCheckDTO.setType(controlledListCommonsService.searchConceptByConcept(ControlledListConcept.VERIFICATION_CHECKS_REVOCATION));

        Text desc = new Text();
        locales.forEach(locale -> desc.addContent(messageSource.getMessage(locale, "verify.check.verification.skipped"), locale.getLanguage()));
        verificationCheckDTO.setDescription(desc);

        return verificationCheckDTO;
    }

    public boolean containsAnySignature(byte[] xmlBytes) {
        DSSEDCIValidationService dssValidationUtils = new DSSEDCIValidationService();
        Reports reports = dssValidationUtils.validateXML(xmlBytes, false);
        return reports.getSimpleReport().getSignaturesCount() > 0;
    }

    private VerificationCheckDTO buildSealVerificationCheck(byte[] xmlBytes, List<Locale> locales) {
        VerificationCheckDTO verificationCheckDTO = new VerificationCheckDTO();
        Text desc = new Text();
        Text descLong = new Text();
        Reports reports = this.getDssedciValidationService().validateXML(xmlBytes, false);

        if (logger.isDebugEnabled()) {
            logger.debug("Valid Signature Count: ", () -> reports.getSimpleReport().getValidSignaturesCount());
            logger.debug("Errors: ", () -> reports.getSimpleReport().getErrors(reports.getSimpleReport().getFirstSignatureId()));
        }
        if (reports == null) {
            locales.forEach(locale -> desc.addContent(messageSource.getMessage(locale, "verify.check.seal.broken"), locale.getLanguage()));
            verificationCheckDTO.setStatus(controlledListCommonsService.searchConceptByConcept(ControlledListConcept.VERIFICATION_STATUS_ERROR, EDCIConfig.Defaults.DEFAULT_LOCALE));
        } else if (reports.getSimpleReport().getSignaturesCount() < 1) {
            locales.forEach(locale -> desc.addContent(messageSource.getMessage(locale, "verify.check.not.sealed"), locale.getLanguage()));
            verificationCheckDTO.setStatus(controlledListCommonsService.searchConceptByConcept(ControlledListConcept.VERIFICATION_STATUS_SKIPPED, EDCIConfig.Defaults.DEFAULT_LOCALE));
        } else if (reports.getSimpleReport().getValidSignaturesCount() > 0) {
            //Get short issuer, initialize both values to it
            String shortIssuer;
            shortIssuer = reports.getSimpleReport().getSignedBy(reports.getSimpleReport().getFirstSignatureId());
            StringBuilder longIssuerSB = new StringBuilder().append(shortIssuer);
            //loop through report used certificates
            List<CertificateWrapper> usedCertificates = reports.getDiagnosticData().getUsedCertificates();
            for (int i = 0; i < usedCertificates.size(); i++) {
                if (usedCertificates.get(i) != null && usedCertificates.get(i).getCommonName() != null) {
                    longIssuerSB.append(usedCertificates.get(i).getCommonName().concat(EDCIConstants.StringPool.STRING_SPACE).concat(EDCIConstants.StringPool.STRING_SEMICOLON));
                }
            }

            longIssuerSB.append(EDCIConstants.StringPool.STRING_COMMA);
            Date issueDate = reports.getSimpleReport().getSigningTime(reports.getSimpleReport().getFirstSignatureId());
            locales.forEach(locale -> descLong.addContent(messageSource.getMessage(locale, "verify.check.sealed", longIssuerSB.toString(), issueDate), locale.getLanguage()));
            locales.forEach(locale -> desc.addContent(messageSource.getMessage(locale, "verify.check.sealed.short", shortIssuer), locale.getLanguage()));
            verificationCheckDTO.setStatus(controlledListCommonsService.searchConceptByConcept(ControlledListConcept.VERIFICATION_STATUS_OK, EDCIConfig.Defaults.DEFAULT_LOCALE));
        } else {
            StringBuilder errors = new StringBuilder();
            for (String error : reports.getSimpleReport().getErrors(reports.getSimpleReport().getFirstSignatureId())) {
                errors.append(error).append(";");
            }
            locales.forEach(locale -> desc.addContent(messageSource.getMessage(locale, "verify.check.seal.broken"), locale.getLanguage()));
            logger.error("verify.check.seal.broken ", () -> errors.toString());
            verificationCheckDTO.setStatus(controlledListCommonsService.searchConceptByConcept(ControlledListConcept.VERIFICATION_STATUS_ERROR, EDCIConfig.Defaults.DEFAULT_LOCALE));
        }

        verificationCheckDTO.setType(controlledListCommonsService.searchConceptByConcept(ControlledListConcept.VERIFICATION_CHECKS_SEAL));

        verificationCheckDTO.setDescription(desc);
        if (descLong != null && descLong.getContents() != null && !descLong.getContents().isEmpty()) {
            verificationCheckDTO.setLongDescription(descLong);
        }

        return verificationCheckDTO;
    }

//    private VerificationCheckDTO buildOwnerVerificationCheckV2(CredentialVerifyRequestDTO credentialVerifyRequestDTO, eu.europa.ec.empl.edci.datamodel.model.PersonDTO credentialSubject) {
//
//        VerificationCheckDTO verificationCheckDTO = new VerificationCheckDTO();
//        Text desc = null;
//
//        if (StringUtils.isBlank(credentialVerifyRequestDTO.getFirstName()) || StringUtils.isBlank(credentialVerifyRequestDTO.getLastName())) {
//            desc = new Text(messageSource.getMessage("verify.check.missing.property", "FirstName and LastName"), LocaleContextHolder.getLocale().getLanguage());
//            verificationCheckDTO.setStatus(controlledListCommonsService.searchConceptByConcept(ControlledListConcept.VERIFICATION_STATUS_SKIPPED));
//        } else {
//            if (validateOwner(credentialSubject, credentialVerifyRequestDTO)) {
//                desc = new Text(messageSource.getMessage("verify.check.issued.wallet.owner"), LocaleContextHolder.getLocale().getLanguage());
//                verificationCheckDTO.setStatus(controlledListCommonsService.searchConceptByConcept(ControlledListConcept.VERIFICATION_STATUS_OK));
//            } else {
//
//                String credentialFullName = credentialSubject.getFullName().getStringContent(LocaleContextHolder.getLocale().getLanguage());
//                String requestFullName = credentialVerifyRequestDTO.getFirstName().concat(credentialVerifyRequestDTO.getLastName());
//
//                desc = new Text(messageSource.getMessage("verify.check.issued.wallet.holder", credentialFullName, requestFullName), LocaleContextHolder.getLocale().getLanguage());
//                verificationCheckDTO.setStatus(controlledListCommonsService.searchConceptByConcept(ControlledListConcept.VERIFICATION_STATUS_ERROR));
//            }
//        }
//        verificationCheckDTO.setType(controlledListCommonsService.searchConceptByConcept(ControlledListConcept.VERIFICATION_CHECKS_OWNER));
//
//        verificationCheckDTO.setDescription(desc);
//
//        return verificationCheckDTO;
//    }

//    private boolean validateOwner(PersonDTO credentialSubject, CredentialVerifyRequestDTO credentialVerifyRequestDTO) {
//
//        String requestFullName = credentialVerifyRequestDTO.getFirstName().concat(credentialVerifyRequestDTO.getLastName());
//        String credentialFullName = credentialSubject.getFullName().getStringContent(LocaleContextHolder.getLocale().getLanguage());
//
//        if (credentialFullName != null) {
//            credentialFullName = credentialFullName.replaceAll("\\s", "");
//        }
//        if (logger.isDebugEnabled()) {
//            logger.trace(String.format("requestFullName: %s, credentialFullName: %s", requestFullName, credentialFullName));
//        }
//        if (requestFullName.equalsIgnoreCase(credentialFullName)) return true;
//        return false;
//    }


    private VerificationCheckDTO buildValidityVerificationCheck(Date expirationDate, List<Locale> locales) {

        VerificationCheckDTO verificationCheckDTO = new VerificationCheckDTO();
        Text desc = new Text();

        verificationCheckDTO.setType(controlledListCommonsService.searchConceptByConcept(ControlledListConcept.VERIFICATION_CHECKS_EXPIRY));
        if (expirationDate != null) {
            if (credentialUtil.validateExpiry(expirationDate)) {
                locales.forEach(locale -> desc.addContent(messageSource.getMessage(locale, "verify.check.still.valid"), locale.getLanguage()));
                verificationCheckDTO.setStatus(controlledListCommonsService.searchConceptByConcept(ControlledListConcept.VERIFICATION_STATUS_OK, EDCIConfig.Defaults.DEFAULT_LOCALE));
            } else {
                locales.forEach(locale -> desc.addContent(messageSource.getMessage(locale, "verify.check.expired", expirationDate), locale.getLanguage()));
                verificationCheckDTO.setStatus(controlledListCommonsService.searchConceptByConcept(ControlledListConcept.VERIFICATION_STATUS_ERROR, EDCIConfig.Defaults.DEFAULT_LOCALE));
            }
        } else {
            locales.forEach(locale -> desc.addContent(messageSource.getMessage(locale, "verify.check.still.valid"), locale.getLanguage()));
            verificationCheckDTO.setStatus(controlledListCommonsService.searchConceptByConcept(ControlledListConcept.VERIFICATION_STATUS_OK, EDCIConfig.Defaults.DEFAULT_LOCALE));

        }

        verificationCheckDTO.setType(controlledListCommonsService.searchConceptByConcept(ControlledListConcept.VERIFICATION_CHECKS_EXPIRY));
        verificationCheckDTO.setDescription(desc);

        return verificationCheckDTO;
    }


    public DSSEDCIValidationService getDssedciValidationService() {
        return dssedciValidationService;
    }

    public void setDssedciValidationService(DSSEDCIValidationService dssedciValidationService) {
        this.dssedciValidationService = dssedciValidationService;
    }

    public QMSAccreditationsService getQmsAccreditationsService() {
        return qmsAccreditationsService;
    }

    public void setQmsAccreditationsService(QMSAccreditationsService qmsAccreditationsService) {
        this.qmsAccreditationsService = qmsAccreditationsService;
    }
}
