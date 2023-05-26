package eu.europa.ec.empl.edci.issuer.service.spec;

import eu.europa.ec.empl.edci.exception.clientErrors.EDCINotFoundException;
import eu.europa.ec.empl.edci.issuer.entity.specs.LearningAssessmentSpecDAO;
import eu.europa.ec.empl.edci.issuer.repository.AssessmentSpecRepository;
import eu.europa.ec.empl.edci.repository.mapper.IRestMapper;
import eu.europa.ec.empl.edci.repository.service.CrudService;
import eu.europa.ec.empl.edci.security.service.EDCIUserService;
import eu.europa.ec.empl.edci.util.ResourcesUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

@Service
@Transactional
public class AssessmentSpecService implements CrudService<LearningAssessmentSpecDAO> {
    private static final Logger logger = LogManager.getLogger(AssessmentSpecService.class);

    @Autowired
    ResourcesUtil resourcesUtil;

    @Autowired
    private EDCIUserService edciUserService;

    @Override
    public EDCIUserService getEDCIUserService() {
        return this.edciUserService;
    }

    @Autowired
    private AssessmentSpecRepository assessmentSpecRepository;

    public AssessmentSpecRepository getRepository() {
        return assessmentSpecRepository;
    }

    public <T> LearningAssessmentSpecDAO clone(Long oid, IRestMapper<LearningAssessmentSpecDAO, T, ?> assessmentSpecRestMapper) {

        LearningAssessmentSpecDAO learningAssessmentSpecDAO = find(oid);
        if (learningAssessmentSpecDAO == null) {
            throw new EDCINotFoundException().addDescription("Assessment with oid [" + oid + "] not found");
        }

        //Removing the oids of the data type dependencies
        LearningAssessmentSpecDAO assessmentSpecCloneDAO = assessmentSpecRestMapper.toDAO(assessmentSpecRestMapper.toVO(learningAssessmentSpecDAO));

        assessmentSpecCloneDAO.setPk(null);
        assessmentSpecCloneDAO.setAuditDAO(null);
        assessmentSpecCloneDAO.setLabel(this.generateDuplicatedLabel(assessmentSpecCloneDAO.getLabel()));

        //Relations
        if (learningAssessmentSpecDAO.getAwardedBy() != null) {
            assessmentSpecCloneDAO.getAwardedBy().setAwardingBody(new HashSet<>(
                    learningAssessmentSpecDAO.getAwardedBy().getAwardingBody()));
        }

        assessmentSpecCloneDAO.setHasPart(new HashSet<>(learningAssessmentSpecDAO.getHasPart()));
        assessmentSpecCloneDAO.setAssessedBy(new HashSet<>(learningAssessmentSpecDAO.getAssessedBy()));

        return save(assessmentSpecCloneDAO);

    }

    public LearningAssessmentSpecDAO save(LearningAssessmentSpecDAO objectDAO, Runnable... postCreateActions) {

        List<Runnable> runnableList = new ArrayList<>(Arrays.asList(postCreateActions));
        runnableList.add(() -> resourcesUtil.checkLoopTree(objectDAO, (a) -> a.getHasPart(), "Sub assessments"));

        return CrudService.super.save(objectDAO, runnableList.toArray(new Runnable[]{}));

    }

}
