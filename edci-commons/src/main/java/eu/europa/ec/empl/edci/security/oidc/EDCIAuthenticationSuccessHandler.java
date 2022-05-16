package eu.europa.ec.empl.edci.security.oidc;

import eu.europa.ec.empl.edci.constants.EDCIParameter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class EDCIAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private static final Log logger = LogFactory.getLog(EDCIAuthenticationSuccessHandler.class);

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        logger.debug(String.format("User Authentication Success [%s]", authentication.toString()));

        String targetURl = (String) request.getSession().getAttribute(EDCIParameter.REDIRECTURI);
        if (targetURl != null) {
            logger.debug(String.format("Redirecting to [%s]", targetURl));
            getRedirectStrategy().sendRedirect(request, response, targetURl);
        } else {
            logger.debug(String.format("No targetURL found, should redirect to [%s]", this.getDefaultTargetUrl()));
            super.onAuthenticationSuccess(request, response, authentication);
        }
    }
}
