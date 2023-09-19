package eu.europa.ec.empl.edci.issuer.web.model.customization;

public class CustomizedRelationView {

    private Boolean included;
    private String relPathIdentifier;

    public Boolean getIncluded() {
        return included;
    }

    public void setIncluded(Boolean included) {
        this.included = included;
    }

    public String getRelPathIdentifier() {
        return relPathIdentifier;
    }

    public void setRelPathIdentifier(String relPathIdentifier) {
        this.relPathIdentifier = relPathIdentifier;
    }
}
