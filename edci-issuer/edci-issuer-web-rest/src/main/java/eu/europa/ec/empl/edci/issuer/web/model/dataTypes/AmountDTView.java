package eu.europa.ec.empl.edci.issuer.web.model.dataTypes;

import javax.persistence.Column;

public class AmountDTView extends DataTypeView {

    @Column(name = "CONTENT")
    private Float content; //1

    @Column(name = "UNIT")
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
