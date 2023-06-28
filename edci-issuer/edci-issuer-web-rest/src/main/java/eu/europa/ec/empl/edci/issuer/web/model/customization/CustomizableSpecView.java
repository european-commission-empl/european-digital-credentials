package eu.europa.ec.empl.edci.issuer.web.model.customization;

import java.util.HashSet;
import java.util.Set;

public class CustomizableSpecView {

    private Set<CustomizableEntityView> customizableEntityViews = new HashSet<>();

    public Set<CustomizableEntityView> getCustomizableEntityViews() {
        return customizableEntityViews;
    }

    public void setCustomizableEntityViews(Set<CustomizableEntityView> customizableEntityViews) {
        this.customizableEntityViews = customizableEntityViews;
    }

}
