package eu.europa.ec.empl.edci.issuer.web.model.dataTypes;

import java.util.List;

public class ResultCategoryDTView extends DataTypeView {

    private List<ResultCategoryDTView> category;

    private TextDTView label; //1

    private ScoreDTView score; //0..1

    private ScoreDTView minScore; //0..1

    private ScoreDTView maxScore; //0..1

    private Integer count; //1

    private NoteDTView description;

    public List<ResultCategoryDTView> getCategory() {
        return category;
    }

    public void setCategory(List<ResultCategoryDTView> category) {
        this.category = category;
    }

    public TextDTView getLabel() {
        return label;
    }

    public void setLabel(TextDTView label) {
        this.label = label;
    }

    public ScoreDTView getScore() {
        return score;
    }

    public void setScore(ScoreDTView score) {
        this.score = score;
    }

    public ScoreDTView getMinScore() {
        return minScore;
    }

    public void setMinScore(ScoreDTView minScore) {
        this.minScore = minScore;
    }

    public ScoreDTView getMaxScore() {
        return maxScore;
    }

    public void setMaxScore(ScoreDTView maxScore) {
        this.maxScore = maxScore;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public NoteDTView getDescription() {
        return description;
    }

    public void setDescription(NoteDTView description) {
        this.description = description;
    }
}