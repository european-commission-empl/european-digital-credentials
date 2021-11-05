package eu.europa.ec.empl.edci.issuer.common.model;

public class ELementCLBasicDTO {

    public String label;
    public String description;

    public ELementCLBasicDTO() {
    }

    public ELementCLBasicDTO(String label, String description) {
        this.label = label;
        this.description = description;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
