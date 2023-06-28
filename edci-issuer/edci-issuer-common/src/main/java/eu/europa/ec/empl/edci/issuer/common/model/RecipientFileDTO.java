package eu.europa.ec.empl.edci.issuer.common.model;

import eu.europa.ec.empl.edci.issuer.common.model.base.FileDTO;

import java.util.List;

public class RecipientFileDTO extends FileDTO {

    private List<RecipientDataDTO> recipientDataDTOS;

    public List<RecipientDataDTO> getRecipientDataDTOS() {
        return recipientDataDTOS;
    }

    public void setRecipientDataDTOS(List<RecipientDataDTO> recipientDataDTOS) {
        this.recipientDataDTOS = recipientDataDTOS;
    }
}
