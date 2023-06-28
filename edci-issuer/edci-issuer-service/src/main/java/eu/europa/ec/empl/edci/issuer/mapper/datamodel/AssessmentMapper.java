package eu.europa.ec.empl.edci.issuer.mapper.datamodel;

import eu.europa.ec.empl.edci.datamodel.jsonld.model.LearningAssessmentDTO;
import eu.europa.ec.empl.edci.issuer.common.model.AssessmentsListIssueDTO;
import eu.europa.ec.empl.edci.issuer.entity.specs.LearningAssessmentSpecDAO;
import org.mapstruct.*;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", uses = {BaseDAOMapper.class, VariousObjectsMapper.class, AgentOrganizationMapper.class, LearningAchievementMapper.class}, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AssessmentMapper {

    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "specifiedBy.gradingScheme", source = "specifiedBy.gradingScheme"),
            @Mapping(target = "specifiedBy.homepage", source = "specifiedBy.homePage"),
            @Mapping(target = "type", ignore = true),
            @Mapping(target = "specifiedBy.type", ignore = true)
    })
    LearningAssessmentDTO toDTO(LearningAssessmentSpecDAO learningAssessmentSpecDAO);

    List<LearningAssessmentDTO> toDTOList(List<LearningAssessmentSpecDAO> learningAssessmentSpecDAO);

    default AssessmentsListIssueDTO toListIssueDTO(Set<LearningAssessmentSpecDAO> learningAssessmentSpecDAOS) {
        AssessmentsListIssueDTO assmView = new AssessmentsListIssueDTO();
        assmView.getAssessments().putAll(learningAssessmentSpecDAOS.stream().collect(Collectors.toMap(k -> k.getPk(), v -> v.getLabel())));
        return assmView;
    }

    @BeforeMapping()
    default void checkEmptySpec(LearningAssessmentSpecDAO source, @MappingTarget LearningAssessmentDTO target) {
        if (source != null && source.getSpecifiedBy() != null) {
            if (source.getSpecifiedBy().isEmpty()) {
                source.setSpecifiedBy(null);
            }
        }
    }
}
