package eu.europa.ec.empl.edci.issuer.common.model.customization;

import java.util.Set;

public class CustomizableInstanceDTO {

    private String label;
    private Integer position;
    private Integer order;
    private Set<CustomizableInstanceFieldDTO> fields;
    private Set<CustomizableInstanceRelationDTO> relations;


    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }

    public Set<CustomizableInstanceFieldDTO> getFields() {
        return fields;
    }

    public void setFields(Set<CustomizableInstanceFieldDTO> fields) {
        this.fields = fields;
    }

    public Set<CustomizableInstanceRelationDTO> getRelations() {
        return relations;
    }

    public void setRelations(Set<CustomizableInstanceRelationDTO> relations) {
        this.relations = relations;
    }
}
