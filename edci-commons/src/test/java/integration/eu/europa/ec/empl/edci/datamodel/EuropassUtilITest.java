package integration.eu.europa.ec.empl.edci.datamodel;

import eu.europa.ec.empl.edci.constants.ControlledListConcept;
import eu.europa.ec.empl.edci.constants.MessageKeys;
import eu.europa.ec.empl.edci.datamodel.listener.EDCIJAXBMarshalListener;
import eu.europa.ec.empl.edci.datamodel.listener.EDCIJAXBUnmarshalListener;
import eu.europa.ec.empl.edci.datamodel.model.*;
import eu.europa.ec.empl.edci.datamodel.model.base.CredentialHolderDTO;
import eu.europa.ec.empl.edci.datamodel.model.dataTypes.*;
import eu.europa.ec.empl.edci.datamodel.model.verifiable.presentation.EuropassPresentationDTO;
import eu.europa.ec.empl.edci.datamodel.model.verifiable.presentation.VerificationCheckDTO;
import eu.europa.ec.empl.edci.service.ControlledListCommonsService;
import eu.europa.ec.empl.edci.service.EDCIMessageService;
import eu.europa.ec.empl.edci.service.RDFsparqlBridgeService;
import eu.europa.ec.empl.edci.util.EDCICredentialModelUtil;
import eu.europa.ec.empl.edci.util.JsonUtil;
import eu.europa.ec.empl.edci.util.Validator;
import eu.europa.ec.empl.edci.util.XmlUtil;
import integration.eu.europa.ec.empl.base.AbstractIntegrationBaseTest;
import org.joda.time.Period;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXParseException;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBException;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URI;
import java.util.*;

//TODO: Review
public class EuropassUtilITest extends AbstractIntegrationBaseTest {

    @InjectMocks
    protected EDCICredentialModelUtil edciCredentialModelUtil;

    @InjectMocks
    @Spy
    protected XmlUtil xmlUtil;

    @Spy
    private JsonUtil jsonUtil;

    @Mock
    protected EDCIMessageService edciMessageService;

    @Spy
    protected Validator validator;

    @Spy
    protected EDCIJAXBMarshalListener edcijaxbListener;

    @Spy
    protected EDCIJAXBUnmarshalListener edcijaxbUnmarshalListener;

    @Spy
    @InjectMocks
    private ControlledListCommonsService controlledListCommonsService;

    @Mock
    private RDFsparqlBridgeService rdfSparqlBridgeService;


    private static final String TYPE_ACREDITATION_URI = ControlledListConcept.VERIFICATION_CHECKS_ACCREDITATION.getUrl();
    private static final int STATUS_CODE_SKIPPED = 2;
    private static final String STATUS_SKIPPED_URI = "http://data.europa.eu/europass/verificationStatus/skip";

    @Before
    public void injectDependencies() {
        Mockito.lenient().when(edcijaxbListener.getReflectiveUtil()).thenReturn(reflectiveUtil);
        Mockito.lenient().when(edcijaxbListener.getEdciCredentialModelUtil()).thenReturn(edciCredentialModelUtil);
        Mockito.lenient().when(reflectiveUtil.getValidator()).thenReturn(validator);
        Mockito.lenient().when(edciMessageService.getMessage(MessageKeys.Exception.Global.GLOBAL_COLUMN)).thenReturn("column");
        Mockito.lenient().when(edciMessageService.getMessage(MessageKeys.Exception.Global.GLOBAL_LINE)).thenReturn("line");
        // Mockito.doReturn("certifies that").when(edciMessageService).getMessage(Mockito.anyString());
    }

    protected static final Logger _logger = LoggerFactory.getLogger(EuropassUtilITest.class);

    @Test
    public void test_XSD_v1_0() {
        SchemaFactory schemaFactory = SchemaFactory
                .newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);

        InputStream is = null;
        try {
            is = getClass().getClassLoader().getResourceAsStream("xsdValidation/edci_credential_0100.xml");
            Source xmlSource = new StreamSource(is);
            Schema schema = schemaFactory.newSchema();
            javax.xml.validation.Validator validator = schema.newValidator();
            validator.validate(xmlSource);
        } catch (SAXParseException e) {
            System.out.println(e.getMessage() + "\n " + e.toString());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {

                }
            }
        }
    }

    @Test
    public void parseAndClone_EuropassCredential_noAssociations_ShouldMatchXSD() throws IOException, JAXBException {
        GregorianCalendar gregorianCalendar = new GregorianCalendar(2031, 8, 16, 11, 35, 35);/**/
        String xsd = "<xs:schema attributeFormDefault=\"unqualified\" elementFormDefault=\"qualified\" targetNamespace=\"data.europa.eu/snb\" xmlns:xs=\"http://www.w3.org/2001/XMLSchema\"><xs:element name=\"europassCredential\"><xs:complexType><xs:sequence><xs:element type=\"xs:dateTime\" name=\"issuanceDate\"/><xs:element type=\"xs:dateTime\" name=\"expirationDate\"/><xs:element name=\"type\"><xs:complexType><xs:sequence><xs:element type=\"xs:string\" name=\"content\"/><xs:element type=\"xs:string\" name=\"description\"/><xs:element type=\"xs:string\" name=\"listId\"/><xs:element type=\"xs:string\" name=\"listName\"/><xs:element type=\"xs:string\" name=\"name\"/></xs:sequence><xs:attribute type=\"xs:anyURI\" name=\"codeId\"/></xs:complexType></xs:element><xs:element name=\"title\"><xs:complexType><xs:sequence><xs:element name=\"text\" maxOccurs=\"unbounded\" minOccurs=\"0\"><xs:complexType><xs:simpleContent><xs:extension base=\"xs:string\"><xs:attribute type=\"xs:string\" name=\"lang\" use=\"optional\"/></xs:extension></xs:simpleContent></xs:complexType></xs:element></xs:sequence></xs:complexType></xs:element><xs:element name=\"description\"><xs:complexType><xs:sequence><xs:element name=\"text\" maxOccurs=\"unbounded\" minOccurs=\"0\"><xs:complexType><xs:simpleContent><xs:extension base=\"xs:string\"><xs:attribute type=\"xs:string\" name=\"lang\" use=\"optional\"/></xs:extension></xs:simpleContent></xs:complexType></xs:element></xs:sequence></xs:complexType></xs:element></xs:sequence><xs:attribute type=\"xs:string\" name=\"id\"/></xs:complexType></xs:element></xs:schema>";
        Code type = new Code();
        type.setTargetDescription(new Text("target_description (en_GB)", "en_GB"));
        type.setTargetFramework(new Text("target_framework (en_GB)", "en_GB"));
        type.setTargetName(new Text("target_name (en_GB)", "en_GB"));
        type.setTargetNotation("target_notation");
        type.setUri("http://id.com/item");
        type.setTargetFrameworkURI("http://id.com");

        Text title = new Text("Credential 1 (en_GB)", "en_GB");
        title.setContent("es_ES", "Credencial 1 (es_ES)");
        title.setContent("ca_CA", "Credencial u(ca_CA)");

        Note description = new Note("Note 1 (en_GB)", "en_GB");
        description.setContent("es_ES", "Credencial 1 (es_ES)");
        description.setContent("ca_CA", "Credencial dos (ca_CA");

        EuropassCredentialDTO europassCredentialDTO = new EuropassCredentialDTO();
        europassCredentialDTO.setId(URI.create("urn:example:credential:".concat(UUID.randomUUID().toString())));
        europassCredentialDTO.setIssuanceDate(new Date());
        europassCredentialDTO.setExpirationDate(gregorianCalendar.getTime());
        europassCredentialDTO.setTitle(title);
        europassCredentialDTO.setDescription(description);
        europassCredentialDTO.setType(type);

//        String json = edciCredentialModelUtil.toCloneJSON(europassCredentialDTO, EuropassCredentialDTO.class);
//        EuropassCredentialDTO eup = edciCredentialModelUtil.fromCloneJSON(json, EuropassCredentialDTO.class);
        String xml = edciCredentialModelUtil.toXML(europassCredentialDTO);
        System.out.println("xml2: " + xml);
        // assertTrue(edciCredentialModelUtil.isValidXML(xml, xsd, EuropassCredentialDTO.class));
    }

    @Test
    public void generateXMLFromEuropassPresentation() throws IOException, JAXBException {

        File file = new File("src/test/resources/credential-withspecialchars.xml");

        EuropassCredentialDTO europassCredentialDTO = edciCredentialModelUtil.fromInputStream(new FileInputStream(file)).getCredential();

        EuropassPresentationDTO ep = new EuropassPresentationDTO();

        ep.setVerifiableCredential(europassCredentialDTO);
        ep.setId(URI.create(ep.getPrefix(ep).concat(UUID.randomUUID().toString())));
        ep.setVerifications(new ArrayList<>());

//        ep.setIssuer(createOrganizationDTO());
//        ep.getIssuer().setId(URI.create("urn:epass:organisation:02"));

        Text desc = null;
        desc = new Text("desc en", "en");
        desc.addContent("desc es", "es");
        VerificationCheckDTO vc = new VerificationCheckDTO();
        vc.setDescription(desc);
        //TODO vp cl, set status
//        vc.setStatus(ControlledListConcept.VERIFICATION_STATUS_SKIPPED);
//        vc.setType(ControlledListConcept.VERIFICATION_CHECKS_ACCREDITATION);
        vc.setStatus(new Code(ControlledListConcept.VERIFICATION_STATUS_SKIPPED.getUrl(),
                new Text("TODO vp cl", "en"), new Text("TODO vp cl", "en"),
                ControlledListConcept.VERIFICATION_STATUS_SKIPPED.getControlledList().getUrl(), "TODO vp cl",
                new Text("TODO vp cl", "en")));
        vc.setType(new Code(ControlledListConcept.VERIFICATION_CHECKS_ACCREDITATION.getUrl(),
                new Text("TODO vp cl", "en"), new Text("TODO vp cl", "en"),
                ControlledListConcept.VERIFICATION_CHECKS_ACCREDITATION.getControlledList().getUrl(), "TODO vp cl",
                new Text("TODO vp cl", "en")));

        ep.getVerifications().add(vc);

        ep.setType(edciCredentialModelUtil.getTypeCode(EuropassPresentationDTO.class, ControlledListConcept.VERIFICATION_TYPE_MANDATED_ISSUE.getUrl()));

        String xml = edciCredentialModelUtil.toXML(ep);
        System.out.println("xml: " + xml);

//        String schemaLocation = "src/test/resources/xsdValidation/vp-generic-profile.xsd";
//        String schemaLocation = "src/test/resources/xsdValidation/vp-generic-profile.xsd";
//
//        File file = new File("src/test/resources/xsdValidation/vp-generic-profile.xml");
//        Files.deleteIfExists(Paths.get(file.getAbsolutePath()));
//        Marshaller jaxbMarshaller = xmlUtil.getMarshallerWithSchemaLocation(schemaLocation);
//        jaxbMarshaller.marshal(ep, file);

        InputStream inputStream = new ByteArrayInputStream(xml.getBytes());
        CredentialHolderDTO credentialHolderDTO = edciCredentialModelUtil.fromInputStream(inputStream);

        xml = edciCredentialModelUtil.toXML(ep);
        System.out.println("##################################################################################################################");
        System.out.println("xml: " + xml);

        // assertTrue(edciCredentialModelUtil.isValidXML(xml, xsd, EuropassCredentialDTO.class));
    }


    public void parseFromXmlFile_withSpecialChars() throws JAXBException, MalformedURLException, IOException {
        File file = new File("src/test/resources/credential-withspecialchars.xml");

        EuropassCredentialDTO europassCredentialDTO = edciCredentialModelUtil.fromInputStream(new FileInputStream(file)).getCredential();

        System.out.println(edciCredentialModelUtil.toXML(europassCredentialDTO));
    }

    public void clonePerson_shouldHaveAllLocalizables() throws JAXBException, MalformedURLException, IOException {
        PersonDTO personDTO = createPersonDTO();

        PersonDTO clonedPersonDTO = edciCredentialModelUtil.cloneModel(personDTO);

        PersonDTO clonedPersonDTO2 = xmlUtil.fromString(xmlUtil.toXML(clonedPersonDTO, PersonDTO.class), PersonDTO.class);

        System.out.println(String.format("[%d] / [%d]",
                personDTO.getPreferredName().getContents().size(),
                clonedPersonDTO2.getPreferredName().getContents().size()));

        System.out.println(xmlUtil.toXML(clonedPersonDTO2, PersonDTO.class));


    }

    @Test
    public void testClone() throws JAXBException, MalformedURLException, IOException {
        cloneEntity_shouldHaveAllLocalizables(ScoringSchemeDTO.class);
    }

    public <T> void cloneEntity_shouldHaveAllLocalizables(Class<T> clazz) throws JAXBException, MalformedURLException, IOException {
        T entity = testUtilities.createMockObject(clazz);

        System.out.println(xmlUtil.toXML(entity, clazz));

        T clone = edciCredentialModelUtil.cloneModel(entity);
        System.out.println(xmlUtil.toXML(clone, clazz));
    }

    public void cloneText() throws JAXBException, MalformedURLException {
        Note note = testUtilities.createMockObject(Note.class);

        String xml = xmlUtil.toXML(note, Note.class);

        Note clonedNote = xmlUtil.fromString(xml, Note.class);

        System.out.println(xmlUtil.toXML(clonedNote, Note.class));
    }

    //UTILITIES TO CREATE REQUIRED DTOS FOR TESTS
    protected PersonDTO createPersonDTO() throws MalformedURLException {
        PersonDTO personDTO = new PersonDTO();
        GregorianCalendar issuedDate = new GregorianCalendar(2011, 5, 22, 16, 35, 35);/**/

        LegalIdentifier nationalId = testUtilities.createMockObject(LegalIdentifier.class);

        Identifier identifier1 = testUtilities.createMockObject(Identifier.class);
        Identifier identifier2 = testUtilities.createMockObject(Identifier.class);

        Code type1 = testUtilities.createMockObject(Code.class);
        Code type2 = testUtilities.createMockObject(Code.class);

        Text preferredName = testUtilities.createMockObject(Text.class);

        Text alternativeName1 = testUtilities.createMockObject(Text.class);
        Text alternativeName2 = testUtilities.createMockObject(Text.class);

        Note note1 = testUtilities.createMockObject(Note.class);
        Note note2 = testUtilities.createMockObject(Note.class);

        LocationDTO locationDTO1 = testUtilities.createMockObject(LocationDTO.class);
        LocationDTO locationDTO2 = testUtilities.createMockObject(LocationDTO.class);

        ContactPoint contactPoint1 = testUtilities.createMockObject(ContactPoint.class);
        ContactPoint contactPoint2 = testUtilities.createMockObject(ContactPoint.class);

        Text fullName = testUtilities.createMockObject(Text.class);
        Text givenName = testUtilities.createMockObject(Text.class);
        Text familyName = testUtilities.createMockObject(Text.class);

        Date dateOfBirth = testUtilities.createMockObject(Date.class);

        LocationDTO placeOfBirth = testUtilities.createMockObject(LocationDTO.class);

        Code gender = testUtilities.createMockObject(Code.class);

        Code citizenCode1 = testUtilities.createMockObject(Code.class);
        Code citizenCode2 = testUtilities.createMockObject(Code.class);

        personDTO.setIdentifier(Arrays.asList(identifier1, identifier2));
        personDTO.setType(Arrays.asList(type1, type2));
        personDTO.setPreferredName(preferredName);
        personDTO.setAlternativeName(Arrays.asList(alternativeName1, alternativeName2));
        personDTO.setNote(Arrays.asList(note1, note2));
        personDTO.setHasLocation(Arrays.asList(locationDTO1, locationDTO2));
        personDTO.setContactPoint(Arrays.asList(contactPoint1, contactPoint2));
        personDTO.setId(URI.create("urn:epass:person:01"));
        personDTO.setNationalId(nationalId);
        personDTO.setFullName(fullName);
        personDTO.setGivenNames(givenName);
        personDTO.setFamilyName(familyName);
        personDTO.setDateOfBirth(dateOfBirth);
        personDTO.setPlaceOfBirth(placeOfBirth);
        personDTO.setGender(gender);
        personDTO.setCitizenshipCountry(Arrays.asList(citizenCode1, citizenCode2));

        return personDTO;
    }

    protected OrganizationDTO createOrganizationDTO() throws JAXBException, MalformedURLException {
        OrganizationDTO organizationDTO = new OrganizationDTO();

        Identifier identifier1 = testUtilities.createMockObject(Identifier.class);
        Identifier identifier2 = testUtilities.createMockObject(Identifier.class);

        Code type1 = testUtilities.createMockObject(Code.class);
        Code type2 = testUtilities.createMockObject(Code.class);

        Text preferredName = testUtilities.createMockObject(Text.class);

        Text alternativeName1 = testUtilities.createMockObject(Text.class);
        Text alternativeName2 = testUtilities.createMockObject(Text.class);

        Note note1 = testUtilities.createMockObject(Note.class);
        Note note2 = testUtilities.createMockObject(Note.class);

        LocationDTO locationDTO1 = testUtilities.createMockObject(LocationDTO.class);
        LocationDTO locationDTO2 = testUtilities.createMockObject(LocationDTO.class);

        ContactPoint contactPoint1 = testUtilities.createMockObject(ContactPoint.class);
        ContactPoint contactPoint2 = testUtilities.createMockObject(ContactPoint.class);

        LegalIdentifier legalIdentifier = testUtilities.createMockObject(LegalIdentifier.class);

        LegalIdentifier vatIdentifier1 = testUtilities.createMockObject(LegalIdentifier.class);
        LegalIdentifier vatIdentifier2 = testUtilities.createMockObject(LegalIdentifier.class);

        LegalIdentifier taxIdentifier1 = testUtilities.createMockObject(LegalIdentifier.class);
        LegalIdentifier taxIdentifier2 = testUtilities.createMockObject(LegalIdentifier.class);

        WebDocumentDTO homePage1 = testUtilities.createMockObject(WebDocumentDTO.class);
        WebDocumentDTO homePage2 = testUtilities.createMockObject(WebDocumentDTO.class);

        MediaObject logo = testUtilities.createMockObject(MediaObject.class);

        organizationDTO.setId(URI.create("urn:epass:organisation:01"));
        organizationDTO.setIdentifier(Arrays.asList(identifier1, identifier2));
        organizationDTO.setType(Arrays.asList(type1, type2));
        organizationDTO.setPreferredName(preferredName);
        organizationDTO.setAlternativeName(Arrays.asList(alternativeName1, alternativeName2));
        organizationDTO.setNote(Arrays.asList(note1, note2));
        organizationDTO.setHasLocation(Arrays.asList(locationDTO1, locationDTO2));
        organizationDTO.setContactPoint(Arrays.asList(contactPoint1, contactPoint2));
        organizationDTO.setLegalIdentifier(legalIdentifier);
        organizationDTO.setVatIdentifier(Arrays.asList(vatIdentifier1, vatIdentifier2));
        organizationDTO.setTaxIdentifier(Arrays.asList(taxIdentifier1, taxIdentifier2));
        organizationDTO.setHomepage(Arrays.asList(homePage1, homePage2));
        organizationDTO.setLogo(logo);

        return organizationDTO;
    }

    protected AccreditationDTO createAccreditationDTO() throws MalformedURLException {
        AccreditationDTO accreditationDTO = new AccreditationDTO();

        Identifier identifier1 = testUtilities.createMockObject(Identifier.class);
        Identifier identifier2 = testUtilities.createMockObject(Identifier.class);

        Code accreditationType = testUtilities.createMockObject(Code.class);

        Text title = testUtilities.createMockObject(Text.class);

        Note description = testUtilities.createMockObject(Note.class);

        Score decision = testUtilities.createMockObject(Score.class);

        WebDocumentDTO report = testUtilities.createMockObject(WebDocumentDTO.class);

        Code limitField1 = testUtilities.createMockObject(Code.class);
        Code limitField2 = testUtilities.createMockObject(Code.class);

        Code limitEqfLevel1 = testUtilities.createMockObject(Code.class);
        Code limitEqfLevel2 = testUtilities.createMockObject(Code.class);

        Code limitJurisdiction1 = testUtilities.createMockObject(Code.class);
        Code limitJurisdiction2 = testUtilities.createMockObject(Code.class);

        Date issueDate = testUtilities.createMockObject(Date.class);
        Date reviewDate = testUtilities.createMockObject(Date.class);
        Date expiryDate = testUtilities.createMockObject(Date.class);

        Note additionalNote1 = testUtilities.createMockObject(Note.class);
        Note additionalNote2 = testUtilities.createMockObject(Note.class);

        WebDocumentDTO homePage1 = testUtilities.createMockObject(WebDocumentDTO.class);
        WebDocumentDTO homePage2 = testUtilities.createMockObject(WebDocumentDTO.class);

        WebDocumentDTO supplementaryDocument1 = testUtilities.createMockObject(WebDocumentDTO.class);
        WebDocumentDTO supplementaryDocument2 = testUtilities.createMockObject(WebDocumentDTO.class);


        accreditationDTO.setId(URI.create("urn:epass:accreditation:01"));
        accreditationDTO.setIdentifier(Arrays.asList(identifier1, identifier2));
        accreditationDTO.setAccreditationType(accreditationType);
        accreditationDTO.setTitle(title);
        accreditationDTO.setDescription(description);
        accreditationDTO.setDecision(decision);
        accreditationDTO.setReport(report);
        accreditationDTO.setLimitField(Arrays.asList(limitField1, limitField2));
        accreditationDTO.setLimitEqfLevel(Arrays.asList(limitEqfLevel1, limitEqfLevel2));
        accreditationDTO.setLimitJurisdiction(Arrays.asList(limitJurisdiction1, limitJurisdiction2));
        accreditationDTO.setIssueDate(issueDate);
        accreditationDTO.setReviewDate(reviewDate);
        accreditationDTO.setExpiryDate(expiryDate);
        accreditationDTO.setAdditionalNote(Arrays.asList(additionalNote1, additionalNote2));
        accreditationDTO.setHomePage(Arrays.asList(homePage1, homePage2));
        accreditationDTO.setSupplementaryDocument(Arrays.asList(supplementaryDocument1, supplementaryDocument2));

        return accreditationDTO;
    }

    protected LearningAchievementDTO createLearningAchievementDTO() throws MalformedURLException {
        LearningAchievementDTO learningAchievementDTO = new LearningAchievementDTO();

        Identifier identifier1 = testUtilities.createMockObject(Identifier.class);
        Identifier identifier2 = testUtilities.createMockObject(Identifier.class);

        Text title = testUtilities.createMockObject(Text.class);

        Note description = testUtilities.createMockObject(Note.class);

        Note note1 = testUtilities.createMockObject(Note.class);
        Note note2 = testUtilities.createMockObject(Note.class);

        learningAchievementDTO.setId(URI.create("urn:eppas:learningAchievementDTO:01"));
        learningAchievementDTO.setIdentifier(Arrays.asList(identifier1, identifier2));
        learningAchievementDTO.setTitle(title);
        learningAchievementDTO.setDescription(description);
        learningAchievementDTO.setAdditionalNote(Arrays.asList(note1, note2));

        return learningAchievementDTO;
    }

    protected AssessmentDTO createAssessmentDTO() throws MalformedURLException {
        AssessmentDTO assessmentDTO = new AssessmentDTO();

        Identifier identifier1 = testUtilities.createMockObject(Identifier.class);
        Identifier identifier2 = testUtilities.createMockObject(Identifier.class);

        Text title = testUtilities.createMockObject(Text.class);
        Text description = testUtilities.createMockObject(Text.class);

        Note additionalNote1 = testUtilities.createMockObject(Note.class);
        Note additionalNote2 = testUtilities.createMockObject(Note.class);

        Score grade = testUtilities.createMockObject(Score.class);

        ShortenedGradingDTO shortenedGradingDTO = testUtilities.createMockObject(ShortenedGradingDTO.class);
        ResultDistributionDTO resultDistributionDTO = testUtilities.createMockObject(ResultDistributionDTO.class);

        Date issuanceDate = testUtilities.createMockObject(Date.class);

        Code idVerification = testUtilities.createMockObject(Code.class);


        assessmentDTO.setId(URI.create("urn:epass:assessmentDTO:01"));
        assessmentDTO.setIdentifier(Arrays.asList(identifier1, identifier2));
        assessmentDTO.setTitle(title);
        assessmentDTO.setDescription(description);
        assessmentDTO.setAdditionalNote(Arrays.asList(additionalNote1, additionalNote2));
        assessmentDTO.setGrade(grade);
        assessmentDTO.setShortenedGrading(shortenedGradingDTO);
        assessmentDTO.setResultDistribution(resultDistributionDTO);
        assessmentDTO.setIssuedDate(issuanceDate);
        assessmentDTO.setIdVerification(idVerification);

        return assessmentDTO;
    }

    protected AssessmentSpecificationDTO createAssessmentSpecificationDTO() throws MalformedURLException {
        AssessmentSpecificationDTO assessmentSpecificationDTO = new AssessmentSpecificationDTO();

        Identifier identifier1 = testUtilities.createMockObject(Identifier.class);
        Identifier identifier2 = testUtilities.createMockObject(Identifier.class);

        Text title = testUtilities.createMockObject(Text.class);

        Text alternativeLabel1 = testUtilities.createMockObject(Text.class);
        Text alternativeLabel2 = testUtilities.createMockObject(Text.class);

        Note description = testUtilities.createMockObject(Note.class);

        Note additionalNote1 = testUtilities.createMockObject(Note.class);
        Note additionalNote2 = testUtilities.createMockObject(Note.class);

        WebDocumentDTO homePage1 = testUtilities.createMockObject(WebDocumentDTO.class);
        WebDocumentDTO homePage2 = testUtilities.createMockObject(WebDocumentDTO.class);

        WebDocumentDTO supplementaryDocument1 = testUtilities.createMockObject(WebDocumentDTO.class);
        WebDocumentDTO supplementaryDocument2 = testUtilities.createMockObject(WebDocumentDTO.class);

        Code language1 = testUtilities.createMockObject(Code.class);
        Code language2 = testUtilities.createMockObject(Code.class);

        Code mode = testUtilities.createMockObject(Code.class);

        ScoringSchemeDTO scoringSchemeDTO = testUtilities.createMockObject(ScoringSchemeDTO.class);

        assessmentSpecificationDTO.setId(URI.create("urn:assessmentSpecification:01"));
        assessmentSpecificationDTO.setIdentifier(Arrays.asList(identifier1, identifier2));
        assessmentSpecificationDTO.setTitle(title);
        assessmentSpecificationDTO.setAlternativeLabel(Arrays.asList(alternativeLabel1, alternativeLabel2));
        assessmentSpecificationDTO.setDescription(description);
        assessmentSpecificationDTO.setAdditionalNote(Arrays.asList(additionalNote1, additionalNote2));
        assessmentSpecificationDTO.setHomePage(Arrays.asList(homePage1, homePage2));
        assessmentSpecificationDTO.setSupplementaryDocument(Arrays.asList(supplementaryDocument1, supplementaryDocument2));
        assessmentSpecificationDTO.setLanguage(Arrays.asList(language1, language2));
        assessmentSpecificationDTO.setMode(mode);
        assessmentSpecificationDTO.setGradingScheme(scoringSchemeDTO);

        return assessmentSpecificationDTO;
    }

    protected LearningActivityDTO createLearningActivityDTO() throws MalformedURLException {
        LearningActivityDTO learningActivityDTO = new LearningActivityDTO();

        Identifier identifier1 = testUtilities.createMockObject(Identifier.class);
        Identifier identifier2 = testUtilities.createMockObject(Identifier.class);

        Text title = testUtilities.createMockObject(Text.class);
        Note description = testUtilities.createMockObject(Note.class);

        Note additionalNote1 = testUtilities.createMockObject(Note.class);
        Note additionalNote2 = testUtilities.createMockObject(Note.class);

        Period workload = testUtilities.createMockObject(Period.class);

        Date startedAtTime = testUtilities.createMockObject(Date.class);
        Date endedAtTime = testUtilities.createMockObject(Date.class);

        LocationDTO locationDTO1 = testUtilities.createMockObject(LocationDTO.class);
        LocationDTO locationDTO2 = testUtilities.createMockObject(LocationDTO.class);

        learningActivityDTO.setId(URI.create("urn:eppas:activity:01"));
        learningActivityDTO.setIdentifier(Arrays.asList(identifier1, identifier2));
        learningActivityDTO.setTitle(title);
        learningActivityDTO.setDescription(description);
        learningActivityDTO.setAdditionalNote(Arrays.asList(additionalNote1, additionalNote2));
        learningActivityDTO.setWorkload(workload);
        learningActivityDTO.setStartedAtTime(startedAtTime);
        learningActivityDTO.setEndedAtTime(endedAtTime);
        learningActivityDTO.setLocation(Arrays.asList(locationDTO1, locationDTO2));

        return learningActivityDTO;
    }

    protected LearningActivitySpecificationDTO createLearningActivitySpecificationDTO() throws MalformedURLException {
        LearningActivitySpecificationDTO learningActivitySpecificationDTO = new LearningActivitySpecificationDTO();

        Identifier identifier1 = testUtilities.createMockObject(Identifier.class);
        Identifier identifier2 = testUtilities.createMockObject(Identifier.class);

        Code learningActivityType1 = testUtilities.createMockObject(Code.class);
        Code learningActivityType2 = testUtilities.createMockObject(Code.class);

        Text title = testUtilities.createMockObject(Text.class);

        Text alternativeLabel1 = testUtilities.createMockObject(Text.class);
        Text alternativeLabel2 = testUtilities.createMockObject(Text.class);

        Note description = testUtilities.createMockObject(Note.class);

        Note additionalNote1 = testUtilities.createMockObject(Note.class);
        Note additionalNote2 = testUtilities.createMockObject(Note.class);

        WebDocumentDTO homePage1 = testUtilities.createMockObject(WebDocumentDTO.class);
        WebDocumentDTO homePage2 = testUtilities.createMockObject(WebDocumentDTO.class);

        WebDocumentDTO supplementaryDocument1 = testUtilities.createMockObject(WebDocumentDTO.class);
        WebDocumentDTO supplementaryDocument2 = testUtilities.createMockObject(WebDocumentDTO.class);

        Period workload = testUtilities.createMockObject(Period.class);

        Code language1 = testUtilities.createMockObject(Code.class);
        Code language2 = testUtilities.createMockObject(Code.class);

        Code mode = testUtilities.createMockObject(Code.class);

        learningActivitySpecificationDTO.setId(URI.create("urn:epass:learningActivitySpecification:01"));
        learningActivitySpecificationDTO.setIdentifier(Arrays.asList(identifier1, identifier2));
        learningActivitySpecificationDTO.setLearningActivityType(Arrays.asList(learningActivityType1, learningActivityType2));
        learningActivitySpecificationDTO.setTitle(title);
        learningActivitySpecificationDTO.setAlternativeLabel(Arrays.asList(alternativeLabel1, alternativeLabel2));
        learningActivitySpecificationDTO.setDescription(description);
        learningActivitySpecificationDTO.setAdditionalNote(Arrays.asList(additionalNote1, additionalNote2));
        learningActivitySpecificationDTO.setHomePage(Arrays.asList(homePage1, homePage2));
        learningActivitySpecificationDTO.setSupplementaryDocument(Arrays.asList(supplementaryDocument1, supplementaryDocument2));
        learningActivitySpecificationDTO.setWorkload(workload);
        learningActivitySpecificationDTO.setLanguage(Arrays.asList(language1, language2));
        return learningActivitySpecificationDTO;
    }

    protected LearningOpportunityDTO createLearningOpportunityDTO() throws JAXBException, MalformedURLException {
        LearningOpportunityDTO learningOpportunityDTO = new LearningOpportunityDTO();

        Identifier identifier1 = testUtilities.createMockObject(Identifier.class);
        Identifier identifier2 = testUtilities.createMockObject(Identifier.class);

        Text title = testUtilities.createMockObject(Text.class);

        Text alternativeLabel1 = testUtilities.createMockObject(Text.class);
        Text alternativeLabel2 = testUtilities.createMockObject(Text.class);

        Note description = testUtilities.createMockObject(Note.class);

        Note additionalNote1 = testUtilities.createMockObject(Note.class);
        Note additionalNote2 = testUtilities.createMockObject(Note.class);

        WebDocumentDTO homePage1 = testUtilities.createMockObject(WebDocumentDTO.class);
        WebDocumentDTO homePage2 = testUtilities.createMockObject(WebDocumentDTO.class);

        WebDocumentDTO supplementaryDocument1 = testUtilities.createMockObject(WebDocumentDTO.class);
        WebDocumentDTO supplementaryDocument2 = testUtilities.createMockObject(WebDocumentDTO.class);

        Date startedAtDate = testUtilities.createMockObject(Date.class);
        Date endedAtDate = testUtilities.createMockObject(Date.class);

        Period duration = testUtilities.createMockObject(Period.class);

        LocationDTO providedAt1 = testUtilities.createMockObject(LocationDTO.class);
        LocationDTO providedAt2 = testUtilities.createMockObject(LocationDTO.class);

        Code learningSchedule = testUtilities.createMockObject(Code.class);

        Note scheduleInformation = testUtilities.createMockObject(Note.class);

        Note admissionProcedure = testUtilities.createMockObject(Note.class);

        PriceDetailsDTO priceDetailsDTO1 = testUtilities.createMockObject(PriceDetailsDTO.class);
        PriceDetailsDTO priceDetailsDTO2 = testUtilities.createMockObject(PriceDetailsDTO.class);

        learningOpportunityDTO.setId(URI.create("urn:epass:leaningOpportunity:01"));
        learningOpportunityDTO.setIdentifier(Arrays.asList(identifier1, identifier2));
        learningOpportunityDTO.setTitle(title);
        learningOpportunityDTO.setAlternativeLabel(Arrays.asList(alternativeLabel1, alternativeLabel2));
        learningOpportunityDTO.setDescription(description);
        learningOpportunityDTO.setAdditionalNote(Arrays.asList(additionalNote1, additionalNote2));
        learningOpportunityDTO.setHomePage(Arrays.asList(homePage1, homePage2));
        learningOpportunityDTO.setSupplementaryDocument(Arrays.asList(supplementaryDocument1, supplementaryDocument2));
        learningOpportunityDTO.setStartedAtDate(startedAtDate);
        learningOpportunityDTO.setEndedAtDate(endedAtDate);
        learningOpportunityDTO.setDuration(duration);
        learningOpportunityDTO.setProvidedAt(Arrays.asList(providedAt1, providedAt2));
        learningOpportunityDTO.setLearningSchedule(learningSchedule);
        learningOpportunityDTO.setScheduleInformation(scheduleInformation);
        learningOpportunityDTO.setAdmissionProcedure(admissionProcedure);
        learningOpportunityDTO.setPriceDetails(Arrays.asList(priceDetailsDTO1, priceDetailsDTO2));

        return learningOpportunityDTO;
    }

    protected EntitlementDTO createEntitlementDTO() throws MalformedURLException {
        EntitlementDTO entitlementDTO = new EntitlementDTO();

        Identifier identifier1 = testUtilities.createMockObject(Identifier.class);
        Identifier identifier2 = testUtilities.createMockObject(Identifier.class);

        Text title = testUtilities.createMockObject(Text.class);
        Note description = testUtilities.createMockObject(Note.class);

        Date issuedDate = testUtilities.createMockObject(Date.class);
        Date expirationDate = testUtilities.createMockObject(Date.class);

        Note additionalNote1 = testUtilities.createMockObject(Note.class);
        Note additionalNote2 = testUtilities.createMockObject(Note.class);

        entitlementDTO.setId(URI.create("urn:epass:entitlement:1"));
        entitlementDTO.setIdentifier(Arrays.asList(identifier1, identifier2));
        entitlementDTO.setTitle(title);
        entitlementDTO.setDescription(description);
        entitlementDTO.setIssuedDate(issuedDate);
        entitlementDTO.setExpiryDate(expirationDate);
        entitlementDTO.setAdditionalNote(Arrays.asList(additionalNote1, additionalNote2));

        return entitlementDTO;
    }

    protected EntitlementSpecificationDTO createEntitlementSpecificationDTO() throws MalformedURLException {
        EntitlementSpecificationDTO entitlementSpecificationDTO = new EntitlementSpecificationDTO();

        Identifier identifier1 = testUtilities.createMockObject(Identifier.class);
        Identifier identifier2 = testUtilities.createMockObject(Identifier.class);

        Text title = testUtilities.createMockObject(Text.class);

        Text alternativeLabel1 = testUtilities.createMockObject(Text.class);
        Text alternativeLabel2 = testUtilities.createMockObject(Text.class);

        Note description = testUtilities.createMockObject(Note.class);

        Note additionalNote1 = testUtilities.createMockObject(Note.class);
        Note additionalNote2 = testUtilities.createMockObject(Note.class);

        WebDocumentDTO homePage1 = testUtilities.createMockObject(WebDocumentDTO.class);
        WebDocumentDTO homePage2 = testUtilities.createMockObject(WebDocumentDTO.class);

        Code status = testUtilities.createMockObject(Code.class);
        Code limitJurisdiction1 = testUtilities.createMockObject(Code.class);
        Code limitJurisdiction2 = testUtilities.createMockObject(Code.class);

        Code escoOccupationAssociation1 = testUtilities.createMockObject(Code.class);
        Code escoOccupationAssociation2 = testUtilities.createMockObject(Code.class);

        Code occupationAssociation1 = testUtilities.createMockObject(Code.class);
        Code occupationAssociation2 = testUtilities.createMockObject(Code.class);


        entitlementSpecificationDTO.setId(URI.create("urn:epass:entitlementspec:01"));
        entitlementSpecificationDTO.setIdentifier(Arrays.asList(identifier1, identifier2));
        entitlementSpecificationDTO.setTitle(title);
        entitlementSpecificationDTO.setAlternativeLabel(Arrays.asList(alternativeLabel1, alternativeLabel2));
        entitlementSpecificationDTO.setDescription(description);
        entitlementSpecificationDTO.setAdditionalNote(Arrays.asList(additionalNote1, additionalNote2));
        entitlementSpecificationDTO.setHomePage(Arrays.asList(homePage1, homePage2));
        entitlementSpecificationDTO.setStatus(status);
        entitlementSpecificationDTO.setLimitJurisdiction(Arrays.asList(limitJurisdiction1, limitJurisdiction2));
        entitlementSpecificationDTO.setLimitOccupation(Arrays.asList(escoOccupationAssociation1, escoOccupationAssociation2));
        entitlementSpecificationDTO.setLimitNationalOccupation(Arrays.asList(occupationAssociation1, occupationAssociation2));

        return entitlementSpecificationDTO;
    }

    protected LearningSpecificationDTO createLearningSpecificationDTO() throws MalformedURLException {
        LearningSpecificationDTO learningSpecificationDTO = new LearningSpecificationDTO();

        Identifier identifier1 = testUtilities.createMockObject(Identifier.class);
        Identifier identifier2 = testUtilities.createMockObject(Identifier.class);

        Code code1 = testUtilities.createMockObject(Code.class);
        Code code2 = testUtilities.createMockObject(Code.class);

        Text title = testUtilities.createMockObject(Text.class);

        Text alternativeLabel1 = testUtilities.createMockObject(Text.class);
        Text alternativeLabel2 = testUtilities.createMockObject(Text.class);

        Note definition = testUtilities.createMockObject(Note.class);
        Note learningOutcomeDescription = testUtilities.createMockObject(Note.class);

        Note additionalNote1 = testUtilities.createMockObject(Note.class);
        Note additionalNote2 = testUtilities.createMockObject(Note.class);

        WebDocumentDTO webDocumentDTO1 = testUtilities.createMockObject(WebDocumentDTO.class);
        WebDocumentDTO webDocumentDTO2 = testUtilities.createMockObject(WebDocumentDTO.class);

        WebDocumentDTO webDocumentDTO3 = testUtilities.createMockObject(WebDocumentDTO.class);
        WebDocumentDTO webDocumentDTO4 = testUtilities.createMockObject(WebDocumentDTO.class);

        Code code3 = testUtilities.createMockObject(Code.class);
        Code code4 = testUtilities.createMockObject(Code.class);

        Code educationSubjectAssociation1 = testUtilities.createMockObject(Code.class);
        Code educationSubjectAssociation2 = testUtilities.createMockObject(Code.class);


        Period volumeOfLearning = testUtilities.createMockObject(Period.class);

        Score ectsCreditPoints = testUtilities.createMockObject(Score.class);

        Score creditPoints1 = testUtilities.createMockObject(Score.class);
        Score creditPoints2 = testUtilities.createMockObject(Score.class);

        Code educationLevelAssociation1 = testUtilities.createMockObject(Code.class);
        Code educationLevelAssociation2 = testUtilities.createMockObject(Code.class);

        Code language1 = testUtilities.createMockObject(Code.class);
        Code language2 = testUtilities.createMockObject(Code.class);

        Code mode1 = testUtilities.createMockObject(Code.class);
        Code mode2 = testUtilities.createMockObject(Code.class);

        Code learningSetting = testUtilities.createMockObject(Code.class);

        Period maximumDuration = testUtilities.createMockObject(Period.class);

        Code targetGroup1 = testUtilities.createMockObject(Code.class);
        Code targetGroup2 = testUtilities.createMockObject(Code.class);

        Note entryRequirementNote = testUtilities.createMockObject(Note.class);

        learningSpecificationDTO.setId(URI.create("urn:epass:learningSpecification:01"));
        learningSpecificationDTO.setIdentifier(Arrays.asList(identifier1, identifier2));
        learningSpecificationDTO.setLearningOpportunityType(Arrays.asList(code1, code2));
        learningSpecificationDTO.setTitle(title);
        learningSpecificationDTO.setAlternativeLabel(Arrays.asList(alternativeLabel1, alternativeLabel2));
        learningSpecificationDTO.setDefinition(definition);
        learningSpecificationDTO.setLearningOutcomeDescription(learningOutcomeDescription);
        learningSpecificationDTO.setAdditionalNote(Arrays.asList(additionalNote1, additionalNote2));
        learningSpecificationDTO.setHomePage(Arrays.asList(webDocumentDTO1, webDocumentDTO2));
        learningSpecificationDTO.setSupplementaryDocument(Arrays.asList(webDocumentDTO3, webDocumentDTO4));
        learningSpecificationDTO.setIscedFCode(Arrays.asList(code3, code4));
        learningSpecificationDTO.setEducationSubject(Arrays.asList(educationSubjectAssociation1, educationSubjectAssociation2));
        learningSpecificationDTO.setVolumeOfLearning(volumeOfLearning);
        learningSpecificationDTO.setEctsCreditPoints(ectsCreditPoints);
        learningSpecificationDTO.setCreditPoints(Arrays.asList(creditPoints1, creditPoints2));
        learningSpecificationDTO.setEducationLevel(Arrays.asList(educationLevelAssociation1, educationLevelAssociation2));
        learningSpecificationDTO.setLanguage(Arrays.asList(language1, language2));
        learningSpecificationDTO.setMode(Arrays.asList(mode1, mode2));
        learningSpecificationDTO.setLearningSetting(learningSetting);
        learningSpecificationDTO.setMaximumDuration(maximumDuration);
        learningSpecificationDTO.setTargetGroup(Arrays.asList(targetGroup1, targetGroup2));
        learningSpecificationDTO.setEntryRequirementNote(entryRequirementNote);

        return learningSpecificationDTO;
    }

    protected AwardingProcessDTO createAwardingProcessDTO() throws MalformedURLException {
        AwardingProcessDTO awardingProcessDTO = new AwardingProcessDTO();

        Identifier identifier1 = testUtilities.createMockObject(Identifier.class);
        Identifier identifier2 = testUtilities.createMockObject(Identifier.class);

        Note description = testUtilities.createMockObject(Note.class);

        Note additionalNote1 = testUtilities.createMockObject(Note.class);
        Note additionalNote2 = testUtilities.createMockObject(Note.class);

        LocationDTO locationDTO = testUtilities.createMockObject(LocationDTO.class);
        Date awardingDate = testUtilities.createMockObject(Date.class);

        awardingProcessDTO.setId(URI.create("urn:epass:awardingProcess:01"));
        awardingProcessDTO.setIdentifier(Arrays.asList(identifier1, identifier2));
        awardingProcessDTO.setDescription(description);
        awardingProcessDTO.setAdditionalNote(Arrays.asList(additionalNote1, additionalNote2));
        awardingProcessDTO.setAwardingLocation(locationDTO);
        awardingProcessDTO.setAwardingDate(awardingDate);

        return awardingProcessDTO;
    }

    protected QualificationDTO createQualificationDTO() throws MalformedURLException {
        QualificationDTO qualificationDTO = new QualificationDTO(testUtilities.createMockObject(LearningSpecificationDTO.class));

        Code eqfLevel = testUtilities.createMockObject(Code.class);

        Code nqfLevel1 = testUtilities.createMockObject(Code.class);
        Code nqfLevel2 = testUtilities.createMockObject(Code.class);

        Boolean isPartialQualification = testUtilities.createMockObject(Boolean.class);

        Code qualificationAssociationTypeDTO1 = testUtilities.createMockObject(Code.class);
        Code qualificationAssociationTypeDTO2 = testUtilities.createMockObject(Code.class);

        qualificationDTO.setEqfLevel(eqfLevel);
        qualificationDTO.setNqfLevel(Arrays.asList(nqfLevel1, nqfLevel2));
        qualificationDTO.setIsPartialQualification(isPartialQualification);
        qualificationDTO.setQualificationCode(Arrays.asList(qualificationAssociationTypeDTO1, qualificationAssociationTypeDTO2));

        return qualificationDTO;
    }

    protected LearningOutcomeDTO createLearningOutcomeDTO() throws MalformedURLException {
        LearningOutcomeDTO learningOutcomeDTO = new LearningOutcomeDTO();

        Identifier identifier1 = testUtilities.createMockObject(Identifier.class);
        Identifier identifier2 = testUtilities.createMockObject(Identifier.class);

        Text name = testUtilities.createMockObject(Text.class);

        Note description = testUtilities.createMockObject(Note.class);

        Code learningOutcomeType = testUtilities.createMockObject(Code.class);
        Code reusabilityLevel = testUtilities.createMockObject(Code.class);

        Code escoSkillAssociationDTO1 = testUtilities.createMockObject(Code.class);
        Code escoSkillAssociationDTO2 = testUtilities.createMockObject(Code.class);

        learningOutcomeDTO.setId(URI.create("urn:epass:learningOutcome:"));
        learningOutcomeDTO.setIdentifier(Arrays.asList(identifier1, identifier2));
        learningOutcomeDTO.setName(name);
        learningOutcomeDTO.setDescription(description);
        learningOutcomeDTO.setLearningOutcomeType(learningOutcomeType);
        learningOutcomeDTO.setReusabilityLevel(reusabilityLevel);
        learningOutcomeDTO.setRelatedESCOSkill(Arrays.asList(escoSkillAssociationDTO1, escoSkillAssociationDTO2));


        return learningOutcomeDTO;
    }

    public AwardingOpportunityDTO createAwardingOpportunityDTO() throws MalformedURLException {
        AwardingOpportunityDTO awardingOpportunityDTO = new AwardingOpportunityDTO();

        Identifier identifier1 = testUtilities.createMockObject(Identifier.class);
        Identifier identifier2 = testUtilities.createMockObject(Identifier.class);

        Code location = testUtilities.createMockObject(Code.class);
        Date startDate = testUtilities.createMockObject(Date.class);
        Date endDate = testUtilities.createMockObject(Date.class);

        awardingOpportunityDTO.setId(URI.create("urn:awardingopportunity:01"));
        awardingOpportunityDTO.setIdentifier(Arrays.asList(identifier1, identifier2));
        awardingOpportunityDTO.setLocation(location);
        awardingOpportunityDTO.setStartDate(startDate);
        awardingOpportunityDTO.setEndDate(endDate);

        return awardingOpportunityDTO;
    }

}
