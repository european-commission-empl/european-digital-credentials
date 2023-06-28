package eu.europa.ec.empl.edci.wallet.mapper;

import eu.europa.ec.empl.edci.wallet.common.model.CredentialDTO;
import eu.europa.ec.empl.edci.wallet.entity.CredentialDAO;
import org.mapstruct.Context;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring", uses = {WalletMapper.class, CredentialLocalizableInfoMapper.class, ShareLinkMapper.class})
public interface CredentialMapper {


//    default CredentialDTO toDTO(CredentialDAO credentialDAO) {
//        return toDTOCycleAvoiding(credentialDAO, new CycleAvoidingMappingContext());
//    }

    CredentialDTO toDTO(CredentialDAO credentialDAO, @Context CycleAvoidingMappingContext cycleAvoidingMappingContext);

//    default CredentialDAO toDAO(CredentialDTO credentialDTO) {
//        return toDAOCycleAvoiding(credentialDTO, new CycleAvoidingMappingContext());
//    }

    CredentialDAO toDAO(CredentialDTO credentialDTO, @Context CycleAvoidingMappingContext cycleAvoidingMappingContext);

    List<CredentialDTO> toDTOList(List<CredentialDAO> credentialDAO, @Context CycleAvoidingMappingContext cycleAvoidingMappingContext);

}
