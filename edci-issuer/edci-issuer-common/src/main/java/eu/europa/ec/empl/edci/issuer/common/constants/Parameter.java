package eu.europa.ec.empl.edci.issuer.common.constants;

public class Parameter {

    public class Path {
        //Format: "/{PARAMETER_NAME}"
        public static final String FILE = "/{" + Parameter.FILE + "}";
        public static final String SIGNATURE_PARAMS = "/{" + Parameter.SIGNATURE_PARAMS + "}";
        public static final String EMAIL = "/{" + Parameter.EMAIL + "}";
        public static final String EXCEL_TYPE = "/{" + Parameter.EXCEL_TYPE + "}";
        public static final String UUID = "/{" + Parameter.UUID + "}";
        public static final String CREDENTIALXML = "/{" + Parameter.CREDENTIALXML + "}";
        public static final String ID = "/{" + Parameter.ID + "}";
        public static final String OID = "/{" + Parameter.OID + "}";
        public static final String TYPE = "/{" + Parameter.TYPE + "}";
        public static final String PARENT = "/{" + Parameter.PARENT + "}";
        public static final String REQ_LANGS = "/{" + Parameter.REQ_LANGS + "}";
    }

    public static final String DIPLOMA_TEMPLATE = "_diplomaTemplate";
    public static final String FILE = "_file";
    public static final String SIGNATURE_PARAMS = "_signatureParams";
    public static final String FILES = "_files";
    public static final String EMAIL = "_email";
    public static final String EXCEL_TYPE = "_type";
    public static final String UUID = "_uuid";
    public static final String WALLET_USER_ID = "_userId";
    public static final String WALLET_USER_EMAIL = "_userEmail";
    public static final String CREDENTIALXML = "_credentialXML";
    public static final String ID = "_id";
    public static final String OID = "oid";
    public static final String LOCALE = "locale";
    public static final String PASSWORD = "password";
    public static final String REQ_LANGS = "requestedLangs";
    public static final String SIGN_ON_BEHALF = "signOnBehalf";

    public static final String TYPE = "type";
    public static final String SORT = "sort";
    public static final String DIRECTION = "direction";
    public static final String PAGE = "page";
    public static final String SIZE = "size";
    public static final String SEARCH = "search";
    public static final String URIS = "uris";
    public static final String PARENT = "parent";

}
