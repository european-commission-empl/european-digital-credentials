package eu.europa.ec.empl.edci.config.service;

import org.apache.tomcat.dbcp.dbcp2.BasicDataSource;
import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.instrument.classloading.ReflectiveLoadTimeWeaver;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.EclipseLinkJpaDialect;
import org.springframework.orm.jpa.vendor.EclipseLinkJpaVendorAdapter;

import javax.sql.DataSource;
import java.util.Properties;

@Configuration
public interface IDBConfigService {

    abstract String getString(String key);

    abstract String getString(String key, String defaultValue);

    abstract BasicDataSource getDataSource();

    abstract void setDataSource(BasicDataSource dataSource);

    abstract String getPersistenceUnitName();

    abstract String getPackagesToScan();

    default DataSource dataSource() {

        if (this.getDataSource() == null) {
            BasicDataSource basicDataSource = new BasicDataSource();
            basicDataSource.setDriverClassName(getString("datasource.db.driverClassName"));
            basicDataSource.setUrl(getString("datasource.db.url"));
            basicDataSource.setUsername(getString("datasource.db.username"));
            basicDataSource.setPassword(getString("datasource.db.password"));
            basicDataSource.setDefaultAutoCommit(false);
            this.setDataSource(basicDataSource);
        }

        return this.getDataSource();
    }

    @Bean
    default LocalContainerEntityManagerFactoryBean entityManagerFactory() {
        EclipseLinkJpaVendorAdapter vendorAdapter = new EclipseLinkJpaVendorAdapter();
        vendorAdapter.setGenerateDdl(true);
        vendorAdapter.setShowSql(true);

        LocalContainerEntityManagerFactoryBean factory =
                new LocalContainerEntityManagerFactoryBean();
        factory.setPersistenceUnitName(this.getPersistenceUnitName());
        factory.setJpaVendorAdapter(vendorAdapter);
        factory.setJpaDialect(new EclipseLinkJpaDialect());
        factory.setDataSource(dataSource());
        factory.setLoadTimeWeaver(new ReflectiveLoadTimeWeaver());
        factory.setPackagesToScan(this.getPackagesToScan());
        factory.setJpaProperties(jpaProperties());
        factory.afterPropertiesSet();

        return factory;
    }

    @Bean
    default PersistenceExceptionTranslationPostProcessor persistenceExceptionTranslationPostProcessor() {
        return new PersistenceExceptionTranslationPostProcessor();
    }

    @Bean
    default JpaTransactionManager transactionManager() {
        JpaTransactionManager txManager = new JpaTransactionManager();
        txManager.setEntityManagerFactory(entityManagerFactory().getObject());
        return txManager;
    }

    default Properties jpaProperties() {
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
}
