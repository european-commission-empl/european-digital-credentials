package integration.eu.europa.ec.empl.edci.datamodel;

import eu.europa.ec.empl.edci.constants.Defaults;
import eu.europa.ec.empl.edci.datamodel.model.*;
import org.junit.Assert;
import org.junit.Test;

import javax.xml.bind.JAXBException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

//TODO: Review
public class EuropassUtilAgentITest extends EuropassUtilITest {

    @Test
    public void parsePerson_noAssociations_ShouldMatchXSD() throws IOException, JAXBException {
        _logger.trace(xmlUtil.toXML(createPersonDTO(), PersonDTO.class));
    }


    public void parseOrganisation_noAssociations_ShouldMatchXSD() throws IOException, JAXBException {
        _logger.trace(xmlUtil.toXML(createOrganizationDTO(), OrganizationDTO.class));
    }


    public void parseAccreditation_noAssociations_ShouldMatchXSD() throws IOException, JAXBException {
        _logger.trace(xmlUtil.toXML(createAccreditationDTO(), AccreditationDTO.class));
    }

    @Test
    public void parsePerson_WithContactPoint_ShouldHaveMailTo() throws IOException, JAXBException {
        PersonDTO personDTO = createPersonDTO();
        String xml = xmlUtil.toXML(personDTO, PersonDTO.class);
        Assert.assertTrue(xml.contains(Defaults.DEFAULT_MAILTO));
    }

    @Test
    public void unmarshallPerson_WithContactPoint_ShouldNotHaveMailTo() throws IOException, JAXBException {
        File file = new File("src/test/resources/person-withContactPoint.xml");
        boolean noMailTo = true;
        PersonDTO personDTO = xmlUtil.fromInputStream(new FileInputStream(file), PersonDTO.class);
        for (ContactPoint contactPoint : personDTO.getContactPoint()) {
            for (MailboxDTO mailboxDTO : contactPoint.getEmail()) {
                String mail = mailboxDTO.getId().toString();
                if (mail.contains(Defaults.DEFAULT_MAILTO)) noMailTo = false;
            }
        }
        Assert.assertTrue(noMailTo);
    }

    @Test
    public void unmarshallPerson_WithContactPoint_ShouldNotHaveMailTo_uriwallet() throws IOException, JAXBException {
        File file = new File("src/test/resources/person-withContactPoint-uriwallet.xml");
        boolean noMailTo = true;
        PersonDTO personDTO = xmlUtil.fromInputStream(new FileInputStream(file), PersonDTO.class);
        for (ContactPoint contactPoint : personDTO.getContactPoint()) {
            for (MailboxDTO mailboxDTO : contactPoint.getEmail()) {
                String mail = mailboxDTO.getId().toString();
                if (mail.contains(Defaults.DEFAULT_MAILTO)) noMailTo = false;
                System.out.println(mail);
            }
        }
        Assert.assertTrue(noMailTo);
    }

    @Test
    public void unMarshallPerson_lookForWalletAddress() throws IOException, JAXBException {
        File file = new File("src/test/resources/person-withContactPoint-uriwallet.xml");
        boolean noMailTo = true;
        PersonDTO personDTO = xmlUtil.fromInputStream(new FileInputStream(file), PersonDTO.class);
        for (ContactPoint contactPoint : personDTO.getContactPoint()) {
            for (String wallet : contactPoint.getWalletAddress()) {
                System.out.println(wallet);
            }
        }
    }

}
