package eu.europa.ec.empl.edci.wallet.common.constants;

import eu.europa.ec.empl.edci.constants.EDCIConfig;

public class WalletConfig extends EDCIConfig {
    public class Path {
        public static final String SECURITY_FILE = "file:${edci.properties.base}/wallet/security.properties";
        public static final String WALLET_FILE = "file:${edci.properties.base}/wallet/wallet.properties";
        public static final String PROXY_FILE = "file:${edci.properties.base}/wallet/proxy.properties";
        public static final String FRONT_FILE = "file:${edci.properties.base}/wallet/wallet_front.properties";
    }
}
