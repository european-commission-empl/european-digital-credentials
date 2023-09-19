package eu.europa.ec.empl.edci.datamodel.upload;

import eu.europa.ec.empl.edci.datamodel.jsonld.model.MediaObjectDTO;

import java.util.Map;

public class DisplayDetailsDTO {

    private String template;
    private Map<String,Map<String, String>> labels;
    private MediaObjectDTO background;

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

    public MediaObjectDTO getBackground() {
        return background;
    }

    public void setBackground(MediaObjectDTO background) {
        this.background = background;
    }
}
