package eu.europa.ec.empl.edci.annotation;

import eu.europa.ec.empl.edci.constants.ControlledList;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Specifies that the customizable field is intended to be from a controlled List
 * This annotation must always be coupled with @CustomizableField annotation
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface CustomizableCLFieldDTO {

    /**
     * Defines the target framework for the controlled list, based on the ControlledList enum
     *
     * @return Controlled List target framework
     */
    public ControlledList targetFramework();

}
