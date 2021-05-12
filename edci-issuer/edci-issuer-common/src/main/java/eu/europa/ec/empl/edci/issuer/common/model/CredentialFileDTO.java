package eu.europa.ec.empl.edci.issuer.common.model;

import eu.europa.ec.empl.edci.issuer.common.model.base.FileDTO;

import java.util.List;

public class CredentialFileDTO extends FileDTO {

    private List<CredentialDTO> credentials;

    public CredentialFileDTO() {

    }

    public List<CredentialDTO> getCredentials() {
        return credentials;
    }

    public void setCredentials(List<CredentialDTO> credentials) {
        this.credentials = credentials;
    }
}
