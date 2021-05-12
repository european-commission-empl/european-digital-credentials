package eu.europa.ec.empl.edci.util;


import eu.europa.ec.empl.edci.constants.MessageKeys;
import eu.europa.ec.empl.edci.datamodel.model.base.Nameable;
import eu.europa.ec.empl.edci.datamodel.model.dataTypes.IdentifiableName;
import eu.europa.ec.empl.edci.datamodel.validation.ValidationResult;
import eu.europa.ec.empl.edci.service.EDCIMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintViolation;
import java.util.*;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
public class EDCIValidationUtil {

    @Autowired
    private ReflectiveUtil reflectiveUtil;

    @Autowired
    private Validator validator;

    @Autowired
    private EDCIMessageService edciMessageService;

    public Map<String, Object> getMessageVariables(ConstraintViolation constraintViolation) {
        Map<String, Object> variables = new HashMap<String, Object>();

        String fullPath = constraintViolation.getPropertyPath().toString();
        //String parameterPath = fullPath.substring(fullPath.lastIndexOf(".") + 1);
        IdentifiableName informativeName = this.getDeepestIdentifiableName(constraintViolation);

        variables.put(MessageKeys.Validation.VALIDATION_MESSAGE_VARIABLE_INFORMATIVENAME_CLASSNAME, informativeName.getClassName());
        variables.put(MessageKeys.Validation.VALIDATION_MESSAGE_VARIABLE_INFORMATIVENAME_FIELDNAME, informativeName.getFieldName());
        variables.put(MessageKeys.Validation.VALIDATION_MESSAGE_VARIABLE_INFORMATIVENAME_FIELDVALUE, informativeName.getFieldValue());

        variables.put(MessageKeys.Validation.VALIDATION_MESSAGE_VARIABLE_FIELDNAME, fullPath);
        return variables;
    }

    public IdentifiableName getDeepestIdentifiableName(ConstraintViolation constraintViolation) {
        //Get  Identifiable name from Leaf bean, most of time this will be the case
        IdentifiableName identifiableName = new IdentifiableName();

        if (Nameable.class.isAssignableFrom(constraintViolation.getLeafBean().getClass())) {
            Nameable nameable = (Nameable) constraintViolation.getLeafBean();
            identifiableName = nameable.getParsedIdentifiableName();
        }
        if (!identifiableName.isFullyInformed()) {
            //remove last apparence in property path, as it is the Leaf Bean and has already been tested
            String propertyPath = constraintViolation.getPropertyPath().toString();
            propertyPath = propertyPath.substring(0, propertyPath.lastIndexOf("."));

            //Split propertyPath and look for deepest identifiable name, repeat until one is found
            List<String> parameterPath = new ArrayList<>(Arrays.asList(propertyPath.split("\\.")));
            while (!identifiableName.isFullyInformed()) {
                Object instance = this.getReflectiveUtil().getLastInstanceFromPropertyPath(parameterPath, constraintViolation.getRootBean());
                if (instance instanceof Nameable) {
                    Nameable nameable = (Nameable) instance;
                    identifiableName = nameable.getParsedIdentifiableName();
                }
                if (parameterPath.size() > 1) {
                    parameterPath.remove(parameterPath.size() - 1);
                } else if (!identifiableName.isFullyInformed()) {
                    //If nothing found, fill all with "Undefined"
                    identifiableName.setFieldValue(this.getEdciMessageService().getMessage(MessageKeys.Validation.VALIDATION_MESSAGE_VARIABLE_UNFOUND));
                    identifiableName.setFieldName(this.getEdciMessageService().getMessage(MessageKeys.Validation.VALIDATION_MESSAGE_VARIABLE_UNFOUND));
                    identifiableName.setClassName(this.getEdciMessageService().getMessage(MessageKeys.Validation.VALIDATION_MESSAGE_VARIABLE_UNFOUND));
                    //If message source is not available, a break is required
                    if (!identifiableName.isFullyInformed()) {
                        identifiableName.init();
                        break;
                    }
                }
            }
        }
        return identifiableName;
    }

    public void loadLocalizedMessages(ValidationResult validationResult) {
        validationResult.getValidationErrors().stream().forEach(
                validationError -> {
                    if (!validator.isEmpty(validationError.getErrorKey())) {
                        validationError.setErrorMessage(this.getEdciMessageService().getVariableMessage(validationError.getErrorKey(), validationError.getMessageVariables()));
                    }
                }
        );
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
