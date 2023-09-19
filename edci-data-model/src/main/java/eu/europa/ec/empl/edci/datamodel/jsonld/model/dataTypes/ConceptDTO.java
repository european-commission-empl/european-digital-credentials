package eu.europa.ec.empl.edci.datamodel.jsonld.model.dataTypes;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSetter;
import eu.europa.ec.empl.edci.annotation.EDCIIdentifier;
import eu.europa.ec.empl.edci.datamodel.jsonld.model.base.JsonLdCommonDTO;

import java.util.Objects;

@EDCIIdentifier(prefix = "urn:epass:concept:")
public class ConceptDTO extends JsonLdCommonDTO {

    private ConceptSchemeDTO inScheme;
    private LiteralMap prefLabel;
    private String notation;

    public ConceptDTO() {
        super();
    }

    @JsonCreator
    public ConceptDTO(String uri) {
        super(uri);
    }

    public ConceptSchemeDTO getInScheme() {
        return inScheme;
    }

    public void setInScheme(ConceptSchemeDTO inScheme) {
        this.inScheme = inScheme;
    }

    public void setPrefLabel(LiteralMap prefLabel) {
        this.prefLabel = prefLabel;
    }

    public LiteralMap getPrefLabel() {
        return prefLabel;
    }

    public String getNotation() {
        return notation;
    }

    @JsonSetter
    public void setNotation(LiteralDTO literalDTO) {
        this.setNotation(literalDTO.getValue());
    }

    @JsonIgnore
    public void setNotation(String notation) {
        this.notation = notation;
    }

    @Override
    public String toString() {
        return prefLabel != null ? prefLabel.toString() : this.getId() != null ? this.getId().toString() : "_blank";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ConceptDTO)) return false;
        if (!super.equals(o)) return false;
        ConceptDTO that = (ConceptDTO) o;
        return Objects.equals(inScheme, that.inScheme) &&
                Objects.equals(prefLabel, that.prefLabel) &&
                Objects.equals(notation, that.notation);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), inScheme, prefLabel, notation);
    }
}
