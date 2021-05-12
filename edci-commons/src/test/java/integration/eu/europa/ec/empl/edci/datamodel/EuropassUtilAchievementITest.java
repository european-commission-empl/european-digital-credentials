package integration.eu.europa.ec.empl.edci.datamodel;

import eu.europa.ec.empl.edci.annotation.EDCIIdentifier;
import eu.europa.ec.empl.edci.datamodel.model.*;
import eu.europa.ec.empl.edci.datamodel.model.dataTypes.Code;
import eu.europa.ec.empl.edci.datamodel.model.dataTypes.Text;
import org.junit.Test;

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.util.Arrays;

//TODO: Review
public class EuropassUtilAchievementITest extends EuropassUtilITest {

    @Test
    public void parseAchievement_noAssociations_ShouldMatchXSD() throws JAXBException, MalformedURLException {
        _logger.trace(xmlUtil.toXML(createLearningAchievementDTO(), LearningAchievementDTO.class));
    }


    public void parseLearningSpecification_noAssociations_ShouldMatchXSD() throws JAXBException, MalformedURLException {
        _logger.trace(xmlUtil.toXML(createLearningSpecificationDTO(), LearningSpecificationDTO.class));
    }


    public void parseAwardingProcess_noAssociations_ShouldMatchXSD() throws JAXBException, MalformedURLException {
        System.out.println(xmlUtil.toXML(createAwardingProcessDTO(), AwardingProcessDTO.class));
    }


    public void parseQualificationDTO_noAssociations_ShouldMatchXSD() throws JAXBException, MalformedURLException {
        _logger.trace(xmlUtil.toXML(createQualificationDTO(), QualificationDTO.class));
    }


    public void parseLearningOutcomeDTO_noAssociations_ShouldMatchXSD() throws JAXBException, MalformedURLException {
        _logger.trace(xmlUtil.toXML(createLearningOutcomeDTO(), LearningOutcomeDTO.class));
    }


    public void parseAwardingOpportunityDTO_noAssociations_ShouldMatchXSD() throws JAXBException, MalformedURLException {
        _logger.trace(xmlUtil.toXML(createAwardingOpportunityDTO(), AwardingOpportunityDTO.class));
    }

    @Test
    public void parseLearningAchievementSpecificationReferences_ShouldMatchXSD() throws IOException, JAXBException {
        EuropassCredentialDTO europassCredentialDTO = new EuropassCredentialDTO();

        LearningAchievementDTO learningAchievementDTO1 = new LearningAchievementDTO();
        learningAchievementDTO1.setTitle(new Text("learningAch1", "en"));

        LearningAchievementDTO learningAchievementDTO2 = new LearningAchievementDTO();
        learningAchievementDTO2.setTitle(new Text("learningAch2", "en"));

        LearningSpecificationDTO learningSpecificationDTO = new LearningSpecificationDTO();
        learningSpecificationDTO.setTitle(new Text("learningSpec", "en"));
        learningSpecificationDTO.setId(URI.create(LearningSpecificationDTO.class.getAnnotation(EDCIIdentifier.class).prefix() + "001"));

        QualificationDTO qualificationDTO = new QualificationDTO();
        qualificationDTO.setId(URI.create("urn:qualification:01"));
        qualificationDTO.setNqfLevel(Arrays.asList(testUtilities.createMockObject(Code.class)));

        learningAchievementDTO1.setSpecifiedBy(learningSpecificationDTO);
        learningAchievementDTO2.setSpecifiedBy(qualificationDTO);

        PersonDTO personDTO = new PersonDTO();
        personDTO.setFullName(new Text("personName", "en"));

        personDTO.setAchieved(Arrays.asList(learningAchievementDTO1, learningAchievementDTO2));

        europassCredentialDTO.setCredentialSubject(personDTO);

        EuropassCredentialDTO europassCredentialDTOclone = edciCredentialModelUtil.cloneModel(europassCredentialDTO);
        // europassCredentialDTO.setLearningSpecificationReferences(new HashSet<>(Arrays.asList(learningSpecificationDTO)));

        System.out.println(xmlUtil.toXML(europassCredentialDTO, EuropassCredentialDTO.class));
        System.out.println("################################");
        System.out.println(xmlUtil.toXML(europassCredentialDTOclone, EuropassCredentialDTO.class));
        //assertTrue(xmlUtil.isValidXML(xml, xsd, EuropassCredentialDTO.class));

    }

}
