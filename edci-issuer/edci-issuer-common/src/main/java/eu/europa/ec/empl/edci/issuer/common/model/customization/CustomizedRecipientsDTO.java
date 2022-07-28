package eu.europa.ec.empl.edci.issuer.common.model.customization;

import java.util.HashSet;
import java.util.Set;

public class CustomizedRecipientsDTO {

    private Set<CustomizedRecipientDTO> recipients = new HashSet<>();

    public Set<CustomizedRecipientDTO> getRecipients() {
        return recipients;
    }

    public void setRecipients(Set<CustomizedRecipientDTO> recipients) {
        this.recipients = recipients;
    }
}
