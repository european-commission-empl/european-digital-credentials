package eu.europa.ec.empl.edci.util;

import eu.europa.ec.empl.edci.datamodel.jsonld.model.EuropeanDigitalCredentialDTO;
import eu.europa.ec.empl.edci.datamodel.jsonld.model.LearningAchievementDTO;
import eu.europa.ec.empl.edci.datamodel.jsonld.model.PersonDTO;
import eu.europa.ec.empl.edci.datamodel.jsonld.model.dataTypes.LiteralMap;
import eu.europa.ec.empl.edci.exception.clientErrors.EDCIConflictException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.util.*;

@RunWith(MockitoJUnitRunner.Silent.class)
public class ResourcesUtilTest {

    @InjectMocks
    @Spy
    protected ResourcesUtil resourcesUtil;


    @Test
    public void checkLoopLine_shouldCallMethod() {
        Mockito.doNothing().when(resourcesUtil).checkLoopTree(ArgumentMatchers.any(),ArgumentMatchers.any(),ArgumentMatchers.any(),ArgumentMatchers.any());
        resourcesUtil.checkLoopLine(new EuropeanDigitalCredentialDTO(), europeanDigitalCredentialDTO -> europeanDigitalCredentialDTO, "fieldName");
        Mockito.verify(resourcesUtil, Mockito.times(1)).checkLoopTree(ArgumentMatchers.any(),ArgumentMatchers.any(),ArgumentMatchers.any(),ArgumentMatchers.any());

    }

    @Test
    public void checkLoopTree_shouldCallMethod() {
        Mockito.doNothing().when(resourcesUtil).checkLoopTree(ArgumentMatchers.any(),ArgumentMatchers.any(),ArgumentMatchers.any(),ArgumentMatchers.any());
        resourcesUtil.checkLoopTree(new EuropeanDigitalCredentialDTO(), europeanDigitalCredentialDTO -> Arrays.asList(europeanDigitalCredentialDTO), "fieldName");
        Mockito.verify(resourcesUtil, Mockito.times(1)).checkLoopTree(ArgumentMatchers.any(),ArgumentMatchers.any(),ArgumentMatchers.any(),ArgumentMatchers.any());

    }

    @Test(expected = EDCIConflictException.class)
    public void checkLoopTree_hashSet_shouldThrowException() {
        //The function is returning the same object so, it will be interpreted as a loop and a exception will be thrown
        resourcesUtil.checkLoopTree(new HashSet<>(), new EuropeanDigitalCredentialDTO(), europeanDigitalCredentialDTO -> Arrays.asList(europeanDigitalCredentialDTO), "fieldName");
    }

    @Test
    public void checkLoopTree_hashSet_shouldNotCallMethod() {
        resourcesUtil.checkLoopTree(new HashSet<>(), null, europeanDigitalCredentialDTO -> Arrays.asList(europeanDigitalCredentialDTO), "fieldName");
        //We set times to 1, because the first call from the test counts
        Mockito.verify(resourcesUtil, Mockito.times(1)).checkLoopTree(ArgumentMatchers.any(),ArgumentMatchers.any(),ArgumentMatchers.any(),ArgumentMatchers.any());

    }

    @Test
    public void checkContentClassLoopTree_shouldCallMethod() {
        Mockito.doNothing().when(resourcesUtil).checkContentClassLoopTree(ArgumentMatchers.any(),ArgumentMatchers.any());
        resourcesUtil.checkContentClassLoopTree(new EuropeanDigitalCredentialDTO());
        Mockito.verify(resourcesUtil, Mockito.times(1)).checkContentClassLoopTree(ArgumentMatchers.any(),ArgumentMatchers.any());

    }

    @Test
    public void checkContentClassLoopTree_linkedHashMap_shouldNotCallMethod() {
        resourcesUtil.checkContentClassLoopTree(new LinkedHashMap<>(), new EuropeanDigitalCredentialDTO());
        Mockito.verify(resourcesUtil, Mockito.times(0)).findAllObjectsIn(ArgumentMatchers.any(),ArgumentMatchers.any());

    }

    @Test
    public void checkContentClassLoopTree_linkedHashMap_shouldCallMethod() throws URISyntaxException {
        resourcesUtil.checkContentClassLoopTree(new LinkedHashMap<>(), JsonLdFactoryUtil.getBaseUncompletedVerifiableCredential());
        Mockito.verify(resourcesUtil, Mockito.atLeastOnce()).findAllObjectsIn(ArgumentMatchers.any(),ArgumentMatchers.any());

    }

    @Test(expected = EDCIConflictException.class)
    public void checkContentClassLoopTree_linkedHashMap_shouldThrowException() throws URISyntaxException {
        resourcesUtil.checkContentClassLoopTree(new LinkedHashMap<>(), JsonLdFactoryUtil.getCredentialWithLoops());
        Mockito.verify(resourcesUtil, Mockito.atLeastOnce()).findAllObjectsIn(ArgumentMatchers.any(),ArgumentMatchers.any());

    }

    @Test
    public void getCollectionFromMethod_shouldReturnNotEmpty() throws URISyntaxException, NoSuchMethodException {
        Method method = EuropeanDigitalCredentialDTO.class.getMethod("getType");
        Assert.assertFalse(resourcesUtil.getCollectionFromMethod(JsonLdFactoryUtil.getBaseUncompletedVerifiableCredential(), String.class, method).isEmpty());

    }

    @Test
    public void getCollectionFromMethod_shouldReturnNotThrowIllegalException() throws NoSuchMethodException {
        Method method = MockFactoryUtil.class.getMethod("throwIllegalAccessException");
        Assert.assertTrue(resourcesUtil.getCollectionFromMethod(new MockFactoryUtil(), String.class, method).isEmpty());

    }

    @Test
    public void getCollectionFromMethod_shouldReturnNotThrowInvocationException() throws NoSuchMethodException {
        Method method = MockFactoryUtil.class.getMethod("throwInvocationTargetException");
        Assert.assertTrue(resourcesUtil.getCollectionFromMethod(new MockFactoryUtil(), String.class, method).isEmpty());

    }

    @Test
    public void getObjectFromMethod_shouldReturnNotNull() throws NoSuchMethodException, URISyntaxException {
        Method method = EuropeanDigitalCredentialDTO.class.getMethod("getType");
        Assert.assertNotNull(resourcesUtil.getObjectFromMethod(JsonLdFactoryUtil.getBaseUncompletedVerifiableCredential(), method));

    }

    @Test
    public void getObjectFromMethod_shouldNotThrowIllegalException() throws NoSuchMethodException {
        Method method = MockFactoryUtil.class.getMethod("throwIllegalAccessException");
        Assert.assertNull(resourcesUtil.getObjectFromMethod(new MockFactoryUtil(), method));

    }

    @Test
    public void getObjectFromMethod_shouldNotThrowInvocationException() throws  NoSuchMethodException {
        Method method = MockFactoryUtil.class.getMethod("throwInvocationTargetException");
        Assert.assertNull(resourcesUtil.getObjectFromMethod(new MockFactoryUtil(), method));

    }

    @Test
    public void findAllObjectsIn_shouldReturnEmpty() throws URISyntaxException {
            Assert.assertTrue(resourcesUtil.findAllObjectsIn(JsonLdFactoryUtil.getBaseUncompletedVerifiableCredential(), LearningAchievementDTO.class).isEmpty());

    }

    @Test
    public void findAllObjectsIn_shouldReturnNotEmpty() throws URISyntaxException {
        Assert.assertFalse(resourcesUtil.findAllObjectsIn(JsonLdFactoryUtil.getBaseUncompletedVerifiableCredential(), List.class).isEmpty());

    }

    @Test
    public void findAllObjectsInRecursively_objectClassFilterClass_shouldCallMethod() throws URISyntaxException {
        Mockito.doReturn(new ArrayList<>()).when(resourcesUtil).findAllObjectsInRecursively(ArgumentMatchers.any(), ArgumentMatchers.any(),
                ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any());

        resourcesUtil.findAllObjectsInRecursively(JsonLdFactoryUtil.getBaseUncompletedVerifiableCredential(),
                LearningAchievementDTO.class, null, null);

        Mockito.verify(resourcesUtil, Mockito.times(1)).findAllObjectsInRecursively(ArgumentMatchers.any(), ArgumentMatchers.any(),
                ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any());
    }

    @Test
    public void findAllObjectsInRecursively_objectClassClass_shouldCallMethod() throws URISyntaxException {
        Mockito.doReturn(new ArrayList<>()).when(resourcesUtil).findAllObjectsInRecursively(ArgumentMatchers.any(), ArgumentMatchers.any(),
                ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any());

        resourcesUtil.findAllObjectsInRecursively(JsonLdFactoryUtil.getBaseUncompletedVerifiableCredential(),
                LearningAchievementDTO.class, LearningAchievementDTO.class);

        Mockito.verify(resourcesUtil, Mockito.times(1)).findAllObjectsInRecursively(ArgumentMatchers.any(), ArgumentMatchers.any(),
                ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any());
    }

    @Test
    public void findAllObjectsInRecursively_objectListClassFilterClass_shouldReturnNotEmpty() throws URISyntaxException {
        List<LiteralMap> auxList = new ArrayList<>();

        resourcesUtil.findAllObjectsInRecursively(JsonLdFactoryUtil.getBaseUncompletedVerifiableCredential(), auxList,
                LiteralMap.class, null, PersonDTO.class);

        Assert.assertFalse(auxList.isEmpty());

    }

    @Test
    public void findAllObjectsInRecursively_objectListClassFilterClass_shouldReturnEmpty() throws URISyntaxException {
        List<LiteralMap> auxList = new ArrayList<>();

        resourcesUtil.findAllObjectsInRecursively(JsonLdFactoryUtil.getBaseUncompletedVerifiableCredential(), auxList,
                LiteralMap.class, null, Mockito.class);

        Assert.assertTrue(auxList.isEmpty());

    }

    @Test
    public void findAllMethodsIn_shouldReturnNotEmpty() throws URISyntaxException {
        Assert.assertFalse(resourcesUtil.findAllMethodsIn(JsonLdFactoryUtil.getBaseUncompletedVerifiableCredential(), LiteralMap.class).isEmpty());

    }

    @Test
    public void findAllMethodsInRecursively_objectClassClass_shouldCallMethod() throws URISyntaxException {
        Mockito.doReturn(new ArrayList<>()).when(resourcesUtil).findAllMethodsInRecursively(ArgumentMatchers.any(), ArgumentMatchers.any(),
                ArgumentMatchers.any(), ArgumentMatchers.any());

        resourcesUtil.findAllMethodsInRecursively(JsonLdFactoryUtil.getBaseUncompletedVerifiableCredential(),
                LearningAchievementDTO.class, LearningAchievementDTO.class);

        Mockito.verify(resourcesUtil, Mockito.times(1)).findAllMethodsInRecursively(ArgumentMatchers.any(), ArgumentMatchers.any(),
                ArgumentMatchers.any(), ArgumentMatchers.any());
    }

    @Test
    public void findAllMethodsInRecursively_objectListClassFilterClass_shouldReturnEmpty() throws URISyntaxException {
        HashMap<Method, Set<Object>>  auxList = new HashMap<>();

        resourcesUtil.findAllMethodsInRecursively(JsonLdFactoryUtil.getBaseUncompletedVerifiableCredential(), auxList,
                LiteralMap.class, PersonDTO.class);

        Assert.assertFalse(auxList.isEmpty());

    }

}
