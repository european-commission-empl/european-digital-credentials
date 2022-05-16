package eu.europa.ec.empl.edci.datamodel.view;

import java.util.List;

public class



AchievementSpecTabView implements ITabView {

    private List<LinkFieldView> homePage;
    private List<IdentifierFieldView> identifier;
    private List<LearningOutcomeFieldView> learningOutcome;
    private String definition;
    private String title;
    private String workloadInHours;
    private String maximumDurationInMonths;
    private String ectsCreditPoints;
    private List<String> creditPoints;
    private List<String> thematicArea;
    private List<String> learningMode;
    private List<String> instructionLanguage;
    private List<String> targetGroup;
    private List<String> educationLevel;
    private String learningSetting;
    private List<String> learningOpportunityType;
    private String entryRequirements;
    private List<LinkFieldView> otherDocuments;
    private List<NoteFieldView> moreInformation;

    private QualificationFieldView qualification; //If LearningSpecificationDTO is an instance of QualificationDTO. The extra fields will fit in here

    public List<LinkFieldView> getHomePage() {
        return homePage;
    }

    public void setHomePage(List<LinkFieldView> homePage) {
        this.homePage = homePage;
    }

    public List<IdentifierFieldView> getIdentifier() {
        return identifier;
    }

    public void setIdentifier(List<IdentifierFieldView> identifier) {
        this.identifier = identifier;
    }

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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getWorkloadInHours() {
        return workloadInHours;
    }

    public void setWorkloadInHours(String workloadInHours) {
        this.workloadInHours = workloadInHours;
    }

    public String getMaximumDurationInMonths() {
        return maximumDurationInMonths;
    }

    public void setMaximumDurationInMonths(String maximumDurationInMonths) {
        this.maximumDurationInMonths = maximumDurationInMonths;
    }

    public String getEctsCreditPoints() {
        return ectsCreditPoints;
    }

    public void setEctsCreditPoints(String ectsCreditPoints) {
        this.ectsCreditPoints = ectsCreditPoints;
    }

    public List<String> getThematicArea() {
        return thematicArea;
    }

    public void setThematicArea(List<String> thematicArea) {
        this.thematicArea = thematicArea;
    }

    public List<String> getLearningMode() {
        return learningMode;
    }

    public void setLearningMode(List<String> learningMode) {
        this.learningMode = learningMode;
    }

    public List<String> getInstructionLanguage() {
        return instructionLanguage;
    }

    public void setInstructionLanguage(List<String> instructionLanguage) {
        this.instructionLanguage = instructionLanguage;
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

    public List<String> getLearningOpportunityType() {
        return learningOpportunityType;
    }

    public void setLearningOpportunityType(List<String> learningOpportunityType) {
        this.learningOpportunityType = learningOpportunityType;
    }

    public String getEntryRequirements() {
        return entryRequirements;
    }

    public void setEntryRequirements(String entryRequirements) {
        this.entryRequirements = entryRequirements;
    }

    public List<LinkFieldView> getOtherDocuments() {
        return otherDocuments;
    }

    public void setOtherDocuments(List<LinkFieldView> otherDocuments) {
        this.otherDocuments = otherDocuments;
    }

    public List<NoteFieldView> getMoreInformation() {
        return moreInformation;
    }

    public void setMoreInformation(List<NoteFieldView> moreInformation) {
        this.moreInformation = moreInformation;
    }

    public QualificationFieldView getQualification() {
        return qualification;
    }

    public void setQualification(QualificationFieldView qualification) {
        this.qualification = qualification;
    }


    public List<String> getCreditPoints() {
        return creditPoints;
    }

    public void setCreditPoints(List<String> creditPoints) {
        this.creditPoints = creditPoints;
    }
}


