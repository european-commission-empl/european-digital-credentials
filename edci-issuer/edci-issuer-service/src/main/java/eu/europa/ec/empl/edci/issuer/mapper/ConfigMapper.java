package eu.europa.ec.empl.edci.issuer.mapper;

import eu.europa.ec.empl.edci.issuer.common.model.ConfigDTO;
import eu.europa.ec.empl.edci.issuer.entity.config.ConfigDAO;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ConfigMapper {

    ConfigDTO toDTO(ConfigDAO configDAO);

    ConfigDAO toDAO(ConfigDTO configDTO);

    List<ConfigDTO> toDTO(List<ConfigDAO> configDAOS);

    List<ConfigDAO> toDAO(List<ConfigDTO> configDTOS);
}
