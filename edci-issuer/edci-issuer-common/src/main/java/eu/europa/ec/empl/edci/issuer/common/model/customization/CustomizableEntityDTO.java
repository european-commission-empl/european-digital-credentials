package eu.europa.ec.empl.edci.issuer.common.model.customization;

import eu.europa.ec.empl.edci.issuer.common.annotation.CustomizableEntity;

import java.util.Set;

public class CustomizableEntityDTO {

    private String labelKey;
    private String specClass;
    private Integer position;
    private Set<CustomizableFieldDTO> fields;
    private Set<CustomizableRelationDTO> relations;


    public CustomizableEntityDTO() {

    }

    public CustomizableEntityDTO(CustomizableEntity customizableEntity) {
        this.setLabelKey(customizableEntity.labelKey());
        this.setSpecClass(customizableEntity.specClass().getName());
        this.setPosition(customizableEntity.position());
    }

    public String getLabelKey() {
        return labelKey;
    }

    public void setLabelKey(String labelKey) {
        this.labelKey = labelKey;
    }

    public String getSpecClass() {
        return specClass;
    }

    public void setSpecClass(String specClass) {
        this.specClass = specClass;
    }

    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }

    public Set<CustomizableFieldDTO> getFields() {
        return fields;
    }

    public void setFields(Set<CustomizableFieldDTO> fields) {
        this.fields = fields;
    }

    public Set<CustomizableRelationDTO> getRelations() {
        return relations;
    }

    public void setRelations(Set<CustomizableRelationDTO> relations) {
        this.relations = relations;
    }
}
