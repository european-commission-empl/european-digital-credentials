package eu.europa.ec.empl.edci.issuer.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import eu.europa.ec.empl.edci.constants.ControlledListConcept;
import eu.europa.ec.empl.edci.datamodel.adapter.DurationJsonAdapter;
import eu.europa.ec.empl.edci.datamodel.model.EuropassCredentialDTO;
import eu.europa.ec.empl.edci.datamodel.model.dataTypes.Code;
import eu.europa.ec.empl.edci.datamodel.model.dataTypes.LegalIdentifier;
import eu.europa.ec.empl.edci.issuer.common.model.customization.CustomizedRecipientsDTO;
import eu.europa.ec.empl.edci.issuer.entity.specs.EuropassCredentialSpecDAO;
import eu.europa.ec.empl.edci.issuer.mapper.CredentialMapper;
import eu.europa.ec.empl.edci.issuer.mapper.datamodel.*;
import eu.europa.ec.empl.edci.issuer.util.EDCIWorkBookReader;
import eu.europa.ec.empl.edci.issuer.util.IssuerCustomizableModelUtil;
import eu.europa.ec.empl.edci.mapper.commons.StringDateMapping;
import eu.europa.ec.empl.edci.service.ControlledListCommonsService;
import eu.europa.ec.empl.edci.util.ReflectiveUtil;
import eu.europa.ec.empl.edci.util.Validator;
import eu.europa.ec.empl.edci.util.XmlUtil;
import integration.eu.europa.ec.empl.base.AbstractIntegrationBaseTest;
import org.joda.time.Period;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class IssuerCustomizedModelServiceITest extends AbstractIntegrationBaseTest {

    @Spy
    XmlUtil xmlUtil;

    @Spy
    private IssuerCustomizedModelService issuerCustomizedModelService;

    @Spy
    private EDCIWorkBookReader edciWorkBookReader;

    @Spy
    private Validator validator;

    @Spy
    ReflectiveUtil reflectiveUtil;

    @Mock
    ControlledListCommonsService controlledListCommonsService;

    @Spy
    private IssuerCustomizableModelUtil issuerCustomizableModelUtil;

    @Spy
    private CredentialMapper credentialMapper = Mappers.getMapper(CredentialMapper.class);

    @Spy
    private AgentOrganizationMapper agentOrganizationMapper = Mappers.getMapper(AgentOrganizationMapper.class);

    @Spy
    private VariousObjectsMapper variousObjectsMapper = Mappers.getMapper(VariousObjectsMapper.class);

    @Spy
    private StringDateMapping stringDateMapping = Mappers.getMapper(StringDateMapping.class);

    @Spy
    private LearningAchievementMapper learningAchievementMapper = Mappers.getMapper(LearningAchievementMapper.class);

    @Spy
    private EntitlementMapper entitlementMapper = Mappers.getMapper(EntitlementMapper.class);

    @Spy
    private LearningActivityMapper learningActivityMapper = Mappers.getMapper(LearningActivityMapper.class);

    @Spy
    private AssessmentMapper assessmentMapper = Mappers.getMapper(AssessmentMapper.class);

    private String generated_graded_template = "src/test/resources/customizableModel/generated_graded_2_recipients.xls";

    private String gradedCustomCredSpec = "src/test/resources/customizableModel/gradedCustomSpec.json";

    @Before
    public void setUp() {
        Mockito.lenient().when(issuerCustomizedModelService.getEdciWorkBookReader()).thenReturn(edciWorkBookReader);
        Mockito.lenient().when(edciWorkBookReader.getValidator()).thenReturn(validator);
        Mockito.lenient().doReturn("c44a699a-3768-40e8-be9e-f4ff753eceed").when(issuerCustomizableModelUtil).getCustomizableEntityIdentifierField(ArgumentMatchers.any(EuropassCredentialSpecDAO.class));

        Mockito.lenient().when(controlledListCommonsService.searchConceptByUri(
                ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.anyList())).thenReturn(new Code(ControlledListConcept.HUMAN_SEX_FEMALE.getUrl()));
        Mockito.lenient().when(controlledListCommonsService.searchCountryByEuvocField(
                ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.anyList()))
                .thenReturn(new Code("http://publications.europa.eu/resource/authority/country/BDI"), new Code("http://publications.europa.eu/resource/authority/country/ESP"));

        //Mocks
        issuerCustomizedModelService.setCredentialMapper(credentialMapper);
        issuerCustomizedModelService.setReflectiveUtil(reflectiveUtil);
        issuerCustomizedModelService.setStringDateMapping(stringDateMapping);
        issuerCustomizedModelService.setControlledListCommonsService(controlledListCommonsService);
        issuerCustomizedModelService.setIssuerCustomizableModelUtil(issuerCustomizableModelUtil);
        issuerCustomizableModelUtil.setReflectiveUtil(reflectiveUtil);
        reflectiveUtil.setValidator(validator);
        ReflectionTestUtils.setField(credentialMapper, "agentOrganizationMapper", agentOrganizationMapper);
        ReflectionTestUtils.setField(credentialMapper, "learningAchievementMapper", learningAchievementMapper);
        ReflectionTestUtils.setField(credentialMapper, "entitlementMapper", entitlementMapper);
        ReflectionTestUtils.setField(credentialMapper, "learningActivityMapper", learningActivityMapper);
        ReflectionTestUtils.setField(credentialMapper, "assessmentMapper", assessmentMapper);

        ReflectionTestUtils.setField(learningAchievementMapper, "learningActivityMapper", learningActivityMapper);
        ReflectionTestUtils.setField(learningAchievementMapper, "variousObjectsMapper", variousObjectsMapper);
        ReflectionTestUtils.setField(learningAchievementMapper, "agentOrganizationMapper", agentOrganizationMapper);
        ReflectionTestUtils.setField(learningAchievementMapper, "assessmentMapper", assessmentMapper);

        ReflectionTestUtils.setField(learningActivityMapper, "variousObjectsMapper", variousObjectsMapper);
        ReflectionTestUtils.setField(learningActivityMapper, "agentOrganizationMapper", agentOrganizationMapper);

        ReflectionTestUtils.setField(entitlementMapper, "variousObjectsMapper", variousObjectsMapper);
        ReflectionTestUtils.setField(entitlementMapper, "agentOrganizationMapper", agentOrganizationMapper);

        ReflectionTestUtils.setField(assessmentMapper, "variousObjectsMapper", variousObjectsMapper);
        ReflectionTestUtils.setField(assessmentMapper, "agentOrganizationMapper", agentOrganizationMapper);

        ReflectionTestUtils.setField(agentOrganizationMapper, "variousObjectsMapper", variousObjectsMapper);

    }

    @Test
    public void generateCustomizedRecipientsFromXLS_shouldHave2Recipients_whenUsingRecipientsOnlyXLS() throws Exception {
        MultipartFile multipartFile = new MockMultipartFile("test.xls", Files.readAllBytes(Paths.get(generated_graded_template)));

        CustomizedRecipientsDTO customizedRecipientsDTO = this.issuerCustomizedModelService.getRecipientsFromXLS(multipartFile);

        System.out.println(new Gson().toJson(customizedRecipientsDTO));

    }

    @Test
    public void xxxxxxxxxxxxxxxxxxxxxxxxxxxxxx() throws Exception {

        MultipartFile multipartFile = new MockMultipartFile("test.xls", Files.readAllBytes(Paths.get(generated_graded_template)));

        CustomizedRecipientsDTO customizedRecipientsDTO = this.issuerCustomizedModelService.getRecipientsFromXLS(multipartFile);

        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Period.class, new DurationJsonAdapter());
        Gson gson = gsonBuilder.create();
        EuropassCredentialSpecDAO europassCredentialSpecDAO = gson.fromJson(new FileReader(gradedCustomCredSpec), EuropassCredentialSpecDAO.class);

        EuropassCredentialDTO credentialDTO = issuerCustomizedModelService.fromCustomToDTO(europassCredentialSpecDAO, customizedRecipientsDTO).get(0);

        System.out.println(xmlUtil.toXML(credentialDTO, EuropassCredentialDTO.class));

    }

    @Test
    public void setLocalizableField() throws Exception {

        //Data
        MultipartFile multipartFile = new MockMultipartFile("test.xls", Files.readAllBytes(Paths.get(generated_graded_template)));
        CustomizedRecipientsDTO customizedRecipientsDTO = this.issuerCustomizedModelService.getRecipientsFromXLS(multipartFile);
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Period.class, new DurationJsonAdapter());
        Gson gson = gsonBuilder.create();
        EuropassCredentialSpecDAO europassCredentialSpecDAO = gson.fromJson(new FileReader(gradedCustomCredSpec), EuropassCredentialSpecDAO.class);
        EuropassCredentialDTO credentialDTO = issuerCustomizedModelService.getCredentialMapper().toDTO(europassCredentialSpecDAO);

        //Exec
        issuerCustomizedModelService.setLocalizableField(credentialDTO.getTitle(), "en", "Modified title", "en");

        //Assert
        Assert.assertEquals("Modified title", credentialDTO.getTitle().getContent("en").getContent());

    }

    @Test
    public void setObjectField() throws Exception {

        //Data
        MultipartFile multipartFile = new MockMultipartFile("test.xls", Files.readAllBytes(Paths.get(generated_graded_template)));
        CustomizedRecipientsDTO customizedRecipientsDTO = this.issuerCustomizedModelService.getRecipientsFromXLS(multipartFile);
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Period.class, new DurationJsonAdapter());
        Gson gson = gsonBuilder.create();
        EuropassCredentialSpecDAO europassCredentialSpecDAO = gson.fromJson(new FileReader(gradedCustomCredSpec), EuropassCredentialSpecDAO.class);
        EuropassCredentialDTO credentialDTO = issuerCustomizedModelService.getCredentialMapper().toDTO(europassCredentialSpecDAO);
        credentialDTO.getCredentialSubject().setNationalId(new LegalIdentifier());

        //Exec & Assert
        issuerCustomizedModelService.setObjectField(credentialDTO, "validFrom", "2022-08-19", "en", false);
        Assert.assertNotNull(credentialDTO.getValidFrom());

        issuerCustomizedModelService.setObjectField(credentialDTO, "validUntil", "2022-05-09T00:00:00+02:00", "en", false);
        Assert.assertNotNull(credentialDTO.getValidUntil());

        issuerCustomizedModelService.setObjectField(credentialDTO.getCredentialSubject(), "familyName", "RenamedName", "en", false);
        Assert.assertEquals("RenamedName", credentialDTO.getCredentialSubject().getFamilyName().getContent("en").getContent());

        issuerCustomizedModelService.setObjectField(credentialDTO.getCredentialSubject().getNationalId(), "content", "12341234J", "en", false);
        issuerCustomizedModelService.setObjectField(credentialDTO.getCredentialSubject().getNationalId(), "spatialId", "NIE", "en", false);
        Assert.assertEquals("12341234J", credentialDTO.getCredentialSubject().getNationalId().getContent());
        Assert.assertEquals("NIE", credentialDTO.getCredentialSubject().getNationalId().getSpatialId());

    }

    @Test
    public void setCodeField() throws Exception {

        //Data
        MultipartFile multipartFile = new MockMultipartFile("test.xls", Files.readAllBytes(Paths.get(generated_graded_template)));
        CustomizedRecipientsDTO customizedRecipientsDTO = this.issuerCustomizedModelService.getRecipientsFromXLS(multipartFile);
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Period.class, new DurationJsonAdapter());
        Gson gson = gsonBuilder.create();
        EuropassCredentialSpecDAO europassCredentialSpecDAO = gson.fromJson(new FileReader(gradedCustomCredSpec), EuropassCredentialSpecDAO.class);
        EuropassCredentialDTO credentialDTO = issuerCustomizedModelService.getCredentialMapper().toDTO(europassCredentialSpecDAO);
        credentialDTO.getCredentialSubject().setNationalId(new LegalIdentifier());

        List langs = new ArrayList<>();
        langs.add("en");
        langs.add("fr");

        //Exec & Assert
        issuerCustomizedModelService.setCodeField(credentialDTO.getCredentialSubject(), "citizenshipCountry", "US", "en", langs, true);
        Assert.assertNotNull(credentialDTO.getCredentialSubject().getCitizenshipCountry());

        issuerCustomizedModelService.setCodeField(credentialDTO.getCredentialSubject(), "gender", "F", "en", langs, false);
        Assert.assertNotNull(credentialDTO.getCredentialSubject().getGender());

    }


    @Test
    public void setFinalCollectionEntity_Code() throws Exception {

        //Data
        MultipartFile multipartFile = new MockMultipartFile("test.xls", Files.readAllBytes(Paths.get(generated_graded_template)));
        CustomizedRecipientsDTO customizedRecipientsDTO = this.issuerCustomizedModelService.getRecipientsFromXLS(multipartFile);
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Period.class, new DurationJsonAdapter());
        Gson gson = gsonBuilder.create();
        EuropassCredentialSpecDAO europassCredentialSpecDAO = gson.fromJson(new FileReader(gradedCustomCredSpec), EuropassCredentialSpecDAO.class);
        EuropassCredentialDTO credentialDTO = issuerCustomizedModelService.getCredentialMapper().toDTO(europassCredentialSpecDAO);
        credentialDTO.getCredentialSubject().setNationalId(new LegalIdentifier());

        List langs = new ArrayList<>();
        langs.add("en");
        langs.add("fr");

        Set<String> cleanedLists = new HashSet<>();

        //Exec & Assert
        issuerCustomizedModelService.setFinalCollectionEntity(credentialDTO.getCredentialSubject(), credentialDTO.getCredentialSubject().getCitizenshipCountry(),
                "citizenshipCountry", null, "UK", "en", credentialDTO, cleanedLists);
        Assert.assertEquals(1, credentialDTO.getCredentialSubject().getCitizenshipCountry().size());

        issuerCustomizedModelService.setFinalCollectionEntity(credentialDTO.getCredentialSubject(), credentialDTO.getCredentialSubject().getCitizenshipCountry(),
                "citizenshipCountry", null, "ES", "en", credentialDTO, cleanedLists);
        Assert.assertEquals(2, credentialDTO.getCredentialSubject().getCitizenshipCountry().size());

    }

    @Test
    public void setFinalCollectionEntity_Id_Lang() throws Exception {

        //Data
        MultipartFile multipartFile = new MockMultipartFile("test.xls", Files.readAllBytes(Paths.get(generated_graded_template)));
        CustomizedRecipientsDTO customizedRecipientsDTO = this.issuerCustomizedModelService.getRecipientsFromXLS(multipartFile);
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Period.class, new DurationJsonAdapter());
        Gson gson = gsonBuilder.create();
        EuropassCredentialSpecDAO europassCredentialSpecDAO = gson.fromJson(new FileReader(gradedCustomCredSpec), EuropassCredentialSpecDAO.class);
        EuropassCredentialDTO credentialDTO = issuerCustomizedModelService.getCredentialMapper().toDTO(europassCredentialSpecDAO);
        credentialDTO.getCredentialSubject().setNationalId(new LegalIdentifier());

        List langs = new ArrayList<>();
        langs.add("en");
        langs.add("fr");

        Set<String> cleanedLists = new HashSet<>();

        //Exec & Assert
        issuerCustomizedModelService.setFinalCollectionEntity(credentialDTO.getCredentialSubject().getAchievements().get(0), credentialDTO.getCredentialSubject().getAchievements().get(0).getAdditionalNote(),
                "additionalNote", "requirement", "Updated value", "en", credentialDTO, cleanedLists);
        Assert.assertTrue(credentialDTO.getCredentialSubject().getAchievements().get(0).getAdditionalNote().stream().anyMatch(note -> "Updated value".equals(note.getStringContent("en"))));

    }
}
