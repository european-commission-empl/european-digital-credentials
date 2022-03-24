package integration.eu.europa.ec.empl.edci.issuer.service.spec;

import eu.europa.ec.empl.edci.config.service.IConfigService;
import eu.europa.ec.empl.edci.service.ControlledListCommonsService;
import eu.europa.ec.empl.edci.service.RDFsparqlBridgeService;
import integration.eu.europa.ec.empl.base.AbstractIntegrationBaseTest;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Spy;

public class SPARQLITest extends AbstractIntegrationBaseTest {

    @InjectMocks
    ControlledListCommonsService controlledListCommonsService;

    @Spy
    @InjectMocks
    private RDFsparqlBridgeService rdfSparqlBridgeService;

    @Spy
    private IConfigService iConfigService;

    //We've had some connectivity issues when launching this tests

    @Test
    public void searchConcepts_shouldReturnValues_whenCalled() throws Exception {

//        Mockito.when(issuerConfigService.getString(Constant.RDF_SPARQL_ENDPOINT)).thenReturn("http://publications.europa.eu/webapi/rdf/sparql");
//
//        Page<CodeDTDAO> code = rdfSearchService.searchConcepts(ControlledList.HUMAN_SEX.getUrl(), "ma", "en", 0, 1000, Arrays.asList("en", "es", "it"));
//
//        Assert.assertNotEquals(0, code.getTotalElements());
//
//        List<RDFConcept> extRes = rdfSearchService.searchRDFConceptsByUri(ControlledList.CREDENTIAL_TYPE.getUrl(), "", "en", 0, 1000, Arrays.asList("en", "es", "it"));
//
//        Assert.assertNotEquals(0, extRes.size());
//        Assert.assertNotNull(extRes.iterator().next().getExternalResource());

    }

    @Test
    public void searchLanguages_shouldReturnValues_whenCalled() throws Exception {

//        Mockito.when(iConfigService.getString(Constant.RDF_SPARQL_ENDPOINT)).thenReturn("http://publications.europa.eu/webapi/rdf/sparql");
//
//        Code code = controlledListCommonsService.searchLanguageByLang("es");

    }

    @Test
    public void searchISCEDFConcepts_shouldReturnValues_whenCalled() throws Exception {

//        Mockito.when(issuerConfigService.getString(Constant.RDF_SPARQL_ENDPOINT)).thenReturn("http://publications.europa.eu/webapi/rdf/sparql");
//
//        Page<CodeDTDAO> code = rdfSearchService.searchISCEDFTreeConcepts(ControlledList.ISCED_F.getUrl(), "", "en", Arrays.asList("en", "es", "it"));
//
//        Assert.assertNotEquals(0, code.getTotalElements());
    }

    @Test
    public void searchBroaderConcepts_shouldReturnValues_whenCalled() throws Exception {

//        Mockito.when(issuerConfigService.getString(Constant.RDF_SPARQL_ENDPOINT)).thenReturn("http://publications.europa.eu/webapi/rdf/sparql");
//
//        Page<CodeDTDAO> parentCodes = rdfSearchService.searchConcepts(ControlledList.NQF.getUrl(), "", "en", 0, 5, Arrays.asList("en", "es", "it"));
//
//        Page<CodeDTDAO> code = rdfSearchService.searchBroaderConcepts(ControlledList.NQF.getUrl(), parentCodes.getContent().get(0).getUri(), "", "en", 0, 1000, Arrays.asList("en", "es", "it"));
//
//        Assert.assertNotEquals(0, code.getTotalElements());
    }

}
