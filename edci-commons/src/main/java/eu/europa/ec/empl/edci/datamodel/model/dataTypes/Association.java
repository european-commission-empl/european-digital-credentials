package eu.europa.ec.empl.edci.datamodel.model.dataTypes;

public class Association {
    private String srcClass;
    private int srcId;
    private String srcFieldAssociation;
    private String destClass;
    //private String type;
    private int destId;

    public Association() {

    }

    public Association(String srcClass, int srcId, String srcFieldAssociation, String destClass, int destId) {
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

    public int getSrcId() {
        return srcId;
    }

    public void setSrcId(int srcId) {
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

    public int getDestId() {
        return destId;
    }

    public void setDestId(int destId) {
        this.destId = destId;
    }
}
