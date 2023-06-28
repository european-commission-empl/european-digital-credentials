package eu.europa.ec.empl.edci.model.view.fields;

public class AmountFieldView {
    private Long value;
    private String unit;

    public Long getValue() {
        return value;
    }

    public void setValue(Long value) {
        this.value = value;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }
}
