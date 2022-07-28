package eu.europa.ec.empl.edci.issuer.mapper.datamodel;

import eu.europa.ec.empl.edci.datamodel.model.LearningActivityDTO;
import eu.europa.ec.empl.edci.issuer.entity.specs.LearningActivitySpecDAO;
import org.mapstruct.BeforeMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", uses = {VariousObjectsMapper.class, AgentOrganizationMapper.class}, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface LearningActivityMapper {

    LearningActivityDTO toDTO(LearningActivitySpecDAO learningActivitySpecDAO);

    LearningActivitySpecDAO toDAO(LearningActivityDTO learningActivityDTO);

    @BeforeMapping()
    default void checkEmptySpec(LearningActivitySpecDAO source, @MappingTarget LearningActivityDTO target) {
        if (source != null && source.getSpecifiedBy() != null) {
            if (source.getSpecifiedBy().isEmpty()) {
                source.setSpecifiedBy(null);
            }
        }
    }
}
