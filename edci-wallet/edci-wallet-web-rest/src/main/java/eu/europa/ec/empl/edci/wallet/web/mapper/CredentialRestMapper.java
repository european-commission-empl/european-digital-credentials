package eu.europa.ec.empl.edci.wallet.web.mapper;

import eu.europa.ec.empl.edci.datamodel.model.base.LocalizableDTO;
import eu.europa.ec.empl.edci.datamodel.view.CredentialBaseView;
import eu.europa.ec.empl.edci.util.XmlUtil;
import eu.europa.ec.empl.edci.wallet.common.model.CredentialDTO;
import eu.europa.ec.empl.edci.wallet.common.model.CredentialLocalizableInfoDTO;
import eu.europa.ec.empl.edci.wallet.mapper.CredentialMapper;
import eu.europa.ec.empl.edci.wallet.service.WalletConfigService;
import eu.europa.ec.empl.edci.wallet.service.utils.CredentialLocalizableInfoUtil;
import eu.europa.ec.empl.edci.wallet.web.model.CredentialUploadView;
import eu.europa.ec.empl.edci.wallet.web.model.CredentialView;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mapstruct.*;
import org.springframework.context.i18n.LocaleContextHolder;

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.util.List;

@Mapper(componentModel = "spring")
public interface CredentialRestMapper {

    public static final Logger logger = LogManager.getLogger(CredentialMapper.class);

    @Mappings({
            @Mapping(source = "walletAddress", target = "walletDTO.walletAddress"),
            @Mapping(source = "userId", target = "walletDTO.userId")
    })
    CredentialDTO toDTO(CredentialUploadView credentialUploadView);

    CredentialDTO toDTO(CredentialBaseView credentialView);

    List<CredentialDTO> toDTOList(List<CredentialBaseView> credentialViews);

    @Mappings({
            @Mapping(source = "credentialLocalizableInfoDTOS", target = "title", qualifiedByName = "getTitle"),
            @Mapping(source = "credentialLocalizableInfoDTOS", target = "description", qualifiedByName = "getDescription"),
            @Mapping(source = "credentialLocalizableInfoDTOS", target = "type", qualifiedByName = "getType"),
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

    @Named("getType")
    default String getType(List<CredentialLocalizableInfoDTO> credentialLocalizableInfoDTOS, @Context WalletConfigService walletConfigService, @Context CredentialLocalizableInfoUtil credentialLocalizableInfoUtil) {
        CredentialLocalizableInfoDTO credentialLocalizableInfoDTO = credentialLocalizableInfoUtil.getCurrentLocaleInfoOrAny(credentialLocalizableInfoDTOS);
        return credentialLocalizableInfoDTO == null ? null : credentialLocalizableInfoDTO.getCredentialType();
    }

    @AfterMapping
    default void setViewerURL(@MappingTarget CredentialView credentialView, CredentialDTO credentialDTO, @Context WalletConfigService walletConfigService, @Context CredentialLocalizableInfoUtil credentialLocalizableInfoUtil) {
        if (credentialDTO.getWalletDTO() != null && !credentialDTO.getWalletDTO().getTemporary()) {
            credentialView.setViewerURL(walletConfigService.getViewerURL(credentialDTO));
        }
    }


    abstract List<CredentialView> toVOList(List<CredentialDTO> credentialDTOS, @Context WalletConfigService walletConfigService, @Context CredentialLocalizableInfoUtil credentialLocalizableInfoUtil);


    default <T extends LocalizableDTO> String getLocalizableXMLString(byte[] bytes, @Context XmlUtil xmlUtil, Class<T> clazz, WalletConfigService walletConfigService) throws JAXBException, IOException {
        T localizable = xmlUtil.fromBytes(bytes, clazz);
        return localizable.getLocalizedStringOrAny(LocaleContextHolder.getLocale().toString());
    }


}
