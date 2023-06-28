package eu.europa.ec.empl.edci.issuer.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import eu.europa.ec.empl.edci.datamodel.adapter.DurationJsonAdapter;
import eu.europa.ec.empl.edci.issuer.common.model.customization.*;
import eu.europa.ec.empl.edci.issuer.entity.specs.EuropassCredentialSpecDAO;
import eu.europa.ec.empl.edci.issuer.service.spec.EuropassCredentialSpecService;
import eu.europa.ec.empl.edci.issuer.util.EDCIWorkBookReader;
import eu.europa.ec.empl.edci.issuer.util.IssuerCustomizableModelUtil;
import eu.europa.ec.empl.edci.service.EDCIMessageService;
import eu.europa.ec.empl.edci.util.JsonLdUtil;
import eu.europa.ec.empl.edci.util.ReflectiveUtil;
import eu.europa.ec.empl.edci.util.Validator;
import integration.eu.europa.ec.empl.base.AbstractIntegrationBaseTest;
import org.joda.time.Period;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.springframework.context.MessageSource;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;

public class IssuerCustomizableModeServiceITest extends AbstractIntegrationBaseTest {

    @Spy
    private JsonLdUtil jsonLdUtil;

    @Spy
    private IssuerCustomizableModelService issuerCustomizableModelService;

    @Spy
    private EuropassCredentialSpecService europassCredentialSpecService;

    @Spy
    private ReflectiveUtil reflectiveUtil;

    @Spy
    private Validator validator;

    @Spy
    private EDCIMessageService edciMessageService;

    @Spy
    private MessageSource messageSource;

    @Spy
    private EDCIWorkBookReader edciWorkBookReader;

    @Spy
    private IssuerCustomizableModelUtil issuerCustomizableModelUtil;

    private String caseA_selection = "src/test/resources/customizableModel/case_a.json";
    private String caseA_response = "src/test/resources/customizableModel/case_a_response.json";
    private String caseB_selection = "src/test/resources/customizableModel/case_b.json";
    private String caseB_response = "src/test/resources/customizableModel/case_b_response.json";
    private String caseC_selection = "src/test/resources/customizableModel/case_c.json";
    private String caseC_response = "src/test/resources/customizableModel/case_c_response.json";
    private String relatesToCase_selection = "src/test/resources/customizableModel/case_relatesTo.json";
    private String relatesToDuplicatesCase_selection = "src/test/resources/customizableModel/case_relatesTo_duplicates.json";
    private String customCredSpec = "src/test/resources/customizableModel/customCredSpec.json";
    private String gradedCustomCredSpec = "src/test/resources/customizableModel/gradedCustomSpec.json";
    private String xls_selection = "src/test/resources/customizableModel/case_xls.json";
    private String xls_template = "src/test/resources/customizableModel/generated_template.xls";
    private String xls_template_graded = "src/test/resources/customizableModel/generated_graded_2_recipients.xls";

    @Before
    public void setUp() {
        Mockito.lenient().when(reflectiveUtil.getValidator()).thenReturn(validator);
        Mockito.lenient().when(edciMessageService.getMessageSource()).thenReturn(messageSource);
        // Mockito.lenient().when(edciMessageService.getMessage(ArgumentMatchers.anyString())).thenReturn("MockedMsg");

        Mockito.lenient().when(issuerCustomizableModelUtil.getValidator()).thenReturn(validator);
        Mockito.lenient().when(issuerCustomizableModelUtil.getEdciMessageService()).thenReturn(edciMessageService);
        Mockito.lenient().when(issuerCustomizableModelUtil.getReflectiveUtil()).thenReturn(reflectiveUtil);

        Mockito.lenient().when(issuerCustomizableModelService.getEdciWorkBookReader()).thenReturn(edciWorkBookReader);
        Mockito.lenient().when(issuerCustomizableModelService.getEuropassCredentialSpecService()).thenReturn(europassCredentialSpecService);
        Mockito.lenient().when(issuerCustomizableModelService.getReflectiveUtil()).thenReturn(reflectiveUtil);
        Mockito.lenient().when(issuerCustomizableModelService.getValidator()).thenReturn(validator);
        Mockito.lenient().when(issuerCustomizableModelService.getEdciMessageService()).thenReturn(edciMessageService);
        Mockito.lenient().when(issuerCustomizableModelService.getIssuerCustomizableModelUtil()).thenReturn(issuerCustomizableModelUtil);
    }

    @Test
    public void generateCredentialCustomizableInstanceSpecDTO_shouldHaveSameSizeThanJsonResponse_whenUsingCaseA() throws Exception {
        CustomizableSpecDTO customizableSpecDTO_caseA = new Gson().fromJson(new FileReader(caseA_selection), CustomizableSpecDTO.class);
        EuropassCredentialSpecDAO europassCredentialSpecDAO = new Gson().fromJson(new FileReader(customCredSpec), EuropassCredentialSpecDAO.class);
        Mockito.doReturn(true).when(europassCredentialSpecService).exists(2L);
        Mockito.doReturn(europassCredentialSpecDAO).when(europassCredentialSpecService).find(2L);

        CustomizableInstanceSpecDTO expectedCustomizableSpec = new Gson().fromJson(new FileReader(caseA_response), CustomizableInstanceSpecDTO.class);
        CustomizableInstanceSpecDTO credentialCustomizableSpec = this.issuerCustomizableModelService.getCustomizableInstanceSpec(customizableSpecDTO_caseA, 2L);
        Assert.assertEquals(expectedCustomizableSpec.getCustomizableInstanceDTOS().size(), credentialCustomizableSpec.getCustomizableInstanceDTOS().size());
    }

    @Test
    public void generateCredentialCustomizableInstanceSpecDTO_shouldContainAddressCountryAndNationalIdentifierSpatialId_whenUsingRelatesToCase() throws Exception {
        CustomizableSpecDTO customizableSpecDTO_relatesTo = new Gson().fromJson(new FileReader(relatesToCase_selection), CustomizableSpecDTO.class);
        EuropassCredentialSpecDAO europassCredentialSpecDAO = new Gson().fromJson(new FileReader(customCredSpec), EuropassCredentialSpecDAO.class);
        Mockito.doReturn(true).when(europassCredentialSpecService).exists(2L);
        Mockito.doReturn(europassCredentialSpecDAO).when(europassCredentialSpecService).find(2L);
        CustomizableInstanceSpecDTO credentialCustomizableSpec = this.issuerCustomizableModelService.getCustomizableInstanceSpec(customizableSpecDTO_relatesTo, 2L);
        Assert.assertEquals(credentialCustomizableSpec.getCustomizableInstanceDTOS().size(), 1);
        CustomizableInstanceDTO personalData = credentialCustomizableSpec.getCustomizableInstanceDTOS().iterator().next();
        Assert.assertEquals(personalData.getFields().size(), 6);
    }

    @Test
    public void generateCredentialCustomizableInstanceSpecDTO_shouldContainOnly4InstancesWithOneAddressCountryAndNationalIdentifierSpatialId_whenUsingRelatesToDuplicatesCase() throws Exception {
        CustomizableSpecDTO customizableSpecDTO_relatesTo_duplicates = new Gson().fromJson(new FileReader(relatesToDuplicatesCase_selection), CustomizableSpecDTO.class);
        EuropassCredentialSpecDAO europassCredentialSpecDAO = new Gson().fromJson(new FileReader(customCredSpec), EuropassCredentialSpecDAO.class);
        Mockito.doReturn(true).when(europassCredentialSpecService).exists(2L);
        Mockito.doReturn(europassCredentialSpecDAO).when(europassCredentialSpecService).find(2L);
        CustomizableInstanceSpecDTO credentialCustomizableSpec = this.issuerCustomizableModelService.getCustomizableInstanceSpec(customizableSpecDTO_relatesTo_duplicates, 2L);
        Assert.assertEquals(credentialCustomizableSpec.getCustomizableInstanceDTOS().size(), 1);
        CustomizableInstanceDTO personalData = credentialCustomizableSpec.getCustomizableInstanceDTOS().iterator().next();
        Assert.assertEquals(personalData.getFields().size(), 4);
    }

    @Test
    public void generateCredentialCustomizableInstanceSpecDTO_shouldHaveSameSizeThanJsonResponse_whenUsingCaseB() throws Exception {
        CustomizableSpecDTO customizableSpecDTO_caseB = new Gson().fromJson(new FileReader(caseB_selection), CustomizableSpecDTO.class);
        EuropassCredentialSpecDAO europassCredentialSpecDAO = new Gson().fromJson(new FileReader(customCredSpec), EuropassCredentialSpecDAO.class);
        Mockito.doReturn(true).when(europassCredentialSpecService).exists(2L);
        Mockito.doReturn(europassCredentialSpecDAO).when(europassCredentialSpecService).find(2L);

        CustomizableInstanceSpecDTO expectedCustomizableSpec = new Gson().fromJson(new FileReader(caseB_response), CustomizableInstanceSpecDTO.class);
        CustomizableInstanceSpecDTO credentialCustomizableSpec = this.issuerCustomizableModelService.getCustomizableInstanceSpec(customizableSpecDTO_caseB, 2L);
        Assert.assertEquals(expectedCustomizableSpec.getCustomizableInstanceDTOS().size(), credentialCustomizableSpec.getCustomizableInstanceDTOS().size());
    }

    @Test
    public void generateCredentialCustomizableSpecDTO_shouldHaveSameSizeThanJsonResponse_whenUsingCaseC() throws Exception {
        CustomizableSpecDTO customizableSpecDTO_caseC = new Gson().fromJson(new FileReader(caseC_selection), CustomizableSpecDTO.class);
        EuropassCredentialSpecDAO europassCredentialSpecDAO = new Gson().fromJson(new FileReader(customCredSpec), EuropassCredentialSpecDAO.class);
        Mockito.doReturn(true).when(europassCredentialSpecService).exists(2L);
        Mockito.doReturn(europassCredentialSpecDAO).when(europassCredentialSpecService).find(2L);

        CustomizableInstanceSpecDTO credentialCustomizableSpec = this.issuerCustomizableModelService.getCustomizableInstanceSpec(customizableSpecDTO_caseC, 2L);
        CustomizableInstanceSpecDTO expectedCustomizableSpec = new Gson().fromJson(new FileReader(caseC_response), CustomizableInstanceSpecDTO.class);
        Assert.assertEquals(expectedCustomizableSpec.getCustomizableInstanceDTOS().size(), credentialCustomizableSpec.getCustomizableInstanceDTOS().size());
    }

    @Test
    public void generateFullCredentialCustomizableInstanceDTO_shouldThrowNoExceptions() throws Exception {
        CustomizableSpecDTO fullCustomizableSpecDTO = this.issuerCustomizableModelService.getFullCustomizableSpecList();
        //ObjectMapper jacksonObjectMapper = JsonLdUtil.getJacksonObjectMapper();
        EuropassCredentialSpecDAO europassCredentialSpecDAO = jsonLdUtil.unMarshall(new FileInputStream(customCredSpec), EuropassCredentialSpecDAO.class);
        Mockito.doReturn(true).when(europassCredentialSpecService).exists(2L);
        Mockito.doReturn(europassCredentialSpecDAO).when(europassCredentialSpecService).find(2L);

        CustomizableInstanceSpecDTO customizableInstanceSpecDTO = this.issuerCustomizableModelService.getCustomizableInstanceSpec(fullCustomizableSpecDTO, 2L);
        System.out.println(new Gson().toJson(customizableInstanceSpecDTO));
    }

    @Test
    public void generateXLS_shouldGenerateXLS_whenUsingXLSSelection() throws Exception {
        EuropassCredentialSpecDAO europassCredentialSpecDAO = new Gson().fromJson(new FileReader(customCredSpec), EuropassCredentialSpecDAO.class);
        CustomizableSpecDTO customizableSpecDTO_caseC = new Gson().fromJson(new FileReader(xls_selection), CustomizableSpecDTO.class);
        Mockito.doReturn(true).when(europassCredentialSpecService).exists(2L);
        Mockito.doReturn(europassCredentialSpecDAO).when(europassCredentialSpecService).find(2L);
        try (FileOutputStream fileOutputStream = new FileOutputStream(xls_template)) {
            fileOutputStream.write(this.issuerCustomizableModelService.getCustomizableXLSTemplate(customizableSpecDTO_caseC, 2L));
        }
        Assert.assertTrue(new File(xls_template).exists());
    }

    @Test
    public void generateGradedXLS_shouldGenerateXLS_whenUsingGradedXLSSelection() throws Exception {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Period.class, new DurationJsonAdapter());
        Gson gson = gsonBuilder.create();
        EuropassCredentialSpecDAO europassCredentialSpecDAO = gson.fromJson(new FileReader(gradedCustomCredSpec), EuropassCredentialSpecDAO.class);
        CustomizableSpecDTO customizableSpecDTO_full = this.issuerCustomizableModelService.getFullCustomizableSpecList();
        Mockito.doReturn(true).when(europassCredentialSpecService).exists(3308L);
        Mockito.doReturn(europassCredentialSpecDAO).when(europassCredentialSpecService).find(3308L);
        try (FileOutputStream fileOutputStream = new FileOutputStream(xls_template_graded)) {
            fileOutputStream.write(this.issuerCustomizableModelService.getCustomizableXLSTemplate(customizableSpecDTO_full, 3308L));
        }
        Assert.assertTrue(new File(xls_template_graded).exists());
    }

    @Test
    public void generateCustomizableSpecDTO_shouldReturnNonEmptyCollection_whenCallingWithNoArgs() {
        CustomizableSpecDTO customizableSpecDTO = issuerCustomizableModelService.getFullCustomizableSpecList();
        System.out.println(new Gson().toJson(customizableSpecDTO));
        Assert.assertEquals(6, customizableSpecDTO.getCustomizableEntityDTOS().size());
    }

    @Test
    public void generateCustomizableEntity_shouldReturnDTO_whenUsingCredentialSpecClass() {
        CustomizableEntityDTO customizableEntityDTO = issuerCustomizableModelService.generateCustomizableEntity(EuropassCredentialSpecDAO.class);
        Assert.assertEquals("custom.entity.label.credential", customizableEntityDTO.getLabelKey());
        Assert.assertEquals(4, customizableEntityDTO.getFields().size());
        Assert.assertEquals(3, customizableEntityDTO.getRelations().size());
    }

    @Test
    public void generateCustomizableField_shouldReturnCorrectDTO_whenUsingCredentialSpecTitle() throws Exception {
        CustomizableFieldDTO customizableFieldDTO = issuerCustomizableModelService.generateCustomizableField(EuropassCredentialSpecDAO.class.getDeclaredField("title"));
        Assert.assertEquals("{\"position\":1,\"labelKey\":\"custom.field.credential.title\",\"fieldPath\":\"title($lang)\",\"mandatory\":false}", new Gson().toJson(customizableFieldDTO));
    }

    @Test
    public void generateCustomizableRelation_shouldReturnDTO_whenUsingCredentialSpecAchieved() throws Exception {
        CustomizableRelationDTO customizableRelationDTO = issuerCustomizableModelService.generateCustomizableRelation(EuropassCredentialSpecDAO.class.getDeclaredField("achieved"));
        Assert.assertEquals("{\"position\":5,\"labelKey\":\"custom.relation.credential.achievements\",\"relPath\":\"REC.achievements{$id}\"}", new Gson().toJson(customizableRelationDTO));
    }


}
