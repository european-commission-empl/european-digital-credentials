package eu.europa.ec.empl.edci.issuer.mapper.datamodel;

import eu.europa.ec.empl.edci.datamodel.model.LearningAchievementDTO;
import eu.europa.ec.empl.edci.datamodel.model.LearningSpecificationDTO;
import eu.europa.ec.empl.edci.datamodel.model.QualificationDTO;
import eu.europa.ec.empl.edci.issuer.entity.dataContainers.LearningSpecificationDCDAO;
import eu.europa.ec.empl.edci.issuer.entity.specs.LearningAchievementSpecDAO;
import org.mapstruct.*;

@Mapper(componentModel = "spring", uses = {LearningActivityMapper.class, VariousObjectsMapper.class, AgentOrganizationMapper.class, AssessmentMapper.class}, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface LearningAchievementMapper {

    @Mappings({
            @Mapping(source = "learningAchievementSpecDAO.specifiedBy", qualifiedByName = "getSpecification", target = "specifiedBy"),
    })
    LearningAchievementDTO toDTO(LearningAchievementSpecDAO learningAchievementSpecDAO);

    LearningAchievementSpecDAO toDAO(LearningAchievementDTO learningAchievementDTO);

    LearningSpecificationDTO toLearningSpecificationDTO(LearningSpecificationDCDAO learningSpecificationDCDAO);

    QualificationDTO toQualificationDTO(LearningSpecificationDCDAO learningSpecificationDCDAO, Boolean workarround);

    @Named("getSpecification")
    default LearningSpecificationDTO getSpecification(LearningSpecificationDCDAO learningSpecificationDCDAO) {

        if (learningSpecificationDCDAO == null) {
            return null;
        }

        if ((learningSpecificationDCDAO.getEqfLevel() != null && !learningSpecificationDCDAO.getEqfLevel().isEmpty()) ||
                (learningSpecificationDCDAO.getHasAccreditation() != null && !learningSpecificationDCDAO.getHasAccreditation().isEmpty()) ||
                (learningSpecificationDCDAO.getQualificationCode() != null && !learningSpecificationDCDAO.getQualificationCode().isEmpty())) {
            return toQualificationDTO(learningSpecificationDCDAO, true);
        } else {
            return toLearningSpecificationDTO(learningSpecificationDCDAO);
        }

    }

    @BeforeMapping()
    default void checkEmptySpec(LearningAchievementSpecDAO source, @MappingTarget LearningAchievementDTO target) {
        if (source == null) {
            return;
        }
        if (source.getSpecifiedBy() != null && source.getSpecifiedBy().isEmpty()) {
            source.setSpecifiedBy(null);
        }
        if (source.getWasAwardedBy() != null && source.getWasAwardedBy().isEmpty()) {
            source.setWasAwardedBy(null);
        }
    }
}
