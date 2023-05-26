package eu.europa.ec.empl.edci.issuer.web.mapper.spec;

import eu.europa.ec.empl.edci.constants.EDCIMessageKeys;
import eu.europa.ec.empl.edci.issuer.entity.dataTypes.NoteDTDAO;
import eu.europa.ec.empl.edci.issuer.web.model.dataTypes.NoteDTView;
import eu.europa.ec.empl.edci.service.EDCIMessageService;
import org.apache.commons.lang.LocaleUtils;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring")
public abstract class MoreInformationNoteRestMapper {

    @Autowired
    private EDCIMessageService edciMessageService;
    
    @AfterMapping
    public void toNoteDTDAOafterMapping(@MappingTarget NoteDTDAO noteDTDAO, NoteDTView noteDTView) {
        if (noteDTDAO.getSubject() != null && noteDTDAO.getSubject().getTargetName() != null) {
            noteDTDAO.getSubject().getTargetName().getContents().stream()
                    .filter(contentDTDAO ->
                            contentDTDAO.getContent().equals(EDCIMessageKeys.FieldLabel.MAIN_ADDITIONAL_NOTE_TOPIC)
                    ).forEach(contentDTDAO -> contentDTDAO.setContent(
                    edciMessageService.getMessage(LocaleUtils.toLocale(contentDTDAO.getLanguage()),
                            EDCIMessageKeys.FieldLabel.MAIN_ADDITIONAL_NOTE_TOPIC)));
        }
    }

    public EDCIMessageService getEdciMessageService() {
        return edciMessageService;
    }

    public void setEdciMessageService(EDCIMessageService edciMessageService) {
        this.edciMessageService = edciMessageService;
    }
}
