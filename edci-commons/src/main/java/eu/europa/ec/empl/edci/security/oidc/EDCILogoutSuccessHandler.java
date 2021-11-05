package eu.europa.ec.empl.edci.security.oidc;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import eu.europa.ec.empl.edci.config.service.IConfigService;
import eu.europa.ec.empl.edci.constants.EDCIConfig;
import eu.europa.ec.empl.edci.security.model.dto.LogOutResponseDTO;
import org.apache.http.HttpStatus;
import org.mitre.openid.connect.client.service.ServerConfigurationService;
import org.mitre.openid.connect.model.DefaultUserInfo;
import org.mitre.openid.connect.model.OIDCAuthenticationToken;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.authentication.logout.SimpleUrlLogoutSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;

@Component
public class EDCILogoutSuccessHandler extends SimpleUrlLogoutSuccessHandler implements LogoutSuccessHandler {

    private IConfigService configService;

    public ServerConfigurationService serverConfigurationService;

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        if (authentication instanceof OIDCAuthenticationToken) {
            OIDCAuthenticationToken oidcAuthenticationToken = (OIDCAuthenticationToken) authentication;
            String redirectUrl = "";
            if (!oidcAuthenticationToken.getUserInfo().getSub().equals(this.createMockUser().getUserInfo().getSub())) {
                String issuer = oidcAuthenticationToken.getIssuer();
                String endSessionEndpoint = this.getConfigService().getString(EDCIConfig.Security.IDP_END_SESSION_URL, null);
                try {
                    endSessionEndpoint = serverConfigurationService.getServerConfiguration(issuer).getEndSessionEndpoint();

                } catch (Exception e) {
                    logger.error("[D] - Could not find a end session URL from server configuration");
                }

                URI uri = UriComponentsBuilder.fromUriString(endSessionEndpoint)
                        .queryParam("id_token_hint", oidcAuthenticationToken.getIdToken().serialize())
                        .queryParam("post_logout_redirect_uri", this.getConfigService().getString(EDCIConfig.Security.POST_LOGOUT_URL)).build().encode().toUri();

                redirectUrl = uri.toString();
            } else {
                redirectUrl = this.getConfigService().getString(EDCIConfig.APP_CONTEXT_ROOT);
            }

            LogOutResponseDTO logOutResponse = new LogOutResponseDTO(redirectUrl);
            String jsonString = new Gson().toJson(logOutResponse);
            response.setStatus(HttpStatus.SC_OK);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.getWriter().write(jsonString);
            response.setContentLength(jsonString.length());
            response.getWriter().flush();
        }
    }


    public ServerConfigurationService getServerConfigurationService() {
        return serverConfigurationService;
    }

    public void setServerConfigurationService(ServerConfigurationService serverConfigurationService) {
        this.serverConfigurationService = serverConfigurationService;
    }

    public IConfigService getConfigService() {
        return configService;
    }

    public void setConfigService(IConfigService configService) {
        this.configService = configService;
    }

    private OIDCAuthenticationToken createMockUser() {
        DefaultUserInfo user = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create().fromJson(this.getConfigService().getString(EDCIConfig.Security.MOCK_USER_INFO), DefaultUserInfo.class);
        return new OIDCAuthenticationToken("mockuser", "mockIDP", user, null, null, null, null);
    }

}
