package eu.europa.ec.empl.edci.datamodel.validation;

import org.apache.jena.shacl.validation.ReportEntry;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ValidationResult {

    private boolean valid;
    private List<ValidationError> validationErrors = new ArrayList<ValidationError>();

    public ValidationResult() {

    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public List<ValidationError> getValidationErrors() {
        return validationErrors;
    }

    public List<String> getErrorKeys() {
        return this.getInnerStringList((validationError -> validationError.getErrorKey()));
    }

    public List<String> getErrorMessages() {
        return this.getInnerStringList((validationError -> validationError.getErrorMessage()));
    }

    public List<String> getDistinctErrorMessages() {
        return this.getErrorMessages().stream().distinct().collect(Collectors.toList());
    }

    public void setValidationErrors(List<ValidationError> validationErrors) {
        this.validationErrors = validationErrors;
    }

    public void addValidationErrorMessage(String messageValue) {
        ValidationError validationError = new ValidationError();
        validationError.setErrorMessage(messageValue);
        this.getValidationErrors().add(validationError);
    }

    public void addValidationErrorMessage(String messageValue, ReportEntry entry) {
        ValidationError validationError = new ValidationError();
        validationError.setErrorMessage(messageValue);
        try {
            validationError.setNodeValue(entry.focusNode().toString());
            validationError.setConstraint(entry.constraint().toString());
            validationError.setPath(entry.resultPath().toString());
        } catch (Exception e) {
            //Silent exception
        }
        this.getValidationErrors().add(validationError);
    }

    public void addValidationError(String messageKey) {
        ValidationError validationError = new ValidationError(messageKey);
        this.getValidationErrors().add(validationError);
    }

    public void addValidationError(ValidationError validationError) {
        this.getValidationErrors().add(validationError);
    }

    public void addValidationErrors(List<ValidationError> validationErrors) {
        this.getValidationErrors().addAll(validationErrors);
    }

    private List<String> getInnerStringList(Function<ValidationError, String> function) {
        return this.getValidationErrors().stream().map(function).collect(Collectors.toList());
    }

}
