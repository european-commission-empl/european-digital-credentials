package eu.europa.ec.empl.edci.issuer.common.model;

public class RelationDTO {
    private String originClassName;
    private String originId;
    private String association;
    private String destClassName;
    private String destId;

    public RelationDTO() {

    }

    public String getOriginClassName() {
        return originClassName;
    }

    public void setOriginClassName(String originClassName) {
        this.originClassName = originClassName;
    }

    public String getOriginId() {
        return originId;
    }

    public void setOriginId(String originId) {
        this.originId = originId;
    }

    public String getAssociation() {
        return association;
    }

    public void setAssociation(String association) {
        this.association = association;
    }

    public String getDestClassName() {
        return destClassName;
    }

    public void setDestClassName(String destClassName) {
        this.destClassName = destClassName;
    }

    public String getDestId() {
        return destId;
    }

    public void setDestId(String destId) {
        this.destId = destId;
    }
}
