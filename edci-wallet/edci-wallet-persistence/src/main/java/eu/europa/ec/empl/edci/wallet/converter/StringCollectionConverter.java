package eu.europa.ec.empl.edci.wallet.converter;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Converter(autoApply = true)
public class StringCollectionConverter implements AttributeConverter<List<String>, String> {

    private static final String SPLIT_CHAR = "|";
    private static final String ENCODING = "UTF-8";

    @Override
    public String convertToDatabaseColumn(List<String> stringList) {
        if (stringList == null) {
            return null;
        }
        return stringList.stream().collect(Collectors.joining(SPLIT_CHAR));
    }

    @Override
    public List<String> convertToEntityAttribute(String string) {
        if (string == null) {
            return null;
        }
        return Arrays.asList(string.split(Pattern.quote(SPLIT_CHAR)));

    }
}