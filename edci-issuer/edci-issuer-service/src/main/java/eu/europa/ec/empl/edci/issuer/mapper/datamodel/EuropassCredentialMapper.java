package eu.europa.ec.empl.edci.issuer.mapper.datamodel;

import eu.europa.ec.empl.edci.datamodel.model.EuropassCredentialDTO;
import eu.europa.ec.empl.edci.issuer.entity.specs.EuropassCredentialSpecDAO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {VariousObjectsMapper.class, AgentOrganizationMapper.class, LearningAchievementMapper.class, LearningAchievementMapper.class})
public interface EuropassCredentialMapper {

    EuropassCredentialDTO toDTO(EuropassCredentialSpecDAO europassCredentialSpecDAO/*, @Context String locale*/);

    EuropassCredentialSpecDAO toDAO(EuropassCredentialDTO europassCredentialDTO);

}
