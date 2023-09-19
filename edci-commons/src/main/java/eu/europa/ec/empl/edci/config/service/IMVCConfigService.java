package eu.europa.ec.empl.edci.config.service;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.AbstractEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.io.support.ResourcePropertySource;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@Configuration
public interface IMVCConfigService {

    abstract String getString(String key);

    abstract String getString(String key, String defaultValue);
    
    abstract Map<String, Object> getFrontEndProperties();

    abstract Map<String, Object> getBackEndProperties();

    default Map<String, Object> getPropertiesFromFile(Environment env, String filePath) {
        //Get Only FileName
        String frontFile = filePath.substring(filePath.lastIndexOf("/") + 1);
        Map<String, Object> map = new HashMap<>();
        //Search for property source
        for (Iterator it = ((AbstractEnvironment) env).getPropertySources().iterator(); it.hasNext(); ) {
            Object propertySource = it.next();
            if (propertySource instanceof ResourcePropertySource
                    && ((ResourcePropertySource) propertySource).getName().contains(frontFile)) {
                //Add all properties from property source
                map.putAll(((ResourcePropertySource) propertySource).getSource());
            }
        }
        return map;
    }

}
