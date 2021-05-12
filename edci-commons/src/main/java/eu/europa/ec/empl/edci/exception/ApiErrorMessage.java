package eu.europa.ec.empl.edci.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import eu.europa.ec.empl.edci.constants.ErrorCode;
import eu.europa.ec.empl.edci.constants.EuropassConstants;

import java.util.Date;

public class ApiErrorMessage {

    private String code = ErrorCode.UNDEFINED.getCode();
    private String message = "There's been an unexpected error";
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = EuropassConstants.DATE_ISO_8601)
    private Date timestamp = new Date();
    private String path;

    public ApiErrorMessage() {

    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

}