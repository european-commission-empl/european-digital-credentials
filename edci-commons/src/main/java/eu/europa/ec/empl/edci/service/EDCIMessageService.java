package eu.europa.ec.empl.edci.service;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.annotation.Scope;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
public class EDCIMessageService {

    @Autowired
    private MessageSource messageSource;

    public static final org.apache.log4j.Logger logger = Logger.getLogger(EDCIMessageService.class);

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
     * @param locale Language of the message to be retireved
     * @param msgKey Key of the message
     * @param params Parameters of the message (Optional)
     * @return A String with the message and it's parameters already supplied (if any)
     */
    public String getMessage(Locale locale, String msgKey, Object... params) {

        String message;
        try {
            message = this.getMessageSource().getMessage(msgKey, params, locale);
        } catch (NoSuchMessageException e) {
            logger.error("Message not found", e);
            if (Locale.ENGLISH.getLanguage().equals(locale.getLanguage())) {
                message = msgKey;
            } else {
                try {
                    message = this.getMessageSource().getMessage(msgKey, params, Locale.ENGLISH);
                } catch (NoSuchMessageException e1) {
                    message = msgKey;
                }
            }
        }

        return message;

    }


    /**
     * Gets the message from the messages_[lang].properties files defined in the resources folder, depending on the locale, then
     * replaces all of the occurrences of the keys in the map with it's value toString() result.
     *
     * @param locale    Language of the message to be retireved
     * @param msgKey    Key of the message
     * @param variables Variables of the message in Name/Value format(Optional)
     * @return A String with the message and replaced variables
     */
    public String getVariableMessage(Locale locale, String msgKey, Map<String, Object> variables) {
        String message = getMessage(locale, msgKey);

        if (variables != null && message != null && !message.equals("")) {
            for (Map.Entry<String, Object> variable : variables.entrySet()) {
                message = message.replaceAll(Pattern.quote(variable.getKey()), variable.getValue() != null ? variable.getValue().toString() : "");
            }
        }

        return message;
    }

    /**
     * Gets the message from the messages_[lang].properties files defined in the resources folder, with current locale, then
     * replaces all of the occurrences of the keys in the map with it's value toString() result.
     *
     * @param msgKey    Key of the message
     * @param variables Variables of the message in Name/Value format(Optional)
     * @return A String with the message and replaced variables
     */
    public String getVariableMessage(String msgKey, Map<String, Object> variables) {
        return this.getVariableMessage(this.getCurrentLocale(), msgKey, variables);
    }

    public MessageSource getMessageSource() {
        return messageSource;
    }

    public void setMessageSource(MessageSource messageSource) {
        this.messageSource = messageSource;
    }
}