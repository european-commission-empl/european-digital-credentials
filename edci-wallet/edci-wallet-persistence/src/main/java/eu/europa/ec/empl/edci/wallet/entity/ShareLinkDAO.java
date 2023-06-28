package eu.europa.ec.empl.edci.wallet.entity;

import eu.europa.ec.empl.edci.repository.entity.IGenericDAO;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.*;
import java.util.Date;


@Entity(name = "SHARELINK")
@Table(name = ShareLinkDAO.TABLE)
@Transactional(propagation = Propagation.REQUIRED)
public class ShareLinkDAO implements IGenericDAO {

    public static final String TABLE = "SHARELINK_T";
    public static final String TABLE_SHORT = "SHARE";
    public static final String TABLE_PK_REF = TABLE_SHORT + "_PK";
    public static final String TABLE_SEQ = TABLE + "_SEQ";

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = TABLE_SEQ)
    @SequenceGenerator(sequenceName = TABLE_SEQ, allocationSize = 1, name = TABLE_SEQ)
    @Column(name = "SHARELINK_ID")
    private Long pk;
    @Column(name = "CREATIONDATE")
    private Date creationDate = new Date();
    @Column(name = "SHAREHASH", unique = true)
    private String shareHash;
    @Column(name = "EXPIRATIONDATE")
    private Date expirationDate;
    @ManyToOne
    @JoinColumn(name = "CRED_ID", referencedColumnName = "CRED_ID")
    private CredentialDAO credential;

    public ShareLinkDAO() {
    }


    @Override
    public Long getPk() {
        return pk;
    }

    @Override
    public void setPk(Long pk) {
        this.pk = pk;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public String getShareHash() {
        return shareHash;
    }

    public void setShareHash(String shareHash) {
        this.shareHash = shareHash;
    }

    public Date getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(Date expirationDate) {
        this.expirationDate = expirationDate;
    }

    public CredentialDAO getCredential() {
        return credential;
    }

    public void setCredential(CredentialDAO credential) {
        this.credential = credential;
    }

}
