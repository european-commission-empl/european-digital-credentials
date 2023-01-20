package eu.europa.ec.empl.edci.issuer.common.model.customization;

import java.util.HashSet;
import java.util.Set;

public class CustomizableSpecDTO {

    private Set<CustomizableEntityDTO> customizableEntityDTOS = new HashSet<>();

    public Set<CustomizableEntityDTO> getCustomizableEntityDTOS() {
        return customizableEntityDTOS;
    }

    public void setCustomizableEntityDTOS(Set<CustomizableEntityDTO> customizableEntityDTOS) {
        this.customizableEntityDTOS = customizableEntityDTOS;
    }
}
