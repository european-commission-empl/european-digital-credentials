package eu.europa.ec.empl.edci.util;

import com.apicatalog.jsonld.JsonLdError;
import com.fasterxml.jackson.core.JsonProcessingException;
import eu.europa.ec.empl.edci.config.service.BaseConfigService;
import eu.europa.ec.empl.edci.constants.ControlledListConcept;
import eu.europa.ec.empl.edci.constants.DataModelConstants;
import eu.europa.ec.empl.edci.datamodel.jsonld.model.*;
import eu.europa.ec.empl.edci.datamodel.jsonld.model.dataTypes.ConceptDTO;
import eu.europa.ec.empl.edci.datamodel.jsonld.model.dataTypes.LiteralMap;
import eu.europa.ec.empl.edci.datamodel.validation.ValidationResult;
import eu.europa.ec.empl.edci.exception.EDCIException;
import eu.europa.ec.empl.edci.service.ControlledListCommonsService;
import org.apache.jena.shacl.ValidationReport;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.context.i18n.LocaleContextHolder;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.util.*;

@RunWith(MockitoJUnitRunner.Silent.class)
public class CredentialUtilTest {

    @InjectMocks
    @Spy
    private CredentialUtil credentialUtil;

    @Mock
    private JsonLdUtil jsonLdUtil;

    @Mock
    private BaseConfigService baseConfigService;

    @Mock
    private JsonUtil jsonUtil;

    @Mock
    private ControlledListCommonsService controlledListCommonsService;

    @Mock
    private ReflectiveUtil reflectiveUtil;

    @Before
    public void setUp() {
        Mockito.doReturn("http://testUri.com").when(baseConfigService).getString(DataModelConstants.Properties.JSON_CONTEXT, "");
    }

    @Test
    public void marshallCredentialAsString_ShouldCallMethod() throws JsonProcessingException, JsonLdError {
        Mockito.doReturn("test").when(jsonLdUtil).marshallToCompactString(ArgumentMatchers.any(), ArgumentMatchers.any());
        credentialUtil.marshallCredentialAsString(new EuropeanDigitalCredentialDTO());

        Mockito.verify(jsonLdUtil, Mockito.times(1)).marshallToCompactString(ArgumentMatchers.any(), ArgumentMatchers.any());
    }

    @Test
    public void marshallCredentialAsBytes_ShouldCallMethod() throws JsonProcessingException, JsonLdError {
        Mockito.doReturn("test").when(jsonLdUtil).marshallToCompactString(ArgumentMatchers.any(), ArgumentMatchers.any());
        credentialUtil.marshallCredentialAsBytes(new EuropeanDigitalCredentialDTO());

        Mockito.verify(jsonLdUtil, Mockito.times(1)).marshallToCompactString(ArgumentMatchers.any(), ArgumentMatchers.any());
    }

    @Test
    public void unMarshallCredential_String_ShouldCallMethod() throws IOException, ParseException {
        Mockito.doReturn(new EuropeanDigitalCredentialDTO()).when(jsonUtil).unMarshall(ArgumentMatchers.anyString(), ArgumentMatchers.any());
        credentialUtil.unMarshallCredential("test");

        Mockito.verify(jsonUtil, Mockito.times(1)).unMarshall(ArgumentMatchers.anyString(), ArgumentMatchers.any());
    }

    @Test
    public void unMarshallCredential_Bytes_ShouldCallMethod() throws IOException, ParseException {
        Mockito.doReturn(new EuropeanDigitalCredentialDTO()).when(credentialUtil).unMarshallCredential(ArgumentMatchers.anyString());
        credentialUtil.unMarshallCredential("test".getBytes(StandardCharsets.UTF_8));

        Mockito.verify(credentialUtil, Mockito.times(1)).unMarshallCredential(ArgumentMatchers.anyString());
    }

    @Test
    public void getHumanReadableFileName_ShouldCallMethod() {
        EuropeanDigitalCredentialDTO europeanDigitalCredentialDTO = new EuropeanDigitalCredentialDTO();
        DisplayParameterDTO displayParameterDTO = new DisplayParameterDTO();
        displayParameterDTO.setTitle(new LiteralMap("en", "title"));
        europeanDigitalCredentialDTO.setDisplayParameter(displayParameterDTO);
        Mockito.doReturn("test").when(credentialUtil).getAvailableName(ArgumentMatchers.any(), ArgumentMatchers.any());

        Assert.assertNotNull(credentialUtil.getHumanReadableFileName(europeanDigitalCredentialDTO, "en"));
    }

    @Test
    public void getHumanReadableEncodedFileName_ShouldReturnNotNull() {
        EuropeanDigitalCredentialDTO europeanDigitalCredentialDTO = new EuropeanDigitalCredentialDTO();
        DisplayParameterDTO displayParameterDTO = new DisplayParameterDTO();
        displayParameterDTO.setTitle(new LiteralMap("en", "title"));
        europeanDigitalCredentialDTO.setDisplayParameter(displayParameterDTO);
        Mockito.doReturn("test").when(credentialUtil).getAvailableName(ArgumentMatchers.any(), ArgumentMatchers.any());

        Assert.assertNotNull(credentialUtil.getHumanReadableEncodedFileName(europeanDigitalCredentialDTO, "en"));
    }

    @Test
    public void getAvailableNames_ShouldReturnAnonymous() {
        PersonDTO personDTO = new PersonDTO();

        String result = credentialUtil.getAvailableName(personDTO, "en");
        Assert.assertEquals("Anonymous", result);
    }

    @Test
    public void getAvailableNames_ShouldReturnFullName() {
        PersonDTO personDTO = new PersonDTO();
        personDTO.setFullName(new LiteralMap("en", "fullName"));

        String result = credentialUtil.getAvailableName(personDTO, "en");
        Assert.assertEquals("fullName", result);
    }

    @Test
    public void getAvailableNames_ShouldReturnGivenName() {
        PersonDTO personDTO = new PersonDTO();
        personDTO.setGivenName(new LiteralMap("en", "givenName"));

        String result = credentialUtil.getAvailableName(personDTO, "en");
        Assert.assertEquals("givenName", result);
    }

    @Test
    public void getAvailableNames_ShouldReturnBirthName() {
        PersonDTO personDTO = new PersonDTO();
        personDTO.setBirthName(new LiteralMap("en", "birthName"));

        String result = credentialUtil.getAvailableName(personDTO, "en");
        Assert.assertEquals("birthName", result);
    }

    @Test
    public void getAvailableNames_ShouldReturnFamilyName() {
        PersonDTO personDTO = new PersonDTO();
        personDTO.setFamilyName(new LiteralMap("en", "familyName"));

        String result = credentialUtil.getAvailableName(personDTO, "en");
        Assert.assertEquals("familyName", result);
    }

    @Test
    public void validateCredential_Bytes_ShouldCallMethod() {
        Mockito.doReturn(new ValidationResult()).when(credentialUtil).validateCredential(ArgumentMatchers.anyString());
        credentialUtil.validateCredential("test".getBytes(StandardCharsets.UTF_8));

        Mockito.verify(credentialUtil, Mockito.times(1)).validateCredential(ArgumentMatchers.anyString());
    }

    @Test
    public void validateCredential_String_ShouldReturnInvalid() throws IOException, ParseException {
        Mockito.doReturn(null).when(credentialUtil).unMarshallCredential(ArgumentMatchers.anyString());
        ValidationResult validationResult = credentialUtil.validateCredential("test");

        Assert.assertFalse(validationResult.isValid());
    }

    @Test
    public void validateCredential_String_ThrowIO_ShouldReturnInvalid() throws IOException, ParseException {
        Mockito.when(credentialUtil.unMarshallCredential(ArgumentMatchers.anyString())).thenThrow(new IOException());
        ValidationResult validationResult = credentialUtil.validateCredential("test");

        Assert.assertFalse(validationResult.isValid());
    }

    @Test
    public void validateCredential_String_ThrowEDCIe_ShouldReturnInvalid() throws IOException, ParseException {
        Mockito.when(credentialUtil.unMarshallCredential(ArgumentMatchers.anyString())).thenThrow(new EDCIException());
        ValidationResult validationResult = credentialUtil.validateCredential("test");

        Assert.assertFalse(validationResult.isValid());
    }

    @Test
    public void validateCredential_String_ShouldReturnValid() throws IOException, ParseException, URISyntaxException {
        EuropeanDigitalCredentialDTO europeanDigitalCredentialDTO = JsonLdFactoryUtil.getSimpleCredential();
        Mockito.doReturn(ValidationReport.create().build()).when(jsonLdUtil).validateRDF(ArgumentMatchers.anyString(), ArgumentMatchers.anyString());
        Mockito.doReturn(europeanDigitalCredentialDTO).when(credentialUtil).unMarshallCredential(ArgumentMatchers.anyString());
        ValidationResult validationResult = credentialUtil.validateCredential("test");

        Assert.assertTrue(validationResult.isValid());
    }

    @Test
    public void validateCredential_String_ShouldReturnInValid() throws IOException, ParseException, URISyntaxException {
        EuropeanDigitalCredentialDTO europeanDigitalCredentialDTO = JsonLdFactoryUtil.getSimpleCredential();
        Mockito.doReturn(europeanDigitalCredentialDTO).when(credentialUtil).unMarshallCredential(ArgumentMatchers.anyString());
        ValidationResult validationResult = credentialUtil.validateCredential("test");

        Assert.assertFalse(validationResult.isValid());
    }

    @Test
    public void validateCredential_StringShacls_ShouldThrowReturnInValid() throws IOException {
        Mockito.doThrow(new EDCIException()).when(jsonLdUtil).validateRDF(ArgumentMatchers.anyString(), ArgumentMatchers.anyString());
        ValidationResult validationResult = credentialUtil.validateCredential(null, new HashSet<>(Arrays.asList("payaya")));

        Assert.assertFalse(validationResult.isValid());
    }

    @Test
    public void validateCredential_StringShacls_ShouldReturnInValid() {
        ValidationResult validationResult = credentialUtil.validateCredential("test", new HashSet<>());

        Assert.assertFalse(validationResult.isValid());
    }

    @Test
    public void getCredentialOrPayload_bytes_ShouldCallMethod() throws ParseException {
        Mockito.doReturn("test").when(credentialUtil).getCredentialOrPayload(ArgumentMatchers.anyString());
        credentialUtil.getCredentialOrPayload("test".getBytes(StandardCharsets.UTF_8));

        Mockito.verify(credentialUtil, Mockito.times(1)).getCredentialOrPayload(ArgumentMatchers.anyString());
    }

    @Test
    public void getCredentialOrPayload_string_ShouldReturnNotNull() throws ParseException {
        Assert.assertNotNull(credentialUtil.getCredentialOrPayload("test"));
    }

    @Test
    public void isCredentialSignedWithJWS_ShouldReturnNotNull() throws ParseException {
        Assert.assertNotNull(credentialUtil.isCredentialSignedWithJWS("test"));
    }

    @Test
    public void getEvidenceByType_ShouldReturnNull() {
        Assert.assertNull(credentialUtil.getEvidenceByType(ControlledListConcept.EVIDENCE_TYPE_MANDATE, new ArrayList<>()));
    }

    @Test
    public void getEvidenceByType_ShouldCallMethod() {
        Evidence evidence1 = new Evidence();
        evidence1.setDcType(new ConceptDTO(ControlledListConcept.EVIDENCE_TYPE_MANDATE.getUrl()));

        Evidence evidence2 = new Evidence();
        evidence2.setDcType(new ConceptDTO(ControlledListConcept.EVIDENCE_TYPE_ACCREDITATION.getUrl()));

        Evidence evidence = credentialUtil.getEvidenceByType(ControlledListConcept.EVIDENCE_TYPE_MANDATE, Arrays.asList(evidence1, evidence2));
        Assert.assertEquals(ControlledListConcept.EVIDENCE_TYPE_MANDATE.getUrl(),evidence.getDcType().getId().toString());

    }

    @Test
    public void getEvidencesByType_ShouldReturnNull() {
        Assert.assertNull(credentialUtil.getEvidencesByType(ControlledListConcept.EVIDENCE_TYPE_MANDATE, new ArrayList<>()));
    }

    @Test
    public void getEvidencesByType_ShouldCallMethod() {
        Evidence evidence1 = new Evidence();
        evidence1.setDcType(new ConceptDTO(ControlledListConcept.EVIDENCE_TYPE_MANDATE.getUrl()));

        Evidence evidence2 = new Evidence();
        evidence2.setDcType(new ConceptDTO(ControlledListConcept.EVIDENCE_TYPE_ACCREDITATION.getUrl()));

        List<Evidence> evidence = credentialUtil.getEvidencesByType(ControlledListConcept.EVIDENCE_TYPE_MANDATE, Arrays.asList(evidence1, evidence2));
        Assert.assertEquals(ControlledListConcept.EVIDENCE_TYPE_MANDATE.getUrl(),evidence.get(0).getDcType().getId().toString());
        Assert.assertEquals(1, evidence.size());

    }

    @Test
    public void guessPrimaryLanguage_ShouldReturnLocaleContext() {
        Assert.assertEquals(LocaleContextHolder.getLocale(), credentialUtil.guessPrimaryLanguage(new EuropeanDigitalCredentialDTO()));

    }

    @Test
    public void guessPrimaryLanguage_ShouldCallMethod() throws URISyntaxException {
        Mockito.doReturn(Locale.ENGLISH.toString()).when(controlledListCommonsService).searchLanguageISO639ByConcept(ArgumentMatchers.any(ConceptDTO.class));
        credentialUtil.guessPrimaryLanguage(JsonLdFactoryUtil.getSimpleCredential());
        Mockito.verify(controlledListCommonsService, Mockito.times(1)).searchLanguageISO639ByConcept(ArgumentMatchers.any(ConceptDTO.class));

    }

    @Test
    public void guessPrimaryLanguage_ShouldCallMethod2() throws URISyntaxException {
        EuropeanDigitalCredentialDTO europeanDigitalCredentialDTO = JsonLdFactoryUtil.getSimpleCredential();
        europeanDigitalCredentialDTO.getDisplayParameter().setPrimaryLanguage(null);
        Mockito.doReturn(Locale.ENGLISH.toString()).when(controlledListCommonsService).searchLanguageISO639ByConcept(ArgumentMatchers.any(ConceptDTO.class));
        credentialUtil.guessPrimaryLanguage(europeanDigitalCredentialDTO);
        Mockito.verify(controlledListCommonsService, Mockito.times(1)).searchLanguageISO639ByConcept(ArgumentMatchers.any(ConceptDTO.class));

    }

    @Test
    public void getAvailableLanguages_ShouldCallMethod() throws URISyntaxException {
        Mockito.doReturn(Locale.ENGLISH.toString()).when(controlledListCommonsService).searchLanguageISO639ByConcept(ArgumentMatchers.any(ConceptDTO.class));
        credentialUtil.getAvailableLanguages(JsonLdFactoryUtil.getSimpleCredential());
        Mockito.verify(controlledListCommonsService, Mockito.times(1)).searchLanguageISO639ByConcept(ArgumentMatchers.any(ConceptDTO.class));

    }

    @Test
    public void getAvailableLanguages_ShouldNotCallMethod() throws URISyntaxException {
        EuropeanDigitalCredentialDTO europeanDigitalCredentialDTO = JsonLdFactoryUtil.getSimpleCredential();
        europeanDigitalCredentialDTO.getDisplayParameter().setLanguage(null);
        credentialUtil.getAvailableLanguages(europeanDigitalCredentialDTO);
        Mockito.verify(controlledListCommonsService, Mockito.times(0)).searchLanguageISO639ByConcept(ArgumentMatchers.any(ConceptDTO.class));

    }

    @Test
    public void isAccreditedCredential_shouldReturnTrue() throws URISyntaxException {
        EuropeanDigitalCredentialDTO europeanDigitalCredentialDTO = JsonLdFactoryUtil.getSimpleCredential();
        ConceptDTO conceptDTO = new ConceptDTO(ControlledListConcept.CREDENTIAL_TYPE_ACCREDITATION.getUrl());
        List<ConceptDTO> conceptDTOList = new ArrayList<>();
        conceptDTOList.add(conceptDTO);
        europeanDigitalCredentialDTO.setCredentialProfiles(conceptDTOList);
        Assert.assertTrue(credentialUtil.isAccreditedCredential(europeanDigitalCredentialDTO));
    }

    @Test
    public void isAccreditedCredential_shouldReturnFalse() throws URISyntaxException {
        EuropeanDigitalCredentialDTO europeanDigitalCredentialDTO = JsonLdFactoryUtil.getSimpleCredential();
        ConceptDTO conceptDTO = new ConceptDTO(ControlledListConcept.CREDENTIAL_TYPE_CONVERTED.getUrl());
        List<ConceptDTO> conceptDTOList = new ArrayList<>();
        conceptDTOList.add(conceptDTO);
        europeanDigitalCredentialDTO.setCredentialProfiles(conceptDTOList);
        Assert.assertFalse(credentialUtil.isAccreditedCredential(europeanDigitalCredentialDTO));
    }

    @Test
    public void isCredentialProfilePresent_shouldReturnTrue() throws URISyntaxException {
        EuropeanDigitalCredentialDTO europeanDigitalCredentialDTO = JsonLdFactoryUtil.getSimpleCredential();
        ConceptDTO conceptDTO = new ConceptDTO(ControlledListConcept.CREDENTIAL_TYPE_ACCREDITATION.getUrl());
        List<ConceptDTO> conceptDTOList = new ArrayList<>();
        conceptDTOList.add(conceptDTO);
        europeanDigitalCredentialDTO.setCredentialProfiles(conceptDTOList);
        Assert.assertTrue(credentialUtil.isCredentialProfilePresent(europeanDigitalCredentialDTO, ControlledListConcept.CREDENTIAL_TYPE_ACCREDITATION));
    }

    @Test
    public void isCredentialProfilePresent_shouldReturnFalse() throws URISyntaxException {
        EuropeanDigitalCredentialDTO europeanDigitalCredentialDTO = JsonLdFactoryUtil.getSimpleCredential();
        ConceptDTO conceptDTO = new ConceptDTO(ControlledListConcept.CREDENTIAL_TYPE_CONVERTED.getUrl());
        List<ConceptDTO> conceptDTOList = new ArrayList<>();
        conceptDTOList.add(conceptDTO);
        europeanDigitalCredentialDTO.setCredentialProfiles(conceptDTOList);
        Assert.assertFalse(credentialUtil.isCredentialProfilePresent(europeanDigitalCredentialDTO, ControlledListConcept.CREDENTIAL_TYPE_ACCREDITATION));
    }

    @Test
    public void getClaimsOfClass_shouldReturnNotEmpty() throws URISyntaxException {
        Assert.assertFalse(credentialUtil.getClaimsOfClass(JsonLdFactoryUtil.getSimpleCredential(), LearningAchievementDTO.class).isEmpty());
    }

    @Test
    public void isAllowedType_shouldReturnTrue() {
        Assert.assertTrue(credentialUtil.isAllowedType("VerifiableCredential"));
    }

    @Test
    public void isAllowedType_shouldReturnTrue1() {
        Assert.assertTrue(credentialUtil.isAllowedType("EuropeanDigitalCredential"));
    }

    @Test
    public void isAllowedType_shouldReturnTrue2() {
        Assert.assertTrue(credentialUtil.isAllowedType("ConvertedCredential"));
    }

    @Test
    public void isAllowedType_shouldReturnFalse() {
        Assert.assertFalse(credentialUtil.isAllowedType("Verifiab"));
    }

    @Test
    public void doAddMissingIdentifiers_shouldCallMethods() throws URISyntaxException {
        credentialUtil.doAddMissingIdentifiers(JsonLdFactoryUtil.getSimpleCredential());

        Mockito.verify(reflectiveUtil, Mockito.times(1)).getInnerObjectsOfType(ArgumentMatchers.any(), ArgumentMatchers.any());
        Mockito.verify(reflectiveUtil, Mockito.times(1)).getTypesHashMap(ArgumentMatchers.any());

    }

    @Test
    public void doAddMissingIdentifiers_shouldHaveID() throws URISyntaxException {
        EuropeanDigitalCredentialDTO europeanDigitalCredentialDTO = JsonLdFactoryUtil.getSimpleCredential();
        europeanDigitalCredentialDTO.setId(null);
        Map<String, List<Object>> map = new HashMap();
        map.put(europeanDigitalCredentialDTO.getClass().getName(), Arrays.asList(europeanDigitalCredentialDTO));
        Mockito.doReturn(Arrays.asList(europeanDigitalCredentialDTO)).when(reflectiveUtil).getInnerObjectsOfType(ArgumentMatchers.any(), ArgumentMatchers.any());
        Mockito.doReturn(map).when(reflectiveUtil).getTypesHashMap(ArgumentMatchers.any());

        credentialUtil.doAddMissingIdentifiers(europeanDigitalCredentialDTO);

        Mockito.verify(reflectiveUtil, Mockito.times(1)).getInnerObjectsOfType(ArgumentMatchers.any(), ArgumentMatchers.any());
        Mockito.verify(reflectiveUtil, Mockito.times(1)).getTypesHashMap(ArgumentMatchers.any());

        Assert.assertNotNull(europeanDigitalCredentialDTO.getId());
    }


}
