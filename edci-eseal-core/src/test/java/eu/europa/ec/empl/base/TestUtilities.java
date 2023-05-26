package eu.europa.ec.empl.base;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.xml.validation.Validator;
import java.lang.reflect.Field;
import java.util.List;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
public class TestUtilities {

    private static final Logger _logger = LogManager.getLogger(TestUtilities.class);

    @Autowired
    private Validator validator;

    public TestUtilities(Validator validator) {
        this.validator = validator;
    }

    public Validator getValidator() {
        return validator;
    }

    public void setValidator(Validator validator) {
        this.validator = validator;
    }

    protected boolean isListInstance(Field field) {
        return List.class.isAssignableFrom(field.getType());
    }

}
