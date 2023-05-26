package eu.europa.ec.empl.edci.issuer.converter;

import org.springframework.util.StringUtils;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.net.URI;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Converter(autoApply = true)
public class UriListConverter implements AttributeConverter<List<URI>, String> {

    private static final String SPLIT_CHAR = ";";
    private static final String ENCODING = "UTF-8";

    @Override
    public String convertToDatabaseColumn(List<URI> stringList) {
        return stringList.stream().map(str -> {
            String returnValue = null;
            try {
                returnValue = URLEncoder.encode(toString(str), ENCODING);
            } catch (Exception e) {
                returnValue = "ERROR";
            }
            return returnValue;
        }).collect(Collectors.joining(SPLIT_CHAR));
    }

    @Override
    public List<URI> convertToEntityAttribute(String string) {
        return Arrays.asList(string.split(SPLIT_CHAR)).stream().map(str -> {
            URI returnValue = null;
            try {
                returnValue = toURI(URLDecoder.decode(str, ENCODING));
            } catch (Exception e) {
                returnValue = null;
            }
            return returnValue;
        }).collect(Collectors.toList());

    }

    public String toString(URI entityValue) {
        return (entityValue == null) ? null : entityValue.toString();
    }

    public URI toURI(String databaseValue) {
        return (StringUtils.hasLength(databaseValue) ? URI.create(databaseValue.trim()) : null);
    }
}