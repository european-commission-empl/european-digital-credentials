package eu.europa.ec.empl.edci.wallet.entity;

import eu.europa.ec.empl.edci.repository.entity.IGenericDAO;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.*;
import java.util.Date;

@Entity(name = "AUX_MIGRATION_LOCK")
@Table(name = ConversionLockDAO.TABLE)
public class ConversionLockDAO implements IGenericDAO {

    public static final String TABLE = "AUX_MIGRATION_LOCK";
    public static final String TABLE_SHORT = "AUX_MIGR_LOCK";
    public static final String TABLE_PK_REF = TABLE_SHORT + "_PK";
    public static final String TABLE_SEQ = TABLE + "_SEQ";

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = TABLE_SEQ)
    @SequenceGenerator(sequenceName = TABLE_SEQ, allocationSize = 1, name = TABLE_SEQ)
    @Column(name = "ID")
    private Long pk;

    @Column(name = "EXECUTION_DATE", nullable = false)
    private Date executionDate = new Date();

    public ConversionLockDAO() {

    }

    @Override
    public Long getPk() {
        return pk;
    }

    @Override
    public void setPk(Long pk) {
        this.pk = pk;
    }

    public Date getExecutionDate() {
        return executionDate;
    }

    public void setExecutionDate(Date executionDate) {
        this.executionDate = executionDate;
    }
}

