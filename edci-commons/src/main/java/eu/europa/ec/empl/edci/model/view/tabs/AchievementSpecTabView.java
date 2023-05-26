package eu.europa.ec.empl.edci.model.view.tabs;


import eu.europa.ec.empl.edci.model.view.base.ITabView;
import eu.europa.ec.empl.edci.model.view.base.SpecificationView;
import eu.europa.ec.empl.edci.model.view.fields.AwardingOpportunityFieldView;
import eu.europa.ec.empl.edci.model.view.fields.LearningOutcomeFieldView;
import eu.europa.ec.empl.edci.model.view.fields.NoteFieldView;
import eu.europa.ec.empl.edci.model.view.fields.QualificationFieldView;

import java.util.ArrayList;
import java.util.List;

public class AchievementSpecTabView extends SpecificationView implements ITabView {

    private List<LearningOutcomeFieldView> learningOutcome;
    private String definition;
    private NoteFieldView learningOutcomeSummary; //This field is the one presented in the viewer and used in the OCB as specifiedBy description
    private String workloadInHours;
    private String maximumDuration;
    private List<String> creditPoint;
    private List<String> thematicArea;
    private List<String> targetGroup;
    private List<String> educationLevel;
    private String learningSetting;
    private NoteFieldView entryRequirement;
    private List<String> educationSubject;
    private List<String> language;
    private List<String> mode;
    private String volumeOfLearning;
    private List<AwardingOpportunityFieldView> awardingOpportunity = new ArrayList<>();

    private QualificationFieldView qualification; //If LearningSpecificationDTO is an instance of QualificationDTO. The extra fields will fit in here

    public List<LearningOutcomeFieldView> getLearningOutcome() {
        return learningOutcome;
    }

    public void setLearningOutcome(List<LearningOutcomeFieldView> learningOutcome) {
        this.learningOutcome = learningOutcome;
    }

    public String getDefinition() {
        return definition;
    }

    public void setDefinition(String definition) {
        this.definition = definition;
    }

    public String getWorkloadInHours() {
        return workloadInHours;
    }

    public void setWorkloadInHours(String workloadInHours) {
        this.workloadInHours = workloadInHours;
    }

    public String getMaximumDuration() {
        return maximumDuration;
    }

    public void setMaximumDuration(String maximumDuration) {
        this.maximumDuration = maximumDuration;
    }

    public List<String> getThematicArea() {
        return thematicArea;
    }

    public void setThematicArea(List<String> thematicArea) {
        this.thematicArea = thematicArea;
    }

    public List<String> getTargetGroup() {
        return targetGroup;
    }

    public void setTargetGroup(List<String> targetGroup) {
        this.targetGroup = targetGroup;
    }

    public List<String> getEducationLevel() {
        return educationLevel;
    }

    public void setEducationLevel(List<String> educationLevel) {
        this.educationLevel = educationLevel;
    }

    public String getLearningSetting() {
        return learningSetting;
    }

    public void setLearningSetting(String learningSetting) {
        this.learningSetting = learningSetting;
    }

    public NoteFieldView getEntryRequirement() {
        return entryRequirement;
    }

    public void setEntryRequirement(NoteFieldView entryRequirement) {
        this.entryRequirement = entryRequirement;
    }

    public QualificationFieldView getQualification() {
        return qualification;
    }

    public void setQualification(QualificationFieldView qualification) {
        this.qualification = qualification;
    }

    public List<String> getCreditPoint() {
        return creditPoint;
    }

    public void setCreditPoint(List<String> creditPoint) {
        this.creditPoint = creditPoint;
    }

    public NoteFieldView getLearningOutcomeSummary() {
        return learningOutcomeSummary;
    }

    public void setLearningOutcomeSummary(NoteFieldView learningOutcomeSummary) {
        this.learningOutcomeSummary = learningOutcomeSummary;
    }

    public List<String> getEducationSubject() {
        return educationSubject;
    }

    public void setEducationSubject(List<String> educationSubject) {
        this.educationSubject = educationSubject;
    }

    public List<String> getLanguage() {
        return language;
    }

    public void setLanguage(List<String> language) {
        this.language = language;
    }

    public List<String> getMode() {
        return mode;
    }

    public void setMode(List<String> mode) {
        this.mode = mode;
    }

    public String getVolumeOfLearning() {
        return volumeOfLearning;
    }

    public void setVolumeOfLearning(String volumeOfLearning) {
        this.volumeOfLearning = volumeOfLearning;
    }

    public List<AwardingOpportunityFieldView> getAwardingOpportunity() {
        return awardingOpportunity;
    }

    public void setAwardingOpportunity(List<AwardingOpportunityFieldView> awardingOpportunity) {
        this.awardingOpportunity = awardingOpportunity;
    }
}


