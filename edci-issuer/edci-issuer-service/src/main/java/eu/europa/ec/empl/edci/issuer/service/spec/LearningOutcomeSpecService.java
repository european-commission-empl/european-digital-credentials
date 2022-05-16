package eu.europa.ec.empl.edci.issuer.service.spec;

import eu.europa.ec.empl.edci.exception.clientErrors.EDCINotFoundException;
import eu.europa.ec.empl.edci.issuer.entity.specs.LearningOutcomeSpecDAO;
import eu.europa.ec.empl.edci.issuer.repository.LearningOutcomeSpecRepository;
import eu.europa.ec.empl.edci.repository.mapper.IRestMapper;
import eu.europa.ec.empl.edci.repository.service.CrudService;
import eu.europa.ec.empl.edci.security.service.EDCIUserService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class LearningOutcomeSpecService implements CrudService<LearningOutcomeSpecDAO> {
    private static final Logger logger = LogManager.getLogger(LearningOutcomeSpecService.class);

    @Autowired
    private EDCIUserService edciUserService;

    @Override
    public EDCIUserService getEDCIUserService() {
        return this.edciUserService;
    }

    @Autowired
    private LearningOutcomeSpecRepository learningOutcomeSpecRepository;

    public LearningOutcomeSpecRepository getRepository() {
        return learningOutcomeSpecRepository;
    }

    public <T> LearningOutcomeSpecDAO clone(Long oid, IRestMapper<LearningOutcomeSpecDAO, T, ?> learningOutcomeSpecRestMapper) {

        LearningOutcomeSpecDAO learningOutcomeSpecDAO = find(oid);
        if (learningOutcomeSpecDAO == null) {
            throw new EDCINotFoundException().addDescription("LearningOutcome with oid [" + oid + "] not found");
        }

        //Removing the oids of the data type dependencies
        LearningOutcomeSpecDAO learningOutcomeSpecCloneDAO = learningOutcomeSpecRestMapper.toDAO(learningOutcomeSpecRestMapper.toVO(learningOutcomeSpecDAO));
        learningOutcomeSpecCloneDAO.setPk(null);
        learningOutcomeSpecCloneDAO.setAuditDAO(null);

        learningOutcomeSpecCloneDAO.setDefaultTitle(generateTitleDuplicated(learningOutcomeSpecCloneDAO.getDefaultTitle()));

        return save(learningOutcomeSpecCloneDAO);

    }
}