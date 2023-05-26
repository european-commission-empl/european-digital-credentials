package eu.europa.ec.empl.edci.issuer.entity.dataTypes;


import javax.persistence.*;

@Entity(name = LegalIdentifierDTDAO.TABLE_LI)
@DiscriminatorValue(value = LegalIdentifierDTDAO.TABLE_LI)
public class LegalIdentifierDTDAO extends IdentifierDTDAO {

    public static final String TABLE_LI = "DT_LEGAL_IDENTIFIER";

    @OneToOne(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinColumn(name = "SPATIAL_CODE_ID_PK", referencedColumnName = "PK")
    private CodeDTDAO spatialId; //1

    public CodeDTDAO getSpatialId() {
        return spatialId;
    }

    public void setSpatialId(CodeDTDAO spatialId) {
        this.spatialId = spatialId;
    }
}