package eu.europa.ec.empl.edci.model.external.qdr;

import eu.europa.ec.empl.edci.annotation.EDCIIdentifier;
import eu.europa.ec.empl.edci.annotation.MandatoryConceptScheme;

import javax.validation.constraints.NotNull;
import java.util.Objects;

@EDCIIdentifier(prefix = "urn:epass:legalIdentifier:")
public class QDRLegalIdentifier extends QDRIdentifier {

    @NotNull
    @MandatoryConceptScheme("http://publications.europa.eu/resource/authority/country")
    private QDRConceptDTO spatial;

    public QDRConceptDTO getSpatial() {
        return spatial;
    }

    public void setSpatial(QDRConceptDTO spatial) {
        this.spatial = spatial;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof QDRLegalIdentifier)) return false;
        if (!super.equals(o)) return false;
        QDRLegalIdentifier that = (QDRLegalIdentifier) o;
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
