package eu.europa.ec.empl.edci.issuer.mapper.datamodel;

import eu.europa.ec.empl.edci.datamodel.jsonld.model.AgentDTO;
import eu.europa.ec.empl.edci.datamodel.jsonld.model.OrganisationDTO;
import eu.europa.ec.empl.edci.issuer.entity.specs.OrganizationSpecDAO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", uses = {VariousObjectsMapper.class, BaseDAOMapper.class, AccreditationMapper.class})
public interface AgentOrganizationMapper {

    @Mappings({
            @Mapping(source = "homePage", target = "homepage"),
            @Mapping(target = "altLabel", ignore = true)
    })
    OrganisationDTO toOrgDTO(OrganizationSpecDAO orgDAO);

    default Collection<AgentDTO> toAgentDTO(Collection<OrganizationSpecDAO> orgDAO) {

        if (orgDAO == null) {
            return null;
        }

        List<AgentDTO> returnCollection = orgDAO.stream().map(orgAux -> toOrgDTO(orgAux)).collect(Collectors.toList());
        return returnCollection;
    }

}
