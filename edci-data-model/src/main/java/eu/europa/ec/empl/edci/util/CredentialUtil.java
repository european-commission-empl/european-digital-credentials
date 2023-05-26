package eu.europa.ec.empl.edci.util;

import com.apicatalog.jsonld.JsonLdError;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.nimbusds.jose.JWSObjectJSON;
import eu.europa.ec.empl.edci.constants.ControlledListConcept;
import eu.europa.ec.empl.edci.constants.DataModelConstants;
import eu.europa.ec.empl.edci.datamodel.jsonld.model.EuropeanDigitalCredentialDTO;
import eu.europa.ec.empl.edci.datamodel.jsonld.model.Evidence;
import eu.europa.ec.empl.edci.datamodel.jsonld.model.PersonDTO;
import eu.europa.ec.empl.edci.datamodel.jsonld.model.ShaclValidator2017;
import eu.europa.ec.empl.edci.datamodel.jsonld.model.base.Identifiable;
import eu.europa.ec.empl.edci.datamodel.jsonld.model.dataTypes.ConceptDTO;
import eu.europa.ec.empl.edci.datamodel.validation.ValidationResult;
import eu.europa.ec.empl.edci.exception.EDCIException;
import eu.europa.ec.empl.edci.service.ControlledListCommonsService;
import org.apache.commons.lang3.LocaleUtils;
import org.apache.jena.shacl.ValidationReport;
import org.apache.jena.shacl.validation.ReportEntry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.util.HtmlUtils;

import javax.mail.internet.MimeUtility;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Component(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class CredentialUtil {

    private final Logger logger = LogManager.getLogger(CredentialUtil.class);

    @Autowired
    private JsonLdUtil jsonLdUtil;

    @Autowired
    private ReflectiveUtil reflectiveUtil;

    @Autowired
    private ControlledListCommonsService controlledListCommonsService;

    private final Pattern patternCredId = Pattern.compile(DataModelConstants.PAYLOAD_PARAMETER);

    public enum MandatoryType {

        VERIFIABLE_CREDENTIAL("VerifiableCredential"),
        EUROPEAN_DIGITAL_CREDENTIAL("EuropeanDigitalCredential");

        private String type;

        private MandatoryType(String type) {
            this.type = type;
        }

        public String getType() {
            return this.type;
        }

        public static List<String> getTypes() {
            return Arrays.stream(MandatoryType.values()).map(MandatoryType::getType).collect(Collectors.toList());
        }
    }

    public String marshallCredentialAsString(EuropeanDigitalCredentialDTO europeanDigitalCredentialDTO) throws JsonProcessingException, JsonLdError {
        return this.getJsonLdUtil().marshallToCompactString(europeanDigitalCredentialDTO, this.getJsonLdUtil().getMandatoryContext());
    }

    public byte[] marshallCredentialAsBytes(EuropeanDigitalCredentialDTO europeanDigitalCredentialDTO) throws JsonProcessingException, JsonLdError {
        return this.getJsonLdUtil().marshallToCompactString(europeanDigitalCredentialDTO, this.getJsonLdUtil().getMandatoryContext()).getBytes();
    }

    public EuropeanDigitalCredentialDTO unMarshallCredential(String credential) throws IOException, ParseException {
        return this.getJsonLdUtil().unMarshall(this.getCredentialOrPayload(credential), EuropeanDigitalCredentialDTO.class);
    }

    public EuropeanDigitalCredentialDTO unMarshallCredential(byte[] credential) throws IOException, ParseException {
        return this.unMarshallCredential(new String(credential, StandardCharsets.UTF_8));
    }

    public String getHumanReadableFileName(EuropeanDigitalCredentialDTO europassCredentialDTO, String locale) {
        String fileName = this.getAvailableName(europassCredentialDTO.getCredentialSubject(), locale)
                .concat(DataModelConstants.StringPool.STRING_SPACE).concat(DataModelConstants.StringPool.STRING_HYPHEN).concat(DataModelConstants.StringPool.STRING_SPACE)
                .concat(MultilangFieldUtil.getLiteralStringOrAny(europassCredentialDTO.getDisplayParameter().getTitle(), locale))
                .concat(DataModelConstants.JSON_LD_EXTENSION);
        fileName = fileName.replaceAll("[:\\\\/*?|<>]", "_");
        return fileName;
    }

    public String getHumanReadableEncodedFileName(EuropeanDigitalCredentialDTO europeanDigitalCredentialDTO, String locale) {
        String fileName = this.getHumanReadableFileName(europeanDigitalCredentialDTO, locale);
        try {
            fileName = MimeUtility.encodeText(fileName, StandardCharsets.UTF_8.name(), null);

        } catch (UnsupportedEncodingException e) {
            logger.error("error encodig attached fileName");
        }
        return fileName;
    }

    public String getAvailableName(PersonDTO credentialSubject, String locale) {
        String name;

        if (credentialSubject.getFullName() != null && !credentialSubject.getFullName().isEmpty()) {
            name = MultilangFieldUtil.getLiteralString(credentialSubject.getFullName(), locale).orElse(DataModelConstants.StringPool.STRING_EMPTY);
        } else if (credentialSubject.getGivenName() != null && !credentialSubject.getGivenName().isEmpty()) {
            name = MultilangFieldUtil.getLiteralString(credentialSubject.getGivenName(), locale).orElse(DataModelConstants.StringPool.STRING_EMPTY);

            name = name.concat(credentialSubject.getPatronymicName() != null ?
                    DataModelConstants.StringPool.STRING_SPACE +
                            MultilangFieldUtil.getLiteralString(credentialSubject.getPatronymicName(), locale).orElse(DataModelConstants.StringPool.STRING_EMPTY) : DataModelConstants.StringPool.STRING_EMPTY);

            name = name.concat(credentialSubject.getFamilyName() != null ?
                    DataModelConstants.StringPool.STRING_SPACE +
                            MultilangFieldUtil.getLiteralString(credentialSubject.getFamilyName(), locale).orElse(DataModelConstants.StringPool.STRING_EMPTY) : DataModelConstants.StringPool.STRING_EMPTY);
        } else if (credentialSubject.getBirthName() != null && !credentialSubject.getBirthName().isEmpty()) {
            name = MultilangFieldUtil.getLiteralString(credentialSubject.getBirthName(), locale).orElse(DataModelConstants.StringPool.STRING_EMPTY);
        } else if (credentialSubject.getFamilyName() != null && !credentialSubject.getFamilyName().isEmpty()) {
            name = MultilangFieldUtil.getLiteralString(credentialSubject.getFamilyName(), locale).orElse(DataModelConstants.StringPool.STRING_EMPTY);
        } else {
            name = "Anonymous";
        }

        return name;
    }

    /**
     * The String of the credential Itself, for JWS credentials, first extract the payload using JadesValidationService.getCredentialOrPayload from eseal-core
     *
     * @param credential
     * @return ValidationResult
     */
    public ValidationResult validateCredential(byte[] credential) {
        return validateCredential(new String(credential, StandardCharsets.UTF_8));
    }

    /**
     * The String of the credential Itself, for JWS credentials, first extract the payload using JadesValidationService.getCredentialOrPayload from eseal-core
     *
     * @param credential
     * @return ValidationResult
     */
    public ValidationResult validateCredential(String credential) {
        ValidationResult validationResult = new ValidationResult();

        try {
            EuropeanDigitalCredentialDTO credentialDTO = this.unMarshallCredential(credential);

            if (credentialDTO == null) {
                validationResult.addValidationError(DataModelConstants.BadRquest.UPLOAD_CREDENTIAL_BAD_FORMAT);
                validationResult.setValid(false);
            } else {
                Set<URI> schemaLocations = new HashSet<>(credentialDTO.getCredentialSchema().stream().map(ShaclValidator2017::getId).collect(Collectors.toSet()));
                doRunSHACLValidation(credential, schemaLocations.stream().map(URI::toString).collect(Collectors.toSet()), validationResult);
            }
        } catch (EDCIException e) {
            validationResult.addValidationError(e.getMessageKey());
            validationResult.setValid(false);
        } catch (Exception e) {
            validationResult.addValidationError(e.getMessage());
            validationResult.setValid(false);
        }

        return validationResult;
    }

    public ValidationResult validateCredential(String credential, Set<String> shacls) {
        ValidationResult validationResult = new ValidationResult();

        try {
            doRunSHACLValidation(credential, shacls, validationResult);
        } catch (Exception e) {
            validationResult.addValidationError(e.getMessage());
            validationResult.setValid(false);
        }

        return validationResult;
    }

    private void doRunSHACLValidation(String credential, Set<String> shacls, ValidationResult validationResult) throws IOException {
        for (String schema : shacls) {
            ValidationReport validationReport = this.getJsonLdUtil().validateRDF(credential, schema);
            validationResult.setValid(validationReport.conforms());

            if (!validationResult.isValid()) {
                for (ReportEntry entry : validationReport.getEntries()) {
                    validationResult.addValidationErrorMessage(this.sanitizeValidationReportEntryMessage(entry), entry);
                }
                break;
            }
        }
    }

    public String getCredentialOrPayload(byte[] bytes) throws ParseException {
        String fileString = new String(bytes, StandardCharsets.UTF_8);
        return this.getCredentialOrPayload(fileString);
    }

    public boolean isCredentialSignedWithJWS(String fileString) throws ParseException {
        Matcher matcherId = patternCredId.matcher(fileString);
        return matcherId.find();
    }

    public String getCredentialOrPayload(String fileString) throws ParseException {
        if (!isCredentialSignedWithJWS(fileString)) {
            return fileString;
        } else {
            JWSObjectJSON jwsObject = JWSObjectJSON.parse(fileString);
            String stringContent = jwsObject.getPayload().toBase64URL().toString();
            return stringContent;
        }
    }

    public Evidence getEvidenceByType(ControlledListConcept type, List<Evidence> evidenceList) {
        if(!evidenceList.isEmpty()) {
            String url = type.getUrl();

            for(Evidence evidence : evidenceList) {
                if(evidence.getDcType() != null && url.equalsIgnoreCase(evidence.getDcType().getId().toString())) {
                    return evidence;
                }
            }
        }

        return null;
    }


    private String sanitizeValidationReportEntryMessage(ReportEntry entry) {
        StringBuilder errorMessage = new StringBuilder();
        errorMessage.append("<strong>Node:</strong> ").append(HtmlUtils.htmlEscape(entry.focusNode().toString())).append("\n");
        errorMessage.append("<strong>Path:</strong> ").append(HtmlUtils.htmlEscape(entry.resultPath().toString())).append("\n");
        errorMessage.append("<strong>Error: </strong>").append(HtmlUtils.htmlEscape(entry.message().toString())).append("\n\n");
        return errorMessage.toString();
    }

    public Locale guessPrimaryLanguage(EuropeanDigitalCredentialDTO europassCredentialDTO) {
        Locale locale = null;
        //ToDo -> Trim values?
        //Try getting primary language
        if (europassCredentialDTO.getDisplayParameter() != null && europassCredentialDTO.getDisplayParameter().getPrimaryLanguage() != null) {
            locale = LocaleUtils.toLocale(this.getControlledListCommonsService()
                    .searchLanguageISO639ByConcept(
                            europassCredentialDTO.getDisplayParameter().getPrimaryLanguage()
                    ));
            //check for any available language
        } else if (europassCredentialDTO.getDisplayParameter() != null && europassCredentialDTO.getDisplayParameter().getLanguage() != null
                && !europassCredentialDTO.getDisplayParameter().getLanguage().isEmpty()) {
            locale = LocaleUtils.toLocale(this.getControlledListCommonsService()
                    .searchLanguageISO639ByConcept(
                            europassCredentialDTO.getDisplayParameter().getLanguage().stream().findFirst().get()));
        } else {
            //return any context locale
            locale = LocaleContextHolder.getLocale();
        }
        return locale;
    }

    public Set<String> getAvailableLanguages(EuropeanDigitalCredentialDTO europeanDigitalCredentialDTO) {
        if (europeanDigitalCredentialDTO.getDisplayParameter() != null
                && europeanDigitalCredentialDTO.getDisplayParameter().getLanguage() != null
                && !europeanDigitalCredentialDTO.getDisplayParameter().getLanguage().isEmpty()) {
            List<ConceptDTO> langConcepts = europeanDigitalCredentialDTO.getDisplayParameter().getLanguage();
            return langConcepts.stream().map(conceptDTO -> {
                return this.getControlledListCommonsService().searchLanguageISO639ByConcept(conceptDTO);
            }).collect(Collectors.toSet());
        } else {
            return new HashSet<>();
        }
    }

    public boolean isAccreditedCredential(EuropeanDigitalCredentialDTO europeanDigitalCredentialDTO) {
        return europeanDigitalCredentialDTO.getCredentialProfiles().stream().anyMatch(conceptDTO -> conceptDTO.getId().toString().equals(ControlledListConcept.CREDENTIAL_TYPE_ACCREDITATION.getUrl()));
    }

    public List<String> getMandatoryTypes(List<String> types) {
        List<String> mandatoryTypes = Arrays.asList("VerifiableCredential", "EuropeanDigitalCredential");
        return mandatoryTypes.stream().filter(mandatoryType -> !types.contains(mandatoryType)).collect(Collectors.toList());
    }

    public <
            T> List<T> getClaimsOfClass(EuropeanDigitalCredentialDTO europeanDigitalCredentialDTO, Class<T> clazz) {
        return europeanDigitalCredentialDTO.getCredentialSubject().getHasClaim()
                .stream()
                .filter(claimDTO -> clazz.isAssignableFrom(claimDTO.getClass()))
                .map(claim -> clazz.cast(claim))
                .collect(Collectors.toList());
    }

    public boolean isAllowedType(String type) {
        switch (type) {
            case "VerifiableCredential":
                return true;
            case "EuropeanDigitalCredential":
                return true;
            case "ConvertedCredential":
                return true;
            default:
                return false;
        }
    }

    public void doAddMissingIdentifiers(EuropeanDigitalCredentialDTO europassCredentialDTO) {
        List<Identifiable> allIdentifiables = this.getReflectiveUtil().getInnerObjectsOfType(Identifiable.class, europassCredentialDTO).stream().collect(Collectors.toList());
        List<Identifiable> identifiablesWithoutID = allIdentifiables.stream()
                .filter(jsonLdCommonDTO -> jsonLdCommonDTO.getId() == null).collect(Collectors.toList());
        List<Identifiable> identifiablesWithID = allIdentifiables.stream()
                .filter(jsonLdCommonDTO -> jsonLdCommonDTO.getId() != null &&
                        jsonLdCommonDTO.getId().toString().contains(jsonLdCommonDTO.getIdPrefix(jsonLdCommonDTO))).collect(Collectors.toList());

        Map<String, List<Object>> orderedObjectsWithoutID = this.getReflectiveUtil().getTypesHashMap(identifiablesWithoutID);
        Map<Integer, URI> identifiedHashes = new HashMap<Integer, URI>();

        for (Identifiable identifiable : identifiablesWithID) {
            identifiedHashes.put(identifiable.hashCode(), identifiable.getId());
        }

        for (Map.Entry<String, List<Object>> entry : orderedObjectsWithoutID.entrySet()) {
            List<Object> identifiableList = entry.getValue();
            int counter = 1;

            for (int i = 0; i < identifiableList.size(); i++) {
                Object object = identifiableList.get(i);
                Identifiable identifiable = (Identifiable) object;
                URI uri = null;
                if (identifiedHashes.containsKey(identifiable.hashCode())) {
                    uri = identifiedHashes.get(identifiable.hashCode());
                } else {
                    //Avoid Same ID with different Hashes
                    while (identifiedHashes.values().contains(URI.create(identifiable.getIdPrefix(identifiable).concat(String.valueOf(counter))))) {
                        counter++;
                    }
                    String uriString = identifiable.getIdPrefix(identifiable).concat(String.valueOf(counter));

                    uri = URI.create(uriString);
                    identifiedHashes.put(identifiable.hashCode(), uri);
                }
                identifiable.setId(uri);
            }
        }
    }

    public JsonLdUtil getJsonLdUtil() {
        return jsonLdUtil;
    }

    public void setJsonLdUtil(JsonLdUtil jsonLdUtil) {
        this.jsonLdUtil = jsonLdUtil;
    }

    public ControlledListCommonsService getControlledListCommonsService() {
        return controlledListCommonsService;
    }

    public void setControlledListCommonsService(ControlledListCommonsService controlledListCommonsService) {
        this.controlledListCommonsService = controlledListCommonsService;
    }

    public ReflectiveUtil getReflectiveUtil() {
        return reflectiveUtil;
    }

    public void setReflectiveUtil(ReflectiveUtil reflectiveUtil) {
        this.reflectiveUtil = reflectiveUtil;
    }
}
