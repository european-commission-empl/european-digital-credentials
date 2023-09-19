package eu.europa.ec.empl.edci.issuer.service.consumers;

import eu.europa.ec.empl.edci.config.service.ProxyConfigService;
import eu.europa.ec.empl.edci.constants.ControlledListConcept;
import eu.europa.ec.empl.edci.constants.DataModelConstants;
import eu.europa.ec.empl.edci.context.ConsumerContext;
import eu.europa.ec.empl.edci.datamodel.jsonld.model.AccreditationDTO;
import eu.europa.ec.empl.edci.datamodel.jsonld.model.EuropeanDigitalCredentialDTO;
import eu.europa.ec.empl.edci.datamodel.jsonld.model.Evidence;
import eu.europa.ec.empl.edci.datamodel.jsonld.model.dataTypes.ConceptDTO;
import eu.europa.ec.empl.edci.datamodel.jsonld.model.dataTypes.ConceptSchemeDTO;
import eu.europa.ec.empl.edci.datamodel.upload.EuropeanDigitalCredentialUploadDTO;
import eu.europa.ec.empl.edci.service.ControlledListCommonsService;
import eu.europa.ec.empl.edci.service.QDRAccreditationExternalService;
import eu.europa.ec.empl.edci.util.CredentialUtil;
import eu.europa.ec.empl.edci.util.JsonLdFactoryUtil;
import eu.europa.ec.empl.edci.util.JsonLdUtil;
import eu.europa.ec.empl.edci.util.JsonUtil;
import integration.eu.europa.ec.empl.base.AbstractIntegrationBaseTest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

public class AccreditationConsumerITest extends AbstractIntegrationBaseTest {

    @InjectMocks
    public AccreditationConsumer accreditationConsumer;

    @Spy
    public QDRAccreditationExternalService accreditationExternalService;

    @Spy
    public JsonLdUtil jsonLdUtil;

    @Mock
    public ControlledListCommonsService controlledListCommonsService;

    @Spy
    public CredentialUtil credentialUtil;

    @Spy
    public JsonUtil jsonUtil;

    @Spy
    public ProxyConfigService issuerConfigService;

    private final String ACCREDITATION_SAMPLE_ID = "http://data.europa.eu/snb/data-dev/accreditation/68999ff3-abee-4d4c-afb9-d85ac8e09cc6";
    private final String ACCREDITATION_ENDPOINT_URL = "https://esco-qdr-dev-searchapi.cogni.zone/europass/qdr-search/accreditation/rdf";

    @Before
    public void setUp() throws IOException {
        this.setUpProxy(issuerConfigService);
        this.setUpJena(issuerConfigService);

        String accreditations_frame = "../../edci-data-model/src/main/resources/accreditation/accreditations_frame.jsonld";
        String frameJson = Files.readString(Paths.get(accreditations_frame));

        Mockito.lenient().doReturn(ACCREDITATION_ENDPOINT_URL).when(issuerConfigService).getString(DataModelConstants.Properties.ACCREDITATION_ENDPOINT);

        Mockito.lenient().doReturn(frameJson).when(accreditationExternalService).getFrame();
        Mockito.lenient().doReturn(jsonLdUtil).when(accreditationExternalService).getJsonLdUtil();
        Mockito.lenient().doReturn(jsonUtil).when(accreditationExternalService).getJsonUtil();
        Mockito.lenient().doReturn(controlledListCommonsService).when(accreditationExternalService).getControlledListCommonsService();
        Mockito.lenient().doReturn(issuerConfigService).when(accreditationExternalService).getBaseConfigService();

        Mockito.lenient().doReturn(issuerConfigService).when(jsonLdUtil).getConfigService();


        jsonLdUtil.postConstruct();
    }

    @Test
    public void accept_credentialShouldHaveNotNullAccreditationTitle() throws URISyntaxException {
        EuropeanDigitalCredentialDTO europeanDigitalCredentialDTO = JsonLdFactoryUtil.getBaseUncompletedVerifiableCredential();

        Mockito.when(controlledListCommonsService.searchLanguageISO639ByConcept(Mockito.any(ConceptDTO.class))).thenReturn("en");
        Evidence evidence = new Evidence();
        evidence.setId(URI.create("urn:evidence:1"));
        ConceptDTO accreditationCredType = new ConceptDTO();
        accreditationCredType.setId(URI.create(ControlledListConcept.CREDENTIAL_TYPE_ACCREDITATION.getUrl()));
        accreditationCredType.setInScheme(new ConceptSchemeDTO(ControlledListConcept.EVIDENCE_TYPE_ACCREDITATION.getControlledList().getUrl()));
        europeanDigitalCredentialDTO.setCredentialProfiles(Arrays.asList(accreditationCredType));
        ConceptDTO accreditationEvidenceType = new ConceptDTO();
        accreditationEvidenceType.setId(URI.create(ControlledListConcept.EVIDENCE_TYPE_ACCREDITATION.getUrl()));
        accreditationEvidenceType.setInScheme(new ConceptSchemeDTO(ControlledListConcept.EVIDENCE_TYPE_ACCREDITATION.getControlledList().getUrl()));
        evidence.setDcType(accreditationEvidenceType);
        AccreditationDTO accreditationDTO = new AccreditationDTO();
        accreditationDTO.setId(URI.create(ACCREDITATION_SAMPLE_ID));
        evidence.setAccreditation(accreditationDTO);
        europeanDigitalCredentialDTO.setEvidence(Arrays.asList(evidence));

        EuropeanDigitalCredentialUploadDTO europeanDigitalCredentialUploadDTO = new EuropeanDigitalCredentialUploadDTO();
        europeanDigitalCredentialUploadDTO.setCredential(europeanDigitalCredentialDTO);

        this.accreditationConsumer.accept(new ConsumerContext(europeanDigitalCredentialUploadDTO));

        Assert.assertNotNull(europeanDigitalCredentialDTO.getEvidence().get(0).getAccreditation().getTitle());
    }
}
