package eu.europa.ec.empl.edci.issuer.entity.dataTypes;

import eu.europa.ec.empl.edci.repository.entity.IGenericDAO;
import org.joda.time.LocalDate;

import javax.persistence.*;

@Entity(name = PeriodOfTimeDTDAO.TABLE)
@Table(name = PeriodOfTimeDTDAO.TABLE)
public class PeriodOfTimeDTDAO implements IGenericDAO {

    public static final String TABLE = "DT_PERIOD_OF_TIME";
    public static final String TABLE_SHORT = "DT_PERIOD";
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
    @JoinColumn(name = "NAME_PK", referencedColumnName = "PK")
    private TextDTDAO prefLabel; //0..*

    @Column(name = "START_DATE")
    private LocalDate startDate; //0..1

    @Column(name = "END_DATE")
    private LocalDate endDate; //0..1

    @Override
    public Long getPk() {
        return pk;
    }

    @Override
    public void setPk(Long pk) {
        this.pk = pk;
    }

    public TextDTDAO getPrefLabel() {
        return prefLabel;
    }

    public void setPrefLabel(TextDTDAO prefLabel) {
        this.prefLabel = prefLabel;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }
}
