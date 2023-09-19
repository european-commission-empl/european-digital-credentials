package eu.europa.ec.empl.edci.issuer.entity.dataTypes;

import eu.europa.ec.empl.edci.repository.entity.IGenericDAO;

import javax.persistence.*;

@Entity(name = AmountDTDAO.TABLE)
@Table(name = AmountDTDAO.TABLE)
public class AmountDTDAO implements IGenericDAO {

    public static final String TABLE = "DT_AMOUNT";
    public static final String TABLE_SHORT = "DT_AMOUNT";
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

    @Column(name = "CONTENT")
    private Float value; //1

    @OneToOne(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinColumn(name = "UNIT_CODE_PK", referencedColumnName = "PK")
    private CodeDTDAO unit; //1

    public Float getValue() {
        return value;
    }

    public void setValue(Float value) {
        this.value = value;
    }

    public CodeDTDAO getUnit() {
        return unit;
    }

    public void setUnit(CodeDTDAO unit) {
        this.unit = unit;
    }

    public Long getPk() {
        return pk;
    }

    public void setPk(Long pk) {
        this.pk = pk;
    }
}
