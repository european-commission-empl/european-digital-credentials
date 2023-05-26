package eu.europa.ec.empl.edci.mapper.commons;

import org.mapstruct.Mapper;
import org.springframework.util.StringUtils;

import java.net.URI;

@Mapper(componentModel = "spring")
public interface StringUriMapping {

    default String convertToDatabaseColumn(URI entityValue) {
        return (entityValue == null) ? null : entityValue.toString();
    }

    default URI convertToEntityAttribute(String databaseValue) {
        return (StringUtils.hasLength(databaseValue) ? URI.create(databaseValue.trim()) : null);
    }
}