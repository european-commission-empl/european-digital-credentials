package eu.europa.ec.empl.edci.wallet.mapper;

import eu.europa.ec.empl.edci.wallet.common.model.CredentialLocalizableInfoDTO;
import eu.europa.ec.empl.edci.wallet.entity.CredentialLocalizableInfoDAO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring", uses = CredentialMapper.class)
public interface CredentialLocalizableInfoMapper {

    @Mappings({
            @Mapping(source = "credentialDAO", target = "credentialDTO", ignore = true)
    })
    CredentialLocalizableInfoDTO toDTO(CredentialLocalizableInfoDAO credentialLocalizableInfoDAO);

    @Mappings({
            @Mapping(source = "credentialDTO", target = "credentialDAO", ignore = true)
    })
    CredentialLocalizableInfoDAO toDAO(CredentialLocalizableInfoDTO credentialLocalizableInfoDTO);

}
