package eu.europa.ec.empl.edci.dss.exception;


import eu.europa.ec.empl.edci.dss.constants.ErrorCode;
import eu.europa.ec.empl.edci.dss.service.messages.EsealCoreMessageService;
import org.springframework.http.HttpStatus;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

public class ESealException extends RuntimeException implements I18NException {

    private HttpStatus httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
    private ErrorCode code = ErrorCode.UNDEFINED;
    private String messageKey = "global.internal.error";
    private String[] messageArgs = new String[]{};
    private String description;

    public ESealException() {
    }

    public ESealException(Throwable cause) {
        initCause(cause);
    }

    public ESealException(ErrorCode fullCode, String... messageArgs) {
        this.httpStatus = fullCode.getHttpStatus();
        this.code = fullCode;
        this.messageKey = fullCode.getLabelKey();
        this.messageArgs = messageArgs;
    }

    public ESealException(HttpStatus httpStatus, ErrorCode code) {
        this.httpStatus = httpStatus;
        this.code = code;
    }

    public ESealException(HttpStatus httpStatus, ErrorCode code, String messageKey, String... messageArgs) {
        this.httpStatus = httpStatus;
        this.code = code;
        this.messageKey = messageKey;
        this.messageArgs = messageArgs;
    }

    public ESealException(HttpStatus httpStatus, String messageKey, String... messageArgs) {
        this.httpStatus = httpStatus;
        this.messageKey = messageKey;
        this.messageArgs = messageArgs;
    }

    public ESealException(String messageKey, String... messageArgs) {
        this.messageKey = messageKey;
        this.messageArgs = messageArgs;
    }

    public ESealApiErrorMessage toApiErrorMessage(EsealCoreMessageService messageSource, HttpServletRequest req) {
        ESealApiErrorMessage error = new ESealApiErrorMessage();
        error.setCode(getCode() != null ? getCode().getCode() : ErrorCode.UNDEFINED.getCode());
        error.setMessage(messageSource.getMessage(getMessageKey(), getMessageArgs()));
        error.setTimestamp(new Date());
        error.setPath(req.getPathInfo());
        error.setDescription(this.getDescription());
        return error;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public ErrorCode getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return this.getMessageKey();
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

    public ESealException setCause(Throwable cause) {
        initCause(cause);
        return this;
    }

    public ESealException addDescription(String description) {
        this.description = description;
        return this;
    }
}
