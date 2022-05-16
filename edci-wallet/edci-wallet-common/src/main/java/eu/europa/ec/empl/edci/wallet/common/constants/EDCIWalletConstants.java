package eu.europa.ec.empl.edci.wallet.common.constants;

public abstract class EDCIWalletConstants {

    public static final String STRING_EQUAL = "=";
    public static final String STRING_SLASH = "/";
    public static final String STRING_AMPERSAND = "&";
    public static final String STRING_BLANK = "";

    public static final String USER = "USER";
    public static final String FORM = "FORM";

    public static final String CONFIG_PROPERTY_MAIL_SMTP_USER = "mail.smtp.user";
    public static final String CONFIG_PROPERTY_MAIL_SMTP_PASS = "mail.smtp.pass";
    public static final String CONFIG_PROPERTY_MAIL_SMTP_HOST = "mail.smtp.host";
    public static final String CONFIG_PROPERTY_MAIL_SMTP_AUTH = "mail.smtp.auth";
    public static final String CONFIG_PROPERTY_MAIL_DEBUG = "mail.debug";
    public static final String CONFIG_PROPERTY_MAIL_SMTP_STARTTLS = "mail.smtp.starttls.enable";
    public static final String CONFIG_PROPERTY_MAIL_DEFAULT_TEMPLATE = "default.mail.template";

    public static final String CONFIG_PROPERTY_VIEWER_URL = "viewer.url";
    public static final String CONFIG_EUROPASS_URL = "europass.url";

    public static final String MAIL_WILDCARD_CREDENTIALNAME = "[$credentialName$]";
    public static final String MAIL_WILDCARD_FULLNAME = "[$fullName$]";
    public static final String MAIL_WILDCARD_ISSUER = "[$issuer.alternativeName$]";
    public static final String MAIL_WILDCARD_EUROPASSURL = "[$europassURL$]";
    public static final String MAIL_WILDCARD_VIEWER_URL = "[$viewerURL$]";
    public static final String MAIL_TEMPLATES_DIRECTORY = "templates/mail/";

    public static final String TEMPLATE_MAIL_WALLET_CREATED_CRED = "created_credential_mail";
    public static final String TEMPLATE_MAIL_WALLET_CREATED_TEMPCRED = "temp_credential_mail";

    public static final String VERIFICATION_DIRECTORY = "verification/";
    public static final String INSTITUTIONAL_ACCREDITATION = VERIFICATION_DIRECTORY.concat("sample-orgAccreditation-input");
    public static final String QUALIFICATION_ACCREDITATION = VERIFICATION_DIRECTORY.concat("sample-qfAccreditation-input");

    public static final String VIEWER_URL = "http://viewer.europass.eu/";

    public static final String ERRORMESSAGE_GENERIC = "A server error has ocurred - please contact you administrator";
    public static final String ERRORMESSAGE_BADREQUEST = "The request sent has not the correct format, please change format and resend";

    public static final String CONFIG_PROPERTY_VIEWER_VIEW_ADDRESSS = "viewer.view.full.address";
    public static final String CONFIG_PROPERTY_VIEWER_SHARED_URL = "viewer.shared.url";

    public static final String CONFIG_PROPERTY_ALLOWED_DOMAINS = "allowed.domains";

    public static final String CONFIG_CLEAN_UNACCESSED_TEMP_WALLETS_DAYS = "wallets.clean.old.days"; //0 means disabled
    public static final String CONFIG_CLEAN_OLD_SHARELINKS = "sharelinks.clean.old.enabled";
    public static final String CONFIG_CLEAN_UNACCESSED_TEMP_WALLETS_WITH_NO_CRED = "wallets.clean.old.with.no.cred";

    public static final String VERIFIABLE_PRESENTATION_ID_PREFIX = "ID";

    public static final String CREDENTIAL_STORED_TYPE_EUROPASS_CREDENTIAL = "EC";
    public static final String CREDENTIAL_STORED_TYPE_EUROPASS_PRESENTATION = "EP";

    public static final String CREDENTIAL_PDF_TYPE_FULL = "full";
    public static final String CREDENTIAL_PDF_TYPE_DIPLOMA = "diploma";


}
