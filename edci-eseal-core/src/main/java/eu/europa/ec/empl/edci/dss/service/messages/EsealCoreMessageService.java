package eu.europa.ec.empl.edci.dss.service.messages;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.annotation.Scope;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
public class EsealCoreMessageService {

    @Autowired
    private ExposedResourceMessageBundleSource messageSource;

    public static final Logger logger = LogManager.getLogger(EsealCoreMessageService.class);

    /**
     * Retrieves and returns the current locale from the LocaleContextHolder
     *
     * @return Current Locale
     */
    public Locale getCurrentLocale() {
        return LocaleContextHolder.getLocale();
    }

    /**
     * Gets the message from the messages_[lang].properties files defined in the resources folder, depending on the locale.
     *
     * @param msgKey Key of the message
     * @param params Parameters of the message (Optional)
     * @return A String with the message and it's parameters already supplied (if any)
     */
    public String getMessage(String msgKey, Object... params) {

        return getMessage(LocaleContextHolder.getLocale(), msgKey, params);

    }


    /**
     * Gets the message from the messages_[lang].properties files defined in the resources folder, depending on the locale.
     *
     * @param locale Language of the message to be retrieved
     * @param msgKey Key of the message
     * @param params Parameters of the message (Optional)
     * @return A String with the message and it's parameters already supplied (if any)
     */
    public String getMessage(Locale locale, String msgKey, Object... params) {

        String message;
        try {
            message = this.getMessageSource().getMessage(msgKey, params, locale);
            if (message == null) throw new NoSuchMessageException(msgKey);
        } catch (NoSuchMessageException e) {
            logger.error("Message not found", e);
            if (Locale.ENGLISH.getLanguage().equals(locale.getLanguage())) {
                message = msgKey;
            } else {
                try {
                    message = this.getMessageSource().getMessage(msgKey, params, Locale.ENGLISH);
                    if (message == null) throw new NoSuchMessageException(msgKey);
                } catch (NoSuchMessageException e1) {
                    message = msgKey;
                }
            }
        }

        return message;

    }

    public MessageSource getMessageSource() {
        return messageSource;
    }

    public void setMessageSource(ExposedResourceMessageBundleSource messageSource) {
        this.messageSource = messageSource;
    }
}
