package eu.europa.ec.empl.edci.issuer.service.liquibase;

import eu.europa.ec.empl.edci.context.SpringApplicationContext;
import eu.europa.ec.empl.edci.issuer.common.constants.IssuerConstants;
import eu.europa.ec.empl.edci.issuer.repository.*;
import eu.europa.ec.empl.edci.liquibase.EDCIAbstractCustomTask;
import eu.europa.ec.empl.edci.repository.entity.IOCBIdentifiedDAO;
import liquibase.database.Database;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class CustomChangeOCBIDs extends EDCIAbstractCustomTask {

    private static final Logger logger = LogManager.getLogger(CustomChangeOCBIDs.class);
    private final static String VALIDATION_CHECK_QUERY = "SELECT COUNT(*) AS COUNT FROM SPEC_EUROPASS_CREDENTIAL WHERE OCBID != null";


    @Override
    protected void executeChange(Database database) throws Exception {

        System.out.println("####STARTING OCBID CUSTOM CHANGE####");
        BeanFactory beanFactory = SpringApplicationContext.getBeanFactory();
        AssessmentSpecRepository assessmentSpecRepository = beanFactory.getBean(AssessmentSpecRepository.class);
        EntitlementSpecRepository entitlementSpecRepository = beanFactory.getBean(EntitlementSpecRepository.class);
        EuropassCredentialSpecRepository europassCredentialSpecRepository = beanFactory.getBean(EuropassCredentialSpecRepository.class);
        LearningAchievementSpecRepository learningAchievementSpecRepository = beanFactory.getBean(LearningAchievementSpecRepository.class);
        LearningActivitySpecRepository learningActivitySpecRepository = beanFactory.getBean(LearningActivitySpecRepository.class);
        System.out.println("ACQUIRED NECESSARY BEANS");

        this.updateEntities(assessmentSpecRepository.findAll().stream().map(assessmentSpecDAO -> (IOCBIdentifiedDAO) assessmentSpecDAO).collect(Collectors.toList())
                , assessmentSpecRepository);
        this.updateEntities(entitlementSpecRepository.findAll().stream().map(entitlementSpecDAO -> (IOCBIdentifiedDAO) entitlementSpecDAO).collect(Collectors.toList())
                , entitlementSpecRepository);
        this.updateEntities(europassCredentialSpecRepository.findAll().stream().map(europassCredentialSpecDAO -> (IOCBIdentifiedDAO) europassCredentialSpecDAO).collect(Collectors.toList())
                , europassCredentialSpecRepository);
        this.updateEntities(learningAchievementSpecRepository.findAll().stream().map(learningAchievementSpecDAO -> (IOCBIdentifiedDAO) learningAchievementSpecDAO).collect(Collectors.toList())
                , learningAchievementSpecRepository);
        this.updateEntities(learningActivitySpecRepository.findAll().stream().map(learningActivitySpecDAO -> (IOCBIdentifiedDAO) learningActivitySpecDAO).collect(Collectors.toList())
                , learningActivitySpecRepository);

        System.out.println("####ENDED OCBID CUSTOM CHANGE####");

    }

    protected void updateEntities(List<IOCBIdentifiedDAO> iocbIdentifiedDAOList, JpaRepository repository) {
        if (iocbIdentifiedDAOList != null && !iocbIdentifiedDAOList.isEmpty()) {
            System.out.println(String.format("Updating list of %d entities of type %s", iocbIdentifiedDAOList.size(), iocbIdentifiedDAOList.get(0).getClass().getName()));
            for (IOCBIdentifiedDAO iocbIdentifiedDAO : iocbIdentifiedDAOList) {
                if (iocbIdentifiedDAO.getOCBID() == null || iocbIdentifiedDAO.getOCBID().equals(IssuerConstants.DEFAULT_OCBID)) {
                    iocbIdentifiedDAO.setOCBID(UUID.randomUUID().toString());
                    repository.save(iocbIdentifiedDAO);
                }
            }
        }
    }

    @Override
    protected void executeRollBack(Database database) throws Exception {
        System.out.println("rollback logic in XML");
    }

    @Override
    protected String getValidationSQL() {
        return VALIDATION_CHECK_QUERY;
    }

    @Override
    protected long getValidationSQLResult() {
        return 0;
    }

    @Override
    protected Logger getLogger() {
        return logger;
    }
}
