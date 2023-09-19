package eu.europa.ec.empl.edci.issuer.entity.dataTypes;

import eu.europa.ec.empl.edci.repository.entity.IGenericDAO;

import javax.persistence.*;

@Entity(name = CreditPointDTDAO.TABLE)
@Table(name = CreditPointDTDAO.TABLE)
public class CreditPointDTDAO implements IGenericDAO {

    public static final String TABLE = "DT_CREDIT_POINT";
    public static final String TABLE_SHORT = "DT_CREDIT";
    public static final String TABLE_PK_REF = TABLE_SHORT + "_PK";
    public static final String TABLE_SEQ = TABLE + "_SEQ";

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = TABLE_SEQ)
    @SequenceGenerator(sequenceName = TABLE_SEQ, allocationSize = 1, name = TABLE_SEQ)
    @Column(name = "PK")
    private Long pk;

    /* *************
     *   Fields    *
     ***************/

    @OneToOne(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinColumn(name = "FRAMEWORK_PK", referencedColumnName = "PK")
    private CodeDTDAO framework; //1

    @Column(name = "POINTS", nullable = false)
    private String point; //1

    @Override
    public Long getPk() {
        return pk;
    }

    @Override
    public void setPk(Long pk) {
        this.pk = pk;
    }

    public CodeDTDAO getFramework() {
        return framework;
    }

    public void setFramework(CodeDTDAO framework) {
        this.framework = framework;
    }

    public String getPoint() {
        return point;
    }

    public void setPoint(String point) {
        this.point = point;
    }
}
