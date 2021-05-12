package eu.europa.ec.empl.edci.issuer.service;

import eu.europa.ec.empl.edci.constants.ControlledList;
import eu.europa.ec.empl.edci.constants.ErrorCode;
import eu.europa.ec.empl.edci.constants.EuropassConstants;
import eu.europa.ec.empl.edci.constants.MessageKeys;
import eu.europa.ec.empl.edci.datamodel.model.AddressDTO;
import eu.europa.ec.empl.edci.datamodel.model.LocationDTO;
import eu.europa.ec.empl.edci.datamodel.model.OrganizationDTO;
import eu.europa.ec.empl.edci.datamodel.model.base.CredentialHolderDTO;
import eu.europa.ec.empl.edci.datamodel.model.dataTypes.Code;
import eu.europa.ec.empl.edci.datamodel.model.dataTypes.LegalIdentifier;
import eu.europa.ec.empl.edci.datamodel.model.dataTypes.Text;
import eu.europa.ec.empl.edci.datamodel.model.verifiable.presentation.EuropassPresentationDTO;
import eu.europa.ec.empl.edci.datamodel.validation.AfterSealing;
import eu.europa.ec.empl.edci.datamodel.validation.ValidationError;
import eu.europa.ec.empl.edci.datamodel.validation.ValidationResult;
import eu.europa.ec.empl.edci.exception.EDCIException;
import eu.europa.ec.empl.edci.issuer.common.constants.EDCIIssuerConstants;
import eu.europa.ec.empl.edci.issuer.mapper.ControlledListsMapper;
import eu.europa.ec.empl.edci.issuer.service.spec.ControlledListsOldService;
import eu.europa.ec.empl.edci.service.ControlledListCommonsService;
import eu.europa.ec.empl.edci.service.EDCIMessageService;
import eu.europa.ec.empl.edci.service.EDCIValidationService;
import eu.europa.ec.empl.edci.util.EDCICredentialModelUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.security.auth.x500.X500Principal;
import javax.validation.groups.Default;
import javax.xml.bind.JAXBException;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class CertificateService {

    private static final Logger logger = Logger.getLogger(CertificateService.class);

    @Autowired
    private EDCIMessageService edciMessageService;

    @Autowired
    private EDCICredentialModelUtil edciCredentialModelUtil;

    @Autowired
    private ControlledListsOldService controlledListsService;

    @Autowired
    private EDCIValidationService edciValidationService;

    @Autowired
    private ControlledListCommonsService controlledListCommonsService;

    @Autowired
    private ControlledListsMapper controlledListsMapper;

    @Autowired
    private FileService fileService;

    @Autowired
    private IssuerConfigService issuerConfigService;

    public String getCertificateReplaceMsg(Map<String, String> certificateInfo) {

        boolean noFieldReplaced = true;

        String msgLabel = edciMessageService.getMessage("issuer.eSeal.certificate.msg").concat("\n\n");

        String organizationProperty = "";
        if (certificateInfo.get(EDCIIssuerConstants.CERTIFICATE_ATTRIBUTE_ORGANIZATION) != null) {
            organizationProperty = certificateInfo.get(EDCIIssuerConstants.CERTIFICATE_ATTRIBUTE_ORGANIZATION);
            noFieldReplaced = false;
        }

        String organizationIdProperty = "";
        if (certificateInfo.get(EDCIIssuerConstants.CERTIFICATE_ATTRIBUTE_ORGANIZATION_IDENTIFIER) != null) {
            organizationIdProperty = "(" + certificateInfo.get(EDCIIssuerConstants.CERTIFICATE_ATTRIBUTE_ORGANIZATION_IDENTIFIER) + ")";
            noFieldReplaced = false;
        }

        String commonNameProperty = "";
        if (certificateInfo.get(EDCIIssuerConstants.CERTIFICATE_ATTRIBUTE_COMMON_NAME) != null) {
            commonNameProperty = (!noFieldReplaced ? " - " : "") + certificateInfo.get(EDCIIssuerConstants.CERTIFICATE_ATTRIBUTE_COMMON_NAME);
            noFieldReplaced = false;
        }

        if (!noFieldReplaced) {
            msgLabel += edciMessageService.getMessage("issuer.eSeal.certificate." + EDCIIssuerConstants.CERTIFICATE_ATTRIBUTE_ORGANIZATION,
                    organizationProperty, organizationIdProperty, commonNameProperty).concat("\n");
        }

        if (certificateInfo.get(EDCIIssuerConstants.CERTIFICATE_ATTRIBUTE_COUNTRY_NAME) != null) {
            String countryLabel = edciMessageService.getMessage("issuer.eSeal.certificate." + EDCIIssuerConstants.CERTIFICATE_ATTRIBUTE_COUNTRY_NAME,
                    certificateInfo.get(EDCIIssuerConstants.CERTIFICATE_ATTRIBUTE_COUNTRY_NAME));
            msgLabel = msgLabel.concat(countryLabel).concat("\n");

        }

        String timestampLabel = edciMessageService.getMessage("issuer.eSeal.certificate.timestamp", new SimpleDateFormat(EuropassConstants.DATE_ISO_8601).format(new Date()));

        return msgLabel.concat(timestampLabel);

    }

    public Map<String, String> getCertificateInfo(String certificate) {

        Map<String, String> issuerAttributes = new HashMap<>();

        try {
            X509Certificate cert = (X509Certificate) CertificateFactory.getInstance("X.509").generateCertificate(new ByteArrayInputStream(certificate.getBytes("utf-8")));
            X500Principal subject = cert.getSubjectX500Principal();

            Map<String, String> oidMap = new HashMap<>();
            oidMap.put("2.5.4.97", EDCIIssuerConstants.CERTIFICATE_ATTRIBUTE_ORGANIZATION_IDENTIFIER);
            String issuerName = subject.getName(X500Principal.RFC2253, oidMap);

            List<String> values = Arrays.asList(issuerName.split(EDCIIssuerConstants.STRING_COMMA));
            for (String value : values) {
                String[] attributeNameAndValue = value.split(EDCIIssuerConstants.STRING_EQUALS);
                issuerAttributes.put(attributeNameAndValue[0], attributeNameAndValue[1]);
            }

        } catch (CertificateException | UnsupportedEncodingException e) {
            logger.error(e.getMessage(), e);
        }

        return issuerAttributes;
    }

    /*
     * Adds information from used credential to the XML
     * Adds issued date to the XML
     * */
    public List<String> overwriteCertificateFields(String filePath, Map<String, String> credentialIssuerInfo) {

        List<String> errorFields = new ArrayList<>();

        try {

            String countryName = credentialIssuerInfo.get(EDCIIssuerConstants.CERTIFICATE_ATTRIBUTE_COUNTRY_NAME);
            String organization = credentialIssuerInfo.get(EDCIIssuerConstants.CERTIFICATE_ATTRIBUTE_ORGANIZATION);
            String organizationId = credentialIssuerInfo.get(EDCIIssuerConstants.CERTIFICATE_ATTRIBUTE_ORGANIZATION_IDENTIFIER);
            String commonName = credentialIssuerInfo.get(EDCIIssuerConstants.CERTIFICATE_ATTRIBUTE_COMMON_NAME);

            CredentialHolderDTO euroPassCredential = null;
            try {
                euroPassCredential = edciCredentialModelUtil.fromFile(new File(filePath));
            } catch (IOException e) {
                logger.error(String.format("IOException: %s", e.getMessage()), e);
                throw new EDCIException(HttpStatus.BAD_REQUEST, ErrorCode.CREDENTIAL_NOT_READABLE, MessageKeys.Exception.BadRquest.UPLOAD_CREDENTIAL_NOT_READABLE).setCause(e);
            } catch (JAXBException e) {
                logger.error(String.format("JAXBException: %s", e.getMessage()), e);
                throw new EDCIException(HttpStatus.BAD_REQUEST, ErrorCode.CREDENTIAL_NOT_READABLE, MessageKeys.Exception.BadRquest.UPLOAD_CREDENTIAL_NOT_READABLE).setCause(e);
            }

            if (Boolean.valueOf(issuerConfigService.getDatabaseConfigurationValue(EDCIIssuerConstants.CONFIG_PROPERTY_ALLOW_QSEALS_ONLY))) {
                if (euroPassCredential instanceof EuropassPresentationDTO && euroPassCredential.getIssuer() == null) {
                    euroPassCredential.setIssuer(new OrganizationDTO());
                    euroPassCredential.getIssuer().setId(URI.create(euroPassCredential.getIssuer().getPrefix(euroPassCredential.getIssuer()).concat("vp:1")));
                }
            } else {
                    if (euroPassCredential instanceof EuropassPresentationDTO && euroPassCredential.getIssuer() == null) {
                        euroPassCredential.setIssuer(new OrganizationDTO());
                        euroPassCredential.getIssuer().setId(URI.create(euroPassCredential.getIssuer().getPrefix(euroPassCredential.getIssuer()).concat("vp:1")));
                        //TODO: test only without Qseal
                        if (countryName == null || organization == null || organizationId == null || commonName == null) {
                            countryName = countryName == null ? "ES" : countryName;
                            organization = organization == null ? "mockOrg" : organization;
                            organizationId = organizationId == null ? "mockID-for-testing" : organizationId;
                            commonName = commonName == null ? "Mock Name" : commonName;
                        }
                    }
            }

            if (euroPassCredential.getIssuer() != null) {

                if (!StringUtils.isBlank(countryName)) {

                    Code country = controlledListCommonsService.searchCountryByEuvocField(ControlledList.COUNTRY.getUrl(),
                            countryName, euroPassCredential.getCredential().getAvailableLanguages());

                    if (country != null) {

                        AddressDTO addr = new AddressDTO();
                        addr.setCountryCode(country);

                        LocationDTO loc = new LocationDTO();
                        loc.setHasAddress(new ArrayList<>());
                        loc.getHasAddress().add(addr);
                        loc.setId(URI.create(loc.getPrefix(loc).concat("vp:1")));

                        euroPassCredential.getIssuer().setHasLocation(new ArrayList<LocationDTO>());
                        euroPassCredential.getIssuer().getHasLocation().add(loc);

                    }

                }

                if (!StringUtils.isBlank(organization)) {
                    Text prefName = new Text();
                    prefName.setContent(euroPassCredential.getCredential().getPrimaryLanguage(), organization);
                    euroPassCredential.getIssuer().setPreferredName(prefName);
                }

                if (!StringUtils.isBlank(organizationId)) {
                    LegalIdentifier identif = new LegalIdentifier();
                    identif.setContent(organizationId);
                    if (!StringUtils.isBlank(countryName)) {
                        identif.setSpatialId(countryName);
                    }
                    euroPassCredential.getIssuer().setLegalIdentifier(identif);
                }

                if (!StringUtils.isBlank(commonName)) {
                    List<Text> alternNames = new ArrayList<>();
                    Text alternName = new Text();
                    alternName.setContent(euroPassCredential.getCredential().getPrimaryLanguage(), commonName);
                    alternNames.add(alternName);
                    euroPassCredential.getIssuer().setAlternativeName(alternNames);
                }

            }

            euroPassCredential.getCredential().setIssued(new Date());

            //TODO: Essential
            ValidationResult validationResult = edciValidationService.validateAndLocalize(euroPassCredential, AfterSealing.class);

            if (!validationResult.isValid()) {
                euroPassCredential.getCredential().setValid(false);
                List<String> errorMessages = new ArrayList<>();
                errorMessages = validationResult.getDistinctErrorMessages();
                Collections.sort(errorMessages);
                euroPassCredential.getValidationErrors().addAll(errorMessages);

                for (ValidationError error : validationResult.getValidationErrors()) {
                    if (MessageKeys.Validation.VALIDATION_ORGANIZATION_LOCATION_MIN.equals(error.getErrorKey())) {
                        errorFields.add(edciMessageService.getMessage("issuer.eSeal.certificate.field.country"));
                    } else if (MessageKeys.Validation.VALIDATION_ORGANIZATION_LEGALIDENTIFIER_NOTNULL.equals(error.getErrorKey())) {
                        errorFields.add(edciMessageService.getMessage("issuer.eSeal.certificate.field.organization.identifier"));
                    } else if (MessageKeys.Validation.VALIDATION_ORGANIZATION_PREFERREDNAME_NOTNULL.equals(error.getErrorKey())) {
                        errorFields.add(edciMessageService.getMessage("issuer.eSeal.certificate.field.organization"));
                    }
                }
            }

            fileService.createXMLFiles(euroPassCredential);

        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }

        return errorFields;
    }

}
