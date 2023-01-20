package eu.europa.ec.empl.edci.issuer.common.model.customization;

import java.util.HashSet;
import java.util.Set;

public class CustomizedSpecDTO {

    Set<CustomizableEntityDTO> entities = new HashSet<>();

    public Set<CustomizableEntityDTO> getEntities() {
        return entities;
    }

    public void setEntities(Set<CustomizableEntityDTO> entities) {
        this.entities = entities;
    }
}
