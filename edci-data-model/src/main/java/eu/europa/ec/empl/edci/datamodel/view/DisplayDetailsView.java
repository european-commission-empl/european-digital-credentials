package eu.europa.ec.empl.edci.datamodel.view;

import java.util.Map;

public class DisplayDetailsView {

    private String template;
    private Map<String,Map<String, String>> labels;
    private MediaObjectView background;

    public String getTemplate() {
        return template;
    }

    public void setTemplate(String template) {
        this.template = template;
    }

    public Map<String,Map<String, String>> getLabels() {
        return labels;
    }

    public void setLabels(Map<String,Map<String, String>> labels) {
        this.labels = labels;
    }

    public MediaObjectView getBackground() {
        return background;
    }

    public void setBackground(MediaObjectView background) {
        this.background = background;
    }
}
