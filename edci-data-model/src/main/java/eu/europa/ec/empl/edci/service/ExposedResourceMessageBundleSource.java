package eu.europa.ec.empl.edci.service;

import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.stereotype.Component;

import java.util.Locale;
import java.util.Properties;

/**
 * The Exposed resource message bundle source, gets messages from bundle.properties.
 */
@Component("ExposedResourceMessageBundleSource")
public class ExposedResourceMessageBundleSource extends ReloadableResourceBundleMessageSource {
    /**
     * Gets messages from bundles.properties using a provided locale.
     *
     * @param locale the locale
     * @return the messages
     */
    public Properties getMessages(Locale locale) {
        if(locale == null) {
            return null;
        }

        return getMergedProperties(locale).getProperties();
    }
}