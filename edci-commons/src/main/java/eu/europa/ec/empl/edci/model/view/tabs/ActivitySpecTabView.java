package eu.europa.ec.empl.edci.model.view.tabs;

import eu.europa.ec.empl.edci.model.view.base.ITabView;
import eu.europa.ec.empl.edci.model.view.base.SpecificationView;

import java.util.ArrayList;
import java.util.List;

public class ActivitySpecTabView extends SpecificationView implements ITabView {

    private List<String> mode;
    private List<String> language;
    private List<String> contactHour = new ArrayList<>();
    private String volumeOfLearning;

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

    public List<String> getContactHour() {
        return contactHour;
    }

    public void setContactHour(List<String> contactHour) {
        this.contactHour = contactHour;
    }

    public String getVolumeOfLearning() {
        return volumeOfLearning;
    }

    public void setVolumeOfLearning(String volumeOfLearning) {
        this.volumeOfLearning = volumeOfLearning;
    }
}
