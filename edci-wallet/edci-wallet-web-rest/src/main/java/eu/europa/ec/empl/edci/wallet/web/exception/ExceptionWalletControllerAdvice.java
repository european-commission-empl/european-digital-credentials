package eu.europa.ec.empl.edci.wallet.web.exception;

import eu.europa.ec.empl.edci.rest.ExceptionControllerAdvice;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.ControllerAdvice;

@ControllerAdvice
public class ExceptionWalletControllerAdvice extends ExceptionControllerAdvice {

    public static final Logger logger = LogManager.getLogger(ExceptionWalletControllerAdvice.class);

//    private String getJson(Object exceptionResponse) {
//        String jsonString = "";
//        try {
//            ObjectMapper mapper = new ObjectMapper();
//            mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
//            mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
//            jsonString = mapper.writeValueAsString(exceptionResponse);
//        } catch (JsonProcessingException e) {
//            logger.error(e.getMessage(), e);
//        }
//        return jsonString;
//    }

//    @Override
//    public <T> ResponseEntity<String> generateResponse(T body, MultiValueMap<String, String> headers, HttpStatus status) {
//        return new ResponseEntity<String>(getJson(body), headers, status);
//    }

}