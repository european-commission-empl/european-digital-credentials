package eu.europa.ec.empl.edci.issuer.web.model.customization;

public class CustomizedFieldView {

    private String fieldPathIdentifier;
    private String value;

    public String getFieldPathIdentifier() {
        return fieldPathIdentifier;
    }

    public void setFieldPathIdentifier(String fieldPathIdentifier) {
        this.fieldPathIdentifier = fieldPathIdentifier;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
