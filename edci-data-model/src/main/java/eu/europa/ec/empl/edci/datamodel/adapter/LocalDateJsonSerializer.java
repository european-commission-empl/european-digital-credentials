package eu.europa.ec.empl.edci.datamodel.adapter;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import eu.europa.ec.empl.edci.constants.DataModelConstants;
import org.joda.time.LocalDate;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class LocalDateJsonSerializer extends JsonSerializer<LocalDate> {

    @Override
    public void serialize(LocalDate value, JsonGenerator jgen, SerializerProvider provider) throws IOException {
        jgen.writeString(value.toString());
    }


}
