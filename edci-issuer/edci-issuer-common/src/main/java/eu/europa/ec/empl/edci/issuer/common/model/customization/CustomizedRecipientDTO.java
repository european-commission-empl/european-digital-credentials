package eu.europa.ec.empl.edci.issuer.common.model.customization;

import java.util.HashSet;
import java.util.Set;

public class CustomizedRecipientDTO {

    private Set<CustomizedFieldDTO> fields = new HashSet<>();
    private Set<CustomizedRelationDTO> relations = new HashSet<>();

    public Set<CustomizedFieldDTO> getFields() {
        return fields;
    }

    public void setFields(Set<CustomizedFieldDTO> fields) {
        this.fields = fields;
    }

    public Set<CustomizedRelationDTO> getRelations() {
        return relations;
    }

    public void setRelations(Set<CustomizedRelationDTO> relations) {
        this.relations = relations;
    }

}
