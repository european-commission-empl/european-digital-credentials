package eu.europa.ec.empl.edci.model.view.fields;

import java.util.List;

public class GroupFieldView {
    private String altLabel;
    private List<ContactPointFieldView> contactPoint;
    private String prefLabel;

    public String getAltLabel() {
        return altLabel;
    }

    public void setAltLabel(String altLabel) {
        this.altLabel = altLabel;
    }

    public String getPrefLabel() {
        return prefLabel;
    }

    public void setPrefLabel(String prefLabel) {
        this.prefLabel = prefLabel;
    }

    public List<ContactPointFieldView> getContactPoint() {
        return contactPoint;
    }

    public void setContactPoint(List<ContactPointFieldView> contactPoint) {
        this.contactPoint = contactPoint;
    }
}
