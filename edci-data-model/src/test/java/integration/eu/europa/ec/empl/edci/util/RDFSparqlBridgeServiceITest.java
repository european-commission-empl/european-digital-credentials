package integration.eu.europa.ec.empl.edci.util;

import eu.europa.ec.empl.edci.config.service.ProxyConfigService;
import eu.europa.ec.empl.edci.constants.ControlledListConcept;
import eu.europa.ec.empl.edci.datamodel.controlledList.RDFConcept;
import eu.europa.ec.empl.edci.datamodel.jsonld.model.dataTypes.ConceptDTO;
import eu.europa.ec.empl.edci.service.RDFsparqlBridgeService;
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
public class RDFSparqlBridgeServiceITest {

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

    @Before
    public void setUp() {
        //PowerMockRunner
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
    public void givenEmptyFrameworkWhenSearchRDFConceptsThenResultIsEmpty() {
        List<RDFConcept> rdfConcepts = rdFsparqlBridgeService.searchRDFConcepts("", "", new ArrayList<>(),"");
        Assert.assertEquals(rdfConcepts.isEmpty(), true);
    }

    @Test
    public void givenDataFrameworkWhenSearchRDFConceptsThenResultIsNotEmpty() {
        Mockito.when(resultSet.hasNext()).thenReturn(true).thenReturn(false).thenReturn(true).thenReturn(false);
        List<RDFConcept> rdfConcepts = rdFsparqlBridgeService.searchRDFConcepts("test", "en", new ArrayList<>(),"test");
        Assert.assertEquals(rdfConcepts.get(0).targetFrameworkUri, "test");
        Assert.assertEquals(rdfConcepts.isEmpty(), false);
    }

    @Test
    public void givenEmptyFrameworkWhenSearchRDFConceptsPageThenResultIsEmpty() {
        List<RDFConcept> rdfConcepts = rdFsparqlBridgeService.searchRDFConcepts("", "", "", 0, 0, new ArrayList<>());
        Assert.assertEquals(rdfConcepts.isEmpty(), true);
    }

    @Test
    public void givenDataFrameworkWhenSearchRDFConceptsPageThenResultIsNotEmpty() {
        Mockito.when(resultSet.hasNext()).thenReturn(true).thenReturn(false).thenReturn(true).thenReturn(false);
        List<RDFConcept> rdfConcepts = rdFsparqlBridgeService.searchRDFConcepts("test", "search","en", 0, 0, new ArrayList<>());
        Assert.assertEquals(rdfConcepts.get(0).targetFrameworkUri, "test");
        Assert.assertEquals(rdfConcepts.isEmpty(), false);
    }

    @Test
    public void givenEmptyFrameworkWhenSearchCountryByEuvocFieldThenResultIsEmpty() {
        List<RDFConcept> rdfConcepts = rdFsparqlBridgeService.searchCountryByEuvocField("", "", new ArrayList<>());
        Assert.assertEquals(rdfConcepts.isEmpty(), true);
    }

    @Test
    public void givenDataFrameworkWhenSearchCountryByEuvocFieldThenResultIsNotEmpty() {
        Mockito.when(resultSet.hasNext()).thenReturn(true).thenReturn(false).thenReturn(true).thenReturn(false);
        List<RDFConcept> rdfConcepts = rdFsparqlBridgeService.searchCountryByEuvocField("test", "en", new ArrayList<>());
        Assert.assertEquals(rdfConcepts.get(0).targetFrameworkUri, "test");
        Assert.assertEquals(rdfConcepts.isEmpty(), false);
    }

    @Test
    public void givenEmptyFrameworkWhenSearchBroaderConceptsThenResultIsEmpty() {
        Set<RDFConcept> rdfConcepts = rdFsparqlBridgeService.searchBroaderConcepts("", "", "", "", 0, 0, new ArrayList<>());
        Assert.assertEquals(rdfConcepts.isEmpty(), true);
    }

    @Test
    public void givenDataFrameworkWhenSearchBroaderConceptsThenResultIsNotEmpty() {
        Mockito.when(resultSet.hasNext()).thenReturn(true).thenReturn(false).thenReturn(true).thenReturn(false);
        Set<RDFConcept> rdfConcepts = rdFsparqlBridgeService.searchBroaderConcepts("test","broader", "search","en", 0, 0, new ArrayList<>());
        Assert.assertEquals(rdfConcepts.stream().findFirst().get().getTargetFrameworkUri(), "test");
        Assert.assertEquals(rdfConcepts.isEmpty(), false);
    }

    @Test
    public void givenEmptyFrameworkWhenSearchISCEDFTreeConceptsThenResultIsEmpty() {
        Set<RDFConcept> rdfConcepts = rdFsparqlBridgeService.searchISCEDFTreeConcepts("", "", "", new ArrayList<>());
        Assert.assertEquals(rdfConcepts.isEmpty(), true);
    }

    @Test
    public void givenDataFrameworkWhenSearchICEDTreeConceptsThenResultIsNotEmpty() {
        Mockito.when(resultSet.hasNext()).thenReturn(true).thenReturn(false).thenReturn(true).thenReturn(false);
        Set<RDFConcept> rdfConcepts = rdFsparqlBridgeService.searchISCEDFTreeConcepts("test","search","en", new ArrayList<>());
        Assert.assertEquals(rdfConcepts.stream().findFirst().get().getTargetFrameworkUri(), "test");
        Assert.assertEquals(rdfConcepts.isEmpty(), false);
    }

    @Test
    public void givenEmptyFrameworkWhenSearchTreeConceptsThenResultIsEmpty() {
        Set<RDFConcept> rdfConcepts = rdFsparqlBridgeService.searchTreeConcepts("", "");
        Assert.assertEquals(rdfConcepts.isEmpty(), true);
    }

    @Test
    public void givenDataFrameworkWhenSearchTreeConceptsThenResultIsNotEmpty() {
        Mockito.when(resultSet.hasNext()).thenReturn(true).thenReturn(false).thenReturn(true).thenReturn(false);
        Set<RDFConcept> rdfConcepts = rdFsparqlBridgeService.searchTreeConcepts("test","URI");
        Assert.assertEquals(rdfConcepts.stream().findFirst().get().getTargetFrameworkUri(), "test");
        Assert.assertEquals(rdfConcepts.isEmpty(), false);
    }

    @Test
    public void givenEmptyFrameworkWhenSearchLanguagesByLangThenResultIsEmpty() {
        RDFConcept rdfConcepts = rdFsparqlBridgeService.searchLanguagesByLang("", "", new ArrayList<>());
        Assert.assertNull(rdfConcepts);
    }

    @Test
    public void givenDataFrameworkWhenSearchLanguagesByLangThenResultIsNotEmpty() {
        Mockito.when(resultSet.hasNext()).thenReturn(true).thenReturn(false).thenReturn(true).thenReturn(false);
        RDFConcept rdfConcepts = rdFsparqlBridgeService.searchLanguagesByLang("test","URI", new ArrayList<>());
        Assert.assertEquals(rdfConcepts.getTargetFrameworkUri(), "test");
        Assert.assertNotNull(rdfConcepts);
    }

    @Test
    public void givenEmptyFrameworkWhenCountConceptsThenResultIsEmpty() {
        int result = rdFsparqlBridgeService.countConcepts("", "", "", "");
        Assert.assertEquals(0, result);
    }

    @Test
    public void givenDataFrameworkWhenCountConceptsThenResultIsNotEmpty() {
        Mockito.when(resultSet.hasNext()).thenReturn(true).thenReturn(false).thenReturn(true).thenReturn(false);
        int result = rdFsparqlBridgeService.countConcepts("test","broader", "search", "en");
        Assert.assertEquals(1, result);
    }

    @Test
    public void givenEmptyWhenSearchTypeAndSchemaForCredTypeThenResultIsEmpty() {
        Pair<String, URI> result = rdFsparqlBridgeService.searchTypeAndSchemaForCredType("");
        Assert.assertNull(result);
    }

    @Test
    public void givenDataFrameworkWhenSearchTypeAndSchemaForCredTypeThenResultIsNotEmpty() {
        Mockito.when(resultSet.hasNext()).thenReturn(true).thenReturn(false).thenReturn(true).thenReturn(false);

        Pair<String, URI> result = rdFsparqlBridgeService.searchTypeAndSchemaForCredType(ControlledListConcept.CREDENTIAL_TYPE_CONVERTED.getUrl());
        Assert.assertEquals("Converted", result.getKey());

        Pair<String, URI> result2 = rdFsparqlBridgeService.searchTypeAndSchemaForCredType(ControlledListConcept.CREDENTIAL_TYPE_DIPLOMA_SUPPLEMENT.getUrl());
        Assert.assertEquals("DiplomaSupplement", result2.getKey());

        Pair<String, URI> result3 = rdFsparqlBridgeService.searchTypeAndSchemaForCredType(ControlledListConcept.CREDENTIAL_TYPE_GENERIC.getUrl());
        Assert.assertEquals("Generic", result3.getKey());

        Pair<String, URI> result4 = rdFsparqlBridgeService.searchTypeAndSchemaForCredType(ControlledListConcept.CREDENTIAL_TYPE_ISSUED_MANDATE.getUrl());
        Assert.assertEquals("IssuedByMandate", result4.getKey());

        Pair<String, URI> result5 = rdFsparqlBridgeService.searchTypeAndSchemaForCredType(ControlledListConcept.CREDENTIAL_TYPE_ACCREDITATION.getUrl());
        Assert.assertEquals("Accredited", result5.getKey());

    }

    @Test
    public void givenEmptyFrameworkWhenSearchLanguageISO639ByURIThenResultIsEmpty() {
        String rdfConcepts = rdFsparqlBridgeService.searchLanguageISO639ByURI("");
        Assert.assertNull(rdfConcepts);
    }

    @Test
    public void givenDataFrameworkWhenSearchLanguageISO639ByURIThenResultIsNotEmpty() {
        Mockito.when(resultSet.hasNext()).thenReturn(true).thenReturn(false).thenReturn(true).thenReturn(false);
        String rdfConcepts = rdFsparqlBridgeService.searchLanguageISO639ByURI("URI");
        Assert.assertEquals(rdfConcepts, "test");
        Assert.assertNotNull(rdfConcepts);
    }

    @Test
    public void givenRDFConceptWhenToConceptDTOThenResultIsNotNull() {
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

}
