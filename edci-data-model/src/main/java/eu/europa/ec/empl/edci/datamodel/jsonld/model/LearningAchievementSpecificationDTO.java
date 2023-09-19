package eu.europa.ec.empl.edci.datamodel.jsonld.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import eu.europa.ec.empl.edci.annotation.EDCIIdentifier;
import eu.europa.ec.empl.edci.datamodel.jsonld.model.dataTypes.ConceptDTO;
import eu.europa.ec.empl.edci.datamodel.jsonld.model.dataTypes.CreditPointDTO;
import eu.europa.ec.empl.edci.datamodel.jsonld.model.dataTypes.NoteDTO;
import org.joda.time.Period;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


@EDCIIdentifier(prefix = "urn:epass:learningAchievementSpec:")
@JsonSubTypes({
        @JsonSubTypes.Type(value = QualificationDTO.class)
})
@JsonTypeInfo(use = JsonTypeInfo.Id.DEDUCTION, property = "type", visible = true, defaultImpl = LearningAchievementSpecificationDTO.class)
public class LearningAchievementSpecificationDTO extends SpecificationDTO {

    @JsonManagedReference
    private List<AwardingOpportunityDTO> awardingOpportunity = new ArrayList<>();
    private List<CreditPointDTO> creditPoint = new ArrayList<>();
    private List<ConceptDTO> educationLevel = new ArrayList<>();
    private List<ConceptDTO> educationSubject = new ArrayList<>();
    private List<LearningEntitlementSpecificationDTO> entitlesTo = new ArrayList<>();
    private NoteDTO entryRequirement;
    private List<LearningAchievementSpecificationDTO> generalisationOf = new ArrayList<>();
    private List<LearningAchievementSpecificationDTO> hasPart = new ArrayList<>();
    private List<LearningActivitySpecificationDTO> influencedBy = new ArrayList<>();
    private List<LearningAchievementSpecificationDTO> isPartOf = new ArrayList<>();
    private List<ConceptDTO> language = new ArrayList<>();
    private List<LearningOutcomeDTO> learningOutcome = new ArrayList<>();
    private NoteDTO learningOutcomeSummary;
    private ConceptDTO learningSetting;
    private Period maximumDuration;
    private List<ConceptDTO> mode = new ArrayList<>();
    private List<LearningAssessmentSpecificationDTO> provenBy = new ArrayList<>();
    private List<LearningAchievementSpecificationDTO> specialisationOf = new ArrayList<>();
    private List<ConceptDTO> targetGroup = new ArrayList<>();
    private List<ConceptDTO> thematicArea = new ArrayList<>();
    private Period volumeOfLearning;

    public LearningAchievementSpecificationDTO() {
        super();
    }

    @JsonCreator
    public LearningAchievementSpecificationDTO(String uri) {
        super(uri);
    }
    
    public List<AwardingOpportunityDTO> getAwardingOpportunity() {
        return awardingOpportunity;
    }

    public List<CreditPointDTO> getCreditPoint() {
        return creditPoint;
    }

    public List<ConceptDTO> getEducationLevel() {
        return educationLevel;
    }

    public List<ConceptDTO> getEducationSubject() {
        return educationSubject;
    }

    public List<LearningEntitlementSpecificationDTO> getEntitlesTo() {
        return entitlesTo;
    }

    public NoteDTO getEntryRequirement() {
        return entryRequirement;
    }

    public void setEntryRequirement(NoteDTO entryRequirement) {
        this.entryRequirement = entryRequirement;
    }

    public List<LearningAchievementSpecificationDTO> getHasPart() {
        return hasPart;
    }

    public List<LearningActivitySpecificationDTO> getInfluencedBy() {
        return influencedBy;
    }

    public List<ConceptDTO> getLanguage() {
        return language;
    }

    public List<LearningOutcomeDTO> getLearningOutcome() {
        return learningOutcome;
    }

    public NoteDTO getLearningOutcomeSummary() {
        return learningOutcomeSummary;
    }

    public void setLearningOutcomeSummary(NoteDTO learningOutcomeSummary) {
        this.learningOutcomeSummary = learningOutcomeSummary;
    }

    public ConceptDTO getLearningSetting() {
        return learningSetting;
    }

    public void setLearningSetting(ConceptDTO learningSetting) {
        this.learningSetting = learningSetting;
    }

    public Period getMaximumDuration() {
        return maximumDuration;
    }

    public void setMaximumDuration(Period maximumDuration) {
        this.maximumDuration = maximumDuration;
    }

    public List<ConceptDTO> getMode() {
        return mode;
    }

    public List<LearningAssessmentSpecificationDTO> getProvenBy() {
        return provenBy;
    }

    public List<LearningAchievementSpecificationDTO> getSpecialisationOf() {
        return specialisationOf;
    }

    public List<ConceptDTO> getTargetGroup() {
        return targetGroup;
    }

    public List<ConceptDTO> getThematicArea() {
        return thematicArea;
    }

    public Period getVolumeOfLearning() {
        return volumeOfLearning;
    }

    public void setVolumeOfLearning(Period volumeOfLearning) {
        this.volumeOfLearning = volumeOfLearning;
    }

    public List<LearningAchievementSpecificationDTO> getIsPartOf() {
        return isPartOf;
    }

    public List<LearningAchievementSpecificationDTO> getGeneralisationOf() {
        return generalisationOf;
    }

    public void setAwardingOpportunity(List<AwardingOpportunityDTO> awardingOpportunity) {
        this.awardingOpportunity = awardingOpportunity;
    }

    public void setCreditPoint(List<CreditPointDTO> creditPoint) {
        this.creditPoint = creditPoint;
    }

    public void setEducationLevel(List<ConceptDTO> educationLevel) {
        this.educationLevel = educationLevel;
    }

    public void setEducationSubject(List<ConceptDTO> educationSubject) {
        this.educationSubject = educationSubject;
    }

    public void setEntitlesTo(List<LearningEntitlementSpecificationDTO> entitlesTo) {
        this.entitlesTo = entitlesTo;
    }

    public void setGeneralisationOf(List<LearningAchievementSpecificationDTO> generalisationOf) {
        this.generalisationOf = generalisationOf;
    }

    public void setHasPart(List<LearningAchievementSpecificationDTO> hasPart) {
        this.hasPart = hasPart;
    }

    public void setInfluencedBy(List<LearningActivitySpecificationDTO> influencedBy) {
        this.influencedBy = influencedBy;
    }

    public void setIsPartOf(List<LearningAchievementSpecificationDTO> isPartOf) {
        this.isPartOf = isPartOf;
    }

    public void setLanguage(List<ConceptDTO> language) {
        this.language = language;
    }

    public void setLearningOutcome(List<LearningOutcomeDTO> learningOutcome) {
        this.learningOutcome = learningOutcome;
    }

    public void setMode(List<ConceptDTO> mode) {
        this.mode = mode;
    }

    public void setProvenBy(List<LearningAssessmentSpecificationDTO> provenBy) {
        this.provenBy = provenBy;
    }

    public void setSpecialisationOf(List<LearningAchievementSpecificationDTO> specialisationOf) {
        this.specialisationOf = specialisationOf;
    }

    public void setTargetGroup(List<ConceptDTO> targetGroup) {
        this.targetGroup = targetGroup;
    }

    public void setThematicArea(List<ConceptDTO> thematicArea) {
        this.thematicArea = thematicArea;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LearningAchievementSpecificationDTO)) return false;
        if (!super.equals(o)) return false;
        LearningAchievementSpecificationDTO that = (LearningAchievementSpecificationDTO) o;
        return Objects.equals(awardingOpportunity, that.awardingOpportunity) &&
                Objects.equals(creditPoint, that.creditPoint) &&
                Objects.equals(educationLevel, that.educationLevel) &&
                Objects.equals(educationSubject, that.educationSubject) &&
                Objects.equals(entitlesTo, that.entitlesTo) &&
                Objects.equals(entryRequirement, that.entryRequirement) &&
                Objects.equals(generalisationOf, that.generalisationOf) &&
                Objects.equals(hasPart, that.hasPart) &&
                Objects.equals(influencedBy, that.influencedBy) &&
                Objects.equals(isPartOf, that.isPartOf) &&
                Objects.equals(language, that.language) &&
                Objects.equals(learningOutcome, that.learningOutcome) &&
                Objects.equals(learningOutcomeSummary, that.learningOutcomeSummary) &&
                Objects.equals(learningSetting, that.learningSetting) &&
                Objects.equals(maximumDuration, that.maximumDuration) &&
                Objects.equals(mode, that.mode) &&
                Objects.equals(provenBy, that.provenBy) &&
                Objects.equals(specialisationOf, that.specialisationOf) &&
                Objects.equals(targetGroup, that.targetGroup) &&
                Objects.equals(thematicArea, that.thematicArea) &&
                Objects.equals(volumeOfLearning, that.volumeOfLearning);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), awardingOpportunity, creditPoint, educationLevel, educationSubject, entitlesTo, entryRequirement, generalisationOf, hasPart, influencedBy, isPartOf, language, learningOutcome, learningOutcomeSummary, learningSetting, maximumDuration, mode, provenBy, specialisationOf, targetGroup, thematicArea, volumeOfLearning);
    }
}
