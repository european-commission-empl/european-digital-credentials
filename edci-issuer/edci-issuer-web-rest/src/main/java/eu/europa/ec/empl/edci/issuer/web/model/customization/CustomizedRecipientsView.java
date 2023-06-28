package eu.europa.ec.empl.edci.issuer.web.model.customization;

import java.util.HashSet;
import java.util.Set;

public class CustomizedRecipientsView {

    private Set<CustomizedRecipientView> recipients = new HashSet<>();

    public Set<CustomizedRecipientView> getRecipients() {
        return recipients;
    }

    public void setRecipients(Set<CustomizedRecipientView> recipients) {
        this.recipients = recipients;
    }
}
