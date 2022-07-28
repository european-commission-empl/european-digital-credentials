package eu.europa.ec.empl.edci.datamodel.validation;

import eu.europa.ec.empl.edci.datamodel.model.ContactPoint;
import org.apache.commons.collections4.CollectionUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ContactPointEmailWalletAddressValidator
       implements ConstraintValidator<ValidContactPoint, ContactPoint> {

    public void initialize(ValidContactPoint constraintAnnotation) {

    }

    @Override
    public boolean isValid(ContactPoint address,
                           ConstraintValidatorContext constraintValidatorContext) {

        if (address != null
                && CollectionUtils.isEmpty(address.getWalletAddress()) && CollectionUtils.isEmpty(address.getEmail())) {
            return false;
        } else {
            return true;
        }

    }
}