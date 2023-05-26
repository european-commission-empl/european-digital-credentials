package eu.europa.ec.empl.edci.config.service;

import com.nimbusds.jose.JWSAlgorithm;
import eu.europa.ec.empl.edci.constants.EDCIConfig;
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
public interface IOIDCConfigService {

    abstract String getString(String key);

    /**
     * OIDC CONFIGURATION
     **/
    @Bean
    default RegisteredClient registeredClient() {
        RegisteredClient registeredClient = new RegisteredClient();
        registeredClient.setClientId(this.getString(EDCIConfig.Security.CLIENT_ID));
        registeredClient.setClientSecret(this.getString(EDCIConfig.Security.CLIENT_SECRET));
        Set<String> scopes = new HashSet<>(Arrays.asList(this.getString(EDCIConfig.Security.SCOPES).split(",")));
        registeredClient.setScope(scopes);
        if (this.getString(EDCIConfig.Security.CODE_CHALLENGE_METHOD) != null) {
            registeredClient.setCodeChallengeMethod(PKCEAlgorithm.parse(this.getString(EDCIConfig.Security.CODE_CHALLENGE_METHOD)));
        }
        registeredClient.setRequestObjectSigningAlg(JWSAlgorithm.parse(this.getString(EDCIConfig.Security.SIGNING_ALG)));
        Set<String> redirectUris = new HashSet<>();
        redirectUris.add(this.getString(EDCIConfig.Security.REDIRECT_URL));
        registeredClient.setRedirectUris(redirectUris);
        return registeredClient;
    }

    @Bean
    default StaticSingleIssuerService staticIssuerService() {
        StaticSingleIssuerService staticSingleIssuerService = new StaticSingleIssuerService();
        staticSingleIssuerService.setIssuer(this.getString(EDCIConfig.Security.IDP_URL));
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
        registeredClientMap.put(this.getString(EDCIConfig.Security.IDP_URL), this.registeredClient());
        staticClientConfigurationService.setClients(registeredClientMap);
        return staticClientConfigurationService;
    }


    @Bean
    default StaticIntrospectionConfigurationService staticIntrospectionConfigurationService() {
        StaticIntrospectionConfigurationService staticIntrospectionConfigurationService = new StaticIntrospectionConfigurationService();
        staticIntrospectionConfigurationService.setIntrospectionUrl(this.getString(EDCIConfig.Security.INTROSPECTION_URL));
        staticIntrospectionConfigurationService.setClientConfiguration(this.registeredClient());
        return staticIntrospectionConfigurationService;
    }
}
