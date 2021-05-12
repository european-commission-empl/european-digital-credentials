package eu.europa.ec.empl.edci.wallet.mapper;

import eu.europa.ec.empl.edci.wallet.common.model.CredentialDTO;
import eu.europa.ec.empl.edci.wallet.entity.CredentialDAO;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", uses = {WalletMapper.class, CredentialLocalizableInfoMapper.class})
public interface CredentialMapper {


    //CredentialBasicDTO toBasicDTO(CredentialDTO credentialDTO);

    @Mappings({
            @Mapping(source = "credentialDAO.walletDAO", target = "walletDTO"),
            @Mapping(source = "credentialDAO.shareLinkDAOList", target = "shareLinkDTOList"),
            @Mapping(source = "credentialLocalizableInfoDAOS", target = "credentialLocalizableInfoDTOS")
    })
    CredentialDTO toDTO(CredentialDAO credentialDAO);


    @Mappings({
            @Mapping(source = "walletDTO", target = "walletDAO"),
            @Mapping(source = "credentialLocalizableInfoDTOS", target = "credentialLocalizableInfoDAOS")
    })
    CredentialDAO toDAO(CredentialDTO credentialDTO);

    @AfterMapping()
    default void toDAOCredentialAfterMapping(CredentialDTO credentialDTO, @MappingTarget CredentialDAO credentialDAO) {
        credentialDAO.getCredentialLocalizableInfoDAOS().stream().forEach(credentialLocalizableInfoDAO -> credentialLocalizableInfoDAO.setCredentialDAO(credentialDAO));
    }

    List<CredentialDTO> toDTOList(List<CredentialDAO> credentialDAO);

}
