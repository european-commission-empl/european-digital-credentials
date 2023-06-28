package eu.europa.ec.empl.edci.viewer.common.constants;

import eu.europa.ec.empl.edci.constants.EDCIEndpoint;

public class ViewerEndpoint extends EDCIEndpoint {

    public static final String CONFIG_BASE = "/config";
    public static final String CONFIG_FRONT = "/front-properties";

    public class V1 {

        public static final String CREDENTIALS_BASE = "/credentials";
        public static final String SHARELINKS_BASE = "/sharelinks";
        public static final String USER_BASE = "/users";
        public static final String USER_DETAILS = "/details";

        public static final String ROOT = "";

        public static final String DIPLOMA = "/diploma";
        public static final String DETAILS = "/details";
        public static final String VERIFICATION = "/verification";

        public static final String DOWNLOAD_CREDENTIAL = "/credential";
        public static final String DOWNLOAD_VERIFIABLE = "/verifiable";
        public static final String DOWNLOAD_PRESENTATION = "/presentation";
        public static final String SHARELINK_PRESENTATION = "/presentation";
        public static final String SHARELINK_CREDENTIAL = "/credential";
        public static final String CONVERT_CREDENTIAL = "/convert";
        public static final String VERIFY = "/verify";

    }


}
