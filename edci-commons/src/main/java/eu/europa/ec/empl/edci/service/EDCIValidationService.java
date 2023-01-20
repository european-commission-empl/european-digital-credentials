package eu.europa.ec.empl.edci.service;

import eu.europa.ec.empl.edci.constants.EDCIMessageKeys;
import eu.europa.ec.empl.edci.datamodel.model.EuropassCredentialDTO;
import eu.europa.ec.empl.edci.datamodel.model.base.CredentialHolderDTO;
import eu.europa.ec.empl.edci.datamodel.validation.ValidationError;
import eu.europa.ec.empl.edci.datamodel.validation.ValidationResult;
import eu.europa.ec.empl.edci.util.EDCIValidationUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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

    private static final Logger logger = LogManager.getLogger(EDCIValidationService.class);

    @Autowired
    private EDCIMessageService edciMessageService;

    @Autowired
    private EDCIValidationUtil edciValidationUtil;

    @Autowired
    private eu.europa.ec.empl.edci.util.Validator edciValidator;

    public ValidationResult validateAndLocalizeCredentialHolder(CredentialHolderDTO credentialHolderDTO, Class<? extends Default>... groups) {
        EuropassCredentialDTO europassCredentialDTO = credentialHolderDTO.getCredential();
        ValidationResult validationResult = this.validateAndLocalize(europassCredentialDTO, groups);
        if (this.getEdciValidator().isEmpty(europassCredentialDTO.getWalletAddress())
                && this.getEdciValidator().isEmpty(europassCredentialDTO.getEmail())) {
            validationResult.setValid(false);
            ValidationError validationError = new ValidationError(EDCIMessageKeys.Validation.PUBLIC_CRED_NO_CONTACT);
            this.getEdciValidationUtil().loadLocalizedMessage(validationError);
            validationResult.addValidationError(validationError);
        }
        return validationResult;
    }

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

    public eu.europa.ec.empl.edci.util.Validator getEdciValidator() {
        return edciValidator;
    }

    public void setEdciValidator(eu.europa.ec.empl.edci.util.Validator edciValidator) {
        this.edciValidator = edciValidator;
    }
}

