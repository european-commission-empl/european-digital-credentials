package eu.europa.ec.empl.edci.issuer.mapper.datamodel;

import eu.europa.ec.empl.edci.datamodel.jsonld.model.EuropeanDigitalCredentialDTO;
import eu.europa.ec.empl.edci.issuer.entity.specs.EuropassCredentialSpecDAO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring", uses = {VariousObjectsMapper.class, BaseDAOMapper.class, AgentOrganizationMapper.class, LearningAchievementMapper.class, LearningActivityMapper.class, EntitlementMapper.class, AssessmentMapper.class})
public interface EuropassCredentialMapper {

    @Mappings({
            @Mapping(target = "id", ignore = true)
    })
    EuropeanDigitalCredentialDTO toDTO(EuropassCredentialSpecDAO europassCredentialSpecDAO/*, @Context String locale*/);

}
