package eu.europa.ec.empl.edci.issuer.util;

import com.apicatalog.jsonld.JsonLdError;
import eu.europa.ec.empl.edci.constants.*;
import eu.europa.ec.empl.edci.datamodel.jsonld.model.*;
import eu.europa.ec.empl.edci.datamodel.jsonld.model.dataTypes.ConceptDTO;
import eu.europa.ec.empl.edci.datamodel.jsonld.model.dataTypes.LegalIdentifier;
import eu.europa.ec.empl.edci.datamodel.jsonld.model.dataTypes.LiteralMap;
import eu.europa.ec.empl.edci.datamodel.view.AttachmentView;
import eu.europa.ec.empl.edci.dss.constants.ESealConfig;
import eu.europa.ec.empl.edci.exception.EDCIException;
import eu.europa.ec.empl.edci.issuer.mapper.ControlledListsMapper;
import eu.europa.ec.empl.edci.issuer.service.IssuerConfigService;
import eu.europa.ec.empl.edci.issuer.service.IssuerFileService;
import eu.europa.ec.empl.edci.service.ControlledListCommonsService;
import eu.europa.ec.empl.edci.service.EDCIFileService;
import eu.europa.ec.empl.edci.service.EDCIMessageService;
import eu.europa.ec.empl.edci.util.CredentialUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class CertificateUtils {

    private static final Logger logger = LogManager.getLogger(CertificateUtils.class);

    @Autowired
    private EDCIFileService edciFileService;

    @Autowired
    private EDCIMessageService edciMessageService;

    @Autowired
    private ControlledListCommonsService controlledListCommonsService;

    @Autowired
    private ControlledListsMapper controlledListsMapper;

    @Autowired
    private IssuerFileService fileService;

    @Autowired
    private CredentialUtil credentialUtil;

    @Autowired
    private IssuerConfigService issuerConfigService;

    public String getCertificateReplaceMsg(Map<String, String> certificateInfo) {

        boolean noFieldReplaced = true;

        String msgLabel = edciMessageService.getMessage("issuer.eSeal.certificate.msg").concat("<br/><br/>");

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
                    organizationProperty, organizationIdProperty, commonNameProperty).concat("<br/>");
        }

        if (certificateInfo.get(EDCIConstants.Certificate.CERTIFICATE_ATTRIBUTE_COUNTRY_NAME) != null) {
            String countryLabel = edciMessageService.getMessage("issuer.eSeal.certificate." + EDCIConstants.Certificate.CERTIFICATE_ATTRIBUTE_COUNTRY_NAME,
                    certificateInfo.get(EDCIConstants.Certificate.CERTIFICATE_ATTRIBUTE_COUNTRY_NAME));
            msgLabel = msgLabel.concat(countryLabel).concat("<br/>");

        }

        String timestampLabel = edciMessageService.getMessage("issuer.eSeal.certificate.timestamp", new SimpleDateFormat(EDCIConstants.DATE_ISO_8601).format(new Date()));

        return msgLabel.concat(timestampLabel);

    }

    /**
     * This methods gets the credential from the file systems using the file path provided, then adds the issuing organization
     * to the credential,this information is retrieved from the sealing certificate. <br>
     * If the mandate issue is provided (File or description, at least one) this information is attached to the credential,
     * the controlled list ISSUED_MANDATE is also added to the credential profiles. <br>
     *
     * @param mandatedIssueFile    the mandate issue information
     * @param filePath             the file path
     * @param credentialIssuerInfo the credential issuer information
     * @throws IOException    the io exception
     * @throws ParseException the parse exception
     * @throws JsonLdError    the json ld error
     * @see <a href="http://data.europa.eu/snb/credential/c_9a31f32a">Issued Mandate controlled list</a>
     */
    public void overwriteCertificateFields(AttachmentView mandatedIssueFile, String filePath, Map<String, String> credentialIssuerInfo) throws IOException, ParseException, JsonLdError {

        if (filePath == null) {
            throw new EDCIException(ErrorCode.CREDENTIAL_NOT_FOUND);
        }

        String countryName = credentialIssuerInfo.get(EDCIConstants.Certificate.CERTIFICATE_ATTRIBUTE_COUNTRY_NAME);
        String organization = credentialIssuerInfo.get(EDCIConstants.Certificate.CERTIFICATE_ATTRIBUTE_ORGANIZATION);
        String organizationId = credentialIssuerInfo.get(EDCIConstants.Certificate.CERTIFICATE_ATTRIBUTE_ORGANIZATION_IDENTIFIER);
        String commonName = credentialIssuerInfo.get(EDCIConstants.Certificate.CERTIFICATE_ATTRIBUTE_COMMON_NAME);
        EuropeanDigitalCredentialDTO euroPassCredential = null;

        byte[] fileBytes = Files.readAllBytes(this.getEdciFileService().getOrCreateFile(filePath).toPath());
        euroPassCredential = this.getCredentialUtil().unMarshallCredential(fileBytes);

        if (!issuerConfigService.getBoolean(ESealConfig.Properties.ADV_QSEAL_ONLY, ESealConfig.Defaults.ADV_QSEAL_ONLY)) {
            countryName = countryName == null ? "ES" : countryName;
            organization = organization == null ? "mockOrg" : organization;
            organizationId = organizationId == null ? "mockID:for:testing" : organizationId;
            commonName = commonName == null ? "Mock Name" : commonName;
        }

        String primaryLanguage = this.getControlledListCommonsService().searchLanguageISO639ByConcept(euroPassCredential.getDisplayParameter().getPrimaryLanguage());
        List<String> availableLanguages = this.getControlledListCommonsService().searchLanguageISO639ByConcept(euroPassCredential.getDisplayParameter().getLanguage());

        if (euroPassCredential.getIssuer() == null) {
            OrganisationDTO issuer = new OrganisationDTO();
            issuer.setId(URI.create(issuer.getIdPrefix(issuer).concat(UUID.randomUUID().toString())));
            euroPassCredential.setIssuer(issuer);
        }

        if (!StringUtils.isBlank(countryName)) {
            ConceptDTO country = controlledListCommonsService.searchCountryByEuvocField(ControlledList.COUNTRY.getUrl(),
                    countryName,
                    availableLanguages);
            if (country != null) {

                AddressDTO addr = new AddressDTO();
                addr.setId(URI.create("urn:certificateAddress:1"));
                addr.setCountryCode(country);

                LocationDTO loc = new LocationDTO();
                loc.setId(URI.create("urn:certificateLocation:1"));
                loc.getAddress().add(addr);

                euroPassCredential.getIssuer().getLocation().add(loc);
            }

            //organizationId -> legalIdentifier.notation
            //countryName to concept -> legalIdentifier.spatial
            if (!StringUtils.isBlank(organizationId)) {
                LegalIdentifier identif = new LegalIdentifier();
                identif.setId(URI.create("urn:certificateIdentifier:1"));
                identif.setNotation(organizationId);
                if (country != null) {
                    identif.setSpatial(country);
                }
                euroPassCredential.getIssuer().seteIDASIdentifier(identif);
            }

            if (!StringUtils.isBlank(commonName)) {
                LiteralMap altName = new LiteralMap();
                altName.put(primaryLanguage, commonName);
                euroPassCredential.getIssuer().setAltLabel(altName);
            }
        }
        if (!StringUtils.isBlank(organization)) {
            LiteralMap legalName = new LiteralMap();
            legalName.put(primaryLanguage, organization);
            euroPassCredential.getIssuer().setLegalName(legalName);
        }

        if (mandatedIssueFile != null) {
            Evidence evidence = new Evidence();
            List<Evidence> originalEvidence = this.getCredentialUtil().getEvidencesByType(ControlledListConcept.EVIDENCE_TYPE_MANDATE, euroPassCredential.getEvidence());

            if(originalEvidence != null) {
                for(Evidence e : originalEvidence) {
                    euroPassCredential.getEvidence().remove(e);
                }
            }

            if (mandatedIssueFile.getContent() != null) {
                ControlledListConcept type;

                if (MediaType.APPLICATION_PDF_VALUE.equalsIgnoreCase(mandatedIssueFile.getType())) {
                    type = ControlledListConcept.FILE_TYPE_PDF;
                } else if (MediaType.IMAGE_PNG_VALUE.equalsIgnoreCase(mandatedIssueFile.getType())) {
                    type = ControlledListConcept.FILE_TYPE_PNG;
                } else {
                    type = ControlledListConcept.FILE_TYPE_JPEG;
                }

                MediaObjectDTO mediaObjectDTO = new MediaObjectDTO();
                mediaObjectDTO.setId(URI.create("urn:epass:mediaObject:".concat(UUID.randomUUID().toString())));
                mediaObjectDTO.setContent(mandatedIssueFile.getContent());
                mediaObjectDTO.setContentEncoding(this.getControlledListCommonsService().searchConceptByConcept(ControlledListConcept.ENCODING_BASE64, primaryLanguage));
                mediaObjectDTO.setContentType(this.getControlledListCommonsService().searchConceptByConcept(type, primaryLanguage));
                mediaObjectDTO.setTitle(new LiteralMap(primaryLanguage, mandatedIssueFile.getName()));

                evidence.getEmbeddedEvidence().add(mediaObjectDTO);
            }
            evidence.setDcType(this.getControlledListCommonsService().searchConceptByConcept(ControlledListConcept.EVIDENCE_TYPE_MANDATE, primaryLanguage));
            evidence.setEvidenceStatement(mandatedIssueFile.getDescription());
            evidence.setId(URI.create("urn:epass:evidence:".concat(UUID.randomUUID().toString())));

            euroPassCredential.getEvidence().add(evidence);

            if(!this.getCredentialUtil().isCredentialProfilePresent(euroPassCredential, ControlledListConcept.CREDENTIAL_TYPE_ISSUED_MANDATE)) {
                euroPassCredential.getCredentialProfiles().add(this.getControlledListCommonsService().searchConceptByConcept(ControlledListConcept.CREDENTIAL_TYPE_ISSUED_MANDATE, primaryLanguage));
            }

            List<URI> credentialSchemaUris = this.getControlledListCommonsService()
                    .getShaclURIsFromProfiles(euroPassCredential.getCredentialProfiles());
            //Add Credential Schemas
            euroPassCredential.setCredentialSchema(credentialSchemaUris.stream().map(uri -> {
                return new ShaclValidator2017(uri);
            }).collect(Collectors.toList()));
        }


        euroPassCredential.setIssued(ZonedDateTime.now());
        euroPassCredential.setIssuanceDate(euroPassCredential.getIssued());

        int lastIndexSlash = filePath.lastIndexOf("/") == -1 ? filePath.lastIndexOf("\\") : filePath.lastIndexOf("/");
        String folderName = filePath.substring(0, lastIndexSlash);
        String fileName = filePath.substring(lastIndexSlash + 1);
        fileService.createJSONLDFile(euroPassCredential, folderName, fileName);

    }

    public EDCIFileService getEdciFileService() {
        return edciFileService;
    }

    public void setEdciFileService(EDCIFileService edciFileService) {
        this.edciFileService = edciFileService;
    }

    public ControlledListCommonsService getControlledListCommonsService() {
        return controlledListCommonsService;
    }

    public void setControlledListCommonsService(ControlledListCommonsService controlledListCommonsService) {
        this.controlledListCommonsService = controlledListCommonsService;
    }

    public CredentialUtil getCredentialUtil() {
        return credentialUtil;
    }

    public void setCredentialUtil(CredentialUtil credentialUtil) {
        this.credentialUtil = credentialUtil;
    }

}
