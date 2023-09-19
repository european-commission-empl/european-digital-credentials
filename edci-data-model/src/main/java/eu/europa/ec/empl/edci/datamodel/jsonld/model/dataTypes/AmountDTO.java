package eu.europa.ec.empl.edci.datamodel.jsonld.model.dataTypes;

import com.fasterxml.jackson.annotation.JsonCreator;
import eu.europa.ec.empl.edci.annotation.EDCIIdentifier;
import eu.europa.ec.empl.edci.datamodel.jsonld.model.base.JsonLdCommonDTO;

import javax.validation.constraints.NotNull;
import java.util.Objects;

@EDCIIdentifier(prefix = "urn:epass:amount:")
public class AmountDTO extends JsonLdCommonDTO {

    @NotNull
    private Long value;
    @NotNull
    private ConceptDTO unit;

    public AmountDTO() {
        super();
    }

    @JsonCreator
    public AmountDTO(String uri) {
        super(uri);
    }

    public @NotNull Long getValue() {
        return value;
    }

    public void setValue(@NotNull Long value) {
        this.value = value;
    }

    public ConceptDTO getUnit() {
        return unit;
    }

    public void setUnit(ConceptDTO unit) {
        this.unit = unit;
    }

    @Override
    public String toString() {
        return value + " " + unit.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AmountDTO)) return false;
        if (!super.equals(o)) return false;
        AmountDTO amountDTO = (AmountDTO) o;
        return Objects.equals(value, amountDTO.value) &&
                Objects.equals(unit, amountDTO.unit);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), value, unit);
    }
}
