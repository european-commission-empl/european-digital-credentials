package eu.europa.ec.empl.edci.mapper.commons;

import org.mapstruct.Mapper;
import org.springframework.util.StringUtils;

import java.util.Base64;

@Mapper(componentModel = "spring")
public interface StringBytesMapping {

    default String convertToDatabaseColumn(byte[] entityValue) {
        return (entityValue == null) ? null : Base64.getEncoder().encodeToString(entityValue);
    }

    default byte[] convertToEntityAttribute(String databaseValue) {
        return (StringUtils.hasLength(databaseValue) ? Base64.getDecoder().decode(databaseValue) : null);
    }
}