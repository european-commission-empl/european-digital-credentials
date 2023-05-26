package eu.europa.ec.empl.edci.issuer.mapper.datamodel;

import eu.europa.ec.empl.edci.datamodel.jsonld.model.LearningAchievementDTO;
import eu.europa.ec.empl.edci.datamodel.jsonld.model.LearningAchievementSpecificationDTO;
import eu.europa.ec.empl.edci.datamodel.jsonld.model.QualificationDTO;
import eu.europa.ec.empl.edci.issuer.entity.dataContainers.LearningAchSpecificationDCDAO;
import eu.europa.ec.empl.edci.issuer.entity.specs.LearningAchievementSpecDAO;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", uses = {EntitlementMapper.class, LearningActivityMapper.class, BaseDAOMapper.class, VariousObjectsMapper.class, AgentOrganizationMapper.class, AssessmentMapper.class, AccreditationMapper.class}, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface LearningAchievementMapper {
    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "type", ignore = true),
            @Mapping(source = "learningAchievementSpecDAO.specifiedBy", qualifiedByName = "getSpecification", target = "specifiedBy"),
    })
    LearningAchievementDTO toDTO(LearningAchievementSpecDAO learningAchievementSpecDAO);

    List<LearningAchievementDTO> toDTOList(List<LearningAchievementSpecDAO> learningAchievementSpecDAO);

    @Mappings({
            @Mapping(target = "type", ignore = true),
            @Mapping(target = "creditPoint", source = "creditPoints"),
            @Mapping(source = "homePage", target = "homepage")
    })
    LearningAchievementSpecificationDTO toLearningSpecificationDTO(LearningAchSpecificationDCDAO learningAchSpecificationDCDAO);

    @Mappings({
            @Mapping(target = "type", ignore = true),
            @Mapping(target = "homepage", source = "learningAchSpecificationDCDAO.homePage"),
            @Mapping(target = "eqfLevel", source = "learningAchSpecificationDCDAO.EQFLevel"),
            @Mapping(target = "nqfLevel", source = "learningAchSpecificationDCDAO.NQFLevel"),
            @Mapping(target = "creditPoint", source = "learningAchSpecificationDCDAO.creditPoints")
    })
    QualificationDTO toQualificationDTO(LearningAchSpecificationDCDAO learningAchSpecificationDCDAO, Boolean workarround);

    @Named("getSpecification")
    default LearningAchievementSpecificationDTO getSpecification(LearningAchSpecificationDCDAO learningAchSpecificationDCDAO) {

        if (learningAchSpecificationDCDAO == null) {
            return null;
        }

        if ((learningAchSpecificationDCDAO.getNQFLevel() != null && !learningAchSpecificationDCDAO.getNQFLevel().isEmpty()) ||
                (learningAchSpecificationDCDAO.getEQFLevel() != null) ||
                (learningAchSpecificationDCDAO.getAccreditation() != null && !learningAchSpecificationDCDAO.getAccreditation().isEmpty()) ||
                (learningAchSpecificationDCDAO.getQualificationCode() != null && !learningAchSpecificationDCDAO.getQualificationCode().isEmpty())) {
            return toQualificationDTO(learningAchSpecificationDCDAO, true);
        } else {
            return toLearningSpecificationDTO(learningAchSpecificationDCDAO);
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
    }

}
