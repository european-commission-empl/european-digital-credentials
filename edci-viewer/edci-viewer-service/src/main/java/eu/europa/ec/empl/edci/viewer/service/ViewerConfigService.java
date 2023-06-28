package eu.europa.ec.empl.edci.viewer.service;

import eu.europa.ec.empl.edci.config.service.IMVCConfigService;
import eu.europa.ec.empl.edci.config.service.IOIDCConfigService;
import eu.europa.ec.empl.edci.config.service.ProxyConfigService;
import eu.europa.ec.empl.edci.constants.EDCIConfig;
import eu.europa.ec.empl.edci.security.oidc.EDCIAuthenticationSuccessHandler;
import eu.europa.ec.empl.edci.security.session.EDCINoRedirectInvalidSessionStrategy;
import eu.europa.ec.empl.edci.viewer.common.constants.ViewerConfig;
import org.springframework.context.annotation.*;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@Configuration
@PropertySources({
        @PropertySource(value = "classpath:/config/viewer/viewer_${spring.profiles.active}.properties"),
        @PropertySource(value = "classpath:/config/security/security_${spring.profiles.active}.properties"),
        @PropertySource(ViewerConfig.Path.VIEWER_FILE),
        @PropertySource(ViewerConfig.Path.PROXY_FILE),
        @PropertySource(ViewerConfig.Path.SECURITY_FILE),
        @PropertySource(ViewerConfig.Path.FRONT_FILE)
})
@Primary
public class ViewerConfigService extends ProxyConfigService implements IMVCConfigService, IOIDCConfigService {


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
        return this.getPropertiesFromFile(this.getEnv(), ViewerConfig.Path.FRONT_FILE);
    }

    @Override
    public Map<String, Object> getBackEndProperties() {
        return this.getPropertiesFromFile(this.getEnv(), ViewerConfig.Path.VIEWER_FILE);
    }

    @Bean
    public EDCINoRedirectInvalidSessionStrategy EDCINoRedirectInvalidSessionStrategy() {
        EDCINoRedirectInvalidSessionStrategy strategy = new EDCINoRedirectInvalidSessionStrategy(this.getString(EDCIConfig.Security.EXPIRED_SESSION_REDIRECT_URL));
        return strategy;
    }
}

