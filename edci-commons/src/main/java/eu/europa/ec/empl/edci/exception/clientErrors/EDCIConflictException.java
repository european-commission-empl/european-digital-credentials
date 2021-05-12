package eu.europa.ec.empl.edci.exception.clientErrors;

import eu.europa.ec.empl.edci.constants.ErrorCode;
import eu.europa.ec.empl.edci.exception.EDCIException;
import org.springframework.http.HttpStatus;

public class EDCIConflictException extends EDCIException {

    private final static String MSG = "exception.client.error.msg.conflict";
    private final static HttpStatus STATUS = HttpStatus.CONFLICT;

    public EDCIConflictException() {
        super(STATUS, MSG);
    }

    public EDCIConflictException(ErrorCode code) {
        super(STATUS, code, MSG);
    }

    public EDCIConflictException(String msgKey, String... msgArgs) {
        super(STATUS, msgKey, msgArgs);
    }

    public EDCIConflictException(ErrorCode code, String msgKey, String... msgArgs) {
        super(STATUS, code, msgKey, msgArgs);
    }
}
