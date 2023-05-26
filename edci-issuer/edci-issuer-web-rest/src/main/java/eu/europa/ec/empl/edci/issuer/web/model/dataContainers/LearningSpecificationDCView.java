package eu.europa.ec.empl.edci.issuer.web.model.dataContainers;

import eu.europa.ec.empl.edci.issuer.web.model.dataTypes.*;

import java.util.List;
import java.util.Set;

public class LearningSpecificationDCView extends DataContainerView {

    private Set<IdentifierDTView> identifier; //*

    private Set<CodeDTView> learningOpportunityType; //*

    private TextDTView title; //0..1

    private TextDTView altLabel; //*

    private NoteDTView definition; //0..1

    private NoteDTView learningOutcomeSummary; //0..1

    private Set<NoteDTView> additionalNote; //*

    private Set<WebDocumentDCView> homePage; //*

    private Set<WebDocumentDCView> supplementaryDocument; //*

    private Set<CodeDTView> thematicArea; //*

    private List<CodeDTView> educationSubject; //* EducationSubjectAssociationDTView

    private Long volumeOfLearning; //0..1

    private List<CreditPointView> creditPoints; //*

    private List<CodeDTView> educationLevel; //* EducationSubjectAssociationDTView

    private Set<CodeDTView> language; //*

    private Set<CodeDTView> mode; //*

    private CodeDTView learningSetting; //0..1

    private Integer maximumDuration; //0..1

    private List<CodeDTView> targetGroup; //*

    private NoteDTView entryRequirement; //0..1

    private Set<AwardingOpportunityDCView> awardingOpportunity; //*

    private CodeDTView eqfLevel; //0..1

    private List<CodeDTView> nqfLevel; //*

    private CodeDTView nqfLevelParent; //0..1

    private Boolean isPartialQualification; //0..1

    private List<CodeDTView> qualificationCode; //*

    private List<CodeDTView> dcType;

    public List<CodeDTView> getDcType() {
        return dcType;
    }

    public void setDcType(List<CodeDTView> dcType) {
        this.dcType = dcType;
    }

    public Set<IdentifierDTView> getIdentifier() {
        return identifier;
    }

    public void setIdentifier(Set<IdentifierDTView> identifier) {
        this.identifier = identifier;
    }

    public CodeDTView getEqfLevel() {
        return eqfLevel;
    }

    public void setEqfLevel(CodeDTView eqfLevel) {
        this.eqfLevel = eqfLevel;
    }

    public List<CodeDTView> getNqfLevel() {
        return nqfLevel;
    }

    public void setNqfLevel(List<CodeDTView> nqfLevel) {
        this.nqfLevel = nqfLevel;
    }

    public Boolean getPartialQualification() {
        return isPartialQualification;
    }

    public void setPartialQualification(Boolean partialQualification) {
        isPartialQualification = partialQualification;
    }

    public CodeDTView getNqfLevelParent() {
        return nqfLevelParent;
    }

    public void setNqfLevelParent(CodeDTView nqfLevelParent) {
        this.nqfLevelParent = nqfLevelParent;
    }

    public List<CodeDTView> getQualificationCode() {
        return qualificationCode;
    }

    public void setQualificationCode(List<CodeDTView> qualificationCode) {
        this.qualificationCode = qualificationCode;
    }

    public Set<CodeDTView> getLearningOpportunityType() {
        return learningOpportunityType;
    }

    public void setLearningOpportunityType(Set<CodeDTView> learningOpportunityType) {
        this.learningOpportunityType = learningOpportunityType;
    }

    public TextDTView getTitle() {
        return title;
    }

    public void setTitle(TextDTView title) {
        this.title = title;
    }

    public TextDTView getAltLabel() {
        return altLabel;
    }

    public void setAltLabel(TextDTView altLabel) {
        this.altLabel = altLabel;
    }

    public NoteDTView getDefinition() {
        return definition;
    }

    public void setDefinition(NoteDTView definition) {
        this.definition = definition;
    }

    public NoteDTView getLearningOutcomeSummary() {
        return learningOutcomeSummary;
    }

    public void setLearningOutcomeSummary(NoteDTView learningOutcomeSummary) {
        this.learningOutcomeSummary = learningOutcomeSummary;
    }

    public Set<NoteDTView> getAdditionalNote() {
        return additionalNote;
    }

    public void setAdditionalNote(Set<NoteDTView> additionalNote) {
        this.additionalNote = additionalNote;
    }

    public Set<WebDocumentDCView> getHomePage() {
        return homePage;
    }

    public void setHomePage(Set<WebDocumentDCView> homePage) {
        this.homePage = homePage;
    }

    public Set<WebDocumentDCView> getSupplementaryDocument() {
        return supplementaryDocument;
    }

    public void setSupplementaryDocument(Set<WebDocumentDCView> supplementaryDocument) {
        this.supplementaryDocument = supplementaryDocument;
    }

    public Long getVolumeOfLearning() {
        return volumeOfLearning;
    }

    public void setVolumeOfLearning(Long volumeOfLearning) {
        this.volumeOfLearning = volumeOfLearning;
    }

    public List<CreditPointView> getCreditPoints() {
        return creditPoints;
    }

    public void setCreditPoints(List<CreditPointView> creditPoints) {
        this.creditPoints = creditPoints;
    }

    public List<CodeDTView> getEducationSubject() {
        return educationSubject;
    }

    public void setEducationSubject(List<CodeDTView> educationSubject) {
        this.educationSubject = educationSubject;
    }

    public List<CodeDTView> getEducationLevel() {
        return educationLevel;
    }

    public void setEducationLevel(List<CodeDTView> educationLevel) {
        this.educationLevel = educationLevel;
    }

    public Set<CodeDTView> getLanguage() {
        return language;
    }

    public void setLanguage(Set<CodeDTView> language) {
        this.language = language;
    }

    public Set<CodeDTView> getMode() {
        return mode;
    }

    public void setMode(Set<CodeDTView> mode) {
        this.mode = mode;
    }

    public CodeDTView getLearningSetting() {
        return learningSetting;
    }

    public void setLearningSetting(CodeDTView learningSetting) {
        this.learningSetting = learningSetting;
    }

    public Integer getMaximumDuration() {
        return maximumDuration;
    }

    public void setMaximumDuration(Integer maximumDuration) {
        this.maximumDuration = maximumDuration;
    }

    public List<CodeDTView> getTargetGroup() {
        return targetGroup;
    }

    public void setTargetGroup(List<CodeDTView> targetGroup) {
        this.targetGroup = targetGroup;
    }

    public NoteDTView getEntryRequirement() {
        return entryRequirement;
    }

    public void setEntryRequirement(NoteDTView entryRequirement) {
        this.entryRequirement = entryRequirement;
    }

    public Set<CodeDTView> getThematicArea() {
        return thematicArea;
    }

    public void setThematicArea(Set<CodeDTView> thematicArea) {
        this.thematicArea = thematicArea;
    }

    public Set<AwardingOpportunityDCView> getAwardingOpportunity() {
        return awardingOpportunity;
    }

    public void setAwardingOpportunity(Set<AwardingOpportunityDCView> awardingOpportunity) {
        this.awardingOpportunity = awardingOpportunity;
    }
}