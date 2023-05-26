package eu.europa.ec.empl.edci.dss.config;

import eu.europa.ec.empl.edci.dss.constants.ESealConfig;
import eu.europa.ec.empl.edci.dss.model.proxy.EDCIProxyConfig;
import eu.europa.ec.empl.edci.dss.model.proxy.EDCIProxyProperties;
import eu.europa.esig.dss.alert.ExceptionOnStatusAlert;
import eu.europa.esig.dss.service.crl.OnlineCRLSource;
import eu.europa.esig.dss.service.http.commons.CommonsDataLoader;
import eu.europa.esig.dss.service.http.commons.FileCacheDataLoader;
import eu.europa.esig.dss.service.http.commons.OCSPDataLoader;
import eu.europa.esig.dss.service.http.commons.TimestampDataLoader;
import eu.europa.esig.dss.service.http.proxy.ProxyConfig;
import eu.europa.esig.dss.service.http.proxy.ProxyProperties;
import eu.europa.esig.dss.service.ocsp.JdbcCacheOCSPSource;
import eu.europa.esig.dss.service.ocsp.OnlineOCSPSource;
import eu.europa.esig.dss.service.tsp.OnlineTSPSource;
import eu.europa.esig.dss.spi.client.jdbc.JdbcCacheConnector;
import eu.europa.esig.dss.spi.tsl.TrustedListsCertificateSource;
import eu.europa.esig.dss.tsl.cache.CacheCleaner;
import eu.europa.esig.dss.tsl.job.TLValidationJob;
import eu.europa.esig.dss.tsl.source.LOTLSource;
import eu.europa.esig.dss.validation.CommonCertificateVerifier;
import org.apache.hc.client5.http.ssl.TrustAllStrategy;
import org.apache.tomcat.dbcp.dbcp2.BasicDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.*;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.sql.DataSource;
import java.sql.SQLException;

@Service
@Configuration
@EnableTransactionManagement
@PropertySources({
        @PropertySource(value = "classpath:/config/eseal/eseal_${spring.profiles.active}.properties", ignoreResourceNotFound = false),
        @PropertySource(ESealConfig.Path.ESEAL_FILE),
        @PropertySource(ESealConfig.Path.PROXY_FILE)
})
@Import({SchedulingConfig.class})
public class ESealCoreConfigService {

    @Autowired
    private Environment env;

    private static BasicDataSource dataSource = null;

    public <T> T get(String key, Class<T> clazz) {
        return this.env.getProperty(key, clazz);
    }

    public <T> T get(String key, Class<T> clazz, T defaultValue) {
        return this.env.getProperty(key, clazz, defaultValue);
    }

    public Boolean getBoolean(String key) {
        return this.get(key, Boolean.class, false);
    }

    public Boolean getBoolean(String key, Boolean defaultValue) {
        return this.get(key, Boolean.class, defaultValue);
    }

    public Long getLong(String key) {
        return this.get(key, Long.class, 0L);
    }

    public Long getLong(String key, Long defaultValue) {
        return this.get(key, Long.class, defaultValue);
    }

    public Integer getInteger(String key) {
        return this.get(key, Integer.class, 0);
    }

    public Integer getInteger(String key, Integer defaultValue) {
        return this.get(key, Integer.class, defaultValue);
    }

    public String getString(String key) {
        return this.get(key, String.class);
    }

    public String getString(String key, String defaultValue) {
        return this.get(key, String.class, defaultValue);
    }

    public String[] getStringArray(String key) {
        String value = this.getString(key);
        return value != null ? value.split(",") : null;
    }

    public DataSource dataSource() {

        if (dataSource == null) {
            dataSource = new BasicDataSource();
            dataSource.setDriverClassName(getString(ESealConfig.Properties.DRIVER_CLASS_NAME));
            dataSource.setUrl(getString(ESealConfig.Properties.DATABASE_URL));
            dataSource.setUsername(getString(ESealConfig.Properties.DATABASE_USERNAME));
            dataSource.setPassword(getString(ESealConfig.Properties.DATABASE_PASSWORD));
            dataSource.setDefaultAutoCommit(false);
        }

        return dataSource;
    }

    @Bean(name = "esealCommonsDataLoader")
    public CommonsDataLoader dataLoader() {
        CommonsDataLoader dataLoader = new CommonsDataLoader();
        dataLoader.setProxyConfig(toProxyEsealConfig());
        dataLoader.setTrustStrategy(TrustAllStrategy.INSTANCE);
        return dataLoader;
    }

    @Bean
    public OCSPDataLoader ocspDataLoader() {
        OCSPDataLoader ocspDataLoader = new OCSPDataLoader();
        ocspDataLoader.setProxyConfig(toProxyEsealConfig());
        ocspDataLoader.setTrustStrategy(TrustAllStrategy.INSTANCE);
        return ocspDataLoader;
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
        EdciJdbcCacheCRLSource jdbcCacheCRLSource = new EdciJdbcCacheCRLSource(this.getString(ESealConfig.Properties.TARGET_DATABASE, ESealConfig.Defaults.TARGET_DATABASE));
        OnlineCRLSource onlineCRLSource = new OnlineCRLSource();
        onlineCRLSource.setDataLoader(this.dataLoader());
        JdbcCacheConnector jdbcCacheConnector = new JdbcCacheConnector(dataSource());
        jdbcCacheCRLSource.setJdbcCacheConnector(jdbcCacheConnector);
        jdbcCacheCRLSource.setProxySource(onlineCRLSource);
        jdbcCacheCRLSource.setDefaultNextUpdateDelay(this.getLong(ESealConfig.Properties.CRL_REFRESH, ESealConfig.Defaults.CRL_REFRESH));
        jdbcCacheCRLSource.setMaxNextUpdateDelay(this.getLong(ESealConfig.Properties.CRL_REFRESH, ESealConfig.Defaults.CRL_REFRESH));
        jdbcCacheCRLSource.setRemoveExpired(true);
        return jdbcCacheCRLSource;
    }

    @Bean
    public JdbcCacheOCSPSource cacheOCSPSource() {
        JdbcCacheOCSPSource jdbcCacheOCSPSource = new JdbcCacheOCSPSource();
        OnlineOCSPSource onlineOCSPSource = new OnlineOCSPSource();
        onlineOCSPSource.setDataLoader(this.ocspDataLoader());
        JdbcCacheConnector jdbcCacheConnector = new JdbcCacheConnector(dataSource());
        jdbcCacheOCSPSource.setJdbcCacheConnector(jdbcCacheConnector);
        jdbcCacheOCSPSource.setProxySource(onlineOCSPSource);
        jdbcCacheOCSPSource.setDefaultNextUpdateDelay(this.getLong(ESealConfig.Properties.OCSP_REFRESH, ESealConfig.Defaults.OCSP_REFRESH));
        jdbcCacheOCSPSource.setMaxNextUpdateDelay(this.getLong(ESealConfig.Properties.OCSP_REFRESH, ESealConfig.Defaults.OCSP_REFRESH));
        return jdbcCacheOCSPSource;
    }


    @Bean
    public OnlineTSPSource onlineTSPSource() {
        OnlineTSPSource onlineTSPSource = new OnlineTSPSource(getString("eseal.tsp.server", ESealConfig.Properties.DSS_PATH));
        TimestampDataLoader timestampDataLoader = new TimestampDataLoader();
        timestampDataLoader.setProxyConfig(toProxyEsealConfig());

        onlineTSPSource.setDataLoader(timestampDataLoader);
        return onlineTSPSource;
    }

    @Bean
    public TLValidationJob tlValidationJob() {
        LOTLSource lotlSource = new LOTLSource();
        lotlSource.setUrl(this.getString(ESealConfig.Properties.LOTL_SOURCE, ESealConfig.Defaults.LOTL_SOURCE));
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
//        commonCertificateVerifier.setDataLoader(this.dataLoader()); //Removed from 5.8 to 5.9
        commonCertificateVerifier.setTrustedCertSources(trustedListsCertificateSource());
        commonCertificateVerifier.setOcspSource(this.cacheOCSPSource());
        commonCertificateVerifier.setCrlSource(this.cacheCRLSource());
        commonCertificateVerifier.setAlertOnMissingRevocationData(new ExceptionOnStatusAlert());
        return commonCertificateVerifier;

    }

    public EDCIProxyConfig proxyEDCIEsealConfig() {
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
    public ProxyConfig toProxyEsealConfig() {
        EDCIProxyConfig proxyConfig = proxyEDCIEsealConfig();
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

}
