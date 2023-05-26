package eu.europa.ec.empl.edci.issuer.common.model.customization;

import java.util.HashSet;
import java.util.Set;

public class CustomizedRecipientDTO_bad {

    private Set<CustomizedRecipientDTO> entities = new HashSet<>();

    public Set<CustomizedRecipientDTO> getEntities() {
        return entities;
    }

    public void setEntities(Set<CustomizedRecipientDTO> entities) {
        this.entities = entities;
    }
}
