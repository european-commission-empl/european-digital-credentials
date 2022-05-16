package eu.europa.ec.empl.edci.exception.clientErrors;

import eu.europa.ec.empl.edci.constants.ErrorCode;
import eu.europa.ec.empl.edci.exception.EDCIException;
import org.springframework.http.HttpStatus;

public class EDCINotFoundException extends EDCIException {

    private final static String MSG = "exception.client.error.msg.not.found";
    private final static HttpStatus STATUS = HttpStatus.NOT_FOUND;

    public EDCINotFoundException() {
        super(STATUS, MSG);
    }

    public EDCINotFoundException(ErrorCode code) {
        super(STATUS, code, MSG);
    }

    public EDCINotFoundException(String msgKey, String... msgArgs) {
        super(STATUS, msgKey, msgArgs);
    }

    public EDCINotFoundException(ErrorCode code, String msgKey, String... msgArgs) {
        super(STATUS, code, msgKey, msgArgs);
    }
}
