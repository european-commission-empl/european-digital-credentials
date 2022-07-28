package eu.europa.ec.empl.edci.issuer.web.model.dataTypes;

public class ShortenedGradingDTView extends DataTypeView {

    private ScoreDTView percentageLower;

    private ScoreDTView percentageEqual;

    private ScoreDTView percentageHigher;

    public ScoreDTView getPercentageLower() {
        return percentageLower;
    }

    public void setPercentageLower(ScoreDTView percentageLower) {
        this.percentageLower = percentageLower;
    }

    public ScoreDTView getPercentageEqual() {
        return percentageEqual;
    }

    public void setPercentageEqual(ScoreDTView percentageEqual) {
        this.percentageEqual = percentageEqual;
    }

    public ScoreDTView getPercentageHigher() {
        return percentageHigher;
    }

    public void setPercentageHigher(ScoreDTView percentageHigher) {
        this.percentageHigher = percentageHigher;
    }
}