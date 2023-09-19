package eu.europa.ec.empl.edci.issuer.web.model.dataTypes;

public class MeasureDTView extends DataTypeView {

    private Float content; //1

    private String unit; //1

    public Float getContent() {
        return content;
    }

    public void setContent(Float content) {
        this.content = content;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }
}