package integration.eu.europa.ec.empl.edci.datamodel;

import eu.europa.ec.empl.edci.datamodel.model.LearningActivityDTO;
import eu.europa.ec.empl.edci.datamodel.model.LearningActivitySpecificationDTO;
import org.junit.Test;

import javax.xml.bind.JAXBException;
import java.net.MalformedURLException;

//TODO: Review
public class EuropassUtilActivityITest extends EuropassUtilITest {

    @Test
    public void parseActivity_noAssociations_ShouldMatchXSD() throws JAXBException, MalformedURLException {
        _logger.trace(xmlUtil.toXML(createLearningActivityDTO(), LearningActivityDTO.class));
    }


    public void parseLearningActivitySpecification_noAssociations_ShouldMatchXSD() throws JAXBException, MalformedURLException {
        _logger.trace(xmlUtil.toXML(createLearningActivitySpecificationDTO(), LearningActivitySpecificationDTO.class));
    }

}
