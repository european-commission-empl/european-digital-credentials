package eu.europa.ec.empl.edci.datamodel.validation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ValidationError {

    private String errorKey;
    private String errorMessage;
    private List<String> messageVariables = new ArrayList<>();

    public ValidationError() {

    }

    public ValidationError(String errorKey) {
        this.setErrorKey(errorKey);
    }

    public ValidationError(String errorKey, String... messageVariables) {
        this.setErrorKey(errorKey);
        this.setMessageVariables(Arrays.asList(messageVariables));
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

    public List<String> getMessageVariables() {
        return messageVariables;
    }

    public void setMessageVariables(List<String> messageVariables) {
        this.messageVariables = messageVariables;
    }

    public static ValidationError fromMessage(String errorMessage) {
        ValidationError validationError = new ValidationError();
        validationError.setErrorMessage(errorMessage);
        return validationError;
    }

    @Override
    public String toString() {
        return getErrorMessage();
    }


}
