package eu.europa.ec.empl.edci.datamodel.view;

public class ShortenedGradingFieldView {

    private String percentageLower; //1
    private String percentageEqual; //1
    private String percentageHigher; //1

    public String getPercentageLower() {
        return percentageLower;
    }

    public void setPercentageLower(String percentageLower) {
        this.percentageLower = percentageLower;
    }

    public String getPercentageEqual() {
        return percentageEqual;
    }

    public void setPercentageEqual(String percentageEqual) {
        this.percentageEqual = percentageEqual;
    }

    public String getPercentageHigher() {
        return percentageHigher;
    }

    public void setPercentageHigher(String percentageHigher) {
        this.percentageHigher = percentageHigher;
    }
}