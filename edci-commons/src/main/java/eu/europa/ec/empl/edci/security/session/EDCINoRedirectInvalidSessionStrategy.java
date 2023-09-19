package eu.europa.ec.empl.edci.security.session;

import eu.europa.ec.empl.edci.exception.EDCIException;
import eu.europa.ec.empl.edci.service.EDCIMessageService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.util.UrlUtils;
import org.springframework.util.Assert;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class EDCINoRedirectInvalidSessionStrategy implements EDCISimpleInvalidSessionStrategy {

    private final Logger logger = LogManager.getLogger(EDCIRedirectInvalidSessionStrategy.class);
    private String redirectTo = "/home";
    @Autowired
    private EDCIMessageService messageService;

    public EDCINoRedirectInvalidSessionStrategy(String invalidSessionUrl) {
        Assert.isTrue(UrlUtils.isValidRedirectUrl(invalidSessionUrl), "url must start with '/' or with 'http(s)'");
        this.setRedirectTo(invalidSessionUrl);
    }

    @Override
    public void onNonApiRequestInvalidSessionDetected(HttpServletRequest request, HttpServletResponse response) {
        request.getSession().invalidate();
        request.getSession();
        try {
            String dispatcherPath = request.getPathInfo() != null ? request.getServletPath() + request.getPathInfo() : request.getServletPath();
            request.getRequestDispatcher(dispatcherPath).forward(request, response);
        } catch (ServletException | IOException e) {
            this.getLogger().error("error redirecting to %s after session invalid was detected");
            throw new EDCIException().addDescription("Could not redirect after session invalid detected");
        }
    }

    public void setMessageService(EDCIMessageService messageService) {
        this.messageService = messageService;
    }

    @Override
    public String getRedirectTo() {
        return this.redirectTo;
    }

    @Override
    public EDCIMessageService getMessageService() {
        return this.messageService;
    }

    @Override
    public Logger getLogger() {
        return this.logger;
    }

    public void setRedirectTo(String redirectTo) {
        this.redirectTo = redirectTo;
    }
}
