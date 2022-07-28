package integration.eu.europa.ec.empl.edci.parsers;

import eu.europa.ec.empl.edci.parsers.rdf.RDFParser;
import eu.europa.ec.empl.edci.parsers.rdf.model.RDFDescription;
import integration.eu.europa.ec.empl.base.AbstractIntegrationBaseTest;
import org.junit.Test;
import org.mockito.Spy;

import java.io.IOException;
import java.util.ArrayList;

//TODO: Review
public class RDFParserITest extends AbstractIntegrationBaseTest {

    @Spy
    RDFParser rdfParser;


    @Test
    public void getEntityFromXML_shouldParseXML_LEAForROOT() throws IOException {

        RDFDescription descr = new RDFDescription();
        descr.setTargetName(new ArrayList<>());

//        rdfParser.unmarshallControlledList(ControlledList.HUMAN_SEX);
//        rdfParser.unmarshallControlledList(ControlledList.CREDENTIAL_TYPE);
//        rdfParser.unmarshallControlledList(ControlledList.ISCED_F);
//        rdfParser.unmarshallControlledList(ControlledList.NQF);
//        rdfParser.getEntityFromXML("http://data.europa.eu/snb/learning-activity/25831c2", null);

    }


}