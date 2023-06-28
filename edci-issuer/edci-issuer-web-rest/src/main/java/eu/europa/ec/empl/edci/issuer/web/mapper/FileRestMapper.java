package eu.europa.ec.empl.edci.issuer.web.mapper;

import eu.europa.ec.empl.edci.issuer.common.model.CredentialFileDTO;
import eu.europa.ec.empl.edci.issuer.common.model.RecipientFileDTO;
import eu.europa.ec.empl.edci.issuer.web.model.CredentialFileUploadResponseView;
import eu.europa.ec.empl.edci.issuer.web.model.FileView;
import eu.europa.ec.empl.edci.issuer.web.model.RecipientFileUploadResponseView;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.util.List;

@Mapper(componentModel = "spring", uses = CredentialRestMapper.class)
public interface FileRestMapper {

    @Mappings({
            @Mapping(target = "credentials", ignore = true),
            @Mapping(source = "file", target = "file"),
            @Mapping(source = "valid", target = "valid")
    })
    CredentialFileDTO toDTO(FileView fileView);

    List<CredentialFileDTO> toDTOList(List<FileView> fileViewList);

    @Mappings({
            @Mapping(source = "file", target = "file"),
            @Mapping(source = "valid", target = "valid")
    })
    FileView toVO(CredentialFileDTO fileDTO);

    List<FileView> toVOList(List<CredentialFileDTO> fileDTOList);

    @Mappings({
            @Mapping(source = "valid", target = "valid"),
            @Mapping(source = "credentials", target = "credentials")
    })
    CredentialFileUploadResponseView toFileUploadVO(CredentialFileDTO fileDTO);

    @Mappings({
            @Mapping(source = "recipientDataDTOS", target = "recipientDataViews")
    })
    RecipientFileUploadResponseView toRecipientFileUploadVO(RecipientFileDTO recipientFileDTO);

    /* default FileUploadResponseView toFileUploadVO(List<EuropassCredentialDTO> europassCredentialDTO, CredentialRestMapper credentialRestMapper) {
         FileUploadResponseView fileRespoDTO = new FileUploadResponseView();
         fileRespoDTO.setCredentials(credentialRestMapper.toVOList(
                 credentialRestMapper.europassToDTOList(europassCredentialDTO, EuropassConstants.DEFAULT_LOCALE)));
         fileRespoDTO.setValid(fileRespoDTO.getCredentials().stream().allMatch(CredentialView::getValid));
         return fileRespoDTO;
     }*/
}
