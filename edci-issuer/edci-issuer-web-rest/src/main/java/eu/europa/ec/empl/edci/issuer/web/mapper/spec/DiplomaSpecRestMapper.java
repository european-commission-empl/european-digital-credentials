package eu.europa.ec.empl.edci.issuer.web.mapper.spec;

import eu.europa.ec.empl.edci.issuer.entity.specs.DiplomaSpecDAO;
import eu.europa.ec.empl.edci.issuer.web.model.specs.DiplomaSpecView;
import eu.europa.ec.empl.edci.issuer.web.model.specs.lite.DiplomaSpecLiteView;
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
@Mapper(componentModel = "spring", uses = {StringBytesMapping.class, DurationLongMapper.class, StringUriMapping.class, StringDateMapping.class})
public interface DiplomaSpecRestMapper extends IRestMapper<DiplomaSpecDAO, DiplomaSpecView, DiplomaSpecLiteView> {

    @Mappings({
            @Mapping(source = "oid", target = "pk"),
            @Mapping(source = "additionalInfo.languages", target = "languages")
    })
    DiplomaSpecDAO toDAO(DiplomaSpecView view);

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
    DiplomaSpecView toVO(DiplomaSpecDAO dao);

    @Override
    default String getDisplayName(DiplomaSpecDAO dao, String locale) {
        return dao.getLabel();
    }
}
