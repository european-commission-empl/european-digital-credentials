package eu.europa.ec.empl.edci.util;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.ObjectMapper;
import eu.europa.ec.empl.edci.exception.ApiErrorMessage;
import eu.europa.ec.empl.edci.exception.EDCIException;
import eu.europa.ec.empl.edci.exception.EDCIRestException;
import org.apache.commons.io.IOUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class RestTemplateErrorHandler implements ResponseErrorHandler {

    public static final org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(RestTemplateErrorHandler.class);

    @Override
    public boolean hasError(ClientHttpResponse response) throws IOException {
        return (response.getStatusCode().series() == HttpStatus.Series.CLIENT_ERROR || response.getStatusCode().series() == HttpStatus.Series.SERVER_ERROR);
    }

    @Override
    public void handleError(ClientHttpResponse response) throws IOException {
        try {
            ApiErrorMessage apiErrorMessage = new ObjectMapper().readValue(IOUtils.toString(response.getBody(), StandardCharsets.UTF_8.name()), ApiErrorMessage.class);
            throw new EDCIRestException(apiErrorMessage, response.getStatusCode());
        } catch (JsonParseException e) {
            logger.error(e);
            throw new EDCIException();
        }
    }
}
