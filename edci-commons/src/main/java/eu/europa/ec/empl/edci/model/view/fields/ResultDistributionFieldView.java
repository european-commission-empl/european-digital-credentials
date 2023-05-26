package eu.europa.ec.empl.edci.model.view.fields;

import java.util.List;

public class ResultDistributionFieldView {

    private List<ResultCategoryFieldView> resultCategory;
    private String description;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<ResultCategoryFieldView> getResultCategory() {
        return resultCategory;
    }

    public void setResultCategory(List<ResultCategoryFieldView> resultCategory) {
        this.resultCategory = resultCategory;
    }
}