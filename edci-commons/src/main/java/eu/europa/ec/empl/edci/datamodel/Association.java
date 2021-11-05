package eu.europa.ec.empl.edci.datamodel;

public class Association {

    private String srcClass;
    private Integer srcId;
    private String srcFieldAssociation;
    private String destClass;
    private Integer destId;


    public Association(String srcClass, Integer srcId, String srcFieldAssociation, String destClass, Integer destId) {
        this.srcClass = srcClass;
        this.srcId = srcId;
        this.srcFieldAssociation = srcFieldAssociation;
        this.destClass = destClass;
        this.destId = destId;
    }

    public String getSrcClass() {
        return srcClass;
    }

    public void setSrcClass(String srcClass) {
        this.srcClass = srcClass;
    }

    public Integer getSrcId() {
        return srcId;
    }

    public void setSrcId(Integer srcId) {
        this.srcId = srcId;
    }

    public String getSrcFieldAssociation() {
        return srcFieldAssociation;
    }

    public void setSrcFieldAssociation(String srcFieldAssociation) {
        this.srcFieldAssociation = srcFieldAssociation;
    }

    public String getDestClass() {
        return destClass;
    }

    public void setDestClass(String destClass) {
        this.destClass = destClass;
    }

    public Integer getDestId() {
        return destId;
    }

    public void setDestId(Integer destId) {
        this.destId = destId;
    }

}
