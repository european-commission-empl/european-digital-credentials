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
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.deser.BeanDeserializerModifier;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import eu.europa.ec.empl.edci.config.service.ProxyConfigService;
import eu.europa.ec.empl.edci.constants.DataModelConstants;
import eu.europa.ec.empl.edci.constants.ErrorCode;
import eu.europa.ec.empl.edci.datamodel.adapter.LocalDateJsonDeserializer;
import eu.europa.ec.empl.edci.datamodel.adapter.LocalDateJsonSerializer;
import eu.europa.ec.empl.edci.datamodel.adapter.ZonedDateJsonDeserializer;
import eu.europa.ec.empl.edci.datamodel.adapter.ZonedDateJsonSerializer;
import eu.europa.ec.empl.edci.datamodel.jsonld.model.util.JsonLdDeserializer;
import eu.europa.ec.empl.edci.exception.EDCIException;
import eu.europa.ec.empl.edci.jsonld.titanium.EDCIDocumentLoader;
import jakarta.json.Json;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObject;
import jakarta.json.JsonStructure;
import org.apache.commons.io.IOUtils;
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
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.*;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.ZonedDateTime;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
public class JsonLdUtil {

    @Autowired
    private ProxyConfigService configService;

    public static final String JSONLD_CONTEXT_STRING = "@context";
    public static final String STANDARD_FRAME_PATH = "jsonld/frame.jsonld";
    public static final String JENA_OFFICIAL_CONTENT_TYPE_TTL = "text/turtle";
    public static final String JENA_OFFICIAL_CONTENT_TYPE_RDF_XML = "application/rdf+xml";
    public static final String DEFAULT_GENERIC_CONSTRAINTS_FILE_PATH = "http://dev.everisdx.io/datamodel/shacl/EDC-generic-int-loops.ttl";
    protected static final Logger logger = LogManager.getLogger(JsonLdUtil.class);
    private DocumentLoader documentLoader = null;
    private LruCache documentCache = new LruCache(5);

    @PostConstruct
    public void postConstruct() {
        ReaderRIOTFactory oldFactory = RDFParserRegistry.getFactory(RDFLanguages.TURTLE);
        Lang defaultTurtle = null;

        if (configService != null && !JENA_OFFICIAL_CONTENT_TYPE_RDF_XML.equalsIgnoreCase(
                configService.getString("jena.default.triples.content.type", JENA_OFFICIAL_CONTENT_TYPE_RDF_XML))) {

            defaultTurtle = LangBuilder.create("Turtle", JENA_OFFICIAL_CONTENT_TYPE_TTL)
                    .addAltNames(new String[]{"TTL"})
                    .addAltContentTypes(new String[]{"application/turtle", ""})
                    .addFileExtensions(new String[]{"ttl", ""}).build();

            RDFLanguages.register(defaultTurtle);
            RDFParserRegistry.registerLangTriples(defaultTurtle, oldFactory);

        }
    }

    //Will not be used by the moment, to avoid unclosed streams
    private InputStream marshalStream(Object object) throws JsonProcessingException, JsonLdError {
        return new ByteArrayInputStream(marshallAsBytes(object));
    }

    public byte[] marshallAsBytes(Object object) throws JsonProcessingException, JsonLdError {
        return JsonLdUtil.getJacksonObjectMapper().writeValueAsBytes(object);
    }

    public String marshallAsString(Object object) throws JsonProcessingException {
        return JsonLdUtil.getJacksonObjectMapper().writeValueAsString(object);
    }

    public String doFrame(String jsonString, String frame, URI... context) throws JsonLdError {
        JsonDocument jsonDocument = null;
        JsonDocument frameDocument = null;

        try (StringReader stringReader = new StringReader(jsonString)) {
            jsonDocument = JsonDocument.of(stringReader);
        } catch (JsonLdError e) {
            logger.error("Could not load JSON Document", e);
            throw new EDCIException(ErrorCode.JSONLD_INVALID_JSON_INPUT).addDescription("Could not load JSON Document");
        }

        try (StringReader stringReader = new StringReader(frame)) {
            frameDocument = JsonDocument.of(stringReader);
        } catch (JsonLdError e) {
            logger.error("Could not load JSON Document", e);
            throw new EDCIException(ErrorCode.JSONLD_INVALID_JSON_INPUT).addDescription("Could not load JSON Document");
        }

        JsonObject jsonObject = JsonLd.frame(jsonDocument, frameDocument).get();

        return jsonObject != null ? jsonObject.toString() : null;
    }

    public String marshallToCompactString(Object object, URI... context) throws JsonProcessingException, JsonLdError {
        String jsonString = this.getJacksonObjectMapper().writeValueAsString(object);
        JsonDocument jsonDocument = null;
        try (StringReader stringReader = new StringReader(jsonString)) {
            jsonDocument = JsonDocument.of(stringReader);
        } catch (JsonLdError e) {
            logger.error("Could not load JSON Document", e);
            throw new EDCIException(ErrorCode.JSONLD_INVALID_JSON_INPUT).addDescription("Could not load JSON Document");
        }

        JsonDocument contextDocument = this.getContextDocument(context);

        JsonObject jsonObject = JsonLd.compact(jsonDocument, contextDocument).options(this.getJsonLdOptions(context)).get();

        return jsonObject != null ? jsonObject.toString() : null;
    }


    public String marshallToFlattenedString(Object object, URI... context) throws JsonProcessingException, JsonLdError {
        String jsonString = JsonLdUtil.getJacksonObjectMapper().writeValueAsString(object);
        JsonDocument jsonDocument = null;
        try (StringReader stringReader = new StringReader(jsonString)) {
            jsonDocument = JsonDocument.of(stringReader);
        } catch (JsonLdError e) {
            logger.error("Could not load JSON Document", e);
            throw new EDCIException(ErrorCode.JSONLD_INVALID_JSON_INPUT).addDescription("Could not load JSON Document");
        }

        JsonDocument contextDocument = this.getContextDocument(context);
        JsonStructure jsonStructure = JsonLd.flatten(jsonDocument).context(contextDocument).options(this.getJsonLdOptions(context)).get();

        return jsonStructure != null ? jsonStructure.toString() : null;
    }

    public JsonDocument getContextDocument(URI... context) {
        JsonStructure jsonStructure;
        if (context.length > 1) {
            JsonArrayBuilder jsonArrayBuilder = Json.createArrayBuilder();
            for (int i = 0; i < context.length; i++) {
                jsonArrayBuilder.add(context[i].toString());
            }
            jsonStructure = Json.createObjectBuilder().add("@context", jsonArrayBuilder).build();
        } else {
            jsonStructure = Json.createObjectBuilder().add("@context", context[0].toString()).build();
        }
        return JsonDocument.of(jsonStructure);
    }

    public <T> T unMarshall(String jsonLd, Class<T> clazz) throws IOException {
        return JsonLdUtil.getJacksonObjectMapper().readValue(jsonLd, clazz);
    }

    public <T> T unMarshall(byte[] jsonLd, Class<T> clazz) throws IOException {
        return JsonLdUtil.getJacksonObjectMapper().readValue(jsonLd, clazz);
    }

    public <T> T unMarshall(InputStream jsonLdStream, Class<T> clazz) throws IOException {
        String jsonLd = IOUtils.toString(jsonLdStream, StandardCharsets.UTF_8.name());
        return this.unMarshall(jsonLd, clazz);
    }

    public ValidationReport validateRDF(byte[] jsonLd, String shape) throws IOException {
        return validateRDF(new String(jsonLd, StandardCharsets.UTF_8), shape);
    }

    public ValidationReport validateRDF(byte[] jsonLd, byte[] shape) throws IOException {
        return validateRDF(new String(jsonLd, StandardCharsets.UTF_8), shape);
    }

    /**
     * Validate JSON-LD RDF using external shapes file in TTL format
     *
     * @param jsonLd the jsonLD to be validated
     * @param shape  the external TTL To be used
     * @return a Jena ValidationReport
     * @throws IOException
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
        } catch (Exception e) {
            throw new EDCIException().setCause(e);
        }
    }

    /**
     * Validate JSON-LD RDF using internal TTL file
     *
     * @param jsonLd
     * @param shape
     * @return
     * @throws FileNotFoundException
     */
    public ValidationReport validateRDF(String jsonLd, byte[] shape) throws FileNotFoundException {
        try (ByteArrayInputStream shapeI = new ByteArrayInputStream(shape)) {
            Graph dataGraph = RDFParser.fromString(jsonLd).forceLang(Lang.JSONLD11).build().toGraph();
            String baseUrl = "http://data.europa.eu/snb/model/edc-constraints-mdr-shouldHaveNoImpact";
            OntModel m = ModelFactory.createOntologyModel();
            Model shapesModel = m.read(new ByteArrayInputStream(shape), baseUrl);
            return validateRDF(dataGraph, shapesModel);
        } catch (IOException e) {
            logger.error("Something went wrong");
        }

        return null;
    }

    private ValidationReport validateRDF(Graph dataGraph, Model shapesModel) throws IOException {
        dataGraph.getPrefixMapping().setNsPrefixes(shapesModel.clearNsPrefixMap());
        //TODO: TESTING PURPOSE, REMOVE IN FINAL VERSION
        //shapesModel.write(System.out);

        Shapes shapes = Shapes.parse(shapesModel);
        return ShaclValidator.get().validate(shapes, dataGraph);

    }

    private DocumentLoader getDocumentLoader() {
        if (this.documentLoader == null) {
            this.documentLoader = new EDCIDocumentLoader(this.getConfigService().getDefaultHttpClient(), this.getConfigService().getDefaultHttpsClient());
        }
        return this.documentLoader;
    }

    private JsonLdOptions getJsonLdOptions() {
        JsonLdOptions options = new JsonLdOptions();
        options.setCompactToRelative(true);
        options.setOrdered(true);
        options.setExplicit(false);
        options.setDocumentLoader(this.getDocumentLoader());
        options.setCompactArrays(true);
        options.setEmbed(JsonLdEmbed.NEVER);
        options.setDocumentCache(documentCache);
        return options;
    }

    private JsonLdOptions getJsonLdOptions(URI... context) {
        JsonLdOptions options = this.getJsonLdOptions();
        options.setExpandContext(this.getContextDocument(context));
        return options;
    }

    public static ObjectMapper getJacksonObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addDeserializer(ZonedDateTime.class, new ZonedDateJsonDeserializer());
        module.addDeserializer(LocalDate.class, new LocalDateJsonDeserializer());
        module.addSerializer(ZonedDateTime.class, new ZonedDateJsonSerializer());
        module.addSerializer(LocalDate.class, new LocalDateJsonSerializer());

        objectMapper.registerModule(new JodaModule());
        objectMapper.registerModule(module);
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.configure(DeserializationFeature.FAIL_ON_MISSING_CREATOR_PROPERTIES, false);
        objectMapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
        objectMapper.configure(SerializationFeature.USE_EQUALITY_FOR_OBJECT_ID, true);

        addPostDeserializeSupport(objectMapper);
        objectMapper.setVisibility(objectMapper.getSerializationConfig().getDefaultVisibilityChecker()
                .withFieldVisibility(JsonAutoDetect.Visibility.ANY)
                .withGetterVisibility(JsonAutoDetect.Visibility.NONE)
                .withSetterVisibility(JsonAutoDetect.Visibility.NONE)
                .withCreatorVisibility(JsonAutoDetect.Visibility.NONE)
        );
        return objectMapper;
    }

    public static void addPostDeserializeSupport(ObjectMapper objectMapper) {
        SimpleModule module = new SimpleModule();
        module.setDeserializerModifier(new BeanDeserializerModifier() {
            @Override
            public JsonDeserializer<?> modifyDeserializer(DeserializationConfig config, BeanDescription beanDescription,
                                                          JsonDeserializer<?> originalDeserializer) {
                return new JsonLdDeserializer(originalDeserializer, beanDescription);
            }
        });
        objectMapper.registerModule(module);
    }

    public ProxyConfigService getConfigService() {
        return configService;
    }

    public void setConfigService(ProxyConfigService configService) {
        this.configService = configService;
    }

    public URI[] getMandatoryContext() {
        URI[] CREDENTIAL_MANDATORY_CONTEXTS = {
                URI.create("https://www.w3.org/2018/credentials/v1"),
                URI.create(this.getConfigService().getString(DataModelConstants.Properties.JSON_CONTEXT, ""))
        };

        return CREDENTIAL_MANDATORY_CONTEXTS;
    }
}
