package eu.europa.ec.empl.edci.issuer.mapper.datamodel;

import eu.europa.ec.empl.edci.datamodel.jsonld.model.AccreditationDTO;
import eu.europa.ec.empl.edci.issuer.entity.specs.AccreditationSpecDAO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", uses = {BaseDAOMapper.class, VariousObjectsMapper.class, AgentOrganizationMapper.class}, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AccreditationMapper {
    @Mappings({
            @Mapping(target = "id", ignore = true)
    })
    AccreditationDTO toAccreditationDTO(AccreditationSpecDAO dao);
}
