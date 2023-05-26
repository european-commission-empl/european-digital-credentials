package eu.europa.ec.empl.edci.issuer.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Specifies that the field is a customizable relation, and thus can be selected or unselected for inclusion
 * during customization.
 * This Annotation must be present in a class with @CustomizableEntity, otherwise will be ignored.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface CustomizableRelation {

    /**
     * defines the position of the field inside the entity field list, mostly for frontend presentation. Takes into
     * account the @CustomizableField annotations positions.
     *
     * @return the position
     */
    public int position();

    /**
     * defines the label key that will be looked for translation
     *
     * @return the label key
     */
    public String labelKey();

    /**
     * An expression that describes the position of this relation in the final credential datamodel.
     *
     * @return the relation path
     */
    public String relPath();

    /**
     * An identifier to group and order the relations.
     *
     * @return the group Id
     */
    public int groupId();

}
