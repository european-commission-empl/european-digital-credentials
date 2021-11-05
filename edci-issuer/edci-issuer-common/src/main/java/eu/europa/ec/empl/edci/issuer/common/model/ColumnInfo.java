package eu.europa.ec.empl.edci.issuer.common.model;

public class ColumnInfo {
    private String className;
    private String field;
    private String type;
    //private String Range;
    private String rangeProperty;
    private int rangeRef;
    private String language;

    public ColumnInfo() {

    }

    public ColumnInfo(String className, String field, String type, String rangeProperty, int rangeRef, String language) {
        this.setClassName(className);
        this.setField(field);
        this.setType(type);
        this.setRangeProperty(rangeProperty);
        this.setRangeRef(rangeRef);
        this.setLanguage(language);
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getRangeProperty() {
        return rangeProperty;
    }

    public void setRangeProperty(String rangeProperty) {
        this.rangeProperty = rangeProperty;
    }

    public int getRangeRef() {
        return rangeRef;
    }

    public void setRangeRef(int rangeRef) {
        this.rangeRef = rangeRef;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }
}
