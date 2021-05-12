package eu.europa.ec.empl.edci.exception.clientErrors;

import eu.europa.ec.empl.edci.constants.ErrorCode;
import eu.europa.ec.empl.edci.exception.EDCIException;
import org.springframework.http.HttpStatus;

public class EDCIBadRequestException extends EDCIException {

    private final static String MSG = "exception.client.error.msg.bad.request";
    private final static HttpStatus STATUS = HttpStatus.BAD_REQUEST;

    public EDCIBadRequestException() {
        super(STATUS, MSG);
    }

    public EDCIBadRequestException(ErrorCode code) {
        super(STATUS, code, MSG);
    }

    public EDCIBadRequestException(String msgKey, String... msgArgs) {
        super(STATUS, msgKey, msgArgs);
    }

    public EDCIBadRequestException(ErrorCode code, String msgKey, String... msgArgs) {
        super(STATUS, code, msgKey, msgArgs);
    }

}
