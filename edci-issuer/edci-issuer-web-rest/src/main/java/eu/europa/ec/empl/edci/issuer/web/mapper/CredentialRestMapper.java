package eu.europa.ec.empl.edci.issuer.web.mapper;

import eu.europa.ec.empl.edci.dss.model.signature.*;
import eu.europa.ec.empl.edci.issuer.common.model.*;
import eu.europa.ec.empl.edci.issuer.common.model.open.PublicSealAndSendDTO;
import eu.europa.ec.empl.edci.issuer.mapper.datamodel.*;
import eu.europa.ec.empl.edci.issuer.web.model.CredentialHashResponseView;
import eu.europa.ec.empl.edci.issuer.web.model.CredentialHashView;
import eu.europa.ec.empl.edci.issuer.web.model.CredentialView;
import eu.europa.ec.empl.edci.issuer.web.model.data.IssueBuildCredentialView;
import eu.europa.ec.empl.edci.issuer.web.model.data.RecipientDataView;
import eu.europa.ec.empl.edci.issuer.web.model.signature.*;
import eu.europa.ec.empl.edci.issuer.web.model.specs.PublicSealAndSendView;
import eu.europa.ec.empl.edci.mapper.commons.StringDateMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.util.List;

@Mapper(componentModel = "spring", uses = {VariousObjectsMapper.class, StringDateMapping.class, AgentOrganizationMapper.class, LearningAchievementMapper.class, EntitlementMapper.class, LearningActivityMapper.class})
public interface CredentialRestMapper {

    CredentialDTO toDTO(CredentialView credentialView);

    List<CredentialDTO> toDTOList(List<CredentialView> credentialViews);

    CredentialView toVO(CredentialDTO credentialDTO);

    List<CredentialView> toVOList(List<CredentialDTO> credentialDTOList);

    CredentialHashDTO toHashDTO(CredentialHashView credentialHashView);

    CredentialHashView toHashVO(CredentialHashDTO credentialHashDTO);

    CredentialHashResponseView toHashResponseVO(CredentialHashDTO credentialHashDTO);

    SignatureParametersDTO toDTO(SignatureParametersView signatureParametersView);

    SignatureParametersView toVO(SignatureParametersDTO signatureParametersDTO);

    SignatureParametersResponseDTO toDTO(SignatureParametersResponseView signatureParametersResponseView);

    SignatureParametersResponseView toVO(SignatureParametersResponseDTO signatureParametersResponseDTO);

    SignatureParametersFeedbackDTO toDTO(SignatureParametersFeedbackView signatureParametersFeedbackView);

    SignatureParametersFeedbackView toVO(SignatureParametersFeedbackDTO signatureParametersFeedbackDTO);

    SignatureParametersInfoDTO toDTO(SignatureParametersInfoView signatureParametersInfoView);

    SignatureParametersInfoView toVO(SignatureParametersInfoDTO signatureParametersInfoDTO);

    SignatureParametersTokenIdView toDTO(SignatureParametersTokenIdDTO signatureParametersTokenIdDTO);

    SignatureParametersTokenIdDTO toVO(SignatureParametersTokenIdView signatureParametersTokenIdView);

    SignatureBytesDTO toDTO(SignatureBytesView signatureBytesView);

    SignatureBytesView toVO(SignatureBytesDTO signatureBytesDTO);

    List<SignatureBytesDTO> toDTOByteList(List<SignatureBytesView> signatureBytesViewList);

    List<SignatureBytesView> toVOByteList(List<SignatureBytesDTO> signatureBytesDTOList);

    EDCIIssuerSignatureNexuDTO toDTO(SignatureNexuView signatureNexuView);

    SignatureNexuView toVO(EDCIIssuerSignatureNexuDTO signatureNexuDTO);

    List<EDCIIssuerSignatureNexuDTO> toDTOSignatureList(List<SignatureNexuView> signatureNexuViews);

    List<SignatureNexuView> toVOSignatureList(List<EDCIIssuerSignatureNexuDTO> signatureNexuDTOS);

    SignatureNexuResponseDTO toDTO(SignatureNexuResponseView signatureNexuResponseView);

    SignatureNexuResponseView toVO(SignatureNexuResponseDTO signatureNexuResponseDTO);

    SignatureNexuFeedbackDTO toDTO(SignatureNexuFeedbackView signatureNexuFeedbackView);

    SignatureNexuFeedbackView toVO(SignatureNexuFeedbackDTO signatureNexuFeedbackDTO);

    PublicSealAndSendDTO toDTO(PublicSealAndSendView publicSealAndSendView);

    PublicSealAndSendView toVO(PublicSealAndSendDTO publicSealAndSendDTO);

    @Mappings({
            @Mapping(source = "credentialViews", target = "credentialDTO")
    })
    LocalSignatureRequestDTO toDTO(LocalSignatureRequestView localSignatureRequestView);

    @Mappings({
            @Mapping(source = "credentialDTO", target = "credentialViews")
    })
    LocalSignatureRequestView toVO(LocalSignatureRequestDTO localSignatureRequestDTO);

    SignatureNexuInfoDTO toDTO(SignatureNexuInfoView signatureNexuInfoView);

    SignatureNexuInfoView toVO(SignatureNexuInfoDTO signatureNexuInfoDTO);

    /*@Mappings({
            @Mapping(source = "type", target = "type.uri")
    })
    EuropassCredentialDTO toEuropass(CredentialDTO credentialDTO);

    @Mappings({
            @Mapping(source = "europassCredentialDTO.type.uri", target = "type")
    })
    CredentialDTO europassToDTO(EuropassCredentialDTO europassCredentialDTO, @Context String locale);

    List<CredentialDTO> europassToDTOList(List<EuropassCredentialDTO> europassCredentialDTOS, @Context String locale);

    List<EuropassCredentialDTO> toEuropassList(List<CredentialDTO> credentialDTOList);*/

    IssueBuildCredentialDTO toDTO(IssueBuildCredentialView issueBuildCredentialView);

    IssueBuildCredentialView toVO(IssueBuildCredentialDTO issueBuildCredentialDTO);

    RecipientDataDTO toDTO(RecipientDataView recipientDataView);


}