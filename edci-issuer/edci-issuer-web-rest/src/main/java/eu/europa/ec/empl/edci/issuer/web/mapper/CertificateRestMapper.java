package eu.europa.ec.empl.edci.issuer.web.mapper;

import eu.europa.ec.empl.edci.issuer.common.model.CertificateDTO;
import eu.europa.ec.empl.edci.issuer.web.model.CertificateView;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.util.List;

/**
 * The interface Mdm rest mapper.
 */
@Mapper(componentModel = "spring")
public interface CertificateRestMapper {

    @Mappings(
            @Mapping(target = "id", ignore = true)
    )
    CertificateDTO certificateVOtoDTO(CertificateView view);

    /**
     * Territory v oto dto list list.
     *
     * @param view the view
     * @return the list
     */
    List<CertificateDTO> certificateVOtoDTOList(List<CertificateView> view);

    /**
     * Territory dt oto vo territory view.
     *
     * @param dto the dto
     * @return the territory view
     */
    /*@Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "date", ignore = true)
    })*/
    CertificateView certificateDTOtoVO(CertificateDTO dto);

    List<CertificateView> certificateDTOtoVOList(List<CertificateDTO> dtoList);

}
