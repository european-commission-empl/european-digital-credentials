package eu.europa.ec.empl.edci.wallet.mapper;

import eu.europa.ec.empl.edci.wallet.common.model.ShareLinkDTO;
import eu.europa.ec.empl.edci.wallet.entity.ShareLinkDAO;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.util.List;

@Mapper(componentModel = "spring", uses = CredentialMapper.class)
public interface ShareLinkMapper {


//    default ShareLinkDTO toDTO(ShareLinkDAO shareLinkDAO) {
//        return toDTOCycleAvoiding(shareLinkDAO, new CycleAvoidingMappingContext());
//    }

    @Mappings({
            @Mapping(source = "pk", target = "id")
    })
    ShareLinkDTO toDTO(ShareLinkDAO shareLinkDAO, @Context CycleAvoidingMappingContext cycleAvoidingMappingContext);


//    default ShareLinkDAO toDAO(ShareLinkDTO shareLinkDTO) {
//        return toDAOCycleAvoiding(shareLinkDTO, new CycleAvoidingMappingContext());
//    }

    @Mappings({
            @Mapping(source = "id", target = "pk")
    })
    ShareLinkDAO toDAO(ShareLinkDTO shareLinkDTO, @Context CycleAvoidingMappingContext cycleAvoidingMappingContext);

    List<ShareLinkDTO> toDTOList(List<ShareLinkDAO> shareLinkDAO, @Context CycleAvoidingMappingContext cycleAvoidingMappingContext);


}
