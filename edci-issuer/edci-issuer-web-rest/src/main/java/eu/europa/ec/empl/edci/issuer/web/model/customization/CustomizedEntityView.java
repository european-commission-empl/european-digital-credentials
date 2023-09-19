package eu.europa.ec.empl.edci.issuer.web.model.customization;

import java.util.HashSet;
import java.util.Set;

public class CustomizedEntityView {

    private Set<CustomizedFieldView> fields = new HashSet<>();
    private Set<CustomizedRelationView> relations = new HashSet<>();

    public Set<CustomizedFieldView> getFields() {
        return fields;
    }

    public void setFields(Set<CustomizedFieldView> fields) {
        this.fields = fields;
    }

    public Set<CustomizedRelationView> getRelations() {
        return relations;
    }

    public void setRelations(Set<CustomizedRelationView> relations) {
        this.relations = relations;
    }
}
