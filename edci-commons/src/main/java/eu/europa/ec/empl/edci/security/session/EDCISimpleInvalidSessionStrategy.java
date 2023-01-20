package eu.europa.ec.empl.edci.security.session;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import eu.europa.ec.empl.edci.constants.EDCIConstants;
import eu.europa.ec.empl.edci.constants.ErrorCode;
import eu.europa.ec.empl.edci.exception.EDCIException;
import eu.europa.ec.empl.edci.exception.ExceptionResponse;
import eu.europa.ec.empl.edci.service.EDCIMessageService;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.web.session.InvalidSessionStrategy;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public interface EDCISimpleInvalidSessionStrategy extends InvalidSessionStrategy {

    void onNonApiRequestInvalidSessionDetected(HttpServletRequest request, HttpServletResponse response);

    String getRedirectTo();

    EDCIMessageService getMessageService();

    Logger getLogger();

    default void onInvalidSessionDetected(HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (request.getHeader(EDCIConstants.HttpHeaders.X_REQUESTED_WITH) != null && request.getHeader(EDCIConstants.HttpHeaders.X_REQUESTED_WITH).equals(EDCIConstants.Security.XMLHttpRequest)) {
            writeExceptionInResponseBody(request, response, new EDCIException(HttpStatus.FORBIDDEN, ErrorCode.SESSION_EXPIRED, "exception.session.expired"), this.getRedirectTo());
        } else {
            this.onNonApiRequestInvalidSessionDetected(request, response);
        }

    }

    default void writeExceptionInResponseBody(HttpServletRequest request, HttpServletResponse response, EDCIException ex, String redirectTo) {
        ExceptionResponse error = new ExceptionResponse();
        error.setHttpStatus(ex.getHttpStatus().value());
        error.setMessage(this.getMessageService().getMessage(ex.getMessageKey(), ex.getMessageArgs()));
        error.setExceptionCode(ex.getCode().getCode());
        error.setRedirectTo(redirectTo);

        this.getLogger().error(error.getMessage(), ex);
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

}
