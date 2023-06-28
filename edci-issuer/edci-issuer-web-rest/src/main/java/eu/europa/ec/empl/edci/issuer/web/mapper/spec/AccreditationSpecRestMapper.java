package eu.europa.ec.empl.edci.issuer.web.mapper.spec;

import eu.europa.ec.empl.edci.constants.EDCIConstants;
import eu.europa.ec.empl.edci.issuer.entity.dataTypes.ContentDTDAO;
import eu.europa.ec.empl.edci.issuer.entity.specs.AccreditationSpecDAO;
import eu.europa.ec.empl.edci.issuer.web.model.specs.AccreditationSpecView;
import eu.europa.ec.empl.edci.issuer.web.model.specs.AssessmentsListIssueView;
import eu.europa.ec.empl.edci.issuer.web.model.specs.lite.AccreditationSpecLiteView;
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

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * The interface Mdm rest mapper.
 */
@Mapper(componentModel = "spring", uses = {StringBytesMapping.class, DurationLongMapper.class, StringUriMapping.class, StringDateMapping.class})
public interface AccreditationSpecRestMapper extends IRestMapper<AccreditationSpecDAO, AccreditationSpecView, AccreditationSpecLiteView> {

    @Mappings({
            @Mapping(source = "oid", target = "pk"),
            @Mapping(source = "additionalInfo.languages", target = "languages")
    })
    AccreditationSpecDAO toDAO(AccreditationSpecView view);

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
    AccreditationSpecView toVO(AccreditationSpecDAO dao);

    default AssessmentsListIssueView toVOListIssueView(Set<AccreditationSpecDAO> AccreditationSpecDAOS) {
        AssessmentsListIssueView assmView = new AssessmentsListIssueView();
        assmView.getAssessments().putAll(AccreditationSpecDAOS.stream().collect(Collectors.toMap(k -> k.getPk(), v -> v.getLabel())));
        return assmView;
    }

    @Override
    default String getDisplayName(AccreditationSpecDAO dao, String locale) {
        if(dao.getTitle() == null ) {
            return "";
        }

        Optional<ContentDTDAO> titleContentDAO = dao.getTitle().getLocalizedContent(locale);

        if(titleContentDAO == null ) {
            return "";
        }

        String title = titleContentDAO.isPresent() ? titleContentDAO.get().getContent() : dao.getTitle().getStringContent(dao.getDefaultLanguage());
        return dao.getLabel() != null && !dao.getLabel().isEmpty() ?
                title.concat(EDCIConstants.StringPool.STRING_SPACE)
                        .concat(EDCIConstants.StringPool.STRING_HYPHEN)
                        .concat(EDCIConstants.StringPool.STRING_SPACE)
                        .concat(dao.getLabel()) : title;
    }
}
