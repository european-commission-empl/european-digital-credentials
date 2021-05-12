package eu.europa.ec.empl.edci.wallet.common.constants;

public class EDCIWalletConfig {
    public class Path {
        public static final String SECURITY_FILE = "file:${catalina.base}/conf/edci/wallet/security.properties";
        public static final String WALLET_FILE = "file:${catalina.base}/conf/edci/wallet/wallet.properties";
        public static final String MAIL_FILE = "file:${catalina.base}/conf/edci/wallet/mail.properties";
        public static final String PROXY_FILE = "file:${catalina.base}/conf/edci/wallet/proxy.properties";
        public static final String WALLET_DSS_FILE = "file:${catalina.base}/conf/edci/wallet/wallet_dss.properties";
    }
}
