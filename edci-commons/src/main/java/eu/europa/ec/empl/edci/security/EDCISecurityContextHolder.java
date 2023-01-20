package eu.europa.ec.empl.edci.security;

import eu.europa.ec.empl.edci.security.base.IEDCISecurityContextHolder;
import org.mitre.openid.connect.model.OIDCAuthenticationToken;
import org.mitre.openid.connect.model.UserInfo;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
public class EDCISecurityContextHolder implements IEDCISecurityContextHolder {

    @Override
    public UserInfo getOIDCUserInfo() {
        return this.getOIDCAuthentication() == null ? null : this.getOIDCAuthentication().getUserInfo();
    }

    @Override
    public String getSub() {
        String sub = "";

        if (this.getAuthentication() != null && this.getOIDCUserInfo() != null) {
            sub = ((OIDCAuthenticationToken) SecurityContextHolder.getContext().getAuthentication()).getUserInfo().getSub();
        }

        return sub;
    }

    @Override
    public Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }


    public OIDCAuthenticationToken getOIDCAuthentication() {
        return (getAuthentication() instanceof OIDCAuthenticationToken) ? (OIDCAuthenticationToken) SecurityContextHolder.getContext().getAuthentication() : null;
    }

    @Override
    public void setAuthentication(Authentication authentication) {
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }


}

