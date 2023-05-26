package eu.europa.ec.empl.edci.issuer.web.model.customization;

import org.apache.commons.lang3.RandomStringUtils;

import java.util.Set;

public class CustomizableInstanceView {

    private String frontId;
    private Integer position;
    private Integer order;
    private String label;
    private String entityPK;
    private Set<CustomizableInstanceFieldView> fields;
    private Set<CustomizableInstanceRelationView> relations;

    public CustomizableInstanceView() {
        this.setFrontId(RandomStringUtils.randomAlphanumeric(10));
    }

    public String getFrontId() {
        return frontId;
    }

    public void setFrontId(String frontId) {
        this.frontId = frontId;
    }

    public Integer getPosition() {
        return position;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getEntityPK() {
        return entityPK;
    }

    public void setEntityPK(String entityPK) {
        this.entityPK = entityPK;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }

    public Set<CustomizableInstanceFieldView> getFields() {
        return fields;
    }

    public void setFields(Set<CustomizableInstanceFieldView> fields) {
        this.fields = fields;
    }

    public Set<CustomizableInstanceRelationView> getRelations() {
        return relations;
    }

    public void setRelations(Set<CustomizableInstanceRelationView> relations) {
        this.relations = relations;
    }

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }
}
