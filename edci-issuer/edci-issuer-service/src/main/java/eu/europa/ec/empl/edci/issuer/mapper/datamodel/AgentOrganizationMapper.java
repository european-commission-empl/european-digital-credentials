package eu.europa.ec.empl.edci.issuer.mapper.datamodel;

import eu.europa.ec.empl.edci.datamodel.model.OrganizationDTO;
import eu.europa.ec.empl.edci.datamodel.model.base.AgentDTO;
import eu.europa.ec.empl.edci.issuer.entity.specs.OrganizationSpecDAO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.util.*;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", uses = {VariousObjectsMapper.class})
public interface AgentOrganizationMapper {

    @Mappings({
            @Mapping(source = "homePage", target = "homepage")
    })
    OrganizationDTO toOrgDTO(OrganizationSpecDAO orgDAO);

    @Mappings({
            @Mapping(source = "homepage", target = "homePage")
    })
    OrganizationSpecDAO toOrgDAO(OrganizationDTO orgDAO);

    default Set<OrganizationSpecDAO> toListOrgDAO(OrganizationSpecDAO orgDAO) {
        if (orgDAO == null) {
            return new HashSet<>();
        }
        return new HashSet<OrganizationSpecDAO>(Arrays.asList(orgDAO));
    }

    default OrganizationSpecDAO toSingleOrgDAO(Set<OrganizationSpecDAO> organizationSpecDAOS) {
        if (organizationSpecDAOS == null || organizationSpecDAOS.isEmpty()) {
            return null;
        }
        return organizationSpecDAOS.stream().findFirst().get();
    }

    default Collection<AgentDTO> toAgentDTO(Collection<OrganizationSpecDAO> orgDAO) {

        if (orgDAO == null) {
            return null;
        }

        List<AgentDTO> returnCollection = orgDAO.stream().map(orgAux -> toOrgDTO(orgAux)).collect(Collectors.toList());
        return returnCollection;
    }

    default Collection<OrganizationSpecDAO> fromAgentDTO(Collection<AgentDTO> orgDAO) {

        if (orgDAO == null || !(orgDAO instanceof OrganizationDTO)) {
            return null;
        }

        List<OrganizationSpecDAO> returnCollection = orgDAO.stream().filter(orgAux -> orgAux instanceof OrganizationDTO)
                .map(orgAux -> toOrgDAO((OrganizationDTO) orgAux)).collect(Collectors.toList());
        return returnCollection;
    }

}
