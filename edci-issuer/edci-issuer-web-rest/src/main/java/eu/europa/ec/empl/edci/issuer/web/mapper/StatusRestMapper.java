package eu.europa.ec.empl.edci.issuer.web.mapper;

import eu.europa.ec.empl.edci.issuer.common.model.StatusDTO;
import eu.europa.ec.empl.edci.issuer.web.model.StatusView;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface StatusRestMapper {

    public StatusDTO toDTO(StatusView statusView);

    public StatusView toVO(StatusDTO statusDTO);

}
