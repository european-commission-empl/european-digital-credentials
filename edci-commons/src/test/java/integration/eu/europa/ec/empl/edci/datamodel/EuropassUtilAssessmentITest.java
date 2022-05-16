package integration.eu.europa.ec.empl.edci.datamodel;

import eu.europa.ec.empl.edci.datamodel.model.AssessmentDTO;
import eu.europa.ec.empl.edci.datamodel.model.AssessmentSpecificationDTO;
import org.junit.Assert;
import org.junit.Test;

import javax.xml.bind.JAXBException;
import java.net.MalformedURLException;

//TODO: Review
public class EuropassUtilAssessmentITest extends EuropassUtilITest {

    @Test
    public void parseAssessment_noAssociations_ShouldMatchXSD() throws JAXBException, MalformedURLException {
//        _logger.trace(xmlUtil.toXML(createAssessmentDTO(), AssessmentDTO.class));
        Assert.assertTrue(true);
    }

//
//    public void parseAssessmentSpecification_ShouldMatchXSD() throws JAXBException, MalformedURLException {
//        _logger.trace(xmlUtil.toXML(createAssessmentSpecificationDTO(), AssessmentSpecificationDTO.class));
//    }


}
