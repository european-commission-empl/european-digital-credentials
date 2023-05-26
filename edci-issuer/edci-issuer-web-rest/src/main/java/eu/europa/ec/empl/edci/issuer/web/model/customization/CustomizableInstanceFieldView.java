package eu.europa.ec.empl.edci.issuer.web.model.customization;

import eu.europa.ec.empl.edci.issuer.common.model.customization.FieldType;
import org.apache.commons.lang3.RandomStringUtils;

import java.util.List;

public class CustomizableInstanceFieldView {

    private String frontId;
    private Integer position;
    private String label;
    private String fieldPath;
    private FieldType fieldType;
    private Boolean mandatory;
    private String toolTip;
    private String validation;
    private String controlledListType;
    private List<String> additionalInfo;

    public CustomizableInstanceFieldView() {
        this.setFrontId(RandomStringUtils.randomAlphanumeric(10));
    }

    public String getFrontId() {
        return frontId;
    }

    public void setFrontId(String frontId) {
        this.frontId = frontId;
    }

    public String getControlledListType() {
        return controlledListType;
    }

    public void setControlledListType(String controlledListType) {
        this.controlledListType = controlledListType;
    }

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

    public FieldType getFieldType() {
        return fieldType;
    }

    public void setFieldType(FieldType fieldType) {
        this.fieldType = fieldType;
    }

    public Boolean getMandatory() {
        return mandatory;
    }

    public void setMandatory(Boolean mandatory) {
        this.mandatory = mandatory;
    }

    public String getToolTip() {
        return toolTip;
    }

    public void setToolTip(String toolTip) {
        this.toolTip = toolTip;
    }

    public String getValidation() {
        return validation;
    }

    public void setValidation(String validation) {
        this.validation = validation;
    }

    public List<String> getAdditionalInfo() {
        return additionalInfo;
    }

    public void setAdditionalInfo(List<String> additionalInfo) {
        this.additionalInfo = additionalInfo;
    }
}
