package eu.europa.ec.empl.edci.issuer.web.model.dataTypes;

public class ShortenedGradingDTView extends DataTypeView {

    private Long percentageLower;

    private Long percentageEqual;

    private Long percentageHigher;

    public Long getPercentageLower() {
        return percentageLower;
    }

    public void setPercentageLower(Long percentageLower) {
        this.percentageLower = percentageLower;
    }

    public Long getPercentageEqual() {
        return percentageEqual;
    }

    public void setPercentageEqual(Long percentageEqual) {
        this.percentageEqual = percentageEqual;
    }

    public Long getPercentageHigher() {
        return percentageHigher;
    }

    public void setPercentageHigher(Long percentageHigher) {
        this.percentageHigher = percentageHigher;
    }
}