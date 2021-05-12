package eu.europa.ec.empl.edci.service;

import eu.europa.ec.empl.edci.config.service.IConfigService;
import eu.europa.ec.empl.edci.constants.EuropassConstants;
import eu.europa.ec.empl.edci.datamodel.controlledList.RDFConcept;
import eu.europa.ec.empl.edci.datamodel.controlledList.RDFConceptScheme;
import org.apache.commons.lang3.StringUtils;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
public class RDFsparqlBridgeService {

    private static final Logger logger = Logger.getLogger(RDFsparqlBridgeService.class);

    @Autowired
    private EDCIMessageService edciMessageService;

    @Autowired
    private IConfigService iConfigService;

    private static Map<String, RDFConceptScheme> controlledListsConceptSchemes = new HashMap<>();

    public List<RDFConcept> searchRDFConcepts(String targetFramework, String locale, Collection<String> retrieveLangs, String... uris) {

        RDFConceptScheme controlledListCS = searchConceptScheme(targetFramework);

        if (controlledListCS == null || uris == null || uris.length == 0) {
            return new ArrayList<>();
        }

        String skosPrefix = "prefix skos: <http://www.w3.org/2004/02/skos/core#>\n";
        String rdfPrefix = "prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n";
        String authPrefix = "prefix auth: <http://publications.europa.eu/ontology/authority/>\n";

        String retrieveLangsFilter = "";
        if (retrieveLangs != null && !retrieveLangs.isEmpty()) {
            retrieveLangsFilter = "FILTER ( LANG(?targetName) in (" + retrieveLangs.stream().collect(Collectors.joining("','", "'", "'")) + ") )";
        }

        String innerQueryString =
                "select ?concept ?externalResource \n" +
                        "WHERE{  \n" +
                        "?concept rdf:type ?type .  \n" +
                        "?concept skos:prefLabel ?label .  \n" +
                        "OPTIONAL { ?concept auth:deprecated ?deprecated } . \n" +
                        "OPTIONAL { ?concept skos:externalToDO ?externalResource} \n" +
                        " \n" +
                        "FILTER(STR(?type) = \"http://www.w3.org/2004/02/skos/core#Concept\"  \n" +
                        "&& LANG(?label) = \"" + locale +
                        "\" && STR(?concept) in (" + Arrays.stream(uris).map(String::trim).collect(Collectors.joining("','", "'", "'")) + ") \n" +
                        " && COALESCE(STR(?deprecated),\"false\") = \"false\" " +
                        " ) \n" +
                        "}  \n" +
                        " ";

        String outerQueryString = skosPrefix + rdfPrefix + authPrefix +
                "SELECT ?concept ?externalResource ?targetName \n" +
                "{  \n" +
                "?concept skos:prefLabel ?targetName .  \n" +
                retrieveLangsFilter +
                "{" + innerQueryString + "}" +
                "}";

        Set<RDFConcept> rdfConcepts = null;

        try (QueryExecution qexec = QueryExecutionFactory.sparqlService(
                iConfigService.getString(EuropassConstants.CONFIG_PUBLICATIONS_RDF_SPARQL_ENDPOINT), outerQueryString, targetFramework)) {

            ResultSet rs = qexec.execSelect();

            rdfConcepts = toRDFConcept(targetFramework, rs);

        }

        return new ArrayList<>(rdfConcepts);
    }

    public List<RDFConcept> searchCountryByEuvocField(String targetFramework, String euvocId, Collection<String> retrieveLangs) {

        String euvocAttribute = "http://publications.europa.eu/ontology/euvoc#TED";

        RDFConceptScheme controlledListCS = searchConceptScheme(targetFramework);

        if (controlledListCS == null || StringUtils.isEmpty(euvocId)) {
            return new ArrayList<>();
        }

        String skosPrefix = "prefix skos: <http://www.w3.org/2004/02/skos/core#>\n";
        String rdfPrefix = "prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n";
        String authPrefix = "prefix auth: <http://publications.europa.eu/ontology/authority/>\n";

        String retrieveLangsFilter = "";
        if (retrieveLangs != null && !retrieveLangs.isEmpty()) {
            retrieveLangsFilter = "FILTER ( LANG(?targetName) in (" + retrieveLangs.stream().collect(Collectors.joining("','", "'", "'")) + ") )";
        }

        String innerQueryString =
                "select ?concept ?externalResource \n" +
                        "WHERE{  \n" +
                        "?concept rdf:type ?type .  \n" +
                        "?concept skos:notation ?euvoc . \n" +
                        "OPTIONAL { ?concept auth:deprecated ?deprecated } . \n" +
                        "OPTIONAL { ?concept skos:externalToDO ?externalResource} \n" +
                        " \n" +
                        "FILTER(STR(?type) = \"http://www.w3.org/2004/02/skos/core#Concept\"  \n" +
                        " && ?euvoc = \"" + euvocId + "\"^^<" + euvocAttribute + "> \n" +
                        " && COALESCE(STR(?deprecated),\"false\") = \"false\" " +
                        " ) \n" +
                        "}  \n" +
                        " ";

        String outerQueryString = skosPrefix + rdfPrefix + authPrefix +
                "SELECT ?concept ?externalResource ?targetName \n" +
                "{  \n" +
                "?concept skos:prefLabel ?targetName .  \n" +
                retrieveLangsFilter +
                "{" + innerQueryString + "}" +
                "}";

        Set<RDFConcept> rdfConcepts = null;

        try (QueryExecution qexec = QueryExecutionFactory.sparqlService(
                iConfigService.getString(EuropassConstants.CONFIG_PUBLICATIONS_RDF_SPARQL_ENDPOINT), outerQueryString, targetFramework)) {

            ResultSet rs = qexec.execSelect();

            rdfConcepts = toRDFConcept(targetFramework, rs);

        }

        return new ArrayList<>(rdfConcepts);
    }

    public List<RDFConcept> searchRDFConcepts(String targetFramework, String search, String locale, int page, int size, Collection<String> retrieveLangs) {

        RDFConceptScheme controlledListCS = searchConceptScheme(targetFramework);

        if (controlledListCS == null) {
            return new ArrayList<>();
        }

        String skosPrefix = "prefix skos: <http://www.w3.org/2004/02/skos/core#>\n";
        String rdfPrefix = "prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n";
        String authPrefix = "prefix auth: <http://publications.europa.eu/ontology/authority/>\n";
        String termsPrefix = "prefix terms: <http://purl.org/dc/terms/>\n";

        String retrieveLangsFilter = "";
        if (retrieveLangs != null && !retrieveLangs.isEmpty()) {
            retrieveLangsFilter = "FILTER ( LANG(?targetName) in (" + retrieveLangs.stream().collect(Collectors.joining("','", "'", "'")) + ") )";
        }

        String innerQueryString =
                "select ?concept (STR(?xsd) as ?externalResource) \n" +
                        "WHERE{  \n" +
                        "?concept rdf:type ?type .  \n" +
                        "?concept skos:prefLabel ?label .  \n" +
                        "OPTIONAL { ?concept auth:deprecated ?deprecated } . \n" +
                        "OPTIONAL { ?uri terms:conformsTo ?to }. \n" +
                        "OPTIONAL { ?to rdf:toDo ?xsd } \n" +
                        " \n" +
                        "FILTER(STR(?type) = \"http://www.w3.org/2004/02/skos/core#Concept\"  \n" +
                        "&& LANG(?label) = \"" + locale + "\" && regex(?label, \"" + search + "\", \"i\") \n" +
                        " && COALESCE(STR(?deprecated),\"false\") = \"false\" " +
                        " ) \n" +
                        "}  \n" +
                        "order by ASC(?concept) \n" +//+ direction + "(?" + sort + ")  \n" +
                        "offset " + (page * size) + " \n" +
                        "limit " + (size + (page * size)) + " ";

        String outerQueryString = skosPrefix + rdfPrefix + authPrefix + termsPrefix +
                "SELECT ?concept ?externalResource ?targetName \n" +
                "{  \n" +
                "?concept skos:prefLabel ?targetName .  \n" +
                retrieveLangsFilter +
                "{" + innerQueryString + "}" +
                "}";

        Set<RDFConcept> rdfConcepts = null;

        try (QueryExecution qexec = QueryExecutionFactory.sparqlService(
                iConfigService.getString(EuropassConstants.CONFIG_PUBLICATIONS_RDF_SPARQL_ENDPOINT), outerQueryString, targetFramework)) {

            ResultSet rs = qexec.execSelect();

            rdfConcepts = toRDFConcept(targetFramework, rs);

        }

        return new ArrayList(rdfConcepts);

    }

    public Set<RDFConcept> searchBroaderConcepts(String targetFramework, String targetBroader, String search, String locale, int page, int size, Collection<String> retrieveLangs) {

        RDFConceptScheme controlledListCS = searchConceptScheme(targetFramework);

        if (controlledListCS == null) {
            return new HashSet<RDFConcept>(new ArrayList<>());
        }

        String skosPrefix = "prefix skos: <http://www.w3.org/2004/02/skos/core#>\n";
        String rdfPrefix = "prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n";
        String authPrefix = "prefix auth: <http://publications.europa.eu/ontology/authority/>\n";

        String retrieveLangsFilter = "";
        if (retrieveLangs != null && !retrieveLangs.isEmpty()) {
            retrieveLangsFilter = "FILTER ( LANG(?targetName) in (" + retrieveLangs.stream().collect(Collectors.joining("','", "'", "'")) + ") )";
        }

        String broaderParent = targetBroader != null ? "broader" : "topConceptOf";
        String valueParent = targetBroader != null ? targetBroader : targetFramework;


        String innerQueryString =
                "select ?concept ?externalResource \n" +
                        "WHERE{  \n" +
                        "?concept rdf:type ?type .  \n" +
                        "?concept skos:prefLabel ?label .  \n" +
                        "?concept skos:" + broaderParent + " ?" + broaderParent + " .  \n" +
                        "OPTIONAL { ?concept auth:deprecated ?deprecated } . \n" +
                        "OPTIONAL { ?concept skos:externalToDO ?externalResource} \n" +
                        " \n" +
                        "FILTER(STR(?type) = \"http://www.w3.org/2004/02/skos/core#Concept\"  \n" +
                        "&& STR(?" + broaderParent + ") = \"" + valueParent + "\"  \n" +
                        "&& LANG(?label) = \"" + locale + "\" && regex(?label, \"" + search + "\", \"i\") \n" +
                        " && COALESCE(STR(?deprecated),\"false\") = \"false\" " +
                        " ) \n" +
                        "}  \n" +
                        "order by ASC(?concept) \n" +
                        "offset " + (page * size) + " \n" +
                        "limit " + (size + (page * size)) + " ";

        String outerQueryString = skosPrefix + rdfPrefix + authPrefix +
                "SELECT ?concept ?externalResource ?targetName \n" +
                "{  \n" +
                "?concept skos:prefLabel ?targetName .  \n" +
                retrieveLangsFilter +
                "{" + innerQueryString + "}" +
                "}";

        Set<RDFConcept> rdfConcepts = null;

        try (QueryExecution qexec = QueryExecutionFactory.sparqlService(
                iConfigService.getString(EuropassConstants.CONFIG_PUBLICATIONS_RDF_SPARQL_ENDPOINT), outerQueryString, targetFramework)) {

            ResultSet rs = qexec.execSelect();

            rdfConcepts = toRDFConcept(targetFramework, rs);

        }

        return rdfConcepts;

    }


    public Set<RDFConcept> searchISCEDFTreeConcepts(String targetFramework, String search, String locale, Collection<String> retrieveLangs) {

        RDFConceptScheme controlledListCS = searchConceptScheme(targetFramework);

        if (controlledListCS == null) {
            return new HashSet<RDFConcept>(new ArrayList<>());
        }

        String skosPrefix = "prefix skos: <http://www.w3.org/2004/02/skos/core#>\n";
        String rdfPrefix = "prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n";
        String authPrefix = "prefix auth: <http://publications.europa.eu/ontology/authority/>\n";
        String dcPrefix = "prefix dc: <http://purl.org/dc/elements/1.1/>\n";

        String retrieveLangsFilter = "";
        if (retrieveLangs != null && !retrieveLangs.isEmpty()) {
            retrieveLangsFilter = "FILTER ( LANG(?targetName) in (" + retrieveLangs.stream().collect(Collectors.joining("','", "'", "'")) + ") )";
        }

        String queryString = skosPrefix + rdfPrefix + authPrefix + dcPrefix +
                "select ?id (?id as ?targetNamePrefix) ?concept ?targetName\n" +
                "\n" +
                "WHERE{  \n" +
                "?concept a skos:Concept .\n" +
                "?concept dc:identifier ?id .\n" +
                "?concept skos:prefLabel ?label .  \n" +
                "OPTIONAL { ?concept skos:prefLabel ?targetName . " + retrieveLangsFilter + " } .  \n" +
                "OPTIONAL { ?concept auth:deprecated ?deprecated } . \n" +
                "\n" +
                "FILTER(LANG(?label) = \"" + locale + "\" && regex(?label, \"" + search + "\", \"i\") \n" +
                " && COALESCE(STR(?deprecated),\"false\") = \"false\" " +
                ") \n" +
                "}  \n" +
                "order by ASC(?id)\n";

        Set<RDFConcept> rdfConcepts = null;

        try (QueryExecution qexec = QueryExecutionFactory.sparqlService(
                iConfigService.getString(EuropassConstants.CONFIG_PUBLICATIONS_RDF_SPARQL_ENDPOINT), queryString, targetFramework)) {

            ResultSet rs = qexec.execSelect();

            rdfConcepts = toRDFConcept(targetFramework, rs);

        }

        return rdfConcepts;

    }

    public RDFConcept searchLanguagesByLang(String targetFramework, String lang, Collection<String> retrieveLangs) {

        RDFConceptScheme controlledListCS = searchConceptScheme(targetFramework);

        if (controlledListCS == null || lang == null) {
            return null;
        }

        String skosPrefix = "prefix skos: <http://www.w3.org/2004/02/skos/core#>\n";
        String rdfPrefix = "prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n";
        String authPrefix = "prefix auth: <http://publications.europa.eu/ontology/authority/>\n";

        String retrieveLangsFilter = "";
        if (retrieveLangs != null && !retrieveLangs.isEmpty()) {
            retrieveLangsFilter = "FILTER ( LANG(?targetName) in (" + retrieveLangs.stream().collect(Collectors.joining("','", "'", "'")) + ") )";
        }

        String innerQueryString =
                "select ?concept \n" +
                        "WHERE{  \n" +
                        "?concept rdf:type ?type .  \n" +
                        "?concept skos:notation ?notation .  \n" +
                        "OPTIONAL { ?concept auth:deprecated ?deprecated } .  \n" +
                        "FILTER(\n" +
                        "STR(?type) = \"http://www.w3.org/2004/02/skos/core#Concept\" \n" +
                        "&& STR(?notation) = \"" + lang + "\" \n" +
                        "&& STR(datatype(?notation)) = \"http://publications.europa.eu/ontology/euvoc#ISO_639_1\"\n" +
                        " && COALESCE(STR(?deprecated),\"false\") = \"false\" " +
                        " ) \n" +
                        "}  ";

        String outerQueryString = skosPrefix + rdfPrefix + authPrefix +
                "SELECT ?concept ?targetName \n" +
                "{  \n" +
                "?concept skos:prefLabel ?targetName .  \n" +
                retrieveLangsFilter +
                "{" + innerQueryString + "}" +
                "}";

        Set<RDFConcept> rdfConcepts = null;

        try (QueryExecution qexec = QueryExecutionFactory.sparqlService(
                iConfigService.getString(EuropassConstants.CONFIG_PUBLICATIONS_RDF_SPARQL_ENDPOINT), outerQueryString, targetFramework)) {

            ResultSet rs = qexec.execSelect();

            rdfConcepts = toRDFConcept(targetFramework, rs);

        }

        return rdfConcepts.isEmpty() ? null : rdfConcepts.iterator().next();
    }

    public Integer countConcepts(String targetFramework, String targetBroader, String search, String locale, Collection<String> retrieveLangs) {

        Integer returnValue = 0;

        String skosPrefix = "prefix skos: <http://www.w3.org/2004/02/skos/core#>\n";
        String rdfPrefix = "prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n";
        String authPrefix = "prefix auth: <http://publications.europa.eu/ontology/authority/>\n";

        String retrieveLangsFilter = "";
        if (retrieveLangs != null && !retrieveLangs.isEmpty()) {
            retrieveLangsFilter = " LANG(?targetName) in (" + retrieveLangs.stream().collect(Collectors.joining("','", "'", "'")) + ") ";
        }

        String queryString = skosPrefix + rdfPrefix + authPrefix;

        if (targetBroader == null || targetBroader.isEmpty()) {
            queryString = queryString +
                    "select (count(?concept) as ?co) \n" +
                    "WHERE{   \n" +
                    "?concept rdf:type ?type .   \n" +
                    "?concept skos:prefLabel ?label .   \n" +
                    "OPTIONAL { ?concept auth:deprecated ?deprecated } .    \n" +
                    "  \n" +
                    "FILTER(STR(?type) = \"http://www.w3.org/2004/02/skos/core#Concept\"  \n" +
                    "&& LANG(?label) = \"" + locale + "\" && regex(?label, \"" + search + "\", \"i\") \n" +
                    retrieveLangsFilter +
                    " && COALESCE(STR(?deprecated),\"false\") = \"false\" " +
                    " )  \n" +
                    "}  \n";
        } else {
            queryString = queryString +
                    "select (count(?concept) as ?co) \n" +
                    "WHERE{   \n" +
                    "?concept rdf:type ?type .   \n" +
                    "?concept skos:prefLabel ?label .   \n" +
                    "?concept skos:broader ?broader .   \n" +
                    "OPTIONAL { ?concept auth:deprecated ?deprecated } .    \n" +
                    "  \n" +
                    "FILTER(STR(?type) = \"http://www.w3.org/2004/02/skos/core#Concept\"  \n" +
                    "&& STR(?broader) = \"" + targetBroader + "\"  \n" +
                    "&& LANG(?label) = \"" + locale + "\" && regex(?label, \"" + search + "\", \"i\") \n" +
                    retrieveLangsFilter +
                    " && COALESCE(STR(?deprecated),\"false\") = \"false\" " +
                    " )  \n" +
                    "}  \n";
        }
        try (QueryExecution qexec = QueryExecutionFactory.sparqlService(
                iConfigService.getString(EuropassConstants.CONFIG_PUBLICATIONS_RDF_SPARQL_ENDPOINT), queryString, targetFramework)) {

            ResultSet rs = qexec.execSelect();

            if (rs.hasNext()) {
                QuerySolution rb = rs.nextSolution();

                returnValue = rb.get(rb.varNames().next()).asLiteral().getInt();
            }

        } catch (Exception e) {
            logger.error(e);
        }

        return returnValue;

    }

    public RDFConceptScheme searchConceptScheme(String targetFramework) {

        RDFConceptScheme rdfConceptScheme = controlledListsConceptSchemes.get(targetFramework);

        if (rdfConceptScheme != null) {
            return rdfConceptScheme;
        }

        String skosPrefix = "prefix skos: <http://www.w3.org/2004/02/skos/core#>\n";
        String rdfPrefix = "prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n";
        String authPrefix = "prefix auth: <http://publications.europa.eu/ontology/authority/>\n";

        String outerQueryString = skosPrefix + rdfPrefix + authPrefix +
                "select ?targetName ?targetNotation \n" +
                "WHERE{  \n" +
                "?concept rdf:type ?type .  \n" +
                "?concept skos:prefLabel ?targetName .  \n" +
                "OPTIONAL { ?concept auth:table.id ?targetNotation } .\n" +
                " \n" +
                "FILTER( \n" +
                "STR(?type) = \"http://www.w3.org/2004/02/skos/core#ConceptScheme\"  \n" +
                ")  \n" +
                "}";

        try (QueryExecution qexec = QueryExecutionFactory.sparqlService(
                iConfigService.getString(EuropassConstants.CONFIG_PUBLICATIONS_RDF_SPARQL_ENDPOINT), outerQueryString, targetFramework)) {

            ResultSet rs = qexec.execSelect();

            rdfConceptScheme = toRDFConceptScheme(targetFramework, rs);

            controlledListsConceptSchemes.put(targetFramework, rdfConceptScheme);
        }

        return rdfConceptScheme;

    }

    public Set<RDFConcept> toRDFConcept(String targetFrameworkURI, ResultSet rs) {

        Set<RDFConcept> conceptList = new LinkedHashSet<>();
        RDFConcept rdfConcept = new RDFConcept();

        while (rs.hasNext()) {

            QuerySolution rb = rs.nextSolution();

            RDFNode uri = rb.get("concept");

            if (uri != null) {

                if (rdfConcept.getUri() == null || !uri.toString().equals(rdfConcept.getUri())) {
                    rdfConcept = new RDFConcept();
                    rdfConcept.setUri(uri.toString());
                    rdfConcept.setTargetFrameworkUri(targetFrameworkURI);
                    conceptList.add(rdfConcept);
                }

                try {
                    Literal targetName = rb.getLiteral("targetName");
                    if (targetName != null) {
                        String prefix = "";
                        RDFNode targetNamePrefix = rb.get("targetNamePrefix");
                        if (targetNamePrefix != null) {
                            prefix = targetNamePrefix.toString() + " - ";
                        }
                        rdfConcept.addTargetName(targetName.getLanguage(), prefix + targetName.getString());
                    }
                } catch (Exception e) {
                    logger.error("Error loading Concept name literal for " + targetFrameworkURI + " controlled list", e);
                }

                Literal externalResource = rb.getLiteral("externalResource");
                if (rdfConcept.getExternalResource() == null && externalResource != null) {
                    //TODO: remove this IF (not the else) when the Credential externalResource is well published
                    if ("https://data.europa.eu/snb/resource/distribution/v1/xsd/schema/genericschema.xsd".equals(externalResource.getString())) {
                        if ("http://data.europa.eu/snb/credential/e34929035b".equals(rdfConcept.getUri())) {
                            rdfConcept.setExternalResource("http://52.57.141.197/fs/0.10.0/credential/xsd/edci_generic.xsd");
                        } else if ("http://data.europa.eu/snb/credential/bdc47cb449".equals(rdfConcept.getUri())) {
                            rdfConcept.setExternalResource("http://52.57.141.197/fs/0.10.0/credential/xsd/entitlementsOnly.xsd");
                        } else if ("http://data.europa.eu/snb/credential/48b514e72a".equals(rdfConcept.getUri())) {
                            rdfConcept.setExternalResource("http://52.57.141.197/fs/0.10.0/credential/xsd/edci_onlyOneActivity.xsd");
                        } else {
                            rdfConcept.setExternalResource("http://52.57.141.197/fs/0.10.0/credential/xsd/edci_generic.xsd");
                        }
                    } else {
                        rdfConcept.setExternalResource(externalResource.getString());
                    }
                }

            }

        }

        if (rdfConcept.getTargetName() != null && rdfConcept.getTargetName().

                isEmpty()) {
            rdfConcept.setTargetName(null);
        }

        return conceptList;

    }


    public RDFConceptScheme toRDFConceptScheme(String targetFrameworkURI, ResultSet rs) {

        RDFConceptScheme rdfConceptScheme = new RDFConceptScheme();

        rdfConceptScheme.setTargetFrameworkUri(targetFrameworkURI);

        while (rs.hasNext()) {
            QuerySolution rb = rs.nextSolution();

            Literal targetNotation = rb.getLiteral("targetNotation");
            if (targetNotation != null && rdfConceptScheme.getTargetNotation() == null) {
                rdfConceptScheme.setTargetNotation(targetNotation.getString());
            }

            try {
                Literal targetName = rb.getLiteral("targetName");
                if (targetName != null) {
                    rdfConceptScheme.addTargetFramework(targetName.getLanguage(), targetName.getString());
                }
            } catch (Exception e) {
                logger.error("Error loading ConceptSchema name literal for " + targetFrameworkURI + " controlled list", e);
            }

        }

        if (rdfConceptScheme.getTargetFramework() != null && rdfConceptScheme.getTargetFramework().isEmpty()) {
            rdfConceptScheme.setTargetFramework(null);
        }

        return rdfConceptScheme;

    }

    public void resetAllControlledListsConceptSchemes() {
        controlledListsConceptSchemes.clear();
    }
}

