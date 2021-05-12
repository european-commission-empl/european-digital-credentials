package eu.europa.ec.empl.edci.constants;

public enum ControlledListConcept {

    VERIFICATION_CHECKS_OWNER(ControlledList.VERIFICATION_CHECKS, "http://data.europa.eu/snb/verification/1faf8d450f"),
    VERIFICATION_CHECKS_REVOCATION(ControlledList.VERIFICATION_CHECKS, "http://data.europa.eu/snb/verification/271aef9eb4"),
    VERIFICATION_CHECKS_FORMAT(ControlledList.VERIFICATION_CHECKS, "http://data.europa.eu/snb/verification/2f5b89b96f"),
    VERIFICATION_CHECKS_EXPIRY(ControlledList.VERIFICATION_CHECKS, "http://data.europa.eu/snb/verification/94d828f160"),
    VERIFICATION_CHECKS_ACCREDITATION(ControlledList.VERIFICATION_CHECKS, "http://data.europa.eu/snb/verification/e2bbc86a28"),
    VERIFICATION_CHECKS_SEAL(ControlledList.VERIFICATION_CHECKS, "http://data.europa.eu/snb/verification/f9c2016fe9"),
    VERIFICATION_CHECKS_CUSTOM(ControlledList.VERIFICATION_CHECKS, "http://data.europa.eu/snb/verification/c_50990fe3"),

    VERIFICATION_TYPE_NOTARIZED_COPY(ControlledList.VERIFICATION_TYPE, "http://52.57.141.197/fs/0.10.0/vp/certifiedCopy"),
    VERIFICATION_TYPE_MANDATED_ISSUE(ControlledList.VERIFICATION_TYPE, "http://52.57.141.197/fs/0.10.0/vp/signOnBehalf"),
    VERIFICATION_TYPE_SHARED(ControlledList.VERIFICATION_TYPE, "http://52.57.141.197/fs/0.10.0/vp/export"),

    VERIFICATION_STATUS_SKIPPED(ControlledList.VERIFICATION_STATUS, "http://data.europa.eu/snb/verification-status/641f0c5e5d"),
    VERIFICATION_STATUS_OK(ControlledList.VERIFICATION_STATUS, "http://data.europa.eu/snb/verification-status/9895008394"),
    VERIFICATION_STATUS_ERROR(ControlledList.VERIFICATION_STATUS, "http://data.europa.eu/snb/verification-status/9d26eb9a37"),

    CREDENTIAL_TYPE_GENERIC(ControlledList.CREDENTIAL_TYPE, "http://data.europa.eu/snb/credential/e34929035b"),

    ENCODING_BASE64(ControlledList.ENCODING, "http://data.europa.eu/snb/encoding/6146cde7dd"),
    FILE_TYPE_JPEG(ControlledList.FILE_TYPE, "http://publications.europa.eu/resource/authority/file-type/JPEG");

    private ControlledList controlledList;
    private String url;

    private ControlledListConcept(ControlledList controlledList, String url) {
        this.controlledList = controlledList;
        this.url = url;
    }

    public ControlledList getControlledList() {
        return controlledList;
    }

    public void setControlledList(ControlledList controlledList) {
        this.controlledList = controlledList;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
