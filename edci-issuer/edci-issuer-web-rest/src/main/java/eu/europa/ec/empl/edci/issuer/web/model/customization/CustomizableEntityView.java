package eu.europa.ec.empl.edci.issuer.web.model.customization;

import java.util.Set;

public class CustomizableEntityView {

    private String label;
    private String specClass;
    private Integer position;
    private Set<CustomizableFieldView> fields;
    private Set<CustomizableRelationView> relations;


    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
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

    public Set<CustomizableFieldView> getFields() {
        return fields;
    }

    public void setFields(Set<CustomizableFieldView> fields) {
        this.fields = fields;
    }

    public Set<CustomizableRelationView> getRelations() {
        return relations;
    }

    public void setRelations(Set<CustomizableRelationView> relations) {
        this.relations = relations;
    }
}
