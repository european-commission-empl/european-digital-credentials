package eu.europa.ec.empl.edci.issuer.web.model.signature;

import eu.europa.ec.empl.edci.dss.model.signature.DSSTimestampDTO;

import java.util.Date;

public class SignatureBytesView {
    private String bytes;
    private String uuid;
    private Date date;
    private DSSTimestampDTO dssTimestampDTO;
    private Boolean valid;
    private String warningMsg;
    private String errorMessage;

    public SignatureBytesView() {
    }

    public Boolean getValid() {
        return valid;
    }

    public void setValid(Boolean valid) {
        this.valid = valid;
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

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
