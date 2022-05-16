package eu.europa.ec.empl.edci.datamodel.view;

import java.util.List;

public class ActivitySpecTabView implements ITabView {

    private String title;
    private String description;
    private List<LinkFieldView> homePage;
    private List<IdentifierFieldView> identifier;
    private List<String> learningActivityType;
    private String modeOfLearning;
    private String workloadInHours;
    private List<String> instructionLanguage;
    private List<LinkFieldView> otherDocuments;
    private List<NoteFieldView> moreInformation;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

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

    public List<String> getLearningActivityType() {
        return learningActivityType;
    }

    public void setLearningActivityType(List<String> learningActivityType) {
        this.learningActivityType = learningActivityType;
    }

    public String getModeOfLearning() {
        return modeOfLearning;
    }

    public void setModeOfLearning(String modeOfLearning) {
        this.modeOfLearning = modeOfLearning;
    }

    public String getWorkloadInHours() {
        return workloadInHours;
    }

    public void setWorkloadInHours(String workloadInHours) {
        this.workloadInHours = workloadInHours;
    }

    public List<String> getInstructionLanguage() {
        return instructionLanguage;
    }

    public void setInstructionLanguage(List<String> instructionLanguage) {
        this.instructionLanguage = instructionLanguage;
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
