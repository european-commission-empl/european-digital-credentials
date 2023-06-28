package eu.europa.ec.empl.edci.issuer.common.model.customization;

import eu.europa.ec.empl.edci.issuer.common.annotation.CustomizableField;

import java.util.List;

public class CustomizableFieldDTO {

    private Integer position;
    private String labelKey;
    private String fieldPath;
    private Boolean mandatory;
    private String relatesTo;

    private List<String> additionalInfo;

    public CustomizableFieldDTO() {

    }

    public CustomizableFieldDTO(CustomizableField customizableField) {
        this.setPosition(customizableField.position());
        this.setLabelKey(customizableField.labelKey());
        this.setFieldPath(customizableField.fieldPath());
        this.setMandatory(customizableField.mandatory());
        this.setRelatesTo(customizableField.relatesTo());
    }

    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }

    public String getLabelKey() {
        return labelKey;
    }

    public void setLabelKey(String labelKey) {
        this.labelKey = labelKey;
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

    public String getRelatesTo() {
        return relatesTo;
    }

    public void setRelatesTo(String relatesTo) {
        this.relatesTo = relatesTo;
    }

    public List<String> getAdditionalInfo() {
        return additionalInfo;
    }

    public void setAdditionalInfo(List<String> additionalInfo) {
        this.additionalInfo = additionalInfo;
    }
}
