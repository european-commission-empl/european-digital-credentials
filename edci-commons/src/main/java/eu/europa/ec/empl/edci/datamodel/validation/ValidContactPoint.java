package eu.europa.ec.empl.edci.datamodel.validation;

import eu.europa.ec.empl.edci.constants.EDCIMessageKeys;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {ContactPointEmailWalletAddressValidator.class})
public @interface ValidContactPoint {

    String message() default EDCIMessageKeys.Validation.VALIDATION_CONTACT_POINT;

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}