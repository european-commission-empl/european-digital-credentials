package eu.europa.ec.empl.base;

import eu.europa.ec.empl.edci.util.MockFactoryUtil;
import eu.europa.ec.empl.edci.util.Validator;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.util.List;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
public class TestUtilities {

    private static final Logger _logger = Logger.getLogger(TestUtilities.class);

    @Autowired
    private Validator validator;

    @Autowired
    private MockFactoryUtil mockFactoryUtil;

    public TestUtilities(Validator validator) {
        this.validator = validator;
    }

    public Validator getValidator() {
        return validator;
    }

    public void setValidator(Validator validator) {
        this.validator = validator;
    }

    public <T> T createMockObject(Class<T> clazz) throws MalformedURLException {
        return mockFactoryUtil.createMockObject(clazz);
    }

    protected boolean isListInstance(Field field) {
        return List.class.isAssignableFrom(field.getType());
    }

}
