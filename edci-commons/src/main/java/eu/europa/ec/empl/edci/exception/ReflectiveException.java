package eu.europa.ec.empl.edci.exception;

import eu.europa.ec.empl.edci.constants.ErrorCode;
import org.springframework.http.HttpStatus;

public class ReflectiveException extends EDCIException {

    public ReflectiveException(String msgKey, String... msgArgs) {
        super(HttpStatus.INTERNAL_SERVER_ERROR, ErrorCode.REFLECTION, msgKey, msgArgs);
    }

}
