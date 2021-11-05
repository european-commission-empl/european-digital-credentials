package eu.europa.ec.empl.edci.wallet.web.model;

import java.util.List;

public class CredentialListView
{
    private List<CredentialView> credentials;

    public CredentialListView(){}

    public List<CredentialView> getCredentials() {
        return credentials;
    }

    public void setCredentials(List<CredentialView> credentials) {
        this.credentials = credentials;
    }
}
