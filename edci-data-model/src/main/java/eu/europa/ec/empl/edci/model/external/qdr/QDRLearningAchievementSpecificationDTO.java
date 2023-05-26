package eu.europa.ec.empl.edci.model.external.qdr;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import eu.europa.ec.empl.edci.annotation.EDCIIdentifier;
import eu.europa.ec.empl.edci.annotation.MandatoryConceptScheme;
import eu.europa.ec.empl.edci.datamodel.jsonld.model.AwardingOpportunityDTO;
import eu.europa.ec.empl.edci.datamodel.jsonld.model.QualificationDTO;
import org.joda.time.Period;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


@EDCIIdentifier(prefix = "urn:epass:learningAchievementSpec:")
@JsonSubTypes({
        @JsonSubTypes.Type(value = QualificationDTO.class)
})
@JsonTypeInfo(use = JsonTypeInfo.Id.DEDUCTION, property = "type", visible = true, defaultImpl = QDRLearningAchievementSpecificationDTO.class)
public class QDRLearningAchievementSpecificationDTO extends QDRSpecificationDTO {

    @JsonManagedReference
    private List<AwardingOpportunityDTO> awardingOpportunity = new ArrayList<>();
    private List<QDRCreditPointDTO> creditPoint = new ArrayList<>();
    private List<QDRConceptDTO> educationLevel = new ArrayList<>();
    private List<QDRConceptDTO> educationSubject = new ArrayList<>();
    private List<QDRLearningEntitlementSpecificationDTO> entitlesTo = new ArrayList<>();
    private QDRNoteDTO entryRequirement;
    private List<QDRLearningAchievementSpecificationDTO> generalisationOf = new ArrayList<>();
    private List<QDRLearningAchievementSpecificationDTO> hasPart = new ArrayList<>();
    private List<QDRLearningActivitySpecificationDTO> influencedBy = new ArrayList<>();
    private List<QDRLearningAchievementSpecificationDTO> isPartOf = new ArrayList<>();
    @MandatoryConceptScheme("http://publications.europa.eu/resource/authority/language")
    private List<QDRConceptDTO> language = new ArrayList<>();
    private List<QDRLearningOutcomeDTO> learningOutcome = new ArrayList<>();
    private QDRNoteDTO learningOutcomeSummary;
    @MandatoryConceptScheme("http://data.europa.eu/snb/learning-setting/25831c2")
    private QDRConceptDTO learningSetting;
    private Period maximumDuration;
    private List<QDRConceptDTO> mode = new ArrayList<>();
    private List<QDRLearningAssessmentSpecificationDTO> provenBy = new ArrayList<>();
    private List<QDRLearningAchievementSpecificationDTO> specialisationOf = new ArrayList<>();
    private List<QDRConceptDTO> targetGroup = new ArrayList<>();
    @MandatoryConceptScheme("http://data.europa.eu/snb/isced-f/25831c2")
    private List<QDRConceptDTO> thematicArea = new ArrayList<>();
    private Period volumeOfLearning;

    public List<AwardingOpportunityDTO> getAwardingOpportunity() {
        return awardingOpportunity;
    }

    public List<QDRCreditPointDTO> getCreditPoint() {
        return creditPoint;
    }

    public List<QDRConceptDTO> getEducationLevel() {
        return educationLevel;
    }

    public List<QDRConceptDTO> getEducationSubject() {
        return educationSubject;
    }

    public List<QDRLearningEntitlementSpecificationDTO> getEntitlesTo() {
        return entitlesTo;
    }

    public QDRNoteDTO getEntryRequirement() {
        return entryRequirement;
    }

    public void setEntryRequirement(QDRNoteDTO entryRequirement) {
        this.entryRequirement = entryRequirement;
    }

    public List<QDRLearningAchievementSpecificationDTO> getHasPart() {
        return hasPart;
    }

    public List<QDRLearningActivitySpecificationDTO> getInfluencedBy() {
        return influencedBy;
    }

    public List<QDRConceptDTO> getLanguage() {
        return language;
    }

    public List<QDRLearningOutcomeDTO> getLearningOutcome() {
        return learningOutcome;
    }

    public QDRNoteDTO getLearningOutcomeSummary() {
        return learningOutcomeSummary;
    }

    public void setLearningOutcomeSummary(QDRNoteDTO learningOutcomeSummary) {
        this.learningOutcomeSummary = learningOutcomeSummary;
    }

    public QDRConceptDTO getLearningSetting() {
        return learningSetting;
    }

    public void setLearningSetting(QDRConceptDTO learningSetting) {
        this.learningSetting = learningSetting;
    }

    public Period getMaximumDuration() {
        return maximumDuration;
    }

    public void setMaximumDuration(Period maximumDuration) {
        this.maximumDuration = maximumDuration;
    }

    public List<QDRConceptDTO> getMode() {
        return mode;
    }

    public List<QDRLearningAssessmentSpecificationDTO> getProvenBy() {
        return provenBy;
    }

    public List<QDRLearningAchievementSpecificationDTO> getSpecialisationOf() {
        return specialisationOf;
    }

    public List<QDRConceptDTO> getTargetGroup() {
        return targetGroup;
    }

    public List<QDRConceptDTO> getThematicArea() {
        return thematicArea;
    }

    public Period getVolumeOfLearning() {
        return volumeOfLearning;
    }

    public void setVolumeOfLearning(Period volumeOfLearning) {
        this.volumeOfLearning = volumeOfLearning;
    }

    public List<QDRLearningAchievementSpecificationDTO> getIsPartOf() {
        return isPartOf;
    }

    public List<QDRLearningAchievementSpecificationDTO> getGeneralisationOf() {
        return generalisationOf;
    }

    public void setAwardingOpportunity(List<AwardingOpportunityDTO> awardingOpportunity) {
        this.awardingOpportunity = awardingOpportunity;
    }

    public void setCreditPoint(List<QDRCreditPointDTO> creditPoint) {
        this.creditPoint = creditPoint;
    }

    public void setEducationLevel(List<QDRConceptDTO> educationLevel) {
        this.educationLevel = educationLevel;
    }

    public void setEducationSubject(List<QDRConceptDTO> educationSubject) {
        this.educationSubject = educationSubject;
    }

    public void setEntitlesTo(List<QDRLearningEntitlementSpecificationDTO> entitlesTo) {
        this.entitlesTo = entitlesTo;
    }

    public void setGeneralisationOf(List<QDRLearningAchievementSpecificationDTO> generalisationOf) {
        this.generalisationOf = generalisationOf;
    }

    public void setHasPart(List<QDRLearningAchievementSpecificationDTO> hasPart) {
        this.hasPart = hasPart;
    }

    public void setInfluencedBy(List<QDRLearningActivitySpecificationDTO> influencedBy) {
        this.influencedBy = influencedBy;
    }

    public void setIsPartOf(List<QDRLearningAchievementSpecificationDTO> isPartOf) {
        this.isPartOf = isPartOf;
    }

    public void setLanguage(List<QDRConceptDTO> language) {
        this.language = language;
    }

    public void setLearningOutcome(List<QDRLearningOutcomeDTO> learningOutcome) {
        this.learningOutcome = learningOutcome;
    }

    public void setMode(List<QDRConceptDTO> mode) {
        this.mode = mode;
    }

    public void setProvenBy(List<QDRLearningAssessmentSpecificationDTO> provenBy) {
        this.provenBy = provenBy;
    }

    public void setSpecialisationOf(List<QDRLearningAchievementSpecificationDTO> specialisationOf) {
        this.specialisationOf = specialisationOf;
    }

    public void setTargetGroup(List<QDRConceptDTO> targetGroup) {
        this.targetGroup = targetGroup;
    }

    public void setThematicArea(List<QDRConceptDTO> thematicArea) {
        this.thematicArea = thematicArea;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof QDRLearningAchievementSpecificationDTO)) return false;
        if (!super.equals(o)) return false;
        QDRLearningAchievementSpecificationDTO that = (QDRLearningAchievementSpecificationDTO) o;
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
