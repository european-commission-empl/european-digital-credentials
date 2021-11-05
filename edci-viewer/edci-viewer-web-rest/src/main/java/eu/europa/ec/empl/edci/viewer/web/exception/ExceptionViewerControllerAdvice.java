package eu.europa.ec.empl.edci.viewer.web.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import eu.europa.ec.empl.edci.rest.ExceptionControllerAdvice;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.ControllerAdvice;

import java.text.SimpleDateFormat;

@ControllerAdvice
public class ExceptionViewerControllerAdvice extends ExceptionControllerAdvice {

    public static final org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(ExceptionViewerControllerAdvice.class);

    private String getJson(Object exceptionResponse) {
        String jsonString = "";
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
            mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
            jsonString = mapper.writeValueAsString(exceptionResponse);
        } catch (JsonProcessingException e) {
            logger.error(e.getMessage(), e);
        }
        return jsonString;
    }

    @Override
    public <T> ResponseEntity<String> generateResponse(T body, MultiValueMap<String, String> headers, HttpStatus status) {
        return new ResponseEntity<String>(getJson(body), headers, status);
    }
}