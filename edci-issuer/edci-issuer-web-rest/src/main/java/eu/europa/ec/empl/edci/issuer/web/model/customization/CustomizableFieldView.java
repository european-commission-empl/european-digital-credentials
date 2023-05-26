package eu.europa.ec.empl.edci.issuer.web.model.customization;

import java.util.List;

public class CustomizableFieldView {

    private Integer position;
    private String label;
    private String fieldPath;
    private Boolean mandatory;
    private Boolean relationDependant;
    private List<String> additionalInfo;

    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getFieldPath() {
        return fieldPath;
    }

    public void setFieldPath(String fieldPath) {
        this.fieldPath = fieldPath;
    }

    public Boolean getMandatory() {
        return mandatory;
    }

    public void setMandatory(Boolean mandatory) {
        this.mandatory = mandatory;
    }

    public Boolean getRelationDependant() {
        return relationDependant;
    }

    public void setRelationDependant(Boolean relationDependant) {
        this.relationDependant = relationDependant;
    }

    public List<String> getAdditionalInfo() {
        return additionalInfo;
    }

    public void setAdditionalInfo(List<String> additionalInfo) {
        this.additionalInfo = additionalInfo;
    }
}
