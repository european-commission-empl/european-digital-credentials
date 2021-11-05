package eu.europa.ec.empl.edci.datamodel.validation;

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

    public void addValidationError(ValidationError validationError) {
        this.getValidationErrors().add(validationError);
    }

    private List<String> getInnerStringList(Function<ValidationError, String> function) {
        return this.getValidationErrors().stream().map(function).collect(Collectors.toList());
    }

}
