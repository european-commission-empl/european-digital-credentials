package eu.europa.ec.empl.edci.security.model.mapper;

import eu.europa.ec.empl.edci.security.model.dto.UserDetailsDTO;
import org.mapstruct.Mapper;
import org.mitre.openid.connect.model.UserInfo;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserDetailsDTO toDTO(UserInfo userInfo);
}
