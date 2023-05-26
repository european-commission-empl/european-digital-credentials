package eu.europa.ec.empl.edci.wallet.web.mapper;

import eu.europa.ec.empl.edci.mapper.commons.StringDateMapping;
import eu.europa.ec.empl.edci.wallet.common.model.CredentialDTO;
import eu.europa.ec.empl.edci.wallet.common.model.ShareLinkDTO;
import eu.europa.ec.empl.edci.wallet.web.model.ShareLinkInfoView;
import eu.europa.ec.empl.edci.wallet.web.model.ShareLinkView;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;


@Mapper(componentModel = "spring", uses = {StringDateMapping.class})
public interface ShareLinkRestMapper {

    public ShareLinkDTO toDTO(ShareLinkView shareLinkView, String credentialUuid);

    @AfterMapping
    default void toDTOAfter(@MappingTarget ShareLinkDTO shareLinkDTO, ShareLinkView shareLinkView, String credentialUuid) {
        shareLinkDTO.setCredential(new CredentialDTO() {{
            setUuid(credentialUuid);
        }});
    }

    public ShareLinkInfoView toResponseVO(ShareLinkDTO shareLinkDTO);

}
