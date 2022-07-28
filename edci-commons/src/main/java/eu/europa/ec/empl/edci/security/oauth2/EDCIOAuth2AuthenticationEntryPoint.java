package eu.europa.ec.empl.edci.security.oauth2;

import eu.europa.ec.empl.edci.constants.ErrorCode;
import eu.europa.ec.empl.edci.exception.EDCIException;
import eu.europa.ec.empl.edci.exception.security.EDCIForbiddenException;
import eu.europa.ec.empl.edci.exception.security.EDCIUnauthorizedException;
import eu.europa.ec.empl.edci.rest.ExceptionControllerAdvice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.provider.error.DefaultWebResponseExceptionTranslator;
import org.springframework.security.oauth2.provider.error.OAuth2AuthenticationEntryPoint;
import org.springframework.security.oauth2.provider.error.OAuth2ExceptionRenderer;
import org.springframework.security.oauth2.provider.error.WebResponseExceptionTranslator;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.mvc.support.DefaultHandlerExceptionResolver;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component("EDCIOAuth2AuthenticationEntryPoint")
public class EDCIOAuth2AuthenticationEntryPoint extends OAuth2AuthenticationEntryPoint {

    private WebResponseExceptionTranslator<?> exceptionTranslator = new DefaultWebResponseExceptionTranslator();

    @Autowired
    private ExceptionControllerAdvice exceptionControllerAdvice;
    private OAuth2ExceptionRenderer exceptionRenderer = new EDCIOAuth2ExceptionRenderer();
    private HandlerExceptionResolver handlerExceptionResolver = new DefaultHandlerExceptionResolver();

    public WebResponseExceptionTranslator<?> getExceptionTranslator() {
        return exceptionTranslator;

    }

    @Override
    public void setExceptionTranslator(WebResponseExceptionTranslator<?> exceptionTranslator) {
        this.exceptionTranslator = exceptionTranslator;
    }


    public ExceptionControllerAdvice getExceptionControllerAdvice() {
        return exceptionControllerAdvice;
    }

    public void setExceptionControllerAdvice(ExceptionControllerAdvice exceptionControllerAdvice) {
        this.exceptionControllerAdvice = exceptionControllerAdvice;
    }


    public OAuth2ExceptionRenderer getExceptionRenderer() {
        return exceptionRenderer;
    }

    @Override
    public void setExceptionRenderer(OAuth2ExceptionRenderer exceptionRenderer) {
        this.exceptionRenderer = exceptionRenderer;
    }

    public HandlerExceptionResolver getHandlerExceptionResolver() {
        return handlerExceptionResolver;
    }

    public void setHandlerExceptionResolver(HandlerExceptionResolver handlerExceptionResolver) {
        this.handlerExceptionResolver = handlerExceptionResolver;
    }

    //Override to get exception objects from exceptionControllerAdvice
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        ResponseEntity<?> result = null;

        try {
            //Translate AuthException and add headers
            result = this.getExceptionTranslator().translate(authException);
            result = this.enhanceResponse(result, authException);
            //Get Exception object from controllerAdvice depending on status code
            if (result.getStatusCode().equals(HttpStatus.FORBIDDEN)) {
                result = this.getExceptionControllerAdvice().handleForbiddenException(request, new EDCIForbiddenException(ErrorCode.FORBIDDEN));
            } else if (result.getStatusCode().equals(HttpStatus.UNAUTHORIZED)) {
                result = this.getExceptionControllerAdvice().handleUnauthorizedException(request, new EDCIUnauthorizedException(ErrorCode.UNAUTHORIZED));
            }
        } catch (Exception e) {
            result = this.getExceptionControllerAdvice().handleOtherExceptions(request, new EDCIException());
        }
        //Render the resulting exception
        try {
            this.getExceptionRenderer().handleHttpEntityResponse(result, new ServletWebRequest(request, response));
            response.flushBuffer();
        } catch (Exception e) {
            this.getHandlerExceptionResolver().resolveException(request, response, this, e);
        }


    }
}
