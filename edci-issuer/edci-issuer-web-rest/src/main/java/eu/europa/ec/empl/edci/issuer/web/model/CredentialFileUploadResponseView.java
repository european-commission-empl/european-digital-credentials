package eu.europa.ec.empl.edci.issuer.web.model;

import java.util.ArrayList;
import java.util.List;

public class CredentialFileUploadResponseView {

    private boolean valid;
    private List<CredentialView> credentials = new ArrayList<>();

    public CredentialFileUploadResponseView() {

    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public List<CredentialView> getCredentials() {
        return credentials;
    }

    public void setCredentials(List<CredentialView> credentials) {
        this.credentials = credentials;
    }

}
