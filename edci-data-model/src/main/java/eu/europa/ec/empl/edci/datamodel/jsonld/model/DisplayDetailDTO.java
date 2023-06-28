package eu.europa.ec.empl.edci.datamodel.jsonld.model;

import eu.europa.ec.empl.edci.annotation.EDCIIdentifier;
import eu.europa.ec.empl.edci.datamodel.jsonld.model.base.JsonLdCommonDTO;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.util.Objects;

@EDCIIdentifier(prefix = "urn:epass:displayDetail:")
public class DisplayDetailDTO extends JsonLdCommonDTO {
    @NotNull
    private MediaObjectDTO image;
    @Positive
    private Integer page;

    public MediaObjectDTO getImage() {
        return image;
    }

    public void setImage(MediaObjectDTO image) {
        this.image = image;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DisplayDetailDTO)) return false;
        if (!super.equals(o)) return false;
        DisplayDetailDTO that = (DisplayDetailDTO) o;
        return Objects.equals(image, that.image) &&
                Objects.equals(page, that.page);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), image, page);
    }
}
