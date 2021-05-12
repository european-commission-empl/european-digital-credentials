package eu.europa.ec.empl.edci.issuer.common.model.signature;

public class SignatureParametersFeedbackDTO {
    private SignatureParametersInfoDTO info;
    private String nexuVersion;

    public SignatureParametersFeedbackDTO(){}

    public SignatureParametersInfoDTO getInfo() {
        return info;
    }

    public void setInfo(SignatureParametersInfoDTO info) {
        this.info = info;
    }

    public String getNexuVersion() {
        return nexuVersion;
    }

    public void setNexuVersion(String nexuVersion) {
        this.nexuVersion = nexuVersion;
    }
}
