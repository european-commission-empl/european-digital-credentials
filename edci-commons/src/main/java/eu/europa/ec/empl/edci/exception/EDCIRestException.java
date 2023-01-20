package eu.europa.ec.empl.edci.exception;

import eu.europa.ec.empl.edci.constants.ErrorCode;
import org.springframework.http.HttpStatus;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

public class EDCIRestException extends RuntimeException {

    private ErrorCode code;
    private String message;
    private Date timestamp;
    private String path;
    private HttpStatus httpStatus;

    public EDCIRestException(ApiErrorMessage apiErrorMessage, HttpStatus httpStatus) {
        this.code = apiErrorMessage.getCode() != null ? ErrorCode.fromCode(apiErrorMessage.getCode()) : ErrorCode.UNDEFINED;
        this.message = apiErrorMessage.getMessage();
        this.timestamp = apiErrorMessage.getTimestamp();
        this.path = apiErrorMessage.getPath();
        this.httpStatus = httpStatus;
    }

    public ApiErrorMessage toApiErrorMessage(HttpServletRequest req) {
        ApiErrorMessage error = new ApiErrorMessage();
        error.setCode(getCode() != null ? getCode().getCode() : ErrorCode.UNDEFINED.getCode());
        error.setMessage(message);
        error.setTimestamp(new Date());
        error.setPath(req.getPathInfo());
        return error;
    }

    public ErrorCode getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public String getPath() {
        return path;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

}