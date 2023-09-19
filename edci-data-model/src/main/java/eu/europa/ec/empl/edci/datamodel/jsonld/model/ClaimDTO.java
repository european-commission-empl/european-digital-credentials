package eu.europa.ec.empl.edci.datamodel.jsonld.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import eu.europa.ec.empl.edci.annotation.EDCIIdentifier;
import eu.europa.ec.empl.edci.datamodel.jsonld.model.base.JsonLdCommonDTO;
import eu.europa.ec.empl.edci.datamodel.jsonld.model.dataTypes.*;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@EDCIIdentifier(prefix = "urn:epass:claim:")
@JsonSubTypes({
        @JsonSubTypes.Type(value = LearningEntitlementDTO.class, name = "LearningEntitlement"),
        @JsonSubTypes.Type(value = LearningAchievementDTO.class, name = "LearningAchievement"),
        @JsonSubTypes.Type(value = LearningActivityDTO.class, name = "LearningActivity"),
        @JsonSubTypes.Type(value = LearningAssessmentDTO.class, name = "LearningAssessment")}
)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY,  property = "type", visible = true, defaultImpl = ClaimDTO.class)
public class ClaimDTO extends JsonLdCommonDTO {

    @NotNull
    private AwardingProcessDTO awardedBy;
    private LiteralMap description;
    private List<Identifier> identifier = new ArrayList<>();
    private List<NoteDTO> additionalNote = new ArrayList<>();
    private List<WebResourceDTO> supplementaryDocument = new ArrayList<>();
    @NotNull
    private LiteralMap title;
    private List<ConceptDTO> dcType = new ArrayList<>();

    public ClaimDTO() {
        super();
    }

    @JsonCreator
    public ClaimDTO(String uri) {
        super(uri);
    }

    @Override
    public String getName() {
        return this.getNameFromFieldList(this, "title", "identifier", "description", "dcType", "type", "id");
    }

    public LiteralMap getTitle() {
        return title;
    }

    public void setTitle(LiteralMap title) {
        this.title = title;
    }

    public AwardingProcessDTO getAwardedBy() {
        return awardedBy;
    }

    public void setAwardedBy(AwardingProcessDTO awardedBy) {
        this.awardedBy = awardedBy;
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

    public List<NoteDTO> getAdditionalNote() {
        return additionalNote;
    }

    public List<WebResourceDTO> getSupplementaryDocument() {
        return supplementaryDocument;
    }

    public List<ConceptDTO> getDcType() {
        return dcType;
    }

    public void setIdentifier(List<Identifier> identifier) {
        this.identifier = identifier;
    }

    public void setAdditionalNote(List<NoteDTO> additionalNote) {
        this.additionalNote = additionalNote;
    }

    public void setSupplementaryDocument(List<WebResourceDTO> supplementaryDocument) {
        this.supplementaryDocument = supplementaryDocument;
    }

    public void setDcType(List<ConceptDTO> dcType) {
        this.dcType = dcType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ClaimDTO)) return false;
        if (!super.equals(o)) return false;
        ClaimDTO claimDTO = (ClaimDTO) o;
        return Objects.equals(awardedBy, claimDTO.awardedBy) &&
                Objects.equals(description, claimDTO.description) &&
                Objects.equals(identifier, claimDTO.identifier) &&
                Objects.equals(additionalNote, claimDTO.additionalNote) &&
                Objects.equals(supplementaryDocument, claimDTO.supplementaryDocument) &&
                Objects.equals(title, claimDTO.title) &&
                Objects.equals(dcType, claimDTO.dcType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), awardedBy, description, identifier, additionalNote, supplementaryDocument, title, dcType);
    }
}
