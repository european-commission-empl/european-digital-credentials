package eu.europa.ec.empl.edci.datamodel.jsonld.model.util;

import com.fasterxml.jackson.core.JsonLocation;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.deser.std.DelegatingDeserializer;
import eu.europa.ec.empl.edci.datamodel.jsonld.model.base.JsonLdContextHolder;
import eu.europa.ec.empl.edci.exception.EDCIException;
import jakarta.json.Json;
import jakarta.json.JsonReader;
import jakarta.json.JsonValue;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JsonLdDeserializer extends DelegatingDeserializer {
    private final BeanDescription beanDescription;

    public JsonLdDeserializer(JsonDeserializer<?> delegate, BeanDescription beanDescription) {
        super(delegate);
        this.beanDescription = beanDescription;
    }

    @Override
    protected JsonDeserializer<?> newDelegatingInstance(JsonDeserializer<?> jsonDeserializer) {
        return new JsonLdDeserializer(jsonDeserializer, beanDescription);
    }

    @Override
    public Object deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
        Object object = null;
        if (JsonLdContextHolder.class.isAssignableFrom(beanDescription.getBeanClass())) {
            object = super.deserialize(jp, ctxt);
            String jsonString = getOriginalJsonFromSourceRef(jp);
            JsonLdContextHolder jsonLdContextHolder = (JsonLdContextHolder) object;
            jsonLdContextHolder.setJsonLdContext(this.getJsonContext(jsonString));
        } else {
            object = super.deserialize(jp, ctxt);
        }
        return object;
    }

    public String getOriginalJsonFromSourceRef(JsonParser jp) throws IOException {
        JsonLocation endLocation = jp.getCurrentLocation();
        Object sourceRef = endLocation.getSourceRef();
        String jsonSource = null;
        if (String.class.isAssignableFrom(sourceRef.getClass())) {
            jsonSource = sourceRef.toString();
        } else if (StringReader.class.isAssignableFrom(sourceRef.getClass())) {
            jsonSource = IOUtils.toString((StringReader) sourceRef);
        } else if (byte[].class.isAssignableFrom(sourceRef.getClass())) {
            jsonSource = new String((byte[]) sourceRef, StandardCharsets.UTF_8);
        } else {
            throw new EDCIException().addDescription("JSON-LD unmarshalling is only supported when using Strings, StringReaders or byte arrays");
        }
        return jsonSource;
    }

    public JsonValue getJsonContext(String originalJson) throws IOException {
        Pattern pattern = Pattern.compile("\"@context\":([\\[\"{].*[\\]\"}])");
        Matcher matcher = pattern.matcher(originalJson);
        if (matcher.find()) {
            String contextValue = matcher.group(1);
            try (StringReader contextReader = new StringReader(contextValue)) {
                try (JsonReader jsonReader = Json.createReader(contextReader)) {
                    return jsonReader.readValue();
                }
            }
        } else {
            return null;
        }
    }
}
