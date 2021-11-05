package eu.europa.ec.empl.edci.dss.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Service
@Configuration
@EnableTransactionManagement
@PropertySources({
        @PropertySource(value = "classpath:/config/dss.properties", ignoreResourceNotFound = true)
})
public class DSSConfigService {

    @Autowired
    private Environment env;

    public <T> T get(String key, Class<T> clazz) {
        return this.env.getProperty(key, clazz);
    }

    public <T> T get(String key, Class<T> clazz, T defaultValue) {
        return this.env.getProperty(key, clazz, defaultValue);
    }

    public Boolean getBoolean(String key) {
        return this.get(key, Boolean.class, false);
    }

    public Boolean getBoolean(String key, Boolean defaultValue) {
        return this.get(key, Boolean.class, defaultValue);
    }

    public Integer getInteger(String key) {
        return this.get(key, Integer.class, 0);
    }

    public Integer getInteger(String key, Integer defaultValue) {
        return this.get(key, Integer.class, defaultValue);
    }

    public String getString(String key) {
        return this.get(key, String.class);
    }

    public String getString(String key, String defaultValue) {
        return this.get(key, String.class, defaultValue);
    }

    public String[] getStringArray(String key) {
        return this.getString(key).split(",");
    }

}
