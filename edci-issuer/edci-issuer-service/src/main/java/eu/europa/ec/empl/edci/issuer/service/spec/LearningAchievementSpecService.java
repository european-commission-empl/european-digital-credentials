package eu.europa.ec.empl.edci.issuer.service.spec;

import eu.europa.ec.empl.edci.exception.clientErrors.EDCINotFoundException;
import eu.europa.ec.empl.edci.issuer.entity.specs.LearningAchievementSpecDAO;
import eu.europa.ec.empl.edci.issuer.entity.specs.LearningAssessmentSpecDAO;
import eu.europa.ec.empl.edci.issuer.repository.LearningAchievementSpecRepository;
import eu.europa.ec.empl.edci.repository.mapper.IRestMapper;
import eu.europa.ec.empl.edci.repository.service.CrudService;
import eu.europa.ec.empl.edci.security.service.EDCIUserService;
import eu.europa.ec.empl.edci.util.ResourcesUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Function;

@Service
@Transactional
public class LearningAchievementSpecService implements CrudService<LearningAchievementSpecDAO> {
    private static final Logger logger = LogManager.getLogger(LearningAchievementSpecService.class);

    @Autowired
    ResourcesUtil resourcesUtil;

    @Autowired
    private EDCIUserService edciUserService;

    @Override
    public EDCIUserService getEDCIUserService() {
        return this.edciUserService;
    }

    @Autowired
    private LearningAchievementSpecRepository achievementSpecRepository;

    public LearningAchievementSpecRepository getRepository() {
        return achievementSpecRepository;
    }

    public LearningAchievementSpecDAO saveCheckingLoops(LearningAchievementSpecDAO learningAchievementDAO,
                                                        Function<LearningAchievementSpecDAO, Collection<LearningAchievementSpecDAO>> getRelElems, String fieldName) {

        if (getRelElems.apply(learningAchievementDAO) != null && !getRelElems.apply(learningAchievementDAO).isEmpty()) {
            resourcesUtil.checkLoopTree(learningAchievementDAO, getRelElems, fieldName);
        }

        return getRepository().save(learningAchievementDAO);
    }

    public <T> LearningAchievementSpecDAO clone(Long oid, IRestMapper<LearningAchievementSpecDAO, T, ?> learningAchievementSpecRestMapper) {

        LearningAchievementSpecDAO learningAchievementSpecDAO = find(oid);
        if (learningAchievementSpecDAO == null) {
            throw new EDCINotFoundException().addDescription("LearningAchievement with oid [" + oid + "] not found");
        }

        //Removing the oids of the data type dependencies
        LearningAchievementSpecDAO learningAchievementSpecCloneDAO = learningAchievementSpecRestMapper.toDAO(learningAchievementSpecRestMapper.toVO(learningAchievementSpecDAO));
        learningAchievementSpecCloneDAO.setPk(null);
        learningAchievementSpecCloneDAO.setAuditDAO(null);
        learningAchievementSpecCloneDAO.setLabel(this.generateDuplicatedLabel(learningAchievementSpecCloneDAO.getLabel()));
        //Relations
        if (learningAchievementSpecDAO.getAwardedBy() != null) {
            learningAchievementSpecCloneDAO.getAwardedBy().setAwardingBody(new HashSet<>(
                    learningAchievementSpecDAO.getAwardedBy().getAwardingBody()));
        }

        learningAchievementSpecCloneDAO.setProvenBy(new HashSet<>(learningAchievementSpecDAO.getProvenBy()));
        learningAchievementSpecCloneDAO.setInfluencedBy(new HashSet<>(learningAchievementSpecDAO.getInfluencedBy()));
        learningAchievementSpecCloneDAO.setEntitlesTo(new HashSet<>(learningAchievementSpecDAO.getEntitlesTo()));
        learningAchievementSpecCloneDAO.setHasPart(new HashSet<>(learningAchievementSpecDAO.getHasPart()));

        if (learningAchievementSpecDAO.getSpecifiedBy() != null) {
            learningAchievementSpecCloneDAO.getSpecifiedBy().setAccreditation(new HashSet<>(
                    learningAchievementSpecDAO.getSpecifiedBy().getAccreditation()));
            learningAchievementSpecCloneDAO.getSpecifiedBy().setLearningOutcome(new HashSet<>(
                    learningAchievementSpecDAO.getSpecifiedBy().getLearningOutcome()));
        }

        return save(learningAchievementSpecCloneDAO);

    }

    public LearningAchievementSpecDAO save(LearningAchievementSpecDAO objectDAO, Runnable... postCreateActions) {

        List<Runnable> runnableList = new ArrayList<>(Arrays.asList(postCreateActions));
        runnableList.add(() -> resourcesUtil.checkLoopTree(objectDAO, (a) -> a.getHasPart(), "Sub Achievements"));

        return CrudService.super.save(objectDAO, runnableList.toArray(new Runnable[]{}));

    }

    public Set<LearningAssessmentSpecDAO> getAllAssessmentsFrom(boolean searchInSubAchivements, LearningAchievementSpecDAO... ach) {

        Set<LearningAssessmentSpecDAO> assessmentListAux = new HashSet<>();

        for (LearningAchievementSpecDAO achAux : ach) {
            if (searchInSubAchivements) {
                getAllAchAndSubAch(assessmentListAux, achAux);
            } else {
                achAux.getProvenBy().stream().forEach(a -> assessmentListAux.addAll(getAllAssAndSubAss(assessmentListAux, a)));
            }
        }

        return assessmentListAux;
    }

    protected Set<LearningAchievementSpecDAO> getAllAchAndSubAch(Set<LearningAssessmentSpecDAO> assessmentListAux, LearningAchievementSpecDAO ach) {

        Set<LearningAchievementSpecDAO> achListAux = new HashSet<>();

        achListAux.add(ach);
        ach.getProvenBy().stream().forEach(a -> assessmentListAux.addAll(getAllAssAndSubAss(assessmentListAux, a)));

        for (LearningAchievementSpecDAO achAux : ach.getHasPart()) {
            achListAux.addAll(getAllAchAndSubAch(assessmentListAux, achAux));
        }

        return achListAux;

    }

    protected Set<LearningAssessmentSpecDAO> getAllAssAndSubAss(Set<LearningAssessmentSpecDAO> assessmentListAux, LearningAssessmentSpecDAO ass) {

        Set<LearningAssessmentSpecDAO> assListAux = new HashSet<>();

        assListAux.add(ass);
        assessmentListAux.addAll(ass.getHasPart());

        for (LearningAssessmentSpecDAO assAux : ass.getHasPart()) {
            assListAux.addAll(getAllAssAndSubAss(assessmentListAux, assAux));
        }

        return assListAux;

    }

}
