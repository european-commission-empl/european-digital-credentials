package eu.europa.ec.empl.edci.issuer.liquibase;

import liquibase.integration.spring.SpringLiquibase;
import org.apache.tomcat.dbcp.dbcp2.BasicDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.core.env.Environment;

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
        liquibase.setDataSource(dataSource());

        return liquibase;
    }
}
