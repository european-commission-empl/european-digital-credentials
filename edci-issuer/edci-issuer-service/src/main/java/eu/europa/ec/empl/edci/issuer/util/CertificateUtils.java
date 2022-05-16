package eu.europa.ec.empl.edci.issuer.util;

import eu.europa.ec.empl.edci.constants.ControlledList;
import eu.europa.ec.empl.edci.constants.EDCIConstants;
import eu.europa.ec.empl.edci.constants.EDCIMessageKeys;
import eu.europa.ec.empl.edci.constants.ErrorCode;
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
import eu.europa.ec.empl.edci.issuer.common.constants.IssuerConfig;
import eu.europa.ec.empl.edci.issuer.mapper.ControlledListsMapper;
import eu.europa.ec.empl.edci.issuer.service.IssuerConfigService;
import eu.europa.ec.empl.edci.issuer.service.IssuerFileService;
import eu.europa.ec.empl.edci.service.ControlledListCommonsService;
import eu.europa.ec.empl.edci.service.EDCIFileService;
import eu.europa.ec.empl.edci.service.EDCIMessageService;
import eu.europa.ec.empl.edci.service.EDCIValidationService;
import eu.europa.ec.empl.edci.util.EDCICredentialModelUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.annotation.Nullable;
import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class CertificateUtils {

    private static final Logger logger = LogManager.getLogger(CertificateUtils.class);

    @Autowired
    private EDCIFileService edciFileService;

    @Autowired
    private EDCIMessageService edciMessageService;

    @Autowired
    private EDCICredentialModelUtil edciCredentialModelUtil;

    @Autowired
    private EDCIValidationService edciValidationService;

    @Autowired
    private ControlledListCommonsService controlledListCommonsService;

    @Autowired
    private ControlledListsMapper controlledListsMapper;

    @Autowired
    private IssuerFileService fileService;

    @Autowired
    private IssuerConfigService issuerConfigService;

    public String getCertificateReplaceMsg(Map<String, String> certificateInfo) {

        boolean noFieldReplaced = true;

        String msgLabel = edciMessageService.getMessage("issuer.eSeal.certificate.msg").concat("\n\n");

        String organizationProperty = "";
        if (certificateInfo.get(EDCIConstants.Certificate.CERTIFICATE_ATTRIBUTE_ORGANIZATION) != null) {
            organizationProperty = certificateInfo.get(EDCIConstants.Certificate.CERTIFICATE_ATTRIBUTE_ORGANIZATION);
            noFieldReplaced = false;
        }

        String organizationIdProperty = "";
        if (certificateInfo.get(EDCIConstants.Certificate.CERTIFICATE_ATTRIBUTE_ORGANIZATION_IDENTIFIER) != null) {
            organizationIdProperty = "(" + certificateInfo.get(EDCIConstants.Certificate.CERTIFICATE_ATTRIBUTE_ORGANIZATION_IDENTIFIER) + ")";
            noFieldReplaced = false;
        }

        String commonNameProperty = "";
        if (certificateInfo.get(EDCIConstants.Certificate.CERTIFICATE_ATTRIBUTE_COMMON_NAME) != null) {
            commonNameProperty = (!noFieldReplaced ? " - " : "") + certificateInfo.get(EDCIConstants.Certificate.CERTIFICATE_ATTRIBUTE_COMMON_NAME);
            noFieldReplaced = false;
        }

        if (!noFieldReplaced) {
            msgLabel += edciMessageService.getMessage("issuer.eSeal.certificate." + EDCIConstants.Certificate.CERTIFICATE_ATTRIBUTE_ORGANIZATION,
                    organizationProperty, organizationIdProperty, commonNameProperty).concat("\n");
        }

        if (certificateInfo.get(EDCIConstants.Certificate.CERTIFICATE_ATTRIBUTE_COUNTRY_NAME) != null) {
            String countryLabel = edciMessageService.getMessage("issuer.eSeal.certificate." + EDCIConstants.Certificate.CERTIFICATE_ATTRIBUTE_COUNTRY_NAME,
                    certificateInfo.get(EDCIConstants.Certificate.CERTIFICATE_ATTRIBUTE_COUNTRY_NAME));
            msgLabel = msgLabel.concat(countryLabel).concat("\n");

        }

        String timestampLabel = edciMessageService.getMessage("issuer.eSeal.certificate.timestamp", new SimpleDateFormat(EDCIConstants.DATE_ISO_8601).format(new Date()));

        return msgLabel.concat(timestampLabel);

    }


    /*
     * Adds information from used credential to the XML
     * Adds issued date to the XML
     * */
    public List<String> overwriteCertificateFields(String filePath, Map<String, String> credentialIssuerInfo, @Nullable String resultingPath) {
        List<String> errorFields = new ArrayList<>();

        try {

            String countryName = credentialIssuerInfo.get(EDCIConstants.Certificate.CERTIFICATE_ATTRIBUTE_COUNTRY_NAME);
            String organization = credentialIssuerInfo.get(EDCIConstants.Certificate.CERTIFICATE_ATTRIBUTE_ORGANIZATION);
            String organizationId = credentialIssuerInfo.get(EDCIConstants.Certificate.CERTIFICATE_ATTRIBUTE_ORGANIZATION_IDENTIFIER);
            String commonName = credentialIssuerInfo.get(EDCIConstants.Certificate.CERTIFICATE_ATTRIBUTE_COMMON_NAME);
            CredentialHolderDTO euroPassCredential = null;

            try {
                euroPassCredential = edciCredentialModelUtil.fromFile(this.getEdciFileService().getOrCreateFile(filePath));
            } catch (IOException e) {
                logger.error(String.format("IOException: %s", e.getMessage()), e);
                throw new EDCIException(HttpStatus.BAD_REQUEST, ErrorCode.CREDENTIAL_NOT_READABLE, EDCIMessageKeys.Exception.BadRquest.UPLOAD_CREDENTIAL_NOT_READABLE).setCause(e);
            } catch (JAXBException e) {
                logger.error(String.format("JAXBException: %s", e.getMessage()), e);
                throw new EDCIException(HttpStatus.BAD_REQUEST, ErrorCode.CREDENTIAL_NOT_READABLE, EDCIMessageKeys.Exception.BadRquest.UPLOAD_CREDENTIAL_NOT_READABLE).setCause(e);
            }

            if (euroPassCredential instanceof EuropassPresentationDTO && euroPassCredential.getIssuer() == null) {
                euroPassCredential.setIssuer(new OrganizationDTO());
                euroPassCredential.getIssuer().setId(URI.create(euroPassCredential.getIssuer().getPrefix(euroPassCredential.getIssuer()).concat("vp:1")));
            }

            if (!issuerConfigService.getBoolean(IssuerConfig.Issuer.ALLOW_QSEALS_ONLY, false)) {
                countryName = countryName == null ? "ES" : countryName;
                organization = organization == null ? "mockOrg" : organization;
                organizationId = organizationId == null ? "mockID-for-testing" : organizationId;
                commonName = commonName == null ? "Mock Name" : commonName;
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
                    if (EDCIMessageKeys.Validation.VALIDATION_ORGANIZATION_LOCATION_MIN.equals(error.getErrorKey())) {
                        errorFields.add(edciMessageService.getMessage("issuer.eSeal.certificate.field.country"));
                    } else if (EDCIMessageKeys.Validation.VALIDATION_ORGANIZATION_LEGALIDENTIFIER_NOTNULL.equals(error.getErrorKey())) {
                        errorFields.add(edciMessageService.getMessage("issuer.eSeal.certificate.field.organization.identifier"));
                    } else if (EDCIMessageKeys.Validation.VALIDATION_ORGANIZATION_PREFERREDNAME_NOTNULL.equals(error.getErrorKey())) {
                        errorFields.add(edciMessageService.getMessage("issuer.eSeal.certificate.field.organization"));
                    }
                }
            }

            if (resultingPath == null) {
                fileService.createXMLFiles(euroPassCredential);
            } else {
                fileService.createXMLFile(euroPassCredential, resultingPath);
            }


        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }

        return errorFields;
    }

    public EDCIFileService getEdciFileService() {
        return edciFileService;
    }

    public void setEdciFileService(EDCIFileService edciFileService) {
        this.edciFileService = edciFileService;
    }
}
