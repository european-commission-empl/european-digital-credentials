package eu.europa.ec.empl.edci.util;

import com.apicatalog.jsonld.JsonLd;
import com.apicatalog.jsonld.JsonLdEmbed;
import com.apicatalog.jsonld.JsonLdError;
import com.apicatalog.jsonld.JsonLdOptions;
import com.apicatalog.jsonld.context.cache.LruCache;
import com.apicatalog.jsonld.document.Document;
import com.apicatalog.jsonld.document.JsonDocument;
import com.apicatalog.jsonld.loader.DocumentLoader;
import com.apicatalog.rdf.RdfDataset;
import com.fasterxml.jackson.core.JsonProcessingException;
import eu.europa.ec.empl.edci.config.service.ProxyConfigService;
import eu.europa.ec.empl.edci.constants.ErrorCode;
import eu.europa.ec.empl.edci.exception.EDCIException;
import eu.europa.ec.empl.edci.jsonld.titanium.EDCIDocumentLoader;
import jakarta.json.Json;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObject;
import jakarta.json.JsonStructure;
import org.apache.jena.graph.Graph;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.*;
import org.apache.jena.riot.system.JenaTitanium;
import org.apache.jena.shacl.ShaclValidator;
import org.apache.jena.shacl.Shapes;
import org.apache.jena.shacl.ValidationReport;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;

/**
 * Utility class for JsonLD operations.
 * Uses custom EDCIDocumentLoader with proxy configuration and workaround for downloading context files.
 * Uses JsonUtil(and options) for plain JSON operations
 *
 * @see EDCIDocumentLoader
 * @see ProxyConfigService
 * @see JsonUtil
 */
@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
public class JsonLdUtil {

    @Autowired
    private ProxyConfigService configService;
    @Autowired
    private JsonUtil jsonUtil;
    protected static final Logger logger = LogManager.getLogger(JsonLdUtil.class);
    private DocumentLoader documentLoader = null;
    private LruCache documentCache = new LruCache(5);
    public static final String JENA_OFFICIAL_CONTENT_TYPE_TTL = "text/turtle";
    public static final String JENA_OFFICIAL_CONTENT_TYPE_RDF_XML = "application/rdf+xml";

    /**
     * Configure Jena to be able to parse as TTL by default when no content-negotiation is present on the server.
     */
    @PostConstruct
    public void postConstruct() {
        ReaderRIOTFactory oldFactory = RDFParserRegistry.getFactory(RDFLanguages.TURTLE);
        Lang defaultTurtle;
        if (configService != null && !JENA_OFFICIAL_CONTENT_TYPE_RDF_XML.equalsIgnoreCase(
                configService.getString("jena.default.triples.content.type", JENA_OFFICIAL_CONTENT_TYPE_RDF_XML))) {
            defaultTurtle = LangBuilder.create("Turtle", JENA_OFFICIAL_CONTENT_TYPE_TTL)
                    .addAltNames("TTL")
                    .addAltContentTypes("application/turtle", "")
                    .addFileExtensions("ttl", "").build();

            RDFLanguages.register(defaultTurtle);
            RDFParserRegistry.registerLangTriples(defaultTurtle, oldFactory);
        }
    }

    /**
     * Frames a JSON-LD string based using a frame that provides the context that defines how this process should be done.
     *
     * @param jsonString the JSON-LD string
     * @param frame      the frame string
     * @return the framed JSON-LD string
     * @throws JsonLdError on error
     * @see <a href="https://www.w3.org/TR/json-ld11-framing/">W3C Framing features</a>
     */
    public String doFrame(String jsonString, String frame) throws JsonLdError {
        JsonDocument jsonDocument;
        JsonDocument frameDocument;

        try (StringReader stringReader = new StringReader(jsonString)) {
            jsonDocument = JsonDocument.of(stringReader);
        } catch (JsonLdError e) {
            logger.error("Could not load JSON-LD Document while framing", e);
            throw new EDCIException(ErrorCode.JSONLD_INVALID_JSON_INPUT).addDescription("Could not load JSON-LD Document while framing");
        }

        try (StringReader stringReader = new StringReader(frame)) {
            frameDocument = JsonDocument.of(stringReader);
        } catch (JsonLdError e) {
            logger.error("Could not load Frame Document while framing", e);
            throw new EDCIException(ErrorCode.JSONLD_INVALID_JSON_INPUT).addDescription("Could not load Frame Document while framing");
        }
        Instant start = Instant.now();
        JsonObject jsonObject = JsonLd.frame(jsonDocument, frameDocument).options(this.getJsonLdOptions())
                .get();
        Instant end = Instant.now();
        logger.debug(String.format("framing took %d seconds", Duration.between(start, end).toSeconds()));
        return jsonObject != null ? jsonObject.toString() : null;
    }

    /**
     * Marshall an object to a compact JSON-LD String, a context is provided in order to define how the marshall should be done.
     *
     * @param object  the Object to be marshall
     * @param context the context to be used for marshalling
     * @return the JSON-LD compact String
     * @throws JsonProcessingException on JSON marshalling error
     * @throws JsonLdError             on JSON-LD compaction error
     */
    public String marshallToCompactString(Object object, URI... context) throws JsonProcessingException, JsonLdError {
        String jsonString = this.getJsonUtil().marshallAsString(object);
        JsonDocument jsonDocument;
        try (StringReader stringReader = new StringReader(jsonString)) {
            jsonDocument = JsonDocument.of(stringReader);
        } catch (JsonLdError e) {
            logger.error("error loading JSON-LD document for compacting", e);
            throw new EDCIException(ErrorCode.JSONLD_INVALID_JSON_INPUT).addDescription("error loading JSON-LD document for compacting");
        }
        JsonDocument contextDocument = this.getContextDocument(context);
        JsonObject jsonObject = JsonLd.compact(jsonDocument, contextDocument).options(this.getJsonLdOptions(context)).get();
        return jsonObject != null ? jsonObject.toString() : null;
    }

    /**
     * Validate an RDF in JSON-LD format against a SHACL Shape.
     * For more information about SHACL @see <a href="https://www.w3.org/TR/shacl/">W3C Shacl definition</a>
     *
     * @param jsonLd the JSON-LD rdf to ve validated
     * @param shape  the SHACL file URL
     * @return a Jena ValidationReport
     * @throws IOException on error
     */
    public ValidationReport validateRDF(byte[] jsonLd, String shape) throws IOException {
        return validateRDF(new String(jsonLd, StandardCharsets.UTF_8), shape);
    }

    /**
     * Validate an RDF in JSON-LD format against a SHACL Shape
     * For more information about SHACL @see <a href="https://www.w3.org/TR/shacl/">W3C Shacl definition</a>
     *
     * @param jsonLd the JSON-LD rdf to ve validated
     * @param shape  the SHACL file content
     * @return a Jena ValidationReport
     * @throws IOException on error
     */
    public ValidationReport validateRDF(byte[] jsonLd, byte[] shape) throws IOException {
        return validateRDF(new String(jsonLd, StandardCharsets.UTF_8), shape);
    }

    /**
     * Validate JSON-LD RDF using SHACL external shapes file in TTL format
     * For more information about SHACL @see <a href="https://www.w3.org/TR/shacl/">W3C Shacl definition</a>
     *
     * @param jsonLd the jsonLD to be validated
     * @param shape  the SHACL file URL
     * @return a Jena ValidationReport
     * @throws IOException on parsing error
     */
    public ValidationReport validateRDF(String jsonLd, String shape) throws IOException {

        try (StringReader sr = new StringReader(jsonLd)) {
            Document document = JsonDocument.of(sr);
            // Use titanium to read RDF to include options with cache, then convert
            RdfDataset dataset = JsonLd.toRdf(document).options(this.getJsonLdOptions()).get();
            Graph dataGraph = JenaTitanium.convert(dataset).getDefaultGraph();
            OntModel m = ModelFactory.createOntologyModel();
            Model shapesModel = m.read(shape);
            return validateRDF(dataGraph, shapesModel);
        } catch (JsonLdError e) {
            throw new EDCIException().setCause(e);
        }
    }

    /**
     * Validate JSON-LD RDF using internal SHACL file
     * For more information about SHACL @see <a href="https://www.w3.org/TR/shacl/">W3C Shacl definition</a>
     *
     * @param jsonLd the RDF JSON-LD String
     * @param shape  the shacl file content
     * @return a Jena ValidationReport
     */
    public ValidationReport validateRDF(String jsonLd, byte[] shape) throws IOException {
        try (ByteArrayInputStream shapeIs = new ByteArrayInputStream(shape)) {
            Graph dataGraph = RDFParser.fromString(jsonLd).forceLang(Lang.JSONLD11).build().toGraph();
            String baseUrl = "http://data.europa.eu/snb/model/edc-constraints-mdr-shouldHaveNoImpact";
            OntModel m = ModelFactory.createOntologyModel();
            Model shapesModel = m.read(shapeIs, baseUrl);
            return validateRDF(dataGraph, shapesModel);
        }
    }

    /**
     * Validates RDF Graph against a SHACL shape Model using Jena
     * For more information about SHACL @see <a href="https://www.w3.org/TR/shacl/">W3C Shacl definition</a>
     *
     * @param dataGraph   the Jena dataGraph to be validated
     * @param shapesModel the Jena Model for the SHACL shape
     * @return a Jena ValidationReport
     */
    private ValidationReport validateRDF(Graph dataGraph, Model shapesModel) {
        dataGraph.getPrefixMapping().setNsPrefixes(shapesModel.clearNsPrefixMap());
        Shapes shapes = Shapes.parse(shapesModel);
        return ShaclValidator.get().validate(shapes, dataGraph);
    }

    /**
     * Creates a JsonDocument with multiple uri values to use as a Context when multiple external references are required
     *
     * @param context the contexts to be used
     * @return the context json document
     */
    private JsonDocument getContextDocument(URI... context) {
        JsonStructure jsonStructure;
        if (context.length > 1) {
            JsonArrayBuilder jsonArrayBuilder = Json.createArrayBuilder();
            Arrays.stream(context).forEach(uri -> jsonArrayBuilder.add(uri.toString()));
            jsonStructure = Json.createObjectBuilder().add("@context", jsonArrayBuilder).build();
        } else {
            jsonStructure = Json.createObjectBuilder().add("@context", context[0].toString()).build();
        }
        return JsonDocument.of(jsonStructure);
    }

    /**
     * Creates a custom EDCIDocumentLoader with proxy options and file downloading workaround.
     * Creates single instance and reuses it if created.
     *
     * @return the DocumentLoader
     */
    private DocumentLoader getDocumentLoader() {
        if (this.documentLoader == null) {
            this.documentLoader = new EDCIDocumentLoader(this.getConfigService().getDefaultHttpClient(), this.getConfigService().getDefaultHttpsClient());
        }
        return this.documentLoader;
    }

    /**
     * get JsonLDOptions with cache and custom DocumentLoader.
     * Explicit is set to false.
     * CompactArrays is set to true
     * CompactToRelative set to true
     * Ordered set to true
     *
     * @return the JsonLdOptions object
     * @see <a href="https://www.w3.org/TR/json-ld11-api/#the-jsonldoptions-type">JsonLDOptions doc</a>
     */
    private JsonLdOptions getJsonLdOptions() {
        JsonLdOptions options = new JsonLdOptions();
        options.setCompactToRelative(true);
        options.setOrdered(true);
        options.setExplicit(false);
        options.setDocumentLoader(this.getDocumentLoader());
        options.setCompactArrays(true);
        options.setEmbed(JsonLdEmbed.NEVER);
        options.setDocumentCache(this.getDocumentCache());
        return options;
    }

    /**
     * get JsonLdOptions with expand contexts
     *
     * @param context the contexts to be included in the options
     * @return the JsonLdOptions
     */
    private JsonLdOptions getJsonLdOptions(URI... context) {
        JsonLdOptions options = this.getJsonLdOptions();
        options.setExpandContext(this.getContextDocument(context));
        return options;
    }

    public JsonUtil getJsonUtil() {
        return jsonUtil;
    }

    public void setJsonUtil(JsonUtil jsonUtil) {
        this.jsonUtil = jsonUtil;
    }

    public ProxyConfigService getConfigService() {
        return configService;
    }

    public void setConfigService(ProxyConfigService configService) {
        this.configService = configService;
    }

    public LruCache getDocumentCache() {
        return documentCache;
    }

    public void setDocumentCache(LruCache documentCache) {
        this.documentCache = documentCache;
    }
}
