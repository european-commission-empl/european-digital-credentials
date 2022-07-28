package eu.europa.ec.empl.edci.wallet.mapper;

import eu.europa.ec.empl.edci.wallet.common.model.ShareLinkDTO;
import eu.europa.ec.empl.edci.wallet.entity.ShareLinkDAO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.util.List;

@Mapper(componentModel = "spring", uses = CredentialMapper.class)
public interface ShareLinkMapper {

    @Mappings({
            @Mapping(source = "credentialDAO", target = "credentialDTO"),
            @Mapping(source = "pk", target = "id")
    })
    ShareLinkDTO toDTO(ShareLinkDAO shareLinkDAO);

    @Mappings({
            @Mapping(source = "credentialDTO", target = "credentialDAO"),
            @Mapping(source = "id", target = "pk")
    })
    ShareLinkDAO toDAO(ShareLinkDTO shareLinkDTO);

    List<ShareLinkDTO> toDTOList(List<ShareLinkDAO> shareLinkDAO);


}
