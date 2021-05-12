package eu.europa.ec.empl.edci.issuer.common.model.signature;

import eu.europa.ec.empl.edci.issuer.common.model.CredentialDTO;

import java.util.Date;

public class SignatureNexuDTO {

    private String presentation;
    private String success;
    private String uuid;
    private SignatureNexuResponseDTO response;
    private SignatureNexuFeedbackDTO feedback;
    private CredentialDTO credential;
    private Date date;
    private DSSTimestampDTO dssTimestampDTO;

    public SignatureNexuDTO() {
    }

    public String getSuccess() {
        return success;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public void setSuccess(String success) {
        success = success;
    }

    public SignatureNexuResponseDTO getResponse() {
        return response;
    }

    public void setResponse(SignatureNexuResponseDTO response) {
        this.response = response;
    }

    public SignatureNexuFeedbackDTO getFeedback() {
        return feedback;
    }

    public void setFeedback(SignatureNexuFeedbackDTO feedback) {
        this.feedback = feedback;
    }

    public CredentialDTO getCredential() {
        return credential;
    }

    public void setCredential(CredentialDTO credential) {
        this.credential = credential;
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

    public String getPresentation() {
        return presentation;
    }

    public void setPresentation(String presentation) {
        this.presentation = presentation;
    }
}
