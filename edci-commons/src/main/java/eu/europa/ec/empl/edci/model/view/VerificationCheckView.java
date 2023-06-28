package eu.europa.ec.empl.edci.model.view;

import eu.europa.ec.empl.edci.model.view.fields.LinkFieldView;

import java.util.Map;

public class VerificationCheckView {

    private String id;
    private LinkFieldView type;
    private LinkFieldView status;
    private String longDescription;
    private String description;
    private Map<String, String> longDescrAvailableLangs;
    private Map<String, String> descrAvailableLangs;

    public LinkFieldView getType() {
        return type;
    }

    public void setType(LinkFieldView type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LinkFieldView getStatus() {
        return status;
    }

    public void setStatus(LinkFieldView status) {
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLongDescription() {
        return longDescription;
    }

    public void setLongDescription(String longDescription) {
        this.longDescription = longDescription;
    }

    public Map<String, String> getLongDescrAvailableLangs() {
        return longDescrAvailableLangs;
    }

    public void setLongDescrAvailableLangs(Map<String, String> longDescrAvailableLangs) {
        this.longDescrAvailableLangs = longDescrAvailableLangs;
    }

    public Map<String, String> getDescrAvailableLangs() {
        return descrAvailableLangs;
    }

    public void setDescrAvailableLangs(Map<String, String> descrAvailableLangs) {
        this.descrAvailableLangs = descrAvailableLangs;
    }
}
