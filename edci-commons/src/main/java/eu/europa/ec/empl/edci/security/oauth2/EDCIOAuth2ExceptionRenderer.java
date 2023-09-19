package eu.europa.ec.empl.edci.security.oauth2;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.*;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.security.oauth2.provider.error.DefaultOAuth2ExceptionRenderer;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.ServletWebRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class EDCIOAuth2ExceptionRenderer extends DefaultOAuth2ExceptionRenderer {

    private final Log logger = LogFactory.getLog(EDCIOAuth2ExceptionRenderer.class);


    //Force to use JSON mapping in exception rendering
    private void writeWithCustomMessageConverters(Object returnValue, HttpInputMessage inputMessage, HttpOutputMessage outputMessage) throws IOException, HttpMediaTypeNotAcceptableException {
        List<HttpMessageConverter> httpMessageConverters = Arrays.asList(new MappingJackson2HttpMessageConverter());

        for (HttpMessageConverter httpMessageConverter : httpMessageConverters) {
            try {
                MediaType contentType = (MediaType) httpMessageConverter.getSupportedMediaTypes().stream().findFirst().get();
                httpMessageConverter.write(returnValue, contentType, outputMessage);
                this.logger.debug("Written [" + returnValue + "] as \"" + contentType + "\" using [" + httpMessageConverter + "]");
            } catch (Exception e) {
                this.logger.error("Error writing authentication exception", e);
            }
        }
    }

    public void handleHttpEntityResponse(HttpEntity<?> responseEntity, ServletWebRequest webRequest) throws Exception {
        if (responseEntity != null) {
            HttpInputMessage inputMessage = this.createHttpInputMessage(webRequest);
            HttpOutputMessage outputMessage = this.createHttpOutputMessage(webRequest);
            if (responseEntity instanceof ResponseEntity && outputMessage instanceof ServerHttpResponse) {
                ((ServerHttpResponse) outputMessage).setStatusCode(((ResponseEntity) responseEntity).getStatusCode());
            }

            HttpHeaders entityHeaders = responseEntity.getHeaders();
            if (!entityHeaders.isEmpty()) {
                outputMessage.getHeaders().putAll(entityHeaders);
            }

            Object body = responseEntity.getBody();
            if (body != null) {
                this.writeWithCustomMessageConverters(body, inputMessage, outputMessage);
            } else {
                outputMessage.getBody();
            }

        }
    }

    private HttpInputMessage createHttpInputMessage(NativeWebRequest webRequest) throws Exception {
        HttpServletRequest servletRequest = (HttpServletRequest) webRequest.getNativeRequest(HttpServletRequest.class);
        return new ServletServerHttpRequest(servletRequest);
    }

    private HttpOutputMessage createHttpOutputMessage(NativeWebRequest webRequest) throws Exception {
        HttpServletResponse servletResponse = (HttpServletResponse) webRequest.getNativeResponse();
        return new ServletServerHttpResponse(servletResponse);
    }

}

