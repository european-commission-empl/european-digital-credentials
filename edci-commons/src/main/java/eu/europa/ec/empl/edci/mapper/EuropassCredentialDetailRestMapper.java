package eu.europa.ec.empl.edci.mapper;

import eu.europa.ec.empl.edci.constants.EDCIConstants;
import eu.europa.ec.empl.edci.model.view.EuropassDiplomaDTO;
import eu.europa.ec.empl.edci.model.view.EuropassDiplomaView;
import org.mapstruct.*;

import java.text.SimpleDateFormat;
import java.util.Date;

@Mapper(componentModel = "spring", uses = {BaseMapper.class}, unmappedTargetPolicy = ReportingPolicy.IGNORE)

public interface EuropassCredentialDetailRestMapper {

    /* EuropassCredentialDetailView toVO(EuropeanDigitalCredentialDTO europassCredentialDTO);
 
     List<EuropassCredentialDetailView> toVOList(List<EuropeanDigitalCredentialDTO> credentialDetailDTOList);
     */
    @Mappings({
            @Mapping(source = "expirationDate", target = "expirationDate", qualifiedByName = "toDateGMT")
    })
    EuropassDiplomaView toVO(EuropassDiplomaDTO europassDiplomaDTO);

    EuropassDiplomaDTO toDTO(EuropassDiplomaView europassDiplomaView);

    @Named("toDateGMT")
    default String toDateGMT(Date date) {
        if(date == null) {
            return null;
        }

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat();
        simpleDateFormat.applyPattern(EDCIConstants.DATE_FRONT_GMT);

        return simpleDateFormat.format(date);
    }

}
