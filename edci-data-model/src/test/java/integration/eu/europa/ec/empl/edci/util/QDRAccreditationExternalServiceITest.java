package integration.eu.europa.ec.empl.edci.util;

import com.apicatalog.jsonld.JsonLdError;
import eu.europa.ec.empl.edci.config.service.BaseConfigService;
import eu.europa.ec.empl.edci.config.service.ProxyConfigService;
import eu.europa.ec.empl.edci.constants.ControlledListConcept;
import eu.europa.ec.empl.edci.constants.DataModelConstants;
import eu.europa.ec.empl.edci.datamodel.jsonld.model.AccreditationDTO;
import eu.europa.ec.empl.edci.datamodel.jsonld.model.Evidence;
import eu.europa.ec.empl.edci.datamodel.jsonld.model.VerifiableCredentialDTO;
import eu.europa.ec.empl.edci.datamodel.jsonld.model.dataTypes.ConceptDTO;
import eu.europa.ec.empl.edci.datamodel.jsonld.model.dataTypes.ConceptSchemeDTO;
import eu.europa.ec.empl.edci.service.QDRAccreditationExternalService;
import eu.europa.ec.empl.edci.util.JsonLdFactoryUtil;
import eu.europa.ec.empl.edci.util.JsonLdUtil;
import eu.europa.ec.empl.edci.util.JsonUtil;
import org.apache.jena.shacl.ValidationReport;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

@RunWith(MockitoJUnitRunner.Silent.class)
public class QDRAccreditationExternalServiceITest {


    @Spy
    private QDRAccreditationExternalService qdrAccreditationExternalService;
    @Spy
    private JsonUtil jsonUtil;
    @Spy
    private JsonLdUtil jsonLdUtil;
    @Spy
    private ProxyConfigService proxyConfigService;

    private final String ACCREDITATION_SAMPLE_ID_NONDATAMODELCOMPLIANT = "http://data.europa.eu/snb/data-dev/accreditation/68999ff3-abee-4d4c-afb9-d85ac8e09cc6";
    private final String ACCREDITATION_SAMPLE_ID_DATAMODELCOMPLIANT = "http://data.europa.eu/snb/data-dev/accreditation/d2455324-332c-4f80-80b8-bd37312341d6";
    private final String ACCREDITATION_ENDPOINT_URL = "https://esco-qdr-dev-searchapi.cogni.zone/europass/qdr-search/accreditation/rdf";

    private URI[] mandatoryContexts = new URI[]{
            URI.create("https://www.w3.org/2018/credentials/v1"),
            URI.create("http://data.europa.eu/snb/model/context/edc-ap")
            //URI.create("http://dev.everisdx.io/datamodel/test/edc-ap-context.jsonld")
    };

    @Before
    public void setUp() throws IOException {
        this.setUpProxy(proxyConfigService);
        this.setUpJena(proxyConfigService);

        String accreditations_frame = "src/main/resources/accreditation/accreditations_frame.jsonld";
        String frameJson = Files.readString(Paths.get(accreditations_frame));

        Mockito.lenient().doReturn(ACCREDITATION_ENDPOINT_URL).when(proxyConfigService).getString(DataModelConstants.Properties.ACCREDITATION_ENDPOINT);

        Mockito.lenient().doReturn(frameJson).when(qdrAccreditationExternalService).getFrame();
        Mockito.lenient().doReturn(jsonLdUtil).when(qdrAccreditationExternalService).getJsonLdUtil();
        Mockito.lenient().doReturn(jsonUtil).when(qdrAccreditationExternalService).getJsonUtil();
        Mockito.lenient().doReturn(jsonUtil).when(jsonLdUtil).getJsonUtil();
        //Mockito.lenient().doReturn(controlledListCommonsService).when(qdrAccreditationExternalService).getControlledListCommonsService();
        Mockito.lenient().doReturn(proxyConfigService).when(qdrAccreditationExternalService).getBaseConfigService();

        Mockito.lenient().doReturn(proxyConfigService).when(jsonLdUtil).getConfigService();


        jsonLdUtil.postConstruct();
    }

    private void setUpProxy(ProxyConfigService proxyConfigService) {
        Mockito.lenient().doReturn("60").when(proxyConfigService).getString("http.https.timeout.seconds", "10");
        Mockito.lenient().doReturn("false").when(proxyConfigService).getString("proxy.http.enabled", "false");
        Mockito.lenient().doReturn("false").when(proxyConfigService).getString("proxy.https.enabled", "false");
        Mockito.lenient().doReturn("").when(proxyConfigService).getString("proxy.http.host", "");
        Mockito.lenient().doReturn(null).when(proxyConfigService).getInteger("proxy.http.port", null);
        Mockito.lenient().doReturn("").when(proxyConfigService).getString("proxy.https.host", "");
        Mockito.lenient().doReturn(null).when(proxyConfigService).getInteger("proxy.https.port", null);
        Mockito.lenient().doReturn("").when(proxyConfigService).getString("proxy.noproxy.regex.url", "");
    }

    private void setUpJena(BaseConfigService baseConfigService) {
        Mockito.lenient().doReturn("application/rdf+xml").when(baseConfigService).getString("jena.default.triples.content.type");
        Mockito.lenient().doReturn(JsonLdUtil.JENA_OFFICIAL_CONTENT_TYPE_RDF_XML).when(baseConfigService).getString("jena.default.triples.content.type", JsonLdUtil.JENA_OFFICIAL_CONTENT_TYPE_RDF_XML);
    }

    @Test
    public void downloadAndValidateAccreditation_shouldPassValidation_whenUsingSampleCompliantURI() throws URISyntaxException, JsonLdError, IOException {
        VerifiableCredentialDTO verifiableCredentialDTO = JsonLdFactoryUtil.getBaseUncompletedVerifiableCredential();
        AccreditationDTO accreditationDTO = this.getQdrAccreditationExternalService().retrieveAccreditationByUri(ACCREDITATION_SAMPLE_ID_DATAMODELCOMPLIANT, "en");
        Evidence evidence = new Evidence();
        evidence.setId(URI.create("urn:evidence:1"));
        ConceptDTO accreditationType = new ConceptDTO();
        accreditationType.setId(URI.create(ControlledListConcept.EVIDENCE_TYPE_ACCREDITATION.getUrl()));
        accreditationType.setInScheme(new ConceptSchemeDTO(ControlledListConcept.EVIDENCE_TYPE_ACCREDITATION.getControlledList().getUrl()));
        evidence.setDcType(accreditationType);
        evidence.setAccreditation(accreditationDTO);
        verifiableCredentialDTO.setEvidence(Arrays.asList(evidence));

        String credentialJson = this.getJsonLdUtil().marshallToCompactString(verifiableCredentialDTO, this.mandatoryContexts);
        ValidationReport validationReport = jsonLdUtil.validateRDF(credentialJson, JsonLdFactoryUtil.accredited_URL);
        Assert.assertTrue(validationReport.conforms());

    }

    @Test
    public void downloadAndValidateAccreditation_shouldFailValidation_whenUsingSampleNonCompliantURI() throws URISyntaxException, JsonLdError, IOException {
        VerifiableCredentialDTO verifiableCredentialDTO = JsonLdFactoryUtil.getBaseUncompletedVerifiableCredential();
        AccreditationDTO accreditationDTO = this.getQdrAccreditationExternalService().retrieveAccreditationByUri(ACCREDITATION_SAMPLE_ID_NONDATAMODELCOMPLIANT, "en");
        Evidence evidence = new Evidence();
        evidence.setId(URI.create("urn:evidence:1"));
        ConceptDTO accreditationType = new ConceptDTO();
        accreditationType.setId(URI.create(ControlledListConcept.EVIDENCE_TYPE_ACCREDITATION.getUrl()));
        accreditationType.setInScheme(new ConceptSchemeDTO(ControlledListConcept.EVIDENCE_TYPE_ACCREDITATION.getControlledList().getUrl()));
        evidence.setDcType(accreditationType);
        evidence.setAccreditation(accreditationDTO);
        verifiableCredentialDTO.setEvidence(Arrays.asList(evidence));

        String credentialJson = this.getJsonLdUtil().marshallToCompactString(verifiableCredentialDTO, this.mandatoryContexts);
        ValidationReport validationReport = jsonLdUtil.validateRDF(credentialJson, JsonLdFactoryUtil.accredited_URL);

        validationReport.getModel().write(System.out);
        
        Assert.assertFalse(validationReport.conforms());

    }

    public QDRAccreditationExternalService getQdrAccreditationExternalService() {
        return qdrAccreditationExternalService;
    }

    public void setQdrAccreditationExternalService(QDRAccreditationExternalService qdrAccreditationExternalService) {
        this.qdrAccreditationExternalService = qdrAccreditationExternalService;
    }

    public JsonUtil getJsonUtil() {
        return jsonUtil;
    }

    public void setJsonUtil(JsonUtil jsonUtil) {
        this.jsonUtil = jsonUtil;
    }

    public JsonLdUtil getJsonLdUtil() {
        return jsonLdUtil;
    }

    public void setJsonLdUtil(JsonLdUtil jsonLdUtil) {
        this.jsonLdUtil = jsonLdUtil;
    }


    public ProxyConfigService getProxyConfigService() {
        return proxyConfigService;
    }

    public void setProxyConfigService(ProxyConfigService proxyConfigService) {
        this.proxyConfigService = proxyConfigService;
    }

}
