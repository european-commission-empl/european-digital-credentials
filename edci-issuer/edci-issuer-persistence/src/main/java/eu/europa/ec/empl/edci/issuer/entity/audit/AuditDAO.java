package eu.europa.ec.empl.edci.issuer.entity.audit;

import eu.europa.ec.empl.edci.repository.entity.IAuditDAO;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.*;
import java.util.Date;

/**
 * The type Global history dao.
 */
@Entity(name = AuditDAO.TABLE)
@Table(name = AuditDAO.TABLE)
@Transactional(propagation = Propagation.REQUIRED)
public class AuditDAO implements IAuditDAO {

    public static final String TABLE = "AUX_AUDIT";
    public static final String TABLE_SHORT = "AUX_AUDIT";
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

    @Column(name = "CREATE_DATE", nullable = false, updatable = false)
    private Date createDate;

    @Column(name = "UPDATE_DATE", nullable = false)
    private Date updateDate;

    @Column(name = "CREATE_USER", length = 255, nullable = false, updatable = false)
    private String createUserId;

    @Column(name = "UPDATE_USER", length = 255, nullable = false)
    private String updateUserId;

    public AuditDAO() {

    }

    public Long getPk() {
        return pk;
    }

    public void setPk(Long pk) {
        this.pk = pk;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    public String getCreateUserId() {
        return createUserId;
    }

    public void setCreateUserId(String createUserId) {
        this.createUserId = createUserId;
    }

    public String getUpdateUserId() {
        return updateUserId;
    }

    public void setUpdateUserId(String updateUserId) {
        this.updateUserId = updateUserId;
    }
}

