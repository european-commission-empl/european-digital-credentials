package eu.europa.ec.empl.edci.issuer.common.model.customization;

public enum FieldType {

    TEXT("Text"),
    TEXT_AREA("TextArea"),
    DATE("Date"),
    CONTROLLED_LIST("ControlledList");

    private String textValue;

    FieldType(String textValue) {
        this.textValue = textValue;
    }

    public String getTextValue() {
        return this.textValue;
    }
}
