package eu.europa.ec.empl.edci.viewer.common.constants;

import eu.europa.ec.empl.edci.constants.EDCIConfig;

public class ViewerConfig extends EDCIConfig {
    public class Viewer {

        public static final String APP_HOST = "app.host";
        public static final String WALLET_VERIFY_XML_URL = "wallet.verify.xml.url";
        public static final String WALLET_DOWNLOAD_XML = "wallet.download.xml.url";
        public static final String WALLET_DOWNLOAD_DIPLOMA = "wallet.download.diploma.url";
        public static final String WALLET_DOWNLOAD_SHARED_XML = "wallet.download.shared.xml.url";
        public static final String WALLET_DOWNLAOD_VERIFIABLE_PRESENTATION_URL = "wallet.download.verifiable.presentation.url";
        public static final String WALLET_DOWNLAOD_VERIFIABLE_FROM_FILE_PRESENTATION_URL = "wallet.download.verifiable.from.file.presentation.url";
        public static final String WALLET_DOWNLOAD_SHARED_VERIFIABLE_PRESENTATION_URL = "wallet.download.shared.verifiable.presentation.url";
        public static final String WALLET_DOWNLOAD_SHARED_VERIFICATION_URL = "wallet.download.shared.verification.url";
        public static final String WALLET_SHARELINK_FETCH = "wallet.get.sharelink.fetch.url";
        public static final String WALLET_SHARELINK_CREATE = "wallet.sharelink.create.url";
        public static final String WALLET_VERIFY_ID_URL = "wallet.verify.id.url";

    }
    public class Path {
        public static final String SECURITY_FILE = "file:${edci.properties.base}/viewer/security.properties";
        public static final String VIEWER_FILE = "file:${edci.properties.base}/viewer/viewer.properties";
        public static final String FRONT_FILE = "file:${edci.properties.base}/viewer/viewer_front.properties";
    }

}
