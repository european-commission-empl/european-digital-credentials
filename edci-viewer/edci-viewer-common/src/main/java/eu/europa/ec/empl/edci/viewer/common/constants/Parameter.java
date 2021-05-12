package eu.europa.ec.empl.edci.viewer.common.constants;

public class Parameter {

    public class Path {
        //Format: "/{PARAMETER_NAME}"
        public static final String UUID = "/{" + Parameter.UUID + "}";
        public static final String SHARE_HASH = "/{" + Parameter.SHARE_HASH + "}";
        public static final String WALLET_USER_ID = "/{" + Parameter.WALLET_USER_ID + "}"; //TODO: Replace with USER_ID (EDCI-668 Format walletAddress)

    }

    public static final String UUID = "_uuid";
    public static final String SHARE_HASH = "_shareHash";
    public static final String FILE = "file";
    public static final String WALLET_USER_ID = "_userId";
    public static final String LOCALE = "locale";

}
