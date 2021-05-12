package eu.europa.ec.empl.edci.issuer.liquibase;

import eu.europa.ec.empl.edci.exception.EDCIException;
import liquibase.integration.spring.SpringLiquibase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.core.env.Environment;

import javax.naming.InitialContext;
import javax.sql.DataSource;

@Configuration
@PropertySources({
        @PropertySource(value = "classpath:/default_liquibase.properties"),

})
public class LiquibaseConfiguration {

    @Autowired
    private Environment env;

    @Bean
    public DataSource dataSource() {
        InitialContext initialContext;
        DataSource dataSource = null;
        try {
            initialContext = new InitialContext();
            dataSource = (DataSource) initialContext.lookup(this.env.getProperty("jndi.datasource.name"));
        } catch (Exception e) {
            throw new EDCIException("error with liquibase");
        }
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
