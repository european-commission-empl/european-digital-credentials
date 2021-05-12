package eu.europa.ec.empl.edci.service;

import eu.europa.ec.empl.edci.datamodel.validation.ValidationError;
import eu.europa.ec.empl.edci.datamodel.validation.ValidationResult;
import eu.europa.ec.empl.edci.util.EDCIValidationUtil;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import javax.validation.groups.Default;
import java.util.List;
import java.util.Set;

@Service
public class EDCIValidationService {

    private static final Logger logger = Logger.getLogger(EDCIValidationService.class);

    @Autowired
    private EDCIMessageService edciMessageService;

    @Autowired
    private EDCIValidationUtil edciValidationUtil;


    public <T> ValidationResult validateAndLocalize(T object, Class<? extends Default>... groups) {
        ValidationResult validationResult = validate(object, groups);

        if (!validationResult.isValid()) {
            this.getEdciValidationUtil().loadLocalizedMessages(validationResult);
        }

        return validationResult;
    }

    public <T> ValidationResult validate(T object, Class<? extends Default>... groups) {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();

        Set<ConstraintViolation<T>> constraintViolations = validator.validate(object, groups);
        ValidationResult validationResult = new ValidationResult();
        validationResult.setValid(constraintViolations.isEmpty());

        if (!validationResult.isValid()) {
            for (ConstraintViolation<T> constraintViolation : constraintViolations) {
                logger.info(String.format("Unvalid field at: [%s]", constraintViolation.getPropertyPath()));
                ValidationError validationError = new ValidationError();
                validationError.setErrorKey(constraintViolation.getMessage());
                validationError.setMessageVariables(this.getEdciValidationUtil().getMessageVariables(constraintViolation));
                validationResult.addValidationError(validationError);
            }
        }

        return validationResult;
    }

    public List<String> getLocalizedMessages(ValidationResult validationResult) {
        this.getEdciValidationUtil().loadLocalizedMessages(validationResult);
        return validationResult.getErrorMessages();
    }


    public EDCIMessageService getEdciMessageService() {
        return edciMessageService;
    }

    public void setEdciMessageService(EDCIMessageService edciMessageService) {
        this.edciMessageService = edciMessageService;
    }

    public EDCIValidationUtil getEdciValidationUtil() {
        return edciValidationUtil;
    }

    public void setEdciValidationUtil(EDCIValidationUtil edciValidationUtil) {
        this.edciValidationUtil = edciValidationUtil;
    }
}

