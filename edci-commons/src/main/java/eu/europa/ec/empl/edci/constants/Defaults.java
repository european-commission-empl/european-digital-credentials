package eu.europa.ec.empl.edci.constants;

public class Defaults {

    public static final String XML_IDENTIFIER_PREFIX = "urn:epass:default:";
    public static final String XML_CRED_UUID_PREFIX = "urn:credential:";
    public static final String DEFAULT_LOCALE = "en";
    public static final String CREDENTIAL_DEFAULT_PREFIX = "Credential-";

    public static final String DEFAULT_MAILTO = "mailto:";

    public static final String[] LEGACY_SCHEMA_LOCATIONS = {"epass_credential_schema_-xsd.xsd"};

    public enum Environment {
        LOCAL, DEV, QA, TEST, ACC, PROD
    }

}
