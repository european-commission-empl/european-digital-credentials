package eu.europa.ec.empl.edci.security.oidc;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.nimbusds.jwt.JWT;
import eu.europa.ec.empl.edci.config.service.IConfigService;
import eu.europa.ec.empl.edci.constants.EDCIConfig;
import eu.europa.ec.empl.edci.security.EDCISecurityContextHolder;
import eu.europa.ec.empl.edci.security.service.EDCIAuthenticationService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mitre.openid.connect.client.OIDCAuthenticationProvider;
import org.mitre.openid.connect.client.OIDCAuthoritiesMapper;
import org.mitre.openid.connect.model.DefaultUserInfo;
import org.mitre.openid.connect.model.OIDCAuthenticationToken;
import org.mitre.openid.connect.model.PendingOIDCAuthenticationToken;
import org.mitre.openid.connect.model.UserInfo;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.client.resource.OAuth2AccessDeniedException;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.ClientRegistrationException;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.ResourceServerTokenServices;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.util.Assert;

import java.text.ParseException;
import java.util.Iterator;
import java.util.Set;

public class EDCIOIDCAuthenticationProvider extends OIDCAuthenticationProvider implements InitializingBean {

    @Autowired
    private EDCIAuthenticationService edciAuthenticationService;
    @Autowired
    private EDCISecurityContextHolder edciSecurityContextHolder;
    private ResourceServerTokenServices tokenServices;
    private ClientDetailsService clientDetailsService;
    private OIDCAuthoritiesMapper authoritiesMapper;
    private IConfigService configService;
    protected static final Log logger = LogFactory.getLog(EDCIOIDCAuthenticationProvider.class);

    public ResourceServerTokenServices getTokenServices() {
        return tokenServices;
    }

    public void setTokenServices(ResourceServerTokenServices tokenServices) {
        this.tokenServices = tokenServices;
    }

    public ClientDetailsService getClientDetailsService() {
        return clientDetailsService;
    }

    public void setClientDetailsService(ClientDetailsService clientDetailsService) {
        this.clientDetailsService = clientDetailsService;
    }

    public OIDCAuthoritiesMapper getAuthoritiesMapper() {
        return authoritiesMapper;
    }

    @Override
    public void setAuthoritiesMapper(OIDCAuthoritiesMapper authoritiesMapper) {
        this.authoritiesMapper = authoritiesMapper;
    }

    public EDCIAuthenticationService getEdciAuthenticationService() {
        return edciAuthenticationService;
    }

    public void setEdciAuthenticationService(EDCIAuthenticationService edciAuthenticationService) {
        this.edciAuthenticationService = edciAuthenticationService;
    }

    public EDCISecurityContextHolder getEdciSecurityContextHolder() {
        return edciSecurityContextHolder;
    }

    public void setEdciSecurityContextHolder(EDCISecurityContextHolder edciSecurityContextHolder) {
        this.edciSecurityContextHolder = edciSecurityContextHolder;
    }

    public IConfigService getConfigService() {
        return configService;
    }

    public void setConfigService(IConfigService configService) {
        this.configService = configService;
    }

    public static Log getLogger() {
        return logger;
    }

    @Override
    public Authentication authenticate(org.springframework.security.core.Authentication authentication) throws AuthenticationException {
        boolean isMockUser = false;

        //it is found a mock token on the request (probably from localhost:4200)
        if (this.getConfigService().getBoolean(EDCIConfig.Security.MOCK_USER_ACTIVE)) {
            //If mock token and no authenticated return mock user
            isMockUser = true;
            logger.debug("Mock user is active");
        }

        if (isMockUser && this.getEdciAuthenticationService().isAuthenticated()) {
            //If already authenticated, return authentication (also for custom setups with protected app zones)
            return this.getEdciSecurityContextHolder().getAuthentication();
        } else if (isMockUser) {
            //if mock user is active, get it from property json
            return createMockUser();
        }

        if (!this.supports(authentication.getClass())) {
            return null;
        } else if (authentication instanceof PendingOIDCAuthenticationToken) {
            return this.handleCodeFlow((PendingOIDCAuthenticationToken) authentication);
        } else if (authentication instanceof PreAuthenticatedAuthenticationToken) {
            return this.handleIntrospectionFlow((PreAuthenticatedAuthenticationToken) authentication);
        }

        throw new AuthenticationServiceException(String.format("Cannot process authentication of type [%s]", authentication.getClass().getName()));
    }

    private OIDCAuthenticationToken handleIntrospectionFlow(PreAuthenticatedAuthenticationToken authentication) {
        if (authentication == null) {
            throw new InvalidTokenException("Invalid token (token not found)");
        } else {
            String token = (String) authentication.getPrincipal();
            OAuth2Authentication auth = this.tokenServices.loadAuthentication(token);
            if (auth == null) {
                throw new InvalidTokenException("Invalid token: " + token);
            } else {
                JsonObject credentialJson = (JsonObject) auth.getUserAuthentication().getCredentials();
                UserInfo userInfo = DefaultUserInfo.fromJson(credentialJson);

                return new OIDCAuthenticationToken(userInfo.getSub(), credentialJson.get("iss").getAsString(), userInfo,
                        auth.getAuthorities(), null, token, null);
            }
        }
    }

    private OIDCAuthenticationToken handleCodeFlow(PendingOIDCAuthenticationToken authenticationToken) {

        JWT idToken = authenticationToken.getIdToken();
        try {
            UserInfo userInfo = this.extractUserInfo(idToken.getJWTClaimsSet().toString());
            return (OIDCAuthenticationToken) this.createAuthenticationToken(authenticationToken, this.authoritiesMapper.mapAuthorities(idToken, userInfo), userInfo);
        } catch (ParseException e) {
            throw new AuthenticationServiceException("Could not parse Id Token", e);
        }
    }

    private UserInfo extractUserInfo(String json) {
        UserInfo userInfo = DefaultUserInfo.fromJson(new Gson().fromJson(json, JsonObject.class));
        return userInfo;
    }

    private Authentication createMockUser() {
        DefaultUserInfo user = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create().fromJson(this.getConfigService().getString(EDCIConfig.Security.MOCK_USER_INFO), DefaultUserInfo.class);
        return new OIDCAuthenticationToken("mockuser", "mockIDP", user, null, null, null, null);
    }

    public void afterPropertiesSet() {
        Assert.state(this.tokenServices != null, "TokenServices are required");
    }

    private void checkClientDetails(OAuth2Authentication auth) {
        if (this.clientDetailsService != null) {
            ClientDetails client;
            try {
                client = this.clientDetailsService.loadClientByClientId(auth.getOAuth2Request().getClientId());
            } catch (ClientRegistrationException var6) {
                throw new OAuth2AccessDeniedException("Invalid token contains invalid client id");
            }

            Set<String> allowed = client.getScope();
            Iterator var4 = auth.getOAuth2Request().getScope().iterator();

            while (var4.hasNext()) {
                String scope = (String) var4.next();
                if (!allowed.contains(scope)) {
                    throw new OAuth2AccessDeniedException("Invalid token contains disallowed scope (" + scope + ") for this client");
                }
            }
        }

    }

    public boolean supports(Class<?> authentication) {
        return PendingOIDCAuthenticationToken.class.isAssignableFrom(authentication) || PreAuthenticatedAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
