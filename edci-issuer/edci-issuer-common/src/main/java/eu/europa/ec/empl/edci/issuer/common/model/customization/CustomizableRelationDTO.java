package eu.europa.ec.empl.edci.issuer.common.model.customization;

import eu.europa.ec.empl.edci.issuer.common.annotation.CustomizableRelation;

public class CustomizableRelationDTO {

    private Integer position;
    private String labelKey;
    private String relPath;

    public CustomizableRelationDTO() {

    }

    public CustomizableRelationDTO(CustomizableRelation customizableRelation) {
        this.setPosition(customizableRelation.position());
        this.setLabelKey(customizableRelation.labelKey());
        this.setRelPath(customizableRelation.relPath());
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

    public String getRelPath() {
        return relPath;
    }

    public void setRelPath(String relPath) {
        this.relPath = relPath;
    }
}
