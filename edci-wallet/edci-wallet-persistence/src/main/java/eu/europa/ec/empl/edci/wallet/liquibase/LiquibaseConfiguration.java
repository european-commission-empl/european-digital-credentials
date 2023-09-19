package eu.europa.ec.empl.edci.wallet.liquibase;

import eu.europa.ec.empl.edci.exception.EDCIException;
import liquibase.integration.spring.SpringLiquibase;
import org.apache.tomcat.dbcp.dbcp2.BasicDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.naming.InitialContext;
import javax.sql.DataSource;

@Configuration
public class LiquibaseConfiguration {

    @Autowired
    private Environment env;

    public DataSource dataSource() {
        BasicDataSource dataSource = new BasicDataSource();
        dataSource.setDriverClassName(env.getProperty("datasource.db.driverClassName"));
        dataSource.setUrl(env.getProperty("datasource.db.url"));
        dataSource.setUsername(env.getProperty("datasource.db.username"));
        dataSource.setPassword(env.getProperty("datasource.db.password"));
        return dataSource;
    }

    @Bean
    public SpringLiquibase liquibase() {
        SpringLiquibase liquibase = new SpringLiquibase();
        liquibase.setChangeLog("classpath:changeLog/liquibase-changelog-master.xml");
        //TODO: Set liquibase schema?
        liquibase.setDataSource(dataSource());

        return liquibase;
    }
}
