package eu.europa.ec.empl.edci.wallet.common.constants;

import eu.europa.ec.empl.edci.constants.EDCIConfig;

public class WalletConfig extends EDCIConfig {

    public class Wallet {
        public static final String WALLETS_ROOT_FOLDER = "data.wallets.root.folder";
        public static final String WALLETS_DATA_LOCATION = "data.wallets.location";
    }

    public class Path {
        public static final String SECURITY_FILE = "file:${edci.properties.base}/wallet/security.properties";
        public static final String WALLET_FILE = "file:${edci.properties.base}/wallet/wallet.properties";
        public static final String PROXY_FILE = "file:${edci.properties.base}/proxy.properties";
        public static final String FRONT_FILE = "file:${edci.properties.base}/wallet/wallet_front.properties";
    }
}
