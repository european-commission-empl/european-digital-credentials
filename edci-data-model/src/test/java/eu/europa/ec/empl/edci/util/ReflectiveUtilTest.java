package eu.europa.ec.empl.edci.util;

import eu.europa.ec.empl.edci.annotation.CustomizableEntityDTO;
import eu.europa.ec.empl.edci.datamodel.jsonld.model.*;
import eu.europa.ec.empl.edci.datamodel.jsonld.model.dataTypes.Identifier;
import eu.europa.ec.empl.edci.datamodel.upload.EuropeanDigitalCredentialUploadDTO;
import eu.europa.ec.empl.edci.exception.ReflectiveException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.client.reactive.ClientHttpResponse;
import org.springframework.util.ReflectionUtils;

import javax.validation.constraints.NotNull;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.util.*;

@RunWith(MockitoJUnitRunner.Silent.class)
public class ReflectiveUtilTest {

    @InjectMocks
    @Spy
    protected ReflectiveUtil reflectiveUtil;

    @Mock
    protected ResourcesUtil resourcesUtil;

    @Mock
    protected Validator validator;

    @Test
    public void getAllClassesAnnotatedWith_shouldReturnNotEmpty() throws ClassNotFoundException {
        Assert.assertEquals(false, reflectiveUtil.getAllClassesAnnotatedWith(CustomizableEntityDTO.class, "eu.europa.ec.empl.edci").isEmpty());

    }

    @Test
    public void getAllFieldsAnnotatedWith_shouldReturnNotEmpty() {
        Assert.assertEquals(false, reflectiveUtil.getAllFieldsAnnotatedWith(VerifiableCredentialDTO.class, NotNull.class).isEmpty());

    }

    @Test
    public void isCollectionInstance_shouldReturnTrue() {
        Assert.assertEquals(true, reflectiveUtil.isCollectionInstance(new ArrayList<>()));

    }

    @Test
    public void isCollectionInstance_shouldReturnFalse() {
        Assert.assertEquals(false, reflectiveUtil.isCollectionInstance(new EuropeanDigitalCredentialDTO()));

    }

    @Test
    public void isCollectionInstance_field_shouldReturnTrue() throws NoSuchFieldException {
        Assert.assertEquals(true, reflectiveUtil.isCollectionInstance(VerifiableCredentialDTO.class.getDeclaredField("type")));

    }

    @Test
    public void isCollectionInstance_field_shouldReturnFalse() throws NoSuchFieldException {
        Assert.assertEquals(false, reflectiveUtil.isCollectionInstance(VerifiableCredentialDTO.class.getDeclaredField("id")));

    }

    @Test
    public void isListInstance_field_shouldReturnTrue() throws NoSuchFieldException {
        Assert.assertEquals(true, reflectiveUtil.isListInstance(VerifiableCredentialDTO.class.getDeclaredField("type")));

    }

    @Test
    public void isListInstance_field_shouldReturnFalse() throws NoSuchFieldException {
        Assert.assertEquals(false, reflectiveUtil.isListInstance(VerifiableCredentialDTO.class.getDeclaredField("id")));

    }

    @Test
    public void getOrInstanciateListItem_shouldReturnNotNull() throws ReflectiveOperationException {
        Assert.assertNotNull(reflectiveUtil.getOrInstantiateListItem(new ArrayList(), 0,
                VerifiableCredentialDTO.class.getDeclaredField("type")));

    }

    @Test
    public void getOrInstanciateListItem_shouldReturnFirstItem() throws ReflectiveOperationException, URISyntaxException {
        EuropeanDigitalCredentialDTO europeanDigitalCredentialDTO = JsonLdFactoryUtil.getSimpleCredential();
        Assert.assertEquals(europeanDigitalCredentialDTO, reflectiveUtil.getOrInstantiateListItem(Arrays.asList(europeanDigitalCredentialDTO), 0,
                VerifiableCredentialDTO.class.getDeclaredField("type")));

    }

    @Test
    public void setField_shouldReturnItem() throws ReflectiveOperationException, URISyntaxException {
        EuropeanDigitalCredentialDTO europeanDigitalCredentialDTO = JsonLdFactoryUtil.getSimpleCredential();
        Identifier identifier = new Identifier();
        List<Identifier> identifierList = Arrays.asList(identifier);
        Field field = europeanDigitalCredentialDTO.getClass().getDeclaredField("identifier");
        ReflectionUtils.makeAccessible(field);

        reflectiveUtil.setField(field, europeanDigitalCredentialDTO, identifierList);

        Assert.assertEquals(identifierList, europeanDigitalCredentialDTO.getIdentifier());
    }

    @Test
    public void setField_string_shouldReturnItem() throws ReflectiveOperationException, URISyntaxException {
        EuropeanDigitalCredentialDTO europeanDigitalCredentialDTO = JsonLdFactoryUtil.getSimpleCredential();
        Identifier identifier = new Identifier();
        List<Identifier> identifierList = Arrays.asList(identifier);
        Field field = europeanDigitalCredentialDTO.getClass().getDeclaredField("identifier");
        ReflectionUtils.makeAccessible(field);

        Mockito.doReturn(field).when(reflectiveUtil).findField(ArgumentMatchers.any(), ArgumentMatchers.anyString());

        reflectiveUtil.setField("identifier", europeanDigitalCredentialDTO, identifierList);

        Assert.assertEquals(identifierList, europeanDigitalCredentialDTO.getIdentifier());
        Mockito.verify(reflectiveUtil, Mockito.times(1)).findField(ArgumentMatchers.any(), ArgumentMatchers.anyString());
    }

    @Test(expected = ReflectiveException.class)
    public void setFieldBySetter_shouldThrowException() throws ReflectiveOperationException, URISyntaxException {
        EuropeanDigitalCredentialDTO europeanDigitalCredentialDTO = JsonLdFactoryUtil.getSimpleCredential();
        Identifier identifier = new Identifier();
        List<Identifier> identifierList = Arrays.asList(identifier);
        Field field = europeanDigitalCredentialDTO.getClass().getDeclaredField("identifier");
        ReflectionUtils.makeAccessible(field);

        Mockito.doReturn(field).when(reflectiveUtil).findField(ArgumentMatchers.any(), ArgumentMatchers.anyString());
        Mockito.doReturn(true).when(validator).isEmpty(ArgumentMatchers.any());

        reflectiveUtil.setFieldBySetter("identifier", europeanDigitalCredentialDTO, identifierList);

        Assert.assertEquals(identifierList, europeanDigitalCredentialDTO.getIdentifier());
        Mockito.verify(reflectiveUtil, Mockito.times(1)).findField(ArgumentMatchers.any(), ArgumentMatchers.anyString());
    }

    @Test
    public void setFieldBySetter_shouldReturnTrue() throws ReflectiveOperationException, URISyntaxException {
        EuropeanDigitalCredentialUploadDTO europeanDigitalCredentialUploadDTO = new EuropeanDigitalCredentialUploadDTO();

        Mockito.doReturn(false).when(validator).isEmpty(ArgumentMatchers.any());

        Assert.assertTrue(reflectiveUtil.setFieldBySetter("credential", europeanDigitalCredentialUploadDTO, EuropeanDigitalCredentialDTO.class.newInstance()));
    }

    @Test
    public void getCollectionTypeClass_shouldReturnNotNull() throws ReflectiveOperationException, URISyntaxException {
        EuropeanDigitalCredentialDTO europeanDigitalCredentialDTO = JsonLdFactoryUtil.getSimpleCredential();
        Field field = europeanDigitalCredentialDTO.getClass().getDeclaredField("identifier");
        ReflectionUtils.makeAccessible(field);

        Mockito.doReturn(false).when(validator).isEmpty(ArgumentMatchers.any());

        Assert.assertNotNull(reflectiveUtil.getCollectionTypeClass(field));
    }

    @Test
    public void getOrInstanceField_shouldReturnNotNull() throws ReflectiveOperationException, URISyntaxException {
        EuropeanDigitalCredentialDTO europeanDigitalCredentialDTO = JsonLdFactoryUtil.getSimpleCredential();
        Field field = europeanDigitalCredentialDTO.getClass().getDeclaredField("identifier");
        ReflectionUtils.makeAccessible(field);

        Assert.assertNotNull(reflectiveUtil.getOrInstanceField(field, europeanDigitalCredentialDTO, null));
    }

    @Test
    public void getOrInstanceField_shouldReturnNotNullElse() throws ReflectiveOperationException, URISyntaxException {
        EuropeanDigitalCredentialDTO europeanDigitalCredentialDTO = JsonLdFactoryUtil.getSimpleCredential();
        Field field = europeanDigitalCredentialDTO.getCredentialSubject().getHasClaim().get(0).getClass().getDeclaredField("OCBID");
        ReflectionUtils.makeAccessible(field);
        ClaimDTO claimDTO = new ClaimDTO();

        Mockito.doReturn(false).doReturn(true).when(reflectiveUtil).classContainsField(ArgumentMatchers.any(), ArgumentMatchers.any());

        Assert.assertNotNull(reflectiveUtil.getOrInstanceField(field, claimDTO
                , claimDTO));
    }

    @Test(expected = ReflectiveException.class)
    public void getOrInstanceField_shouldThrowException() throws ReflectiveOperationException, URISyntaxException {
        EuropeanDigitalCredentialDTO europeanDigitalCredentialDTO = JsonLdFactoryUtil.getSimpleCredential();
        Field field = europeanDigitalCredentialDTO.getCredentialSubject().getHasClaim().get(0).getClass().getDeclaredField("OCBID");
        ReflectionUtils.makeAccessible(field);
        ClaimDTO claimDTO = new ClaimDTO();

        Mockito.doReturn(false).when(reflectiveUtil).classContainsField(ArgumentMatchers.any(), ArgumentMatchers.any());

        reflectiveUtil.getOrInstanceField(field, claimDTO, claimDTO);
    }

    @Test
    public void getOrInstanceAnyField_shouldCallMethod() throws ReflectiveOperationException, URISyntaxException {
        EuropeanDigitalCredentialDTO europeanDigitalCredentialDTO = JsonLdFactoryUtil.getSimpleCredential();
        Field field = europeanDigitalCredentialDTO.getCredentialSubject().getHasClaim().get(0).getClass().getDeclaredField("OCBID");
        ReflectionUtils.makeAccessible(field);
        ClaimDTO claimDTO = new ClaimDTO();

        Mockito.doReturn(true).when(reflectiveUtil).isCollectionInstance(ArgumentMatchers.any());
        Mockito.doReturn(Arrays.asList(claimDTO)).when(reflectiveUtil).getOrInstanceCollectionField(ArgumentMatchers.any(), ArgumentMatchers.any());

        reflectiveUtil.getOrInstanceAnyField(field, claimDTO, claimDTO);

        Mockito.verify(reflectiveUtil, Mockito.times(1)).getOrInstanceCollectionField(ArgumentMatchers.any(), ArgumentMatchers.any());
        Mockito.verify(reflectiveUtil, Mockito.times(1)).isCollectionInstance(ArgumentMatchers.any());

    }

    @Test
    public void getOrInstanceAnyField_shouldCallInstanceField() throws ReflectiveOperationException, URISyntaxException {
        EuropeanDigitalCredentialDTO europeanDigitalCredentialDTO = JsonLdFactoryUtil.getSimpleCredential();
        Field field = europeanDigitalCredentialDTO.getCredentialSubject().getHasClaim().get(0).getClass().getDeclaredField("OCBID");
        ReflectionUtils.makeAccessible(field);

        Mockito.doReturn(false).when(reflectiveUtil).isCollectionInstance(ArgumentMatchers.any());

        reflectiveUtil.getOrInstanceAnyField(field, europeanDigitalCredentialDTO.getCredentialSubject().getHasClaim().get(0), null);

        Mockito.verify(reflectiveUtil, Mockito.times(1)).isCollectionInstance(ArgumentMatchers.any());
        Mockito.verify(reflectiveUtil, Mockito.times(0)).getOrInstanceCollectionField(ArgumentMatchers.any(), ArgumentMatchers.any());

    }

    @Test
    public void getOrInstanceAnyField_stringObject_shouldCallMethod() throws ReflectiveOperationException, URISyntaxException {
        EuropeanDigitalCredentialDTO europeanDigitalCredentialDTO = JsonLdFactoryUtil.getSimpleCredential();
        Field field = europeanDigitalCredentialDTO.getCredentialSubject().getHasClaim().get(0).getClass().getDeclaredField("OCBID");
        ReflectionUtils.makeAccessible(field);
        ClaimDTO claimDTO = new ClaimDTO();

        Mockito.doReturn(true).when(reflectiveUtil).isCollectionInstance(ArgumentMatchers.any());
        Mockito.doReturn(Arrays.asList(claimDTO)).when(reflectiveUtil).getOrInstanceCollectionField(ArgumentMatchers.any(), ArgumentMatchers.any());

        reflectiveUtil.getOrInstanceAnyField(field, claimDTO);

        Mockito.verify(reflectiveUtil, Mockito.times(1)).getOrInstanceCollectionField(ArgumentMatchers.any(), ArgumentMatchers.any());
        Mockito.verify(reflectiveUtil, Mockito.times(1)).isCollectionInstance(ArgumentMatchers.any());

    }

    @Test
    public void getOrInstanceAnyField_stringObject_shouldCallInstanceField() throws ReflectiveOperationException, URISyntaxException {
        EuropeanDigitalCredentialDTO europeanDigitalCredentialDTO = JsonLdFactoryUtil.getSimpleCredential();
        Field field = europeanDigitalCredentialDTO.getCredentialSubject().getHasClaim().get(0).getClass().getDeclaredField("OCBID");
        ReflectionUtils.makeAccessible(field);

        Mockito.doReturn(false).when(reflectiveUtil).isCollectionInstance(ArgumentMatchers.any());

        reflectiveUtil.getOrInstanceAnyField(field, europeanDigitalCredentialDTO.getCredentialSubject().getHasClaim().get(0));

        Mockito.verify(reflectiveUtil, Mockito.times(1)).isCollectionInstance(ArgumentMatchers.any());
        Mockito.verify(reflectiveUtil, Mockito.times(0)).getOrInstanceCollectionField(ArgumentMatchers.any(), ArgumentMatchers.any());
    }

    @Test
    public void hasParameterlessPublicConstructor_shouldReturnTrue() {
        Assert.assertTrue(reflectiveUtil.hasParameterlessPublicConstructor(EuropeanDigitalCredentialDTO.class));
    }

    @Test
    public void hasParameterlessPublicConstructor_shouldReturnFalse() {
        Assert.assertFalse(reflectiveUtil.hasParameterlessPublicConstructor(ClientHttpResponse.class));
    }

    @Test
    public void getField_shouldReturnNotNull() throws URISyntaxException, NoSuchFieldException {
        EuropeanDigitalCredentialDTO europeanDigitalCredentialDTO = JsonLdFactoryUtil.getSimpleCredential();
        ((LearningAchievementDTO) europeanDigitalCredentialDTO.getCredentialSubject().getHasClaim().get(0)).setOCBID("OCBID");
        Field field = europeanDigitalCredentialDTO.getCredentialSubject().getHasClaim().get(0).getClass().getDeclaredField("OCBID");
        Assert.assertNotNull(reflectiveUtil.getField(field, europeanDigitalCredentialDTO.getCredentialSubject().getHasClaim().get(0)));
    }

    @Test
    public void getField_String_shouldReturnNotNull() throws URISyntaxException {
        EuropeanDigitalCredentialDTO europeanDigitalCredentialDTO = JsonLdFactoryUtil.getSimpleCredential();
        ((LearningAchievementDTO) europeanDigitalCredentialDTO.getCredentialSubject().getHasClaim().get(0)).setOCBID("OCBID");
        Assert.assertNotNull(reflectiveUtil.getField("OCBID", europeanDigitalCredentialDTO.getCredentialSubject().getHasClaim().get(0)));
    }

    @Test
    public void getOrInstanceListField_shouldReturnNotEmpty() throws ReflectiveOperationException, URISyntaxException {
        EuropeanDigitalCredentialDTO europeanDigitalCredentialDTO = JsonLdFactoryUtil.getSimpleCredential();
        Field field = europeanDigitalCredentialDTO.getCredentialSubject().getHasClaim().get(0).getClass().getDeclaredField("hasPart");
        ReflectionUtils.makeAccessible(field);

        Assert.assertEquals(false, reflectiveUtil.getOrInstanceListField(field, europeanDigitalCredentialDTO.getCredentialSubject().getHasClaim().get(0)).isEmpty());

    }

    @Test
    public void getOrInstanceListField_shouldCallReturnEmpty() throws ReflectiveOperationException, URISyntaxException {
        EuropeanDigitalCredentialDTO europeanDigitalCredentialDTO = JsonLdFactoryUtil.getSimpleCredential();
        ((LearningAchievementDTO) europeanDigitalCredentialDTO.getCredentialSubject().getHasClaim().get(0)).setHasPart(null);

        Field field = europeanDigitalCredentialDTO.getCredentialSubject().getHasClaim().get(0).getClass().getDeclaredField("hasPart");
        ReflectionUtils.makeAccessible(field);

        Assert.assertEquals(true, reflectiveUtil.getOrInstanceListField(field, europeanDigitalCredentialDTO.getCredentialSubject().getHasClaim().get(0)).isEmpty());

    }

    @Test
    public void getOrInstanceCollectionField_shouldReturnNotEmpty() throws ReflectiveOperationException, URISyntaxException {
        EuropeanDigitalCredentialDTO europeanDigitalCredentialDTO = JsonLdFactoryUtil.getSimpleCredential();
        Field field = europeanDigitalCredentialDTO.getCredentialSubject().getHasClaim().get(0).getClass().getDeclaredField("hasPart");
        ReflectionUtils.makeAccessible(field);

        Assert.assertEquals(false, reflectiveUtil.getOrInstanceCollectionField(field, europeanDigitalCredentialDTO.getCredentialSubject().getHasClaim().get(0)).isEmpty());

    }

    @Test
    public void getOrInstanceCollectionField_shouldReturnEmpty() throws ReflectiveOperationException, URISyntaxException {
        EuropeanDigitalCredentialDTO europeanDigitalCredentialDTO = JsonLdFactoryUtil.getSimpleCredential();
        ((LearningAchievementDTO) europeanDigitalCredentialDTO.getCredentialSubject().getHasClaim().get(0)).setHasPart(null);

        Field field = europeanDigitalCredentialDTO.getCredentialSubject().getHasClaim().get(0).getClass().getDeclaredField("hasPart");
        ReflectionUtils.makeAccessible(field);

        Assert.assertEquals(true, reflectiveUtil.getOrInstanceCollectionField(field, europeanDigitalCredentialDTO.getCredentialSubject().getHasClaim().get(0)).isEmpty());

    }

    @Test
    public void findOrCreateInstanceOf_else_shouldCallReturnNotNull() throws ReflectiveOperationException, URISyntaxException {
        EuropeanDigitalCredentialDTO europeanDigitalCredentialDTO = JsonLdFactoryUtil.getSimpleCredential();

        List<Object> objectList = new ArrayList<>();
        objectList.addAll(europeanDigitalCredentialDTO.getCredentialSubject().getHasClaim());

        Assert.assertNotNull(reflectiveUtil.findOrCreateInstanceOf(objectList, LearningAchievementDTO.class));

    }

    @Test
    public void findOrCreateInstanceOf_shouldCallReturnNotNull() throws ReflectiveOperationException, URISyntaxException {
        EuropeanDigitalCredentialDTO europeanDigitalCredentialDTO = JsonLdFactoryUtil.getSimpleCredential();

        List<Object> objectList = new ArrayList<>();
        objectList.addAll(europeanDigitalCredentialDTO.getCredentialSubject().getHasClaim());

        Mockito.doReturn(true).when(validator).isEmpty(ArgumentMatchers.any());

        Assert.assertNotNull(reflectiveUtil.findOrCreateInstanceOf(objectList, LearningAchievementDTO.class));

    }

    @Test
    public void findOrCreateInstanceOf_stringCollection_shouldCallReturnNotNull() throws ReflectiveOperationException, URISyntaxException {
        EuropeanDigitalCredentialDTO europeanDigitalCredentialDTO = JsonLdFactoryUtil.getSimpleCredential();

        List<Object> objectList = new ArrayList<>();
        objectList.addAll(europeanDigitalCredentialDTO.getCredentialSubject().getHasClaim());

        Assert.assertNotNull(reflectiveUtil.findOrCreateInstanceOf(LearningAchievementDTO.class.getName(), objectList));

    }

    @Test
    public void findInstanceOf_stringCollection_shouldCallReturnNotNull() throws URISyntaxException {
        EuropeanDigitalCredentialDTO europeanDigitalCredentialDTO = JsonLdFactoryUtil.getSimpleCredential();

        List<Object> objectList = new ArrayList<>();
        objectList.addAll(europeanDigitalCredentialDTO.getCredentialSubject().getHasClaim());

        Assert.assertNotNull(reflectiveUtil.findInstanceOf(objectList, LearningAchievementDTO.class.getName()));

    }

    @Test
    public void isChildField_shouldCallReturnTrue() throws ReflectiveOperationException, URISyntaxException {
        EuropeanDigitalCredentialDTO europeanDigitalCredentialDTO = JsonLdFactoryUtil.getSimpleCredential();

        Field field = europeanDigitalCredentialDTO.getCredentialSubject().getHasClaim().get(0).getClass().getDeclaredField("hasPart");
        ReflectionUtils.makeAccessible(field);

        Assert.assertTrue(reflectiveUtil.isChildField(ClaimDTO.class, field));

    }

    @Test
    public void isChildField_shouldCallReturnFalse() throws ReflectiveOperationException, URISyntaxException {
        EuropeanDigitalCredentialDTO europeanDigitalCredentialDTO = JsonLdFactoryUtil.getSimpleCredential();

        Field field = europeanDigitalCredentialDTO.getCredentialSubject().getHasClaim().get(0).getClass().getDeclaredField("hasPart");
        ReflectionUtils.makeAccessible(field);

        Assert.assertFalse(reflectiveUtil.isChildField(EuropeanDigitalCredentialDTO.class, field));

    }

    @Test
    public void getFields_shouldReturnNonEmpty() throws URISyntaxException {
        EuropeanDigitalCredentialDTO europeanDigitalCredentialDTO = JsonLdFactoryUtil.getSimpleCredential();

        Assert.assertFalse(reflectiveUtil.getFields(europeanDigitalCredentialDTO.getCredentialSubject().getHasClaim().get(0)).isEmpty());

    }

    @Test
    public void getListFields_shouldReturnNonEmpty() throws  URISyntaxException {
        EuropeanDigitalCredentialDTO europeanDigitalCredentialDTO = JsonLdFactoryUtil.getSimpleCredential();

        Assert.assertFalse(reflectiveUtil.getListFields(europeanDigitalCredentialDTO.getCredentialSubject().getHasClaim().get(0)).isEmpty());

    }

    @Test
    public void findField_shouldReturnNotNull() throws  URISyntaxException {
        EuropeanDigitalCredentialDTO europeanDigitalCredentialDTO = JsonLdFactoryUtil.getSimpleCredential();

        Assert.assertNotNull(reflectiveUtil.findField(europeanDigitalCredentialDTO.getCredentialSubject().getHasClaim().get(0), LearningOpportunityDTO.class));

    }

    @Test
    public void findField_shouldReturnNull() throws  URISyntaxException {
        EuropeanDigitalCredentialDTO europeanDigitalCredentialDTO = JsonLdFactoryUtil.getSimpleCredential();

        Assert.assertNull(reflectiveUtil.findField(europeanDigitalCredentialDTO.getCredentialSubject().getHasClaim().get(0), LearningAchievementDTO.class));

    }

    @Test
    public void findField_classString_shouldReturnNotNull() {
        Assert.assertNotNull(reflectiveUtil.findField(LearningAchievementDTO.class, "hasPart"));

    }

    @Test(expected = ReflectiveException.class)
    public void findField_classString_shouldReturnException() {
        Mockito.doReturn(true).when(validator).isEmpty(ArgumentMatchers.any());

        Assert.assertNotNull(reflectiveUtil.findField(LearningAchievementDTO.class, "not.Present"));

    }

    @Test
    public void classContainsField_shouldReturnTrue() {
        Assert.assertTrue(reflectiveUtil.classContainsField(LearningAchievementDTO.class, "hasPart"));

    }

    @Test
    public void classContainsField_shouldReturnFalse() {
        Assert.assertFalse(reflectiveUtil.classContainsField(LearningAchievementDTO.class, "notPresent"));

    }

    @Test
    public void isPrimitiveList_shouldReturnTrue() throws NoSuchFieldException {
        Mockito.doReturn(String.class).when(reflectiveUtil).getCollectionTypeClass(ArgumentMatchers.any());
        Assert.assertTrue(reflectiveUtil.isPrimitiveList(VerifiableCredentialDTO.class.getDeclaredField("id")));
        Mockito.verify(reflectiveUtil, Mockito.times(1)).getCollectionTypeClass(ArgumentMatchers.any());

    }

    @Test
    public void getUniqueInnerObjectsOfType_shouldCallMethod() {
        Mockito.doReturn(Arrays.asList(new OrganisationDTO())).when(reflectiveUtil).getInnerObjectsOfType(ArgumentMatchers.any(), ArgumentMatchers.any());
        Assert.assertFalse(reflectiveUtil.getUniqueInnerObjectsOfType(VerifiableCredentialDTO.class, new OrganisationDTO()).isEmpty());
        Mockito.verify(reflectiveUtil, Mockito.times(1)).getInnerObjectsOfType(ArgumentMatchers.any(), ArgumentMatchers.any());

    }

    @Test
    public void getInnerObjectsOfType_shouldCallMethod() {
        Mockito.doReturn(Arrays.asList(new OrganisationDTO())).when(resourcesUtil).findAllObjectsInRecursively(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any());
        Assert.assertFalse(reflectiveUtil.getInnerObjectsOfType(VerifiableCredentialDTO.class, new OrganisationDTO()).isEmpty());
        Mockito.verify(resourcesUtil, Mockito.times(1)).findAllObjectsInRecursively(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any());

    }

    @Test
    public void getInnerObjectsOfType_filter_shouldCallMethod() {
        Mockito.doReturn(Arrays.asList(new OrganisationDTO())).when(resourcesUtil).findAllObjectsInRecursively(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any());
        Assert.assertFalse(reflectiveUtil.getInnerObjectsOfType(VerifiableCredentialDTO.class, new OrganisationDTO(), null).isEmpty());
        Mockito.verify(resourcesUtil, Mockito.times(1)).findAllObjectsInRecursively(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any());

    }

    @Test
    public void getUniqueInnerMethodsOfType_shouldCallMethod(){
        Map<Method, Set<Object>> map = new HashMap<>();
        map.put(null, new HashSet<>());
        Mockito.doReturn(map).when(resourcesUtil).findAllMethodsInRecursively(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any());
        Assert.assertFalse(reflectiveUtil.getUniqueInnerMethodsOfType(VerifiableCredentialDTO.class, new OrganisationDTO()).isEmpty());
        Mockito.verify(resourcesUtil, Mockito.times(1)).findAllMethodsInRecursively(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any());

    }

    @Test
    public void getTypesHashMap_shouldReturnNotEmpty() throws URISyntaxException {
        EuropeanDigitalCredentialDTO europeanDigitalCredentialDTO = JsonLdFactoryUtil.getSimpleCredential();

        Assert.assertFalse(reflectiveUtil.getTypesHashMap(europeanDigitalCredentialDTO.getCredentialSubject().getHasClaim()).isEmpty());

    }

}
