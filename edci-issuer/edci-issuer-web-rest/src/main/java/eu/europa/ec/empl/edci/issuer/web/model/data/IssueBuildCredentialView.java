package eu.europa.ec.empl.edci.issuer.web.model.data;

import javax.validation.constraints.NotNull;
import java.util.Set;

public class IssueBuildCredentialView {

    @NotNull
    private Long credential;

    @NotNull
    private Set<RecipientDataView> recipients;

    public Long getCredential() {
        return credential;
    }

    public void setCredential(Long credential) {
        this.credential = credential;
    }

    public Set<RecipientDataView> getRecipients() {
        return recipients;
    }

    public void setRecipients(Set<RecipientDataView> recipients) {
        this.recipients = recipients;
    }
}