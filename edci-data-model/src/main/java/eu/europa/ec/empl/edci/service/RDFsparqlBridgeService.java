package eu.europa.ec.empl.edci.service;

import eu.europa.ec.empl.edci.config.service.BaseConfigService;
import eu.europa.ec.empl.edci.constants.ControlledList;
import eu.europa.ec.empl.edci.constants.ControlledListConcept;
import eu.europa.ec.empl.edci.constants.DataModelConstants;
import eu.europa.ec.empl.edci.datamodel.controlledList.RDFConcept;
import eu.europa.ec.empl.edci.datamodel.controlledList.RDFConceptScheme;
import eu.europa.ec.empl.edci.datamodel.jsonld.model.dataTypes.ConceptDTO;
import eu.europa.ec.empl.edci.datamodel.jsonld.model.dataTypes.ConceptSchemeDTO;
import eu.europa.ec.empl.edci.datamodel.jsonld.model.dataTypes.LiteralMap;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.sparql.exec.http.QueryExecutionHTTP;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
public class RDFsparqlBridgeService {

    private static final Logger logger = LogManager.getLogger(RDFsparqlBridgeService.class);

    @Autowired
    private EDCIMessageService edciMessageService;

    @Autowired
    private BaseConfigService iConfigService;

    private static Map<String, RDFConceptScheme> controlledListsConceptSchemes = new HashMap<>();
    private static Map<String, String> controlledListsLanguages = new HashMap<>();

    private static String LOCALE_ENGLISH = Locale.ENGLISH.getLanguage();

    public List<RDFConcept> searchRDFConcepts(String targetFramework, String locale, Collection<String> retrievedLangs, String... uris) {

        List<String> retrieveLangs = new ArrayList<>(retrievedLangs);
        if (!retrieveLangs.contains(locale)) {
            retrieveLangs.add(locale);
        }

        //When searching with a uri, the locale will always be true to ensure that the results are always available (it can happen that the concept is only published in english)
        //When filtering by searching, we need to use the locale because the filter works only with the selected locale, but not in this case
        //TODO: Remove locale parameter
        locale = LOCALE_ENGLISH;
        if (!retrieveLangs.contains(locale)) {
            retrieveLangs.add(locale);
        }

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

        try (QueryExecution qexec = QueryExecutionHTTP.create().endpoint(getiConfigService().getString(
                DataModelConstants.CONFIG_PUBLICATIONS_RDF_SPARQL_ENDPOINT,
                DataModelConstants.Defaults.CONFIG_PUBLICATIONS_RDF_SPARQL_ENDPOINT))
                .query(outerQueryString).addDefaultGraphURI(targetFramework).build()) {

            ResultSet rs = qexec.execSelect();

            rdfConcepts = toRDFConcept(targetFramework, rs);

        } catch (Exception e) {
            logger.error(String.format("Could not download controlled list item %s from controlled list %s", targetFramework, StringUtils.join(uris, "|")), e);
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

        try (QueryExecution qexec = QueryExecutionHTTP.create().endpoint(getiConfigService().getString(
                DataModelConstants.CONFIG_PUBLICATIONS_RDF_SPARQL_ENDPOINT,
                DataModelConstants.Defaults.CONFIG_PUBLICATIONS_RDF_SPARQL_ENDPOINT))
                .query(outerQueryString).addDefaultGraphURI(targetFramework).build()) {

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

        try (QueryExecution qexec = QueryExecutionHTTP.create().endpoint(getiConfigService().getString(
                DataModelConstants.CONFIG_PUBLICATIONS_RDF_SPARQL_ENDPOINT,
                DataModelConstants.Defaults.CONFIG_PUBLICATIONS_RDF_SPARQL_ENDPOINT))
                .query(outerQueryString).addDefaultGraphURI(targetFramework).build()) {

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

        try (QueryExecution qexec = QueryExecutionHTTP.create().endpoint(getiConfigService().getString(
                DataModelConstants.CONFIG_PUBLICATIONS_RDF_SPARQL_ENDPOINT,
                DataModelConstants.Defaults.CONFIG_PUBLICATIONS_RDF_SPARQL_ENDPOINT))
                .query(outerQueryString).addDefaultGraphURI(targetFramework).build()) {

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

        try (QueryExecution qexec = QueryExecutionHTTP.create().endpoint(getiConfigService().getString(
                DataModelConstants.CONFIG_PUBLICATIONS_RDF_SPARQL_ENDPOINT,
                DataModelConstants.Defaults.CONFIG_PUBLICATIONS_RDF_SPARQL_ENDPOINT))
                .query(queryString).addDefaultGraphURI(targetFramework).build()) {

            ResultSet rs = qexec.execSelect();

            rdfConcepts = toRDFConcept(targetFramework, rs);

        }

        return rdfConcepts;

    }

    public Set<RDFConcept> searchTreeConcepts(String targetFramework, String uri) {

        RDFConceptScheme controlledListCS = searchConceptScheme(targetFramework);

        if (controlledListCS == null) {
            return new HashSet<RDFConcept>(new ArrayList<>());
        }

        String skosPrefix = "prefix skos: <http://www.w3.org/2004/02/skos/core#>\n";
        String rdfPrefix = "prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n";
        String authPrefix = "prefix auth: <http://publications.europa.eu/ontology/authority/>\n";
        String dcPrefix = "prefix dc: <http://purl.org/dc/elements/1.1/>\n";

        String queryString = skosPrefix + rdfPrefix + authPrefix + dcPrefix +
                "SELECT  ?id (?id AS ?targetNamePrefix) (?node AS ?concept) ?targetName\n" +
                "WHERE\n" +
                "  {  \n" +
                "    values ?uri {<" + uri + ">} .\n" +
                "    ?node rdf:type skos:Concept ;\n" +
                "             dc:identifier ?id .\n" +
                "    OPTIONAL\n" +
                "      { ?node skos:prefLabel  ?targetName }\n" +
                "    OPTIONAL\n" +
                "      { ?node  auth:deprecated  ?deprecated }\n" +
                "    FILTER (\n" +
                "        coalesce(str(?deprecated), \"false\") = \"false\"\n" +
                "    )\n" +
                "    {\n" +
                "        select *\n" +
                "        WHERE { \n" +
                "           ?uri skos:narrower* ?node.\n" +
                "        }\n" +
                "    }\n" +
                "}\n" +
                "\n" +
                "ORDER BY ASC(?id)";

        Set<RDFConcept> rdfConcepts = null;

        try (QueryExecution qexec = QueryExecutionHTTP.create().endpoint(getiConfigService().getString(
                DataModelConstants.CONFIG_PUBLICATIONS_RDF_SPARQL_ENDPOINT,
                DataModelConstants.Defaults.CONFIG_PUBLICATIONS_RDF_SPARQL_ENDPOINT))
                .query(queryString).addDefaultGraphURI(targetFramework).build()) {

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

        try (QueryExecution qexec = QueryExecutionHTTP.create().endpoint(getiConfigService().getString(
                DataModelConstants.CONFIG_PUBLICATIONS_RDF_SPARQL_ENDPOINT,
                DataModelConstants.Defaults.CONFIG_PUBLICATIONS_RDF_SPARQL_ENDPOINT))
                .query(outerQueryString).addDefaultGraphURI(targetFramework).build()) {
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
        try (QueryExecution qexec = QueryExecutionHTTP.create().endpoint(getiConfigService().getString(
                DataModelConstants.CONFIG_PUBLICATIONS_RDF_SPARQL_ENDPOINT,
                DataModelConstants.Defaults.CONFIG_PUBLICATIONS_RDF_SPARQL_ENDPOINT))
                .query(queryString).addDefaultGraphURI(targetFramework).build()) {
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
        try (QueryExecution qexec = QueryExecutionHTTP.create().endpoint(getiConfigService().getString(
                DataModelConstants.CONFIG_PUBLICATIONS_RDF_SPARQL_ENDPOINT,
                DataModelConstants.Defaults.CONFIG_PUBLICATIONS_RDF_SPARQL_ENDPOINT))
                .query(outerQueryString).addDefaultGraphURI(targetFramework).build()) {
            ResultSet rs = qexec.execSelect();

            rdfConceptScheme = toRDFConceptScheme(targetFramework, rs);

            controlledListsConceptSchemes.put(targetFramework, rdfConceptScheme);
        }

        return rdfConceptScheme;

    }

    public Pair<String, URI> searchTypeAndSchemaForCredType(String credTypeURI) {
        ControlledListConcept controlledListConcept = ControlledListConcept.fromUrl(credTypeURI);
        Pair<String, URI> resultPair = null;
        switch (controlledListConcept) {
            case CREDENTIAL_TYPE_GENERIC:

                resultPair = new ImmutablePair<String, URI>(
                        "Generic",
                        URI.create(this.getiConfigService().getString(DataModelConstants.Properties.GENERIC, ""))
                );
                break;
            case CREDENTIAL_TYPE_CONVERTED:
                resultPair = new ImmutablePair<String, URI>(
                        "Converted",
                        URI.create(this.getiConfigService().getString(DataModelConstants.Properties.CONVERTED, ""))
                );
                break;
            case CREDENTIAL_TYPE_ACCREDITATION:
                resultPair = new ImmutablePair<String, URI>(
                        "Accredited",
                        URI.create(this.getiConfigService().getString(DataModelConstants.Properties.ACCREDITED, ""))
                );
                break;
            case CREDENTIAL_TYPE_DIPLOMA_SUPPLEMENT:
                resultPair = new ImmutablePair<String, URI>(
                        "DiplomaSupplement",
                        URI.create(this.getiConfigService().getString(DataModelConstants.Properties.DIPLOMA_SUPPLEMENT, ""))
                );
                break;
            case CREDENTIAL_TYPE_ISSUED_MANDATE:
                resultPair = new ImmutablePair<String, URI>(
                        "IssuedByMandate",
                        URI.create(this.getiConfigService().getString(DataModelConstants.Properties.ISSUED_MANDATE, ""))
                );
                break;
            default:
                resultPair = new ImmutablePair<String, URI>(
                        "Converted",
                        URI.create(this.getiConfigService().getString(DataModelConstants.Properties.GENERIC, ""))
                );
                break;
        }
        return resultPair;
    }

    public String searchLanguageISO639ByURI(String langURI) {

        String langISO639Code = controlledListsLanguages.get(langURI);

        if (langISO639Code != null) {
            return langISO639Code;
        }

        String skosPrefix = "prefix skos: <http://www.w3.org/2004/02/skos/core#>\n";
        String rdfPrefix = "prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n";
        String authPrefix = "prefix auth: <http://publications.europa.eu/ontology/authority/>\n";

        String outerQueryString = skosPrefix + rdfPrefix + authPrefix +
                "select (STR(?notation) as ?iso639)\n" +
                "WHERE{\n" +
                "?concept rdf:type ?type .\n" +
                "?concept skos:notation ?notation .\n" +
                "OPTIONAL { ?concept auth:deprecated ?deprecated } .\n" +
                "FILTER(\n" +
                " STR(?type) = \"http://www.w3.org/2004/02/skos/core#Concept\"\n" +
                " && STR(?concept) in ('" + langURI + "')\n" +
                " && STR(datatype(?notation)) = \"http://publications.europa.eu/ontology/euvoc#ISO_639_1\"\n" +
                "&& COALESCE(STR(?deprecated),\"false\") = \"false\"\n" +
                " )\n" +
                "}";

        try (QueryExecution qexec = QueryExecutionHTTP.create().endpoint(
                getiConfigService().getString(
                        DataModelConstants.CONFIG_PUBLICATIONS_RDF_SPARQL_ENDPOINT,
                        DataModelConstants.Defaults.CONFIG_PUBLICATIONS_RDF_SPARQL_ENDPOINT))
                .query(outerQueryString).addDefaultGraphURI(ControlledList.LANGUAGE.getUrl()).build()) {

            ResultSet rs = qexec.execSelect();

            if (rs.hasNext()) {
                QuerySolution rb = rs.nextSolution();
                langISO639Code = rb.getLiteral("iso639").getString();
                controlledListsLanguages.put(langURI, langISO639Code);
            } else {
                langISO639Code = null;
            }

        }

        return langISO639Code;

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
                    rdfConcept.setExternalResource(externalResource.getString());
                }

            }

        }

        if (rdfConcept.getTargetName() != null && rdfConcept.getTargetName().

                isEmpty()) {
            rdfConcept.setTargetName(null);
        }

        return conceptList;

    }

    public ConceptDTO toConceptDTO(RDFConcept rdfConcept) {
        ConceptDTO conceptDTO = new ConceptDTO();
        conceptDTO.setId(URI.create(rdfConcept.getUri()));
        conceptDTO.setPrefLabel(LiteralMap.fromMap(rdfConcept.getTargetName()));
        ConceptSchemeDTO conceptSchemeDTO = new ConceptSchemeDTO();
        conceptSchemeDTO.setId(URI.create(rdfConcept.getTargetFrameworkUri()));
        conceptDTO.setInScheme(conceptSchemeDTO);
        return conceptDTO;
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

    public BaseConfigService getiConfigService() {
        return iConfigService;
    }

    public void setiConfigService(BaseConfigService iConfigService) {
        this.iConfigService = iConfigService;
    }
}

