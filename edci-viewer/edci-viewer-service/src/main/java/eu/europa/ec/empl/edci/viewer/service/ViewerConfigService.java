package eu.europa.ec.empl.edci.viewer.service;

import eu.europa.ec.empl.edci.config.service.IConfigService;
import eu.europa.ec.empl.edci.constants.EDCIConfig;
import eu.europa.ec.empl.edci.security.oidc.EDCIAuthenticationSuccessHandler;
import eu.europa.ec.empl.edci.viewer.common.constants.ViewerConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.*;
import org.springframework.core.env.Environment;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@Configuration
@PropertySources({
        @PropertySource(value = "classpath:/config/viewer/viewer_${spring.profiles.active}.properties"),
        @PropertySource(value = "classpath:/config/security/security_${spring.profiles.active}.properties"),
        @PropertySource(ViewerConfig.Path.VIEWER_FILE),
        @PropertySource(ViewerConfig.Path.SECURITY_FILE),
        @PropertySource(ViewerConfig.Path.FRONT_FILE)
})
@Primary
public class ViewerConfigService implements IConfigService {

    @Autowired
    private Environment env;

    public <T> T get(String key, Class<T> clazz) {
        return this.env.getProperty(key, clazz);
    }

    public <T> T get(String key, Class<T> clazz, T defaultValue) {
        return this.env.getProperty(key, clazz, defaultValue);
    }

    @Bean
    public LoginUrlAuthenticationEntryPoint authenticationEntryPoint() {
        LoginUrlAuthenticationEntryPoint loginUrlAuthenticationEntryPoint = new LoginUrlAuthenticationEntryPoint(this.getString(EDCIConfig.Security.LOGIN_URL));
        return loginUrlAuthenticationEntryPoint;
    }

    @Bean
    public EDCIAuthenticationSuccessHandler EDCIAuthenticationSuccessHandler() {
        EDCIAuthenticationSuccessHandler edciAuthenticationSuccessHandler = new EDCIAuthenticationSuccessHandler();
        edciAuthenticationSuccessHandler.setDefaultTargetUrl(this.getString(EDCIConfig.Security.SUCCESS_DEFAULT_URL));
        return edciAuthenticationSuccessHandler;
    }

    @Override
    public Map<String, Object> getFrontEndProperties() {
        return this.getPropertiesFromFile(this.env, ViewerConfig.Path.FRONT_FILE);
    }

    @Override
    public Map<String, Object> getBackEndProperties() {
        return this.getPropertiesFromFile(this.env, ViewerConfig.Path.VIEWER_FILE);
    }

}

