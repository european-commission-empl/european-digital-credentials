package eu.europa.ec.empl.edci.wallet.entity;

import eu.europa.ec.empl.edci.repository.entity.IGenericDAO;

import javax.persistence.*;
import java.util.Date;

@Entity(name = "AUX_EMAIL_LOCK")
@Table(name = EmailLockDAO.TABLE)
public class EmailLockDAO implements IGenericDAO {

    public static final String TABLE = "AUX_EMAIL_LOCK";
    public static final String TABLE_SHORT = "AUX_EMAIL_LOCK";
    public static final String TABLE_PK_REF = TABLE_SHORT + "_PK";
    public static final String TABLE_SEQ = TABLE + "_SEQ";

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = TABLE_SEQ)
    @SequenceGenerator(sequenceName = TABLE_SEQ, allocationSize = 1, name = TABLE_SEQ)
    @Column(name = "ID")
    private Long pk;

    @Column(name = "EXECUTION_DATE", nullable = false)
    private Date executionDate = new Date();

    public EmailLockDAO() {

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

