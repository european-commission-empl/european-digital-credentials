package eu.europa.ec.empl.edci.issuer.web.model.signature;

import eu.europa.ec.empl.edci.datamodel.AttachmentView;

import java.util.List;

public class SignatureParametersView {

    private String presentation;
    private String success;
    private List<String> uuids;
    private SignatureParametersResponseView response;
    private SignatureParametersFeedbackView feedback;
    private AttachmentView mandatedIssue;

    public SignatureParametersView() {
    }

    public String getSuccess() {
        return success;
    }

    public List<String> getUuids() {
        return uuids;
    }

    public void setUuids(List<String> uuids) {
        this.uuids = uuids;
    }

    public void setSuccess(String success) {
        success = success;
    }

    public SignatureParametersResponseView getResponse() {
        return response;
    }

    public void setResponse(SignatureParametersResponseView response) {
        this.response = response;
    }

    public SignatureParametersFeedbackView getFeedback() {
        return feedback;
    }

    public void setFeedback(SignatureParametersFeedbackView feedback) {
        this.feedback = feedback;
    }

    public String getPresentation() {
        return presentation;
    }

    public void setPresentation(String presentation) {
        this.presentation = presentation;
    }

    public AttachmentView getMandatedIssue() {
        return mandatedIssue;
    }

    public void setMandatedIssue(AttachmentView mandatedIssue) {
        this.mandatedIssue = mandatedIssue;
    }
}
