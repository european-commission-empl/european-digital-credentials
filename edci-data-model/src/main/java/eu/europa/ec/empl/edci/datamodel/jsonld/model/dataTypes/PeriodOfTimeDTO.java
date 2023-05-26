package eu.europa.ec.empl.edci.datamodel.jsonld.model.dataTypes;

import eu.europa.ec.empl.edci.annotation.EDCIIdentifier;
import eu.europa.ec.empl.edci.datamodel.jsonld.model.base.JsonLdCommonDTO;

import java.time.ZonedDateTime;
import java.util.Objects;

@EDCIIdentifier(prefix = "urn:epass:period:")
public class PeriodOfTimeDTO extends JsonLdCommonDTO {

    private ZonedDateTime endDate;
    private LiteralMap prefLabel;
    private ZonedDateTime startDate;

    public ZonedDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(ZonedDateTime endDate) {
        this.endDate = endDate;
    }

    public LiteralMap getPrefLabel() {
        return prefLabel;
    }

    public void setPrefLabel(LiteralMap prefLabel) {
        this.prefLabel = prefLabel;
    }

    public ZonedDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(ZonedDateTime startDate) {
        this.startDate = startDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PeriodOfTimeDTO)) return false;
        if (!super.equals(o)) return false;
        PeriodOfTimeDTO that = (PeriodOfTimeDTO) o;
        return Objects.equals(endDate, that.endDate) &&
                Objects.equals(prefLabel, that.prefLabel) &&
                Objects.equals(startDate, that.startDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), endDate, prefLabel, startDate);
    }
}
