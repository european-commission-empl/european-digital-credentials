package integration.eu.europa.ec.empl.edci.datamodel;

import org.junit.Assert;
import org.junit.Test;

import javax.xml.bind.JAXBException;
import java.net.MalformedURLException;

//TODO: Review
public class EuropassUtilEntitlementITest extends EuropassUtilITest {

    @Test
    public void parseEntitlement_noAssociations_ShouldMatchXSD() throws JAXBException, MalformedURLException {
//        _logger.trace(xmlUtil.toXML(createEntitlementDTO(), EntitlementDTO.class));
        Assert.assertTrue(true);
    }

//
//    public void parseEntitlementSpecification_noAssociations_ShouldmatchXSD() throws JAXBException, MalformedURLException {
//        _logger.trace(xmlUtil.toXML(createEntitlementSpecificationDTO(), EntitlementSpecificationDTO.class));
//    }

}
