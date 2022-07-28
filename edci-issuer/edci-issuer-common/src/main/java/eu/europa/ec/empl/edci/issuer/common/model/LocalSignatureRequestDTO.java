package eu.europa.ec.empl.edci.issuer.common.model;

import java.util.List;

public class LocalSignatureRequestDTO {

    private List<CredentialDTO> credentialDTO;
    private String signOnBehalf;
    private String certPassword;

    public List<CredentialDTO> getCredentialDTO() {
        return credentialDTO;
    }

    public void setCredentialDTO(List<CredentialDTO> credentialDTO) {
        this.credentialDTO = credentialDTO;
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
}
