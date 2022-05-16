package eu.europa.ec.empl.edci.issuer.service;

import eu.europa.ec.empl.base.AbstractUnitBaseTest;
import eu.europa.ec.empl.edci.datamodel.model.EuropassCredentialDTO;
import eu.europa.ec.empl.edci.datamodel.model.LearningOpportunityDTO;
import eu.europa.ec.empl.edci.datamodel.model.dataTypes.Content;
import eu.europa.ec.empl.edci.datamodel.model.dataTypes.Text;
import eu.europa.ec.empl.edci.exception.FileBaseDataException;
import eu.europa.ec.empl.edci.factory.ConsumerFactory;
import eu.europa.ec.empl.edci.issuer.util.EDCIWorkBookReader;
import eu.europa.ec.empl.edci.issuer.util.EDCIWorkBookUtil;
import eu.europa.ec.empl.edci.issuer.util.FileUtil;
import eu.europa.ec.empl.edci.util.EDCICredentialModelUtil;
import eu.europa.ec.empl.edci.util.ReflectiveUtil;
import eu.europa.ec.empl.edci.util.Validator;
import eu.europa.ec.empl.edci.util.XmlUtil;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Workbook;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Arrays;

import static org.junit.Assert.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;

public class WorkBookEuroPassDynamicServiceTest extends AbstractUnitBaseTest {

    @InjectMocks
    private EDCIWorkbookService edciWorkbookService;

    @Mock
    private CredentialService credentialService;

    @Spy
    private DownloadService downloadService;

    @Spy
    public EDCIWorkBookReader edciWorkBookReader = new EDCIWorkBookReader();

    @Spy
    public EDCICredentialModelUtil edciCredentialModelUtil;

    @Spy
    private XmlUtil xmlUtil;

    @Spy
    public ReflectiveUtil reflectiveUtil;

    @Spy
    public AssociationService associationService;

    @Spy
    public EDCIWorkBookReader dynamicWorkBookUtil = new EDCIWorkBookReader();

    @Spy
    public FileUtil fileUtil = new FileUtil();

    @Spy
    public ConsumerFactory consumerFactory;

    @Spy
    public EDCIWorkBookUtil workBookEuroPassUtil = new EDCIWorkBookUtil();

    @Spy
    public Validator validator = new Validator();

    private File activities_xls = new File("src/test/resources-unfiltered/Activities_edci_template.xlsm");
    private File achievements_xls = new File("src/test/resources-unfiltered/Achievements_edci_template.xlsm");
    private File organisations_xls = new File("src/test/resources-unfiltered/Organisations_edci_template.xlsm");
    private File credentials_xls = new File("src/test/resources-unfiltered/Credentials_edci_template.xlsm");
    private File edc_xls = new File("src/test/resources-unfiltered/EDC2.xlsm");


    //@Test(expected = FileBaseDataException.class)
    public void parseExcelTemplate_shouldThrowFileBaseDataException() throws IOException, InvalidFormatException, FileBaseDataException {
        ClassLoader classLoader = getClass().getClassLoader();
        doReturn("EuropassCredentialDTO").when(edciWorkBookReader).getBaseMandatoryStringCellValue(any(Cell.class), anyString());
        Workbook workbook = edciWorkbookService.createWorkBook(new FileInputStream(activities_xls));
        boolean result = edciWorkbookService.isValidFormat(workbook);
        assertFalse(result);
    }

   /* public void scanSheets() {
        Workbook workBook = edciWorkbookService.iterateSheets(WorkBookUtil);
    }*/

    @Test
    public void marshallOneText_Multilingual() throws Exception {
        EuropassCredentialDTO europassCredentialDTO = new EuropassCredentialDTO();
        Content contentEn = new Content();

        contentEn.setContent("Hello world");
        contentEn.setLanguage("en");

        Content contentCa = new Content();

        contentCa.setContent("Hola món");
        contentCa.setLanguage("ca");

        Text titleText = new Text();

        titleText.getContents().add(contentCa);
        titleText.getContents().add(contentEn);

        europassCredentialDTO.setTitle(titleText);


        System.out.println(xmlUtil.toXML(europassCredentialDTO, EuropassCredentialDTO.class));

    }


    // @Test
    /*public void marshallAdditionalNoteList_Multilingual() throws Exceptionr {
        QualificationAwardDTO qualificationAwardDTO = new QualificationAwardDTO();

        Note note1 = new Note();

        Content note1ContentEn = new Content();
        note1ContentEn.setLanguage("en");
        note1ContentEn.setContent("Content for the note 1");

        Content note1ContentCa = new Content();
        note1ContentCa.setLanguage("ca");
        note1ContentCa.setContent("Contingut per a nota 1");

        note1.getContents().add(note1ContentEn);
        note1.getContents().add(note1ContentCa);
        note1.setTopic("topic for note 1");


        Note note2 = new Note();

        Content note2ContentEn = new Content();
        note2ContentEn.setLanguage("en");
        note2ContentEn.setContent("Content for the note 2");

        Content note2ContentCa = new Content();
        note2ContentCa.setLanguage("ca");
        note2ContentCa.setContent("Contingut per a nota 2");

        note2.getContents().add(note2ContentEn);
        note2.getContents().add(note2ContentCa);

        note2.setTopic("topic for note 2");

        List<Note> notes = Arrays.asList(note1, note2);

        qualificationAwardDTO.setAdditionalNote(notes);

        StringWriter stringWriter = new StringWriter();
        JAXBContext jaxbContext = JAXBContext.newInstance(QualificationAwardDTO.class);
        Marshaller marshaller = jaxbContext.createMarshaller();
        marshaller.marshal(qualificationAwardDTO, stringWriter);

        System.out.println(stringWriter.toString());

    }*/

    //@Test
    public void marshallAlternativeLabelTextList_Multilingual() throws Exception {
        LearningOpportunityDTO learningOpportunityDTO = new LearningOpportunityDTO();

        Content contentText1En = new Content();

        contentText1En.setContent("Hello world");
        contentText1En.setLanguage("en");

        Content contentText1Ca = new Content();

        contentText1Ca.setContent("Hola món");
        contentText1Ca.setLanguage("ca");

        Text localizedText1 = new Text();
        localizedText1.getContents().add(contentText1En);
        localizedText1.getContents().add(contentText1Ca);

        Content contentText2En = new Content();

        contentText2En.setContent("Bye world");
        contentText2En.setLanguage("en");

        Content contentText2Ca = new Content();

        contentText2Ca.setContent("Adéu món");
        contentText2Ca.setLanguage("ca");

        Text localizedText2 = new Text();
        localizedText2.getContents().add(contentText2En);
        localizedText2.getContents().add(contentText2Ca);

        learningOpportunityDTO.setAlternativeLabel(Arrays.asList(localizedText1, localizedText2));

        StringWriter stringWriter = new StringWriter();
        JAXBContext jaxbContext = JAXBContext.newInstance(LearningOpportunityDTO.class);
        Marshaller marshaller = jaxbContext.createMarshaller();
        marshaller.marshal(learningOpportunityDTO, stringWriter);

        System.out.println(stringWriter.toString());

    }


    //@Test
    public void parse_credentials() throws Exception {
        parseWorkBook(credentials_xls);
    }

    //@Test
    public void parse_organisations() throws Exception {
        parseWorkBook(organisations_xls);
    }

    //@Test
    public void parse_Activities() throws Exception {
        parseWorkBook(activities_xls);
    }

    // @Test
    public void parse_Achievements() throws Exception {
        parseWorkBook(achievements_xls);
    }

    //@Test
    public void parse_FullXLS() throws Exception {
        parseWorkBook(edc_xls);
    }

    public void parseWorkBook(File xls) throws Exception {
        edciWorkbookService.fileUtil = fileUtil;
        System.out.println(xls.getAbsolutePath());
        Workbook workbook = edciWorkbookService.createWorkBook(new FileInputStream(xls));
        prepareMocks();
        System.out.println("TEST_parseWorkBook");
        for (EuropassCredentialDTO europassCredentialDTO : edciWorkbookService.parseCredentialData(workbook)) {
            String xml = edciCredentialModelUtil.toXML(europassCredentialDTO);
            System.out.println(xml);
        }

    }

    public void prepareMocks() {
        reflectiveUtil.validator = validator;
        downloadService.validator = validator;
        downloadService.reflectiveUtil = reflectiveUtil;

        workBookEuroPassUtil.validator = validator;
        workBookEuroPassUtil.reflectiveUtil = reflectiveUtil;

        dynamicWorkBookUtil.validator = validator;
        dynamicWorkBookUtil.reflectiveUtil = reflectiveUtil;
    }


}
