package eu.europa.ec.empl.edci.service;

import eu.europa.ec.empl.edci.config.service.ProxyConfigService;
import eu.europa.ec.empl.edci.constants.ControlledListConcept;
import eu.europa.ec.empl.edci.datamodel.controlledList.RDFConcept;
import eu.europa.ec.empl.edci.datamodel.controlledList.RDFConceptScheme;
import eu.europa.ec.empl.edci.datamodel.jsonld.model.dataTypes.ConceptDTO;
import eu.europa.ec.empl.edci.datamodel.jsonld.model.dataTypes.LiteralMap;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.ModelFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.junit.MockitoJUnitRunner;

import java.net.URI;
import java.util.*;

@RunWith(MockitoJUnitRunner.Silent.class)
public class RDFSparqlBridgeServiceTest {

    @InjectMocks
    @Spy
    protected RDFsparqlBridgeService rdFsparqlBridgeService;

    @Mock
    ProxyConfigService proxyConfigService;

    @Mock
    QueryExecution qexec;

    @Mock
    private ResultSet resultSet;

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

        Mockito.lenient().when(rdFsparqlBridgeService.getiConfigService()).thenReturn(proxyConfigService);
        Mockito.lenient().when(rdFsparqlBridgeService.getiConfigService().getString(ArgumentMatchers.anyString(), ArgumentMatchers.anyString())).thenReturn("http://test.com");
        Mockito.doReturn(qexec).when(rdFsparqlBridgeService).buildQueryExecution(ArgumentMatchers.anyString(), ArgumentMatchers.anyString());
        Mockito.lenient().when(qexec.execSelect()).thenReturn(resultSet);
        Mockito.doReturn(querySolution).when(resultSet).nextSolution();
        Mockito.doReturn(ModelFactory.createDefaultModel().createLiteral("test","test")).when(querySolution).getLiteral(ArgumentMatchers.anyString());
        Mockito.doReturn(ModelFactory.createDefaultModel().createLiteral("test","test")).when(querySolution).get(ArgumentMatchers.anyString());
        Mockito.doReturn(ModelFactory.createDefaultModel().createLiteral("1","1")).when(querySolution).get("1");
        Mockito.doReturn(Arrays.asList("1").iterator()).when(querySolution).varNames();
    }

    @Test
    public void searchRDFConcepts_StringStringCollectionCollection_ShouldReturnEmptyList() {
        Mockito.doReturn(null).when(rdFsparqlBridgeService).searchConceptScheme(ArgumentMatchers.anyString());
        Mockito.doReturn(null).when(rdFsparqlBridgeService).toRDFConcept(ArgumentMatchers.anyString(), ArgumentMatchers.any());

        List<RDFConcept> rdfConcepts = rdFsparqlBridgeService.searchRDFConcepts("", "", new ArrayList<>(),"");

        Assert.assertEquals(rdfConcepts.isEmpty(), true);
        Mockito.verify(rdFsparqlBridgeService, Mockito.times(0)).toRDFConcept(ArgumentMatchers.anyString(), ArgumentMatchers.any());

    }

    @Test
    public void searchRDFConcepts_StringStringCollectionCollection_ShouldReturnNotEmptyList() {
        Mockito.doReturn(rdfConceptScheme).when(rdFsparqlBridgeService).searchConceptScheme(ArgumentMatchers.anyString());
        Mockito.doReturn(new HashSet<>(rdfConcepts)).when(rdFsparqlBridgeService).toRDFConcept(ArgumentMatchers.anyString(), ArgumentMatchers.any());

        List<RDFConcept> rdfConcepts = rdFsparqlBridgeService.searchRDFConcepts("test", "locale", new ArrayList<>(),"test");

        Assert.assertEquals(rdfConcepts.isEmpty(), false);
        Mockito.verify(rdFsparqlBridgeService, Mockito.times(1)).toRDFConcept(ArgumentMatchers.anyString(), ArgumentMatchers.any());
    }

    @Test
    public void searchRDFConcepts_StringStringStringIntIntCollection_ShouldReturnEmptyList() {
        Mockito.doReturn(null).when(rdFsparqlBridgeService).searchConceptScheme(ArgumentMatchers.anyString());
        Mockito.doReturn(null).when(rdFsparqlBridgeService).toRDFConcept(ArgumentMatchers.anyString(), ArgumentMatchers.any());

        List<RDFConcept> rdfConcepts = rdFsparqlBridgeService.searchRDFConcepts("", "", "", 0, 0, new ArrayList<>());

        Assert.assertEquals(rdfConcepts.isEmpty(), true);
        Mockito.verify(rdFsparqlBridgeService, Mockito.times(0)).toRDFConcept(ArgumentMatchers.anyString(), ArgumentMatchers.any());

    }

    @Test
    public void searchRDFConcepts_StringStringStringIntIntCollection_ShouldReturnNotEmptyList() {
        Mockito.doReturn(rdfConceptScheme).when(rdFsparqlBridgeService).searchConceptScheme(ArgumentMatchers.anyString());
        Mockito.doReturn(new HashSet<>(rdfConcepts)).when(rdFsparqlBridgeService).toRDFConcept(ArgumentMatchers.anyString(), ArgumentMatchers.any());

        List<RDFConcept> rdfConcepts = rdFsparqlBridgeService.searchRDFConcepts("test", "search","en", 0, 0, new ArrayList<>());

        Assert.assertEquals(rdfConcepts.isEmpty(), false);
        Mockito.verify(rdFsparqlBridgeService, Mockito.times(1)).toRDFConcept(ArgumentMatchers.anyString(), ArgumentMatchers.any());

    }

    @Test
    public void searchCountryByEuvocField_ShouldReturnEmptyList() {
        Mockito.doReturn(null).when(rdFsparqlBridgeService).searchConceptScheme(ArgumentMatchers.anyString());
        Mockito.doReturn(null).when(rdFsparqlBridgeService).toRDFConcept(ArgumentMatchers.anyString(), ArgumentMatchers.any());

        List<RDFConcept> rdfConcepts = rdFsparqlBridgeService.searchCountryByEuvocField("", "", new ArrayList<>());
        Assert.assertEquals(rdfConcepts.isEmpty(), true);
        Mockito.verify(rdFsparqlBridgeService, Mockito.times(0)).toRDFConcept(ArgumentMatchers.anyString(), ArgumentMatchers.any());

    }

    @Test
    public void searchCountryByEuvocField_ShouldReturnNotEmptyList() {
        Mockito.doReturn(rdfConceptScheme).when(rdFsparqlBridgeService).searchConceptScheme(ArgumentMatchers.anyString());
        Mockito.doReturn(new HashSet<>(rdfConcepts)).when(rdFsparqlBridgeService).toRDFConcept(ArgumentMatchers.anyString(), ArgumentMatchers.any());

        List<RDFConcept> rdfConcepts = rdFsparqlBridgeService.searchCountryByEuvocField("test", "en", new ArrayList<>());

        Assert.assertEquals(rdfConcepts.isEmpty(), false);
        Mockito.verify(rdFsparqlBridgeService, Mockito.times(1)).toRDFConcept(ArgumentMatchers.anyString(), ArgumentMatchers.any());

    }

    @Test
    public void searchBroaderConcepts_ShouldReturnEmptyList() {
        Mockito.doReturn(null).when(rdFsparqlBridgeService).searchConceptScheme(ArgumentMatchers.anyString());

        Set<RDFConcept> rdfConcepts = rdFsparqlBridgeService.searchBroaderConcepts("", "", "", "", 0, 0, new ArrayList<>());
        Assert.assertEquals(rdfConcepts.isEmpty(), true);

        Mockito.verify(rdFsparqlBridgeService, Mockito.times(0)).toRDFConcept(ArgumentMatchers.anyString(), ArgumentMatchers.any());

    }

    @Test
    public void searchBroaderConcepts_ShouldCallToRDFConcept() {
        Mockito.doReturn(rdfConceptScheme).when(rdFsparqlBridgeService).searchConceptScheme(ArgumentMatchers.anyString());
        Mockito.doReturn(new HashSet<>(rdfConcepts)).when(rdFsparqlBridgeService).toRDFConcept(ArgumentMatchers.anyString(), ArgumentMatchers.any());

        Set<RDFConcept> rdfConcepts = rdFsparqlBridgeService.searchBroaderConcepts("test","broader", "search","en", 0, 0, new ArrayList<>());

        Assert.assertEquals(rdfConcepts.isEmpty(), false);
        Mockito.verify(rdFsparqlBridgeService, Mockito.times(1)).toRDFConcept(ArgumentMatchers.anyString(), ArgumentMatchers.any());

    }

    @Test
    public void searchISCEDFTreeConcepts_ShouldReturnEmptyList() {
        Mockito.doReturn(null).when(rdFsparqlBridgeService).searchConceptScheme(ArgumentMatchers.anyString());

        Set<RDFConcept> rdfConcepts = rdFsparqlBridgeService.searchISCEDFTreeConcepts("", "", "", new ArrayList<>());
        Assert.assertEquals(rdfConcepts.isEmpty(), true);
        Mockito.verify(rdFsparqlBridgeService, Mockito.times(0)).toRDFConcept(ArgumentMatchers.anyString(), ArgumentMatchers.any());

    }

    @Test
    public void searchISCEDFTreeConcepts_ShouldCallToRDFConcept() {
        Mockito.doReturn(rdfConceptScheme).when(rdFsparqlBridgeService).searchConceptScheme(ArgumentMatchers.anyString());
        Mockito.doReturn(new HashSet<>(rdfConcepts)).when(rdFsparqlBridgeService).toRDFConcept(ArgumentMatchers.anyString(), ArgumentMatchers.any());
        Set<RDFConcept> rdfConcepts = rdFsparqlBridgeService.searchISCEDFTreeConcepts("test","search","en", new ArrayList<>());

        Mockito.verify(rdFsparqlBridgeService, Mockito.times(1)).toRDFConcept(ArgumentMatchers.anyString(), ArgumentMatchers.any());
        Assert.assertEquals(rdfConcepts.isEmpty(), false);
    }

    @Test
    public void searchTreeConcepts_ShouldReturnEmptyList() {
        Mockito.doReturn(null).when(rdFsparqlBridgeService).searchConceptScheme(ArgumentMatchers.anyString());

        Set<RDFConcept> rdfConcepts = rdFsparqlBridgeService.searchTreeConcepts("", "");
        Assert.assertEquals(rdfConcepts.isEmpty(), true);
        Mockito.verify(rdFsparqlBridgeService, Mockito.times(0)).toRDFConcept(ArgumentMatchers.anyString(), ArgumentMatchers.any());

    }

    @Test
    public void searchTreeConcepts_ShouldCallToRdfConcept() {
        Mockito.doReturn(rdfConceptScheme).when(rdFsparqlBridgeService).searchConceptScheme(ArgumentMatchers.anyString());
        Mockito.doReturn(new HashSet<>(rdfConcepts)).when(rdFsparqlBridgeService).toRDFConcept(ArgumentMatchers.anyString(), ArgumentMatchers.any());

        Set<RDFConcept> rdfConcepts = rdFsparqlBridgeService.searchTreeConcepts("test","URI");
        Assert.assertEquals(rdfConcepts.isEmpty(), false);
        Mockito.verify(rdFsparqlBridgeService, Mockito.times(1)).toRDFConcept(ArgumentMatchers.anyString(), ArgumentMatchers.any());

    }

    @Test
    public void searchLanguagesByLang_ShouldReturnNull() {
        Mockito.doReturn(null).when(rdFsparqlBridgeService).searchConceptScheme(ArgumentMatchers.anyString());

        RDFConcept rdfConcepts = rdFsparqlBridgeService.searchLanguagesByLang("", "", new ArrayList<>());
        Assert.assertNull(rdfConcepts);
        Mockito.verify(rdFsparqlBridgeService, Mockito.times(0)).toRDFConcept(ArgumentMatchers.anyString(), ArgumentMatchers.any());

    }

    @Test
    public void searchLanguagesByLang_ShouldCallToRdfConcept() {
        Mockito.doReturn(rdfConceptScheme).when(rdFsparqlBridgeService).searchConceptScheme(ArgumentMatchers.anyString());
        Mockito.doReturn(new HashSet<>(rdfConcepts)).when(rdFsparqlBridgeService).toRDFConcept(ArgumentMatchers.anyString(), ArgumentMatchers.any());

        RDFConcept rdfConcepts = rdFsparqlBridgeService.searchLanguagesByLang("test","URI", new ArrayList<>());

        Mockito.verify(rdFsparqlBridgeService, Mockito.times(1)).toRDFConcept(ArgumentMatchers.anyString(), ArgumentMatchers.any());
        Assert.assertNotNull(rdfConcepts);
    }

    @Test
    public void countConcepts_ShouldReturnZero() {
        int result = rdFsparqlBridgeService.countConcepts("", "", "", "");
        Assert.assertEquals(0, result);
    }

    @Test
    public void countConcepts_ShouldReturnOne() {
        Mockito.when(resultSet.hasNext()).thenReturn(true).thenReturn(false);
        int result = rdFsparqlBridgeService.countConcepts("test","broader", "search", "en");
        Assert.assertEquals(1, result);
    }

    @Test
    public void searchTypeAndSchemaForCredType_ShouldReturnNull() {
        Pair<String, URI> result = rdFsparqlBridgeService.searchTypeAndSchemaForCredType("");
        Assert.assertNull(result);
    }

    @Test
    public void searchTypeAndSchemaForCredType_ShouldReturnConverted() {
        Pair<String, URI> result = rdFsparqlBridgeService.searchTypeAndSchemaForCredType(ControlledListConcept.CREDENTIAL_TYPE_CONVERTED.getUrl());
        Assert.assertEquals("Converted", result.getKey());
    }

    @Test
    public void searchTypeAndSchemaForCredType_ShouldReturnDiplomaSupplement() {
        Pair<String, URI> result2 = rdFsparqlBridgeService.searchTypeAndSchemaForCredType(ControlledListConcept.CREDENTIAL_TYPE_DIPLOMA_SUPPLEMENT.getUrl());
        Assert.assertEquals("DiplomaSupplement", result2.getKey());
    }

    @Test
    public void searchTypeAndSchemaForCredType_ShouldReturnGeneric() {
        Pair<String, URI> result3 = rdFsparqlBridgeService.searchTypeAndSchemaForCredType(ControlledListConcept.CREDENTIAL_TYPE_GENERIC.getUrl());
        Assert.assertEquals("Generic", result3.getKey());
    }

    @Test
    public void searchTypeAndSchemaForCredType_ShouldReturnIssuedByMandate() {
        Pair<String, URI> result4 = rdFsparqlBridgeService.searchTypeAndSchemaForCredType(ControlledListConcept.CREDENTIAL_TYPE_ISSUED_MANDATE.getUrl());
        Assert.assertEquals("IssuedByMandate", result4.getKey());
    }

    @Test
    public void searchTypeAndSchemaForCredType_ShouldReturnAccredited() {
        Pair<String, URI> result5 = rdFsparqlBridgeService.searchTypeAndSchemaForCredType(ControlledListConcept.CREDENTIAL_TYPE_ACCREDITATION.getUrl());
        Assert.assertEquals("Accredited", result5.getKey());
    }

    @Test
    public void searchLanguageISO639ByURI_ThenResultIsEmpty() {
        String rdfConcepts = rdFsparqlBridgeService.searchLanguageISO639ByURI("");
        Assert.assertNull(rdfConcepts);
    }

    @Test
    public void searchLanguageISO639ByURI_ThenResultIsNotEmpty() {
        Mockito.when(resultSet.hasNext()).thenReturn(true).thenReturn(false);
        String rdfConcepts = rdFsparqlBridgeService.searchLanguageISO639ByURI("URI");
        Assert.assertEquals(rdfConcepts, "test");
        Assert.assertNotNull(rdfConcepts);
    }

    @Test
    public void toConceptDTO_shouldReturnNotNull() {
        RDFConcept conceptDTO = new RDFConcept();
        conceptDTO.setUri(new ConceptDTO().getIdPrefix(new ConceptDTO()));
        Map<String, String> map = new HashMap<>();
        map.put("en", "targetName");
        conceptDTO.setTargetName(map);
        conceptDTO.setTargetFrameworkUri(new ConceptDTO().getIdPrefix(new ConceptDTO()));

        ConceptDTO concept = rdFsparqlBridgeService.toConceptDTO(conceptDTO);

        Assert.assertNotNull(concept);
        Assert.assertEquals("targetName", concept.getPrefLabel().toString());
        Assert.assertEquals(new ConceptDTO().getIdPrefix(new ConceptDTO()),concept.getId().toString());
        Assert.assertEquals(new ConceptDTO().getIdPrefix(new ConceptDTO()),concept.getInScheme().getId().toString());
    }

    @Test
    public void searchRDFConceptScheme_ShouldReturnNull() {
        Mockito.doReturn(null).when(rdFsparqlBridgeService).toRDFConceptScheme(ArgumentMatchers.anyString(), ArgumentMatchers.any());

        RDFConceptScheme rdfConceptScheme = rdFsparqlBridgeService.searchConceptScheme("test");

        Assert.assertNull(rdfConceptScheme);

    }

    @Test
    public void searchRDFConceptScheme_ShouldReturnNotNull() {
        Mockito.doReturn(rdfConceptScheme).when(rdFsparqlBridgeService).toRDFConceptScheme(ArgumentMatchers.anyString(), ArgumentMatchers.any());

        RDFConceptScheme rdfConceptScheme = rdFsparqlBridgeService.searchConceptScheme("test");

        Assert.assertNotNull(rdfConceptScheme);
        Mockito.verify(rdFsparqlBridgeService, Mockito.times(1)).toRDFConceptScheme(ArgumentMatchers.anyString(), ArgumentMatchers.any());

    }

    @Test
    public void toRDFConceptScheme_ShouldOnlyHaveUri() {
        Mockito.when(resultSet.hasNext()).thenReturn(false);

        RDFConceptScheme rdfConceptScheme = rdFsparqlBridgeService.toRDFConceptScheme("test", resultSet);

        Assert.assertNotNull(rdfConceptScheme);
        Assert.assertNull(rdfConceptScheme.getTargetFramework());
        Assert.assertNull(rdfConceptScheme.getTargetNotation());
        Assert.assertNotNull(rdfConceptScheme.getTargetFrameworkUri());

    }

    @Test
    public void toRDFConceptScheme_ShouldReturnNotNull() {
        Mockito.when(resultSet.hasNext()).thenReturn(true).thenReturn(false);

        RDFConceptScheme rdfConceptScheme = rdFsparqlBridgeService.toRDFConceptScheme("test", resultSet);

        Assert.assertNotNull(rdfConceptScheme);
        Assert.assertNotNull(rdfConceptScheme.getTargetFramework());
        Assert.assertNotNull(rdfConceptScheme.getTargetNotation());
        Assert.assertNotNull(rdfConceptScheme.getTargetFrameworkUri());
    }

    @Test
    public void toRDFConcept_ShouldReturnEmptyList() {
        Mockito.when(resultSet.hasNext()).thenReturn(false);

        Set<RDFConcept> rdfConcepts = rdFsparqlBridgeService.toRDFConcept("test", resultSet);

        Assert.assertTrue(rdfConcepts.isEmpty());

    }

    @Test
    public void toRDFConcept_ShouldReturnNotNull() {
        Mockito.when(resultSet.hasNext()).thenReturn(true).thenReturn(false);

        Set<RDFConcept> rdfConcepts = rdFsparqlBridgeService.toRDFConcept("test", resultSet);

        Assert.assertFalse(rdfConcepts.isEmpty());
    }

}
