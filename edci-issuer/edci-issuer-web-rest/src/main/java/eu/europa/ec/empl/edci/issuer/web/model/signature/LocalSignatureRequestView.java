package eu.europa.ec.empl.edci.issuer.web.model.signature;

import eu.europa.ec.empl.edci.datamodel.AttachmentView;
import eu.europa.ec.empl.edci.issuer.web.model.CredentialView;

import java.util.List;

public class LocalSignatureRequestView {

    private List<CredentialView> credentialViews;
    private String signOnBehalf;
    private String certPassword;
    private AttachmentView mandatedIssue;

    public List<CredentialView> getCredentialViews() {
        return credentialViews;
    }

    public void setCredentialViews(List<CredentialView> credentialView) {
        this.credentialViews = credentialView;
    }

    public String getSignOnBehalf() {
        return signOnBehalf;
    }

    public void setSignOnBehalf(String signOnBehalf) {
        this.signOnBehalf = signOnBehalf;
    }

    public String getCertPassword() {
        return certPassword;
    }

    public void setCertPassword(String certPassword) {
        this.certPassword = certPassword;
    }

    public AttachmentView getMandatedIssue() {
        return mandatedIssue;
    }

    public void setMandatedIssue(AttachmentView mandatedIssue) {
        this.mandatedIssue = mandatedIssue;
    }
}
