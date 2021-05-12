package eu.europa.ec.empl.edci.issuer.common.model.signature;

import eu.europa.ec.empl.edci.issuer.common.constants.EDCIIssuerConstants;

import java.util.List;

public class SignatureParametersDTO {

    private String presentation = EDCIIssuerConstants.CREDENTIAL_TYPE_EUROPASS_PRESENTATION;
    private String Success;
    private List<String> uuids;
    private SignatureParametersResponseDTO response;
    private SignatureParametersFeedbackDTO feedback;

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
}
