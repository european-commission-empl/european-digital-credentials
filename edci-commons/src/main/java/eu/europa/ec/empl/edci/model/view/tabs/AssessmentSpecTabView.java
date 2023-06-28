package eu.europa.ec.empl.edci.model.view.tabs;

import eu.europa.ec.empl.edci.model.view.base.ITabView;
import eu.europa.ec.empl.edci.model.view.base.SpecificationView;
import eu.europa.ec.empl.edci.model.view.fields.GradingSchemeFieldView;

import java.util.List;

public class AssessmentSpecTabView extends SpecificationView implements ITabView {

    private List<String> mode;
    private List<String> language;
    private GradingSchemeFieldView gradingScheme; //*

    public List<String> getMode() {
        return mode;
    }

    public void setMode(List<String> mode) {
        this.mode = mode;
    }

    public List<String> getLanguage() {
        return language;
    }

    public void setLanguage(List<String> language) {
        this.language = language;
    }

    public GradingSchemeFieldView getGradingScheme() {
        return gradingScheme;
    }

    public void setGradingScheme(GradingSchemeFieldView gradingScheme) {
        this.gradingScheme = gradingScheme;
    }

}
