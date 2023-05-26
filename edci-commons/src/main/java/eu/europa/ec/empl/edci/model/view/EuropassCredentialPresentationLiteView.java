package eu.europa.ec.empl.edci.model.view;

import eu.europa.ec.empl.edci.model.view.base.ITabView;
import eu.europa.ec.empl.edci.model.view.tabs.CredentialMetadataTabView;

import java.util.List;

public class EuropassCredentialPresentationLiteView implements ITabView {

    private CredentialMetadataTabView credentialMetadata;

    private List<EuropassCredentialPresentationLiteView> subCredentials;

    public CredentialMetadataTabView getCredentialMetadata() {
        return credentialMetadata;
    }

    public void setCredentialMetadata(CredentialMetadataTabView credentialMetadata) {
        this.credentialMetadata = credentialMetadata;
    }

    public List<EuropassCredentialPresentationLiteView> getSubCredentials() {
        return subCredentials;
    }

    public void setSubCredentials(List<EuropassCredentialPresentationLiteView> subCredentials) {
        this.subCredentials = subCredentials;
    }
}

