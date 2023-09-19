package eu.europa.ec.empl.edci.dss.model.signature;

import java.util.Date;

public class SignatureBytesDTO {
    private String uuid;
    private String bytes;
    private Date date;
    private String warningMsg;
    private DSSTimestampDTO dssTimestampDTO;
    private Boolean valid;
    private String errorMessage;

    public SignatureBytesDTO() {
    }

    public SignatureBytesDTO(String bytes, Date date, DSSTimestampDTO dssTimestampDTO) {
        this.bytes = bytes;
        this.date = date;
        this.dssTimestampDTO = dssTimestampDTO;
    }

    public SignatureBytesDTO(String uuid, String warningMsg, Boolean valid, String errorMessages) {
        this.uuid = uuid;
        this.warningMsg = warningMsg;
        this.valid = valid;
        this.errorMessage = errorMessages;
    }

    public Boolean getValid() {
        return valid;
    }

    public void setValid(Boolean valid) {
        this.valid = valid;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getBytes() {
        return bytes;
    }

    public void setBytes(String bytes) {
        this.bytes = bytes;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public DSSTimestampDTO getDssTimestampDTO() {
        return dssTimestampDTO;
    }

    public void setDssTimestampDTO(DSSTimestampDTO dssTimestampDTO) {
        this.dssTimestampDTO = dssTimestampDTO;
    }

    public String getWarningMsg() {
        return warningMsg;
    }

    public void setWarningMsg(String warningMsg) {
        this.warningMsg = warningMsg;
    }
}
