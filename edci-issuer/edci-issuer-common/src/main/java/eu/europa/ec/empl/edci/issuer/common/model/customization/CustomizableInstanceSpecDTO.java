package eu.europa.ec.empl.edci.issuer.common.model.customization;

import java.util.HashSet;
import java.util.Set;

public class CustomizableInstanceSpecDTO {

    private Set<CustomizableInstanceDTO> customizableInstanceDTOS = new HashSet<>();

    public Set<CustomizableInstanceDTO> getCustomizableInstanceDTOS() {
        return customizableInstanceDTOS;
    }

    public void setCustomizableInstanceDTOS(Set<CustomizableInstanceDTO> customizableInstanceDTOS) {
        this.customizableInstanceDTOS = customizableInstanceDTOS;
    }
}

