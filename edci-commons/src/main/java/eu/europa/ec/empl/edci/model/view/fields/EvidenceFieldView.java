package eu.europa.ec.empl.edci.model.view.fields;

import eu.europa.ec.empl.edci.model.view.base.AgentView;

import java.util.ArrayList;
import java.util.List;

public class EvidenceFieldView {
    private List<MediaObjectFieldView> embeddedEvidence = new ArrayList<>();
    private AccreditationFieldView accreditation;
    private String evidenceStatement;
    private AgentView evidenceTarget;

    public List<MediaObjectFieldView> getEmbeddedEvidence() {
        return embeddedEvidence;
    }

    public void setEmbeddedEvidence(List<MediaObjectFieldView> embeddedEvidence) {
        this.embeddedEvidence = embeddedEvidence;
    }

    public AccreditationFieldView getAccreditation() {
        return accreditation;
    }

    public void setAccreditation(AccreditationFieldView accreditation) {
        this.accreditation = accreditation;
    }

    public String getEvidenceStatement() {
        return evidenceStatement;
    }

    public void setEvidenceStatement(String evidenceStatement) {
        this.evidenceStatement = evidenceStatement;
    }

    public AgentView getEvidenceTarget() {
        return evidenceTarget;
    }

    public void setEvidenceTarget(AgentView evidenceTarget) {
        this.evidenceTarget = evidenceTarget;
    }

}
