package eu.europa.ec.empl.edci.exception.security;

import eu.europa.ec.empl.edci.constants.ErrorCode;
import eu.europa.ec.empl.edci.exception.EDCIException;
import org.springframework.http.HttpStatus;

public class EDCIForbiddenException extends EDCIException {

    private final static String MSG = "exception.client.error.msg.forbidden";
    private final static HttpStatus STATUS = HttpStatus.FORBIDDEN;

    public EDCIForbiddenException() {
        super(STATUS, MSG);
    }

    public EDCIForbiddenException(ErrorCode code) {
        super(STATUS, code, MSG);
    }

    public EDCIForbiddenException(String msgKey, String... msgArgs) {
        super(STATUS, msgKey, msgArgs);
    }

    public EDCIForbiddenException(ErrorCode code, String msgKey, String... msgArgs) {
        super(STATUS, code, msgKey, msgArgs);
    }
}
