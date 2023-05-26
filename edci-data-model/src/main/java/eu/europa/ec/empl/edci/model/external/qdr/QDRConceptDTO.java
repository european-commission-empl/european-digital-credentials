package eu.europa.ec.empl.edci.model.external.qdr;

import eu.europa.ec.empl.edci.annotation.EDCIIdentifier;

import java.util.List;
import java.util.Objects;

@EDCIIdentifier(prefix = "urn:epass:concept:")
public class QDRConceptDTO extends QDRJsonLdCommonDTO {

    private List<QDRConceptSchemeDTO> inScheme;
    private String prefLabel;
    private String notation;


    public List<QDRConceptSchemeDTO> getInScheme() {
        return inScheme;
    }

    public void setInScheme(List<QDRConceptSchemeDTO> inScheme) {
        this.inScheme = inScheme;
    }

    public void setPrefLabel(String prefLabel) {
        this.prefLabel = prefLabel;
    }

    public String getPrefLabel() {
        return prefLabel;
    }

    public String getNotation() {
        return notation;
    }

    public void setNotation(String notation) {
        this.notation = notation;
    }

    @Override
    public String toString() {
        return prefLabel != null ? prefLabel.toString() : this.getUri() != null ? this.getUri().toString() : "_blank";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof QDRConceptDTO)) return false;
        if (!super.equals(o)) return false;
        QDRConceptDTO that = (QDRConceptDTO) o;
        return Objects.equals(inScheme, that.inScheme) &&
                Objects.equals(prefLabel, that.prefLabel) &&
                Objects.equals(notation, that.notation);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), inScheme, prefLabel, notation);
    }
}
