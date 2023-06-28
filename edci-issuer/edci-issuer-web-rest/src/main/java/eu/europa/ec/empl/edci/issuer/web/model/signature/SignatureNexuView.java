package eu.europa.ec.empl.edci.issuer.web.model.signature;

import eu.europa.ec.empl.edci.dss.model.signature.DSSTimestampDTO;
import eu.europa.ec.empl.edci.issuer.web.model.CredentialView;

import java.util.Date;

public class SignatureNexuView {

    private String presentation;
    private String success;
    private String uuid;
    private SignatureNexuResponseView response;
    private SignatureNexuFeedbackView feedback;
    private CredentialView credential;
    private Date date;
    private DSSTimestampDTO dssTimestampDTO;

    public SignatureNexuView() {
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
        this.success = success;
    }

    public SignatureNexuResponseView getResponse() {
        return response;
    }

    public void setResponse(SignatureNexuResponseView response) {
        this.response = response;
    }

    public SignatureNexuFeedbackView getFeedback() {
        return feedback;
    }

    public void setFeedback(SignatureNexuFeedbackView feedback) {
        this.feedback = feedback;
    }

    public CredentialView getCredential() {
        return credential;
    }

    public void setCredential(CredentialView credential) {
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
