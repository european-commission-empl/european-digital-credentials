package eu.europa.ec.empl.edci.issuer.service.consumers;


import eu.europa.ec.empl.edci.annotation.EDCIConsumer;
import eu.europa.ec.empl.edci.constants.EDCIConfig;
import eu.europa.ec.empl.edci.context.ConsumerContext;
import eu.europa.ec.empl.edci.datamodel.model.EuropassCredentialDTO;
import eu.europa.ec.empl.edci.datamodel.model.dataTypes.Text;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.i18n.LocaleContext;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@EDCIConsumer(applyTo = EuropassCredentialDTO.class, preProcess = true, priority = 2)
@Component
public class CredentialLanguageConsumer implements Consumer<ConsumerContext> {

    @Override
    public void accept(ConsumerContext context) {
        EuropassCredentialDTO europassCredentialDTO = context.getCredential().getCredential();
        String primaryLanguage = europassCredentialDTO.getPrimaryLanguage() != null ? europassCredentialDTO.getPrimaryLanguage() : EDCIConfig.Defaults.DEFAULT_LOCALE;
        List<String> availableLanguages = europassCredentialDTO.getAvailableLanguages() == null ? new ArrayList<String>() : europassCredentialDTO.getAvailableLanguages();
        if (!availableLanguages.contains(primaryLanguage)) {
            availableLanguages.add(primaryLanguage);
            europassCredentialDTO.setAvailableLanguages(availableLanguages);
        }
    }
}
