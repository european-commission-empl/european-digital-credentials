package eu.europa.ec.empl.edci.security.service.oauth2;

import eu.europa.ec.empl.edci.config.service.IConfigService;
import eu.europa.ec.empl.edci.constants.EDCIConfig;
import eu.europa.ec.empl.edci.security.model.dto.EDCITokenEndpointResponse;
import eu.europa.ec.empl.edci.security.service.EDCIAuthenticationService;
import eu.europa.ec.empl.edci.security.service.EDCIUserService;
import eu.europa.ec.empl.edci.util.EDCIRestRequestBuilder;
import org.apache.log4j.Logger;
import org.mitre.oauth2.model.RegisteredClient;
import org.mitre.openid.connect.client.service.impl.DynamicServerConfigurationService;
import org.mitre.openid.connect.client.service.impl.StaticClientConfigurationService;
import org.mitre.openid.connect.client.service.impl.StaticSingleIssuerService;
import org.mitre.openid.connect.config.ServerConfiguration;
import org.mitre.openid.connect.model.OIDCAuthenticationToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.context.request.RequestContextHolder;

@Service
public class EDCIOauth2TokenService {
    private Logger logger = Logger.getLogger(EDCIOauth2TokenService.class);
    //Must be defined in spring-security.xml
    @Autowired
    private DynamicServerConfigurationService dynamicServerConfigurationService;

    //Must be defined in spring-security.xml
    @Autowired
    private StaticSingleIssuerService staticSingleIssuerService;

    //Must be defined in spring-security.xml
    @Autowired
    private StaticClientConfigurationService staticClientConfigurationService;

    @Autowired
    private EDCIUserService edciUserService;

    @Autowired
    private IConfigService configService;

    @Autowired
    private EDCIAuthenticationService edciAuthenticationService;

    public enum ReloadMethod {
        REFRESH_TOKEN,
        TOKEN_EXCHANGE,
        MOCK
    }

    private ReloadMethod getCurrentReloadMethod() {
        if (configService.getBoolean(EDCIConfig.OIDC_MOCK_USER_ACTIVE)) {
            return ReloadMethod.MOCK;
        } else if (configService.getBoolean(EDCIConfig.OIDC_USE_TOKEN_EXCHANGE)) {
            return ReloadMethod.TOKEN_EXCHANGE;
        } else {
            return ReloadMethod.REFRESH_TOKEN;
        }
    }

    public void reloadAccessToken(String audience) {
        switch (this.getCurrentReloadMethod()) {
            case REFRESH_TOKEN:
                this.reloadFromRefreshToken(audience);
                break;
            case TOKEN_EXCHANGE:
                this.reloadFromTokenExchange(audience);
                break;
            case MOCK:
                logger.debug("Mock user, no reload performed");
                break;
        }
    }

    public void reloadFromRefreshToken(String audience) {
        if (this.getEdciAuthenticationService().isAuthenticated()) {
            RegisteredClient clientConfig = this.getClientConfiguration();
            String tokenEndPoint = this.getTokenEndpoint();

            MultiValueMap<String, String> parameters = new LinkedMultiValueMap<>();
            parameters.add("grant_type", "refresh_token");
            parameters.add("audience", audience);
            parameters.add("refresh_token", this.getEdciAuthenticationService().getRefreshToken());

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            headers.setBasicAuth(clientConfig.getClientId(), clientConfig.getClientSecret());

            EDCITokenEndpointResponse tokenResponse = new EDCIRestRequestBuilder(HttpMethod.POST, tokenEndPoint)
                    .addHeaderRequestedWith()
                    .addHeaders(headers)
                    .addBody(parameters)
                    .buildRequest(EDCITokenEndpointResponse.class)
                    .execute();

            String sessionId = RequestContextHolder.currentRequestAttributes().getSessionId();
            logger.debug(String.format("OIDC - Requesting access token via Refresh Token for user %s and session id %s with RefreshToken %s...", this.getEdciUserService().getUserId(), sessionId, this.getEdciAuthenticationService().getRefreshToken().substring(10)));

            OIDCAuthenticationToken currentAuth = this.getEdciAuthenticationService().getCurrentAuthentication();
            this.getEdciAuthenticationService().setUpdatedTokens(this.getStaticSingleIssuerService().getIssuer(), tokenResponse.getAccess_token(), tokenResponse.getRefresh_token().substring(0, 10));
        }
    }

    public void reloadFromTokenExchange(String audience) {
        if (this.getEdciAuthenticationService().isAuthenticated()) {
            RegisteredClient clientConfig = this.getClientConfiguration();
            String tokenEndPoint = this.getTokenEndpoint();

            MultiValueMap<String, String> parameters = new LinkedMultiValueMap<>();
            parameters.add("grant_type", "urn:ietf:params:oauth:grant-type:token-exchange");
            parameters.add("audience", audience);
            parameters.add("requested_token_type", "urn:ietf:params:oauth:token-type:access_token");
            parameters.add("subject_token_type", "urn:ietf:params:oauth:token-type:refresh_token");
            parameters.add("subject_token", this.getEdciAuthenticationService().getRefreshToken());

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            headers.setBasicAuth(clientConfig.getClientId(), clientConfig.getClientSecret());

            String sessionId = RequestContextHolder.currentRequestAttributes().getSessionId();
            logger.debug(String.format("OIDC - Requesting access token via Token Exchange for user %s and session id %s with RefreshToken %s...", this.getEdciUserService().getUserId(), sessionId, this.getEdciAuthenticationService().getRefreshToken().substring(0, 10)));
            EDCITokenEndpointResponse tokenResponse = new EDCIRestRequestBuilder(HttpMethod.POST, tokenEndPoint)
                    .addHeaderRequestedWith()
                    .addHeaders(headers)
                    .addBody(parameters)
                    .buildRequest(EDCITokenEndpointResponse.class)
                    .execute();

            OIDCAuthenticationToken currentAuth = this.getEdciAuthenticationService().getCurrentAuthentication();
            this.getEdciAuthenticationService().setUpdatedAccessToken(this.getStaticSingleIssuerService().getIssuer(), tokenResponse.getAccess_token());

        }
    }

    public String getTokenEndpoint() {
        ServerConfiguration serverConfig = this.getDynamicServerConfigurationService().getServerConfiguration(this.getStaticSingleIssuerService().getIssuer());
        return serverConfig.getTokenEndpointUri();
    }

    private RegisteredClient getClientConfiguration() {
        return this.getStaticClientConfigurationService().getClientConfiguration(this.getDynamicServerConfigurationService().getServerConfiguration(this.getStaticSingleIssuerService().getIssuer()));
    }

    public DynamicServerConfigurationService getDynamicServerConfigurationService() {
        return dynamicServerConfigurationService;
    }

    public void setDynamicServerConfigurationService(DynamicServerConfigurationService dynamicServerConfigurationService) {
        this.dynamicServerConfigurationService = dynamicServerConfigurationService;
    }

    public StaticSingleIssuerService getStaticSingleIssuerService() {
        return staticSingleIssuerService;
    }

    public void setStaticSingleIssuerService(StaticSingleIssuerService staticSingleIssuerService) {
        this.staticSingleIssuerService = staticSingleIssuerService;
    }

    public EDCIUserService getEdciUserService() {
        return edciUserService;
    }

    public void setEdciUserService(EDCIUserService edciUserService) {
        this.edciUserService = edciUserService;
    }

    public StaticClientConfigurationService getStaticClientConfigurationService() {
        return staticClientConfigurationService;
    }

    public void setStaticClientConfigurationService(StaticClientConfigurationService staticClientConfigurationService) {
        this.staticClientConfigurationService = staticClientConfigurationService;
    }

    public EDCIAuthenticationService getEdciAuthenticationService() {
        return edciAuthenticationService;
    }

    public void setEdciAuthenticationService(EDCIAuthenticationService edciAuthenticationService) {
        this.edciAuthenticationService = edciAuthenticationService;
    }

    public IConfigService getConfigService() {
        return configService;
    }

    public void setConfigService(IConfigService configService) {
        this.configService = configService;
    }
}
