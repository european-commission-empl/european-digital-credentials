package eu.europa.ec.empl.edci.datamodel.validation;


import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
// TODO -> RESTORE WHEN EMAIL/WALLET CHECK ARE BACK @Constraint(validatedBy = {ContactPointEmailWalletAddressValidator.class})
public @interface ValidContactPoint {

    String message() default "EDCIMessageKeys.Validation.VALIDATION_CONTACT_POINT";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}