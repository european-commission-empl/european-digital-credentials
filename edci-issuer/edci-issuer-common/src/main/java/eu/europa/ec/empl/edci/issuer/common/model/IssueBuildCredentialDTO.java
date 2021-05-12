package eu.europa.ec.empl.edci.issuer.common.model;

import javax.validation.constraints.NotNull;
import java.util.Set;

public class IssueBuildCredentialDTO {
    @NotNull
    private Long credential;

    @NotNull
    private Set<RecipientDataDTO> recipients;

    public Long getCredential() {
        return credential;
    }

    public void setCredential(Long credential) {
        this.credential = credential;
    }

    public Set<RecipientDataDTO> getRecipients() {
        return recipients;
    }

    public void setRecipients(Set<RecipientDataDTO> recipients) {
        this.recipients = recipients;
    }
}
