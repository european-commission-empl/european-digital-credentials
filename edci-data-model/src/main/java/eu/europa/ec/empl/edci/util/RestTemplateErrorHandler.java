package eu.europa.ec.empl.edci.util;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import eu.europa.ec.empl.edci.exception.ApiErrorMessage;
import eu.europa.ec.empl.edci.exception.EDCIException;
import eu.europa.ec.empl.edci.exception.EDCIRestException;
import eu.europa.ec.empl.edci.exception.OIDCException;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class RestTemplateErrorHandler implements ResponseErrorHandler {

    public static final Logger logger = LogManager.getLogger(RestTemplateErrorHandler.class);

    @Override
    public boolean hasError(ClientHttpResponse response) throws IOException {
        return (response.getStatusCode().series() == HttpStatus.Series.CLIENT_ERROR || response.getStatusCode().series() == HttpStatus.Series.SERVER_ERROR);
    }

    @Override
    public void handleError(ClientHttpResponse response) throws IOException {
        try (InputStream iS = response.getBody()) {
            String bodyString = IOUtils.toString(iS, StandardCharsets.UTF_8.name());
            try {
                ApiErrorMessage apiErrorMessage = new ObjectMapper().readValue(bodyString, ApiErrorMessage.class);
                throw new EDCIRestException(apiErrorMessage, response.getStatusCode());
            } catch (JsonParseException | JsonMappingException e) {
                logger.error(e);
                try {
                    OIDCException oidcError = new ObjectMapper().readValue(bodyString, OIDCException.class);
                    throw oidcError;
                } catch (JsonParseException | JsonMappingException ex) {
                    logger.error(ex);
                    throw new EDCIException();
                }
            }
        }
    }
}
