package eu.europa.ec.empl.edci.issuer.utils;

import eu.europa.ec.empl.edci.issuer.entity.specs.EuropassCredentialSpecDAO;
import org.apache.commons.lang3.LocaleUtils;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class EuropassCredentialDAOUtils {

    public Locale guessCredentialLocale(EuropassCredentialSpecDAO europassCredentialSpecDAO) {
        Locale locale = null;
        if (europassCredentialSpecDAO.getDefaultLanguage() != null) {
            locale = LocaleUtils.toLocale(europassCredentialSpecDAO.getDefaultLanguage());
        } else if (europassCredentialSpecDAO.getLanguages() != null && !europassCredentialSpecDAO.getLanguages().isEmpty()) {
            locale = LocaleUtils.toLocale(europassCredentialSpecDAO.getLanguages().stream().findFirst().orElse(null));
        } else {
            locale = LocaleContextHolder.getLocale();
        }
        return locale;
    }
}
