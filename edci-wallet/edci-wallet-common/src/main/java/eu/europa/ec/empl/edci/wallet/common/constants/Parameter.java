package eu.europa.ec.empl.edci.wallet.common.constants;

import eu.europa.ec.empl.edci.constants.EDCIParameter;

public class Parameter {

    public class Path {
        //Format: "/{PARAMETER_NAME}"
        public static final String UUID = "/{" + Parameter.UUID + "}";
        public static final String SHARED_HASH = "/{" + Parameter.SHARED_HASH + "}";
        public static final String USER_ID = "/{" + Parameter.USER_ID + "}";
        public static final String LOCALE = "/{" + Parameter.LOCALE + "}";
        public static final String CRED_ID = "/{" + Parameter.CRED_ID + "}";
        public static final String CRED_XML = "/{" + EDCIParameter.WALLET_ADD_CREDENTIAL_XML + "}";
        public static final String USER_EMAIL = "/{" + Parameter.USER_EMAIL + "}";
        public static final String ID = "/{" + Parameter.ID + "}";
    }

    public static final String SHARED_HASH = "_shareHash";
    public static final String USER_ID = "userId";
    public static final String USER_EMAIL = "userId";
    public static final String LOCALE = "locale";
    public static final String CRED_ID = "credId";
    public static final String ID = "id";
    public static final String UUID = "uuid";
    public static final String PDF_TYPE = "pdfType";
    public static final String PDF_EXP_DATE = "expirationDate";
    public static final String SEND_MAIL = "sendEmail";

    public static final String SORT = "sort";
    public static final String DIRECTION = "direction";
    public static final String PAGE = "page";
    public static final String SIZE = "size";
    public static final String SEARCH = "search";
    public static final String SH_USER_ID = "userId";
    public static final String SH_EXPIRED = "expired";
}
