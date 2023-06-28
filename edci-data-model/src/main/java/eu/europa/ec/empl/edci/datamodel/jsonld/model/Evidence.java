package eu.europa.ec.empl.edci.datamodel.jsonld.model;

import eu.europa.ec.empl.edci.annotation.EDCIIdentifier;
import eu.europa.ec.empl.edci.datamodel.jsonld.model.base.JsonLdCommonDTO;
import eu.europa.ec.empl.edci.datamodel.jsonld.model.dataTypes.ConceptDTO;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@EDCIIdentifier(prefix = "urn:epass:evidence:")
public class Evidence extends JsonLdCommonDTO {

    private List<MediaObjectDTO> embeddedEvidence = new ArrayList<>();
    private AccreditationDTO accreditation;
    private String evidenceStatement;
    private AgentDTO evidenceTarget;
    private ConceptDTO dcType;

    public void setEvidenceTarget(AgentDTO evidenceTarget) {
        this.evidenceTarget = evidenceTarget;
    }

    public void setEvidenceStatement(String evidenceStatement) {
        this.evidenceStatement = evidenceStatement;
    }

    public List<MediaObjectDTO> getEmbeddedEvidence() {
        return embeddedEvidence;
    }

    public AccreditationDTO getAccreditation() {
        return accreditation;
    }

    public void setAccreditation(AccreditationDTO accreditation) {
        this.accreditation = accreditation;
    }

    public String getEvidenceStatement() {
        return evidenceStatement;
    }

    public AgentDTO getEvidenceTarget() {
        return evidenceTarget;
    }

    public void setEmbeddedEvidence(List<MediaObjectDTO> embeddedEvidence) {
        this.embeddedEvidence = embeddedEvidence;
    }

    public ConceptDTO getDcType() {
        return dcType;
    }

    public void setDcType(ConceptDTO dcType) {
        this.dcType = dcType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Evidence)) return false;
        if (!super.equals(o)) return false;
        Evidence evidence = (Evidence) o;
        return Objects.equals(embeddedEvidence, evidence.embeddedEvidence) &&
                Objects.equals(accreditation, evidence.accreditation) &&
                Objects.equals(evidenceStatement, evidence.evidenceStatement) &&
                Objects.equals(evidenceTarget, evidence.evidenceTarget) &&
                Objects.equals(dcType, evidence.dcType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), embeddedEvidence, accreditation, evidenceStatement, evidenceTarget, dcType);
    }
}
