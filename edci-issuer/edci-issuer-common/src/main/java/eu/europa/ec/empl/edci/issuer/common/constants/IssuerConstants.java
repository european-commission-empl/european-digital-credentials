package eu.europa.ec.empl.edci.issuer.common.constants;

public abstract class IssuerConstants {

    public static final String USER = "USER";
    public static final String FORM = "FORM";

    public static final String DATE_FORMAT = "dd/MM/yyyy";
    public static final String DATETIME_FORMAT = "dd/MM/yyyy HH:mm";

    public static final String EXCEL_TEMPLATE_PREFIX = "EDC_template-";
    public static final String XML_FILE_PREFIX = "credential-";
    public static final String XML_IDENTIFIERS_PREFIX = "urn:epass:concept:";

    public static final String XLS_PATTERN_FORCENUMBERASTEXT = ".*%";
    public static final String XLS_PATTERN_FORCENUMBERASDATE = "^m\\/d\\/yy$";

    public static final String EXTENSION_XLSX = ".xlsx";
    public static final String EXTENSION_XLSM = ".xlsm";
    public static final String EXTENSION_XLS = ".xls";

    public static final String TEMPLATES_DIRECTORY = "templates/xls";

    public static final String MAIL_TEMPLATES_DIRECTORY = "templates/mail/";
    public static final String MAIL_ISSUED_TEMPLATE = "issued_credential_mail";
    public static final String MAIL_WILDCARD_SUBJECT = "[$CREDENTIAL_SUBJECT_NAME$]";
    public static final String MAIL_WILDCARD_ISSUER = "[$ISSUER_NAME$]";
    public static final String MAIL_WILDCARD_TITLE = "[$CREDENTIAL_TITLE$]";
    public static final String MAIL_WILDCARD_VIEWERURL = "[$EDCI_VIEWER_URL$]";

}
