package eu.europa.ec.empl.edci.liquibase;

import eu.europa.ec.empl.edci.exception.EDCIException;
import eu.europa.ec.empl.edci.util.ContextAwareRunnable;
import liquibase.change.custom.CustomTaskChange;
import liquibase.change.custom.CustomTaskRollback;
import liquibase.database.Database;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.CustomChangeException;
import liquibase.exception.RollbackImpossibleException;
import liquibase.exception.SetupException;
import liquibase.exception.ValidationErrors;
import liquibase.resource.ResourceAccessor;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.web.context.request.RequestContextHolder;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public abstract class EDCIAbstractCustomTask implements CustomTaskChange, CustomTaskRollback {

    private static final Logger logger = LogManager.getLogger(EDCIAbstractCustomTask.class);

    protected abstract void executeChange(Database database) throws Exception;

    protected abstract void executeRollBack(Database database) throws Exception;

    protected abstract String getValidationSQL();

    protected abstract long getValidationSQLResult();

    protected abstract Logger getLogger();

    @Override
    public void execute(Database database) throws CustomChangeException {
        try {
            this.executeChange(database);
        } catch (Exception e) {
            try {
                this.getLogger().error("error executing changes, tyring rollback", e);
                this.executeRollBack(database);
            } catch (Exception ex) {
                this.getLogger().error("error executing rollback", ex);
            }
        }
    }

    @Override
    public String getConfirmationMessage() {
        return null;
    }

    @Override
    public void setUp() throws SetupException {

    }

    @Override
    public void setFileOpener(ResourceAccessor resourceAccessor) {

    }

    @Override
    public ValidationErrors validate(Database database) {
        ValidationErrors validationErrors = new ValidationErrors();
        if (StringUtils.isEmpty(getValidationSQL())) {
            return null;
        }
        //Get JDBC connection
        JdbcConnection dbConn = (JdbcConnection) database.getConnection();
        try (Statement statement = dbConn.createStatement(); ResultSet resultSet = statement.executeQuery(this.getValidationSQL())) {
            if (resultSet.next()) {
                long count = resultSet.getLong("COUNT");
                if (count != this.getValidationSQLResult()) {
                    validationErrors.addError("PRECONDITION QUERY WAS NOT 0");
                    return validationErrors;
                }
            }
        } catch (Exception e) {
            logger.error(e);
            validationErrors.addError("Something went wrong while checking preconditions");
        }
        return null;
    }

    @Override
    public void rollback(Database database) throws CustomChangeException, RollbackImpossibleException {
    }
}
