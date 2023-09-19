package eu.europa.ec.empl.edci.util;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import eu.europa.ec.empl.edci.datamodel.adapter.LocalDateJsonDeserializer;
import eu.europa.ec.empl.edci.datamodel.adapter.LocalDateJsonSerializer;
import eu.europa.ec.empl.edci.datamodel.adapter.ZonedDateJsonDeserializer;
import eu.europa.ec.empl.edci.datamodel.adapter.ZonedDateJsonSerializer;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.ZonedDateTime;

/**
 * Utility class for Json operations
 */
@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
public class JsonUtil {

    private ObjectMapper objectMapper = null;
    protected static final Logger logger = LogManager.getLogger(JsonUtil.class);

    /**
     * Sets mapper options and modules
     *
     * @return the ObjectMapper
     */
    private ObjectMapper getJacksonObjectMapper() {
        if (this.objectMapper == null) {
            ObjectMapper oM = new ObjectMapper();

            SimpleModule module = new SimpleModule();
            module.addDeserializer(ZonedDateTime.class, new ZonedDateJsonDeserializer());
            module.addDeserializer(LocalDate.class, new LocalDateJsonDeserializer());
            module.addSerializer(ZonedDateTime.class, new ZonedDateJsonSerializer());
            module.addSerializer(LocalDate.class, new LocalDateJsonSerializer());

            oM.registerModule(new JodaModule());
            oM.registerModule(module);
            oM.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            oM.configure(DeserializationFeature.FAIL_ON_MISSING_CREATOR_PROPERTIES, false);
            oM.configure(JsonParser.Feature.STRICT_DUPLICATE_DETECTION, true);
            oM.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
            oM.configure(SerializationFeature.USE_EQUALITY_FOR_OBJECT_ID, true);
            oM.setSerializationInclusion(JsonInclude.Include.NON_NULL);
            oM.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
            oM.enable(SerializationFeature.INDENT_OUTPUT);
            oM.setVisibility(oM.getSerializationConfig().getDefaultVisibilityChecker()
                    .withFieldVisibility(JsonAutoDetect.Visibility.ANY)
                    .withGetterVisibility(JsonAutoDetect.Visibility.NONE)
                    .withSetterVisibility(JsonAutoDetect.Visibility.NONE)
                    .withCreatorVisibility(JsonAutoDetect.Visibility.NONE)
            );
            this.setObjectMapper(oM);
        }

        return objectMapper;
    }

    /**
     * UnMarshall an InputStream into target class.<strong>Stream must be manually closed by calling method.</strong>
     *
     * @param jsonLdStream the InputStream
     * @param clazz        the target class
     * @param <T>          the target class
     * @return an instance of the target class
     * @throws IOException on error
     */
    public <T> T unMarshall(InputStream jsonLdStream, Class<T> clazz) throws IOException {
        String jsonLd = IOUtils.toString(jsonLdStream, StandardCharsets.UTF_8.name());
        return this.unMarshall(jsonLd, clazz);
    }

    /**
     * UnMarshall a String into target class
     *
     * @param jsonLd the InputStream
     * @param clazz  the target class
     * @param <T>    the target class
     * @return an instance of the target class
     * @throws IOException on error
     */
    public <T> T unMarshall(String jsonLd, Class<T> clazz) throws IOException {
        return this.getJacksonObjectMapper().readValue(jsonLd, clazz);
    }

    /**
     * UnMarshall a byte array into target class
     *
     * @param jsonLd the byte array
     * @param clazz  the target class
     * @param <T>    the target class
     * @return an instance of the target class
     * @throws IOException on error
     */

    public <T> T unMarshall(byte[] jsonLd, Class<T> clazz) throws IOException {
        return this.getJacksonObjectMapper().readValue(jsonLd, clazz);
    }

    /**
     * Marshall an Object into an InputStream. <strong>Stream must be manually closed by calling method.</strong>
     *
     * @param object the Object to be marshalled
     * @return the resulting InputStream
     * @throws JsonProcessingException on error
     */
    public InputStream marshallAsStream(Object object) throws JsonProcessingException {
        return new ByteArrayInputStream(marshallAsBytes(object));
    }

    /**
     * Marshall an Object into a byte array
     *
     * @param object the Object to be marshalled
     * @return the byte array
     * @throws JsonProcessingException on error
     */
    public byte[] marshallAsBytes(Object object) throws JsonProcessingException {
        return this.getJacksonObjectMapper().writeValueAsBytes(object);
    }

    /**
     * Marshall an Object into a String
     *
     * @param object the Object to be marshalled
     * @return the string
     * @throws JsonProcessingException on error
     */
    public String marshallAsString(Object object) throws JsonProcessingException {
        return this.getJacksonObjectMapper().writeValueAsString(object);
    }

    public ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    public void setObjectMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }


}
