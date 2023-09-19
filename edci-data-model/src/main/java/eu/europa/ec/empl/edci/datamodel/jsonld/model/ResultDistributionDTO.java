package eu.europa.ec.empl.edci.datamodel.jsonld.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import eu.europa.ec.empl.edci.annotation.EDCIIdentifier;
import eu.europa.ec.empl.edci.datamodel.jsonld.model.base.JsonLdCommonDTO;
import eu.europa.ec.empl.edci.datamodel.jsonld.model.dataTypes.LiteralMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@EDCIIdentifier(prefix = "urn:epass:resultDistribution:")
public class ResultDistributionDTO extends JsonLdCommonDTO {

    private LiteralMap description;
    private List<ResultCategoryDTO> resultCategory = new ArrayList<>();

    public ResultDistributionDTO() {
        super();
    }

    @JsonCreator
    public ResultDistributionDTO(String uri) {
        super(uri);
    }

    public LiteralMap getDescription() {
        return description;
    }

    public void setDescription(LiteralMap description) {
        this.description = description;
    }

    public List<ResultCategoryDTO> getResultCategory() {
        return resultCategory;
    }

    public void setResultCategory(List<ResultCategoryDTO> resultCategory) {
        this.resultCategory = resultCategory;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ResultDistributionDTO)) return false;
        if (!super.equals(o)) return false;
        ResultDistributionDTO that = (ResultDistributionDTO) o;
        return Objects.equals(description, that.description) &&
                Objects.equals(resultCategory, that.resultCategory);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), description, resultCategory);
    }
}
