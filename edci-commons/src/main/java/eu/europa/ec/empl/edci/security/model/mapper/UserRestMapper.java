package eu.europa.ec.empl.edci.security.model.mapper;

import eu.europa.ec.empl.edci.security.model.dto.UserDetailsDTO;
import eu.europa.ec.empl.edci.security.model.view.UserDetailsView;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserRestMapper {

    public UserDetailsView toVO(UserDetailsDTO userInfoDTO);
}
