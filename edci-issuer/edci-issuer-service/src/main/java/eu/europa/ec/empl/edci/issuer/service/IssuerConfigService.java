package eu.europa.ec.empl.edci.issuer.service;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import eu.europa.ec.empl.edci.config.service.IConfigService;
import eu.europa.ec.empl.edci.constants.Defaults;
import eu.europa.ec.empl.edci.constants.EDCIConfig;
import eu.europa.ec.empl.edci.dss.config.EdciJdbcCacheCRLSource;
import eu.europa.ec.empl.edci.issuer.common.constants.EDCIIssuerConfig;
import eu.europa.ec.empl.edci.issuer.common.constants.EDCIIssuerConstants;
import eu.europa.ec.empl.edci.issuer.common.model.ConfigDTO;
import eu.europa.ec.empl.edci.issuer.service.dss.config.SchedulingConfig;
import eu.europa.ec.empl.edci.security.oidc.EDCIAuthenticationSuccessHandler;
import eu.europa.ec.empl.edci.util.proxy.EDCIProxyConfig;
import eu.europa.ec.empl.edci.util.proxy.EDCIProxyProperties;
import eu.europa.esig.dss.service.crl.OnlineCRLSource;
import eu.europa.esig.dss.service.http.commons.CommonsDataLoader;
import eu.europa.esig.dss.service.http.commons.OCSPDataLoader;
import eu.europa.esig.dss.service.http.commons.TimestampDataLoader;
import eu.europa.esig.dss.service.http.proxy.ProxyConfig;
import eu.europa.esig.dss.service.http.proxy.ProxyProperties;
import eu.europa.esig.dss.service.ocsp.JdbcCacheOCSPSource;
import eu.europa.esig.dss.service.ocsp.OnlineOCSPSource;
import eu.europa.esig.dss.service.tsp.OnlineTSPSource;
import eu.europa.esig.dss.spi.client.http.DataLoader;
import eu.europa.esig.dss.spi.x509.KeyStoreCertificateSource;
import eu.europa.esig.dss.tsl.service.TSLRepository;
import eu.europa.esig.dss.tsl.service.TSLValidationJob;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.*;
import org.springframework.core.env.Environment;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Primary
@Configuration
@PropertySources({
        @PropertySource(value = "classpath:/config/security/security_${spring.profiles.active}.properties", ignoreResourceNotFound = false),
        @PropertySource(value = "classpath:/config/issuer/issuer_${spring.profiles.active}.properties", ignoreResourceNotFound = false),
        @PropertySource(EDCIIssuerConfig.Path.ISSUER_FILE),
        @PropertySource(EDCIIssuerConfig.Path.ISSUER_DSS_FILE),
        @PropertySource(EDCIIssuerConfig.Path.MAIL_FILE),
        @PropertySource(EDCIIssuerConfig.Path.PROXY_FILE),
        @PropertySource(EDCIIssuerConfig.Path.SECURITY_FILE),
})
@Import({SchedulingConfig.class})
public class IssuerConfigService implements IConfigService {


    @Autowired
    private Environment env;

    @Autowired
    private ConfigDBService dbService;

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
            //httpProperties.setExcludedHosts(httpExcludedHosts);
            config.setHttpProperties(httpProperties);
        }
        if (httpsEnabled) {
            EDCIProxyProperties httpsProperties = new EDCIProxyProperties();
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
            //httpProperties.setExcludedHosts(httpExcludedHosts);
            config.setHttpProperties(httpProperties);
        }
        if (httpsEnabled) {
            ProxyProperties httpsProperties = new ProxyProperties();
            httpsProperties.setHost(proxyConfig.getHttpsProperties().getHost());
            httpsProperties.setPort(proxyConfig.getHttpsProperties().getPort());
            httpsProperties.setUser(proxyConfig.getHttpsProperties().getUser());
            httpsProperties.setPassword(proxyConfig.getHttpsProperties().getPassword());
            //httpsProperties.setExcludedHosts(httpsExcludedHosts);
            config.setHttpsProperties(httpsProperties);
        }
        return config;
    }

    @Bean
    public CommonsDataLoader dataLoader() {
        CommonsDataLoader dataLoader = new CommonsDataLoader();
        dataLoader.setProxyConfig(toProxyConfig());
        return dataLoader;
    }


    @Bean
    public DataSource dataSource() {
        HikariConfig config = new HikariConfig();
        config.setDataSourceJNDI(this.env.getProperty("jndi.datasource.name"));
        config.setAutoCommit(false);
        HikariDataSource hikariDataSource = new HikariDataSource(config);
        //hikariDataSource.setPoolName("DSS-Hikari-Pool-Issuer");
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
        OCSPDataLoader ocspDataLoader = new OCSPDataLoader();
        ocspDataLoader.setProxyConfig(toProxyConfig());
        onlineOCSPSource.setDataLoader(ocspDataLoader);
        return onlineOCSPSource;
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

    /**
     * OIDC CONFIG
     */


    @Bean
    public LoginUrlAuthenticationEntryPoint authenticationEntryPoint() {
        LoginUrlAuthenticationEntryPoint loginUrlAuthenticationEntryPoint = new LoginUrlAuthenticationEntryPoint(this.getString(EDCIConfig.OIDC_LOGIN_URL));
        return loginUrlAuthenticationEntryPoint;
    }

    @Bean
    public EDCIAuthenticationSuccessHandler EDCIAuthenticationSuccessHandler() {
        EDCIAuthenticationSuccessHandler edciAuthenticationSuccessHandler = new EDCIAuthenticationSuccessHandler();
        edciAuthenticationSuccessHandler.setDefaultTargetUrl(this.getString(EDCIConfig.OIDC_SUCCESS_DEFAULT_URL));
        return edciAuthenticationSuccessHandler;
    }

    /**
     * PUBLIC METHODS
     */

    public Defaults.Environment getCurrentEnvironment() {
        return Defaults.Environment.valueOf(this.getString(EDCIIssuerConstants.CONFIG_PROPERTY_ACTIVE_PROFILE));
    }

    public List<ConfigDTO> getDatabaseConfiguration() {
        return this.getDbService().getConfiguration();
    }

    public List<ConfigDTO> saveDatabaseConfiguration(List<ConfigDTO> configDTOS) {
        return this.getDbService().setConfiguration(configDTOS);
    }

    public String getDatabaseConfigurationValue(String key) {
        ConfigDTO configDAO = this.getDbService().findByKey(key);
        return configDAO == null ? null : configDAO.getValue();
    }

    public List<ConfigDTO> populateDatabaseWithProperties(List<String> keys) {
        return keys.stream().map(key -> this.populateDatabaseWithProperty(key)).collect(Collectors.toList());
    }

    public ConfigDTO populateDatabaseWithProperty(String key) {
        String propertyValue = this.getString(key);
        ConfigDTO configDTO = this.getDbService().findByKey(key);

        if (propertyValue != null) {

            if (configDTO == null) {
                configDTO = new ConfigDTO();
                configDTO.setKey(key);
            }

            configDTO.setValue(propertyValue);
        }

        return this.getDbService().save(configDTO);
    }

    public ConfigDBService getDbService() {
        return dbService;
    }

    public void setDbService(ConfigDBService dbService) {
        this.dbService = dbService;
    }
}
