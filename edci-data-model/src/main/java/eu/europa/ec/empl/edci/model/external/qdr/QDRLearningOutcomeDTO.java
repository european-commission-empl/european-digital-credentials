package eu.europa.ec.empl.edci.model.external.qdr;

import eu.europa.ec.empl.edci.annotation.EDCIIdentifier;
import eu.europa.ec.empl.edci.annotation.MandatoryConceptScheme;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@EDCIIdentifier(prefix = "urn:epass:learningOutcome:")
public class QDRLearningOutcomeDTO extends QDRJsonLdCommonDTO {

    private List<QDRIdentifier> identifier = new ArrayList<>();
    private List<QDRNoteDTO> additionalNote = new ArrayList<>();
    @MandatoryConceptScheme("http://data.europa.eu/esco/concept-scheme/skills")
    private List<QDRConceptDTO> relatedESCOSkill = new ArrayList<>();
    private List<QDRConceptDTO> relatedSkill = new ArrayList<>();
    @MandatoryConceptScheme("http://data.europa.eu/snb/skill-reuse-level/25831c2")
    private QDRConceptDTO reusabilityLevel;
    @NotNull
    private String title;
    @MandatoryConceptScheme("http://data.europa.eu/snb/skill-type/25831c2")
    private QDRConceptDTO type;

    public List<QDRIdentifier> getQDRIdentifier() {
        return identifier;
    }

    public List<QDRNoteDTO> getAdditionalNote() {
        return additionalNote;
    }

    public List<QDRConceptDTO> getRelatedESCOSkill() {
        return relatedESCOSkill;
    }

    public List<QDRConceptDTO> getRelatedSkill() {
        return relatedSkill;
    }

    public QDRConceptDTO getReusabilityLevel() {
        return reusabilityLevel;
    }

    public void setReusabilityLevel(QDRConceptDTO reusabilityLevel) {
        this.reusabilityLevel = reusabilityLevel;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public QDRConceptDTO getType() {
        return type;
    }

    public void setType(QDRConceptDTO type) {
        this.type = type;
    }

    public void setQDRIdentifier(List<QDRIdentifier> identifier) {
        this.identifier = identifier;
    }

    public void setAdditionalNote(List<QDRNoteDTO> additionalNote) {
        this.additionalNote = additionalNote;
    }

    public void setRelatedESCOSkill(List<QDRConceptDTO> relatedESCOSkill) {
        this.relatedESCOSkill = relatedESCOSkill;
    }

    public void setRelatedSkill(List<QDRConceptDTO> relatedSkill) {
        this.relatedSkill = relatedSkill;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof QDRLearningOutcomeDTO)) return false;
        if (!super.equals(o)) return false;
        QDRLearningOutcomeDTO that = (QDRLearningOutcomeDTO) o;
        return Objects.equals(identifier, that.identifier) &&
                Objects.equals(additionalNote, that.additionalNote) &&
                Objects.equals(relatedESCOSkill, that.relatedESCOSkill) &&
                Objects.equals(relatedSkill, that.relatedSkill) &&
                Objects.equals(reusabilityLevel, that.reusabilityLevel) &&
                Objects.equals(title, that.title) &&
                Objects.equals(type, that.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), identifier, additionalNote, relatedESCOSkill, relatedSkill, reusabilityLevel, title, type);
    }
}
