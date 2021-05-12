package eu.europa.ec.empl.edci.issuer.entity.dataContainers;

import eu.europa.ec.empl.edci.repository.entity.IGenericDAO;

import javax.persistence.*;


@Entity(name = ProofDCDAO.TABLE)
@Table(name = ProofDCDAO.TABLE)
public class ProofDCDAO implements IGenericDAO {

    public static final String TABLE = "DC_PROOF";
    public static final String TABLE_SHORT = "DC_PROOF";
    public static final String TABLE_PK_REF = TABLE_SHORT + "_PK";
    public static final String TABLE_SEQ = TABLE + "_SEQ";

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = TABLE_SEQ)
    @SequenceGenerator(sequenceName = TABLE_SEQ, allocationSize = 1, name = TABLE_SEQ)
    @Column(name = "PK")
    private Long pk;

    public ProofDCDAO() {

    }

    public Long getPk() {
        return pk;
    }

    public void setPk(Long pk) {
        this.pk = pk;
    }

}