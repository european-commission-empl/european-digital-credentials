package eu.europa.ec.empl.edci.issuer.converter;

import org.joda.time.Period;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter(autoApply = true)
public class DurationConverter implements AttributeConverter<Period, String> {

    @Override
    public String convertToDatabaseColumn(Period attribute) {
        return attribute != null ? attribute.toString() : null;
    }

    @Override
    public Period convertToEntityAttribute(String duration) {
        return duration != null ? Period.parse(duration) : null;
    }

}