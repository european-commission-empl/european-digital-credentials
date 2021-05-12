package eu.europa.ec.empl.edci.issuer.common.constants;

public abstract class EDCIIssuerConstants {

    public static final String USER = "USER";
    public static final String FORM = "FORM";

    public static final String DATE_FORMAT = "dd/MM/yyyy";
    public static final String DATETIME_FORMAT = "dd/MM/yyyy HH:mm";

    public static final String EXCEL_PATH = "/u01/oracle/user_projects/domains/base_domain/mare/";
    public static final String EXCEL_TEMPLATE_PREFIX = "EDC_template-";

    public static final String XML_TMP_REPOSITORY = "/u01/oracle/user_projects/domains/base_domain/mare/";
    public static final String XML_FILE_PREFIX = "credential-";
    public static final String XML_IDENTIFIERS_PREFIX = "urn:epass:concept:";

    public static final String XLS_NEW_PREFIX = "DX";
    public static final String XLS_OLD_PREFIX = "HS";

    public static final String XLS_SHEET_ACHIEVEMENTS = "Achievements";
    public static final String XLS_SHEET_CREDENTIALS = "Credentials";
    public static final String XLS_SHEET_ASSESSMENTS = "Assessments";
    public static final String XLS_SHEET_ACTIVITIES = "Activities";
    public static final String XLS_SHEET_LEARNINGOUTCOMES = "Learning Outcomes";
    public static final String XLS_SHEET_AGENTS = "Agents-Organisations";
    public static final String XLS_SHEET_QADECISIONS = "QA Decisions";
    public static final String XLS_SHEET_ASSESMENTGRADES = "Assessment Grades";
    public static final String XLS_SHEET_ACHIEVEMENTGRADES = "Achievement Grades";
    public static final String XLS_SHEET_CREDENTIAL_BUILDER = "Credential Builder";
    public static final String XLS_SHEET_SUB_CREDENTIALS = "Sub-Credentials";

    public static final String XLS_CELL_TYPE_STRING = "String";
    public static final String XLS_CELL_TYPE_DOUBLE = "Double";
    public static final String XLS_CELL_TYPE_DATE = "Date";

    public static final String XLS_PATTERN_FORCENUMBERASTEXT = ".*%";
    public static final String XLS_PATTERN_FORCENUMBERASDATE = "^m\\/d\\/yy$";

    public static final String EXTENSION_XLSX = ".xlsx";
    public static final String EXTENSION_XLSM = ".xlsm";
    public static final String EXTENSION_XLS = ".xls";

    public static final String STRING_EMPTY = "";
    public static final String STRING_SPACE = " ";
    public static final String STRING_SEMICOLON = ";";
    public static final String STRING_COMMA = ",";
    public static final String STRING_SLASH = "/";
    public static final String STRING_PERCENTAGE = "%";
    public static final String STRING_UNDERSCORE = "_";
    public static final String STRING_EQUALS = "=";

    public static final String CONFIG_PROPERTY_ACTIVE_PROFILE = "active.profile";
    public static final String CONFIG_PROPERTY_MAIL_SMTP_USER = "mail.smtp.user";
    public static final String CONFIG_PROPERTY_MAIL_SMTP_PASS = "mail.smtp.pass";
    public static final String CONFIG_PROPERTY_MAIL_SMTP_HOST = "mail.smtp.host";
    public static final String CONFIG_PROPERTY_MAIL_SMTP_AUTH = "mail.smtp.auth";
    public static final String CONFIG_PROPERTY_MAIL_DEBUG = "mail.debug";
    public static final String CONFIG_PROPERTY_MAIL_SMTP_STARTTLS = "mail.smtp.starttls.enable";
    public static final String CONFIG_PROPERTY_LOAD_CONTROLLED_LISTS_STARTUP = "load.controlled.lists.on.startup";
    public static final String CONFIG_PROPERTY_LOAD_CONTROLLED_LISTS_MONTHLY = "load.controlled.lists.monthly";

    public static final String CONFIG_PROPERTY_DIGEST_ALGORITHM_NAME = "digest.algorithm.name";

    public static final String CONFIG_PROPERTY_TMP_DATA_LOCATION = "tmp.data.location";
    public static final String CONFIG_PROPERTY_TMP_CRED_FOLDER = "tmp.data.credential.folder";
    public static final String CONFIG_PROPERTY_UPLOAD_FILE_CREDENTIAL_REGEX = "upload.file.credential.regex";

    public static final String CONFIG_PROPERTY_WALLET_API_URL = "wallet.api.base.url";
    public static final String CONFIG_PROPERTY_WALLET_ADD_PATH = "wallet.credential.add.path";
    public static final String CONFIG_PUBLICATIONS_RDF_SPARQL_ENDPOINT = "publications.rdf.sparql.endpoint";
    public static final String CONFIG_PROPERTY_WALLET_ADD_EMAIL_PATH = "wallet.credential.add.email.path";

    public static final String CONFIG_PROPERTY_ALLOW_QSEALS_ONLY = "allow.qseals.only";

    public static final String ERRORMESSAGE_GENERIC = "A server error has ocurred - please contact you administrator";
    public static final String ERRORMESSAGE_BADREQUEST = "The request sent has not the correct format, please change format and resend";

    public static final String TEMPLATES_DIRECTORY = "templates/xls";

    public static final String MAIL_TEMPLATES_DIRECTORY = "templates/mail/";
    public static final String MAIL_ISSUED_TEMPLATE = "issued_credential_mail";
    public static final String MAIL_WILDCARD_SUBJECT = "[$CREDENTIAL_SUBJECT_NAME$]";
    public static final String MAIL_WILDCARD_ISSUER = "[$ISSUER_NAME$]";
    public static final String MAIL_WILDCARD_TITLE = "[$CREDENTIAL_TITLE$]";
    public static final String MAIL_WILDCARD_VIEWERURL = "[$EDCI_VIEWER_URL$]";

    public static final String SECURITY_PARAMETER_LOGOUT_URL = "security.logout.url";

    public static final String ERROR_NOT_VALID_FORMAT = "The uploaded file has not the correct format, only xls and xlsx are allowed";

    public static final String DEFAULT_LOCALE = "en";
    public static final String TEST_INPUT_EXCEL = "credential_data_input.xlsx";

    public static final String CERTIFICATE_ATTRIBUTE_COUNTRY_NAME = "C";
    public static final String CERTIFICATE_ATTRIBUTE_ORGANIZATION = "O";
    public static final String CERTIFICATE_ATTRIBUTE_ORGANIZATION_IDENTIFIER = "OI";
    public static final String CERTIFICATE_ATTRIBUTE_COMMON_NAME = "CN";

    public static final String CERTIFICATE_BEGIN_MARKER = "-----BEGIN CERTIFICATE-----\n";
    public static final String CERTIFICATE_END_MARKER = "-----END CERTIFICATE-----";

    public static final String XML_TAG_EUROPASS_CREDENTIAL = "europassCredential";
    public static final String XML_TAG_ISSUED = "issued";
    public static final String XML_TAG_ISSUER = "issuer";
    public static final String XML_TAG_PREFERRED_NAME = "preferredName";
    public static final String XML_TAG_ALTERNATIVE_NAME = "alternativeName";
    public static final String XML_TAG_LEGAL_IDENTIFIER = "legalIdentifier";
    public static final String XML_TAG_HAS_LOCATION = "hasLocation";
    public static final String XML_TAG_HAS_ADDRESS = "hasAddress";
    public static final String XML_TAG_COUNTRY_CODE = "countryCode";
    public static final String XML_TAG_TEXT = "text";
    public static final String XML_TAG_CONTENT = "content";
    public static final String XML_ATTRIBUTE_LANGUAGE = "language";

    public static final String CREDENTIAL_TYPE_EUROPASS_CREDENTIAL = "EC";
    public static final String CREDENTIAL_TYPE_EUROPASS_PRESENTATION = "VP";

}
