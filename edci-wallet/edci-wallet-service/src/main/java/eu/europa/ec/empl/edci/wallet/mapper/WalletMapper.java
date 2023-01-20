package eu.europa.ec.empl.edci.wallet.mapper;

import eu.europa.ec.empl.edci.wallet.common.model.WalletDTO;
import eu.europa.ec.empl.edci.wallet.entity.WalletDAO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring", uses = CredentialMapper.class)
public interface WalletMapper {

    @Mappings({
            @Mapping(source = "pk", target = "id")
    })
    WalletDTO toDTO(WalletDAO walletDAO);

    @Mappings({
            //@Mapping(source = "credentialDTOList", target = "credentialDAOList"),
            @Mapping(source = "id", target = "pk")
    })
    public WalletDAO toDAO(WalletDTO walletDTO);

}
