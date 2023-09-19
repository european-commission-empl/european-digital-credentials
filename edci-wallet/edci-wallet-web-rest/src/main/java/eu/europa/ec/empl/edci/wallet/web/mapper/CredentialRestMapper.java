package eu.europa.ec.empl.edci.wallet.web.mapper;

import eu.europa.ec.empl.edci.wallet.common.model.CredentialDTO;
import eu.europa.ec.empl.edci.wallet.common.model.CredentialLocalizableInfoDTO;
import eu.europa.ec.empl.edci.wallet.mapper.CredentialMapper;
import eu.europa.ec.empl.edci.wallet.service.WalletConfigService;
import eu.europa.ec.empl.edci.wallet.service.utils.CredentialLocalizableInfoUtil;
import eu.europa.ec.empl.edci.wallet.web.model.CredentialBaseView;
import eu.europa.ec.empl.edci.wallet.web.model.CredentialUploadView;
import eu.europa.ec.empl.edci.wallet.web.model.CredentialView;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CredentialRestMapper {

    public static final Logger logger = LogManager.getLogger(CredentialMapper.class);

    CredentialDTO toDTO(CredentialUploadView credentialUploadView);

    CredentialDTO toDTO(CredentialBaseView credentialView);

    List<CredentialDTO> toDTOList(List<CredentialBaseView> credentialViews);

    @Mappings({
            @Mapping(source = "credentialLocalizableInfo", target = "title", qualifiedByName = "getTitle"),
            @Mapping(source = "credentialLocalizableInfo", target = "description", qualifiedByName = "getDescription"),
            @Mapping(source = "credentialLocalizableInfo", target = "profile", qualifiedByName = "getProfile"),
    })
    abstract CredentialView toVO(CredentialDTO credentialDTO, @Context WalletConfigService walletConfigService, @Context CredentialLocalizableInfoUtil credentialLocalizableInfoUtil);


    @Named("getTitle")
    default String getTitle(List<CredentialLocalizableInfoDTO> credentialLocalizableInfoDTOS, @Context WalletConfigService walletConfigService, @Context CredentialLocalizableInfoUtil credentialLocalizableInfoUtil) {
        CredentialLocalizableInfoDTO credentialLocalizableInfoDTO = credentialLocalizableInfoUtil.getCurrentLocaleInfoOrAny(credentialLocalizableInfoDTOS);
        return credentialLocalizableInfoDTO == null ? null : credentialLocalizableInfoDTO.getTitle();
    }

    @Named("getDescription")
    default String getDescription(List<CredentialLocalizableInfoDTO> credentialLocalizableInfoDTOS, @Context WalletConfigService walletConfigService, @Context CredentialLocalizableInfoUtil credentialLocalizableInfoUtil) {
        CredentialLocalizableInfoDTO credentialLocalizableInfoDTO = credentialLocalizableInfoUtil.getCurrentLocaleInfoOrAny(credentialLocalizableInfoDTOS);
        return credentialLocalizableInfoDTO == null ? null : credentialLocalizableInfoDTO.getDescription();
    }

    @Named("getProfile")
    default List<String> getProfile(List<CredentialLocalizableInfoDTO> credentialLocalizableInfoDTOS, @Context WalletConfigService walletConfigService, @Context CredentialLocalizableInfoUtil credentialLocalizableInfoUtil) {
        CredentialLocalizableInfoDTO credentialLocalizableInfoDTO = credentialLocalizableInfoUtil.getCurrentLocaleInfoOrAny(credentialLocalizableInfoDTOS);
        return credentialLocalizableInfoDTO == null ? null : credentialLocalizableInfoDTO.getCredentialProfile();
    }

    @AfterMapping
    default void setViewerURL(@MappingTarget CredentialView credentialView, CredentialDTO credentialDTO, @Context WalletConfigService walletConfigService, @Context CredentialLocalizableInfoUtil credentialLocalizableInfoUtil) {
        if (credentialDTO.getWallet() != null && !credentialDTO.getWallet().getTemporary()) {
            credentialView.setViewerURL(walletConfigService.getViewerURL(credentialDTO));
        }
    }

    abstract List<CredentialView> toVOList(List<CredentialDTO> credentialDTOS, @Context WalletConfigService walletConfigService, @Context CredentialLocalizableInfoUtil credentialLocalizableInfoUtil);

}
