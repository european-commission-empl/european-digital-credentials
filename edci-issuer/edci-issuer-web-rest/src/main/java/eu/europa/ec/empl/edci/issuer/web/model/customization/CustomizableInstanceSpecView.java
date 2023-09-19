package eu.europa.ec.empl.edci.issuer.web.model.customization;

import java.util.HashSet;
import java.util.Set;

public class CustomizableInstanceSpecView {

    private Set<CustomizableInstanceView> customizableInstanceViews = new HashSet<>();

    public Set<CustomizableInstanceView> getCustomizableInstanceViews() {
        return customizableInstanceViews;
    }

    public void setCustomizableInstanceViews(Set<CustomizableInstanceView> customizableInstanceViews) {
        this.customizableInstanceViews = customizableInstanceViews;
    }
}

