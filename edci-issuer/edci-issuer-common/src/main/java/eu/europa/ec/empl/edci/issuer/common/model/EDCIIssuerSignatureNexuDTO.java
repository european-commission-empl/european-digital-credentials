package eu.europa.ec.empl.edci.issuer.common.model;

import eu.europa.ec.empl.edci.dss.model.signature.SignatureNexuDTO;

public class EDCIIssuerSignatureNexuDTO extends SignatureNexuDTO {

    private CredentialDTO credential;


    public EDCIIssuerSignatureNexuDTO() {
    }

    public CredentialDTO getCredential() {
        return credential;
    }

    public void setCredential(CredentialDTO credential) {
        this.credential = credential;
    }
}
