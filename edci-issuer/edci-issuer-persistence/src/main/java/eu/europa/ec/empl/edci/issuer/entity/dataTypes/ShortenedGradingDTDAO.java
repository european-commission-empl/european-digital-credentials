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

    @OneToOne(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinColumn(name = "PERCENTAGE_LOWER_PK", referencedColumnName = "PK")
    private ScoreDTDAO percentageLower;

    @OneToOne(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinColumn(name = "PERCENTAGE_EQUAL_PK", referencedColumnName = "PK")
    private ScoreDTDAO percentageEqual;

    @OneToOne(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinColumn(name = "PERCENTAGE_HIGHER_PK", referencedColumnName = "PK")
    private ScoreDTDAO percentageHigher;

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

    public ScoreDTDAO getPercentageLower() {
        return percentageLower;
    }

    public void setPercentageLower(ScoreDTDAO percentageLower) {
        this.percentageLower = percentageLower;
    }

    public ScoreDTDAO getPercentageEqual() {
        return percentageEqual;
    }

    public void setPercentageEqual(ScoreDTDAO percentageEqual) {
        this.percentageEqual = percentageEqual;
    }

    public ScoreDTDAO getPercentageHigher() {
        return percentageHigher;
    }

    public void setPercentageHigher(ScoreDTDAO percentageHigher) {
        this.percentageHigher = percentageHigher;
    }
}