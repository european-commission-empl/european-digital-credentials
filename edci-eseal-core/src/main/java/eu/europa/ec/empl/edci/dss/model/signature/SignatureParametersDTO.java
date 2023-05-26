package eu.europa.ec.empl.edci.dss.model.signature;

import eu.europa.ec.empl.edci.dss.constants.ESealConstants;

import java.util.List;

public class SignatureParametersDTO {

    private String presentation = ESealConstants.Certificate.CREDENTIAL_TYPE_EUROPASS_PRESENTATION;
    private String Success;
    private List<String> uuids;
    private SignatureParametersResponseDTO response;
    private SignatureParametersFeedbackDTO feedback;
    private String mandatedText;

    public SignatureParametersDTO() {
    }

    public String getSuccess() {
        return Success;
    }

    public void setSuccess(String success) {
        Success = success;
    }

    public List<String> getUuids() {
        return uuids;
    }

    public void setUuids(List<String> uuids) {
        this.uuids = uuids;
    }

    public SignatureParametersResponseDTO getResponse() {
        return response;
    }

    public void setResponse(SignatureParametersResponseDTO response) {
        this.response = response;
    }

    public SignatureParametersFeedbackDTO getFeedback() {
        return feedback;
    }

    public void setFeedback(SignatureParametersFeedbackDTO feedback) {
        this.feedback = feedback;
    }

    public String getPresentation() {
        return presentation;
    }

    public void setPresentation(String presentation) {
        this.presentation = presentation;
    }

    public String getMandatedText() {
        return mandatedText;
    }

    public void setMandatedText(String mandatedText) {
        this.mandatedText = mandatedText;
    }
}
