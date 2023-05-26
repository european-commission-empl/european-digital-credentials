package eu.europa.ec.empl.edci.issuer.common.annotation;

import eu.europa.ec.empl.edci.issuer.common.model.RecipientDataDTO;

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
public @interface CustomizableEntity {

    public static final Class recipientClass = RecipientDataDTO.class;

    public static final String startPlaceHolder = "$";
    public static final String rootStartPlaceHolder = "#";

    public static final String idPlaceHolder = startPlaceHolder + "id";
    public static final String dmPathIdBracketOpen = "{";
    public static final String dmPathIdBracketClose = "}";
    public static final String dmPathIdHolderBlock = dmPathIdBracketOpen + idPlaceHolder + dmPathIdBracketClose;

    public static final String dmPathSeparator = ".";

    public static final String langPlaceHolder = startPlaceHolder + "lang";
    public static final String dmPathLangBracketOpen = "(";
    public static final String dmPathLangBracketClose = ")";
    public static final String dmPathLangHolderBlock = dmPathLangBracketOpen + langPlaceHolder + dmPathLangBracketClose;

    public static final String dmPathPosBracketOpen = "[";
    public static final String dmPathPosBracketClose = "]";

    public static final String entityIDPlaceHolder = startPlaceHolder + "entityId";
    public static final String dmPathEntityIDBracketOpen = "{";
    public static final String dmPathEntityIDBracketClose = "}";
    public static final String dmPathEntityHolderBlock = dmPathEntityIDBracketOpen + entityIDPlaceHolder + dmPathEntityIDBracketClose;

    /**
     * The field name of the field that acts as an identifier, and cannot be customized
     *
     * @return the Identifier field
     */
    public String identifierField();

    /**
     * The label key, that will be looked for the translation
     *
     * @return the label key
     */
    public String labelKey() default "msg.entity";

    /**
     * The position of the entity in the form with the list selectable entities
     *
     * @return the position
     */
    public int position() default -1;

    /**
     * The name of the spec class in the DAOs, use Object.class if it is not in any DAO and totally custom.
     *
     * @return the spec class
     */
    public Class specClass() default Object.class;

    /**
     * The entity code, used in fieldPaths, to identify the entity
     *
     * @return the entity Code
     */
    public String entityCode();
}
