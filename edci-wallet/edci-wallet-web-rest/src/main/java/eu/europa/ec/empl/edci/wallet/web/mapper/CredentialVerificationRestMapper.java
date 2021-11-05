package eu.europa.ec.empl.edci.wallet.web.mapper;

import eu.europa.ec.empl.edci.datamodel.model.verifiable.presentation.VerificationCheckDTO;
import eu.europa.ec.empl.edci.datamodel.view.VerificationCheckView;
import eu.europa.ec.empl.edci.mapper.PresentationCommonsMapper;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.util.List;

@Mapper(componentModel = "spring", uses = {PresentationCommonsMapper.class})
public interface CredentialVerificationRestMapper {


    List<VerificationCheckView> toVOList(List<VerificationCheckDTO> verificationCheckDTOList, @Context String lang);

    @Mappings({
            @Mapping(source = "description", target = "descrAvailableLangs"),
            @Mapping(source = "longDescription", target = "longDescrAvailableLangs"),
            @Mapping(source = "description", target = "description"),
            @Mapping(source = "longDescription", target = "longDescription")
    })
    VerificationCheckView toVO(VerificationCheckDTO verificationCheckDTO, @Context String lang);

}
