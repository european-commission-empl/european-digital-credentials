package eu.europa.ec.empl.edci.issuer.web.model.dataTypes;

public class LegalIdentifierDTView extends IdentifierDTView {

    private String spatialId; //1

    public String getSpatialId() {
        return spatialId;
    }

    public void setSpatialId(String spatialId) {
        this.spatialId = spatialId;
    }
}