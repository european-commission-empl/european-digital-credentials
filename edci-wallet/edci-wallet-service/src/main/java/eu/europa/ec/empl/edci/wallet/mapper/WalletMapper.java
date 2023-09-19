package eu.europa.ec.empl.edci.wallet.mapper;

import eu.europa.ec.empl.edci.wallet.common.model.WalletDTO;
import eu.europa.ec.empl.edci.wallet.entity.WalletDAO;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface WalletMapper {

    @Mappings({
            @Mapping(source = "pk", target = "id")
    })
    WalletDTO toDTO(WalletDAO walletDAO, @Context CycleAvoidingMappingContext cycleAvoidingMappingContext);

    @Mappings({
            @Mapping(source = "id", target = "pk")
    })
    public WalletDAO toDAO(WalletDTO walletDTO, @Context CycleAvoidingMappingContext cycleAvoidingMappingContext);

}
