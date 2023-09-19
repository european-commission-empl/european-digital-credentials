package eu.europa.ec.empl.edci.datamodel.jsonld.model.dataTypes;

import com.fasterxml.jackson.annotation.JsonCreator;
import eu.europa.ec.empl.edci.annotation.EDCIIdentifier;
import eu.europa.ec.empl.edci.datamodel.jsonld.model.base.JsonLdCommonDTO;

import javax.validation.constraints.NotNull;
import java.util.Objects;

@EDCIIdentifier(prefix = "urn:epass:creditPoint:")
public class CreditPointDTO extends JsonLdCommonDTO {

    @NotNull
    private ConceptDTO framework;
    @NotNull
    private String point;

    public CreditPointDTO() {
        super();
    }

    @JsonCreator
    public CreditPointDTO(String uri) {
        super(uri);
    }

    public ConceptDTO getFramework() {
        return framework;
    }

    public void setFramework(ConceptDTO framework) {
        this.framework = framework;
    }

    public String getPoint() {
        return point;
    }

    public void setPoint(String point) {
        this.point = point;
    }

    @NotNull
    public String toString() {
        return point;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CreditPointDTO)) return false;
        if (!super.equals(o)) return false;
        CreditPointDTO that = (CreditPointDTO) o;
        return Objects.equals(framework, that.framework) &&
                Objects.equals(point, that.point);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), framework, point);
    }
}
