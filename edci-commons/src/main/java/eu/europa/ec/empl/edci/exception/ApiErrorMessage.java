package eu.europa.ec.empl.edci.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import eu.europa.ec.empl.edci.constants.EDCIConstants;
import eu.europa.ec.empl.edci.constants.ErrorCode;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ApiErrorMessage {

    private String code = ErrorCode.UNDEFINED.getCode();
    private String message = "There's been an unexpected error";
    private String description;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = EDCIConstants.DATE_ISO_8601)
    private Date timestamp = new Date();
    private String path;
    private Map<Object, Object> affectedAssets = new HashMap<Object, Object>();

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public Map<Object, Object> getAffectedAssets() {
        return affectedAssets;
    }

    public void setAffectedAssets(Map<Object, Object> affectedAssets) {
        this.affectedAssets = affectedAssets;
    }

    public void putAffectedAsset(Object key, Object value) {
        this.getAffectedAssets().put(key, value);
    }
}