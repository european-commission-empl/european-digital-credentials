package eu.europa.ec.empl.edci.wallet.service;

import eu.europa.ec.empl.edci.config.service.IDBConfigService;
import eu.europa.ec.empl.edci.config.service.IMVCConfigService;
import eu.europa.ec.empl.edci.config.service.IOIDCConfigService;
import eu.europa.ec.empl.edci.config.service.ProxyConfigService;
import eu.europa.ec.empl.edci.wallet.common.constants.EDCIWalletConstants;
import eu.europa.ec.empl.edci.wallet.common.constants.WalletConfig;
import eu.europa.ec.empl.edci.wallet.common.model.CredentialDTO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.tomcat.dbcp.dbcp2.BasicDataSource;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Service
@Configuration
@EnableTransactionManagement(proxyTargetClass = true)
@EnableJpaRepositories("eu.europa.ec.empl.edci.wallet.repository")
@PropertySources({
        @PropertySource(value = "classpath:/config/wallet/wallet_${spring.profiles.active}.properties"),
        @PropertySource(value = "classpath:/config/security/security_${spring.profiles.active}.properties", ignoreResourceNotFound = false),
        @PropertySource(WalletConfig.Path.WALLET_FILE),
        @PropertySource(WalletConfig.Path.PROXY_FILE),
        @PropertySource(WalletConfig.Path.SECURITY_FILE),
        @PropertySource(WalletConfig.Path.FRONT_FILE)
})
@Primary
@EnableScheduling
public class WalletConfigService extends ProxyConfigService implements IMVCConfigService, IDBConfigService, IOIDCConfigService {

    public static final Logger logger = LogManager.getLogger(WalletConfigService.class);

    private static BasicDataSource dataSource = null;

    @Override
    public BasicDataSource getDataSource() {
        return dataSource;
    }

    @Override
    public void setDataSource(BasicDataSource dS) {
        dataSource = dS;
    }

    @Override
    public String getPersistenceUnitName() {
        return "walletPersistence";
    }

    @Override
    public String getPackagesToScan() {
        return "eu.europa.ec.empl.edci.wallet";
    }

    public String getViewerURL(CredentialDTO credentialDTO) {
        String viewerURL = EDCIWalletConstants.STRING_BLANK;
        try {
            viewerURL = getString(EDCIWalletConstants.CONFIG_PROPERTY_VIEWER_VIEW_ADDRESSS)
                    .concat(String.valueOf(URLEncoder.encode(String.valueOf(credentialDTO.getUuid()), StandardCharsets.UTF_8.name()))).concat("?walletAddress="+credentialDTO.getWallet().getWalletAddress());
        } catch (UnsupportedEncodingException e) {
            logger.error("[E] - Error creating Viewer URL for credential [{}]", () -> credentialDTO.getPk());
        }
        return viewerURL;
    }

    @Override
    public Map<String, Object> getFrontEndProperties() {
        return this.getPropertiesFromFile(this.getEnv(), WalletConfig.Path.FRONT_FILE);
    }

    @Override
    public Map<String, Object> getBackEndProperties() {
        return this.getPropertiesFromFile(this.getEnv(), WalletConfig.Path.WALLET_FILE);
    }

}
