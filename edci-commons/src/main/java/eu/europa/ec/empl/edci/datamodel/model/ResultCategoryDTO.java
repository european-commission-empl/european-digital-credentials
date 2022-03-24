package eu.europa.ec.empl.edci.datamodel.model;

import eu.europa.ec.empl.edci.constants.EDCIMessageKeys;
import eu.europa.ec.empl.edci.datamodel.model.base.Nameable;
import eu.europa.ec.empl.edci.datamodel.model.dataTypes.Score;
import eu.europa.ec.empl.edci.datamodel.model.dataTypes.Text;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {"label", "score", "minScore", "maxScore", "count"})
public class ResultCategoryDTO implements Nameable {
    @NotNull(message = EDCIMessageKeys.Validation.VALIDATION_RESULTCATEGORY_LABEL_NOTNULL)
    @Valid
    private Text label; //1
    @Valid
    private Score score; //0..1
    @Valid
    private Score minScore; //0..1
    @Valid
    private Score maxScore; //0..1
    @NotNull(message = EDCIMessageKeys.Validation.VALIDATION_RESULTCATEGORY_COUNT_NOTNULL)
    private Integer count; //1 //TODO  enter positiu

    @Override
    public String getIdentifiableName() {
        return this.getIdentifiableNameFromFieldList(this, "label", "score");
    }

    public Text getLabel() {
        return label;
    }

    public void setLabel(Text label) {
        this.label = label;
    }

    public Score getScore() {
        return score;
    }

    public void setScore(Score score) {
        this.score = score;
    }

    public Score getMinScore() {
        return minScore;
    }

    public void setMinScore(Score minScore) {
        this.minScore = minScore;
    }

    public Score getMaxScore() {
        return maxScore;
    }

    public void setMaxScore(Score maxScore) {
        this.maxScore = maxScore;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }
}