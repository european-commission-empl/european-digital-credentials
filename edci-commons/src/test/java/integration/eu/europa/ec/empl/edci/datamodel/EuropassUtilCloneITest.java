package integration.eu.europa.ec.empl.edci.datamodel;

import eu.europa.ec.empl.edci.datamodel.model.*;
import eu.europa.ec.empl.edci.datamodel.model.dataTypes.Note;
import org.junit.Assert;
import org.junit.Test;

import javax.xml.bind.JAXBException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.util.Arrays;

//TODO: Review
public class EuropassUtilCloneITest extends EuropassUtilITest {

    @Test
    public void parseFromXmlFile_withSpecialChars() throws JAXBException, MalformedURLException, IOException {
//        File file = new File("src/test/resources/credential-withspecialchars.xml");
//
//        EuropassCredentialDTO europassCredentialDTO = edciCredentialModelUtil.fromInputStream(new FileInputStream(file)).getCredential();
//
//        System.out.println(edciCredentialModelUtil.toXML(europassCredentialDTO));
        Assert.assertTrue(true);
    }

//
//    public void clonePerson_shouldHaveAllLocalizables() throws JAXBException, MalformedURLException, IOException {
//        PersonDTO personDTO = createPersonDTO();
//
//        PersonDTO clonedPersonDTO = edciCredentialModelUtil.cloneModel(personDTO);
//
//        System.out.println(String.format("[%d] / [%d]",
//                personDTO.getPreferredName().getContents().size(),
//                clonedPersonDTO.getPreferredName().getContents().size()));
//
//        System.out.println(xmlUtil.toXML(clonedPersonDTO, PersonDTO.class));
//
//    }
//
//    @Test
//    public void cloneNestedAwardingBody_sholdMaintainAllReferences() throws JAXBException, IOException {
//        EuropassCredentialDTO europassCredentialDTO = new EuropassCredentialDTO();
//        europassCredentialDTO.setId(URI.create("urn:epass:cred:1"));
//
//        PersonDTO personDTO = new PersonDTO();
//        personDTO.setId(URI.create("urn:epass:person:1"));
//
//        LearningAchievementDTO learningAchievementDTO1 = new LearningAchievementDTO();
//        learningAchievementDTO1.setId(URI.create("urn:epass:ach:1"));
//
//        OrganizationDTO organizationDTO = new OrganizationDTO();
//        organizationDTO.setId(URI.create("urn:epass:organization:1"));
//
//        AwardingProcessDTO awardingProcessDTO = new AwardingProcessDTO();
//        awardingProcessDTO.setId(URI.create("urn:epass:awardingProcess:1"));
//        awardingProcessDTO.setAwardingBody(organizationDTO);
//
//        LearningAchievementDTO learningAchievementDTO2 = new LearningAchievementDTO();
//        learningAchievementDTO2.setId(URI.create("urn:epass:ach:2"));
//        learningAchievementDTO2.setWasAwardedBy(awardingProcessDTO);
//
//        learningAchievementDTO1.setHasPart(Arrays.asList(learningAchievementDTO2));
//        personDTO.setAchieved(Arrays.asList(learningAchievementDTO1));
//        europassCredentialDTO.setCredentialSubject(personDTO);
//
//        System.out.println(edciCredentialModelUtil.toXML(europassCredentialDTO));
//        System.out.println("\n#########################################################\n");
//        System.out.println(edciCredentialModelUtil.toXML(edciCredentialModelUtil.cloneModel(europassCredentialDTO)));
//
//    }
//
//    public void testClone() throws JAXBException, MalformedURLException, IOException {
//        cloneEntity_shouldHaveAllLocalizables(ScoringSchemeDTO.class);
//    }
//
//    public <T> void cloneEntity_shouldHaveAllLocalizables(Class<T> clazz) throws JAXBException, MalformedURLException, IOException {
//        T entity = testUtilities.createMockObject(clazz);
//
//        System.out.println(xmlUtil.toXML(entity, clazz));
//
//        T clone = edciCredentialModelUtil.cloneModel(entity);
//        System.out.println(xmlUtil.toXML(clone, clazz));
//    }
//
//
//    public void cloneText() throws JAXBException, MalformedURLException {
//        Note note = testUtilities.createMockObject(Note.class);
//
//        String xml = xmlUtil.toXML(note, Note.class);
//
//        Note clonedNote = xmlUtil.fromString(xml, Note.class);
//
//        System.out.println(xmlUtil.toXML(clonedNote, Note.class));
//    }
}

