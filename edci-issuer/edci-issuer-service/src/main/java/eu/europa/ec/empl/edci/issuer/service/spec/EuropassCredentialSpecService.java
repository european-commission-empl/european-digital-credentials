package eu.europa.ec.empl.edci.issuer.service.spec;

import eu.europa.ec.empl.edci.exception.clientErrors.EDCINotFoundException;
import eu.europa.ec.empl.edci.issuer.common.constants.XLS;
import eu.europa.ec.empl.edci.issuer.entity.specs.AssessmentSpecDAO;
import eu.europa.ec.empl.edci.issuer.entity.specs.EuropassCredentialSpecDAO;
import eu.europa.ec.empl.edci.issuer.entity.specs.LearningAchievementSpecDAO;
import eu.europa.ec.empl.edci.issuer.mapper.datamodel.AssessmentMapper;
import eu.europa.ec.empl.edci.issuer.repository.EuropassCredentialSpecRepository;
import eu.europa.ec.empl.edci.issuer.service.EDCIWorkbookService;
import eu.europa.ec.empl.edci.issuer.service.IssuerFileService;
import eu.europa.ec.empl.edci.issuer.util.FileUtil;
import eu.europa.ec.empl.edci.repository.mapper.IRestMapper;
import eu.europa.ec.empl.edci.repository.service.CrudService;
import eu.europa.ec.empl.edci.security.service.EDCIUserService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@Service
@Transactional
public class EuropassCredentialSpecService implements CrudService<EuropassCredentialSpecDAO> {
    private static final Logger logger = Logger.getLogger(EuropassCredentialSpecService.class);

    @Autowired
    private EDCIUserService edciUserService;

    @Override
    public EDCIUserService getEDCIUserService() {
        return this.edciUserService;
    }

    @Autowired
    private EuropassCredentialSpecRepository credentialSpecRepository;

    @Autowired
    private LearningAchievementSpecService learningAchievementSpecService;

    @Autowired
    private EDCIWorkbookService edciWorkbookService;

    @Autowired
    private AssessmentMapper assessmentMapper;

    @Autowired
    private FileUtil fileUtil;

    @Autowired
    private IssuerFileService fileService;

    @Override
    public EuropassCredentialSpecRepository getRepository() {
        return credentialSpecRepository;
    }

    public <T> EuropassCredentialSpecDAO clone(Long oid, IRestMapper<EuropassCredentialSpecDAO, T, ?> credentialSpecRestMapper) {

        EuropassCredentialSpecDAO credentialDAO = find(oid);
        if (credentialDAO == null) {
            throw new EDCINotFoundException().addDescription("Credential with oid [" + oid + "] not found");
        }

        //Removing the oids of the data type dependencies
        EuropassCredentialSpecDAO credentialCloneDAO = credentialSpecRestMapper.toDAO(credentialSpecRestMapper.toVO(credentialDAO));
        credentialCloneDAO.setPk(null);
        credentialCloneDAO.setAuditDAO(null);

        credentialCloneDAO.setDefaultTitle(generateTitleDuplicated(credentialCloneDAO.getDefaultTitle()));

        credentialCloneDAO.setEntitledTo(new HashSet<>(credentialDAO.getEntitledTo()));
        credentialCloneDAO.setPerformed(new HashSet<>(credentialDAO.getPerformed()));
        credentialCloneDAO.setAchieved(new HashSet<>(credentialDAO.getAchieved()));
        credentialCloneDAO.setIssuer(credentialDAO.getIssuer());
        credentialCloneDAO.setDisplay(credentialDAO.getDisplay());

        return save(credentialCloneDAO);

    }

    public Set<AssessmentSpecDAO> getCredentialAssessments(Long oid) {
        EuropassCredentialSpecDAO credentialDAO = this.find(oid);
        if (credentialDAO == null) {
            throw new EDCINotFoundException().addDescription("Credential with oid [" + oid + "] not found");
        }

        Set<AssessmentSpecDAO> assessments = this.getLearningAchievementSpecService().getAllAssessmentsFrom(true,
                credentialDAO.getAchieved().toArray(new LearningAchievementSpecDAO[0]));

        return assessments;
    }

    /**
     * Generate a Recipient XLS from a credential spec
     *
     * @param oid the credential spec id
     * @return the bytes of the generated xls
     */
    public ResponseEntity<byte[]> generateRecipientXLS(Long oid) {
        Set<AssessmentSpecDAO> assessments = this.getCredentialAssessments(oid);
        String lang = this.find(oid).getDefaultLanguage();
        String fileName = this.getFileUtil().getTemplateFileName(XLS.RECIPIENT_TEMPLATE_NAME);
        byte[] bytes = this.getEdciWorkbookService().generateRecipientWorkbookBytes(this.getAssessmentMapper().toListIssueDTO(assessments), lang);
        return new ResponseEntity<byte[]>(bytes, this.getFileService().prepareHttpHeadersForFileDownload(fileName), HttpStatus.OK);

    }

    public EuropassCredentialSpecRepository getCredentialSpecRepository() {
        return credentialSpecRepository;
    }

    public void setCredentialSpecRepository(EuropassCredentialSpecRepository credentialSpecRepository) {
        this.credentialSpecRepository = credentialSpecRepository;
    }

    public LearningAchievementSpecService getLearningAchievementSpecService() {
        return learningAchievementSpecService;
    }

    public void setLearningAchievementSpecService(LearningAchievementSpecService learningAchievementSpecService) {
        this.learningAchievementSpecService = learningAchievementSpecService;
    }

    public EDCIWorkbookService getEdciWorkbookService() {
        return edciWorkbookService;
    }

    public void setEdciWorkbookService(EDCIWorkbookService edciWorkbookService) {
        this.edciWorkbookService = edciWorkbookService;
    }

    public AssessmentMapper getAssessmentMapper() {
        return assessmentMapper;
    }

    public void setAssessmentMapper(AssessmentMapper assessmentMapper) {
        this.assessmentMapper = assessmentMapper;
    }

    public FileUtil getFileUtil() {
        return fileUtil;
    }

    public void setFileUtil(FileUtil fileUtil) {
        this.fileUtil = fileUtil;
    }

    public IssuerFileService getFileService() {
        return fileService;
    }

    public void setFileService(IssuerFileService fileService) {
        this.fileService = fileService;
    }
}