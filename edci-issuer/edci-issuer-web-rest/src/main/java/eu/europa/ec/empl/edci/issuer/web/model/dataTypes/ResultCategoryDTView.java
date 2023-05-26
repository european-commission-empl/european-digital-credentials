package eu.europa.ec.empl.edci.issuer.web.model.dataTypes;

import java.util.List;

public class ResultCategoryDTView extends DataTypeView {

    private List<ResultCategoryDTView> category;

    private String label; //1

    private String score; //0..1

    private String minScore; //0..1

    private String maxScore; //0..1

    private Integer count; //1

    private NoteDTView description;

    public List<ResultCategoryDTView> getCategory() {
        return category;
    }

    public void setCategory(List<ResultCategoryDTView> category) {
        this.category = category;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public String getMinScore() {
        return minScore;
    }

    public void setMinScore(String minScore) {
        this.minScore = minScore;
    }

    public String getMaxScore() {
        return maxScore;
    }

    public void setMaxScore(String maxScore) {
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