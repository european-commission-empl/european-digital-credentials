package eu.europa.ec.empl.edci.issuer.util;

import eu.europa.ec.empl.edci.config.service.BaseConfigService;
import eu.europa.ec.empl.edci.constants.DataModelConstants;
import eu.europa.ec.empl.edci.datamodel.jsonld.model.EuropeanDigitalCredentialDTO;
import eu.europa.ec.empl.edci.datamodel.jsonld.model.IndividualDisplayDTO;
import eu.europa.ec.empl.edci.service.ControlledListCommonsService;
import eu.europa.ec.empl.edci.service.EDCIMessageService;
import eu.europa.ec.empl.edci.service.RDFsparqlBridgeService;
import eu.europa.ec.empl.edci.util.HtmlSanitizerUtil;
import eu.europa.ec.empl.edci.util.ImageUtil;
import eu.europa.ec.empl.edci.util.ThymeleafUtil;
import integration.eu.europa.ec.empl.base.EuropeanDigitalCredentialBaseITest;
import org.apache.commons.codec.binary.Base64;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.Spy;

import javax.xml.bind.JAXBException;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Locale;

public class DiplomaUtilsITest extends EuropeanDigitalCredentialBaseITest {

    private String defaultGenericTemplate = "";
    private String defaultDiplomaSupplementTemplate = "";
    private String xmlCredential = "/src/main/resources-unfiltered/diploma/template/credential-viewer_full.xml";
    private String diplomaPath = "/src/main/resources-unfiltered/diploma/template/diploma_default_generic_thymeleaf.html";
    private String diplomaSupplementPath = "/src/main/resources-unfiltered/diploma/template/diploma_default_diplomaSupplement_thymeleaf.html";

    @Spy
    private DiplomaUtils diplomaUtils;

    @Spy
    private ControlledListCommonsService controlledListCommonsService;

    @Spy
    private RDFsparqlBridgeService rdfSparqlBridgeService;

    @Spy
    private BaseConfigService iConfigService;

    @Spy
    private EDCIMessageService edciMessageService;

    @Spy
    private HtmlSanitizerUtil htmlSanitizerUtil;

    @Spy
    private ThymeleafUtil thymeleafUtil;

    @Spy
    private ImageUtil imageUtil;

    @Before
    public void setUp() throws IOException {
        Mockito.lenient().when(diplomaUtils.getControlledListCommonsService()).thenReturn(controlledListCommonsService);
        Mockito.lenient().when(controlledListCommonsService.getRdfSparqlBridgeService()).thenReturn(rdfSparqlBridgeService);
        Mockito.lenient().when(rdfSparqlBridgeService.getiConfigService()).thenReturn(iConfigService);
        Mockito.lenient().when(diplomaUtils.getEdciMessageService()).thenReturn(edciMessageService);
        Mockito.lenient().when(diplomaUtils.getThymeleafUtil()).thenReturn(thymeleafUtil);
        Mockito.lenient().when(diplomaUtils.getHtmlSanitizerUtil()).thenReturn(htmlSanitizerUtil);
        Mockito.lenient().when(diplomaUtils.getImageUtil()).thenReturn(imageUtil);
        Mockito.lenient().when(imageUtil.getConfigService()).thenReturn(iConfigService);

        Mockito.lenient().doReturn(new HashMap<String, String>() {{
            this.put("diploma.msg.certifiesThat", "Una patata certifica que:");
            this.put("diploma.supplement.title.educational.system", "Information on the national Higher Education system");
            this.put("diploma.supplement.title.educational.note", "Educational system note");
            this.put("diploma.supplement.contents.details", "Programme details");
            this.put("diploma.supplement.contents.grading", "Grading system");
            this.put("diploma.supplement.header", "Europass diploma supplement");
            this.put("diploma.supplement.title.holder", "Holder of the qualification");
            this.put("diploma.supplement.family.name", "Family name");
            this.put("diploma.supplement.first.name", "First name");
            this.put("diploma.supplement.date.birth", "Date of birth (DD/MM/YYYY)");
            this.put("diploma.supplement.student.id", "Student ID number or code");
            this.put("diploma.supplement.title.issuing.org", "Issuing organisation");
            this.put("diploma.supplement.name.institution", "Name of institution");
            this.put("diploma.supplement.country", "Country");
            this.put("diploma.supplement.address", "Address");
            this.put("diploma.supplement.title.qualification", "Qualification");
            this.put("diploma.supplement.qualification.name", "Name of the qualification");
            this.put("diploma.supplement.qualification.title", "Title conferred");
            this.put("diploma.supplement.qualification.field.study", "Main field(s) of study");
            this.put("diploma.supplement.qualification.awarding", "Institution awarding the qualification");
            this.put("diploma.supplement.qualification.awarding.status", "Status");
            this.put("diploma.supplement.qualification.administering", "Institution administering studies");
            this.put("diploma.supplement.qualification.administering.status", "Status");
            this.put("diploma.supplement.qualification.lang", "Language(s) of instruction");
            this.put("diploma.supplement.title.level.qualification", "Level of the qualification");
            this.put("diploma.supplement.level.qualification.nfq", "National Framework of Qualifications level");
            this.put("diploma.supplement.level.qualification.duration", "Official size of programme");
            this.put("diploma.supplement.level.qualification.requirements", "Access requirements");
            this.put("diploma.supplement.hour", "hour(s)");
            this.put("diploma.supplement.day", "day(s)");
            this.put("diploma.supplement.month", "month(s)");
            this.put("diploma.supplement.year", "year(s)");
            this.put("diploma.supplement.title.contents.results", "Contents and results gained");
            this.put("diploma.supplement.contents.mode.study", "Mode of learning");
            this.put("diploma.supplement.contents.requirements", "Programme requirements");
            this.put("diploma.supplement.title.subachievements", "Sub-achievements");
            this.put("diploma.supplement.subachievements.code", "Code");
            this.put("diploma.supplement.subachievements.subject", "Subject");
            this.put("diploma.supplement.subachievements.marks", "Marks");
            this.put("diploma.supplement.subachievements.ects", "ECTS credit points");
            this.put("diploma.supplement.title.function.qualification", "Function of the qualification");
            this.put("diploma.supplement.function.qualification.access", "Access to further study");
            this.put("diploma.supplement.function.qualification.prof.status", "Professional status (if applicable)");
            this.put("diploma.supplement.title.information", "Additional information");
            this.put("diploma.supplement.information.additional", "Additional information");
            this.put("diploma.supplement.information.further", "Further information sources");
        }}).when(edciMessageService).getMessages(Locale.ENGLISH);
        Mockito.lenient().doReturn("http://publications.europa.eu/webapi/rdf/sparql").when(iConfigService).getString(DataModelConstants.CONFIG_PUBLICATIONS_RDF_SPARQL_ENDPOINT);
        Mockito.lenient().doReturn("https://webgate.acceptance.ec.europa.eu/europass/eportfolio/api/office/generate/png").when(iConfigService).getString("png.download.url");

        Path path = Paths.get(".").toAbsolutePath().normalize();
        String diploma = path.toFile().getAbsolutePath() + diplomaPath;
        String diplomaSupplement = path.toFile().getAbsolutePath() + diplomaSupplementPath;
        this.defaultGenericTemplate = Files.readString(Paths.get(diploma));
        this.defaultDiplomaSupplementTemplate = Files.readString(Paths.get(diplomaSupplement));
    }


    @Test
    public void informGenericDefaultDiplomaImageNoLabels_shouldGenerateImage_whenUsingGenericCredentialDTO() throws FileNotFoundException {
        EuropeanDigitalCredentialDTO europeanDigitalCredentialDTO = this.getGenericCredentialDTO();
        diplomaUtils.informDiplomaFromTemplate(europeanDigitalCredentialDTO, defaultGenericTemplate, null, null, false);
        //TODO: Check diploma util
        for (IndividualDisplayDTO displayDTO : europeanDigitalCredentialDTO.getDisplayParameter().getIndividualDisplay()) {
            byte data[] = Base64.decodeBase64(displayDTO.getDisplayDetail().get(0).getImage().getContent());
            Path path = Paths.get(".").toAbsolutePath().normalize();
            String imagePath = path.toFile().getAbsolutePath();
            try (OutputStream stream = new FileOutputStream(imagePath + "/src/test/resources/out/singlePageDiploma.jpeg")) {
                stream.write(data);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    @Test
    public void informGenericDefaultDiplomaSupplementImageNoLabels_shouldGenerateImage_whenUsingGenericCredentialDTO() throws IOException, JAXBException {
        //PENDING to implement new data model to diploma supplement template
        EuropeanDigitalCredentialDTO europeanDigitalCredentialDTO = this.getGenericCredentialDTO();
        diplomaUtils.informDiplomaFromTemplate(europeanDigitalCredentialDTO, defaultDiplomaSupplementTemplate, null, null, true);

        for (IndividualDisplayDTO displayDTO : europeanDigitalCredentialDTO.getDisplayParameter().getIndividualDisplay()) {
            for (int i = 0; i < displayDTO.getDisplayDetail().size(); ++i) {
                //TODO: Check diploma util
                byte data[] = Base64.decodeBase64(displayDTO.getDisplayDetail().get(i).getImage().getContent());
                Path path = Paths.get(".").toAbsolutePath().normalize();
                String imagePath = path.toFile().getAbsolutePath();
                try (OutputStream stream = new FileOutputStream(imagePath + "/src/test/resources/out/diplomaSupplement_" + i + ".png")) {
                    stream.write(data);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

}
