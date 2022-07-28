package eu.europa.ec.empl.edci.security.session;

import eu.europa.ec.empl.edci.exception.EDCIException;
import eu.europa.ec.empl.edci.service.EDCIMessageService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.util.UrlUtils;
import org.springframework.util.Assert;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

public class EDCIRedirectInvalidSessionStrategy implements EDCISimpleInvalidSessionStrategy {
    private final Logger logger = LogManager.getLogger(EDCIRedirectInvalidSessionStrategy.class);
    private final RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();
    private String redirectTo = "/home";
    @Autowired
    EDCIMessageService messageService;

    public EDCIRedirectInvalidSessionStrategy(String invalidSessionUrl) {
        Assert.isTrue(UrlUtils.isValidRedirectUrl(invalidSessionUrl), "url must start with '/' or with 'http(s)'");
        this.setRedirectTo(invalidSessionUrl);
    }

    @Override
    public void onNonApiRequestInvalidSessionDetected(HttpServletRequest request, HttpServletResponse response) {
        request.getSession().invalidate();
        HttpSession newSession = request.getSession();
        try {
            this.redirectStrategy.sendRedirect(request, response, this.getRedirectTo());
        } catch (IOException e) {
            this.getLogger().error("error redirecting to %s after session invalid was detected");
            throw new EDCIException().addDescription("Could not redirect after session invalid detected");
        }

    }

    @Override
    public Logger getLogger() {
        return this.logger;
    }

    public EDCIMessageService getMessageService() {
        return messageService;
    }

    public void setMessageService(EDCIMessageService messageService) {
        this.messageService = messageService;
    }

    public RedirectStrategy getRedirectStrategy() {
        return redirectStrategy;
    }

    public String getRedirectTo() {
        return redirectTo;
    }

    public void setRedirectTo(String redirectTo) {
        this.redirectTo = redirectTo;
    }
}
