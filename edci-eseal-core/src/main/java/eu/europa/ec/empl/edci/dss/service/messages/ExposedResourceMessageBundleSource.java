package eu.europa.ec.empl.edci.dss.service.messages;

import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.stereotype.Component;

import java.util.Locale;
import java.util.Properties;

@Component("DssExposedResourceMessageBundleSource")
public class ExposedResourceMessageBundleSource extends ReloadableResourceBundleMessageSource {
    public Properties getMessages(Locale locale) {
        return getMergedProperties(locale).getProperties();
    }
}