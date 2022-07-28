package eu.europa.ec.empl.edci.datamodel.model.dataTypes;

public class IdentifiableName {

    private String className;
    private String fieldName;
    private String fieldValue;

    public IdentifiableName() {

    }

    public IdentifiableName(String className, String fieldName, String fieldValue) {
        this.setClassName(className);
        this.setFieldName(fieldName);
        this.setFieldValue(fieldValue);
    }

    public IdentifiableName init() {
        this.setClassName("");
        this.setFieldName("");
        this.setFieldValue("");
        return this;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getFieldValue() {
        return fieldValue;
    }

    public void setFieldValue(String fieldValue) {
        this.fieldValue = fieldValue;
    }

    public boolean isFullyInformed() {
        return this.getClassName() != null && this.getFieldName() != null && this.getFieldValue() != null;
    }
}
