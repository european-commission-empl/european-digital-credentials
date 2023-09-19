package eu.europa.ec.empl.edci.datamodel.jsonld.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import eu.europa.ec.empl.edci.annotation.EDCIIdentifier;
import eu.europa.ec.empl.edci.datamodel.jsonld.model.base.JsonLdCommonDTO;
import eu.europa.ec.empl.edci.datamodel.jsonld.model.dataTypes.ConceptDTO;
import eu.europa.ec.empl.edci.datamodel.jsonld.model.dataTypes.Identifier;
import eu.europa.ec.empl.edci.datamodel.jsonld.model.dataTypes.LiteralMap;
import eu.europa.ec.empl.edci.datamodel.jsonld.model.dataTypes.NoteDTO;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@EDCIIdentifier(prefix = "urn:epass:learningOutcome:")
public class LearningOutcomeDTO extends JsonLdCommonDTO {

    private List<Identifier> identifier = new ArrayList<>();
    private List<NoteDTO> additionalNote = new ArrayList<>();
    private List<ConceptDTO> relatedESCOSkill = new ArrayList<>();
    private List<ConceptDTO> relatedSkill = new ArrayList<>();
    private ConceptDTO reusabilityLevel;
    @NotNull
    private LiteralMap title;
    private ConceptDTO dcType;

    public LearningOutcomeDTO() {
        super();
    }

    @JsonCreator
    public LearningOutcomeDTO(String uri) {
        super(uri);
    }

    public List<Identifier> getIdentifier() {
        return identifier;
    }

    public List<NoteDTO> getAdditionalNote() {
        return additionalNote;
    }

    public List<ConceptDTO> getRelatedESCOSkill() {
        return relatedESCOSkill;
    }

    public List<ConceptDTO> getRelatedSkill() {
        return relatedSkill;
    }

    public ConceptDTO getReusabilityLevel() {
        return reusabilityLevel;
    }

    public void setReusabilityLevel(ConceptDTO reusabilityLevel) {
        this.reusabilityLevel = reusabilityLevel;
    }

    public LiteralMap getTitle() {
        return title;
    }

    public void setTitle(LiteralMap title) {
        this.title = title;
    }

    public ConceptDTO getDcType() {
        return dcType;
    }

    public void setDcType(ConceptDTO dcType) {
        this.dcType = dcType;
    }

    public void setIdentifier(List<Identifier> identifier) {
        this.identifier = identifier;
    }

    public void setAdditionalNote(List<NoteDTO> additionalNote) {
        this.additionalNote = additionalNote;
    }

    public void setRelatedESCOSkill(List<ConceptDTO> relatedESCOSkill) {
        this.relatedESCOSkill = relatedESCOSkill;
    }

    public void setRelatedSkill(List<ConceptDTO> relatedSkill) {
        this.relatedSkill = relatedSkill;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LearningOutcomeDTO)) return false;
        if (!super.equals(o)) return false;
        LearningOutcomeDTO that = (LearningOutcomeDTO) o;
        return Objects.equals(identifier, that.identifier) &&
                Objects.equals(additionalNote, that.additionalNote) &&
                Objects.equals(relatedESCOSkill, that.relatedESCOSkill) &&
                Objects.equals(relatedSkill, that.relatedSkill) &&
                Objects.equals(reusabilityLevel, that.reusabilityLevel) &&
                Objects.equals(title, that.title) &&
                Objects.equals(dcType, that.dcType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), identifier, additionalNote, relatedESCOSkill, relatedSkill, reusabilityLevel, title, dcType);
    }
}
