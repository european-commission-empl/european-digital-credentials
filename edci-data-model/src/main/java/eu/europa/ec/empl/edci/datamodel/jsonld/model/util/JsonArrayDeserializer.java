package eu.europa.ec.empl.edci.datamodel.jsonld.model.util;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import jakarta.json.JsonArray;

import java.io.IOException;

public class JsonArrayDeserializer extends StdDeserializer<JsonArray> {

    public JsonArrayDeserializer() {
        this(null);
    }

    public JsonArrayDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public JsonArray deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
        JsonNode node = jsonParser.getCodec().readTree(jsonParser);
        return null;
    }
}
