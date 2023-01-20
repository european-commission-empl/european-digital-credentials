package eu.europa.ec.empl.edci.issuer.web.model.signature;

public class SignatureParametersFeedbackView {
    private SignatureParametersInfoView info;
    private String nexuVersion;

    public SignatureParametersFeedbackView(){}

    public SignatureParametersInfoView getInfo() {
        return info;
    }

    public void setInfo(SignatureParametersInfoView info) {
        this.info = info;
    }

    public String getNexuVersion() {
        return nexuVersion;
    }

    public void setNexuVersion(String nexuVersion) {
        this.nexuVersion = nexuVersion;
    }
}
