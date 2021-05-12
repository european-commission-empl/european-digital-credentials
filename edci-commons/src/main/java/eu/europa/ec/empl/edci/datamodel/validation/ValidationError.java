package eu.europa.ec.empl.edci.datamodel.validation;

import java.util.HashMap;
import java.util.Map;

public class ValidationError {

    private String errorKey;
    private String errorMessage;
    private Map<String, Object> messageVariables = new HashMap<String, Object>();

    public ValidationError() {

    }

    public ValidationError(String errorKey) {
        this.setErrorKey(errorKey);
    }


    public String getErrorKey() {
        return errorKey;
    }

    public void setErrorKey(String errorKey) {
        this.errorKey = errorKey;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public Map<String, Object> getMessageVariables() {
        return messageVariables;
    }

    public void setMessageVariables(Map<String, Object> messageVariables) {
        this.messageVariables = messageVariables;
    }

    public void putMessageVariables(String key, Object value) {
        this.getMessageVariables().put(key, value);
    }
}
