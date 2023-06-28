package eu.europa.ec.empl.edci.datamodel.adapter;

import com.google.gson.*;
import org.joda.time.Period;

import java.lang.reflect.Type;

public class DurationJsonAdapter implements JsonDeserializer<Period>, JsonSerializer<Period> {

    @Override
    public JsonElement serialize(Period src, Type typeOfSrc, JsonSerializationContext context) {
        /*JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("period", src.toString());*/
        return new JsonPrimitive(src.toString());
    }

    @Override
    public Period deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        return Period.parse(json.getAsString());
    }
}
