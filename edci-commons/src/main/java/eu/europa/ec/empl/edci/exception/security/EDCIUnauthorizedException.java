package eu.europa.ec.empl.edci.exception.security;

import eu.europa.ec.empl.edci.constants.ErrorCode;
import eu.europa.ec.empl.edci.exception.EDCIException;
import org.springframework.http.HttpStatus;

public class EDCIUnauthorizedException extends EDCIException {
    private final static String MSG = "exception.client.error.msg.unauthorized";
    private final static HttpStatus STATUS = HttpStatus.UNAUTHORIZED;

    public EDCIUnauthorizedException() {
        super(STATUS, MSG);
    }

    public EDCIUnauthorizedException(ErrorCode code) {
        super(STATUS, code, MSG);
    }

    public EDCIUnauthorizedException(String msgKey, String... msgArgs) {
        super(STATUS, msgKey, msgArgs);
    }

    public EDCIUnauthorizedException(ErrorCode code, String msgKey, String... msgArgs) {
        super(STATUS, code, msgKey, msgArgs);
    }

}
