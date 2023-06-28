package eu.europa.ec.empl.edci.model.view.fields;

import java.util.List;

public class DisplayParametersFieldView {
    private List<String> language;
    private String description;
    private String summaryDisplay;
    private List<IndividualDisplayFieldView> individualDisplay;
    private String primaryLanguage;
    private String title;

    public List<String> getLanguage() {
        return language;
    }

    public void setLanguage(List<String> language) {
        this.language = language;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSummaryDisplay() {
        return summaryDisplay;
    }

    public void setSummaryDisplay(String summaryDisplay) {
        this.summaryDisplay = summaryDisplay;
    }

    public List<IndividualDisplayFieldView> getIndividualDisplay() {
        return individualDisplay;
    }

    public void setIndividualDisplay(List<IndividualDisplayFieldView> individualDisplay) {
        this.individualDisplay = individualDisplay;
    }

    public String getPrimaryLanguage() {
        return primaryLanguage;
    }

    public void setPrimaryLanguage(String primaryLanguage) {
        this.primaryLanguage = primaryLanguage;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
