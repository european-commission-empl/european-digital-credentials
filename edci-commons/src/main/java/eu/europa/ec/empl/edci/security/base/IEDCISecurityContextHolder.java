package eu.europa.ec.empl.edci.security.base;

import org.mitre.openid.connect.model.OIDCAuthenticationToken;
import org.mitre.openid.connect.model.UserInfo;
import org.springframework.security.core.Authentication;

public interface IEDCISecurityContextHolder {

    String getSub();

    OIDCAuthenticationToken getOIDCAuthentication();

    Authentication getAuthentication();

    UserInfo getOIDCUserInfo();

    void setAuthentication(Authentication authentication);
}

