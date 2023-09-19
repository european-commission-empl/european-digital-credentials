package eu.europa.ec.empl.edci.datamodel.jsonld.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import eu.europa.ec.empl.edci.annotation.EDCIIdentifier;
import eu.europa.ec.empl.edci.datamodel.jsonld.model.base.JsonLdCommonDTO;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.util.Objects;

@EDCIIdentifier(prefix = "urn:epass:resultCategory:")
public class ResultCategoryDTO extends JsonLdCommonDTO {

    @NotNull
    @Positive
    private Integer count;
    @NotNull
    private String label;
    private String maximumScore;
    private String minimumScore;
    private String score;

    public ResultCategoryDTO() {
        super();
    }

    @JsonCreator
    public ResultCategoryDTO(String uri) {
        super(uri);
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getMaximumScore() {
        return maximumScore;
    }

    public void setMaximumScore(String maximumScore) {
        this.maximumScore = maximumScore;
    }

    public String getMinimumScore() {
        return minimumScore;
    }

    public void setMinimumScore(String minimumScore) {
        this.minimumScore = minimumScore;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ResultCategoryDTO)) return false;
        if (!super.equals(o)) return false;
        ResultCategoryDTO that = (ResultCategoryDTO) o;
        return Objects.equals(count, that.count) &&
                Objects.equals(label, that.label) &&
                Objects.equals(maximumScore, that.maximumScore) &&
                Objects.equals(minimumScore, that.minimumScore) &&
                Objects.equals(score, that.score);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), count, label, maximumScore, minimumScore, score);
    }
}
