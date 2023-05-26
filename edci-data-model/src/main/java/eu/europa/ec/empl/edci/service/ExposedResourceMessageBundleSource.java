package eu.europa.ec.empl.edci.service;

import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.stereotype.Component;

import java.util.Locale;
import java.util.Properties;

@Component("ExposedResourceMessageBundleSource")
public class ExposedResourceMessageBundleSource extends ReloadableResourceBundleMessageSource {
    public Properties getMessages(Locale locale) {
        return getMergedProperties(locale).getProperties();
    }
}