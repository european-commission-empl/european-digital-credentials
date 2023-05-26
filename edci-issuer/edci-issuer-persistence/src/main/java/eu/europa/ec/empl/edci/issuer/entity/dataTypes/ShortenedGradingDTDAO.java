package eu.europa.ec.empl.edci.issuer.entity.dataTypes;

import eu.europa.ec.empl.edci.repository.entity.IGenericDAO;

import javax.persistence.*;


@Entity(name = ShortenedGradingDTDAO.TABLE)
@Table(name = ShortenedGradingDTDAO.TABLE)
public class ShortenedGradingDTDAO implements IGenericDAO {

    public static final String TABLE = "DT_SHORT_GRADING";
    public static final String TABLE_SHORT = "DT_SHO_GRAD";
    public static final String TABLE_PK_REF = TABLE_SHORT + "_PK";
    public static final String TABLE_SEQ = TABLE + "_SEQ";

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = TABLE_SEQ)
    @SequenceGenerator(sequenceName = TABLE_SEQ, allocationSize = 1, name = TABLE_SEQ)
    @Column(name = "PK")
    private Long pk;

    @Column(name = "PERCENTAGE_LOWER")
    private Long percentageLower;

    @Column(name = "PERCENTAGE_EQUAL")
    private Long percentageEqual;

    @Column(name = "PERCENTAGE_HIGHER")
    private Long percentageHigher;

    public ShortenedGradingDTDAO() {

    }

    @Override
    public Long getPk() {
        return pk;
    }

    @Override
    public void setPk(Long pk) {
        this.pk = pk;
    }

    public Long getPercentageLower() {
        return percentageLower;
    }

    public void setPercentageLower(Long percentageLower) {
        this.percentageLower = percentageLower;
    }

    public Long getPercentageEqual() {
        return percentageEqual;
    }

    public void setPercentageEqual(Long percentageEqual) {
        this.percentageEqual = percentageEqual;
    }

    public Long getPercentageHigher() {
        return percentageHigher;
    }

    public void setPercentageHigher(Long percentageHigher) {
        this.percentageHigher = percentageHigher;
    }
}