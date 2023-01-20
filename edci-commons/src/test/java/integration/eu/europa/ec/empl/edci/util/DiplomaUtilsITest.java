package integration.eu.europa.ec.empl.edci.util;

import eu.europa.ec.empl.edci.constants.EDCIConstants;
import eu.europa.ec.empl.edci.datamodel.utils.NamespaceResolver;
import eu.europa.ec.empl.edci.sanitizer.EDCISanitizedHtml;
import eu.europa.ec.empl.edci.service.EDCIMessageService;
import eu.europa.ec.empl.edci.util.EDCICredentialModelUtil;
import eu.europa.ec.empl.edci.util.HtmlSanitizerUtil;
import eu.europa.ec.empl.edci.util.XmlUtil;
import integration.eu.europa.ec.empl.base.AbstractIntegrationBaseTest;
import org.apache.commons.io.FileUtils;
import org.joda.time.Period;
import org.joda.time.format.PeriodFormat;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.springframework.context.i18n.LocaleContextHolder;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class DiplomaUtilsITest extends AbstractIntegrationBaseTest {

    @InjectMocks
    HtmlSanitizerUtil htmlSanitizerUtil;

    @Mock
    private EDCIMessageService edciMessageService;

    @Spy
    @InjectMocks
    private EDCICredentialModelUtil edciCredentialModelUtil;

    @Spy
    private XmlUtil xmlUtil;

    private XPath prepareXPath(Document doc) {
        XPathFactory xpathfactory = XPathFactory.newInstance();
        XPath xpath = xpathfactory.newXPath();
        xpath.setNamespaceContext(new NamespaceResolver(doc));
        return xpath;
    }

    private Document buildCredentialDoc() throws Exception {
        File resource = new File("src/test/resources/diploma/diploma_credential.xml");

        DocumentBuilderFactory factory = xmlUtil.getDocumentBuilderFactory();
        factory.setNamespaceAware(true); // never forget this!
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(resource);
        return doc;
    }


    @Test
    public void getValue_shouldParsePeriod_whenAPeriodFieldIsFound() throws Exception {

        Mockito.doReturn("certifies that").when(edciMessageService).getMessage(Mockito.anyString());

        Document doc = buildCredentialDoc();
        XPath xpath = prepareXPath(doc);

        String valuePeriod = htmlSanitizerUtil.getValue(doc, xpath, "/eup:europassCredential/eup:credentialSubject/eup:activities/eup:activity[@id='urn:epass:activity:1']/eup:workload");

        Assert.assertEquals(PeriodFormat.wordBased(LocaleContextHolder.getLocale()).print(Period.parse("PT20H")), valuePeriod);

    }

    @Test
    public void getValue_shouldReturnImgBase64_whenAnImageFieldIsFound() throws Exception {

        Mockito.doReturn("certifies that").when(edciMessageService).getMessage(Mockito.anyString());

        Document doc = buildCredentialDoc();
        XPath xpath = prepareXPath(doc);

        String value = htmlSanitizerUtil.getValue(doc, xpath, "//eup:organization[@id=/eup:europassCredential/cred:issuer/@idref]/eup:logo");

        Assert.assertTrue(value.contains("base64"));

    }

    @Test
    public void getValue_shouldParseDate_whenADateFieldIsFound() throws Exception {

        Mockito.doReturn("certifies that").when(edciMessageService).getMessage(Mockito.anyString());

        Document doc = buildCredentialDoc();
        XPath xpath = prepareXPath(doc);

        Date dateTmp = new SimpleDateFormat(EDCIConstants.DATE_ISO_8601).parse("2020-06-21T18:27:21+02:00");
        String valueRef = new SimpleDateFormat(EDCIConstants.DATE_FRONT_GMT).format(dateTmp);

        String valueDate = htmlSanitizerUtil.getValue(doc, xpath, "/eup:europassCredential/cred:validFrom");

        Assert.assertEquals(valueRef, valueDate);

    }

    @Test
    public void getValue_shouldReturnText_whenATextFieldIsFound() throws Exception {

        Mockito.doReturn("certifies that").when(edciMessageService).getMessage(Mockito.anyString());

        Document doc = buildCredentialDoc();
        XPath xpath = prepareXPath(doc);

        String value = htmlSanitizerUtil.getValue(doc, xpath, "/eup:europassCredential/eup:title");

        Assert.assertEquals("Professional Doctorate Training", value);

    }

    @Test
    public void processWildcardsHTML_shouldProcessAllWildcards_whenCalled() throws Exception {

        Mockito.doReturn("certifies that").when(edciMessageService).getMessage(Mockito.anyString());

        File resource = new File("src/test/resources/diploma/diploma_credential.xml");
        String xml = FileUtils.readFileToString(resource, StandardCharsets.UTF_8.name());

        EDCISanitizedHtml diploma = htmlSanitizerUtil.processHTML(edciCredentialModelUtil.fromXML(xml).getCredential());

        boolean allReplaced = !diploma.getHtml().get(0).contains("[$");
        Assert.assertTrue(allReplaced);

    }

    @Test
    public void processWildcardsHTML_shouldProcessWildCardsPipes_whenDatesHaveFormatsDefined() throws Exception {

        Mockito.doReturn("certifies that").when(edciMessageService).getMessage(Mockito.anyString());

        File resource = new File("src/test/resources/diploma/diploma_credential_date_pipe.xml");
        String xml = FileUtils.readFileToString(resource, StandardCharsets.UTF_8.name());

        EDCISanitizedHtml diploma = htmlSanitizerUtil.processHTML(edciCredentialModelUtil.fromXML(xml).getCredential());

        boolean dateformatted = diploma.getHtml().get(0).contains("09/03/2021");
        Assert.assertTrue(dateformatted);

    }

}
