package eu.europa.ec.empl.edci.issuer.web.model.signature;

public class SignatureNexuFeedbackView {
    private SignatureNexuInfoView info;
    private String nexuVersion;

    public SignatureNexuFeedbackView(){}

    public SignatureNexuInfoView getInfo() {
        return info;
    }

    public void setInfo(SignatureNexuInfoView info) {
        this.info = info;
    }

    public String getNexuVersion() {
        return nexuVersion;
    }

    public void setNexuVersion(String nexuVersion) {
        this.nexuVersion = nexuVersion;
    }
}
