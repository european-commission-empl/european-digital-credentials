package eu.europa.ec.empl.edci.util;

import eu.europa.ec.empl.base.AbstractUnitBaseTest;
import eu.europa.ec.empl.edci.datamodel.model.*;
import eu.europa.ec.empl.edci.datamodel.model.base.Identifiable;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.InjectMocks;

import java.util.*;

//TODO: Review
public class ResourcesUtilTest extends AbstractUnitBaseTest {

    @InjectMocks
    ResourcesUtil resourcesUtil;

    protected class TestObj {

        long id;
        Set<TestObj> relatesTo = new HashSet<>();

        TestObj parent = null;

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }

        public Set<TestObj> getRelatesTo() {
            return relatesTo;
        }

        public void setRelatesTo(Set<TestObj> relatesTo) {
            this.relatesTo = relatesTo;
        }

        public TestObj getParent() {
            return parent;
        }

        public void setParent(TestObj parent) {
            this.parent = parent;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            TestObj testObj = (TestObj) o;
            return id == testObj.id;
        }

        @Override
        public int hashCode() {
            return Objects.hash(id);
        }
    }

    @Test
    public void checkLoopTree_shouldDoNothing_whenObjectIsNull() throws Exception {

        resourcesUtil.checkLoopTree(new HashSet<Integer>(), null, (a) -> null, "test obj");
    }

    @Test(expected = RuntimeException.class)
    public void checkLoopTree_shouldThrowException_ifTheresADirectLoop() throws Exception {
        boolean hasLoop = false;

        TestObj org1 = new TestObj() {{
            setId(1L);
        }};

        org1.setRelatesTo(new HashSet() {{
            add(org1);
        }});

        resourcesUtil.checkLoopTree(new HashSet<Integer>(), org1, (a) -> a.getRelatesTo(), "test obj");
    }

    @Test(expected = RuntimeException.class)
    public void checkLoopTree_shouldThrowException_ifTwoObjAreRelatedInCycle() throws Exception {
        boolean hasLoop = false;

        TestObj org1 = new TestObj() {{
            setId(1L);
        }};
        TestObj org2 = new TestObj() {{
            setId(2L);
        }};

        org1.setRelatesTo(new HashSet() {{
            add(org2);
        }});

        org2.setRelatesTo(new HashSet() {{
            add(org1);
        }});

        resourcesUtil.checkLoopTree(new HashSet<Integer>(), org1, (a) -> a.getRelatesTo(), "test obj");
    }


    @Test(expected = RuntimeException.class)
    public void checkLoopTree_shouldThrowException_ifTwoObjAreRelatedInCycleIndirectly() throws Exception {
        boolean hasLoop = false;

        TestObj org1 = new TestObj() {{
            setId(1L);
        }};
        TestObj org2 = new TestObj() {{
            setId(2L);
        }};
        TestObj org3 = new TestObj() {{
            setId(3L);
        }};
        TestObj org4 = new TestObj() {{
            setId(4L);
        }};

        org1.setRelatesTo(new HashSet() {{
            add(org2);
        }});
        org2.setRelatesTo(new HashSet() {{
            add(org3);
            add(org4);
            add(org1);
        }});

        resourcesUtil.checkLoopTree(new HashSet<Integer>(), org1, (a) -> a.getRelatesTo(), "test obj");
    }

    @Test
    public void checkLoopTree_shouldDoNothing_whenObjectIsUnrelated() throws Exception {
        boolean hasLoop = false;

        TestObj org1 = new TestObj() {{
            setId(1L);
        }};

        resourcesUtil.checkLoopTree(new HashSet<Integer>(), org1, (a) -> a.getRelatesTo(), "test obj");
    }

    @Test
    public void checkLoopTree_shouldDoNothing_whenObjectHasRelationsWithNoLoops() throws Exception {
        boolean hasLoop = false;

        TestObj org1 = new TestObj() {{
            setId(1L);
        }};
        TestObj org2 = new TestObj() {{
            setId(2L);
        }};
        TestObj org3 = new TestObj() {{
            setId(3L);
        }};
        TestObj org4 = new TestObj() {{
            setId(4L);
        }};

        org1.setRelatesTo(new HashSet() {{
            add(org2);
        }});
        org2.setRelatesTo(new HashSet() {{
            add(org3);
            add(org4);
        }});

        resourcesUtil.checkLoopTree(new HashSet<Integer>(), org1, (a) -> a.getRelatesTo(), "test obj");
    }


    @Test(expected = RuntimeException.class)
    public void checkLoopTree_shouldThrowException_ifTwoObjAreRelatedInCycleIndirectly2() throws Exception {
        boolean hasLoop = false;

        TestObj org1 = new TestObj() {{
            setId(1L);
        }};
        TestObj org2 = new TestObj() {{
            setId(2L);
        }};
        TestObj org3 = new TestObj() {{
            setId(3L);
        }};
        TestObj org4 = new TestObj() {{
            setId(4L);
        }};

        org1.setRelatesTo(new HashSet() {{
            add(org2);
        }});
        org2.setRelatesTo(new HashSet() {{
            add(org3);
            add(org4);
        }});

        org4.setRelatesTo(new HashSet() {{
            add(org1);
        }});

        resourcesUtil.checkLoopTree(new HashSet<Integer>(), org1, (a) -> a.getRelatesTo(), "test obj");
    }

    @Test
    public void checkLooLine_shouldDoNothing_whenObjectIsNull() throws Exception {
        resourcesUtil.checkLoopLine(null, (a) -> null, "test obj");
    }


    @Test
    public void checkLoopLine_shouldDoNothing_whenObjectIsUnrelated() throws Exception {
        boolean hasLoop = false;

        TestObj org1 = new TestObj() {{
            setId(1L);
        }};

        resourcesUtil.checkLoopLine(org1, (a) -> a.getParent(), "test obj");
    }

    @Test(expected = RuntimeException.class)
    public void checkLoopLine_shouldThrowException_ifTheresADirectLoop() throws Exception {
        boolean hasLoop = false;

        TestObj org1 = new TestObj() {{
            setId(1L);
        }};

        org1.setParent(org1);

        resourcesUtil.checkLoopLine(org1, (a) -> a.getParent(), "test obj");
    }

    @Test
    public void checkContentClassLoopTree_shouldDoNothing_ifThereIsNoLoop() throws Exception {

        EuropassCredentialDTO cred = new EuropassCredentialDTO();

        cred.setCredentialSubject(new PersonDTO());

        cred.getCredentialSubject().setAchieved(new ArrayList());
        cred.getCredentialSubject().getAchieved().add(new LearningAchievementDTO());

        LearningAchievementDTO subAch = new LearningAchievementDTO();

        cred.getCredentialSubject().getAchieved().get(0).setHasPart(new ArrayList());
        cred.getCredentialSubject().getAchieved().get(0).getHasPart().add(subAch);


        resourcesUtil.checkContentClassLoopTree(new LinkedHashMap<Integer, String>(), cred);
    }

    @Test(expected = RuntimeException.class)
    public void checkContentClassLoopTree_shouldThrowException_ifTheresALoopInside() throws Exception {

        EuropassCredentialDTO cred = new EuropassCredentialDTO();

        cred.setCredentialSubject(new PersonDTO());

        cred.getCredentialSubject().setAchieved(new ArrayList());
        cred.getCredentialSubject().getAchieved().add(new LearningAchievementDTO());

        LearningAchievementDTO subAch = new LearningAchievementDTO();

        cred.getCredentialSubject().getAchieved().get(0).setHasPart(new ArrayList());
        cred.getCredentialSubject().getAchieved().get(0).getHasPart().add(subAch);

        subAch.setHasPart(new ArrayList<>());
        subAch.getHasPart().add(cred.getCredentialSubject().getAchieved().get(0));

        resourcesUtil.checkContentClassLoopTree(new LinkedHashMap<Integer, String>(), cred);
    }

    @Test
    public void checkContentClassLoopTree_shouldDoNothing_ifThereIsTheSameObjectWithNoLoop() throws Exception {

        EuropassCredentialDTO cred = new EuropassCredentialDTO();

        cred.setCredentialSubject(new PersonDTO());

        cred.getCredentialSubject().setAchieved(new ArrayList());
        cred.getCredentialSubject().getAchieved().add(new LearningAchievementDTO());

        LearningAchievementDTO subAch = new LearningAchievementDTO();

        cred.getCredentialSubject().getAchieved().get(0).setHasPart(new ArrayList());
        cred.getCredentialSubject().getAchieved().get(0).getHasPart().add(subAch);

        LearningSpecificationDTO repeatedInstance = new LearningSpecificationDTO();

        cred.getCredentialSubject().getAchieved().get(0).setSpecifiedBy(repeatedInstance);
        subAch.setSpecifiedBy(repeatedInstance);

        resourcesUtil.checkContentClassLoopTree(new LinkedHashMap<Integer, String>(), cred);
    }

    @Test
    public void findAllObjectsRecInList_shouldGetAllElems_searchingThroughAllTheRelations() throws Exception {

        EuropassCredentialDTO cred = new EuropassCredentialDTO();

        cred.setCredentialSubject(new PersonDTO()); //1
        cred.getCredentialSubject().setPlaceOfBirth(new LocationDTO()); //2

        cred.getCredentialSubject().setAchieved(new ArrayList());
        cred.getCredentialSubject().getAchieved().add(new LearningAchievementDTO());//3

        LearningAchievementDTO subAch = new LearningAchievementDTO();

        cred.getCredentialSubject().getAchieved().get(0).setHasPart(new ArrayList());
        cred.getCredentialSubject().getAchieved().get(0).getHasPart().add(subAch);//4

        LearningSpecificationDTO repeatedInstance = new LearningSpecificationDTO();

        subAch.setSpecifiedBy(repeatedInstance);//5

        cred.setIssuer(new OrganizationDTO()); //6

        Assert.assertEquals(6, resourcesUtil.findAllObjectsInRecursivily(cred, Identifiable.class, Identifiable.class).size());

    }

    @Test
    public void findAllObjectsRecInList_shouldGetAllPersonRelated_searchingThroughAllThePersonObjects() throws Exception {

        EuropassCredentialDTO cred = new EuropassCredentialDTO();

        cred.setCredentialSubject(new PersonDTO()); //1
        cred.getCredentialSubject().setPlaceOfBirth(new LocationDTO()); //2

        cred.getCredentialSubject().setAchieved(new ArrayList());
        cred.getCredentialSubject().getAchieved().add(new LearningAchievementDTO());//3

        LearningAchievementDTO subAch = new LearningAchievementDTO();

        cred.getCredentialSubject().getAchieved().get(0).setHasPart(new ArrayList());
        cred.getCredentialSubject().getAchieved().get(0).getHasPart().add(subAch);

        LearningSpecificationDTO repeatedInstance = new LearningSpecificationDTO();

        subAch.setSpecifiedBy(repeatedInstance);

        cred.setIssuer(new OrganizationDTO()); //4 (root element is always "scanned")

        Assert.assertEquals(4, resourcesUtil.findAllObjectsInRecursivily(cred, Identifiable.class, PersonDTO.class).size());

    }

    @Test
    public void findAllObjectsRecInList_shouldGetAllPersonAndAchRelated_searchingThroughAllThePersonAndAchObjects() throws Exception {

        EuropassCredentialDTO cred = new EuropassCredentialDTO();

        cred.setCredentialSubject(new PersonDTO()); //1
        cred.getCredentialSubject().setPlaceOfBirth(new LocationDTO()); //2

        cred.getCredentialSubject().setAchieved(new ArrayList());
        cred.getCredentialSubject().getAchieved().add(new LearningAchievementDTO());//3

        LearningAchievementDTO subAch = new LearningAchievementDTO();

        cred.getCredentialSubject().getAchieved().get(0).setHasPart(new ArrayList());
        cred.getCredentialSubject().getAchieved().get(0).getHasPart().add(subAch); //4

        LearningSpecificationDTO repeatedInstance = new LearningSpecificationDTO();

        subAch.setSpecifiedBy(repeatedInstance);//5

        cred.setIssuer(new OrganizationDTO()); //6 (root element is always "scanned")

        cred.getIssuer().setUnitOf(new OrganizationDTO());

        Assert.assertEquals(6, resourcesUtil.findAllObjectsInRecursivily(cred, Identifiable.class, PersonDTO.class, LearningAchievementDTO.class).size());

    }


}
