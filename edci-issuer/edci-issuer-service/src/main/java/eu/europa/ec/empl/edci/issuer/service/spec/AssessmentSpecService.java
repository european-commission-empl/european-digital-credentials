package eu.europa.ec.empl.edci.issuer.service.spec;

import eu.europa.ec.empl.edci.exception.clientErrors.EDCINotFoundException;
import eu.europa.ec.empl.edci.issuer.entity.specs.AssessmentSpecDAO;
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
public class AssessmentSpecService implements CrudService<AssessmentSpecDAO> {
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

    public <T> AssessmentSpecDAO clone(Long oid, IRestMapper<AssessmentSpecDAO, T, ?> assessmentSpecRestMapper) {

        AssessmentSpecDAO assessmentSpecDAO = find(oid);
        if (assessmentSpecDAO == null) {
            throw new EDCINotFoundException().addDescription("Assessment with oid [" + oid + "] not found");
        }

        //Removing the oids of the data type dependencies
        AssessmentSpecDAO assessmentSpecCloneDAO = assessmentSpecRestMapper.toDAO(assessmentSpecRestMapper.toVO(assessmentSpecDAO));
        assessmentSpecCloneDAO.setPk(null);
        assessmentSpecCloneDAO.setAuditDAO(null);

        assessmentSpecCloneDAO.setDefaultTitle(generateTitleDuplicated(assessmentSpecCloneDAO.getDefaultTitle()));

        //Relations
        assessmentSpecCloneDAO.setHasPart(new HashSet<>(assessmentSpecDAO.getHasPart()));
        assessmentSpecCloneDAO.setAssessedBy(new HashSet<>(assessmentSpecDAO.getAssessedBy()));

        return save(assessmentSpecCloneDAO);

    }

    public AssessmentSpecDAO save(AssessmentSpecDAO objectDAO, Runnable... postCreateActions) {

        List<Runnable> runnableList = new ArrayList<>(Arrays.asList(postCreateActions));
        runnableList.add(() -> resourcesUtil.checkLoopTree(objectDAO, (a) -> a.getHasPart(), "Sub assessments"));

        return CrudService.super.save(objectDAO, runnableList.toArray(new Runnable[]{}));

    }

}
