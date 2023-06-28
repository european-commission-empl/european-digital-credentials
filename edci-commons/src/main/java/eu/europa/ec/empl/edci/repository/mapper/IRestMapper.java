package eu.europa.ec.empl.edci.repository.mapper;

import eu.europa.ec.empl.edci.repository.entity.IMultilangDAO;
import eu.europa.ec.empl.edci.repository.rest.model.INameDisplayableView;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;

public interface IRestMapper<D, S, L> {

    D toDAO(S view);

    S toVO(D dao);

    @Mappings({
            @Mapping(source = "pk", target = "oid"),
            @Mapping(source = "auditDAO.createDate", target = "additionalInfo.createdOn"),
            @Mapping(source = "auditDAO.updateDate", target = "additionalInfo.updatedOn"),
            @Mapping(source = "languages", target = "additionalInfo.languages"),
            //@Mapping(target = "displayName", qualifiedByName = "getDisplayName")
    })
    L toVOLite(D dao);

    //As a workaround, due to usual "QualifiedByName" not working correctly
    @AfterMapping
    default void toVoLiteAfterMapping(D dao, @MappingTarget L lite) {
        if (IMultilangDAO.class.isAssignableFrom(dao.getClass())) {
            String primaryLang = ((IMultilangDAO) dao).getDefaultLanguage();
            if (INameDisplayableView.class.isAssignableFrom(lite.getClass())) {
                ((INameDisplayableView) lite).setDisplayName(this.getDisplayName(dao, primaryLang));
            }
        }
    }

    String getDisplayName(D dao, String locale);


}
