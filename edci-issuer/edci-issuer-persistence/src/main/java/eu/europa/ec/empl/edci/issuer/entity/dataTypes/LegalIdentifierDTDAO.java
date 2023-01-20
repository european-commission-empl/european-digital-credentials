package eu.europa.ec.empl.edci.issuer.entity.dataTypes;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity(name = LegalIdentifierDTDAO.TABLE_LI)
@DiscriminatorValue(value = LegalIdentifierDTDAO.TABLE_LI)
public class LegalIdentifierDTDAO extends IdentifierDTDAO {

    public static final String TABLE_LI = "DT_LEGAL_IDENTIFIER";

    @Column(name = "SPATIAL_ID") //, nullable = false
    private String spatialId; //1

    public String getSpatialId() {
        return spatialId;
    }

    public void setSpatialId(String spatialId) {
        this.spatialId = spatialId;
    }

}