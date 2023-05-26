package eu.europa.ec.empl.edci.issuer.service;

import eu.europa.ec.empl.edci.config.service.IDBConfigService;
import eu.europa.ec.empl.edci.config.service.IMVCConfigService;
import eu.europa.ec.empl.edci.config.service.IOIDCConfigService;
import eu.europa.ec.empl.edci.config.service.ProxyConfigService;
import eu.europa.ec.empl.edci.constants.DataModelConstants;
import eu.europa.ec.empl.edci.constants.EDCIConfig;
import eu.europa.ec.empl.edci.issuer.common.constants.IssuerConfig;
import eu.europa.ec.empl.edci.security.oidc.EDCIAuthenticationSuccessHandler;
import eu.europa.ec.empl.edci.security.session.EDCIRedirectInvalidSessionStrategy;
import org.apache.tomcat.dbcp.dbcp2.BasicDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.util.Map;

@Service
@Configuration
@EnableTransactionManagement(proxyTargetClass = true)
@EnableJpaRepositories("eu.europa.ec.empl.edci.issuer.repository")
@PropertySources({
        @PropertySource(value = "classpath:/config/security/security_${spring.profiles.active}.properties", ignoreResourceNotFound = false),
        @PropertySource(value = "classpath:/config/issuer/issuer_${spring.profiles.active}.properties", ignoreResourceNotFound = false),
        @PropertySource(IssuerConfig.Path.ISSUER_FILE),
        @PropertySource(IssuerConfig.Path.PROXY_FILE),
        @PropertySource(IssuerConfig.Path.SECURITY_FILE),
        @PropertySource(DataModelConstants.Path.SHACL_FILE),
        //Front file to be used
        @PropertySource(IssuerConfig.Path.FRONT_FILE)
})
public class IssuerConfigService extends ProxyConfigService implements IMVCConfigService, IDBConfigService, IOIDCConfigService {

    private static BasicDataSource dataSource = null;

    @Override
    public BasicDataSource getDataSource() {
        return dataSource;
    }

    @Override
    public void setDataSource(BasicDataSource dS) {
        dataSource = dS;
    }

    @Override
    public String getPersistenceUnitName() {
        return "issuerPersistence";
    }

    @Override
    public String getPackagesToScan() {
        return "eu.europa.ec.empl.edci.issuer";
    }

    /**
     * OIDC CONFIG
     */
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

    @Bean
    public EDCIRedirectInvalidSessionStrategy EDCIRedirectInvalidSessionStrategy() {
        EDCIRedirectInvalidSessionStrategy strategy = new EDCIRedirectInvalidSessionStrategy(this.getString(EDCIConfig.Security.EXPIRED_SESSION_REDIRECT_URL));
        return strategy;

    }

    /**
     * PUBLIC METHODS
     */
    public EDCIConfig.Environment getCurrentEnvironment() {
        return EDCIConfig.Environment.valueOf(this.getString(IssuerConfig.Issuer.ACTIVE_PROFILE));
    }

    public Map<String, Object> getFrontPropertiesFromFile() {
        return this.getPropertiesFromFile(this.getEnv(), IssuerConfig.Path.FRONT_FILE);
    }

    @Override
    public Map<String, Object> getFrontEndProperties() {
        return this.getPropertiesFromFile(this.getEnv(), IssuerConfig.Path.FRONT_FILE);
    }

    @Override
    public Map<String, Object> getBackEndProperties() {
        return this.getPropertiesFromFile(this.getEnv(), IssuerConfig.Path.ISSUER_FILE);
    }

}
