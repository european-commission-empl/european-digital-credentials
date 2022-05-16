package eu.europa.ec.empl.edci.datamodel.view;

import java.util.List;

public class ResultDistributionFieldView {

    private List<ResultCategoryFieldView> category;
    private String description;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<ResultCategoryFieldView> getCategory() {
        return category;
    }

    public void setCategory(List<ResultCategoryFieldView> category) {
        this.category = category;
    }
}