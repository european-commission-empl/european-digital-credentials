package eu.europa.ec.empl.edci.issuer.common.model.customization;

public class CustomizedFieldDTO {

    private String fieldPathIdentifier;
    private String value;

    public CustomizedFieldDTO() {
    }

    public CustomizedFieldDTO(String fieldPathIdentifier, String value) {
        this.fieldPathIdentifier = fieldPathIdentifier;
        this.value = value;
    }

    public String getFieldPathIdentifier() {
        return fieldPathIdentifier;
    }

    public void setFieldPathIdentifier(String fieldPathIdentifier) {
        this.fieldPathIdentifier = fieldPathIdentifier;
    }

    public String getValue() {
        return value != null ? value.trim() : value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
