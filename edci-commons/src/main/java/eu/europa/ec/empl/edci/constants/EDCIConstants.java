package eu.europa.ec.empl.edci.constants;

public class EDCIConstants {

    public static final String DEFAULT_LOCALE = "en_GB";

    public static final String DATE_ISO_8601 = "yyyy-MM-dd'T'HH:mm:ssXXX";
    public static final String DATE_LOCAL = "yyyy-MM-dd"; //Date used in PDF expiration date parameter and diploma HTML

    public static final String DATE_FRONT_LOCAL = "dd/MM/yyyy";
    public static final String DATE_FRONT_LOCAL_OWNER = "dd MMM yyyy";
    public static final String DATE_FRONT_GMT = "dd/MM/yyyy HH:mm 'GMT' Z";

    public static final String NAMESPACE_CRED_URI = "http://data.europa.eu/europass/model/credentials/w3c#";
    public static final String NAMESPACE_VP_URI = "http://data.europa.eu/europass/model/vp/w3c#";
    public static final String NAMESPACE_VP_DEFAULT = "http://data.europa.eu/snb/vp";

    public static final String DEFAULT_VIEWER_DIPLOMA_HTML_PATH = "diploma/diploma_default.html";
    public static final String DEFAULT_VIEWER_DIPLOMA_THYMELEAF_PATH = "diploma/diploma_default_thymeleaf.html";
    public static final String DEFAULT_VIEWER_DIPLOMA_BKG_IMG_PATH = "diploma/diploma-background.png";
    public static final String DEFAULT_VIEWER_LOGO_BKG_IMG_PATH = "diploma/logo_default.png";
    public static final String DEFAULT_CONSTRAINT_XML_PATH = "customConstraint.xml";

    public static final String CONFIG_PUBLICATIONS_RDF_SPARQL_ENDPOINT = "publications.rdf.sparql.endpoint";

    public static class QMSAccreditations {
        public enum Type {
            organization,
            qualification
        }
    }

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
        public static final String STRING_HYPHEN = "-";
        public static final String STRING_EQUALS = "=";
        public static final String STRING_TRUE = "true";
    }

    public static class Security {

        public static final String XMLHttpRequest = "XMLHttpRequest";
        public static final String PATH_EXPIRED_SESSION = "/screen/create/prepare";
        public static final String CONFIG_PROPERTY_SESSION_TIMEOUT = "app.session.timeout";
    }

    public static class Version {

        public static final String V0 = "/v0";
        public static final String V1 = "/v1";
        public static final String V2 = "/model";
    }

    public static class XML {

        public static final String NAMESPACE_CRED_URI = "http://data.europa.eu/europass/model/credentials/w3c#";
        public static final String EXTENSION_XML = ".xml";
    }

    public static class HttpHeaders {
        public static final String X_REQUESTED_WITH = "X-Requested-With";
    }
}
