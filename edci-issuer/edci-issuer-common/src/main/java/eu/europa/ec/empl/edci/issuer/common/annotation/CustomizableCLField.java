package eu.europa.ec.empl.edci.issuer.common.annotation;

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
public @interface CustomizableCLField {

    /**
     * Defines the target framework for the controlled list, based on the ControlledList enum
     *
     * @return Controlled List target framework
     */
    public ControlledList targetFramework();

    /**
     * Return the label key of the description used in this CL
     *
     * @return the label key
     */
    public String descriptionLabelKey();
}
