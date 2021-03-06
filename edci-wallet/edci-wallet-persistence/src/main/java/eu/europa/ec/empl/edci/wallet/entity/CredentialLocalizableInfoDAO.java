package eu.europa.ec.empl.edci.wallet.entity;

import eu.europa.ec.empl.edci.repository.entity.IGenericDAO;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.*;


@Entity(name = CredentialLocalizableInfoDAO.TABLE)
@Table(name = CredentialLocalizableInfoDAO.TABLE, indexes = {@Index(name = CredentialLocalizableInfoDAO.INDEX_NAME, columnList = "CRED_ID,LANG")})
@Transactional(propagation = Propagation.REQUIRED)
public class CredentialLocalizableInfoDAO implements IGenericDAO {

    public static final String TABLE = "CREDENTIAL_LOCALIZABLE_INFO";
    public static final String TABLE_SHORT = "CRED_LOCAl_INFO";
    public static final String TABLE_PK_REF = TABLE_SHORT + "_PK";
    public static final String TABLE_SEQ = TABLE + "_SEQ";
    public static final String INDEX_NAME = "IDX_" + TABLE_SHORT;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = TABLE_SEQ)
    @SequenceGenerator(sequenceName = TABLE_SEQ, allocationSize = 1, name = TABLE_SEQ)
    @Column(name = "PK")
    private Long pk;

    @Column(name = "LANG", nullable = false)
    private String lang;

    @Column(name = "CREDENTIAL_TYPE")
    private String credentialType;

    @Column(name = "TITLE")
    private String title;

    @Column(name = "DESCRIPTION", length = 4000)
    private String description;

    @ManyToOne()
    @JoinColumn(name = "CRED_ID", referencedColumnName = "CRED_ID")
    private CredentialDAO credentialDAO;


    public CredentialLocalizableInfoDAO() {
    }

    public Long getPk() {
        return pk;
    }

    public void setPk(Long pk) {
        this.pk = pk;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public String getCredentialType() {
        return credentialType;
    }

    public void setCredentialType(String credentialType) {
        this.credentialType = credentialType;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public CredentialDAO getCredentialDAO() {
        return credentialDAO;
    }

    public void setCredentialDAO(CredentialDAO credentialDAO) {
        this.credentialDAO = credentialDAO;
    }
}
