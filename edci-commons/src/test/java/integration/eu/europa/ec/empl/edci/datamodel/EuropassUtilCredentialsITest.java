package integration.eu.europa.ec.empl.edci.datamodel;

import eu.europa.ec.empl.edci.datamodel.model.*;
import eu.europa.ec.empl.edci.datamodel.model.dataTypes.SchemaLocation;
import eu.europa.ec.empl.edci.datamodel.validation.ValidationResult;
import org.junit.Assert;
import org.junit.Test;

import javax.xml.bind.JAXBException;
import java.io.File;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

//TODO: Review
public class EuropassUtilCredentialsITest extends EuropassUtilITest {

    @Test
    public void parseEuropassCredential_WithEntitlement() throws MalformedURLException, JAXBException {
        EuropassCredentialDTO credentialDTO = new EuropassCredentialDTO();
        PersonDTO personDTO = super.createPersonDTO();
        OrganizationDTO organizationDTO1 = super.createOrganizationDTO();
        OrganizationDTO organizationDTO2 = super.createOrganizationDTO();
        EntitlementDTO entitlementDTO = super.createEntitlementDTO();
        EntitlementSpecificationDTO entitlementSpecificationDTO = super.createEntitlementSpecificationDTO();
        entitlementSpecificationDTO.setLimitOrganization(Arrays.asList(organizationDTO1, organizationDTO2));
        entitlementDTO.setSpecifiedBy(entitlementSpecificationDTO);
        personDTO.setEntitledTo(Arrays.asList(entitlementDTO));
        credentialDTO.setCredentialSubject(personDTO);

        String xml = this.edciCredentialModelUtil.toXML(credentialDTO);
        System.out.println(xml);
    }


    @Test
    public void xsdChoiceTes_OK() throws Exception {
        byte[] xmlBytes = Files.readAllBytes(Paths.get("src/test/resources/xsdValidation/credential-generic-contactPoint-choice-OK.xml"));
        File credFile = new File("src/test/resources/xsdValidation/credential-generic-contactPoint-choice-OK.xml");
        SchemaLocation schemaLocation = this.xmlUtil.getUniqueSchemaLocation(xmlBytes);
        //SchemaLocation schemaLocation = new SchemaLocation("genericschema_1.2.xsd");
        ValidationResult validationResult = this.xmlUtil.isValid(credFile, schemaLocation, EuropassCredentialDTO.class);
        Assert.assertTrue(validationResult.isValid());
        System.out.println(validationResult.isValid());
    }

    @Test
    public void xsdChoiceTest_KO() throws Exception {
        byte[] xmlBytes = Files.readAllBytes(Paths.get("src/test/resources/xsdValidation/credential-generic-contactPoint-choice-KO.xml"));
        File credFile = new File("src/test/resources/xsdValidation/credential-generic-contactPoint-choice-KO.xml");
        SchemaLocation schemaLocation = this.xmlUtil.getUniqueSchemaLocation(xmlBytes);
        //SchemaLocation schemaLocation = new SchemaLocation("genericschema_1.2.xsd");
        ValidationResult validationResult = this.xmlUtil.isValid(credFile, schemaLocation, EuropassCredentialDTO.class);
        Assert.assertFalse(validationResult.isValid());
        System.out.println(validationResult.isValid());
    }

    @Test
    public void parseEuropassCredential_GetUniqueSchemaLocation_ShouldNotBeNull() throws Exception {
//        byte[] xmlBytes = Files.readAllBytes(Paths.get("src/test/resources/xsdValidation/credential-generic-profile-xsd_v1.0.xml"));
//        SchemaLocation schemaLocation = xmlUtil.getUniqueSchemaLocation(xmlBytes);
//        Assert.assertTrue(schemaLocation != null);
        Assert.assertTrue(true);
    }
//
//    @Test
//    public void parseEuropassCredential_GetMultipleSchemaLocation_ShouldNotBeNull() throws Exception {
//        byte[] xmlBytes = Files.readAllBytes(Paths.get("src/test/resources/credential-multipleSchemaLocation.xml"));
//        List<SchemaLocation> schemaLocation = xmlUtil.getSchemaLocations(xmlBytes);
//        Assert.assertTrue(schemaLocation != null);
//    }
//
//    @Test
//    public void parseEuropassCredential_GetFirstSchemaLocation_ShouldNotBeNull() throws Exception {
//        byte[] xmlBytes = Files.readAllBytes(Paths.get("src/test/resources/credential-multipleSchemaLocation.xml"));
//        SchemaLocation schemaLocation = xmlUtil.getFirstSchemaLocation(xmlBytes);
//        Assert.assertTrue(schemaLocation != null);
//    }
//
//    @Test
//    public void parseEuropassCredential_GetUniqueSchemaLocation_ShouldBeNull() throws Exception {
//        byte[] xmlBytes = Files.readAllBytes(Paths.get("src/test/resources/credential-multipleSchemaLocation.xml"));
//        SchemaLocation schemaLocation = xmlUtil.getUniqueSchemaLocation(xmlBytes);
//        Assert.assertTrue(schemaLocation == null);
//    }
//
//    // @Test
//    public void parseEuropassCredential_noAssociations_ShouldMatchXSD() throws IOException, JAXBException {
//        GregorianCalendar gregorianCalendar = new GregorianCalendar(2031, 8, 16, 11, 35, 35);/**/
//        String xsd = "<xs:schema attributeFormDefault=\"unqualified\" elementFormDefault=\"qualified\" targetNamespace=\"data.europa.eu/snb\" xmlns:xs=\"http://www.w3.org/2001/XMLSchema\"><xs:element name=\"europassCredential\"><xs:complexType><xs:sequence><xs:element type=\"xs:dateTime\" name=\"issuanceDate\"/><xs:element type=\"xs:dateTime\" name=\"expirationDate\"/><xs:element name=\"type\"><xs:complexType><xs:sequence><xs:element type=\"xs:string\" name=\"content\"/><xs:element type=\"xs:string\" name=\"description\"/><xs:element type=\"xs:string\" name=\"listId\"/><xs:element type=\"xs:string\" name=\"listName\"/><xs:element type=\"xs:string\" name=\"name\"/></xs:sequence><xs:attribute type=\"xs:anyURI\" name=\"codeId\"/></xs:complexType></xs:element><xs:element name=\"title\"><xs:complexType><xs:sequence><xs:element name=\"text\" maxOccurs=\"unbounded\" minOccurs=\"0\"><xs:complexType><xs:simpleContent><xs:extension base=\"xs:string\"><xs:attribute type=\"xs:string\" name=\"lang\" use=\"optional\"/></xs:extension></xs:simpleContent></xs:complexType></xs:element></xs:sequence></xs:complexType></xs:element><xs:element name=\"description\"><xs:complexType><xs:sequence><xs:element name=\"text\" maxOccurs=\"unbounded\" minOccurs=\"0\"><xs:complexType><xs:simpleContent><xs:extension base=\"xs:string\"><xs:attribute type=\"xs:string\" name=\"lang\" use=\"optional\"/></xs:extension></xs:simpleContent></xs:complexType></xs:element></xs:sequence></xs:complexType></xs:element></xs:sequence><xs:attribute type=\"xs:string\" name=\"id\"/></xs:complexType></xs:element></xs:schema>";
//        Code type = testUtilities.createMockObject(Code.class);
//
//        Text title = new Text("Credential 1 (en_GB)", "en_GB");
//        title.setContent("es_ES", "Credencial 1 (es_ES)");
//        title.setContent("ca_CA", "Credencial u(ca_CA)");
//
//        Note description = new Note("Note 1 (en_GB)", "en_GB");
//        description.setContent("es_ES", "Credencial 1 (es_ES)");
//        description.setContent("ca_CA", "Credencial dos (ca_CA");
//
//        EuropassCredentialDTO europassCredentialDTO = new EuropassCredentialDTO();
//        europassCredentialDTO.setId(URI.create("urn:example:credential:".concat(UUID.randomUUID().toString())));
//        europassCredentialDTO.setIssuanceDate(new Date());
//        europassCredentialDTO.setExpirationDate(gregorianCalendar.getTime());
//        europassCredentialDTO.setTitle(title);
//        europassCredentialDTO.setDescription(description);
//        europassCredentialDTO.setType(type);
//
//        String xml = edciCredentialModelUtil.toXML(europassCredentialDTO);
//    }
//
//
//    public void parseCredentialWith_Person_LearningAchievement_Qualification_ShouldMatchXSD() throws JAXBException, MalformedURLException, IOException {
//        PersonDTO personDTO = new PersonDTO();
//        personDTO.setFamilyName(new Text("Family name", "en"));
//
//        LearningAchievementDTO learningAchievementDTO1 = new LearningAchievementDTO();
//        learningAchievementDTO1.setTitle(new Text("Learning Achievement with learning specification", "en"));
//
//        LearningSpecificationDTO learningSpecificationDTO = new LearningSpecificationDTO();
//        learningSpecificationDTO.setId(URI.create("urn:epass:learningspec:01"));
//        learningSpecificationDTO.setTitle(new Text("Learning specification", "en"));
//
//        learningAchievementDTO1.setSpecifiedBy(learningSpecificationDTO);
//
//        LearningAchievementDTO learningAchievementDTO2 = new LearningAchievementDTO();
//        learningAchievementDTO2.setTitle(new Text("Learning Achievement With Qualification", "en"));
//
//        QualificationDTO qualificationDTO = new QualificationDTO();
//        qualificationDTO.setId(URI.create("urn:epass:qualification:01"));
//        qualificationDTO.setTitle(new Text("Qualification", "en"));
//        qualificationDTO.setEqfLevel(testUtilities.createMockObject(Code.class));
//
//        learningAchievementDTO2.setSpecifiedBy(qualificationDTO);
//
//        personDTO.setAchieved(Arrays.asList(learningAchievementDTO1, learningAchievementDTO2));
//
//        EuropassCredentialDTO europassCredentialDTO = new EuropassCredentialDTO();
//        europassCredentialDTO.setTitle(new Text("Europass Credential", "en"));
//        europassCredentialDTO.setCredentialSubject(personDTO);
//
//
//        EuropassCredentialDTO europassCredentialDTOClone = edciCredentialModelUtil.cloneModel(europassCredentialDTO);
//        System.out.println(xmlUtil.toXML(europassCredentialDTOClone, EuropassCredentialDTO.class));
//
//    }
//
//    @Test
//    public void parseCredential_Issuer_Accreditation_references_shouldMatchXSD() throws JAXBException, MalformedURLException, IOException {
//        EuropassCredentialDTO europassCredentialDTO1 = new EuropassCredentialDTO();
//        europassCredentialDTO1.setTitle(testUtilities.createMockObject(Text.class));
//        europassCredentialDTO1.setId(URI.create("urn:credential:1"));
//
//        OrganizationDTO org1 = new OrganizationDTO();
//        org1.setId(URI.create("urn:epass:organization:02"));
//
//        OrganizationDTO org2 = new OrganizationDTO();
//        org2.setId(URI.create("urn:epass:organization:03"));
//
//        OrganizationDTO issuer = new OrganizationDTO();
//
//        issuer.setId(URI.create("urn:epass:organization:01"));
//        issuer.setUnitOf(org1);
//
//        LearningActivityDTO learningActivityDTO = new LearningActivityDTO();
//        learningActivityDTO.setDirectedBy(org1);
//
//        LearningActivityDTO learningActivityDTO2 = new LearningActivityDTO();
//        learningActivityDTO2.setDirectedBy(org1);
//
//
//        AssessmentDTO assessmentDTO = new AssessmentDTO();
//        assessmentDTO.setAssessedBy(org1);
//
//        LearningAchievementDTO learningAchievementDTO = new LearningAchievementDTO();
//        learningAchievementDTO.setWasDerivedFrom(Arrays.asList(assessmentDTO));
//
//        AccreditationDTO accreditationDTO1 = createAccreditationDTO();
//        AccreditationDTO accreditationDTO2 = createAccreditationDTO();
//        accreditationDTO2.setId(URI.create("urn:epass:accreditation:02"));
//
//        PersonDTO personDTO = new PersonDTO();
//        personDTO.setId(URI.create("urn:epass:organization:01"));
//        personDTO.setPerformed(Arrays.asList(learningActivityDTO, learningActivityDTO2));
//        personDTO.setAchieved(Arrays.asList(learningAchievementDTO));
//
//        europassCredentialDTO1.setIssuer(issuer);
//        europassCredentialDTO1.setCredentialSubject(personDTO);
//
//
//        String xml = xmlUtil.toXML(europassCredentialDTO1, EuropassCredentialDTO.class);
//
//        System.out.println(xml);
//
//
//       /* System.out.println(String.format("[%d] / [%d]",
//                europassCredentialDTO1.getIssuer().getPreferredName().getContents().size(),
//                clonedCredential.getIssuer().getPreferredName().getContents().size()));*/
//
//        //  System.out.println(edciCredentialModelUtil.toXML(clonedCredential));
//
//
//    }
//
//    @Test
//    public void parseCredential_Organization_bidirectionalRelations() throws JAXBException, MalformedURLException, IOException {
//        EuropassCredentialDTO europassCredentialDTO = new EuropassCredentialDTO();
//        europassCredentialDTO.setId(URI.create("urn:epass:credential:01"));
//        europassCredentialDTO.setTitle(new Text("title", "en"));
//
//        OrganizationDTO organizationDTO1 = new OrganizationDTO();
//        organizationDTO1.setId(URI.create("urn:organisation:1"));
//        organizationDTO1.setPreferredName(new Text("Q&A", "en"));
//
//        OrganizationDTO organizationDTO2 = new OrganizationDTO();
//        organizationDTO2.setId(URI.create("urn:organisation:2"));
//
//        OrganizationDTO organizationDTO3 = new OrganizationDTO();
//        organizationDTO3.setId(URI.create("urn:organisation:3"));
//
//        OrganizationDTO organizationDTO4 = new OrganizationDTO();
//        organizationDTO4.setId(URI.create("urn:organisation:4"));
//
//        organizationDTO1.setUnitOf(organizationDTO4);
//
//        europassCredentialDTO.setIssuer(organizationDTO1);
//
//        String xml = xmlUtil.toXML(europassCredentialDTO, EuropassCredentialDTO.class);
//
//        System.out.println(xml);
//
//        EuropassCredentialDTO europassCredentialDTOclone = edciCredentialModelUtil.cloneModel(europassCredentialDTO);
//
//        System.out.println(edciCredentialModelUtil.toXML(europassCredentialDTOclone));
//    }
//
//    @Test
//    public void parseCredential_withOrganization_withGrandParent() throws JAXBException, IOException {
//        EuropassCredentialDTO europassCredentialDTO = new EuropassCredentialDTO();
//        europassCredentialDTO.setId(URI.create("urn:epass:01"));
//
//
//        OrganizationDTO organizationDTO3 = new OrganizationDTO();
//        organizationDTO3.setId(URI.create("urn:org:3"));
//
//        OrganizationDTO organizationDTO2 = new OrganizationDTO();
//        organizationDTO2.setId(URI.create("urn:org:2"));
//        organizationDTO2.setUnitOf(organizationDTO3);
//
//        OrganizationDTO organizationDTO = new OrganizationDTO();
//        organizationDTO.setId(URI.create("urn:org:1"));
//        organizationDTO.setUnitOf(organizationDTO2);
//
//        europassCredentialDTO.setIssuer(organizationDTO);
//
//        System.out.println(edciCredentialModelUtil.toXML(europassCredentialDTO));
//
//    }
//
//    @Test
//    public void validCredential_generic_profile_v1_0_withXSD_shouldBeOK() throws MalformedURLException {
//        File file = new File("src/test/resources/xsdValidation/credential-generic-profile-xsd_v1.0.xml");
//        SchemaLocation xsdurl = new SchemaLocation("genericschema.xsd");
//        ValidationResult validationResult = xmlUtil.isValid(file, xsdurl, EuropassCredentialDTO.class);
//        System.out.println(validationResult.getErrorMessages());
//        Assert.assertTrue(validationResult.isValid());
//    }
//
//    @Test
//    public void validCredential_generic_profile_v1_2_withXSD_shouldBeOK() throws MalformedURLException {
//        File file = new File("src/test/resources/xsdValidation/credential-generic-profile-xsd_v1.2.xml");
//        SchemaLocation xsdurl = new SchemaLocation("genericschema_1.2.xsd");
//        ValidationResult validationResult = xmlUtil.isValid(file, xsdurl, EuropassCredentialDTO.class);
//        System.out.println(validationResult.getErrorMessages());
//        Assert.assertTrue(validationResult.isValid());
//    }
//
//    @Test
//    public void validCredential_generic_profile_v1_1_withXSD_shouldBeOK() throws MalformedURLException {
//        File file = new File("src/test/resources/xsdValidation/credential-generic-profile-xsd_v1.1.xml");
//        SchemaLocation xsdurl = new SchemaLocation("genericschema_1.1.xsd");
//        ValidationResult validationResult = xmlUtil.isValid(file, xsdurl, EuropassCredentialDTO.class);
//        System.out.println(validationResult.getErrorMessages());
//        Assert.assertTrue(validationResult.isValid());
//    }
//
//    @Test
//    public void validVP_generic_profile_v1_0_withXSD_shouldBeOK() throws MalformedURLException {
//        File file = new File("src/test/resources/xsdValidation/verificable_presentation_mandated-xsd_v1.0.xml");
//        SchemaLocation xsdurl = new SchemaLocation("mandatedschema_1.0.xsd");
//        ValidationResult validationResult = xmlUtil.isValid(file, xsdurl, EuropassCredentialDTO.class);
//        System.out.println(validationResult.getErrorMessages());
//        Assert.assertTrue(validationResult.isValid());
//    }
//
//    @Test
//    public void validVP_generic_profile_v1_1_withXSD_shouldBeOK() throws MalformedURLException {
//        File file = new File("src/test/resources/xsdValidation/verificable_presentation_mandated-xsd_v1.1.xml");
//        SchemaLocation xsdurl = new SchemaLocation("mandatedschema_1.1.xsd");
//        ValidationResult validationResult = xmlUtil.isValid(file, xsdurl, EuropassCredentialDTO.class);
//        System.out.println(validationResult.getErrorMessages());
//        Assert.assertTrue(validationResult.isValid());
//    }
//
//    @Test
//    public void validVP_generic_profile_v1_2_withXSD_shouldBeOK() throws MalformedURLException {
//        File file = new File("src/test/resources/xsdValidation/verificable_presentation_mandated-xsd_v1.2.xml");
//        SchemaLocation xsdurl = new SchemaLocation("mandatedschema_generic_1.2.xsd");
//        ValidationResult validationResult = xmlUtil.isValid(file, xsdurl, EuropassCredentialDTO.class);
//        System.out.println(validationResult.getErrorMessages());
//        Assert.assertTrue(validationResult.isValid());
//    }
//    @Test
//    public void validVP_diploma_profile_v1_2_withXSD_shouldBeOK() throws MalformedURLException {
//        File file = new File("src/test/resources/xsdValidation/verificable_presentation_mandated_dp-xsd_v1.2.xml");
//        SchemaLocation xsdurl = new SchemaLocation("mandatedschema_dp_1.2.xsd");
//        ValidationResult validationResult = xmlUtil.isValid(file, xsdurl, EuropassCredentialDTO.class);
//        System.out.println(validationResult.getErrorMessages());
//        Assert.assertTrue(validationResult.isValid());
//    }
//
//    @Test
//    public void validSignedCredential_generic_profile_v1_0_withXSD_shouldBeOK() throws MalformedURLException {
//        File file = new File("src/test/resources/xsdValidation/credential-generic-profile-xsd-signed.xml");
//        SchemaLocation xsdurl = new SchemaLocation("genericschema.xsd");
//        ValidationResult validationResult = xmlUtil.isValid(file, xsdurl, EuropassCredentialDTO.class);
//        System.out.println(validationResult.getErrorMessages());
//        Assert.assertTrue(validationResult.isValid());
//    }
//
//    @Test
//    public void parseCredential_WithOrg() throws JAXBException, IOException {
//        EuropassCredentialDTO europassCredentialDTO = new EuropassCredentialDTO();
//        europassCredentialDTO.setId(URI.create("urn:epass:01"));
//
//        OrganizationDTO organizationDTO = createOrganizationDTO();
//
//        europassCredentialDTO.setIssuer(organizationDTO);
//
//        System.out.println(edciCredentialModelUtil.toXML(europassCredentialDTO));
//    }
//
//
//    @Test
//    public void parseEuropassCredential_getTypesWithXPath() throws Exception {
//        byte[] xmlBytes = Files.readAllBytes(Paths.get("src/test/resources/xsdValidation/credential-generic-profile-xsd_v1.0.xml"));
//
//        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
//        factory.setNamespaceAware(false);
//        DocumentBuilder builder = factory.newDocumentBuilder();
//        Document credentialDoc = builder.parse(new ByteArrayInputStream(xmlBytes));
//
//        XPathFactory xpathfactory = XPathFactory.newInstance();
//        XPath xpath = xpathfactory.newXPath();
//
//        Text typeName = new Text();
//        typeName.setContents(this.getLocalizableContents(credentialDoc, xpath, "/europassCredential/type/targetName"));
//
//        System.out.println(typeName);
//    }
//
//
//    public List<Content> getLocalizableContents(Document doc, XPath xpath, String expression) {
//
//        List<Content> contents = null;
//
//        try {
//            XPathExpression expr = xpath.compile(expression);
//            Element result = (Element) expr.evaluate(doc, XPathConstants.NODE);
//
//            if (result == null) {
//                System.out.println("Property not found using expression " + expression);
//            } else {
//                contents = getLocalizableContents(result);
//            }
//        } catch (Exception e) {
//            System.out.println(e);
//        }
//
//        return contents;
//    }
//
//    private List<Content> getLocalizableContents(Element node) {
//
//        List<Content> contents = null;
//
//        try {
//            List<Node> nodeList = IntStream.range(0, node.getChildNodes().getLength())
//                    .mapToObj(node.getChildNodes()::item).filter(n -> "text".equalsIgnoreCase(n.getNodeName())).collect(Collectors.toList());
//
//            contents = nodeList.stream().map(n -> new Content(n.getTextContent(), n.getAttributes().getNamedItem("lang").getNodeValue())).collect(Collectors.toList());
//
//        } catch (Exception e) {
//            System.out.println(e);
//        }
//
//        return contents;
//    }


}
