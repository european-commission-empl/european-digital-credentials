package eu.europa.ec.empl.edci.util;

import eu.europa.ec.empl.base.AbstractUnitBaseTest;
import eu.europa.ec.empl.edci.datamodel.model.dataTypes.Content;
import eu.europa.ec.empl.edci.datamodel.model.dataTypes.Note;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.InjectMocks;

import java.util.ArrayList;

public class XmlUtilTest extends AbstractUnitBaseTest {

    @InjectMocks
    private XmlUtil xmlUtil;

    @Test
    public void fromString_shouldReturnXML_whenAnObjectIsPassed() throws Exception {

        Note desc = xmlUtil.fromString("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<note xsi:schemaLocation=\"http://data.europa.eu/snb epass_credential_schema_-xsd.xsd\" xmlns=\"http://data.europa.eu/snb\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:cred=\"http://data.europa.eu/europass/model/credentials/w3c#\" xmlns:ds=\"http://www.w3.org/2000/09/xmldsig#\">\n" +
                "   <text lang=\"en\" content-type=\"text/plain\">example</text>\n" +
                "   <text lang=\"ca\" content-type=\"text/plain\">exemple</text>\n" +
                "</note>\n", Note.class);

        Assert.assertNotNull(desc);
    }

    @Test
    public void toString_shouldReturnObject_whenAnXMLIsPassed() throws Exception {

        Note note = new Note();
        note.setContents(new ArrayList<>());
        note.getContents().add(new Content("example", "en"));
        note.getContents().add(new Content("exemple", "ca"));
        String xml = xmlUtil.toXML(note, Note.class);

        Assert.assertNotNull(xml);
    }


}
