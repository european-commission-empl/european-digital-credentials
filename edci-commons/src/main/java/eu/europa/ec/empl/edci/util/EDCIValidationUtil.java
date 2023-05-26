package eu.europa.ec.empl.edci.util;


import eu.europa.ec.empl.edci.datamodel.validation.ValidationError;
import eu.europa.ec.empl.edci.datamodel.validation.ValidationResult;
import eu.europa.ec.empl.edci.service.EDCIMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
public class EDCIValidationUtil {

    @Autowired
    private ReflectiveUtil reflectiveUtil;

    @Autowired
    private Validator validator;

    @Autowired
    private EDCIMessageService edciMessageService;

    public void loadLocalizedMessage(ValidationError validationError) {
        if (!validator.isEmpty(validationError.getErrorKey())) {
            validationError.setErrorMessage(this.getEdciMessageService().getMessage(validationError.getErrorKey(), validationError.getAffectedAssets()));
        }
    }

    public void loadLocalizedMessages(ValidationResult validationResult) {
        validationResult.getValidationErrors().forEach(this::loadLocalizedMessage);
    }

    public EDCIMessageService getEdciMessageService() {
        return edciMessageService;
    }

    public void setEdciMessageService(EDCIMessageService edciMessageService) {
        this.edciMessageService = edciMessageService;
    }


    public Validator getValidator() {
        return validator;
    }

    public void setValidator(Validator validator) {
        this.validator = validator;
    }

    public ReflectiveUtil getReflectiveUtil() {
        return reflectiveUtil;
    }

    public void setReflectiveUtil(ReflectiveUtil reflectiveUtil) {
        this.reflectiveUtil = reflectiveUtil;
    }
}
