package eu.europa.ec.empl.edci.security.session;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import eu.europa.ec.empl.edci.constants.EDCIConstants;
import eu.europa.ec.empl.edci.constants.ErrorCode;
import eu.europa.ec.empl.edci.exception.EDCIException;
import eu.europa.ec.empl.edci.exception.ExceptionResponse;
import eu.europa.ec.empl.edci.service.EDCIMessageService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.session.InvalidSessionStrategy;
import org.springframework.security.web.util.UrlUtils;
import org.springframework.util.Assert;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;


public class EDCISimpleRedirectInvalidSessionStrategy implements InvalidSessionStrategy {
    private final Log logger = LogFactory.getLog(this.getClass());
    private final String destinationUrl;
    private final RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();
    private boolean createNewSession = true;
    private String redirectTo = "/home";
    @Autowired
    EDCIMessageService messageSource;

    public EDCISimpleRedirectInvalidSessionStrategy(String invalidSessionUrl) {
        Assert.isTrue(UrlUtils.isValidRedirectUrl(invalidSessionUrl), "url must start with '/' or with 'http(s)'");
        this.destinationUrl = invalidSessionUrl;
    }

    @Override
    public void onInvalidSessionDetected(HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (request.getHeader(EDCIConstants.HttpHeaders.X_REQUESTED_WITH) != null && request.getHeader(EDCIConstants.HttpHeaders.X_REQUESTED_WITH).equals(EDCIConstants.Security.XMLHttpRequest)) {
            writeExceptionInResponseBody(request, response, new EDCIException(HttpStatus.FORBIDDEN, ErrorCode.SESSION_EXPIRED, "exception.session.expired"), this.redirectTo);
        } else {
            request.getSession().invalidate();
            HttpSession newSession = request.getSession();
            this.redirectStrategy.sendRedirect(request, response, this.destinationUrl);
        }

    }

    public void setCreateNewSession(boolean createNewSession) {
        this.createNewSession = createNewSession;
    }

    private void writeExceptionInResponseBody(HttpServletRequest request, HttpServletResponse response, EDCIException ex, String redirectTo) {
        ExceptionResponse error = new ExceptionResponse();
        error.setHttpStatus(ex.getHttpStatus().value());
        error.setMessage(messageSource.getMessage(ex.getMessageKey(), ex.getMessageArgs()));
        error.setExceptionCode(ex.getCode().getCode());
        error.setRedirectTo(redirectTo);

        logger.error(error.getMessage(), ex);
        PrintWriter writer = null;

        try {
            writer = response.getWriter();
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setStatus(ex.getHttpStatus().value());
            writer.print(new ObjectMapper().writeValueAsString(error));
        } catch (JsonProcessingException e) {
            System.out.println("[ E ] - Error parsing JSON");
        } catch (IOException e) {
            System.out.println("[ E ] - Error writing response body");
        } finally {
            if (writer != null) {
                writer.flush();
                writer.close();
            }
        }

    }

    public String getDestinationUrl() {
        return destinationUrl;
    }

    public RedirectStrategy getRedirectStrategy() {
        return redirectStrategy;
    }

    public boolean isCreateNewSession() {
        return createNewSession;
    }

    public String getRedirectTo() {
        return redirectTo;
    }

    public void setRedirectTo(String redirectTo) {
        this.redirectTo = redirectTo;
    }
}
