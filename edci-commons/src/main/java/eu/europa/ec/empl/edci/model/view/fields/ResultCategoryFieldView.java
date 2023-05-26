package eu.europa.ec.empl.edci.model.view.fields;

public class ResultCategoryFieldView {

    private String label; //1
    private String score; //0..1
    private String maximumScore; //0..1
    private String minimumScore; //0..1
    private String count; //1


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

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

}