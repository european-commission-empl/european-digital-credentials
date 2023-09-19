package eu.europa.ec.empl.edci.model.view.fields;

import java.util.List;

public class IndividualDisplayFieldView {
    private String language;
    private List<DisplayDetailFieldView> pages;

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public List<DisplayDetailFieldView> getPages() {
        return pages;
    }

    public void setPages(List<DisplayDetailFieldView> pages) {
        this.pages = pages;
    }
}
