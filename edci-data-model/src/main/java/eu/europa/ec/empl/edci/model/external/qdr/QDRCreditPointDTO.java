package eu.europa.ec.empl.edci.model.external.qdr;

import eu.europa.ec.empl.edci.annotation.EDCIIdentifier;

import javax.validation.constraints.NotNull;
import java.util.Objects;

@EDCIIdentifier(prefix = "urn:epass:creditPoint:")
public class QDRCreditPointDTO extends QDRJsonLdCommonDTO {

    @NotNull
    private QDRConceptDTO framework;
    @NotNull
    private String point;

    public QDRConceptDTO getFramework() {
        return framework;
    }

    public void setFramework(QDRConceptDTO framework) {
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
        if (!(o instanceof QDRCreditPointDTO)) return false;
        if (!super.equals(o)) return false;
        QDRCreditPointDTO that = (QDRCreditPointDTO) o;
        return Objects.equals(framework, that.framework) &&
                Objects.equals(point, that.point);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), framework, point);
    }
}
