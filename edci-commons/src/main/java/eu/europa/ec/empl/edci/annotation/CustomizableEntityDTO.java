package eu.europa.ec.empl.edci.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates that the class is a DTO representing a part of the credential DataModel.
 * Used to generate the customizable Spec that can be applied to a credential, based on the presence of this annotation
 * in diverse DTOs, as well as to identify a identifier field that will not change
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface CustomizableEntityDTO {

    /**
     * The field name of the field that acts as an identifier, and cannot be customized
     *
     * @return the Identifier field
     */
    public String identifierField();

}
