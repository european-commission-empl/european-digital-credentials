package eu.europa.ec.empl.edci.issuer.service.spec;

import eu.europa.ec.empl.edci.exception.clientErrors.EDCINotFoundException;
import eu.europa.ec.empl.edci.issuer.entity.specs.LearningActivitySpecDAO;
import eu.europa.ec.empl.edci.issuer.repository.LearningActivitySpecRepository;
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
public class LearningActivitySpecService implements CrudService<LearningActivitySpecDAO> {
    private static final Logger logger = LogManager.getLogger(LearningActivitySpecService.class);

    @Autowired
    ResourcesUtil resourcesUtil;

    @Autowired
    private EDCIUserService edciUserService;

    @Override
    public EDCIUserService getEDCIUserService() {
        return this.edciUserService;
    }

    @Autowired
    private LearningActivitySpecRepository activitySpecRepository;

    public LearningActivitySpecRepository getRepository() {
        return activitySpecRepository;
    }

    public <T> LearningActivitySpecDAO clone(Long oid, IRestMapper<LearningActivitySpecDAO, T, ?> learningActivitySpecRestMapper) {

        LearningActivitySpecDAO learningActivitySpecDAO = find(oid);
        if (learningActivitySpecDAO == null) {
            throw new EDCINotFoundException().addDescription("LearningActivity with oid [" + oid + "] not found");
        }

        //Removing the oids of the data type dependencies
        LearningActivitySpecDAO learningActivitySpecCloneDAO = learningActivitySpecRestMapper.toDAO(learningActivitySpecRestMapper.toVO(learningActivitySpecDAO));
        learningActivitySpecCloneDAO.setPk(null);
        learningActivitySpecCloneDAO.setAuditDAO(null);
        learningActivitySpecCloneDAO.setLabel(this.generateDuplicatedLabel(learningActivitySpecCloneDAO.getLabel()));
        //Relations
        if (learningActivitySpecDAO.getAwardedBy() != null) {
            learningActivitySpecCloneDAO.getAwardedBy().setAwardingBody(new HashSet<>(
                    learningActivitySpecDAO.getAwardedBy().getAwardingBody()));
        }

        learningActivitySpecCloneDAO.setDirectedBy(new HashSet<>(learningActivitySpecDAO.getDirectedBy()));
        learningActivitySpecCloneDAO.setInfluenced(new HashSet<>(learningActivitySpecDAO.getInfluenced()));
      /* THIS RELATIONS ARE NOT BEING USED IN OCB?

        learningActivitySpecCloneDAO.setHasPart(new HashSet<>(learningActivitySpecDAO.getHasPart()));

        if (learningActivitySpecDAO.getSpecifiedBy() != null) {
            learningActivitySpecCloneDAO.getSpecifiedBy().setHasPart(new HashSet<>(
                    learningActivitySpecDAO.getSpecifiedBy().getHasPart()));
            learningActivitySpecCloneDAO.getSpecifiedBy().setTeaches(new HashSet<>(
                    learningActivitySpecDAO.getSpecifiedBy().getTeaches()));
            learningActivitySpecCloneDAO.getSpecifiedBy().setSpecialisationOf(new HashSet<>(
                    learningActivitySpecDAO.getSpecifiedBy().getSpecialisationOf()));
        }*/

        return save(learningActivitySpecCloneDAO);

    }

    public LearningActivitySpecDAO save(LearningActivitySpecDAO objectDAO, Runnable... postCreateActions) {

        List<Runnable> runnableList = new ArrayList<>(Arrays.asList(postCreateActions));
        runnableList.add(() -> resourcesUtil.checkLoopTree(objectDAO, (a) -> a.getHasPart(), "Sub activities"));

        return CrudService.super.save(objectDAO, runnableList.toArray(new Runnable[]{}));

    }
}