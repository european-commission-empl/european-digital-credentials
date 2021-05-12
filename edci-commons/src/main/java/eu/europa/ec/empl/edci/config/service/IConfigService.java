package eu.europa.ec.empl.edci.config.service;

import com.nimbusds.jose.JWSAlgorithm;
import eu.europa.ec.empl.edci.constants.EDCIConfig;
import eu.europa.ec.empl.edci.security.session.EDCISimpleRedirectInvalidSessionStrategy;
import org.mitre.oauth2.introspectingfilter.service.impl.StaticIntrospectionConfigurationService;
import org.mitre.oauth2.model.PKCEAlgorithm;
import org.mitre.oauth2.model.RegisteredClient;
import org.mitre.openid.connect.client.service.impl.StaticClientConfigurationService;
import org.mitre.openid.connect.client.service.impl.StaticSingleIssuerService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.provider.error.OAuth2AuthenticationEntryPoint;

import java.util.*;

@Configuration
public interface IConfigService {

    public <T> T get(String key, Class<T> clazz);

    public <T> T get(String key, Class<T> clazz, T defaultValue);

    default Boolean getBoolean(String key) {
        return this.get(key, Boolean.class, false);
    }

    default Boolean getBoolean(String key, Boolean defaultValue) {
        return this.get(key, Boolean.class, defaultValue);
    }

    default Integer getInteger(String key) {
        return this.get(key, Integer.class, 0);
    }

    default Integer getInteger(String key, Integer defaultValue) {
        return this.get(key, Integer.class, defaultValue);
    }

    default String getString(String key) {
        return this.get(key, String.class);
    }

    default String getString(String key, String defaultValue) {
        return this.get(key, String.class, defaultValue);
    }

    default String[] getStringArray(String key) {
        return this.getString(key) == null ? null : this.getString(key).split(",");
    }

    /**
     * OIDC CONFIGURATION
     **/
    @Bean
    default RegisteredClient registeredClient() {
        RegisteredClient registeredClient = new RegisteredClient();
        registeredClient.setClientId(this.getString(EDCIConfig.OIDC_CLIENTID));
        registeredClient.setClientSecret(this.getString(EDCIConfig.OIDC_SECRET));
        Set<String> scopes = new HashSet<>(Arrays.asList(this.getString(EDCIConfig.OIDC_SCOPES).split(",")));
        registeredClient.setScope(scopes);
        registeredClient.setCodeChallengeMethod(PKCEAlgorithm.parse(this.getString(EDCIConfig.OIDC_CODE_CHALLENGE_METHOD)));
        registeredClient.setRequestObjectSigningAlg(JWSAlgorithm.parse(this.getString(EDCIConfig.OIDC_SIGNING_ALG)));
        Set<String> redirectUris = new HashSet<>();
        redirectUris.add(this.getString(EDCIConfig.OIDC_REDIRECT_URL));
        registeredClient.setRedirectUris(redirectUris);
        return registeredClient;
    }

    @Bean
    default StaticSingleIssuerService staticIssuerService() {
        StaticSingleIssuerService staticSingleIssuerService = new StaticSingleIssuerService();
        staticSingleIssuerService.setIssuer(this.getString(EDCIConfig.OIDC_IDP_URL));
        return staticSingleIssuerService;
    }

    @Bean
    default OAuth2AuthenticationEntryPoint oauthAuthenticationEntryPoint() {
        OAuth2AuthenticationEntryPoint oAuth2AuthenticationEntryPoint = new OAuth2AuthenticationEntryPoint();
        oAuth2AuthenticationEntryPoint.setRealmName("EDCI");
        return oAuth2AuthenticationEntryPoint;
    }

    @Bean
    default StaticClientConfigurationService staticClientConfigurationService() {
        StaticClientConfigurationService staticClientConfigurationService = new StaticClientConfigurationService();
        Map<String, RegisteredClient> registeredClientMap = new HashMap<>();
        registeredClientMap.put(this.getString(EDCIConfig.OIDC_IDP_URL), this.registeredClient());
        staticClientConfigurationService.setClients(registeredClientMap);
        return staticClientConfigurationService;
    }


    @Bean
    default StaticIntrospectionConfigurationService staticIntrospectionConfigurationService() {
        StaticIntrospectionConfigurationService staticIntrospectionConfigurationService = new StaticIntrospectionConfigurationService();
        staticIntrospectionConfigurationService.setIntrospectionUrl(this.getString(EDCIConfig.OIDC_INTROSPECTION_URL));
        staticIntrospectionConfigurationService.setClientConfiguration(this.registeredClient());
        return staticIntrospectionConfigurationService;
    }

    @Bean
    default EDCISimpleRedirectInvalidSessionStrategy EDCISimpleRedirectInvalidSessionStrategy() {
        String invalidSessionUrl = this.getString(EDCIConfig.OIDC_INVALID_SESSION_URL);
        EDCISimpleRedirectInvalidSessionStrategy edciSimpleRedirectInvalidSessionStrategy = new EDCISimpleRedirectInvalidSessionStrategy(invalidSessionUrl);
        edciSimpleRedirectInvalidSessionStrategy.setRedirectTo(this.getString(EDCIConfig.OIDC_EXPIRED_SESSION_REDIRECT_URL));
        edciSimpleRedirectInvalidSessionStrategy.setCreateNewSession(false);
        return edciSimpleRedirectInvalidSessionStrategy;
    }


}
