package eu.europa.ec.empl.edci.issuer.web.model.dataTypes;

public class LegalIdentifierDTView extends IdentifierDTView {

    private CodeDTView spatialId; //1

    public CodeDTView getSpatialId() {
        return spatialId;
    }

    public void setSpatialId(CodeDTView spatialId) {
        this.spatialId = spatialId;
    }
}