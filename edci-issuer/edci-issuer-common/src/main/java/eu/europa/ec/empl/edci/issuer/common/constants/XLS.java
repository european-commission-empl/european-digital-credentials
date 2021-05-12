package eu.europa.ec.empl.edci.issuer.common.constants;

import java.util.Arrays;
import java.util.Optional;

public class XLS {


    public final static int CLASS_ROW = 1;
    public final static int FIELD_ROW = 2;
    public final static int TYPE_ROW = 3;
    public final static int RANGEPROP_ROW = 4;
    public final static int RANGEREF_ROW = 5;
    public final static int LANGUAGE_ROW = 6;
    public final static int LABEL_ROW = 7;
    public final static int DESCRIPTION_ROW = 8;
    public final static int LANGUAGE_DESCRIPTION_ROW = 9;
    public final static int DEFAULT_VALUE_ROW = 10;

    public final static int FIRST_MANDATORY_LABEL_RECIPIENTS = 2;
    public final static int FIRST_DEFAULT_LABEL_RECIPIENTS = 2;

    public final static int DATA_ROW = 13;
    public final static int STARTING_INDEX_COLUMN_INFO = 0;
    public final static int ROW_STARTING_INDEX = DATA_ROW - 1; //-1 to the excel row
    public final static int DEFINITION_COLUMN = 0;
    public final static int STARTING_COLUMN_INDEX = DEFINITION_COLUMN + 1;

    public final static String SPLIT_PATTERN_NESTED_ASSETS = ".*\\..*";
    public final static String PATTERN_MULTIPLE_ASSOCIATIONS = ".*;.*";

    public final static char SPLIT_CHARACTER_NESTED_ASSETS = '.';
    public final static String SPLIT_STRING_NESTED_ASSETS = "\\.";
    public final static String SPLIT_CHARACTER_MULTIPLE_ASSOCIATIONS = ";";

    public final static int MAX_DATA_ROWS_CREDENTIALS = 100;
    public final static int MAX_DATA_ROWS_OTHERS = 100;

    public final static String RECIPIENT_TEMPLATE_NAME = "recipients";
    public final static String RECIPIENTS_SHEET = "Recipients";

    public class Recipient {
        public static final String LABEL_COLUMN = "A";
        public static final String GIVENNAME_COLUMN = "C";
        public static final String FAMILYNAME_COLUMN = "D";
        public static final String DATEOFBIRTH_COLUMN = "E";
        public static final String GENDER_COLUMN = "F";
        public static final String NATIONALID_COLUMN = "H";
        public static final String PLACEOFBIRTH_COLUMN = "K";
        public static final String COUNTRYOFCITIZENSHIP_COLUMN = "M";
        public static final String EMAILADDRESS_COLMN = "O";
        public static final String WALLETADDRESS_COLUMN = "P";
        public static final String FULLADDRESS_COLUMN = "Q";
        public static final String COUNTRYOFRESIDENCE_COLUMN = "R";
    }

    public enum PARSE_TYPE {

        PROPERTY("Property"), ASSOCIATION("Association"), COMMENT("Comment"), NESTED_PROPERTY("NestedProperty"),
        EXTERNAL_ASSOCIATION("ExternalAssociation"), MULTIPLE_ASSOCIATION("MultipleAssociation"), NESTED_ASSOCIATION("NestedAssociation"),
        GRADES_ASSOCIATION("GradesAssociation"), IGNORE("Ignore");

        private String value;

        public String value() {
            return this.value;
        }

        private PARSE_TYPE(String value) {
            this.value = value;
        }

        public static boolean contains(String type) {
            return Arrays.stream(PARSE_TYPE.values()).anyMatch(item -> item.value().equalsIgnoreCase(type));
        }

        public static PARSE_TYPE getParseType(String typeString) {
            Optional<PARSE_TYPE> type = null;
            type = Arrays.stream(PARSE_TYPE.values()).filter(item -> item.value().equalsIgnoreCase(typeString)).findFirst();
            return type.isPresent() ? type.get() : PARSE_TYPE.IGNORE;
        }
    }

    public enum PARSE_CASE {
        DIRECT_ATTRIBUTE, DIRECT_OBJECT, MULTILANG_OBJECT, ATTRIBUTE_LIST, OBJECT_LIST, MULTILANG_OBJECT_LIST, UNKNOWN,
    }


    /*Equivalences and Root class to parse*/
    public enum EQUIVALENCE {
        ROOT_CLASS("EuropassCredential", "eu.europa.ec.empl.edci.datamodel.model.EuropassCredentialDTO"),
        RECIPIENT_CLASS("Person", "eu.europa.ec.empl.edci.datamodel.model.PersonDTO"),
        GRADE_HOLDER("Person", "eu.europa.ec.empl.edci.datamodel.model.PersonDTO"),
        GRADEABLE_ITEM("Assessment", "eu.europa.ec.empl.edci.datamodel.model.AssessmentDTO"),
        CONTAINS_BYTE_LIST("contains", "eu.europa.ec.empl.edci.datamodel.model.EuropassCredentialDTO.contains"),
        CONTAINS_XMLS_LIST("subCredentialsXML", "eu.europa.ec.empl.edci.datamodel.model.EuropassCredentialDTO.SubCredentialsXML");

        private String key;
        private String value;

        public String getKey() {
            return this.key;
        }

        public String getValue() {
            return this.value;
        }

        private EQUIVALENCE(String key, String value) {
            this.key = key;
            this.value = value;
        }

    }

    //Sheets that won't be validated
    public enum UNVALIDATED_SHEETS {
        References("References");

        private String sheet;

        public String getSheet() {
            return this.sheet;
        }

        private UNVALIDATED_SHEETS(String sheet) {
            this.sheet = sheet;
        }

        public static boolean contains(String sheet) {
            return Arrays.stream(values()).anyMatch(item -> item.getSheet().equalsIgnoreCase(sheet));
        }
    }

    //Sheets that won't be scanned
    public enum UNSCANNED_SHEETS {
        Instructions("Instructions"), Language("Language choice"), Designer("Credential Designer"), ToDo("ToDo"), MultiAssociationSheet("MultiAssociation Sheet"), Service("Service");

        private String sheet;

        public String getSheet() {
            return this.sheet;
        }

        private UNSCANNED_SHEETS(String sheet) {
            this.sheet = sheet;
        }

        public static boolean contains(String sheet) {
            return Arrays.stream(values()).anyMatch(item -> item.getSheet().equalsIgnoreCase(sheet));
        }
    }
   /* public final static String PROPERTY_KEYWORD = "Property";
    public final static String ASSOCIATION_KEYWORD = "Association";*/
}
