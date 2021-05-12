package integration.eu.europa.ec.empl.edci.datamodel;

import eu.europa.ec.empl.edci.datamodel.validation.ValidationError;
import eu.europa.ec.empl.edci.datamodel.validation.ValidationResult;
import integration.eu.europa.ec.empl.base.AbstractIntegrationBaseTest;
import integration.eu.europa.ec.empl.extra.BeanX;
import integration.eu.europa.ec.empl.extra.GroupA;
import integration.eu.europa.ec.empl.extra.GroupB;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import javax.validation.groups.Default;
import java.util.Set;

//TODO: Review
public class VerificationGroupsITest extends AbstractIntegrationBaseTest {

    protected static final Logger _logger = LoggerFactory.getLogger(VerificationGroupsITest.class);

    //Copy paste method from EDCIValidationService with extra parameter -> groups
    private <T> ValidationResult validate(T object, Class<T> clazz, Class<? extends Default>... groups) {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();

        Set<ConstraintViolation<T>> constraintViolations = validator.validate(object, groups);
        ValidationResult validationResult = new ValidationResult();
        validationResult.setValid(constraintViolations.isEmpty());

        if (!validationResult.isValid()) {
            for (ConstraintViolation<T> constraintViolation : constraintViolations) {
                ValidationError validationError = new ValidationError();
                _logger.error(constraintViolation.getMessage());
            }
        }

        return validationResult;
    }

    @Test
    public void validate_shouldReturnTrue_whenValidatingNoGroupAndDefFieldInformed() {

        BeanX bean = new BeanX(null, null, null, "Default");

        ValidationResult validationResult = validate(bean, BeanX.class);

        Assert.assertTrue(validationResult.isValid());
    }

    @Test
    public void validate_shouldReturnTrue_whenValidatingGroupDefaultAndDefFieldInformed() {

        BeanX bean = new BeanX(null, null, null, "Default");

        ValidationResult validationResult = validate(bean, BeanX.class, Default.class);

        Assert.assertTrue(validationResult.isValid());
    }

    @Test
    public void validate_shouldReturnFalse_whenValidatingGroupAAndAllFieldsButDefaultInformed() {

        BeanX bean = new BeanX("A", "B", "AB", null);

        ValidationResult validationResult = validate(bean, BeanX.class, GroupA.class, Default.class);

        Assert.assertFalse(validationResult.isValid());
    }

    @Test
    public void validate_shouldReturnTrue_whenValidatingGroupAAndAllFieldsInformed() {

        BeanX bean = new BeanX("A", "B", "AB", "Default");

        ValidationResult validationResult = validate(bean, BeanX.class, GroupA.class);

        Assert.assertTrue(validationResult.isValid());
    }

    @Test
    public void validate_shouldReturnTrue_whenValidatingGroupAAndFieldsAInformed() {

        BeanX bean = new BeanX("A", null, "AB", "Default");

        ValidationResult validationResult = validate(bean, BeanX.class, GroupA.class);

        Assert.assertTrue(validationResult.isValid());
    }

    @Test
    public void validate_shouldReturnFalse_whenValidatingGroupAAndFieldABIsNotInformed() {

        BeanX bean = new BeanX("A", null, null, "Default");

        ValidationResult validationResult = validate(bean, BeanX.class, GroupA.class);

        Assert.assertFalse(validationResult.isValid());
    }

    @Test
    public void validate_shouldReturnFalse_whenValidatingGroupAAndFieldsAreNotInformed() {

        BeanX bean = new BeanX(null, null, null, "Default");

        ValidationResult validationResult = validate(bean, BeanX.class, GroupA.class);

        Assert.assertFalse(validationResult.isValid());
    }

    @Test
    public void validate_shouldReturnTrue_whenValidatingGroupBAndFieldsBAreInformed() {

        BeanX bean = new BeanX(null, "B", "AB", "Default");

        ValidationResult validationResult = validate(bean, BeanX.class, GroupB.class);

        Assert.assertTrue(validationResult.isValid());
    }

    @Test
    public void validate_shouldReturnFalse_whenValidatingGroupBAndFieldsABAreInformed() {

        BeanX bean = new BeanX(null, "B", "AB", "Default");

        ValidationResult validationResult = validate(bean, BeanX.class, GroupA.class, GroupB.class);

        Assert.assertFalse(validationResult.isValid());
    }

    @Test
    public void validate_shouldReturnTrue_whenValidatingGroupBAndFieldsABAreInformed() {

        BeanX bean = new BeanX("A", "B", "AB", "Default");

        ValidationResult validationResult = validate(bean, BeanX.class, GroupA.class, GroupB.class);

        Assert.assertTrue(validationResult.isValid());
    }

}
