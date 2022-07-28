package eu.europa.ec.empl.edci.issuer.web.model.customization;

public class CustomizableRelationView {

    private Integer position;
    private String relPath;
    private String label;

    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }

    public String getRelPath() {
        return relPath;
    }

    public void setRelPath(String relPath) {
        this.relPath = relPath;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

}
