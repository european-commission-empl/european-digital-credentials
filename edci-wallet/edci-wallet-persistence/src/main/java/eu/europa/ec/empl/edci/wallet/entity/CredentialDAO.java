package eu.europa.ec.empl.edci.wallet.entity;

import eu.europa.ec.empl.edci.repository.entity.IGenericDAO;
import eu.europa.ec.empl.edci.wallet.common.constants.EDCIWalletConstants;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity(name = "CREDENTIAL")
@Table(name = CredentialDAO.TABLE, uniqueConstraints = {
        @UniqueConstraint(name = "cred_wallet_uq", columnNames = {"uuid", "WALLET_ID"}),
})
@Transactional(propagation = Propagation.REQUIRED)
public class CredentialDAO implements IGenericDAO {

    public static final String TABLE = "CREDENTIAL_T";
    public static final String TABLE_SHORT = "CRED";
    public static final String TABLE_PK_REF = TABLE_SHORT + "_PK";
    public static final String TABLE_SEQ = TABLE + "_SEQ";
    public static final String INDEX_NAME = "IDX_" + TABLE_SHORT;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(sequenceName = TABLE_SEQ, allocationSize = 1, name = TABLE_SEQ)
    @Column(name = "CRED_ID")
    private Long pk;

    @Column(name = "uuid")
    private String uuid;

    @ManyToOne
    @JoinColumn(name = "WALLET_ID", referencedColumnName = "ID")
    private WalletDAO walletDAO;

    @Column(name = "CREDENTIAL_XML")
    @Lob()
    private byte[] credentialXML;

    @Column(name = "CREATE_DATE")
    private Date createDate = new Date();

    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL, targetEntity = ShareLinkDAO.class, mappedBy = "credentialDAO")
    private List<ShareLinkDAO> shareLinkDAOList;

    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL, targetEntity = CredentialLocalizableInfoDAO.class, mappedBy = "credentialDAO")
    private List<CredentialLocalizableInfoDAO> credentialLocalizableInfoDAOS;

    @Column(name = "type", columnDefinition = "VARCHAR(5) default '" + EDCIWalletConstants.CREDENTIAL_STORED_TYPE_EUROPASS_CREDENTIAL + "' not null")
    private String type;

    public CredentialDAO() {
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Long getPk() {
        return pk;
    }

    public void setPk(Long pk) {
        this.pk = pk;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public byte[] getCredentialXML() {
        return credentialXML;
    }

    public void setCredentialXML(byte[] credentialXML) {
        this.credentialXML = credentialXML;
    }

    public WalletDAO getWalletDAO() {
        return walletDAO;
    }

    public void setWalletDAO(WalletDAO walletDAO) {
        this.walletDAO = walletDAO;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public List<ShareLinkDAO> getShareLinkDAOList() {
        return shareLinkDAOList;
    }

    public void setShareLinkDAOList(List<ShareLinkDAO> shareLinkDAOList) {
        this.shareLinkDAOList = shareLinkDAOList;
    }

    public List<CredentialLocalizableInfoDAO> getCredentialLocalizableInfoDAOS() {
        return credentialLocalizableInfoDAOS;
    }

    public void setCredentialLocalizableInfoDAOS(List<CredentialLocalizableInfoDAO> credentialLocalizableInfoDAOS) {
        this.credentialLocalizableInfoDAOS = credentialLocalizableInfoDAOS;
    }
}
