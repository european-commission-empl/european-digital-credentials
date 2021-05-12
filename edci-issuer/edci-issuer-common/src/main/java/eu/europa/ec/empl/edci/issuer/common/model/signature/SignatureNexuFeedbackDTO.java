package eu.europa.ec.empl.edci.issuer.common.model.signature;

public class SignatureNexuFeedbackDTO {
    private SignatureNexuInfoDTO info;
    private String nexuVersion;

    public SignatureNexuFeedbackDTO(){}

    public SignatureNexuInfoDTO getInfo() {
        return info;
    }

    public void setInfo(SignatureNexuInfoDTO info) {
        this.info = info;
    }

    public String getNexuVersion() {
        return nexuVersion;
    }

    public void setNexuVersion(String nexuVersion) {
        this.nexuVersion = nexuVersion;
    }
}
