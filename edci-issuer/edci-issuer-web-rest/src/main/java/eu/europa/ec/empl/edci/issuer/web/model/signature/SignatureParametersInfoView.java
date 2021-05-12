package eu.europa.ec.empl.edci.issuer.web.model.signature;

public class SignatureParametersInfoView {
    private String jreVendor;
    private String osName;
    private String osArch;
    private String osVersion;
    private String arch;
    private String os;

    public SignatureParametersInfoView(){}

    public String getJreVendor() {
        return jreVendor;
    }

    public void setJreVendor(String jreVendor) {
        this.jreVendor = jreVendor;
    }

    public String getOsName() {
        return osName;
    }

    public void setOsName(String osName) {
        this.osName = osName;
    }

    public String getOsArch() {
        return osArch;
    }

    public void setOsArch(String osArch) {
        this.osArch = osArch;
    }

    public String getOsVersion() {
        return osVersion;
    }

    public void setOsVersion(String osVersion) {
        this.osVersion = osVersion;
    }

    public String getArch() {
        return arch;
    }

    public void setArch(String arch) {
        this.arch = arch;
    }

    public String getOs() {
        return os;
    }

    public void setOs(String os) {
        this.os = os;
    }
}
