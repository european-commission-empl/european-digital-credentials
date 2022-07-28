package eu.europa.ec.empl.edci.dss.model.signature;

import eu.europa.esig.dss.enumerations.TimestampType;

public class DSSTimestampDTO {

    private String base64Timestamp;
    private String canonicalizationMethod;
    private TimestampType type;

    public DSSTimestampDTO() {
    }

    public DSSTimestampDTO(String base64Timestamp, String canonicalizationMethod, TimestampType type) {
        this.base64Timestamp = base64Timestamp;
        this.canonicalizationMethod = canonicalizationMethod;
        this.type = type;
    }

    public String getBase64Timestamp() {
        return base64Timestamp;
    }

    public void setBase64Timestamp(String base64Timestamp) {
        this.base64Timestamp = base64Timestamp;
    }

    public String getCanonicalizationMethod() {
        return canonicalizationMethod;
    }

    public void setCanonicalizationMethod(String canonicalizationMethod) {
        this.canonicalizationMethod = canonicalizationMethod;
    }

    public TimestampType getType() {
        return type;
    }

    public void setType(TimestampType type) {
        this.type = type;
    }

}