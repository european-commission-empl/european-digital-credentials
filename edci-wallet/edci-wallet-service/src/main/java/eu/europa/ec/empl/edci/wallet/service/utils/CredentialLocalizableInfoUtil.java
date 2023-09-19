package eu.europa.ec.empl.edci.wallet.service.utils;

import eu.europa.ec.empl.edci.constants.EDCIConstants;
import eu.europa.ec.empl.edci.wallet.common.model.CredentialLocalizableInfoDTO;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component()
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
public class CredentialLocalizableInfoUtil {


    public CredentialLocalizableInfoDTO getCurrentLocaleInfo(List<CredentialLocalizableInfoDTO> credentialLocalizableInfoDTOList) {
        Optional<CredentialLocalizableInfoDTO> credentialLocalizableInfoDTO = credentialLocalizableInfoDTOList.stream().filter(item -> item.getLang().equalsIgnoreCase(LocaleContextHolder.getLocale().toString())).findFirst();
        return credentialLocalizableInfoDTO.isPresent() ? credentialLocalizableInfoDTO.get() : null;
    }

    public CredentialLocalizableInfoDTO getCurrentLocaleInfoOrAny(List<CredentialLocalizableInfoDTO> credentialLocalizableInfoDTOList) {
        CredentialLocalizableInfoDTO result = null;
        Optional<CredentialLocalizableInfoDTO> credentialLocalizableInfoDTO = credentialLocalizableInfoDTOList.stream().filter(item -> item.getLang().equalsIgnoreCase(LocaleContextHolder.getLocale().toString())).findFirst();
        if (credentialLocalizableInfoDTO.isPresent()) {
            result = credentialLocalizableInfoDTO.get();
        } else {
            credentialLocalizableInfoDTO = credentialLocalizableInfoDTOList.stream().filter(item -> item.getLang().equalsIgnoreCase(EDCIConstants.DEFAULT_LOCALE)).findFirst();
            result = credentialLocalizableInfoDTO.orElse(null);
        }
        if (result == null && !credentialLocalizableInfoDTOList.isEmpty())
            result = credentialLocalizableInfoDTOList.get(0);
        return result;
    }
}
