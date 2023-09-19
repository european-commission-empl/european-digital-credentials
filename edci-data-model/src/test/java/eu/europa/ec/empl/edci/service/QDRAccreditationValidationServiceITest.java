package eu.europa.ec.empl.edci.service;

import eu.europa.ec.empl.edci.config.service.BaseConfigService;
import eu.europa.ec.empl.edci.config.service.ProxyConfigService;
import eu.europa.ec.empl.edci.constants.ControlledListConcept;
import eu.europa.ec.empl.edci.constants.DataModelConstants;
import eu.europa.ec.empl.edci.constants.EDCIMessageKeys;
import eu.europa.ec.empl.edci.datamodel.jsonld.model.*;
import eu.europa.ec.empl.edci.datamodel.jsonld.model.dataTypes.ConceptDTO;
import eu.europa.ec.empl.edci.datamodel.validation.ValidationResult;
import eu.europa.ec.empl.edci.util.CredentialUtil;
import eu.europa.ec.empl.edci.util.JsonLdUtil;
import eu.europa.ec.empl.edci.util.JsonUtil;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

@RunWith(MockitoJUnitRunner.Silent.class)
public class QDRAccreditationValidationServiceITest {

    private String ACCREDITED_CREDENTIAL_PATH = "src/test/resources/accreditation/accredited_credential.jsonld";
    private String FRAMED_ACC_LIMITEQF_ETERNAL = "src/test/resources/accreditation/framed_accreditation-limitEQFlevel-eternal.json";
    private String FRAMED_ACC_LIMITQUALIFICATION_ETERNAL = "src/test/resources/accreditation/framed_accreditation-limitQualification-eternal.json";
    private String FRAMED_ACC_LIMITFIELD_ETERNAL = "src/test/resources/accreditation/framed_accreditation-limitField-eternal.json";
    private String FRAMED_ACC_LIMITJURISDICTION_ETERNAL = "src/test/resources/accreditation/framed_accreditation-limitJurisdiction-eternal.json";
    private String FRAMED_ACC_FUTURE = "src/test/resources/accreditation/framed_accreditation-future.json";
    private String FRAMED_ACC_EXPIRED = "src/test/resources/accreditation/framed_accreditation-expired.json";
    private String ACC_ID_LIMITEQF_ETERNAL = "http://data.europa.eu/snb/data-dev/accreditation/6aae347d-88a9-4d49-8f7c-e87e9d7b407e";
    private String ACC_ID_ISCEDF_ETERNAL = "http://data.europa.eu/snb/data-dev/accreditation/0d358260-fae1-4755-89ab-1309b20b02f9";
    private String ACC_ID_EXPIRED = "http://data.europa.eu/snb/data-dev/accreditation/eb481db5-56e5-461d-956f-96c0c55a8c36";

    private final String ACCREDITATION_ENDPOINT_URL = "https://esco-qdr-dev-searchapi.cogni.zone/europass/qdr-search/accreditation/rdf";


    @Spy
    @InjectMocks
    private QDRAccreditationValidationService qdrAccreditationValidationService;
    @Spy
    private JsonUtil jsonUtil;
    @Spy
    private CredentialUtil credentialUtil;
    @Spy
    private ControlledListCommonsService controlledListCommonsService;
    @Spy
    private QDRAccreditationExternalService qdrAccreditationExternalService;
    @Spy
    private ProxyConfigService baseConfigService;
    @Spy
    private RDFsparqlBridgeService rdFsparqlBridgeService;
    @Spy
    private JsonLdUtil jsonLdUtil;


    @Before
    public void setUp() {
//http://publications.europa.eu/webapi/rdf/sparql
        Mockito.lenient().doReturn(DataModelConstants.Defaults.CONFIG_PUBLICATIONS_RDF_SPARQL_ENDPOINT)
                .when(baseConfigService).getString(DataModelConstants.CONFIG_PUBLICATIONS_RDF_SPARQL_ENDPOINT,
                DataModelConstants.Defaults.CONFIG_PUBLICATIONS_RDF_SPARQL_ENDPOINT);
        Mockito.lenient().doReturn(rdFsparqlBridgeService).when(controlledListCommonsService).getRdfSparqlBridgeService();
        Mockito.lenient().doReturn(baseConfigService).when(rdFsparqlBridgeService).getiConfigService();


    }

    private List<Evidence> generateEvidence(AccreditationDTO accreditationDTO) {
        Evidence evidence = new Evidence("urn:evidence:1");
        evidence.setDcType(ControlledListConcept.asConceptDTO(ControlledListConcept.EVIDENCE_TYPE_ACCREDITATION));
        evidence.setAccreditation(accreditationDTO);
        return Arrays.asList(evidence);
    }

    private void mockExternalServices() {
        Mockito.lenient().doReturn("en").when(controlledListCommonsService).searchLanguageISO639ByConcept(Mockito.any(ConceptDTO.class));
        Mockito.lenient().doReturn(true).when(qdrAccreditationExternalService).doesAccreditationExist(Mockito.anyString(), Mockito.any());
    }

    private void setUpExternalServices() throws IOException {
        this.setUpJena(baseConfigService);
        this.setUpProxy(baseConfigService);
        String accreditations_frame = "src/main/resources/accreditation/accreditations_frame.jsonld";
        String frameJson = Files.readString(Paths.get(accreditations_frame));
        Mockito.lenient().doReturn(baseConfigService).when(qdrAccreditationExternalService).getBaseConfigService();
        Mockito.lenient().doReturn(baseConfigService).when(jsonLdUtil).getConfigService();
        Mockito.lenient().doReturn(frameJson).when(qdrAccreditationExternalService).getFrame();
        Mockito.lenient().doReturn(jsonLdUtil).when(qdrAccreditationExternalService).getJsonLdUtil();
        Mockito.lenient().doReturn(jsonUtil).when(qdrAccreditationExternalService).getJsonUtil();
        Mockito.lenient().doReturn(jsonUtil).when(jsonLdUtil).getJsonUtil();
        Mockito.lenient().doReturn(ACCREDITATION_ENDPOINT_URL).when(baseConfigService).getString(DataModelConstants.Properties.ACCREDITATION_ENDPOINT);
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
    public void isCredentialCovered_shouldGiveTrueAndCallCheckQualifications_whenUsinglimitQualificationEternalAccreditation() throws IOException {
        EuropeanDigitalCredentialDTO europeanDigitalCredentialDTO = jsonUtil.unMarshall(Files.readAllBytes(Paths.get(ACCREDITED_CREDENTIAL_PATH)), EuropeanDigitalCredentialDTO.class);
        AccreditationDTO accreditationDTO = jsonUtil.unMarshall(Files.readAllBytes(Paths.get(FRAMED_ACC_LIMITQUALIFICATION_ETERNAL)), AccreditationDTO.class);
        europeanDigitalCredentialDTO.setEvidence(this.generateEvidence(accreditationDTO));
        this.mockExternalServices();
        Mockito.lenient().doReturn(accreditationDTO).when(qdrAccreditationExternalService).retrieveAccreditationByUri(Mockito.anyString(), Mockito.anyString());
        ValidationResult validationResult = qdrAccreditationValidationService.isCredentialCovered(europeanDigitalCredentialDTO);
        Mockito.verify(qdrAccreditationValidationService, Mockito.times(1))
                .checkQualifications(Mockito.any(LearningAchievementSpecificationDTO.class), Mockito.any(AccreditationDTO.class));
        Assert.assertTrue(validationResult.isValid());
    }

    @Test
    public void isCredentialCovered_shouldGiveTrueAndCallCheckEQFLevel_whenUsinglimitEQFEternalAccreditation() throws IOException {
        EuropeanDigitalCredentialDTO europeanDigitalCredentialDTO = jsonUtil.unMarshall(Files.readAllBytes(Paths.get(ACCREDITED_CREDENTIAL_PATH)), EuropeanDigitalCredentialDTO.class);
        AccreditationDTO accreditationDTO = jsonUtil.unMarshall(Files.readAllBytes(Paths.get(FRAMED_ACC_LIMITEQF_ETERNAL)), AccreditationDTO.class);
        europeanDigitalCredentialDTO.setEvidence(this.generateEvidence(accreditationDTO));
        this.mockExternalServices();
        Mockito.lenient().doReturn(accreditationDTO).when(qdrAccreditationExternalService).retrieveAccreditationByUri(Mockito.anyString(), Mockito.anyString());
        ValidationResult validationResult = qdrAccreditationValidationService.isCredentialCovered(europeanDigitalCredentialDTO);
        Mockito.verify(qdrAccreditationValidationService, Mockito.times(1))
                .checkEQFLevel(Mockito.any(QualificationDTO.class), Mockito.any(AccreditationDTO.class));
        Assert.assertTrue(validationResult.isValid());
    }

    @Test
    public void isCredentialCovered_shouldGiveTrueAndCallCheckISCEDFCode_whenUsinglimitFieldEternalAccreditation() throws IOException {
        EuropeanDigitalCredentialDTO europeanDigitalCredentialDTO = jsonUtil.unMarshall(Files.readAllBytes(Paths.get(ACCREDITED_CREDENTIAL_PATH)), EuropeanDigitalCredentialDTO.class);
        AccreditationDTO accreditationDTO = jsonUtil.unMarshall(Files.readAllBytes(Paths.get(FRAMED_ACC_LIMITFIELD_ETERNAL)), AccreditationDTO.class);
        europeanDigitalCredentialDTO.setEvidence(this.generateEvidence(accreditationDTO));
        this.mockExternalServices();
        Mockito.lenient().doReturn(accreditationDTO).when(qdrAccreditationExternalService).retrieveAccreditationByUri(Mockito.anyString(), Mockito.anyString());
        ValidationResult validationResult = qdrAccreditationValidationService.isCredentialCovered(europeanDigitalCredentialDTO);
        Mockito.verify(qdrAccreditationValidationService, Mockito.times(1))
                .checkISCEDFCode(Mockito.any(LearningAchievementSpecificationDTO.class), Mockito.any(AccreditationDTO.class), Mockito.anyString());
        Assert.assertTrue(validationResult.isValid());
    }

    @Test
    public void isCredentialCovered_shouldGiveTrueAndCallCheckJurisdiction_whenUsinglimitJurisdictionEternalAccreditation() throws IOException {
        EuropeanDigitalCredentialDTO europeanDigitalCredentialDTO = jsonUtil.unMarshall(Files.readAllBytes(Paths.get(ACCREDITED_CREDENTIAL_PATH)), EuropeanDigitalCredentialDTO.class);
        AccreditationDTO accreditationDTO = jsonUtil.unMarshall(Files.readAllBytes(Paths.get(FRAMED_ACC_LIMITJURISDICTION_ETERNAL)), AccreditationDTO.class);
        europeanDigitalCredentialDTO.setEvidence(this.generateEvidence(accreditationDTO));
        this.mockExternalServices();
        Mockito.lenient().doReturn(accreditationDTO).when(qdrAccreditationExternalService).retrieveAccreditationByUri(Mockito.anyString(), Mockito.anyString());
        ValidationResult validationResult = qdrAccreditationValidationService.isCredentialCovered(europeanDigitalCredentialDTO);
        Mockito.verify(qdrAccreditationValidationService, Mockito.times(1))
                .checkJurisdiction(Mockito.any(AwardingProcessDTO.class), Mockito.any(AccreditationDTO.class), Mockito.anyString());
        Assert.assertTrue(validationResult.isValid());
    }


    @Test
    public void isCredentialCovered_shouldGiveFalse_whenUsingFutureAccreditation() throws IOException {
        EuropeanDigitalCredentialDTO europeanDigitalCredentialDTO = jsonUtil.unMarshall(Files.readAllBytes(Paths.get(ACCREDITED_CREDENTIAL_PATH)), EuropeanDigitalCredentialDTO.class);
        AccreditationDTO accreditationDTO = jsonUtil.unMarshall(Files.readAllBytes(Paths.get(FRAMED_ACC_FUTURE)), AccreditationDTO.class);
        europeanDigitalCredentialDTO.setEvidence(this.generateEvidence(accreditationDTO));
        this.mockExternalServices();
        Mockito.lenient().doReturn(accreditationDTO).when(qdrAccreditationExternalService).retrieveAccreditationByUri(Mockito.anyString(), Mockito.anyString());
        ValidationResult validationResult = qdrAccreditationValidationService.isCredentialCovered(europeanDigitalCredentialDTO);
        Assert.assertTrue(validationResult.getValidationErrors().stream().anyMatch(validationError -> validationError.getErrorKey().equals(EDCIMessageKeys.Acreditation.CREDENTIAL_ISSUANCE_DATE_NOT_COVERED)));
        Assert.assertFalse(validationResult.isValid());
    }

    @Test
    public void isCredentialCovered_shouldGiveFalse_whenUsingExpiredAccreditation() throws IOException {
        EuropeanDigitalCredentialDTO europeanDigitalCredentialDTO = jsonUtil.unMarshall(Files.readAllBytes(Paths.get(ACCREDITED_CREDENTIAL_PATH)), EuropeanDigitalCredentialDTO.class);
        AccreditationDTO accreditationDTO = jsonUtil.unMarshall(Files.readAllBytes(Paths.get(FRAMED_ACC_EXPIRED)), AccreditationDTO.class);
        europeanDigitalCredentialDTO.setEvidence(this.generateEvidence(accreditationDTO));
        this.mockExternalServices();
        Mockito.lenient().doReturn(accreditationDTO).when(qdrAccreditationExternalService).retrieveAccreditationByUri(Mockito.anyString(), Mockito.anyString());
        ValidationResult validationResult = qdrAccreditationValidationService.isCredentialCovered(europeanDigitalCredentialDTO);
        Assert.assertTrue(validationResult.getValidationErrors().stream().anyMatch(validationError -> validationError.getErrorKey().equals(EDCIMessageKeys.Acreditation.CREDENTIAL_ISSUANCE_DATE_NOT_COVERED)));
        Assert.assertFalse(validationResult.isValid());
    }

    @Test
    public void isCredentialCovered_shouldGiveTrueAndCallCheckEQFLevel_whenUsingLimitEQFExternalID() throws IOException {
        EuropeanDigitalCredentialDTO europeanDigitalCredentialDTO = jsonUtil.unMarshall(Files.readAllBytes(Paths.get(ACCREDITED_CREDENTIAL_PATH)), EuropeanDigitalCredentialDTO.class);
        this.setUpExternalServices();
        AccreditationDTO accreditationDTO = qdrAccreditationExternalService.retrieveAccreditationByUri(ACC_ID_LIMITEQF_ETERNAL, "en");
        europeanDigitalCredentialDTO.setEvidence(this.generateEvidence(accreditationDTO));
        ValidationResult validationResult = qdrAccreditationValidationService.isCredentialCovered(europeanDigitalCredentialDTO);
        Mockito.verify(qdrAccreditationValidationService, Mockito.times(1))
                .checkEQFLevel(Mockito.any(QualificationDTO.class), Mockito.any(AccreditationDTO.class));
        Assert.assertTrue(validationResult.isValid());
    }

    @Test
    public void isCredentialCovered_shouldGiveTrueAndCallCheckISCEDF_whenUsingISCEDF_ExternalID() throws IOException {
        EuropeanDigitalCredentialDTO europeanDigitalCredentialDTO = jsonUtil.unMarshall(Files.readAllBytes(Paths.get(ACCREDITED_CREDENTIAL_PATH)), EuropeanDigitalCredentialDTO.class);
        this.setUpExternalServices();
        AccreditationDTO accreditationDTO = qdrAccreditationExternalService.retrieveAccreditationByUri(ACC_ID_ISCEDF_ETERNAL, "en");
        europeanDigitalCredentialDTO.setEvidence(this.generateEvidence(accreditationDTO));
        ValidationResult validationResult = qdrAccreditationValidationService.isCredentialCovered(europeanDigitalCredentialDTO);
        Mockito.verify(qdrAccreditationValidationService, Mockito.times(1))
                .checkISCEDFCode(Mockito.any(LearningAchievementSpecificationDTO.class), Mockito.any(AccreditationDTO.class), Mockito.anyString());
        Assert.assertTrue(validationResult.isValid());
    }

    @Test
    public void isCredentialCovered_shouldGiveFalse_whenUsingExpiredAccreditationID() throws IOException {
        EuropeanDigitalCredentialDTO europeanDigitalCredentialDTO = jsonUtil.unMarshall(Files.readAllBytes(Paths.get(ACCREDITED_CREDENTIAL_PATH)), EuropeanDigitalCredentialDTO.class);
        this.setUpExternalServices();
        AccreditationDTO accreditationDTO = qdrAccreditationExternalService.retrieveAccreditationByUri(ACC_ID_EXPIRED, "en");
        europeanDigitalCredentialDTO.setEvidence(this.generateEvidence(accreditationDTO));
        ValidationResult validationResult = qdrAccreditationValidationService.isCredentialCovered(europeanDigitalCredentialDTO);
        Assert.assertTrue(validationResult.getValidationErrors().stream().anyMatch(validationError -> validationError.getErrorKey().equals(EDCIMessageKeys.Acreditation.CREDENTIAL_ISSUANCE_DATE_NOT_COVERED)));
        Assert.assertFalse(validationResult.isValid());

    }
    
}
