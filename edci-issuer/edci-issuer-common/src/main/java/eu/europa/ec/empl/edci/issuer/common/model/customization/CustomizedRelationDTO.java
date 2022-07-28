package eu.europa.ec.empl.edci.issuer.common.model.customization;

public class CustomizedRelationDTO {

    private Boolean included;
    private String relPathIdentifier;

    public CustomizedRelationDTO() {
    }

    public CustomizedRelationDTO(Boolean included, String relPathIdentifier) {
        this.included = included;
        this.relPathIdentifier = relPathIdentifier;
    }

    public Boolean getIncluded() {
        return included != null ? included : false;
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
