package eu.europa.ec.empl.edci.security.service;

import eu.europa.ec.empl.edci.security.EDCISecurityContextHolder;
import eu.europa.ec.empl.edci.security.service.base.IEDCIAuthenticationService;
import org.mitre.openid.connect.model.OIDCAuthenticationToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EDCIAuthenticationService implements IEDCIAuthenticationService {

    @Autowired
    private EDCISecurityContextHolder edciSecurityContextHolder;

    public EDCISecurityContextHolder getEdciSecurityContextHolder() {
        return edciSecurityContextHolder;
    }

    public void setEdciSecurityContextHolder(EDCISecurityContextHolder edciSecurityContextHolder) {
        this.edciSecurityContextHolder = edciSecurityContextHolder;
    }

    @Override
    public boolean isAuthenticated() {
        return this.getEdciSecurityContextHolder().getOIDCAuthentication() == null ? false : this.getEdciSecurityContextHolder().getOIDCAuthentication().isAuthenticated();
    }

    @Override
    public String getAccessToken() {
        return this.isAuthenticated() ? this.getEdciSecurityContextHolder().getOIDCAuthentication().getAccessTokenValue() : null;
    }

    @Override
    public String getRefreshToken() {
        return this.isAuthenticated() ? this.getEdciSecurityContextHolder().getOIDCAuthentication().getRefreshTokenValue() : null;
    }

    public void setUpdatedTokens(String issuer, String accessToken, String refreshToken) {
        OIDCAuthenticationToken currentAuth = this.getEdciSecurityContextHolder().getOIDCAuthentication();
        OIDCAuthenticationToken oidcAuthenticationToken = new OIDCAuthenticationToken(
                currentAuth.getSub(),
                issuer,
                currentAuth.getUserInfo(),
                currentAuth.getAuthorities(),
                currentAuth.getIdToken(),
                accessToken,
                refreshToken);
        this.getEdciSecurityContextHolder().setAuthentication(oidcAuthenticationToken);
    }

    public void setUpdatedAccessToken(String issuer, String accessToken) {
        OIDCAuthenticationToken currentAuth = this.getEdciSecurityContextHolder().getOIDCAuthentication();
        String currentRefreshToken = currentAuth.getRefreshTokenValue();
        this.setUpdatedTokens(issuer, accessToken, currentRefreshToken);
    }

    public OIDCAuthenticationToken getCurrentAuthentication() {
        return this.getEdciSecurityContextHolder().getOIDCAuthentication();
    }

    public void endSession() {
        this.getEdciSecurityContextHolder().setAuthentication(null);
    }
}


