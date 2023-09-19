package integration.eu.europa.ec.empl.edci.util;

import eu.europa.ec.empl.edci.constants.ControlledListConcept;
import eu.europa.ec.empl.edci.datamodel.controlledList.RDFConcept;
import eu.europa.ec.empl.edci.datamodel.controlledList.RDFConceptScheme;
import eu.europa.ec.empl.edci.datamodel.jsonld.model.dataTypes.ConceptDTO;
import eu.europa.ec.empl.edci.service.ControlledListCommonsService;
import eu.europa.ec.empl.edci.service.RDFsparqlBridgeService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RunWith(MockitoJUnitRunner.Silent.class)
public class ControlledListCommonsServiceITest {

    @InjectMocks
    protected ControlledListCommonsService controlledListCommonsService;

    @Mock
    private RDFsparqlBridgeService rdFsparqlBridgeService;


    private List<RDFConcept> rdfConcepts = new ArrayList<>();
    private RDFConcept rdfConcept = new RDFConcept();
    private RDFConceptScheme rdfConceptScheme = new RDFConceptScheme();

    @Before
    public void setUp() {
        String uri = new ConceptDTO().getIdPrefix(ConceptDTO.class);
        Map<String,String> targetName = new HashMap<>();
        targetName.put("en", "targetName");

        rdfConcept.setUri(uri);
        rdfConcept.setTargetName(targetName);
        rdfConcept.setTargetFrameworkUri(uri + "targetFramework");

        rdfConceptScheme.setTargetFramework(targetName);
        rdfConceptScheme.setTargetNotation("targetNotation");
        rdfConceptScheme.setTargetFrameworkUri(uri + "targetFramework");

        rdfConcepts.add(rdfConcept);
    }


    @Test
    public void givenEmptyDataWhenSearchRDFConceptsThenResultIsEmpty() {
        List<RDFConcept> rdfConceptList = controlledListCommonsService.searchRDFConcepts("", "", "", 0, 0, new ArrayList<>());
        Assert.assertEquals(true, rdfConceptList.isEmpty());
    }

    @Test
    public void givenDataWhenSearchRDFConceptsThenResultIsEmpty() {
        Mockito.doReturn(rdfConcepts).when(rdFsparqlBridgeService).searchRDFConcepts(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(),
                ArgumentMatchers.anyString(), ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt(), ArgumentMatchers.anyCollection());
        List<RDFConcept> rdfConceptList = controlledListCommonsService.searchRDFConcepts("targetFramework", "search", "locale", 1, 1, new ArrayList<>());
        Assert.assertEquals(false, rdfConceptList.isEmpty());
    }

    @Test
    public void givenEmptyDataWhenSearchConceptByConceptThenResultIsEmpty() {
        ConceptDTO conceptDTO = controlledListCommonsService.searchConceptByConcept(null);
        Assert.assertNull(conceptDTO);
    }

    @Test
    public void givenDataWhenSearchConceptByConceptThenResultIsEmpty() {
        Mockito.doReturn(rdfConcepts).when(rdFsparqlBridgeService).searchRDFConcepts(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(),
                ArgumentMatchers.anyCollection(), ArgumentMatchers.anyString());

        Mockito.doReturn(rdfConceptScheme).when(rdFsparqlBridgeService).searchConceptScheme(ArgumentMatchers.anyString());

        ConceptDTO conceptDTO = controlledListCommonsService.searchConceptByConcept(ControlledListConcept.CREDENTIAL_TYPE_CONVERTED);
        Assert.assertNotNull(conceptDTO);
        Assert.assertEquals(rdfConcept.getUri(), conceptDTO.getId().toString());
        Assert.assertEquals(rdfConcept.getTargetName().get("en"), conceptDTO.getPrefLabel().get("en").get(0));
        Assert.assertEquals(rdfConceptScheme.getTargetFrameworkUri(), conceptDTO.getInScheme().getId().toString());
    }

    @Test
    public void givenEmptyDataWhenSearchConceptByConceptLangThenResultIsEmpty() {
        ConceptDTO conceptDTO = controlledListCommonsService.searchConceptByConcept(null , "");
        Assert.assertNull(conceptDTO);
    }

    @Test
    public void givenDataWhenSearchConceptByConceptLangThenResultIsEmpty() {
        Mockito.doReturn(rdfConcepts).when(rdFsparqlBridgeService).searchRDFConcepts(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(),
                ArgumentMatchers.anyCollection(), ArgumentMatchers.anyString());

        Mockito.doReturn(rdfConceptScheme).when(rdFsparqlBridgeService).searchConceptScheme(ArgumentMatchers.anyString());

        ConceptDTO conceptDTO = controlledListCommonsService.searchConceptByConcept(ControlledListConcept.CREDENTIAL_TYPE_CONVERTED, "en");
        Assert.assertNotNull(conceptDTO);
        Assert.assertEquals(rdfConcept.getUri(), conceptDTO.getId().toString());
        Assert.assertEquals(rdfConcept.getTargetName().get("en"), conceptDTO.getPrefLabel().get("en").get(0));
        Assert.assertEquals(rdfConceptScheme.getTargetFrameworkUri(), conceptDTO.getInScheme().getId().toString());
    }

    @Test
    public void givenEmptyDataWhenSearchConceptByConceptListThenResultIsEmpty() {
        ConceptDTO conceptDTO = controlledListCommonsService.searchConceptByConcept(null, new ArrayList<>());
        Assert.assertNull(conceptDTO);
    }

    @Test
    public void givenDataWhenSearchConceptByConceptListThenResultIsEmpty() {
        Mockito.doReturn(rdfConcepts).when(rdFsparqlBridgeService).searchRDFConcepts(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(),
                ArgumentMatchers.anyCollection(), ArgumentMatchers.anyString());

        Mockito.doReturn(rdfConceptScheme).when(rdFsparqlBridgeService).searchConceptScheme(ArgumentMatchers.anyString());

        ConceptDTO conceptDTO = controlledListCommonsService.searchConceptByConcept(ControlledListConcept.CREDENTIAL_TYPE_CONVERTED, ControlledListCommonsService.ALLOWED_LANGS);
        Assert.assertNotNull(conceptDTO);
        Assert.assertEquals(rdfConcept.getUri(), conceptDTO.getId().toString());
        Assert.assertEquals(rdfConcept.getTargetName().get("en"), conceptDTO.getPrefLabel().get("en").get(0));
        Assert.assertEquals(rdfConceptScheme.getTargetFrameworkUri(), conceptDTO.getInScheme().getId().toString());
    }

    @Test
    public void givenEmptyDataWhenSearchConceptByConceptListLangThenResultIsEmpty() {
        ConceptDTO conceptDTO = controlledListCommonsService.searchConceptByConcept(null , new ArrayList<>(), "");
        Assert.assertNull(conceptDTO);
    }

    @Test
    public void givenDataWhenSearchConceptByConceptListLangThenResultIsEmpty() {
        Mockito.doReturn(rdfConcepts).when(rdFsparqlBridgeService).searchRDFConcepts(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(),
                ArgumentMatchers.anyCollection(), ArgumentMatchers.anyString());

        Mockito.doReturn(rdfConceptScheme).when(rdFsparqlBridgeService).searchConceptScheme(ArgumentMatchers.anyString());

        ConceptDTO conceptDTO = controlledListCommonsService.searchConceptByConcept(ControlledListConcept.CREDENTIAL_TYPE_CONVERTED, ControlledListCommonsService.ALLOWED_LANGS, "en");
        Assert.assertNotNull(conceptDTO);
        Assert.assertEquals(rdfConcept.getUri(), conceptDTO.getId().toString());
        Assert.assertEquals(rdfConcept.getTargetName().get("en"), conceptDTO.getPrefLabel().get("en").get(0));
        Assert.assertEquals(rdfConceptScheme.getTargetFrameworkUri(), conceptDTO.getInScheme().getId().toString());
    }

    @Test
    public void givenEmptyDataWhenSearchConceptByUriThenResultIsEmpty() {
        ConceptDTO conceptDTO = controlledListCommonsService.searchConceptByUri("", "", "");
        Assert.assertNull(conceptDTO);
    }

    @Test
    public void givenDataWhenSearchConceptByUriThenResultIsEmpty() {
        Mockito.doReturn(rdfConcepts).when(rdFsparqlBridgeService).searchRDFConcepts(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(),
                ArgumentMatchers.anyCollection(), ArgumentMatchers.anyString());

        Mockito.doReturn(rdfConceptScheme).when(rdFsparqlBridgeService).searchConceptScheme(ArgumentMatchers.anyString());

        ConceptDTO conceptDTO = controlledListCommonsService.searchConceptByUri("targetFramework", "search", "locale");
        Assert.assertNotNull(conceptDTO);
        Assert.assertEquals(rdfConcept.getUri(), conceptDTO.getId().toString());
        Assert.assertEquals(rdfConcept.getTargetName().get("en"), conceptDTO.getPrefLabel().get("en").get(0));
        Assert.assertEquals(rdfConceptScheme.getTargetFrameworkUri(), conceptDTO.getInScheme().getId().toString());
    }

    @Test
    public void givenEmptyDataWhenSearchConceptByUriListThenResultIsEmpty() {
        ConceptDTO conceptDTO = controlledListCommonsService.searchConceptByUri("", "", new ArrayList<>(), "");
        Assert.assertNull(conceptDTO);
    }

    @Test
    public void givenDataWhenSearchConceptByUriListThenResultIsEmpty() {
        Mockito.doReturn(rdfConcepts).when(rdFsparqlBridgeService).searchRDFConcepts(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(),
                ArgumentMatchers.anyCollection(), ArgumentMatchers.anyString());

        Mockito.doReturn(rdfConceptScheme).when(rdFsparqlBridgeService).searchConceptScheme(ArgumentMatchers.anyString());

        ConceptDTO conceptDTO = controlledListCommonsService.searchConceptByUri("targetFramework", "search", ControlledListCommonsService.ALLOWED_LANGS, "locale");
        Assert.assertNotNull(conceptDTO);
        Assert.assertEquals(rdfConcept.getUri(), conceptDTO.getId().toString());
        Assert.assertEquals(rdfConcept.getTargetName().get("en"), conceptDTO.getPrefLabel().get("en").get(0));
        Assert.assertEquals(rdfConceptScheme.getTargetFrameworkUri(), conceptDTO.getInScheme().getId().toString());
    }

    @Test
    public void givenEmptyDataWhenSearchConceptByUrisListThenResultIsEmpty() {
        ConceptDTO conceptDTO = controlledListCommonsService.searchConceptByUri("", "", "", new ArrayList<>());
        Assert.assertNull(conceptDTO);
    }

    @Test
    public void givenDataWhenSearchConceptByUrisListThenResultIsEmpty() {
        Mockito.doReturn(rdfConcepts).when(rdFsparqlBridgeService).searchRDFConcepts(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(),
                ArgumentMatchers.anyCollection(), ArgumentMatchers.anyString());

        Mockito.doReturn(rdfConceptScheme).when(rdFsparqlBridgeService).searchConceptScheme(ArgumentMatchers.anyString());

        ConceptDTO conceptDTO = controlledListCommonsService.searchConceptByUri("targetFramework", "search", "locale", ControlledListCommonsService.ALLOWED_LANGS);
        Assert.assertNotNull(conceptDTO);
        Assert.assertEquals(rdfConcept.getUri(), conceptDTO.getId().toString());
        Assert.assertEquals(rdfConcept.getTargetName().get("en"), conceptDTO.getPrefLabel().get("en").get(0));
        Assert.assertEquals(rdfConceptScheme.getTargetFrameworkUri(), conceptDTO.getInScheme().getId().toString());
    }

    @Test
    public void givenEmptyDataWhenSearchConceptByUriFrameworkThenResultIsEmpty() {
        ConceptDTO conceptDTO = controlledListCommonsService.searchConceptByUri("", "");
        Assert.assertNull(conceptDTO);
    }

    @Test
    public void givenDataWhenSearchConceptByUriFrameworkThenResultIsEmpty() {
        Mockito.doReturn(rdfConcepts).when(rdFsparqlBridgeService).searchRDFConcepts(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(),
                ArgumentMatchers.anyCollection(), ArgumentMatchers.anyString());

        Mockito.doReturn(rdfConceptScheme).when(rdFsparqlBridgeService).searchConceptScheme(ArgumentMatchers.anyString());

        ConceptDTO conceptDTO = controlledListCommonsService.searchConceptByUri("targetFramework", "uri");
        Assert.assertNotNull(conceptDTO);
        Assert.assertEquals(rdfConcept.getUri(), conceptDTO.getId().toString());
        Assert.assertEquals(rdfConcept.getTargetName().get("en"), conceptDTO.getPrefLabel().get("en").get(0));
        Assert.assertEquals(rdfConceptScheme.getTargetFrameworkUri(), conceptDTO.getInScheme().getId().toString());
    }

    @Test
    public void givenEmptyDataWhenSearchConceptsByUriListThenResultIsEmpty() {
        List<ConceptDTO> conceptDTO = controlledListCommonsService.searchConceptsByUri("", "", new ArrayList<>(), "");
        Assert.assertNull(conceptDTO);
    }

    @Test
    public void givenDataWhenSearchConceptsByUriListThenResultIsEmpty() {
        Mockito.doReturn(rdfConcepts).when(rdFsparqlBridgeService).searchRDFConcepts(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(),
                ArgumentMatchers.anyCollection(), ArgumentMatchers.anyString());

        Mockito.doReturn(rdfConceptScheme).when(rdFsparqlBridgeService).searchConceptScheme(ArgumentMatchers.anyString());

        List<ConceptDTO> conceptDTO = controlledListCommonsService.searchConceptsByUri("targetFramework", "search", ControlledListCommonsService.ALLOWED_LANGS, "locale");
        Assert.assertNotNull(conceptDTO);
        Assert.assertEquals(rdfConcept.getUri(), conceptDTO.get(0).getId().toString());
        Assert.assertEquals(rdfConcept.getTargetName().get("en"), conceptDTO.get(0).getPrefLabel().get("en").get(0));
        Assert.assertEquals(rdfConceptScheme.getTargetFrameworkUri(), conceptDTO.get(0).getInScheme().getId().toString());
    }

    @Test
    public void givenEmptyDataWhenSearchRDFConceptByUriFrameworkThenResultIsEmpty() {
        RDFConcept conceptDTO = controlledListCommonsService.searchRDFConceptByUri("", "");
        Assert.assertNull(conceptDTO);
    }

    @Test
    public void givenDataWhenSearchRDFConceptByUriFrameworkThenResultIsEmpty() {
        Mockito.doReturn(rdfConcepts).when(rdFsparqlBridgeService).searchRDFConcepts(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(),
                ArgumentMatchers.anyCollection(), ArgumentMatchers.anyString());

        RDFConcept conceptDTO = controlledListCommonsService.searchRDFConceptByUri("targetFramework", "uri");
        Assert.assertNotNull(conceptDTO);
    }

    @Test
    public void givenEmptyDataWhenSearchRDFConceptsByUriListThenResultIsEmpty() {
        List<RDFConcept> conceptDTO = controlledListCommonsService.searchRDFConceptsByUri("", "", new ArrayList<>(), "");
        Assert.assertTrue(conceptDTO.isEmpty());
    }

    @Test
    public void givenDataWhenSearchRDFConceptsByUriListThenResultIsEmpty() {
        Mockito.doReturn(rdfConcepts).when(rdFsparqlBridgeService).searchRDFConcepts(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(),
                ArgumentMatchers.anyCollection(), ArgumentMatchers.anyString());

        Mockito.doReturn(rdfConceptScheme).when(rdFsparqlBridgeService).searchConceptScheme(ArgumentMatchers.anyString());

        List<RDFConcept> conceptDTO = controlledListCommonsService.searchRDFConceptsByUri("targetFramework", "search", ControlledListCommonsService.ALLOWED_LANGS, "locale");
        Assert.assertFalse(conceptDTO.isEmpty());
    }

    @Test
    public void givenEmptyDataWhenDearchCountryByEuvocFieldThenResultIsEmpty() {
        ConceptDTO conceptDTO = controlledListCommonsService.searchCountryByEuvocField("", "", new ArrayList<>());
        Assert.assertNull(conceptDTO);
    }

    @Test
    public void givenDataWhenSearchCountryByEuvocFieldThenResultIsEmpty() {
        Mockito.doReturn(rdfConcepts).when(rdFsparqlBridgeService).searchCountryByEuvocField(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(),
                ArgumentMatchers.anyCollection());

        Mockito.doReturn(rdfConceptScheme).when(rdFsparqlBridgeService).searchConceptScheme(ArgumentMatchers.anyString());

        ConceptDTO conceptDTO = controlledListCommonsService.searchCountryByEuvocField("targetFramework", "uri", ControlledListCommonsService.ALLOWED_LANGS);

        Assert.assertNotNull(conceptDTO);
        Assert.assertEquals(rdfConcept.getUri(), conceptDTO.getId().toString());
        Assert.assertEquals(rdfConcept.getTargetName().get("en"), conceptDTO.getPrefLabel().get("en").get(0));
        Assert.assertEquals(rdfConceptScheme.getTargetFrameworkUri(), conceptDTO.getInScheme().getId().toString());
    }

    @Test
    public void givenEmptyDataWhenGetFileTypeThenResultIsEmpty() {
        ConceptDTO conceptDTO = controlledListCommonsService.getFileType(null);
        Assert.assertNull(conceptDTO);
    }

    @Test
    public void givenDataWhenGetFileTypeThenResultIsEmpty() {
        Mockito.doReturn(rdfConcepts).when(rdFsparqlBridgeService).searchRDFConcepts(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(),
                ArgumentMatchers.anyString(), ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt(), ArgumentMatchers.anyCollection());
        List<RDFConcept> rdfConceptList = controlledListCommonsService.searchRDFConcepts("targetFramework", "search", "locale", 1, 1, new ArrayList<>());
        Assert.assertEquals(false, rdfConceptList.isEmpty());
    }
}
