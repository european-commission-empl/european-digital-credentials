package eu.europa.ec.empl.edci.issuer.web.mapper;

import eu.europa.ec.empl.edci.issuer.common.model.RegistryDTO;
import eu.europa.ec.empl.edci.issuer.web.model.RegistryView;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.util.List;

/**
 * The interface Mdm rest mapper.
 */
@Mapper(componentModel = "spring")
public interface RegistryRestMapper {

    @Mappings(
            @Mapping(target = "regId", ignore = true)
    )
    RegistryDTO registryVOtoDTO(RegistryView view);

    /**
     * Territory v oto dto list list.
     *
     * @param view the view
     * @return the list
     */
    List<RegistryDTO> registryVOtoDTOList(List<RegistryView> view);

    /**
     * Territory dt oto vo territory view.
     *
     * @param dto the dto
     * @return the territory view
     */
    RegistryView registryDTOtoVO(RegistryDTO dto);

    /**
     * Territory dt oto vo list list.
     *
     * @param dto the dto
     * @return the list
     */
    List<RegistryView> registryDTOtoVOList(List<RegistryDTO> dto);
}
