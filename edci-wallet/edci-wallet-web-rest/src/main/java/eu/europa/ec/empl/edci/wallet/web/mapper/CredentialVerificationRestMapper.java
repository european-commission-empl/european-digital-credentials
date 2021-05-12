package eu.europa.ec.empl.edci.wallet.web.mapper;

import eu.europa.ec.empl.edci.datamodel.model.verifiable.presentation.VerificationCheckDTO;
import eu.europa.ec.empl.edci.datamodel.view.VerificationCheckView;
import eu.europa.ec.empl.edci.mapper.PresentationCommonsMapper;
import org.mapstruct.Context;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring", uses = {PresentationCommonsMapper.class})
public interface CredentialVerificationRestMapper {


    List<VerificationCheckView> toVOList(List<VerificationCheckDTO> verificationCheckDTOList, @Context String lang);

    VerificationCheckView toVO(VerificationCheckDTO verificationCheckDTO, @Context String lang);

}
