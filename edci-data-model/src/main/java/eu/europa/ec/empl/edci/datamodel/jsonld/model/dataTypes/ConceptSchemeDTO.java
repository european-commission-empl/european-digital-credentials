package eu.europa.ec.empl.edci.datamodel.jsonld.model.dataTypes;

import eu.europa.ec.empl.edci.annotation.EDCIIdentifier;
import eu.europa.ec.empl.edci.datamodel.jsonld.model.base.JsonLdCommonDTO;

import java.net.URI;
import java.util.Objects;

@EDCIIdentifier(prefix = "urn:epass:conceptScheme:")
public class ConceptSchemeDTO extends JsonLdCommonDTO {


    @Override
    public URI getId() {
        return super.getId();
    }

    @Override
    public void setId(URI id) {
        super.setId(id);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ConceptSchemeDTO)) return false;
        ConceptSchemeDTO that = (ConceptSchemeDTO) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
