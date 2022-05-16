package eu.europa.ec.empl.edci.wallet.service;

import eu.europa.ec.empl.edci.config.service.IConfigService;
import eu.europa.ec.empl.edci.constants.EDCIConfig;
import eu.europa.ec.empl.edci.dss.config.EdciJdbcCacheCRLSource;
import eu.europa.ec.empl.edci.util.proxy.EDCIProxyConfig;
import eu.europa.ec.empl.edci.util.proxy.EDCIProxyProperties;
import eu.europa.ec.empl.edci.wallet.common.constants.EDCIWalletConstants;
import eu.europa.ec.empl.edci.wallet.common.constants.WalletConfig;
import eu.europa.ec.empl.edci.wallet.common.model.CredentialDTO;
import eu.europa.ec.empl.edci.wallet.service.utils.config.SchedulingConfig;
import eu.europa.esig.dss.alert.ExceptionOnStatusAlert;
import eu.europa.esig.dss.service.crl.OnlineCRLSource;
import eu.europa.esig.dss.service.http.commons.CommonsDataLoader;
import eu.europa.esig.dss.service.http.commons.FileCacheDataLoader;
import eu.europa.esig.dss.service.http.commons.TimestampDataLoader;
import eu.europa.esig.dss.service.http.proxy.ProxyConfig;
import eu.europa.esig.dss.service.http.proxy.ProxyProperties;
import eu.europa.esig.dss.service.ocsp.JdbcCacheOCSPSource;
import eu.europa.esig.dss.service.ocsp.OnlineOCSPSource;
import eu.europa.esig.dss.service.tsp.OnlineTSPSource;
import eu.europa.esig.dss.spi.tsl.TrustedListsCertificateSource;
import eu.europa.esig.dss.tsl.cache.CacheCleaner;
import eu.europa.esig.dss.tsl.job.TLValidationJob;
import eu.europa.esig.dss.tsl.source.LOTLSource;
import eu.europa.esig.dss.validation.CommonCertificateVerifier;
import org.apache.http.conn.ssl.TrustAllStrategy;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.tomcat.dbcp.dbcp2.BasicDataSource;
import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.*;
import org.springframework.core.env.Environment;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.instrument.classloading.ReflectiveLoadTimeWeaver;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.EclipseLinkJpaDialect;
import org.springframework.orm.jpa.vendor.EclipseLinkJpaVendorAdapter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.sql.DataSource;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.Map;
import java.util.Properties;

@Service("walletConfigService")
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
@Import({SchedulingConfig.class})
public class WalletConfigService implements IConfigService {

    public static final Logger logger = LogManager.getLogger(WalletConfigService.class);

    @Autowired
    private Environment env;

    private static BasicDataSource dataSource = null;

    public <T> T get(String key, Class<T> clazz) {
        return this.env.getProperty(key, clazz);
    }

    public <T> T get(String key, Class<T> clazz, T defaultValue) {
        return this.env.getProperty(key, clazz, defaultValue);
    }

    /**
     * PROXY CONFIGURATION
     **/
    @Bean
    public EDCIProxyConfig proxyEDCIConfig() {
        boolean httpEnabled = getBoolean("proxy.http.enabled");
        boolean httpsEnabled = getBoolean("proxy.https.enabled");
        if (!httpEnabled && !httpsEnabled) {
            return null;
        }
        EDCIProxyConfig config = new EDCIProxyConfig();
        config.setHttpEnabled(httpEnabled);
        config.setHttpsEnabled(httpsEnabled);
        if (httpEnabled) {
            EDCIProxyProperties httpProperties = new EDCIProxyProperties();
            httpProperties.setHost(getString("proxy.http.host"));
            httpProperties.setPort(getInteger("proxy.http.port"));
            httpProperties.setUser(getString("proxy.http.user"));
            httpProperties.setPassword(getString("proxy.http.pwd"));
            config.setHttpProperties(httpProperties);
        }
        if (httpsEnabled) {
            EDCIProxyProperties httpsProperties = new EDCIProxyProperties();
            httpsProperties.setHost(getString("proxy.https.host"));
            httpsProperties.setPort(getInteger("proxy.https.port"));
            httpsProperties.setUser(getString("proxy.https.user"));
            httpsProperties.setPassword(getString("proxy.https.pwd"));
            config.setHttpsProperties(httpsProperties);
        }
        return config;
    }

    @Bean
    public ProxyConfig toProxyConfig() {
        EDCIProxyConfig proxyConfig = proxyEDCIConfig();
        if (proxyConfig == null) {
            return null;
        }
        boolean httpEnabled = proxyConfig.isHttpEnabled();
        boolean httpsEnabled = proxyConfig.isHttpsEnabled();
        if (!httpEnabled && !httpsEnabled) {
            return null;
        }
        ProxyConfig config = new ProxyConfig();
        if (httpEnabled) {
            ProxyProperties httpProperties = new ProxyProperties();
            httpProperties.setHost(proxyConfig.getHttpProperties().getHost());
            httpProperties.setPort(proxyConfig.getHttpProperties().getPort());
            httpProperties.setUser(proxyConfig.getHttpProperties().getUser());
            httpProperties.setPassword(proxyConfig.getHttpProperties().getPassword());
            config.setHttpProperties(httpProperties);
        }
        if (httpsEnabled) {
            ProxyProperties httpsProperties = new ProxyProperties();
            httpsProperties.setHost(proxyConfig.getHttpsProperties().getHost());
            httpsProperties.setPort(proxyConfig.getHttpsProperties().getPort());
            httpsProperties.setUser(proxyConfig.getHttpsProperties().getUser());
            httpsProperties.setPassword(proxyConfig.getHttpsProperties().getPassword());
            config.setHttpsProperties(httpsProperties);
        }
        return config;
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
        EclipseLinkJpaVendorAdapter vendorAdapter = new EclipseLinkJpaVendorAdapter();
        vendorAdapter.setGenerateDdl(true);
        vendorAdapter.setShowSql(true);

        LocalContainerEntityManagerFactoryBean factory =
                new LocalContainerEntityManagerFactoryBean();
        factory.setPersistenceUnitName("walletPersistence");
        factory.setJpaVendorAdapter(vendorAdapter);
        factory.setJpaDialect(new EclipseLinkJpaDialect());
        factory.setDataSource(dataSource());
        factory.setLoadTimeWeaver(new ReflectiveLoadTimeWeaver());
        factory.setPackagesToScan("eu.europa.ec.empl.edci.wallet");
        factory.setJpaProperties(jpaProperties());
        factory.afterPropertiesSet();

        return factory;
    }

    @Bean
    public PersistenceExceptionTranslationPostProcessor persistenceExceptionTranslationPostProcessor() {
        return new PersistenceExceptionTranslationPostProcessor();
    }

    @Bean
    public JpaTransactionManager transactionManager() {
        JpaTransactionManager txManager = new JpaTransactionManager();
        txManager.setEntityManagerFactory(entityManagerFactory().getObject());
        return txManager;
    }

    public Properties jpaProperties() {
        Properties properties = new Properties();
        properties.put("eclipselink.logging.level", getString("log.level.jpa"));
        properties.put("eclipselink.logging.level.sql", getString("log.level.jpa"));
        properties.put("eclipselink.logging.parameters", "true");
        properties.put("eclipselink.deploy-on-startup", "true");
        properties.put("eclipselink.target-database", getString("datasource.db.target-database"));
        properties.put("eclipselink.cache.shared.default", "false");
        properties.put(PersistenceUnitProperties.WEAVING, "static");
        properties.put(PersistenceUnitProperties.DDL_GENERATION, PersistenceUnitProperties.CREATE_OR_EXTEND);
        properties.put(PersistenceUnitProperties.DDL_GENERATION_MODE, PersistenceUnitProperties.DDL_BOTH_GENERATION);
        properties.put(PersistenceUnitProperties.CREATE_JDBC_DDL_FILE, "create.sql");
        properties.put("eclipselink.ddl-generation", getString("datasource.db.ddl-generation", "create-or-extend-tables"));
        return properties;
    }

    public DataSource dataSource() {

        if (dataSource == null) {
            dataSource = new BasicDataSource();
            dataSource.setDriverClassName(getString("datasource.db.driverClassName"));
            dataSource.setUrl(getString("datasource.db.url"));
            dataSource.setUsername(getString("datasource.db.username"));
            dataSource.setPassword(getString("datasource.db.password"));
            dataSource.setDefaultAutoCommit(false);
        }

        return dataSource;
    }

    @Bean
    public CommonsDataLoader dataLoader() {
        CommonsDataLoader dataLoader = new CommonsDataLoader();
        dataLoader.setProxyConfig(toProxyConfig());
        dataLoader.setTrustStrategy(TrustAllStrategy.INSTANCE);
        return dataLoader;
    }

    @PostConstruct
    public void cachedCRLSourceInitialization() throws SQLException {
        EdciJdbcCacheCRLSource jdbcCacheCRLSource = cacheCRLSource();
        jdbcCacheCRLSource.destroyTable();
        jdbcCacheCRLSource.initTable();
    }

    @PreDestroy
    public void cachedCRLSourceClean() throws SQLException {
        EdciJdbcCacheCRLSource jdbcCacheCRLSource = cacheCRLSource();
        jdbcCacheCRLSource.destroyTable();
    }

    @PostConstruct
    public void cachedOCSPSourceInitialization() throws SQLException {
        JdbcCacheOCSPSource jdbcCacheOCSPSource = cacheOCSPSource();
        jdbcCacheOCSPSource.destroyTable();
        jdbcCacheOCSPSource.initTable();
    }


    @PreDestroy
    public void cachedOCSPSourceClean() throws SQLException {
        JdbcCacheOCSPSource jdbcCacheOCSPSource = cacheOCSPSource();
        jdbcCacheOCSPSource.destroyTable();
    }

    @Bean
    public EdciJdbcCacheCRLSource cacheCRLSource() {
        EdciJdbcCacheCRLSource jdbcCacheCRLSource = new EdciJdbcCacheCRLSource(this.getString(EDCIConfig.Database.TARGET_DATABASE, "MySQL"));
        OnlineCRLSource onlineCRLSource = new OnlineCRLSource();
        onlineCRLSource.setDataLoader(this.dataLoader());
        jdbcCacheCRLSource.setDataSource(dataSource());
        jdbcCacheCRLSource.setProxySource(onlineCRLSource);
        jdbcCacheCRLSource.setDefaultNextUpdateDelay(EDCIConfig.Defaults.CRL_REFRESH);
        jdbcCacheCRLSource.setMaxNextUpdateDelay(EDCIConfig.Defaults.CRL_REFRESH);
        jdbcCacheCRLSource.setRemoveExpired(true);
        return jdbcCacheCRLSource;
    }

    @Bean
    public JdbcCacheOCSPSource cacheOCSPSource() {
        JdbcCacheOCSPSource jdbcCacheOCSPSource = new JdbcCacheOCSPSource();
        OnlineOCSPSource onlineOCSPSource = new OnlineOCSPSource();
        onlineOCSPSource.setDataLoader(this.dataLoader());
        jdbcCacheOCSPSource.setDataSource(dataSource());
        jdbcCacheOCSPSource.setProxySource(onlineOCSPSource);
        jdbcCacheOCSPSource.setDefaultNextUpdateDelay(EDCIConfig.Defaults.OCSP_REFRESH);
        jdbcCacheOCSPSource.setMaxNextUpdateDelay(EDCIConfig.Defaults.OCSP_REFRESH);
        return jdbcCacheOCSPSource;
    }


    @Bean
    public OnlineTSPSource onlineTSPSource() {
        OnlineTSPSource onlineTSPSource = new OnlineTSPSource(getString("dss.tsp"));
        TimestampDataLoader timestampDataLoader = new TimestampDataLoader();
        timestampDataLoader.setProxyConfig(toProxyConfig());

        onlineTSPSource.setDataLoader(timestampDataLoader);
        return onlineTSPSource;
    }

    @Bean
    public TLValidationJob tlValidationJob() {
        LOTLSource lotlSource = new LOTLSource();
        lotlSource.setUrl(this.getString(EDCIConfig.DSS.LOTL_SOURCE));
        lotlSource.setPivotSupport(true);
        FileCacheDataLoader onlineFileLoader = new FileCacheDataLoader(dataLoader());
        CacheCleaner cacheCleaner = new CacheCleaner();
        cacheCleaner.setCleanFileSystem(true);
        cacheCleaner.setDSSFileLoader(onlineFileLoader);
        TLValidationJob validationJob = new TLValidationJob();
        validationJob.setTrustedListCertificateSource(this.trustedListsCertificateSource());
        validationJob.setOnlineDataLoader(onlineFileLoader);
        validationJob.setCacheCleaner(cacheCleaner);
        validationJob.setListOfTrustedListSources(lotlSource);
        return validationJob;
    }

    @Bean
    public TrustedListsCertificateSource trustedListsCertificateSource() {
        return new TrustedListsCertificateSource();
    }

    @Bean
    public CommonCertificateVerifier commonCertificateVerifier() {
        CommonCertificateVerifier commonCertificateVerifier = new CommonCertificateVerifier();
        commonCertificateVerifier.setCheckRevocationForUntrustedChains(true);
        commonCertificateVerifier.setDataLoader(this.dataLoader());
        commonCertificateVerifier.setTrustedCertSources(trustedListsCertificateSource());
        commonCertificateVerifier.setOcspSource(this.cacheOCSPSource());
        commonCertificateVerifier.setCrlSource(this.cacheCRLSource());
        commonCertificateVerifier.setAlertOnMissingRevocationData(new ExceptionOnStatusAlert());
        return commonCertificateVerifier;
    }


    public String getViewerURL(CredentialDTO credentialDTO) {
        String viewerURL = EDCIWalletConstants.STRING_BLANK;
        try {

            viewerURL = getString(EDCIWalletConstants.CONFIG_PROPERTY_VIEWER_VIEW_ADDRESSS)
                    .concat(credentialDTO.getWalletDTO().getUserId()).concat(EDCIWalletConstants.STRING_SLASH).concat(String.valueOf(URLEncoder.encode(String.valueOf(credentialDTO.getUuid()), StandardCharsets.UTF_8.name())));
        } catch (UnsupportedEncodingException e) {
            logger.error("[E] - Error creating Viewer URL for credential [{}]", () -> credentialDTO.getPk());
        }
        return viewerURL;
    }

    @Override
    public Map<String, Object> getFrontEndProperties() {
        return this.getPropertiesFromFile(this.env, WalletConfig.Path.FRONT_FILE);
    }

    @Override
    public Map<String, Object> getBackEndProperties() {
        return this.getPropertiesFromFile(this.env, WalletConfig.Path.WALLET_FILE);
    }

}
