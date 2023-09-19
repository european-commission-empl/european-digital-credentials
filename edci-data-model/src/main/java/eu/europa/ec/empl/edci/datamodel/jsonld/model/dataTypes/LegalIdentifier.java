package eu.europa.ec.empl.edci.datamodel.jsonld.model.dataTypes;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import eu.europa.ec.empl.edci.annotation.CustomizableCLFieldDTO;
import eu.europa.ec.empl.edci.annotation.EDCIIdentifier;
import eu.europa.ec.empl.edci.constants.ControlledList;

import javax.validation.constraints.NotNull;
import java.util.Objects;

@EDCIIdentifier(prefix = "urn:epass:legalIdentifier:")
@JsonTypeInfo(use = JsonTypeInfo.Id.DEDUCTION, property = "type", visible = true, defaultImpl = LegalIdentifier.class)
public class LegalIdentifier extends Identifier {

    @NotNull
    @CustomizableCLFieldDTO(targetFramework = ControlledList.COUNTRY)
    private ConceptDTO spatial;

    public ConceptDTO getSpatial() {
        return spatial;
    }

    public LegalIdentifier() {
        super();
    }

    @JsonCreator
    public LegalIdentifier(String uri) {
        super(uri);
    }

    public void setSpatial(ConceptDTO spatial) {
        this.spatial = spatial;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LegalIdentifier)) return false;
        if (!super.equals(o)) return false;
        LegalIdentifier that = (LegalIdentifier) o;
        return Objects.equals(spatial, that.spatial);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), spatial);
    }

    @Override
    public String toString() {
        return this.getNotation() + " - " + spatial.toString();
    }
}
