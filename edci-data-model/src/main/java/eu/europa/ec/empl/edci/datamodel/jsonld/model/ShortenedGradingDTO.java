package eu.europa.ec.empl.edci.datamodel.jsonld.model;

import eu.europa.ec.empl.edci.annotation.EDCIIdentifier;
import eu.europa.ec.empl.edci.datamodel.jsonld.model.base.JsonLdCommonDTO;

import javax.validation.constraints.NotNull;
import java.util.Objects;

@EDCIIdentifier(prefix = "urn:epass:shortenedGrading:")
public class ShortenedGradingDTO extends JsonLdCommonDTO {

    @NotNull
    private Integer percentageEqual;
    @NotNull
    private Integer percentageHigher;
    @NotNull
    private Integer percentageLower;

    public Integer getPercentageEqual() {
        return percentageEqual;
    }

    public void setPercentageEqual(Integer percentageEqual) {
        this.percentageEqual = percentageEqual;
    }

    public Integer getPercentageHigher() {
        return percentageHigher;
    }

    public void setPercentageHigher(Integer percentageHigher) {
        this.percentageHigher = percentageHigher;
    }

    public Integer getPercentageLower() {
        return percentageLower;
    }

    public void setPercentageLower(Integer percentageLower) {
        this.percentageLower = percentageLower;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ShortenedGradingDTO)) return false;
        if (!super.equals(o)) return false;
        ShortenedGradingDTO that = (ShortenedGradingDTO) o;
        return Objects.equals(percentageEqual, that.percentageEqual) &&
                Objects.equals(percentageHigher, that.percentageHigher) &&
                Objects.equals(percentageLower, that.percentageLower);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), percentageEqual, percentageHigher, percentageLower);
    }
}
