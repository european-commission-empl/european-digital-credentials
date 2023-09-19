package integration.eu.europa.ec.empl.edci.util;

import com.apicatalog.jsonld.JsonLdError;
import eu.europa.ec.empl.edci.config.service.ProxyConfigService;
import eu.europa.ec.empl.edci.constants.ControlledListConcept;
import eu.europa.ec.empl.edci.constants.DataModelConstants;
import eu.europa.ec.empl.edci.datamodel.jsonld.model.AccreditationDTO;
import eu.europa.ec.empl.edci.datamodel.jsonld.model.EuropeanDigitalCredentialDTO;
import eu.europa.ec.empl.edci.datamodel.jsonld.model.Evidence;
import eu.europa.ec.empl.edci.datamodel.jsonld.model.VerifiableCredentialDTO;
import eu.europa.ec.empl.edci.datamodel.jsonld.model.dataTypes.ConceptDTO;
import eu.europa.ec.empl.edci.datamodel.jsonld.model.dataTypes.ConceptSchemeDTO;
import eu.europa.ec.empl.edci.service.ControlledListCommonsService;
import eu.europa.ec.empl.edci.service.RDFsparqlBridgeService;
import eu.europa.ec.empl.edci.util.JsonLdFactoryUtil;
import eu.europa.ec.empl.edci.util.JsonLdUtil;
import eu.europa.ec.empl.edci.util.JsonUtil;
import org.apache.jena.shacl.ValidationReport;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

@RunWith(MockitoJUnitRunner.Silent.class)
public class JsonLdUtilITest {

    @InjectMocks
    @Spy
    private JsonLdUtil jsonLdUtil;

    @Spy
    private ControlledListCommonsService controlledListCommonsService;

    @Spy
    private RDFsparqlBridgeService rdfSparqlBridgeService;

    @Spy
    private ProxyConfigService iConfigService;

    @Spy
    private JsonUtil jsonUtil;

    @Before
    public void setUp() throws IOException {
        Mockito.lenient().when(controlledListCommonsService.getRdfSparqlBridgeService()).thenReturn(rdfSparqlBridgeService);
        Mockito.lenient().when(rdfSparqlBridgeService.getiConfigService()).thenReturn(iConfigService);
        Mockito.lenient().when(jsonLdUtil.getConfigService()).thenReturn(iConfigService);
        Mockito.lenient().doReturn("60").when(iConfigService).getString("http.https.timeout.seconds", "10");
        Mockito.lenient().doReturn("false").when(iConfigService).getString("proxy.http.enabled", "false");
        Mockito.lenient().doReturn("false").when(iConfigService).getString("proxy.https.enabled", "false");
        Mockito.lenient().doReturn("").when(iConfigService).getString("proxy.http.host", "");
        Mockito.lenient().doReturn(null).when(iConfigService).getInteger("proxy.http.port", null);
        Mockito.lenient().doReturn("").when(iConfigService).getString("proxy.https.host", "");
        Mockito.lenient().doReturn(null).when(iConfigService).getInteger("proxy.https.port", null);
        Mockito.lenient().doReturn("").when(iConfigService).getString("proxy.noproxy.regex.url", "");
        Mockito.lenient().doReturn("application/rdf+xml").when(iConfigService).getString("jena.default.triples.content.type");
        Mockito.lenient().doReturn("http://publications.europa.eu/webapi/rdf/sparql").when(iConfigService).getString(DataModelConstants.CONFIG_PUBLICATIONS_RDF_SPARQL_ENDPOINT);
        Mockito.lenient().doReturn(JsonLdUtil.JENA_OFFICIAL_CONTENT_TYPE_RDF_XML).when(iConfigService).getString("jena.default.triples.content.type", JsonLdUtil.JENA_OFFICIAL_CONTENT_TYPE_RDF_XML);
        jsonLdUtil.postConstruct();
    }

    private String simpleCred = "{\"id\":\"urn:credential:0bd26efe-6605-468b-845b-5acf00fabb20\",\"type\":[\"VerifiableCredential\",\"Generic\"],\"expirationDate\":1657802276077,\"issuanceDate\":1657715876077,\"@context\":\"https://www.w3.org/2018/credentials/v1\"}";
    private String cred1_jsonld = "src/test/resources/jsonld/cred1.jsonld";
    private String credFlatten_jsonld = "src/test/resources/jsonld/credFlatten.jsonld";
    private String credNoType = "src/test/resources/jsonld/credNoType.jsonld";
    private String accreditations_frame = "src/main/resources/accreditation/accreditations_frame.jsonld";
    private String cred2_jsonld_1context = "src/test/resources/jsonld/cred2_1context.jsonld";

    private String credContext = "https://www.w3.org/2018/credentials/v1";
    private String extendedContext = "https://www.w3.org/2018/credentials/examples/v1";
    private String customContext = "http://dev.everisdx.io/datamodel/context/edc-ap-jsonld.jsonld";
    private String shape = "http://dev.everisdx.io/datamodel/ttl/EDC-constraints.ttl";
    private String generic_ttl = "http://dev.everisdx.io/datamodel/ttl/EDC-generic.ttl";
    private String semiOfficialContext = "/src/main/resources/jsonld/SemiOfficialContext.jsonld";
    private String xmlCredential = "/src/main/resources/jsonld/validXMLCredential.xml";
    private String extendedContextFile = "/src/main/resources/jsonld/extendedContext.jsonld";
    private String minExtendedContextFile = "/src/main/resources/jsonld/minExtendedContext.jsonld";
    private String shapeFile = "/src/main/resources/jsonld/shape.ttl";
    private String signedCred = "src/test/resources/jsonld/credential-signed.jsonld";
    private String cred_noLegalName = "src/test/resources/jsonld/cred_noLegalName_shouldFail.jsonld";
    private String cred_shouldValidate = "src/test/resources/jsonld/credential_shouldValidate.jsonld";
    private String sampleAccreditation = "src/test/resources/jsonld/Sample.jsonld";
    private String sampleAccreditation2 = "src/test/resources/jsonld/ams-json-ld.jsonld";

    private String accreditation_sample_extended = "src/test/resources/accreditation/ams-json-ld-expanded.jsonld";

    private URI[] mandatoryContexts = new URI[]{
            URI.create("https://www.w3.org/2018/credentials/v1"),
            //URI.create("http://data.europa.eu/snb/model/context/edc-ap")
            URI.create("http://dev.everisdx.io/datamodel/test/edc-ap")
    };

    @Test
    public void toJSONString_shouldGenerateJsonLDString_whenUsingVerifiableCredentialDTO() throws Exception {
        VerifiableCredentialDTO verifiableCredentialDTO = JsonLdFactoryUtil.getBaseUncompletedVerifiableCredential();
        String result = this.getJsonLdUtil().marshallToCompactString(verifiableCredentialDTO, this.mandatoryContexts);
        Assert.assertNotNull(result);
    }


    @Test
    public void validateRDF_ShouldFailValidate() throws Exception {
        String credentialJson = Files.readString(Paths.get(cred1_jsonld));
        EuropeanDigitalCredentialDTO europeanDigitalCredentialDTO = jsonLdUtil.getJsonUtil().unMarshall(credentialJson, EuropeanDigitalCredentialDTO.class);
        ValidationReport validationReport = jsonLdUtil.validateRDF(credentialJson, JsonLdFactoryUtil.generic_full_URL);
        Assert.assertFalse(validationReport.conforms());
    }

    @Test
    public void validateRDF_ShouldPassValidate() throws Exception {
        VerifiableCredentialDTO verifiableCredentialDTO = JsonLdFactoryUtil.getBaseUncompletedVerifiableCredential();
        String credentialJson = this.getJsonLdUtil().marshallToCompactString(verifiableCredentialDTO, this.mandatoryContexts);
        ValidationReport validationReport = jsonLdUtil.validateRDF(credentialJson, JsonLdFactoryUtil.generic_full_URL);
        Assert.assertTrue(validationReport.conforms());
    }


    @Test
    public void frameTest() throws IOException, JsonLdError {
        String accreditationJson = Files.readString(Paths.get(accreditation_sample_extended));
        String frameJson = Files.readString(Paths.get(accreditations_frame));
        String framedAccreditation = getJsonLdUtil().doFrame(accreditationJson, frameJson);
        AccreditationDTO accreditationDTO = getJsonUtil().unMarshall(framedAccreditation, AccreditationDTO.class);
        Assert.assertNotNull(accreditationDTO);
    }


    @Test
    public void frameTestEndpoint() throws IOException, JsonLdError {
        String accreditationJson = Files.readString(Paths.get("src/test/resources/accreditation/acc_sample_endpoint.jsonld"));
        String frameJson = Files.readString(Paths.get(accreditations_frame));
        String framedAccreditation = getJsonLdUtil().doFrame(accreditationJson, frameJson);
        AccreditationDTO accreditationDTO = getJsonUtil().unMarshall(framedAccreditation, AccreditationDTO.class);
        Assert.assertNotNull(accreditationDTO);
    }

    @Test
    public void frameTestEndpoint_ShouldPassValidate_WhileContainingAccreditation() throws Exception {
        VerifiableCredentialDTO verifiableCredentialDTO = JsonLdFactoryUtil.getBaseUncompletedVerifiableCredential();
        String accreditationJson = Files.readString(Paths.get("src/test/resources/accreditation/acc_sample_endpoint.jsonld"));
        String frameJson = Files.readString(Paths.get(accreditations_frame));
        String framedAccreditation = getJsonLdUtil().doFrame(accreditationJson, frameJson);
        AccreditationDTO accreditationDTO = getJsonUtil().unMarshall(framedAccreditation, AccreditationDTO.class);
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


   /* @Test
    public void validateRDF_ShouldPassValidate_WhileContainingAccreditation() throws Exception {
        VerifiableCredentialDTO verifiableCredentialDTO = JsonLdFactoryUtil.getBaseUncompletedVerifiableCredential();
        String accreditationJson = Files.readString(Paths.get(accreditation_sample_extended));
        String frameJson = Files.readString(Paths.get(accreditations_frame));
        String framedAccreditation = getJsonLdUtil().doFrame(accreditationJson, frameJson);
        AccreditationDTO accreditationDTO = getJsonUtil().unMarshall(framedAccreditation, AccreditationDTO.class);
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
    }*/

    public JsonLdUtil getJsonLdUtil() {
        return jsonLdUtil;
    }

    public void setJsonLdUtil(JsonLdUtil jsonLdUtil) {
        this.jsonLdUtil = jsonLdUtil;
    }

    public JsonUtil getJsonUtil() {
        return jsonUtil;
    }

    public void setJsonUtil(JsonUtil jsonUtil) {
        this.jsonUtil = jsonUtil;
    }

    private void assertExtendedVerifiableCredentialJsonLDString(String json) {
        Assert.assertTrue(json.contains("@context"));
        Assert.assertTrue(json.contains("expirationDate"));
        Assert.assertTrue(json.contains("id"));
        Assert.assertTrue(json.contains("issuanceDate"));
        Assert.assertTrue(json.contains("type"));
        Assert.assertTrue(json.contains("issued"));
        Assert.assertTrue(json.contains("credentialSubject"));
        Assert.assertTrue(json.contains("issuer"));
        Assert.assertTrue(json.contains("givenName"));
        Assert.assertTrue(json.contains("familyName"));
    }

    private void assertVerifiableCredentialJsonLDString(String json) {
        Assert.assertTrue(json.contains("@context"));
        Assert.assertTrue(json.contains("expirationDate"));
        Assert.assertTrue(json.contains("id"));
        Assert.assertTrue(json.contains("issuanceDate"));
        Assert.assertTrue(json.contains("type"));
        Assert.assertTrue(json.contains("issued"));
        Assert.assertTrue(json.contains("credentialSubject"));
        Assert.assertTrue(json.contains("issuer"));
    }

    private void assertExtendedVerifiableCredentialDTO(VerifiableCredentialDTO verifiableCredentialDTO) {
        Assert.assertNotNull(verifiableCredentialDTO);
        Assert.assertNotNull(verifiableCredentialDTO.getJsonLdContext());
        Assert.assertNotNull(verifiableCredentialDTO.getExpirationDate());
        Assert.assertNotNull(verifiableCredentialDTO.getId());
        Assert.assertNotNull(verifiableCredentialDTO.getIssuanceDate());
        //Assert.assertNotNull(verifiableCredentialDTO.getType());
        Assert.assertNotNull(verifiableCredentialDTO.getIssued());
        Assert.assertNotNull(verifiableCredentialDTO.getCredentialSubject());
        Assert.assertNotNull(verifiableCredentialDTO.getIssuer());
        Assert.assertNotNull(verifiableCredentialDTO.getCredentialSubject().getGivenName());
        Assert.assertNotNull(verifiableCredentialDTO.getCredentialSubject().getFamilyName());
    }

    private void assertVerifiableCredentialDTO(VerifiableCredentialDTO verifiableCredentialDTO) {
        Assert.assertNotNull(verifiableCredentialDTO);
        Assert.assertNotNull(verifiableCredentialDTO.getJsonLdContext());
        Assert.assertNotNull(verifiableCredentialDTO.getExpirationDate());
        Assert.assertNotNull(verifiableCredentialDTO.getId());
        Assert.assertNotNull(verifiableCredentialDTO.getIssuanceDate());
        //Assert.assertNotNull(verifiableCredentialDTO.getType());
        Assert.assertNotNull(verifiableCredentialDTO.getIssued());
        Assert.assertNotNull(verifiableCredentialDTO.getCredentialSubject());
        Assert.assertNotNull(verifiableCredentialDTO.getIssuer());
    }

    private URI getURIFromFile(String pathString) {
        Path path = Paths.get(".").toAbsolutePath().normalize();
        String contextFile = path.toFile().getAbsolutePath() + pathString;

        return new File(contextFile).toURI();
    }
}
