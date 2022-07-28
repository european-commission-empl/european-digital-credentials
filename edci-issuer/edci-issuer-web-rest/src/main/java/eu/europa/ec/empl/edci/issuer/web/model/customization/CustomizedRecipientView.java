package eu.europa.ec.empl.edci.issuer.web.model.customization;

import java.util.HashSet;
import java.util.Set;

public class CustomizedRecipientView {

    private Set<CustomizedEntityView> entities = new HashSet<>();

    public Set<CustomizedEntityView> getEntities() {
        return entities;
    }

    public void setEntities(Set<CustomizedEntityView> entities) {
        this.entities = entities;
    }
}
