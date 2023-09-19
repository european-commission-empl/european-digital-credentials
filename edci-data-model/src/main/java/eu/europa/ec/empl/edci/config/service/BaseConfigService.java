package eu.europa.ec.empl.edci.config.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
public abstract class BaseConfigService {

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
        return this.getString(key) == null ? null : this.getString(key).split(",");
    }

    public Environment getEnv() {
        return env;
    }

    public void setEnv(Environment env) {
        this.env = env;
    }
}
