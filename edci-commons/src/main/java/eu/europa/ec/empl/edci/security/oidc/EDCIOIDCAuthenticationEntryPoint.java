package eu.europa.ec.empl.edci.security.oidc;

import eu.europa.ec.empl.edci.security.oauth2.EDCIOAuth2AuthenticationEntryPoint;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.security.core.AuthenticationException;
import org.springframework.util.Assert;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

public class EDCIOIDCAuthenticationEntryPoint extends EDCIOAuth2AuthenticationEntryPoint implements InitializingBean {
    private Set<String> loginRedirectRegexes = new HashSet<String>();
    private String loginUrl;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        //If request matches any regex, redirect to login URL
        for (String regex : loginRedirectRegexes) {
            if (Pattern.matches(regex, request.getRequestURI())) {
                response.sendRedirect(loginUrl);
            }
        }

        super.commence(request, response, authException);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.state(!this.loginRedirectRegexes.isEmpty(), "loginRedirectRegexes are required");
        Assert.state(!this.loginUrl.isEmpty(), "loginUrl is required");
    }
}
