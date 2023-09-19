package eu.europa.ec.empl.edci.wallet.mapper;

import eu.europa.ec.empl.edci.wallet.common.model.CredentialLocalizableInfoDTO;
import eu.europa.ec.empl.edci.wallet.entity.CredentialLocalizableInfoDAO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface CredentialLocalizableInfoMapper {

    CredentialLocalizableInfoDTO toDTO(CredentialLocalizableInfoDAO credentialLocalizableInfoDAO);

    CredentialLocalizableInfoDAO toDAO(CredentialLocalizableInfoDTO credentialLocalizableInfoDTO);

}
