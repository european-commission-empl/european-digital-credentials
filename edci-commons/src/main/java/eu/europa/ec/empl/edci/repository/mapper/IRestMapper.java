package eu.europa.ec.empl.edci.repository.mapper;

import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

public interface IRestMapper<D, S, L> {

    D toDAO(S view);

    S toVO(D dao);

    @Mappings({
            @Mapping(source = "pk", target = "oid"),
            @Mapping(source = "auditDAO.createDate", target = "additionalInfo.createdOn"),
            @Mapping(source = "auditDAO.updateDate", target = "additionalInfo.updatedOn"),
            @Mapping(source = "languages", target = "additionalInfo.languages")
    })
    L toVOLite(D dao);

}
