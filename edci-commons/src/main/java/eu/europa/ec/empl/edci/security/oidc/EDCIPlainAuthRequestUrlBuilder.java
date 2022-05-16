package eu.europa.ec.empl.edci.security.oidc;

import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import eu.europa.ec.empl.edci.config.service.IConfigService;
import eu.europa.ec.empl.edci.constants.EDCIConfig;
import org.apache.http.client.utils.URIBuilder;
import org.mitre.oauth2.model.RegisteredClient;
import org.mitre.openid.connect.client.service.AuthRequestUrlBuilder;
import org.mitre.openid.connect.config.ServerConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationServiceException;

import java.net.URISyntaxException;
import java.util.Iterator;
import java.util.Map;

public class EDCIPlainAuthRequestUrlBuilder implements AuthRequestUrlBuilder {

    @Autowired
    private IConfigService iConfigService;

    public EDCIPlainAuthRequestUrlBuilder() {
    }

    public String buildAuthRequestUrl(ServerConfiguration serverConfig, RegisteredClient clientConfig, String redirectUri, String nonce, String state, Map<String, String> options, String loginHint) {
        try {
            String baseURL = iConfigService.getString(EDCIConfig.Security.AUTH_REQUEST_URL, serverConfig.getAuthorizationEndpointUri());
            URIBuilder uriBuilder = new URIBuilder(baseURL);
            uriBuilder.addParameter("response_type", "code");
            uriBuilder.addParameter("client_id", clientConfig.getClientId());
            uriBuilder.addParameter("scope", Joiner.on(" ").join(clientConfig.getScope()));
            uriBuilder.addParameter("redirect_uri", redirectUri);
            uriBuilder.addParameter("nonce", nonce);
            uriBuilder.addParameter("state", state);
            Iterator var9 = options.entrySet().iterator();

            while (var9.hasNext()) {
                Map.Entry<String, String> option = (Map.Entry) var9.next();
                uriBuilder.addParameter((String) option.getKey(), (String) option.getValue());
            }

            if (!Strings.isNullOrEmpty(loginHint)) {
                uriBuilder.addParameter("login_hint", loginHint);
            }

            return uriBuilder.build().toString();
        } catch (URISyntaxException var11) {
            throw new AuthenticationServiceException("Malformed Authorization Endpoint Uri", var11);
        }
    }
}
