package eu.europa.ec.empl.edci.service;

import eu.europa.ec.empl.edci.constants.ControlledListConcept;
import eu.europa.ec.empl.edci.datamodel.controlledList.RDFConcept;
import eu.europa.ec.empl.edci.datamodel.controlledList.RDFConceptScheme;
import eu.europa.ec.empl.edci.datamodel.jsonld.model.dataTypes.ConceptDTO;
import eu.europa.ec.empl.edci.datamodel.jsonld.model.dataTypes.LiteralMap;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.rdf.model.ModelFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.net.URI;
import java.util.*;

@RunWith(MockitoJUnitRunner.Silent.class)
public class ControlledListCommonsServiceTest {

    @InjectMocks
    @Spy
    protected ControlledListCommonsService controlledListCommonsService;

    @Mock
    private RDFsparqlBridgeService rdFsparqlBridgeService;

    @Mock
    private org.apache.jena.query.ResultSet resultSet;

    @Mock
    private QuerySolution querySolution;


    private List<RDFConcept> rdfConcepts = new ArrayList<>();
    private RDFConcept rdfConcept = new RDFConcept();
    private RDFConceptScheme rdfConceptScheme = new RDFConceptScheme();
    private ConceptDTO conceptDTO = new ConceptDTO();

    @Before
    public void setUp() {
        String uri = new ConceptDTO().getIdPrefix(ConceptDTO.class);
        Map<String,String> targetName = new HashMap<>();
        targetName.put("en", "targetName");

        conceptDTO.setId(URI.create(uri));
        conceptDTO.setPrefLabel(new LiteralMap("en", "test"));

        rdfConcept.setUri(uri);
        rdfConcept.setTargetName(targetName);
        rdfConcept.setTargetFrameworkUri(uri + "targetFramework");

        rdfConceptScheme.setTargetFramework(targetName);
        rdfConceptScheme.setTargetNotation("targetNotation");
        rdfConceptScheme.setTargetFrameworkUri(uri + "targetFramework");

        rdfConcepts.add(rdfConcept);
    }

    @Test
    public void searchRDFConcepts_StringStringStringIntIntCollection_shouldCallRDFBridgeSearchRDFConcepts_Always() {
        Mockito.doReturn(rdfConcepts).when(rdFsparqlBridgeService).searchRDFConcepts(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(),
                ArgumentMatchers.anyString(), ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt(), ArgumentMatchers.anyCollection());

        controlledListCommonsService.searchRDFConcepts("targetFramework", "search", "locale", 1, 1, new ArrayList<>());

        Mockito.verify(rdFsparqlBridgeService, Mockito.times(1)).searchRDFConcepts(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(),
                ArgumentMatchers.anyString(), ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt(), ArgumentMatchers.anyCollection());
    }

    @Test
    public void searchConceptByConcept_ControlledListConcept_ShouldReturnNull() {
        Mockito.doReturn(null).when(controlledListCommonsService).searchConceptByUri(ArgumentMatchers.any(), ArgumentMatchers.anyString(),
                ArgumentMatchers.anyString());

        ConceptDTO conceptDTO = controlledListCommonsService.searchConceptByConcept(null);
        Assert.assertNull(conceptDTO);
    }

    @Test
    public void searchConceptByConcept_ControlledListConcept_ShouldCallSearchConceptByUri() {
        Mockito.doReturn(conceptDTO).when(controlledListCommonsService).searchConceptByUri(ArgumentMatchers.any(), ArgumentMatchers.anyString(),
                ArgumentMatchers.anyString());

        controlledListCommonsService.searchConceptByConcept(ControlledListConcept.CREDENTIAL_TYPE_CONVERTED);

        Mockito.verify(controlledListCommonsService, Mockito.times(1)).searchConceptByUri(ArgumentMatchers.any(), ArgumentMatchers.anyString(),
                ArgumentMatchers.anyString());
    }

    @Test
    public void searchConceptByConcept_ControlledListConceptString_ShouldReturnNull() {
        Mockito.doReturn(null).when(controlledListCommonsService).searchConceptByUri(ArgumentMatchers.any(), ArgumentMatchers.anyString(),
                ArgumentMatchers.anyString());

        ConceptDTO conceptDTO = controlledListCommonsService.searchConceptByConcept(null , "");
        Assert.assertNull(conceptDTO);
    }

    @Test
    public void searchConceptByConcept_ControlledListConceptString_ShouldCallSearchConceptByUri() {
        Mockito.doReturn(conceptDTO).when(controlledListCommonsService).searchConceptByUri(ArgumentMatchers.any(), ArgumentMatchers.anyString(),
                ArgumentMatchers.anyString());

        controlledListCommonsService.searchConceptByConcept(ControlledListConcept.CREDENTIAL_TYPE_CONVERTED, "en");

        Mockito.verify(controlledListCommonsService, Mockito.times(1)).searchConceptByUri(ArgumentMatchers.any(), ArgumentMatchers.anyString(),
                ArgumentMatchers.anyString());
    }

    @Test
    public void searchConceptByConcept_ControlledListConceptCollection_ShouldReturnNull() {
        Mockito.doReturn(null).when(controlledListCommonsService).searchConceptByUri(ArgumentMatchers.any(), ArgumentMatchers.anyString(),
                ArgumentMatchers.anyCollection(), ArgumentMatchers.anyString());

        ConceptDTO conceptDTO = controlledListCommonsService.searchConceptByConcept(null, new ArrayList<>());
        Assert.assertNull(conceptDTO);
    }

    @Test
    public void searchConceptByConcept_ControlledListConceptCollection_ShouldCallSearchConceptByUri() {
        Mockito.doReturn(conceptDTO).when(controlledListCommonsService).searchConceptByUri(ArgumentMatchers.any(), ArgumentMatchers.anyString(),
                ArgumentMatchers.anyCollection(), ArgumentMatchers.anyString());

        controlledListCommonsService.searchConceptByConcept(ControlledListConcept.CREDENTIAL_TYPE_CONVERTED, new ArrayList<>());

        Mockito.verify(controlledListCommonsService, Mockito.times(1)).searchConceptByUri(ArgumentMatchers.any(), ArgumentMatchers.anyString(),
                ArgumentMatchers.anyCollection(), ArgumentMatchers.anyString());
    }

    @Test
    public void searchConceptByConcept_ControlledListConceptCollectionString_ShouldReturnNull() {
        Mockito.doReturn(null).when(controlledListCommonsService).searchConceptByUri(ArgumentMatchers.any(), ArgumentMatchers.anyString(),
                ArgumentMatchers.anyCollection(), ArgumentMatchers.anyString());

        ConceptDTO conceptDTO = controlledListCommonsService.searchConceptByConcept(null , new ArrayList<>(), "");
        Assert.assertNull(conceptDTO);
    }

    @Test
    public void searchConceptByConcept_ControlledListConceptCollectionString_ShouldCallSearchConceptByUri() {
        Mockito.doReturn(conceptDTO).when(controlledListCommonsService).searchConceptByUri(ArgumentMatchers.any(), ArgumentMatchers.anyString(),
                ArgumentMatchers.anyCollection(), ArgumentMatchers.anyString());

        controlledListCommonsService.searchConceptByConcept(ControlledListConcept.CREDENTIAL_TYPE_CONVERTED, ControlledListCommonsService.ALLOWED_LANGS, "en");

        Mockito.verify(controlledListCommonsService, Mockito.times(1)).searchConceptByUri(ArgumentMatchers.any(), ArgumentMatchers.anyString(),
                ArgumentMatchers.anyCollection(), ArgumentMatchers.anyString());
    }

    @Test
    public void searchConceptByUri_StringStringString_ShouldReturnNullAndNotCallToConcept() {
        Mockito.doReturn(null).when(rdFsparqlBridgeService).searchConceptScheme(ArgumentMatchers.anyString());
        Mockito.doReturn(null).when(controlledListCommonsService).searchRDFConceptsByUri(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(),
                ArgumentMatchers.anyCollection(), ArgumentMatchers.anyString());

        ConceptDTO conceptDTO = controlledListCommonsService.searchConceptByUri("", "", "");

        Assert.assertNull(conceptDTO);
        Mockito.verify(rdFsparqlBridgeService, Mockito.times(1)).searchConceptScheme(ArgumentMatchers.anyString());
        Mockito.verify(controlledListCommonsService, Mockito.times(1)).searchRDFConceptsByUri(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(),
                ArgumentMatchers.anyCollection(), ArgumentMatchers.anyString());
        Mockito.verify(controlledListCommonsService, Mockito.times(0)).toConcept(ArgumentMatchers.any(RDFConceptScheme.class), ArgumentMatchers.anyCollection(), ArgumentMatchers.anyCollection());

    }

    @Test
    public void searchConceptByUri_StringStringString_ShouldCallMethods() {
        Mockito.doReturn(rdfConceptScheme).when(rdFsparqlBridgeService).searchConceptScheme(ArgumentMatchers.anyString());
        Mockito.doReturn(rdfConcepts).when(controlledListCommonsService).searchRDFConceptsByUri(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(),
                ArgumentMatchers.anyCollection(), ArgumentMatchers.anyString());
        Mockito.doReturn(Arrays.asList(conceptDTO)).when(controlledListCommonsService).toConcept(ArgumentMatchers.any(RDFConceptScheme.class), ArgumentMatchers.anyCollection(), ArgumentMatchers.anyCollection());

        ConceptDTO conceptDTO = controlledListCommonsService.searchConceptByUri("targetFramework", "search", "locale");

        Assert.assertNotNull(conceptDTO);

        Mockito.verify(rdFsparqlBridgeService, Mockito.times(1)).searchConceptScheme(ArgumentMatchers.anyString());
        Mockito.verify(controlledListCommonsService, Mockito.times(1)).searchRDFConceptsByUri(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(),
                ArgumentMatchers.anyCollection(), ArgumentMatchers.anyString());
        Mockito.verify(controlledListCommonsService, Mockito.times(1)).toConcept(ArgumentMatchers.any(RDFConceptScheme.class), ArgumentMatchers.anyCollection(), ArgumentMatchers.anyCollection());

    }

    @Test
    public void searchConceptByUri_StringStringCollectionString_ShouldReturnNullAndNotCallToConcept() {
        Mockito.doReturn(null).when(rdFsparqlBridgeService).searchConceptScheme(ArgumentMatchers.anyString());
        Mockito.doReturn(null).when(controlledListCommonsService).searchRDFConceptsByUri(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(),
                ArgumentMatchers.anyCollection(), ArgumentMatchers.anyString());

        ConceptDTO conceptDTO = controlledListCommonsService.searchConceptByUri("", "", new ArrayList<>(), "");
        Assert.assertNull(conceptDTO);
        Mockito.verify(rdFsparqlBridgeService, Mockito.times(1)).searchConceptScheme(ArgumentMatchers.anyString());
        Mockito.verify(controlledListCommonsService, Mockito.times(1)).searchRDFConceptsByUri(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(),
                ArgumentMatchers.anyCollection(), ArgumentMatchers.anyString());
        Mockito.verify(controlledListCommonsService, Mockito.times(0)).toConcept(ArgumentMatchers.any(RDFConceptScheme.class), ArgumentMatchers.anyCollection(), ArgumentMatchers.anyCollection());

    }

    @Test
    public void searchConceptByUri_StringStringCollectionString_ShouldCallMethods() {
        Mockito.doReturn(rdfConceptScheme).when(rdFsparqlBridgeService).searchConceptScheme(ArgumentMatchers.anyString());
        Mockito.doReturn(rdfConcepts).when(controlledListCommonsService).searchRDFConceptsByUri(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(),
                ArgumentMatchers.anyCollection(), ArgumentMatchers.anyString());
        Mockito.doReturn(Arrays.asList(conceptDTO)).when(controlledListCommonsService).toConcept(ArgumentMatchers.any(RDFConceptScheme.class), ArgumentMatchers.anyCollection(), ArgumentMatchers.anyCollection());

        ConceptDTO conceptDTO = controlledListCommonsService.searchConceptByUri("targetFramework", "search", ControlledListCommonsService.ALLOWED_LANGS, "locale");

        Assert.assertNotNull(conceptDTO);

        Mockito.verify(rdFsparqlBridgeService, Mockito.times(1)).searchConceptScheme(ArgumentMatchers.anyString());
        Mockito.verify(controlledListCommonsService, Mockito.times(1)).searchRDFConceptsByUri(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(),
                ArgumentMatchers.anyCollection(), ArgumentMatchers.anyString());
        Mockito.verify(controlledListCommonsService, Mockito.times(1)).toConcept(ArgumentMatchers.any(RDFConceptScheme.class), ArgumentMatchers.anyCollection(), ArgumentMatchers.anyCollection());
    }

    @Test
    public void searchConceptByUri_StringStringStringCollection_ShouldReturnNull() {
        Mockito.doReturn(null).when(controlledListCommonsService).searchConceptsByUri(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(),
                ArgumentMatchers.anyCollection(), ArgumentMatchers.anyString());

        ConceptDTO conceptDTO = controlledListCommonsService.searchConceptByUri("", "", "", new ArrayList<>());
        Assert.assertNull(conceptDTO);
    }

    @Test
    public void searchConceptByUri_StringStringStringCollection_ShouldCallSearchRDFConceptsByUri() {
        Mockito.doReturn(Arrays.asList(conceptDTO)).when(controlledListCommonsService).searchConceptsByUri(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(),
                ArgumentMatchers.anyCollection(), ArgumentMatchers.anyString());

        controlledListCommonsService.searchConceptByUri("targetFramework", "search", "locale", ControlledListCommonsService.ALLOWED_LANGS);

        Mockito.verify(controlledListCommonsService, Mockito.times(1)).searchConceptsByUri(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(),
                ArgumentMatchers.anyCollection(), ArgumentMatchers.anyString());
    }

    @Test
    public void searchConceptByUri_StringString_ShouldReturnNull() {
        Mockito.doReturn(null).when(controlledListCommonsService).searchConceptsByUri(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(),
                ArgumentMatchers.anyCollection(), ArgumentMatchers.anyString());
        ConceptDTO conceptDTO = controlledListCommonsService.searchConceptByUri("", "");
        Assert.assertNull(conceptDTO);
    }

    @Test
    public void searchConceptByUri_StringString_ShouldCallSearchRDFConceptsByUri() {
        Mockito.doReturn(Arrays.asList(conceptDTO)).when(controlledListCommonsService).searchConceptsByUri(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(),
                ArgumentMatchers.anyCollection(), ArgumentMatchers.anyString());

        controlledListCommonsService.searchConceptByUri("targetFramework", "search");

        Mockito.verify(controlledListCommonsService, Mockito.times(1)).searchConceptsByUri(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(),
                ArgumentMatchers.anyCollection(), ArgumentMatchers.anyString());
    }

    @Test
    public void searchConceptByUri_StringStringCollectionString_ShouldReturnNull() {
        Mockito.doReturn(null).when(rdFsparqlBridgeService).searchConceptScheme(ArgumentMatchers.anyString());
        Mockito.doReturn(null).when(controlledListCommonsService).searchRDFConceptsByUri(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(),
                ArgumentMatchers.anyCollection(), ArgumentMatchers.anyString());

        controlledListCommonsService.searchConceptsByUri("", "", new ArrayList<>(), "");

        Mockito.verify(rdFsparqlBridgeService, Mockito.times(1)).searchConceptScheme(ArgumentMatchers.anyString());
        Mockito.verify(controlledListCommonsService, Mockito.times(1)).searchRDFConceptsByUri(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(),
                ArgumentMatchers.anyCollection(), ArgumentMatchers.anyString());
        Mockito.verify(controlledListCommonsService, Mockito.times(0)).toConcept(ArgumentMatchers.any(RDFConceptScheme.class), ArgumentMatchers.anyCollection(), ArgumentMatchers.anyCollection());
    }

    @Test
    public void searchConceptByUri_StringStringCollectionString_ShouldCallSearchRDFConceptsByUri() {
        Mockito.doReturn(rdfConceptScheme).when(rdFsparqlBridgeService).searchConceptScheme(ArgumentMatchers.anyString());
        Mockito.doReturn(rdfConcepts).when(controlledListCommonsService).searchRDFConceptsByUri(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(),
                ArgumentMatchers.anyCollection(), ArgumentMatchers.anyString());
        Mockito.doReturn(Arrays.asList(conceptDTO)).when(controlledListCommonsService).toConcept(ArgumentMatchers.any(RDFConceptScheme.class), ArgumentMatchers.anyCollection(), ArgumentMatchers.anyCollection());

        controlledListCommonsService.searchConceptsByUri("targetFramework", "search", ControlledListCommonsService.ALLOWED_LANGS, "locale");

        Mockito.verify(rdFsparqlBridgeService, Mockito.times(1)).searchConceptScheme(ArgumentMatchers.anyString());
        Mockito.verify(controlledListCommonsService, Mockito.times(1)).searchRDFConceptsByUri(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(),
                ArgumentMatchers.anyCollection(), ArgumentMatchers.anyString());
        Mockito.verify(controlledListCommonsService, Mockito.times(1)).toConcept(ArgumentMatchers.any(RDFConceptScheme.class), ArgumentMatchers.anyCollection(), ArgumentMatchers.anyCollection());
    }

    @Test
    public void searchRDFConceptByUri_StringString_ShouldReturnNull() {
        RDFConcept conceptDTO = controlledListCommonsService.searchRDFConceptByUri("", "");
        Assert.assertNull(conceptDTO);
    }

    @Test
    public void searchRDFConceptByUri_StringString_ShouldCallSearchRDFConcepts() {
        Mockito.doReturn(rdfConcepts).when(rdFsparqlBridgeService).searchRDFConcepts(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(),
                ArgumentMatchers.anyCollection(), ArgumentMatchers.anyString());

        RDFConcept conceptDTO = controlledListCommonsService.searchRDFConceptByUri("targetFramework", "uri");
        Assert.assertNotNull(conceptDTO);
    }

    @Test
    public void searchRDFConceptByUri_StringStringCollection_ShouldReturnNull() {
        List<RDFConcept> conceptDTO = controlledListCommonsService.searchRDFConceptsByUri("", "", new ArrayList<>(), "");
        Assert.assertTrue(conceptDTO.isEmpty());
    }

    @Test
    public void searchRDFConceptByUri_StringStringCollection_ShouldCallSearchRDFConcepts() {
        Mockito.doReturn(rdfConcepts).when(rdFsparqlBridgeService).searchRDFConcepts(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(),
                ArgumentMatchers.anyCollection(), ArgumentMatchers.anyString());

        controlledListCommonsService.searchRDFConceptsByUri("targetFramework", "search", ControlledListCommonsService.ALLOWED_LANGS, "locale");

        Mockito.verify(rdFsparqlBridgeService ,Mockito.times(1)).searchRDFConcepts(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(),
                ArgumentMatchers.anyCollection(), ArgumentMatchers.anyString());
    }

    @Test
    public void searchCountryByEuvocField_ShouldReturnNull() {
        Mockito.doReturn(null).when(rdFsparqlBridgeService).searchConceptScheme(ArgumentMatchers.anyString());
        Mockito.doReturn(null).when(rdFsparqlBridgeService).searchCountryByEuvocField(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.anyCollection());

        ConceptDTO conceptDTO = controlledListCommonsService.searchCountryByEuvocField("", "", new ArrayList<>());

        Mockito.verify(rdFsparqlBridgeService, Mockito.times(1)).searchConceptScheme(ArgumentMatchers.anyString());
        Mockito.verify(rdFsparqlBridgeService, Mockito.times(1)).searchCountryByEuvocField(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.anyCollection());
        Mockito.verify(controlledListCommonsService, Mockito.times(0)).toConcept(ArgumentMatchers.any(RDFConceptScheme.class), ArgumentMatchers.anyCollection(), ArgumentMatchers.anyCollection());

        Assert.assertNull(conceptDTO);
    }

    @Test
    public void searchCountryByEuvocField_ShouldCallMethods() {
        Mockito.doReturn(rdfConceptScheme).when(rdFsparqlBridgeService).searchConceptScheme(ArgumentMatchers.anyString());
        Mockito.doReturn(rdfConcepts).when(rdFsparqlBridgeService).searchCountryByEuvocField(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.anyCollection());

        controlledListCommonsService.searchCountryByEuvocField("target", "evouc", ControlledListCommonsService.ALLOWED_LANGS);

        Mockito.verify(rdFsparqlBridgeService, Mockito.times(1)).searchConceptScheme(ArgumentMatchers.anyString());
        Mockito.verify(rdFsparqlBridgeService, Mockito.times(1)).searchCountryByEuvocField(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.anyCollection());
        Mockito.verify(controlledListCommonsService, Mockito.times(1)).toConcept(ArgumentMatchers.any(RDFConceptScheme.class), ArgumentMatchers.anyCollection(), ArgumentMatchers.anyCollection());

    }

    @Test
    public void getFileType_ShouldReturnNull() {
        ConceptDTO conceptDTO = controlledListCommonsService.getFileType(null);
        Assert.assertNull(conceptDTO);
    }

    @Test
    public void getFileType_ShouldCallSearchConcepts() {
        Mockito.doReturn(new PageImpl<ConceptDTO>(Arrays.asList(conceptDTO))).when(controlledListCommonsService).searchConcepts(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.anyString(),
                ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt(), ArgumentMatchers.any());

        controlledListCommonsService.getFileType("JPG");

        Mockito.verify(controlledListCommonsService, Mockito.times(1)).searchConcepts(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.anyString(),
                ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt(), ArgumentMatchers.any());
    }

    @Test
    public void searchLanguageByLang_ShouldReturnNull() {
        ConceptDTO conceptDTO = controlledListCommonsService.searchLanguageByLang(null);
        Assert.assertNull(conceptDTO);
        Mockito.verify(controlledListCommonsService, Mockito.times(0)).toConcept(ArgumentMatchers.any(RDFConceptScheme.class), ArgumentMatchers.anyCollection(), ArgumentMatchers.anyCollection());

    }

    @Test
    public void searchLanguageByLang_ShouldCallSearchConcepts() {
        Mockito.doReturn(rdfConcept).when(rdFsparqlBridgeService).searchLanguagesByLang(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.anyCollection());
        Mockito.doReturn(Arrays.asList(conceptDTO)).when(controlledListCommonsService).toConcept(ArgumentMatchers.anyString(), ArgumentMatchers.anyCollection(), ArgumentMatchers.anyCollection());

        controlledListCommonsService.searchLanguageByLang("en");

        Mockito.verify(rdFsparqlBridgeService, Mockito.times(1)).searchLanguagesByLang(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.anyCollection());
        Mockito.verify(controlledListCommonsService, Mockito.times(1)).toConcept(ArgumentMatchers.anyString(), ArgumentMatchers.anyCollection(), ArgumentMatchers.anyCollection());

    }

    @Test
    public void searchConceptsTreeByUri_ShouldReturnNull() {
        Mockito.doReturn(null).when(rdFsparqlBridgeService).searchTreeConcepts(ArgumentMatchers.anyString(), ArgumentMatchers.anyString());
        List<ConceptDTO> conceptDTO = controlledListCommonsService.searchConceptsTreeByUri("", "", "");
        Assert.assertNull(conceptDTO);
        Mockito.verify(controlledListCommonsService, Mockito.times(0)).toConcept(ArgumentMatchers.any(RDFConceptScheme.class), ArgumentMatchers.anyCollection(), ArgumentMatchers.anyCollection());

    }

    @Test
    public void searchConceptsTreeByUri_ShouldCallSearchConcepts() {
        Mockito.doReturn(new HashSet<>(rdfConcepts)).when(rdFsparqlBridgeService).searchTreeConcepts(ArgumentMatchers.anyString(), ArgumentMatchers.anyString());
        Mockito.doReturn(Arrays.asList(conceptDTO)).when(controlledListCommonsService).toConcept(ArgumentMatchers.anyString(), ArgumentMatchers.anyCollection(), ArgumentMatchers.anyCollection());

        List<ConceptDTO> conceptDTO = controlledListCommonsService.searchConceptsTreeByUri("framework", "uri", "en");
        Assert.assertNotNull(conceptDTO);
        Mockito.verify(controlledListCommonsService, Mockito.times(1)).toConcept(ArgumentMatchers.anyString(), ArgumentMatchers.anyCollection(), ArgumentMatchers.anyCollection());

    }

    @Test
    public void searchLanguageISO639ByConcept_ShouldReturnNull() {
        Mockito.doReturn(null).when(controlledListCommonsService).searchLanguageISO639ByURI(ArgumentMatchers.anyString());
        String result = controlledListCommonsService.searchLanguageISO639ByConcept(new ConceptDTO());
        Assert.assertNull(result);

    }

    @Test
    public void searchLanguageISO639ByConcept_ShouldCallSearchConcepts() {
        Mockito.doReturn("patata").when(controlledListCommonsService).searchLanguageISO639ByURI(ArgumentMatchers.anyString());
        String result = controlledListCommonsService.searchLanguageISO639ByConcept(conceptDTO);
        Assert.assertNotNull(result);
        Mockito.verify(controlledListCommonsService, Mockito.times(1)).searchLanguageISO639ByURI(ArgumentMatchers.anyString());

    }

    @Test
    public void searchConcepts_ShouldReturnNull() {

        Mockito.doReturn(rdfConceptScheme).when(rdFsparqlBridgeService).searchConceptScheme(ArgumentMatchers.anyString());
        Mockito.doReturn(null).when(controlledListCommonsService).searchRDFConcepts(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(),
                ArgumentMatchers.anyString(),ArgumentMatchers.anyInt(),ArgumentMatchers.anyInt(),ArgumentMatchers.anyCollection());
        Mockito.doReturn(null).when(controlledListCommonsService).toConcept(rdfConceptScheme, null, new ArrayList<>());

        Page<ConceptDTO> page = controlledListCommonsService.searchConcepts("targetFramework", "search", "locale", 0, 0, new ArrayList<>());

        Assert.assertNull(page);

    }

    @Test
    public void searchConcepts_ShouldCallSearchConcepts() {
        Mockito.doReturn(rdfConceptScheme).when(rdFsparqlBridgeService).searchConceptScheme(ArgumentMatchers.anyString());
        Mockito.doReturn(rdfConcepts).when(controlledListCommonsService).searchRDFConcepts(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(),
                ArgumentMatchers.anyString(),ArgumentMatchers.anyInt(),ArgumentMatchers.anyInt(),ArgumentMatchers.anyCollection());
        Mockito.doReturn(Arrays.asList(conceptDTO)).when(controlledListCommonsService).toConcept(ArgumentMatchers.any(RDFConceptScheme.class), ArgumentMatchers.anyCollection(), ArgumentMatchers.anyCollection());

        Page<ConceptDTO> page = controlledListCommonsService.searchConcepts("targetFramework", "search", "locale", 0, 0, new ArrayList<>());

        Assert.assertNotNull(page.get());
        Mockito.verify(controlledListCommonsService, Mockito.times(1)).toConcept(ArgumentMatchers.any(RDFConceptScheme.class), ArgumentMatchers.anyCollection(), ArgumentMatchers.anyCollection());

    }

    @Test
    public void toConcept_StringCollectionCollection_ShouldCallMethods() {
        Mockito.doReturn(rdfConceptScheme).when(rdFsparqlBridgeService).searchConceptScheme(ArgumentMatchers.anyString());
        Mockito.doReturn(Arrays.asList(conceptDTO)).when(controlledListCommonsService).toConcept(ArgumentMatchers.any(RDFConceptScheme.class), ArgumentMatchers.anyCollection(), ArgumentMatchers.anyCollection());
        controlledListCommonsService.toConcept("concept", rdfConcepts, ControlledListCommonsService.ALLOWED_LANGS);
        Mockito.verify(controlledListCommonsService, Mockito.times(1)).toConcept(ArgumentMatchers.anyString(), ArgumentMatchers.anyCollection(), ArgumentMatchers.anyCollection());

    }

    @Test
    public void toConcept_RDFConceptSchemeCollectionCollection_ShouldReturnEmptyList() {
        List<ConceptDTO> conceptDTOList = controlledListCommonsService.toConcept(rdfConceptScheme, null, new ArrayList<>());
        Assert.assertTrue(conceptDTOList.isEmpty());
    }

    @Test
    public void toConcept_RDFConceptSchemeCollectionCollection_ShouldReturnNotEmptyList() {
        List<ConceptDTO> conceptDTOList = controlledListCommonsService.toConcept(rdfConceptScheme, rdfConcepts, ControlledListCommonsService.ALLOWED_LANGS);
        Assert.assertFalse(conceptDTOList.isEmpty());
    }

    @Test
    public void toRDFConcept_ShouldReturnEmptyList() {
        Set<RDFConcept> conceptDTOList = controlledListCommonsService.toRDFConcept("", null);
        Assert.assertTrue(conceptDTOList.isEmpty());
    }

    @Test
    public void toRDFConcept_ShouldReturnNotEmptyList() {
        Mockito.when(resultSet.hasNext()).thenReturn(true).thenReturn(false);
        Mockito.doReturn(querySolution).when(resultSet).nextSolution();
        Mockito.doReturn(ModelFactory.createDefaultModel().createLiteral("test","test")).when(querySolution).getLiteral(ArgumentMatchers.anyString());
        Mockito.doReturn(ModelFactory.createDefaultModel().createLiteral("test","test")).when(querySolution).get(ArgumentMatchers.anyString());
        Mockito.doReturn(ModelFactory.createDefaultModel().createLiteral("1","1")).when(querySolution).get("1");

        Set<RDFConcept> conceptDTOList = controlledListCommonsService.toRDFConcept("targetFrameworkURI", resultSet);
        Assert.assertFalse(conceptDTOList.isEmpty());
    }

    @Test
    public void toRDFConceptScheme_ShouldReturnEmptyList() {
        RDFConceptScheme rdfConceptScheme = controlledListCommonsService.toRDFConceptScheme("", null);
        Assert.assertNull(rdfConceptScheme);
    }

    @Test
    public void toRDFConceptScheme_ShouldReturnNotEmptyList() {
        Mockito.when(resultSet.hasNext()).thenReturn(true).thenReturn(false);
        Mockito.doReturn(querySolution).when(resultSet).nextSolution();
        Mockito.doReturn(ModelFactory.createDefaultModel().createLiteral("test","test")).when(querySolution).getLiteral(ArgumentMatchers.anyString());
        Mockito.doReturn(ModelFactory.createDefaultModel().createLiteral("test","test")).when(querySolution).get(ArgumentMatchers.anyString());
        Mockito.doReturn(ModelFactory.createDefaultModel().createLiteral("1","1")).when(querySolution).get("1");

        RDFConceptScheme rdfConceptScheme = controlledListCommonsService.toRDFConceptScheme("targetFrameworkURI", resultSet);
        Assert.assertNotNull(rdfConceptScheme);
    }

    @Test
    public void isValidCredentialProfile_ReturnFalse() {
        Assert.assertFalse(controlledListCommonsService.isValidCredentialProfile(URI.create("PATATA")));
    }

    @Test
    public void isValidCredentialProfile_ReturnTrue() {
        Assert.assertTrue(controlledListCommonsService.isValidCredentialProfile(URI.create(ControlledListConcept.CREDENTIAL_TYPE_CONVERTED.getUrl())));
    }

}
