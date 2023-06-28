package eu.europa.ec.empl.edci.issuer.mapper.datamodel;

import eu.europa.ec.empl.edci.datamodel.jsonld.model.LearningEntitlementDTO;
import eu.europa.ec.empl.edci.datamodel.jsonld.model.LearningEntitlementSpecificationDTO;
import eu.europa.ec.empl.edci.issuer.entity.dataContainers.EntitlemSpecificationDCDAO;
import eu.europa.ec.empl.edci.issuer.entity.specs.EntitlementSpecDAO;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", uses = {VariousObjectsMapper.class, BaseDAOMapper.class, AgentOrganizationMapper.class, LearningAchievementMapper.class}, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface EntitlementMapper {

    @Mappings({
            @Mapping(target = "id", ignore = true)
    })
    LearningEntitlementDTO toDTO(EntitlementSpecDAO entitlementSpecDAO);

    List<LearningEntitlementDTO> toDTOList(List<EntitlementSpecDAO> entitlementSpecDAO);

    @Mappings({
            @Mapping(target = "entitlementStatus", source = "status"),
            @Mapping(source = "homePage", target = "homepage")
    })
    LearningEntitlementSpecificationDTO toSpecificationDTO(EntitlemSpecificationDCDAO entitlementSpecDAO);

    List<LearningEntitlementSpecificationDTO> toSpecificationDTOList(List<EntitlemSpecificationDCDAO> entitlementSpecDAO);

    @BeforeMapping()
    default void checkEmptySpec(EntitlementSpecDAO source, @MappingTarget LearningEntitlementDTO target) {
        if (source != null && source.getSpecifiedBy() != null) {
            if (source.getSpecifiedBy().isEmpty()) {
                source.setSpecifiedBy(null);
            }
        }
    }
}
