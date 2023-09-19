package eu.europa.ec.empl.edci.issuer.common.constants;

import eu.europa.ec.empl.edci.datamodel.jsonld.model.*;

import java.util.Arrays;

public class Customization {

    public static final String VALIDATION_LENGTH = "Length";
    public static final String VALIDATION_MANDATORY = "Mandatory";
    public static final String VALIDATION_MANDATORYIF = "MandatoryIf";
    public static final String VALIDATION_MANDATORYIFNOT = "MandatoryIfNot";
    public static final String VALIDATION_DATELOCALFORMAT = "DateLocalFormat";
    public static final String VALIDATION_DATEFORMAT = "DateFormat";
    public static final String VALIDATION_NUMERIC = "Numeric";
    public static final String VALIDATION_EMAIL = "Email";

    public enum Validation {
        Length(VALIDATION_LENGTH),
        Mandatory(VALIDATION_MANDATORY),
        MandatoryIf(VALIDATION_MANDATORYIF),
        MandatoryIfNot(VALIDATION_MANDATORYIFNOT),
        DateLocalFormat(VALIDATION_DATELOCALFORMAT),
        DateFormat(VALIDATION_DATEFORMAT),
        Numeric(VALIDATION_NUMERIC),
        Email(VALIDATION_EMAIL);

        private String name;
        public static final String validator_separator = ",";
        public static final String parameter_separator_open = "(";
        public static final String parameter_separator_close = ")";

        public String getName() {
            return this.name;
        }

        Validation(String name) {
            this.name = name;
        }
    }

    public enum FieldGroup {
        IDENTIFIER(FieldGroup.GROUP_IDENTIFIER),
        ADDRESS(FieldGroup.GROUP_ADDRESS),
        CONTACT_POINT(FieldGroup.GROUP_CONTACT_POINT);

        public static final String GROUP_IDENTIFIER = "identifier";
        public static final String GROUP_DELIVERY_ADDRESS = "delivery";
        public static final String GROUP_OTHER_IDENTIFIER = "otherIdentifier";
        public static final String GROUP_ADDRESS = "address";
        public static final String GROUP_CONTACT_POINT = "contactPoint";

        private String name;

        public String getName() {
            return this.name;
        }

        FieldGroup(String name) {
            this.name = name;
        }
    }

    public enum CustomizableEntities {

        //RDF sources
        PERSONAL("REC", PersonDTO.class),
        CREDENTIAL("#", EuropeanDigitalCredentialDTO.class),
        ACHIEVEMENT("ACH", LearningAchievementDTO.class),
        ASSESSMENT("ASM", LearningAssessmentDTO.class),
        ACTIVITY("ACT", LearningActivityDTO.class),
        ENTITLEMENT("ENT", LearningEntitlementDTO.class);

        private String code;
        private Class clazz;

        private CustomizableEntities(String code, Class clazz) {
            this.code = code;
            this.clazz = clazz;
        }

        public static boolean contains(String code) {
            return Arrays.stream(CustomizableEntities.values()).anyMatch(item -> item.getCode().equals(code));
        }


        public static CustomizableEntities getByCode(String code) {
            return Arrays.stream(CustomizableEntities.values()).filter(entity -> entity.getCode().equals(code)).findFirst().orElse(null);
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public Class getClazz() {
            return clazz;
        }

        public void setClazz(Class clazz) {
            this.clazz = clazz;
        }

    }

}
