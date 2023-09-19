package eu.europa.ec.empl.edci.constants;

public class DataModelConstants {

    public static final String DATE_ISO_8601 = "yyyy-MM-dd'T'HH:mm:ssXXX";
    public static final String DATE_LOCAL = "yyyy-MM-dd"; //Date used in PDF expiration date parameter and diploma HTML
    public static final String CONFIG_PUBLICATIONS_RDF_SPARQL_ENDPOINT = "publications.rdf.sparql.endpoint";
    public static final String JSON_LD_EXTENSION = ".jsonld";
    public static final int FIRST_DIPLOMA_PAGE = 1;
    public static final String PAYLOAD_PARAMETER = "^\\s*\\{\\s*\"payload\"";

    public class Defaults {
        public static final String DEFAULT_LOCALE = "en";
        public static final String DEFAULT_MAILTO = "mailto:";
        public static final String CONFIG_PUBLICATIONS_RDF_SPARQL_ENDPOINT = "http://publications.europa.eu/webapi/rdf/sparql";
    }

    public static class StringPool {
        public static final String STRING_EMPTY = "";
        public static final String STRING_SPACE = " ";
        public static final String STRING_SEMICOLON = ";";
        public static final String STRING_COMMA = ",";
        public static final String STRING_SLASH = "/";
        public static final String STRING_HYPHEN = "-";
        public static final String STRING_PERCENTAGE = "%";
        public static final String STRING_UNDERSCORE = "_";
        public static final String STRING_EQUALS = "=";
        public static final String STRING_TRUE = "true";
        public static final String STRING_QUOTE = "\"";
        public static final String STRING_OPEN_BRACKET = "{";
    }

    public class BadRquest {
        public static final String UPLOAD_CREDENTIAL_BAD_FORMAT = "upload.credential.bad.format";
        public static final String UPLOAD_CREDENTIALS_BAD_FORMAT = "upload.credentials.bad.format";
        public static final String UPLOAD_CREDENTIAL_NOT_READABLE = "upload.credential.not.readable";
        public static final String UPLOAD_INVALID_PROFILE = "upload.invalid.profile";
        public static final String CONTENT_TYPE_NOTFOUND = "exception.content.type.notfound";
        public static final String MISSING_PARAMETER = "error.missing.parameter";
        public static final String MISSING_REQUEST_PART = "error.missing.request.part";
    }

    public class Path {
        public static final String SHACL_FILE = "file:${edci.properties.base}/shacl.properties";
    }

    public class Properties {
        public static final String GENERIC = "edci.credential.v1.shacl.generic";
        public static final String CONVERTED = "edci.credential.v1.shacl.converted";
        public static final String ACCREDITED = "edci.credential.v1.shacl.accredited";
        public static final String DIPLOMA_SUPPLEMENT = "edci.credential.v1.shacl.accredited";
        public static final String ISSUED_MANDATE = "edci.credential.v1.shacl.mandate";
        public static final String JSON_CONTEXT = "edci.credential.v1.json.context";
        public static final String ACCREDITATION_ENDPOINT = "edci.accreditation.endpoint";
        public static final String DELIVERY_ADDRESS_LIMIT = "edci.delivery.address.limit";
        public static final String ACCREDITATION_SEARCH_ENDPOINT = "edci.accreditation.search.endpoint";
    }
}
