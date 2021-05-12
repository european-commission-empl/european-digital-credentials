package eu.europa.ec.empl.edci.datamodel.model;

import eu.europa.ec.empl.edci.annotation.EDCIIdentifier;
import eu.europa.ec.empl.edci.constants.MessageKeys;
import eu.europa.ec.empl.edci.datamodel.model.base.Nameable;
import eu.europa.ec.empl.edci.datamodel.model.base.RootEntity;
import eu.europa.ec.empl.edci.datamodel.model.dataTypes.Identifier;
import eu.europa.ec.empl.edci.datamodel.model.dataTypes.Note;
import eu.europa.ec.empl.edci.datamodel.model.dataTypes.Text;
import org.eclipse.persistence.oxm.annotations.XmlPath;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.*;
import java.net.URI;
import java.util.List;

@XmlRootElement(name = "learningAchievement")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {"id", "identifier", "title", "description", "additionalNote", "wasDerivedFrom", "wasInfluencedBy", "wasAwardedBy", "hasPart", "entitlesTo", "specifiedBy", "associatedLearningOpportunity"})
@EDCIIdentifier(prefix = "urn:epass:learningAchievement:")
public class LearningAchievementDTO implements RootEntity, Nameable {

    @XmlTransient
    private String pk;
    @XmlAttribute
    @NotNull(message = MessageKeys.Validation.VALIDATION_LEARNINGACHIEVEMENT_ID_NOTNULL)
    private URI id; //1
    @Valid
    private List<Identifier> identifier; //*
    @NotNull(message = MessageKeys.Validation.VALIDATION_LEARNINGACHIEVEMENT_TITLE_NOTNULL)
    @Valid
    private Text title; //1
    @Valid
    private Note description; //0..1
    @Valid
    private List<Note> additionalNote; //*
    @Valid
    private List<AssessmentDTO> wasDerivedFrom; //*
    private List<EntitlementDTO> entitlesTo; //*
    @Valid
    @XmlIDREF
    @XmlPath("wasAwardedBy/@idref")
    private AwardingProcessDTO wasAwardedBy; //1
    @XmlElementWrapper(name = "hasPart")
    @XmlElements({
            @XmlElement(name = "learningAchievement", type = LearningAchievementDTO.class),
            @XmlElement(name = "qualificationAward", type = QualificationAwardDTO.class),
    })
    @Valid
    private List<LearningAchievementDTO> hasPart; //*
    @XmlIDREF
    @XmlPath("specifiedBy/@idref")
    @NotNull(message = MessageKeys.Validation.VALIDATION_LEARNINGACHIEVEMENT_SPECIFIEDBY_NOTNULL)
    @Valid
    private LearningSpecificationDTO specifiedBy; //1
    @Valid
    private List<LearningActivityDTO> wasInfluencedBy;
    @Valid
    private LearningOpportunityDTO associatedLearningOpportunity;  //0..1


    public LearningAchievementDTO() {
        this.initIdentifiable();
    }

    @Override
    public URI getId() {
        return id;
    }

    @Override
    public String getIdentifiableName() {
        return this.getIdentifiableNameFromFieldList(this, "title", "description", "identifier", "additionalNote", "wasInfluencedBy", "id");
    }

    @Override
    public void setId(URI id) {
        this.id = id;
    }

    public List<Identifier> getIdentifier() {
        return identifier;
    }

    public void setIdentifier(List<Identifier> identifier) {
        this.identifier = identifier;
    }

    public Text getTitle() {
        return title;
    }

    public void setTitle(Text title) {
        this.title = title;
    }

    public Note getDescription() {
        return description;
    }

    public void setDescription(Note description) {
        this.description = description;
    }

    public List<Note> getAdditionalNote() {
        return additionalNote;
    }

    public void setAdditionalNote(List<Note> additionalNote) {
        this.additionalNote = additionalNote;
    }

    public List<AssessmentDTO> getWasDerivedFrom() {
        return wasDerivedFrom;
    }

    public void setWasDerivedFrom(List<AssessmentDTO> wasDerivedFrom) {
        this.wasDerivedFrom = wasDerivedFrom;
    }

    public List<EntitlementDTO> getEntitlesTo() {
        return entitlesTo;
    }

    public void setEntitlesTo(List<EntitlementDTO> entitlesTo) {
        this.entitlesTo = entitlesTo;
    }

    public AwardingProcessDTO getWasAwardedBy() {
        return wasAwardedBy;
    }

    public void setWasAwardedBy(AwardingProcessDTO wasAwardedBy) {
        this.wasAwardedBy = wasAwardedBy;
    }

    public List<LearningAchievementDTO> getHasPart() {
        return hasPart;
    }

    public void setHasPart(List<LearningAchievementDTO> hasPart) {
        this.hasPart = hasPart;
    }

    public LearningSpecificationDTO getSpecifiedBy() {
        return specifiedBy;
    }

    public void setSpecifiedBy(LearningSpecificationDTO specifiedBy) {
        this.specifiedBy = specifiedBy;
    }

    public List<LearningActivityDTO> getWasInfluencedBy() {
        return wasInfluencedBy;
    }

    public void setWasInfluencedBy(List<LearningActivityDTO> wasInfluencedBy) {
        this.wasInfluencedBy = wasInfluencedBy;
    }

    @Override
    public int hashCode() {
        return this.getHashCode();
    }

    @Override
    public boolean equals(Object object) {
        return this.isEquals(object);
    }

    @Override
    public String getPk() {
        return pk;
    }

    @Override
    public void setPk(String pk) {
        this.pk = pk;
    }

    public LearningOpportunityDTO getAssociatedLearningOpportunity() {
        return associatedLearningOpportunity;
    }

    public void setAssociatedLearningOpportunity(LearningOpportunityDTO associatedLearningOpportunity) {
        this.associatedLearningOpportunity = associatedLearningOpportunity;
    }
}