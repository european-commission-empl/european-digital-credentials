package eu.europa.ec.empl.edci.datamodel.jsonld.model.dataTypes;

import com.fasterxml.jackson.annotation.JsonCreator;
import eu.europa.ec.empl.edci.annotation.EDCIIdentifier;
import eu.europa.ec.empl.edci.datamodel.jsonld.model.base.JsonLdCommonDTO;

import java.net.URI;
import java.util.List;
import java.util.Objects;

@EDCIIdentifier(prefix = "urn:epass:conceptScheme:")
public class ConceptSchemeDTO extends JsonLdCommonDTO {

    public ConceptSchemeDTO() {
        super();
    }

    @JsonCreator
    public ConceptSchemeDTO(List<ConceptSchemeDTO> array) {
        this.setId(array.get(0).getId());
    }

    @JsonCreator
    public ConceptSchemeDTO(String uri) {
        super(uri);
    }

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
