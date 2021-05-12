package eu.europa.ec.empl.edci.wallet.service.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import eu.europa.ec.empl.edci.constants.ControlledListConcept;
import eu.europa.ec.empl.edci.constants.Defaults;
import eu.europa.ec.empl.edci.constants.ErrorCode;
import eu.europa.ec.empl.edci.constants.XML;
import eu.europa.ec.empl.edci.datamodel.model.EuropassCredentialDTO;
import eu.europa.ec.empl.edci.datamodel.model.LearningAchievementDTO;
import eu.europa.ec.empl.edci.datamodel.model.base.CredentialHolderDTO;
import eu.europa.ec.empl.edci.datamodel.model.dataTypes.SchemaLocation;
import eu.europa.ec.empl.edci.datamodel.model.dataTypes.Text;
import eu.europa.ec.empl.edci.datamodel.model.verifiable.presentation.EuropassPresentationDTO;
import eu.europa.ec.empl.edci.datamodel.model.verifiable.presentation.VerificationCheckDTO;
import eu.europa.ec.empl.edci.datamodel.validation.ValidationResult;
import eu.europa.ec.empl.edci.dss.validation.DSSValidationUtils;
import eu.europa.ec.empl.edci.exception.EDCIException;
import eu.europa.ec.empl.edci.service.ControlledListCommonsService;
import eu.europa.ec.empl.edci.service.EDCIMessageService;
import eu.europa.ec.empl.edci.util.ContextAwareRunnable;
import eu.europa.ec.empl.edci.util.EDCICredentialModelUtil;
import eu.europa.ec.empl.edci.util.XmlUtil;
import eu.europa.ec.empl.edci.wallet.common.constants.EDCIWalletConstants;
import eu.europa.ec.empl.edci.wallet.common.model.verification.QMSAccreditation;
import eu.europa.ec.empl.edci.wallet.common.model.verification.QMSAccreditations;
import eu.europa.ec.empl.edci.wallet.service.WalletConfigService;
import eu.europa.esig.dss.diagnostic.CertificateWrapper;
import eu.europa.esig.dss.validation.CertificateVerifier;
import eu.europa.esig.dss.validation.reports.Reports;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.multipart.MultipartFile;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
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
    private static final Logger logger = Logger.getLogger(EuropassCredentialVerifyUtil.class);

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
    EDCIMessageService messageSource;

    @Autowired
    private CredentialUtil credentialUtil;

    @Autowired
    private XmlUtil xmlUtil;

    @Autowired
    private CertificateVerifier certificateVerifier;

    @Autowired
    private WalletConfigService walletConfigService;

    @Autowired
    private ControlledListCommonsService controlledListCommonsService;

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

        ExecutorService executor = Executors.newFixedThreadPool(walletConfigService.getInteger("verifyCredential.num.threads", 7));

        ContextAwareRunnable formatCheck = new ContextAwareRunnable(() -> {
            Thread.currentThread().setName("verifyCredential.formatCheck."+credential.getId());
            //TODO VP: see EDCI-1092
            //ControlledListConcept.VERIFICATION_CHECKS_FORMAT
            credentialReport[0] = buildFormatVerificationCheck(xmlBytes); //TODO: fix me for credential only
        }, RequestContextHolder.currentRequestAttributes());

        executor.submit(formatCheck);

        ContextAwareRunnable verificationCheck = new ContextAwareRunnable(() -> {
            Thread.currentThread().setName("verifyCredential.verificationCheck."+credential.getId());
            //ControlledListConcept.VERIFICATION_CHECKS_SEAL
            credentialReport[1] = buildSealVerificationCheck(xmlBytes);
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
            Thread.currentThread().setName("verifyCredential.revocationVerificationCheck."+credential.getId());
            //ControlledListConcept.VERIFICATION_CHECKS_REVOCATION
            credentialReport[3] = buildRevocationVerificationCheck(xmlBytes);
        }, RequestContextHolder.currentRequestAttributes());

        executor.submit(revocationVerificationCheck);

        ContextAwareRunnable accredtiationVerificationCheck = new ContextAwareRunnable(() -> {
            Thread.currentThread().setName("verifyCredential.accredtiationVerificationCheck."+credential.getId());
            //ControlledListConcept.VERIFICATION_CHECKS_ACCREDITATION
            credentialReport[4] = buildAccredtiationVerificationCheck(xmlBytes);
        }, RequestContextHolder.currentRequestAttributes());

        executor.submit(accredtiationVerificationCheck);

        ContextAwareRunnable validityVerificationCheck = new ContextAwareRunnable(() -> {
            Thread.currentThread().setName("verifyCredential.validityVerificationCheck."+credential.getId());
            //ControlledListConcept.VERIFICATION_CHECKS_EXPIRY
            credentialReport[5] = buildValidityVerificationCheck(least(credential.getExpirationDate(), credential.getCredential().getExpirationDate()));
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

    private VerificationCheckDTO buildFormatVerificationCheck(byte[] bytes) {

        VerificationCheckDTO verificationCheckDTO = new VerificationCheckDTO();

        Boolean isValid = validateFormat(bytes);
        ControlledListConcept statusCode;

        Text desc = null;
        if (isValid == null) {
            desc = new Text(messageSource.getMessage("verify.check.unavailable"), LocaleContextHolder.getLocale().getLanguage());
            statusCode = ControlledListConcept.VERIFICATION_STATUS_SKIPPED;
        } else {
            if (isValid) {
                desc = new Text(messageSource.getMessage("verify.check.valid"), LocaleContextHolder.getLocale().getLanguage());
                statusCode = ControlledListConcept.VERIFICATION_STATUS_OK;
            } else {
                desc = new Text(messageSource.getMessage("verify.check.unreadable"), LocaleContextHolder.getLocale().getLanguage());
                statusCode = ControlledListConcept.VERIFICATION_STATUS_ERROR;
            }
        }

        verificationCheckDTO.setDescription(desc);
        verificationCheckDTO.setStatus(controlledListCommonsService.searchConceptByConcept(statusCode, Defaults.DEFAULT_LOCALE));
        verificationCheckDTO.setType(controlledListCommonsService.searchConceptByConcept(ControlledListConcept.VERIFICATION_CHECKS_FORMAT));

        return verificationCheckDTO;
    }

    public VerificationCheckDTO buildAccreditationVerificationCheck(EuropassCredentialDTO europassCredentialDTO) {

        VerificationCheckDTO verificationCheckDTO = new VerificationCheckDTO();

        QMSAccreditations qmsAccreditations = null;
        Resource resource = null;
        JAXBContext jaxbContext = null;
        Text desc = null;

        try {
            boolean institutionalAccreditationExists = Math.random() < 0.7;
            if (institutionalAccreditationExists) {
                resource = new ClassPathResource(EDCIWalletConstants.INSTITUTIONAL_ACCREDITATION.concat(XML.EXTENSION_XML));
                jaxbContext = JAXBContext.newInstance(QMSAccreditations.class);
            } else {
                resource = new ClassPathResource(EDCIWalletConstants.QUALIFICATION_ACCREDITATION.concat(XML.EXTENSION_XML));
                jaxbContext = JAXBContext.newInstance(QMSAccreditations.class);
            }
        } catch (JAXBException e) {
            logger.error("Error unMarshalling accreditation response", e);
        }

        try {
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            qmsAccreditations = (QMSAccreditations) unmarshaller.unmarshal(resource.getInputStream());
        } catch (JAXBException e) {
            logger.error("Error unMarshalling accreditation response", e);
        } catch (IOException ioe) {
            logger.error("Error unMarshalling accreditation response", ioe);
        }

        try {
            logger.trace(new ObjectMapper().writeValueAsString(qmsAccreditations));
        } catch (JsonProcessingException e) {
            logger.error(e);
        }

        logger.trace("Accreditations length: " + qmsAccreditations.getAccreditation().size());
        for (QMSAccreditation qmsAccreditation : qmsAccreditations.getAccreditation()) {
            if (europassCredentialDTO.getIssuanceDate().after(qmsAccreditation.getIssuedDate()) && europassCredentialDTO.getIssuanceDate().before(qmsAccreditation.getExpiryDate())) {
                verificationCheckDTO.setStatus(controlledListCommonsService.searchConceptByConcept(ControlledListConcept.VERIFICATION_STATUS_OK, Defaults.DEFAULT_LOCALE));
                desc = new Text(messageSource.getMessage("verify.check.still.valid"), LocaleContextHolder.getLocale().getLanguage());
            } else {
                verificationCheckDTO.setStatus(controlledListCommonsService.searchConceptByConcept(ControlledListConcept.VERIFICATION_STATUS_ERROR, Defaults.DEFAULT_LOCALE));
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
    }

    private VerificationCheckDTO buildAccredtiationVerificationCheck(byte[] xmlBytes) {
        VerificationCheckDTO verificationCheckDTO = new VerificationCheckDTO();
        verificationCheckDTO.setStatus(controlledListCommonsService.searchConceptByConcept(ControlledListConcept.VERIFICATION_STATUS_SKIPPED, Defaults.DEFAULT_LOCALE));
        verificationCheckDTO.setType(controlledListCommonsService.searchConceptByConcept(ControlledListConcept.VERIFICATION_CHECKS_ACCREDITATION));

        verificationCheckDTO.setDescription(new Text(messageSource.getMessage("verify.check.verification.skipped"), LocaleContextHolder.getLocale().getLanguage()));

        return verificationCheckDTO;
    }

    private VerificationCheckDTO buildRevocationVerificationCheck(byte[] xmlBytes) {
        VerificationCheckDTO verificationCheckDTO = new VerificationCheckDTO();
        verificationCheckDTO.setStatus(controlledListCommonsService.searchConceptByConcept(ControlledListConcept.VERIFICATION_STATUS_SKIPPED, Defaults.DEFAULT_LOCALE));
        verificationCheckDTO.setType(controlledListCommonsService.searchConceptByConcept(ControlledListConcept.VERIFICATION_CHECKS_REVOCATION));

        verificationCheckDTO.setDescription(new Text(messageSource.getMessage("verify.check.verification.skipped"), LocaleContextHolder.getLocale().getLanguage()));

        return verificationCheckDTO;
    }

    public boolean containsAnySignature(byte[] xmlBytes) {
        DSSValidationUtils dssValidationUtils = new DSSValidationUtils();
        Reports reports = dssValidationUtils.validateXML(xmlBytes, certificateVerifier);
        return reports.getSimpleReport().getSignaturesCount() > 0;
    }

    private VerificationCheckDTO buildSealVerificationCheck(byte[] xmlBytes) {
        VerificationCheckDTO verificationCheckDTO = new VerificationCheckDTO();
        Text desc = null;
        Text descLong = null;
        DSSValidationUtils dssValidationUtils = new DSSValidationUtils();
        Reports reports = dssValidationUtils.validateXML(xmlBytes, certificateVerifier);

        if (reports == null) {
            desc = new Text(messageSource.getMessage("verify.check.seal.broken"), LocaleContextHolder.getLocale().getLanguage());
            verificationCheckDTO.setStatus(controlledListCommonsService.searchConceptByConcept(ControlledListConcept.VERIFICATION_STATUS_ERROR, Defaults.DEFAULT_LOCALE));
        } else if (reports.getSimpleReport().getSignaturesCount() < 1) {
            desc = new Text(messageSource.getMessage("verify.check.not.sealed"), LocaleContextHolder.getLocale().getLanguage());
            verificationCheckDTO.setStatus(controlledListCommonsService.searchConceptByConcept(ControlledListConcept.VERIFICATION_STATUS_SKIPPED, Defaults.DEFAULT_LOCALE));
        } else if (reports.getSimpleReport().getValidSignaturesCount() > 0) {
            String issuer = EDCIWalletConstants.STRING_BLANK;
            List<CertificateWrapper> usedCertificates = reports.getDiagnosticData().getUsedCertificates();
            //Get the first cert that matches the signer, it's common name is the sort Issuer description
            Optional<CertificateWrapper> firstCert = usedCertificates.stream().filter(item -> item.getId().equals(reports.getSimpleReport().getSignedBy(reports.getSimpleReport().getFirstSignatureId()))).findFirst();
            String shortIssuer = EDCIWalletConstants.STRING_BLANK;
            if (firstCert.isPresent()) {
                shortIssuer = firstCert.get().getCommonName();
            }
            for (CertificateWrapper usedCertificate : usedCertificates) {
                if (usedCertificate.getId().equals(reports.getSimpleReport().getSignedBy(reports.getSimpleReport().getFirstSignatureId()))) {
                    issuer = usedCertificate.getCommonName();
                    for (CertificateWrapper cw : usedCertificate.getCertificateChain()) {
                        issuer = issuer.concat(";").concat(cw.getCommonName());
                    }
                    issuer = issuer.concat(",");
                }
            }
            Date issueDate = reports.getSimpleReport().getSigningTime(reports.getSimpleReport().getFirstSignatureId());
            descLong = new Text(messageSource.getMessage("verify.check.sealed", issuer, issueDate), LocaleContextHolder.getLocale().getLanguage());
            desc = new Text(messageSource.getMessage("verify.check.sealed.short", shortIssuer), LocaleContextHolder.getLocale().getLanguage());
            verificationCheckDTO.setStatus(controlledListCommonsService.searchConceptByConcept(ControlledListConcept.VERIFICATION_STATUS_OK, Defaults.DEFAULT_LOCALE));
        } else {
            StringBuilder errors = new StringBuilder();
            for (String error : reports.getSimpleReport().getErrors(reports.getSimpleReport().getFirstSignatureId())) {
                errors.append(error).append(";");
            }
            desc = new Text(messageSource.getMessage("verify.check.seal.broken"), LocaleContextHolder.getLocale().getLanguage());
            logger.error("verify.check.seal.broken " + errors.toString());
            verificationCheckDTO.setStatus(controlledListCommonsService.searchConceptByConcept(ControlledListConcept.VERIFICATION_STATUS_ERROR, Defaults.DEFAULT_LOCALE));
        }

        verificationCheckDTO.setType(controlledListCommonsService.searchConceptByConcept(ControlledListConcept.VERIFICATION_CHECKS_SEAL));

        verificationCheckDTO.setDescription(desc);
        verificationCheckDTO.setLongDescription(descLong);

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


    private VerificationCheckDTO buildValidityVerificationCheck(Date expirationDate) {

        VerificationCheckDTO verificationCheckDTO = new VerificationCheckDTO();
        Text desc = null;

        verificationCheckDTO.setType(controlledListCommonsService.searchConceptByConcept(ControlledListConcept.VERIFICATION_CHECKS_EXPIRY));
        if (expirationDate != null) {
            if (credentialUtil.validateExpiry(expirationDate)) {
                desc = new Text(messageSource.getMessage("verify.check.still.valid"), LocaleContextHolder.getLocale().getLanguage());
                verificationCheckDTO.setStatus(controlledListCommonsService.searchConceptByConcept(ControlledListConcept.VERIFICATION_STATUS_OK, Defaults.DEFAULT_LOCALE));
            } else {
                desc = new Text(messageSource.getMessage("verify.check.expired", expirationDate), LocaleContextHolder.getLocale().getLanguage());
                verificationCheckDTO.setStatus(controlledListCommonsService.searchConceptByConcept(ControlledListConcept.VERIFICATION_STATUS_ERROR, Defaults.DEFAULT_LOCALE));
            }
        } else {
            desc = new Text(messageSource.getMessage("verify.check.still.valid"), LocaleContextHolder.getLocale().getLanguage());
            verificationCheckDTO.setStatus(controlledListCommonsService.searchConceptByConcept(ControlledListConcept.VERIFICATION_STATUS_OK, Defaults.DEFAULT_LOCALE));

        }

        verificationCheckDTO.setType(controlledListCommonsService.searchConceptByConcept(ControlledListConcept.VERIFICATION_CHECKS_EXPIRY));
        verificationCheckDTO.setDescription(desc);

        return verificationCheckDTO;
    }


}
