package eu.europa.ec.empl.edci.issuer.mapper.datamodel;

import eu.europa.ec.empl.edci.datamodel.model.EntitlementDTO;
import eu.europa.ec.empl.edci.issuer.entity.specs.EntitlementSpecDAO;
import org.mapstruct.BeforeMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", uses = {VariousObjectsMapper.class, AgentOrganizationMapper.class}, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface EntitlementMapper {

    EntitlementSpecDAO toDAO(EntitlementDTO entitlementDTO);

    EntitlementDTO toDTO(EntitlementSpecDAO entitlementSpecDAO);

    @BeforeMapping()
    default void checkEmptySpec(EntitlementSpecDAO source, @MappingTarget EntitlementDTO target) {
        if (source != null && source.getSpecifiedBy() != null) {
            if (source.getSpecifiedBy().isEmpty()) {
                source.setSpecifiedBy(null);
            }
        }
    }
}
