package eu.europa.ec.empl.edci.issuer.web.mapper;

import eu.europa.ec.empl.edci.issuer.common.model.ConfigDTO;
import eu.europa.ec.empl.edci.issuer.web.model.ConfigView;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ConfigRestMapper {

    ConfigDTO toDTO(ConfigView configView);

    ConfigView toView(ConfigDTO configDTO);

    List<ConfigDTO> toDTO(List<ConfigView> configViews);

    List<ConfigView> toView(List<ConfigDTO> configDTOS);
}
