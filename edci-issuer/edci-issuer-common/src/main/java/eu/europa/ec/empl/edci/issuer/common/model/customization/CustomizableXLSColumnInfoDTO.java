package eu.europa.ec.empl.edci.issuer.common.model.customization;

import eu.europa.ec.empl.edci.issuer.common.constants.XLS;

public class CustomizableXLSColumnInfoDTO {

    private int columnIndex;
    private String reference;
    private XLS.Recipients.FIELD_TYPE fieldType;

    public CustomizableXLSColumnInfoDTO(int columnIndex, String reference, XLS.Recipients.FIELD_TYPE fieldType) {
        this.columnIndex = columnIndex;
        this.reference = reference;
        this.fieldType = fieldType;
    }

    public int getColumnIndex() {
        return columnIndex;
    }

    public void setColumnIndex(int columnIndex) {
        this.columnIndex = columnIndex;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public XLS.Recipients.FIELD_TYPE getFieldType() {
        return fieldType;
    }

    public void setFieldType(XLS.Recipients.FIELD_TYPE fieldType) {
        this.fieldType = fieldType;
    }
}
