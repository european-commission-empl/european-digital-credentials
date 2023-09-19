package eu.europa.ec.empl.edci.issuer.web.mapper;

import eu.europa.ec.empl.edci.datamodel.jsonld.model.AccreditationDTO;
import eu.europa.ec.empl.edci.issuer.web.mapper.spec.GenericEntitiyRestMapper;
import eu.europa.ec.empl.edci.issuer.web.model.data.AccreditationLiteView;
import eu.europa.ec.empl.edci.mapper.commons.StringUriMapping;
import org.mapstruct.Mapper;

/**
 * The interface Mdm rest mapper.
 */
@Mapper(componentModel = "spring", uses = {StringUriMapping.class, GenericEntitiyRestMapper.class})
public interface AccreditationRestMapper {

    AccreditationLiteView toAccreditationLiteView(AccreditationDTO dto);

}
