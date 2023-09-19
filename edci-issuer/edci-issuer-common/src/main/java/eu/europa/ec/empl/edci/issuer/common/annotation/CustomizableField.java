package eu.europa.ec.empl.edci.issuer.common.annotation;

import eu.europa.ec.empl.edci.issuer.common.model.customization.FieldType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates that the field is a representation of a datamodel field, and that can be customized during issuing.
 * To be appropriately scanned, this field must only be present in fields of classes annotated with @CustomizableEntity
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface CustomizableField {

    /**
     * defines the position of the field inside the entity field list, mostly for frontend presentation. Takes into
     * account the @CustomizableRelations annotations positions.
     *
     * @return the position
     */
    public int position();

    /**
     * defines the method used to define the label that will be looked for the translation. If the value is not an empty string, it will override the labelKey value
     *
     * @return the label key
     */
    public String dynamicMethodLabelKey() default "";

    /**
     * defines the label key that will be looked for the translation
     *
     * @return the label key
     */
    public String labelKey();

    /**
     * The type of input that the field will require from the frontend application, it must be defined in FieldType enum
     *
     * @return the FieldType
     */
    public FieldType fieldType();

    /**
     * The expression that refers to the target field in the credential datamodel
     *
     * @return the fieldPath expression
     */
    public String fieldPath();

    /**
     * Specifies if the field should be mandatory to customize (not related to mandatory in datamodel)
     *
     * @return true if the field is mandatory, default is false
     */
    public boolean mandatory() default false;

    /**
     * returns the size of custom elements that are customizable, only for collection fields
     *
     * @return the size of customizable collection
     */
    public int size() default 1;

    /**
     * the validation that will be used in front-end, see eu.europa.ec.empl.edci.issuer.common.constants.Customization for a list of Validations
     *
     * @return the validation, default is empty
     */
    public String validation() default "";

    /**
     * Additional info to be used in description or tooltips
     *
     * @return the additional info, default is empty
     */
    public String[] additionalInfo() default {};

    /**
     * The name of the shouldInstance Method, used to decide if a non existent object in a certain field should be instanced IE: grades
     *
     * @return the shouldInstance method name, default is empty
     */
    public String shouldInstanceMethodName() default "true";

    /**
     * The fieldPath of a field that should be automatically included if the field is selected, and the target fieldPath field is missing
     *
     * @return the fieldPath of the field that should be automatically included
     */
    public String relatesTo() default "";

}
