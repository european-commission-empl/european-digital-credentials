package eu.europa.ec.empl.edci.exception;

import eu.europa.ec.empl.edci.constants.ErrorCode;
import eu.europa.ec.empl.edci.service.EDCIMessageService;
import org.springframework.http.HttpStatus;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

public class EDCIException extends RuntimeException implements I18NException {

    private HttpStatus httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
    private ErrorCode code = ErrorCode.UNDEFINED;
    private String messageKey = "global.internal.error";
    private String[] messageArgs = new String[]{};
    private String description;

    public EDCIException() {
    }

    public EDCIException(Throwable cause) {
        initCause(cause);
    }

    public EDCIException(HttpStatus httpStatus, ErrorCode code) {
        this.httpStatus = httpStatus;
        this.code = code;
    }

    public EDCIException(HttpStatus httpStatus, ErrorCode code, String messageKey, String... messageArgs) {
        this.httpStatus = httpStatus;
        this.code = code;
        this.messageKey = messageKey;
        this.messageArgs = messageArgs;
    }

    public EDCIException(ErrorCode code, String messageKey, String... messageArgs) {
        this.code = code;
        this.messageKey = messageKey;
        this.messageArgs = messageArgs;
    }

    public EDCIException(HttpStatus httpStatus, String messageKey, String... messageArgs) {
        this.httpStatus = httpStatus;
        this.messageKey = messageKey;
        this.messageArgs = messageArgs;
    }

    public EDCIException(String messageKey, String... messageArgs) {
        this.messageKey = messageKey;
        this.messageArgs = messageArgs;
    }

    public ApiErrorMessage toApiErrorMessage(EDCIMessageService messageSource, HttpServletRequest req) {
        ApiErrorMessage error = new ApiErrorMessage();
        error.setCode(getCode() != null ? getCode().getCode() : ErrorCode.UNDEFINED.getCode());
        error.setMessage(messageSource.getMessage(getMessageKey(), getMessageArgs()));
        error.setTimestamp(new Date());
        error.setPath(req.getPathInfo());
        return error;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public ErrorCode getCode() {
        return code;
    }

    @Override
    public String getMessageKey() {
        return messageKey;
    }

    @Override
    public String[] getMessageArgs() {
        return messageArgs;
    }

    public String getDescription() {
        return description;
    }

    public EDCIException setCause(Throwable cause) {
        initCause(cause);
        return this;
    }

    public EDCIException addDescription(String description) {
        this.description = description;
        return this;
    }
}
