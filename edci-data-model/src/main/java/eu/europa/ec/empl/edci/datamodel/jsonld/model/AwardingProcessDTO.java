package eu.europa.ec.empl.edci.datamodel.jsonld.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import eu.europa.ec.empl.edci.annotation.EDCIIdentifier;
import eu.europa.ec.empl.edci.datamodel.jsonld.model.base.JsonLdCommonDTO;
import eu.europa.ec.empl.edci.datamodel.jsonld.model.dataTypes.ConceptDTO;
import eu.europa.ec.empl.edci.datamodel.jsonld.model.dataTypes.Identifier;
import eu.europa.ec.empl.edci.datamodel.jsonld.model.dataTypes.LiteralMap;
import eu.europa.ec.empl.edci.datamodel.jsonld.model.dataTypes.NoteDTO;

import javax.validation.constraints.NotNull;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@EDCIIdentifier(prefix = "urn:epass:awardingProcess:")
public class AwardingProcessDTO extends JsonLdCommonDTO {
    private List<LearningAssessmentDTO> used = new ArrayList<>();
    @NotNull
    private List<AgentDTO> awardingBody = new ArrayList<>();
    private ZonedDateTime awardingDate;
    @JsonIgnore
    private List<ClaimDTO> awards = new ArrayList<>();
    private LiteralMap description;
    private ConceptDTO educationalSystemNote;
    private List<Identifier> identifier = new ArrayList<>();
    private LocationDTO location;
    private List<NoteDTO> additionalNote = new ArrayList<>();

    public List<LearningAssessmentDTO> getUsed() {
        return used;
    }

    public List<@NotNull AgentDTO> getAwardingBody() {
        return awardingBody;
    }

    public ZonedDateTime getAwardingDate() {
        return awardingDate;
    }

    public void setAwardingDate(ZonedDateTime awardingDate) {
        this.awardingDate = awardingDate;
    }

    public List<ClaimDTO> getAwards() {
        return awards;
    }

    public LiteralMap getDescription() {
        return description;
    }

    public void setDescription(LiteralMap description) {
        this.description = description;
    }

    public List<Identifier> getIdentifier() {
        return identifier;
    }

    public LocationDTO getLocation() {
        return location;
    }

    public void setLocation(LocationDTO location) {
        this.location = location;
    }

    public List<NoteDTO> getAdditionalNote() {
        return additionalNote;
    }

    public ConceptDTO getEducationalSystemNote() {
        return educationalSystemNote;
    }

    public void setEducationalSystemNote(ConceptDTO educationalSystemNote) {
        this.educationalSystemNote = educationalSystemNote;
    }

    public void setUsed(List<LearningAssessmentDTO> used) {
        this.used = used;
    }

    public void setIdentifier(List<Identifier> identifier) {
        this.identifier = identifier;
    }

    public void setAdditionalNote(List<NoteDTO> additionalNote) {
        this.additionalNote = additionalNote;
    }

    public void setAwardingBody(List<AgentDTO> awardingBody) {
        this.awardingBody = awardingBody;
    }

    public void setAwards(List<ClaimDTO> awards) {
        this.awards = awards;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AwardingProcessDTO)) return false;
        if (!super.equals(o)) return false;
        AwardingProcessDTO that = (AwardingProcessDTO) o;
        return Objects.equals(used, that.used) &&
                Objects.equals(awardingBody, that.awardingBody) &&
                Objects.equals(awardingDate, that.awardingDate) &&
                Objects.equals(description, that.description) &&
                Objects.equals(educationalSystemNote, that.educationalSystemNote) &&
                Objects.equals(identifier, that.identifier) &&
                Objects.equals(location, that.location) &&
                Objects.equals(additionalNote, that.additionalNote);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), used, awardingBody, awardingDate, description, educationalSystemNote, identifier, location, additionalNote);
    }
}
