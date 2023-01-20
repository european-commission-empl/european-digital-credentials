package eu.europa.ec.empl.edci.issuer.web.model.dataTypes;

import java.util.List;

public class ResultDistributionDTView extends DataTypeView {

    private List<ResultCategoryDTView> category;

    private NoteDTView description;

    public List<ResultCategoryDTView> getCategory() {
        return category;
    }

    public void setCategory(List<ResultCategoryDTView> category) {
        this.category = category;
    }

    public NoteDTView getDescription() {
        return description;
    }

    public void setDescription(NoteDTView description) {
        this.description = description;
    }

}