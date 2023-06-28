package eu.europa.ec.empl.edci.datamodel.jsonld.model;

import eu.europa.ec.empl.edci.annotation.EDCIIdentifier;
import eu.europa.ec.empl.edci.datamodel.jsonld.model.base.JsonLdCommonDTO;
import eu.europa.ec.empl.edci.datamodel.jsonld.model.dataTypes.ConceptDTO;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@EDCIIdentifier(prefix = "urn:epass:individualDisplay:")
public class IndividualDisplayDTO extends JsonLdCommonDTO {

    private ConceptDTO language;
    @NotNull
    private List<DisplayDetailDTO> displayDetail = new ArrayList<>();

    public ConceptDTO getLanguage() {
        return language;
    }

    public void setLanguage(ConceptDTO language) {
        this.language = language;
    }

    public List<DisplayDetailDTO> getDisplayDetail() {
        return displayDetail;
    }

    public void setDisplayDetail(List<DisplayDetailDTO> displayDetail) {
        this.displayDetail = displayDetail;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof IndividualDisplayDTO)) return false;
        if (!super.equals(o)) return false;
        IndividualDisplayDTO that = (IndividualDisplayDTO) o;
        return Objects.equals(language, that.language) &&
                Objects.equals(displayDetail, that.displayDetail);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), language, displayDetail);
    }
}
