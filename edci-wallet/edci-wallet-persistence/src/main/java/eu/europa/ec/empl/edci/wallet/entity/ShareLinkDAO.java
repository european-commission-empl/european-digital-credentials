package eu.europa.ec.empl.edci.wallet.entity;

import eu.europa.ec.empl.edci.repository.entity.IGenericDAO;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.*;
import java.util.Date;


@Entity(name = "SHARELINK")
@Table(name = "SHARELINK_T")
@Transactional(propagation = Propagation.REQUIRED)
public class ShareLinkDAO implements IGenericDAO {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "SHARELINK_ID")
    private Long pk;
    @Column(name = "CREATIONDATE")
    private Date creationDate = new Date();
    @Column(name = "SHAREHASH", unique = true)
    private String shareHash;
    @Column(name = "EXPIRATIONDATE")
    private Date expirationDate;
    @Column(name = "EXPIRED")
    private boolean expired;
    @ManyToOne
    @JoinColumn(name = "CRED_ID", referencedColumnName = "CRED_ID")
    private CredentialDAO credentialDAO;

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

    public CredentialDAO getCredentialDAO() {
        return credentialDAO;
    }

    public void setCredentialDAO(CredentialDAO credentialDAO) {
        this.credentialDAO = credentialDAO;
    }

    public boolean isExpired() {
        return expired;
    }

    public void setExpired(boolean expired) {
        this.expired = expired;
    }
}
