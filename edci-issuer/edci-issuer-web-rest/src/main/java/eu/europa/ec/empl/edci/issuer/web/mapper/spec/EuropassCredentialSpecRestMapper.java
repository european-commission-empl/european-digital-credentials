package eu.europa.ec.empl.edci.issuer.web.mapper.spec;

import eu.europa.ec.empl.edci.issuer.entity.specs.EuropassCredentialSpecDAO;
import eu.europa.ec.empl.edci.issuer.web.model.specs.EuropassCredentialSpecView;
import eu.europa.ec.empl.edci.issuer.web.model.specs.lite.EuropassCredentialSpecLiteView;
import eu.europa.ec.empl.edci.mapper.annotations.RuntimeMapping;
import eu.europa.ec.empl.edci.mapper.annotations.RuntimeMappings;
import eu.europa.ec.empl.edci.mapper.commons.DurationLongMapper;
import eu.europa.ec.empl.edci.mapper.commons.StringBytesMapping;
import eu.europa.ec.empl.edci.mapper.commons.StringDateMapping;
import eu.europa.ec.empl.edci.mapper.commons.StringUriMapping;
import eu.europa.ec.empl.edci.repository.mapper.IRestMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

/**
 * The interface Mdm rest mapper.
 */
@Mapper(componentModel = "spring",
        uses = {StringBytesMapping.class,
                DurationLongMapper.class,
                LearningActivitySpecRestMapper.class,
                EntitlementSpecRestMapper.class,
                LearningAchievementSpecRestMapper.class,
                StringUriMapping.class,
                StringDateMapping.class}
)
public interface EuropassCredentialSpecRestMapper extends IRestMapper<EuropassCredentialSpecDAO, EuropassCredentialSpecView, EuropassCredentialSpecLiteView> {

    @Mappings({
            @Mapping(source = "oid", target = "pk"),
            @Mapping(source = "additionalInfo.languages", target = "languages")
    })
    EuropassCredentialSpecDAO toDAO(EuropassCredentialSpecView view);

    @Mappings({
            @Mapping(source = "pk", target = "oid"),
            @Mapping(source = "auditDAO.createDate", target = "additionalInfo.createdOn"),
            @Mapping(source = "auditDAO.updateDate", target = "additionalInfo.updatedOn"),
            @Mapping(source = "languages", target = "additionalInfo.languages")
    })
    @RuntimeMappings({
            @RuntimeMapping(source = "pk", target = "oid"),
            @RuntimeMapping(source = "auditDAO.createDate", target = "additionalInfo.createdOn"),
            @RuntimeMapping(source = "auditDAO.updateDate", target = "additionalInfo.updatedOn"),
            @RuntimeMapping(source = "languages", target = "additionalInfo.languages")
    })
    EuropassCredentialSpecView toVO(EuropassCredentialSpecDAO dao);

}
