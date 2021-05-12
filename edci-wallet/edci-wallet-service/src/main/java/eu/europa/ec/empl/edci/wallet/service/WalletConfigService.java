package eu.europa.ec.empl.edci.wallet.service;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import eu.europa.ec.empl.edci.config.service.IConfigService;
import eu.europa.ec.empl.edci.dss.config.EdciJdbcCacheCRLSource;
import eu.europa.ec.empl.edci.wallet.common.constants.EDCIWalletConfig;
import eu.europa.ec.empl.edci.wallet.common.constants.EDCIWalletConstants;
import eu.europa.ec.empl.edci.wallet.common.model.CredentialDTO;
import eu.europa.ec.empl.edci.wallet.service.utils.config.SchedulingConfig;
import eu.europa.esig.dss.service.crl.OnlineCRLSource;
import eu.europa.esig.dss.service.http.commons.CommonsDataLoader;
import eu.europa.esig.dss.service.http.commons.OCSPDataLoader;
import eu.europa.esig.dss.service.http.proxy.ProxyConfig;
import eu.europa.esig.dss.service.http.proxy.ProxyProperties;
import eu.europa.esig.dss.service.ocsp.JdbcCacheOCSPSource;
import eu.europa.esig.dss.service.ocsp.OnlineOCSPSource;
import eu.europa.esig.dss.spi.client.http.DataLoader;
import eu.europa.esig.dss.spi.x509.KeyStoreCertificateSource;
import eu.europa.esig.dss.tsl.service.TSLRepository;
import eu.europa.esig.dss.tsl.service.TSLValidationJob;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.*;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.sql.DataSource;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;

@Service("walletConfigService")
@Configuration
@PropertySources({
        @PropertySource(value = "classpath:/config/wallet/wallet_${spring.profiles.active}.properties"),
        @PropertySource(value = "classpath:/config/security/security_${spring.profiles.active}.properties", ignoreResourceNotFound = false),
        @PropertySource(EDCIWalletConfig.Path.WALLET_FILE),
        @PropertySource(EDCIWalletConfig.Path.WALLET_DSS_FILE),
        @PropertySource(EDCIWalletConfig.Path.MAIL_FILE),
        @PropertySource(EDCIWalletConfig.Path.PROXY_FILE),
        @PropertySource(EDCIWalletConfig.Path.SECURITY_FILE)
})
@Primary
@Import({SchedulingConfig.class})
public class WalletConfigService implements IConfigService {

    public static final Logger logger = Logger.getLogger(WalletConfigService.class);

    @Autowired
    private Environment env;

    public <T> T get(String key, Class<T> clazz) {
        return this.env.getProperty(key, clazz);
    }

    public <T> T get(String key, Class<T> clazz, T defaultValue) {
        return this.env.getProperty(key, clazz, defaultValue);
    }

    @Bean
    public ProxyConfig proxyConfig() {
        boolean httpEnabled = getBoolean("proxy.http.enabled");
        boolean httpsEnabled = getBoolean("proxy.https.enabled");
        if (!httpEnabled && !httpsEnabled) {
            return null;
        }
        ProxyConfig config = new ProxyConfig();
        if (httpEnabled) {
            ProxyProperties httpProperties = new ProxyProperties();
            httpProperties.setHost(getString("proxy.http.host"));
            httpProperties.setPort(getInteger("proxy.http.port"));
            httpProperties.setUser(getString("proxy.http.user"));
            httpProperties.setPassword(getString("proxy.http.pwd"));
            //httpProperties.setExcludedHosts(httpExcludedHosts);
            config.setHttpProperties(httpProperties);
        }
        if (httpsEnabled) {
            ProxyProperties httpsProperties = new ProxyProperties();
            httpsProperties.setHost(getString("proxy.https.host"));
            httpsProperties.setPort(getInteger("proxy.https.port"));
            httpsProperties.setUser(getString("proxy.https.user"));
            httpsProperties.setPassword(getString("proxy.https.pwd"));
            //httpsProperties.setExcludedHosts(httpsExcludedHosts);
            config.setHttpsProperties(httpsProperties);
        }
        return config;
    }

    @Bean
    public CommonsDataLoader dataLoader() {
        CommonsDataLoader dataLoader = new CommonsDataLoader();
        dataLoader.setProxyConfig(proxyConfig());
        return dataLoader;
    }


    @Bean
    public DataSource dataSource() {
        HikariConfig config = new HikariConfig();
        config.setDataSourceJNDI(this.env.getProperty("jndi.datasource.name"));
        config.setAutoCommit(false);
        HikariDataSource hikariDataSource = new HikariDataSource(config);
        //hikariDataSource.setPoolName("DSS-Hikari-Pool-Wallet");
        return hikariDataSource;
    }

    @PostConstruct
    public void cachedCRLSourceInitialization() throws SQLException {
        EdciJdbcCacheCRLSource jdbcCacheCRLSource = cachedCRLSource();
        jdbcCacheCRLSource.initTable();
    }

    @PostConstruct
    public void cachedOCSPSourceInitialization() throws SQLException {
        JdbcCacheOCSPSource jdbcCacheOCSPSource = cachedOCSPSource();
        jdbcCacheOCSPSource.initTable();
    }

    @PreDestroy
    public void cachedCRLSourceClean() throws SQLException {
        EdciJdbcCacheCRLSource jdbcCacheCRLSource = cachedCRLSource();
        jdbcCacheCRLSource.destroyTable();
    }

    @PreDestroy
    public void cachedOCSPSourceClean() throws SQLException {
        JdbcCacheOCSPSource jdbcCacheOCSPSource = cachedOCSPSource();
        jdbcCacheOCSPSource.destroyTable();
    }


    @Bean
    public EdciJdbcCacheCRLSource cachedCRLSource() {
        EdciJdbcCacheCRLSource jdbcCacheCRLSource = new EdciJdbcCacheCRLSource();
        jdbcCacheCRLSource.setDataSource(dataSource());
        jdbcCacheCRLSource.setProxySource(onlineCRLSource(dataLoader()));
        jdbcCacheCRLSource.setDefaultNextUpdateDelay((long) (60 * 3)); // 3
        return jdbcCacheCRLSource;
    }


    @Bean
    public JdbcCacheOCSPSource cachedOCSPSource() {
        JdbcCacheOCSPSource jdbcCacheOCSPSource = new JdbcCacheOCSPSource();
        jdbcCacheOCSPSource.setDataSource(dataSource());
        jdbcCacheOCSPSource.setProxySource(onlineOcspSource(dataLoader()));
        jdbcCacheOCSPSource.setDefaultNextUpdateDelay((long) (1000 * 60 * 3)); // 3 minutes
        return jdbcCacheOCSPSource;
    }


    @Bean
    public OnlineCRLSource onlineCRLSource(DataLoader dataLoader) {
        OnlineCRLSource onlineCRLSource = new OnlineCRLSource();
        onlineCRLSource.setDataLoader(dataLoader);
        return onlineCRLSource;
    }

    @Bean
    public OnlineOCSPSource onlineOcspSource(DataLoader dataLoader) {
        OnlineOCSPSource onlineOCSPSource = new OnlineOCSPSource();
        onlineOCSPSource.setDataLoader(new OCSPDataLoader());
        return onlineOCSPSource;
    }

    @Bean
    public TSLValidationJob tslValidationJob(DataLoader dataLoader, TSLRepository tslRepository, KeyStoreCertificateSource ojContentKeyStore) {
        TSLValidationJob job = new TSLValidationJob();
        job.setDataLoader(dataLoader);
        job.setOjContentKeyStore(ojContentKeyStore);
        job.setLotlUrl("https://ec.europa.eu/tools/lotl/eu-lotl.xml");
        job.setOjUrl("https://eur-lex.europa.eu/legal-content/EN/TXT/?uri=uriserv:OJ.C_.2019.276.01.0001.01.ENG");
        job.setLotlCode("EU");
        job.setRepository(tslRepository);
        return job;
    }

    public String getViewerURL(CredentialDTO credentialDTO) {
        String viewerURL = EDCIWalletConstants.STRING_BLANK;
        try {

            viewerURL = getString(EDCIWalletConstants.CONFIG_PROPERTY_VIEWER_VIEW_ADDRESSS)
                    .concat(credentialDTO.getWalletDTO().getUserId()).concat(EDCIWalletConstants.STRING_SLASH).concat(String.valueOf(URLEncoder.encode(String.valueOf(credentialDTO.getUuid()), StandardCharsets.UTF_8.name())));
        } catch (UnsupportedEncodingException e) {
            logger.error(String.format("[E] - Error creating Viewer URL for credential [%d]", credentialDTO.getPk()));
        }
        return viewerURL;
    }

}
