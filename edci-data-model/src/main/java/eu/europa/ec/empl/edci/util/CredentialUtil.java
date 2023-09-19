package eu.europa.ec.empl.edci.util;

import com.apicatalog.jsonld.JsonLdError;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.nimbusds.jose.JWSObjectJSON;
import eu.europa.ec.empl.edci.config.service.BaseConfigService;
import eu.europa.ec.empl.edci.constants.ControlledListConcept;
import eu.europa.ec.empl.edci.constants.DataModelConstants;
import eu.europa.ec.empl.edci.datamodel.jsonld.model.*;
import eu.europa.ec.empl.edci.datamodel.jsonld.model.base.Identifiable;
import eu.europa.ec.empl.edci.datamodel.jsonld.model.dataTypes.ConceptDTO;
import eu.europa.ec.empl.edci.datamodel.validation.SHACLValidationError;
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

/**
 * This class has util methods related to european digital credentials and calls to JsonLdUtil like marshall, unmarshall or validation process
 * @see JsonLdUtil
 */
@Component(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class CredentialUtil {

    private final Logger logger = LogManager.getLogger(CredentialUtil.class);

    @Autowired
    private JsonLdUtil jsonLdUtil;

    @Autowired
    private JsonUtil jsonUtil;

    @Autowired
    private ReflectiveUtil reflectiveUtil;

    @Autowired
    private ControlledListCommonsService controlledListCommonsService;

    @Autowired
    private BaseConfigService baseConfigService;

    private final Pattern patternCredId = Pattern.compile(DataModelConstants.PAYLOAD_PARAMETER);

    /**
     * The enum Mandatory type.
     */
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

    private URI[] getMandatoryContext() {
        return new URI[]{
                URI.create("https://www.w3.org/2018/credentials/v1"),
                URI.create(this.getBaseConfigService().getString(DataModelConstants.Properties.JSON_CONTEXT, ""))
        };
    }

    /**
     * Marshall an European digital credential to a compact JSON-LD String.
     *
     * @see <a href="https://www.w3.org/TR/json-ld11-api/#introduction">W3C JSON-LD features</a>
     * @param europeanDigitalCredentialDTO the european digital credential dto
     * @return the JSON-LD
     * @throws JsonProcessingException the json processing exception
     * @throws JsonLdError             the json ld error
     */
    public String marshallCredentialAsString(EuropeanDigitalCredentialDTO europeanDigitalCredentialDTO) throws JsonProcessingException, JsonLdError {
        return this.getJsonLdUtil().marshallToCompactString(europeanDigitalCredentialDTO, this.getMandatoryContext());
    }

    /**
     * Marshall an European digital credential to a compact JSON-LD String and convert it to byte[].
     *
     * @see <a href="https://www.w3.org/TR/json-ld11-api/#introduction">W3C JSON-LD features</a>
     * @param europeanDigitalCredentialDTO the european digital credential dto
     * @return the JSON-LD as byte[]
     * @throws JsonProcessingException the json processing exception
     * @throws JsonLdError             the json ld error
     */
    public byte[] marshallCredentialAsBytes(EuropeanDigitalCredentialDTO europeanDigitalCredentialDTO) throws JsonProcessingException, JsonLdError {
        return this.getJsonLdUtil().marshallToCompactString(europeanDigitalCredentialDTO, this.getMandatoryContext()).getBytes();
    }

    /**
     * Unmarshall a credential in JSON-LD format to european digital credential dto.
     *
     * @see <a href="https://www.w3.org/TR/json-ld11-api/#introduction">W3C JSON-LD features</a>
     * @param credential the credential as string
     * @return the european digital credential dto
     * @throws IOException    the io exception
     * @throws ParseException the parse exception
     */
    public EuropeanDigitalCredentialDTO unMarshallCredential(String credential) throws IOException, ParseException {
        return this.getJsonUtil().unMarshall(this.getCredentialOrPayload(credential), EuropeanDigitalCredentialDTO.class);
    }

    /**
     * Unmarshall a credential in JSON-LD format to european digital credential dto.
     *
     * @see <a href="https://www.w3.org/TR/json-ld11-api/#introduction">W3C JSON-LD features</a>
     * @param credential the credential as byte[]
     * @return the european digital credential dto
     * @throws IOException    the io exception
     * @throws ParseException the parse exception
     */
    public EuropeanDigitalCredentialDTO unMarshallCredential(byte[] credential) throws IOException, ParseException {
        return this.unMarshallCredential(new String(credential, StandardCharsets.UTF_8));
    }

    /**
     * This method gets an available name from the european digital credential and concatenate the credential title .
     *
     * @param europeanDigitalCredentialDTO the european digital credential dto
     * @param locale          the locale language in ISO 639-1 format (Ex: 'en' - English)
     * @return the file name
     */
    public String getHumanReadableFileName(EuropeanDigitalCredentialDTO europeanDigitalCredentialDTO, String locale) {
        String fileName = this.getAvailableName(europeanDigitalCredentialDTO.getCredentialSubject(), locale)
                .concat(DataModelConstants.StringPool.STRING_SPACE).concat(DataModelConstants.StringPool.STRING_HYPHEN).concat(DataModelConstants.StringPool.STRING_SPACE)
                .concat(MultilangFieldUtil.getLiteralStringOrAny(europeanDigitalCredentialDTO.getDisplayParameter().getTitle(), locale))
                .concat(DataModelConstants.JSON_LD_EXTENSION);
        fileName = fileName.replaceAll("[:\\\\/*?|<>]", "_");
        return fileName;
    }

    /**
     * This method gets an available name from the european digital credential and concatenate the credential title .
     *
     * @param europeanDigitalCredentialDTO the european digital credential dto
     * @param locale          the locale language in ISO 639-1 format (Ex: 'en' - English)
     * @return the encoded file name
     */
    public String getHumanReadableEncodedFileName(EuropeanDigitalCredentialDTO europeanDigitalCredentialDTO, String locale) {
        String fileName = this.getHumanReadableFileName(europeanDigitalCredentialDTO, locale);
        try {
            fileName = MimeUtility.encodeText(fileName, StandardCharsets.UTF_8.name(), null);

        } catch (UnsupportedEncodingException e) {
            logger.error("error encodig attached fileName");
        }
        return fileName;
    }

    /**
     * Gets available name from a credential subject.
     *
     * if the fullName is not empty it returns del fullName in the provided language
     * if the givenName is not empty it returns del givenName in the provided language + patronymicName and familyName if they exists
     * if the birthName is not empty it returns del birthName in the provided language
     * if the familyName is not empty it returns del familyName in the provided language
     * if none of the above conditions is met, it returns "Anonymous"
     *
     * @param credentialSubject the credential subject of the european digital credential
     * @param locale          the locale language in ISO 639-1 format (Ex: 'en' - English)
     * @return the available name
     */
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
     * Provide the bytes of the credential Itself, for JWS credentials, first extract the payload using getCredentialOrPayload method, then the credential
     * is validated using SHACL shapes file in TTL format, this SHACL is extracted from the credential's field credentialSchema
     * For more information about SHACL @see <a href="https://www.w3.org/TR/shacl/">W3C Shacl definition</a>
     *
     * @param credential the credential
     * @return the validation result
     */
    public ValidationResult validateCredential(byte[] credential) {
        return validateCredential(new String(credential, StandardCharsets.UTF_8));
    }

    /**
     * Provide the string of the credential Itself, for JWS credentials, first extract the payload using getCredentialOrPayload method.
     * Then the credential is validated using SHACL shapes file in TTL format, this SHACL is extracted from the credential's field credentialSchema
     * For more information about SHACL @see <a href="https://www.w3.org/TR/shacl/">W3C Shacl definition</a>
     *
     * @param credential the credential
     * @return the validation result
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

    /**
     * Provide the string of the credential Itself, for JWS credentials, first extract the payload using getCredentialOrPayload method.
     * Then the credential is validated using SHACL shapes file provided in TTL format as string
     * For more information about SHACL @see <a href="https://www.w3.org/TR/shacl/">W3C Shacl definition</a>
     *
     * @param credential the credential
     * @param shacls     the shacls
     * @return the validation result
     */
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
                    validationResult.addValidationError(new SHACLValidationError(this.sanitizeValidationReportEntryMessage(entry), entry));
                }
                break;
            }
        }
    }

    /**
     * Gets credential content, if the credential is sealed, the content is retrieved from the payload.
     *
     * @param bytes the credential as bytes
     * @return the credential or the payload
     * @throws ParseException the parse exception
     */
    public String getCredentialOrPayload(byte[] bytes) throws ParseException {
        String fileString = new String(bytes, StandardCharsets.UTF_8);
        return this.getCredentialOrPayload(fileString);
    }

    /**
     * Is credential signed with jws, it checks if the credential is inside a payload.
     *
     * @param fileString the credential as string
     * @return the boolean
     * @throws ParseException the parse exception
     */
    public boolean isCredentialSignedWithJWS(String fileString) throws ParseException {
        Matcher matcherId = patternCredId.matcher(fileString);
        return matcherId.find();
    }

    /**
     * Gets credential content, if the credential is sealed, the content is retrieved from the payload.
     *
     * @param fileString the credential as string
     * @return the credential or the payload
     * @throws ParseException the parse exception
     */
    public String getCredentialOrPayload(String fileString) throws ParseException {
        if (!isCredentialSignedWithJWS(fileString)) {
            return fileString;
        } else {
            JWSObjectJSON jwsObject = JWSObjectJSON.parse(fileString);
            String stringContent = jwsObject.getPayload().toBase64URL().toString();
            return stringContent;
        }
    }

    /**
     * Gets evidence by type, it checks if the id of the dcType is the same as the one containing the specified type.
     *
     * @param type         the type
     * @param evidenceList the evidence list
     * @return the evidence by type
     */
    public Evidence getEvidenceByType(ControlledListConcept type, List<Evidence> evidenceList) {
        if (evidenceList != null && !evidenceList.isEmpty()) {
            String url = type.getUrl();

            for (Evidence evidence : evidenceList) {
                if (evidence.getDcType() != null && url.equalsIgnoreCase(evidence.getDcType().getId().toString())) {
                    return evidence;
                }
            }
        }

        return null;
    }

    /**
     * Gets a list of evidences by type, it checks if the id of the dcType is the same as the one containing the specified type.
     *
     * @param type         the type
     * @param evidenceList the evidence list
     * @return the list of evidences by type
     */
    public List<Evidence> getEvidencesByType(ControlledListConcept type, List<Evidence> evidenceList) {
        if (evidenceList != null && !evidenceList.isEmpty()) {
            List<Evidence> evidenceListResult = new ArrayList<>();
            String url = type.getUrl();


            for (Evidence evidence : evidenceList) {
                if (evidence.getDcType() != null && url.equalsIgnoreCase(evidence.getDcType().getId().toString())) {
                    evidenceListResult.add(evidence);
                }
            }

            return evidenceListResult;
        }

        return null;
    }


    private String sanitizeValidationReportEntryMessage(ReportEntry entry) {
        StringBuilder errorMessage = new StringBuilder();
        errorMessage.append("<strong>Node:</strong> ").append(HtmlUtils.htmlEscape(entry.focusNode().toString())).append("\n");
        errorMessage.append("<strong>Path:</strong> ").append(HtmlUtils.htmlEscape(entry.resultPath().toString())).append("\n");
        errorMessage.append("<strong>Error: </strong>").append(HtmlUtils.htmlEscape(entry.message())).append("\n\n");
        return errorMessage.toString();
    }

    /**
     * Gets the primary language of the european digital credential, if not present, gets the first available language, if none of them are present
     * then the default locale of the system is returned.
     *
     * @param europeanDigitalCredentialDTO the european digital credential dto
     * @return the locale language in ISO 639-1 format (Ex: 'en' - English)
     */
    public Locale guessPrimaryLanguage(EuropeanDigitalCredentialDTO europeanDigitalCredentialDTO) {
        Locale locale = null;
        //ToDo -> Trim values?
        //Try getting primary language
        if (europeanDigitalCredentialDTO.getDisplayParameter() != null && europeanDigitalCredentialDTO.getDisplayParameter().getPrimaryLanguage() != null) {
            locale = LocaleUtils.toLocale(this.getControlledListCommonsService()
                    .searchLanguageISO639ByConcept(
                            europeanDigitalCredentialDTO.getDisplayParameter().getPrimaryLanguage()
                    ));
            //check for any available language
        } else if (europeanDigitalCredentialDTO.getDisplayParameter() != null && europeanDigitalCredentialDTO.getDisplayParameter().getLanguage() != null
                && !europeanDigitalCredentialDTO.getDisplayParameter().getLanguage().isEmpty()) {
            locale = LocaleUtils.toLocale(this.getControlledListCommonsService()
                    .searchLanguageISO639ByConcept(
                            europeanDigitalCredentialDTO.getDisplayParameter().getLanguage().stream().findFirst().get()));
        } else {
            //return any context locale
            locale = LocaleContextHolder.getLocale();
        }
        return locale;
    }

    /**
     * Gets available languages of the european digital credential in ISO-639 format.
     *
     * @param europeanDigitalCredentialDTO the european digital credential dto
     * @return the available languages
     */
    public Set<String> getAvailableLanguages(EuropeanDigitalCredentialDTO europeanDigitalCredentialDTO) {
        if (europeanDigitalCredentialDTO.getDisplayParameter() != null
                && europeanDigitalCredentialDTO.getDisplayParameter().getLanguage() != null
                && !europeanDigitalCredentialDTO.getDisplayParameter().getLanguage().isEmpty()) {
            List<ConceptDTO> langConcepts = europeanDigitalCredentialDTO.getDisplayParameter().getLanguage();
            return langConcepts.stream().map(conceptDTO ->
                this.getControlledListCommonsService().searchLanguageISO639ByConcept(conceptDTO)
            ).collect(Collectors.toSet());
        } else {
            return new HashSet<>();
        }
    }

    /**
     * Checks if the credential is accredited by checking if it has an accreditation evidence.
     *
     * @param europeanDigitalCredentialDTO the european digital credential dto
     * @return the boolean
     */
    public boolean isAccreditedCredential(EuropeanDigitalCredentialDTO europeanDigitalCredentialDTO) {
        return europeanDigitalCredentialDTO.getCredentialProfiles().stream().anyMatch(conceptDTO -> conceptDTO.getId().toString().equals(ControlledListConcept.CREDENTIAL_TYPE_ACCREDITATION.getUrl()));
    }

    /**
     * Checks if the credential has the provided credential profile.
     *
     * @param europeanDigitalCredentialDTO the european digital credential dto
     * @param controlledListConcept the type to be searched
     * @return the boolean
     */
    public boolean isCredentialProfilePresent(EuropeanDigitalCredentialDTO europeanDigitalCredentialDTO, ControlledListConcept controlledListConcept) {
        return europeanDigitalCredentialDTO.getCredentialProfiles().stream().anyMatch(conceptDTO -> conceptDTO.getId().toString().equals(controlledListConcept.getUrl()));
    }

    /**
     * Gets mandatory default types of an European Digital Credential.
     * @Deprecated
     *
     * @param types the types
     * @return the mandatory types
     */
    @Deprecated
    public List<String> getMandatoryTypes(List<String> types) {
        List<String> mandatoryTypes = Arrays.asList("VerifiableCredential", "EuropeanDigitalCredential");
        return mandatoryTypes.stream().filter(mandatoryType -> !types.contains(mandatoryType)).collect(Collectors.toList());
    }

    /**
     * Search for claims in the european digital credential, filtered by the class provided.
     * Possible classes:
     * {@link LearningAchievementDTO}, {@link LearningEntitlementDTO}, {@link LearningActivityDTO}, {@link LearningAssessmentDTO}
     * @param <T>                          the filtering class
     * @param europeanDigitalCredentialDTO the european digital credential dto
     * @param clazz                        the filtering class
     * @return the claims
     */
    public <T> List<T> getClaimsOfClass(EuropeanDigitalCredentialDTO europeanDigitalCredentialDTO, Class<T> clazz) {
        return europeanDigitalCredentialDTO.getCredentialSubject().getHasClaim()
                .stream()
                .filter(claimDTO -> clazz.isAssignableFrom(claimDTO.getClass()))
                .map(claim -> clazz.cast(claim))
                .collect(Collectors.toList());
    }

    /**
     * Checks if the type provided is an allowed credential type.
     *
     * @param type the type
     * @return the boolean
     */
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

    /**
     * This method adds missing identifiers for identifiable objects using reflection to search for fields inside European digital credential.
     * All objects that extend from {@link eu.europa.ec.empl.edci.datamodel.jsonld.model.base.JsonLdCommonDTO} are identifiable.
     *
     * @param europeanDigitalCredentialDTO the european digital credential dto
     */
    public void doAddMissingIdentifiers(EuropeanDigitalCredentialDTO europeanDigitalCredentialDTO) {
        List<Identifiable> allIdentifiables = this.getReflectiveUtil().getInnerObjectsOfType(Identifiable.class, europeanDigitalCredentialDTO).stream().collect(Collectors.toList());
        List<Identifiable> identifiablesWithoutID = allIdentifiables.stream()
                .filter(jsonLdCommonDTO -> jsonLdCommonDTO.getId() == null).collect(Collectors.toList());
        List<Identifiable> identifiablesWithID = allIdentifiables.stream()
                .filter(jsonLdCommonDTO -> jsonLdCommonDTO.getId() != null &&
                        jsonLdCommonDTO.getId().toString().contains(jsonLdCommonDTO.getIdPrefix(jsonLdCommonDTO))).collect(Collectors.toList());

        Map<String, List<Object>> orderedObjectsWithoutID = this.getReflectiveUtil().getTypesHashMap(identifiablesWithoutID);
        Map<Integer, URI> identifiedHashes = new HashMap<>();

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

    public JsonUtil getJsonUtil() {
        return jsonUtil;
    }

    public void setJsonUtil(JsonUtil jsonUtil) {
        this.jsonUtil = jsonUtil;
    }

    public BaseConfigService getBaseConfigService() {
        return baseConfigService;
    }

    public void setBaseConfigService(BaseConfigService baseConfigService) {
        this.baseConfigService = baseConfigService;
    }
}
