package eu.europa.ec.empl.edci.dss.constants;

public class ESealConstants {

    public static final String DATE_ISO_8601 = "yyyy-MM-dd'T'HH:mm:ssXXX";

    public static class Certificate {
        public static final String CREDENTIAL_TYPE_EUROPASS_CREDENTIAL = "EC";
        public static final String CREDENTIAL_TYPE_EUROPASS_PRESENTATION = "VP";
        public static final String CERTIFICATE_ATTRIBUTE_ORGANIZATION = "O";
        public static final String CERTIFICATE_ATTRIBUTE_COUNTRY_NAME = "C";
        public static final String CERTIFICATE_ATTRIBUTE_ORGANIZATION_IDENTIFIER = "OI";
        public static final String CERTIFICATE_ATTRIBUTE_COMMON_NAME = "CN";
        public static final String CERTIFICATE_BEGIN_MARKER = "-----BEGIN CERTIFICATE-----\n";
        public static final String CERTIFICATE_END_MARKER = "-----END CERTIFICATE-----";
    }

    public static class StringPool {
        public static final String STRING_EMPTY = "";
        public static final String STRING_SPACE = " ";
        public static final String STRING_SEMICOLON = ";";
        public static final String STRING_COMMA = ",";
        public static final String STRING_SLASH = "/";
        public static final String STRING_PERCENTAGE = "%";
        public static final String STRING_UNDERSCORE = "_";
        public static final String STRING_EQUALS = "=";
        public static final String STRING_TRUE = "true";
    }

}
