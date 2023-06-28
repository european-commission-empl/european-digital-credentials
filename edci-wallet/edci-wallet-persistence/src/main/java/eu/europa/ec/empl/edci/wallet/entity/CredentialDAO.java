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
    @GeneratedValue(strategy = GenerationType.AUTO, generator = TABLE_SEQ)
    @SequenceGenerator(sequenceName = TABLE_SEQ, allocationSize = 1, name = TABLE_SEQ)
    @Column(name = "CRED_ID")
    private Long pk;

    @Column(name = "uuid")
    private String uuid;

    @ManyToOne
    @JoinColumn(name = "WALLET_ID", referencedColumnName = "ID")
    private WalletDAO wallet;

    @Basic(fetch=FetchType.LAZY)
    @Column(name = "CREDENTIAL_XML")
    @Lob()
    private byte[] credential;

    @Column(name = "CREATE_DATE")
    private Date createDate = new Date();

    @Column(name = "SIGN_EXPIRY_DATE")
    private Date signatureExpiryDate;

    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL, targetEntity = ShareLinkDAO.class, mappedBy = "credential")
    private List<ShareLinkDAO> shareLinkList;

    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL, targetEntity = CredentialLocalizableInfoDAO.class)
    @JoinColumn(name = "CRED_ID")
    private List<CredentialLocalizableInfoDAO> credentialLocalizableInfo;

    @Column(name = "type", nullable = false)
    private String type = EDCIWalletConstants.CREDENTIAL_STORED_TYPE_EUROPASS_CREDENTIAL;

    @Column(name = "FILE_NAME")
    private String file;

    @Column(name = "SIGNED")
    private Boolean signed;

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

    public byte[] getCredential() {
        return credential;
    }

    public void setCredential(byte[] credential) {
        this.credential = credential;
    }

    public WalletDAO getWallet() {
        return wallet;
    }

    public void setWallet(WalletDAO wallet) {
        this.wallet = wallet;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public List<ShareLinkDAO> getShareLinkList() {
        return shareLinkList;
    }

    public void setShareLinkList(List<ShareLinkDAO> shareLinkList) {
        this.shareLinkList = shareLinkList;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public List<CredentialLocalizableInfoDAO> getCredentialLocalizableInfo() {
        return credentialLocalizableInfo;
    }

    public void setCredentialLocalizableInfo(List<CredentialLocalizableInfoDAO> credentialLocalizableInfo) {
        this.credentialLocalizableInfo = credentialLocalizableInfo;
    }

    public Date getSignatureExpiryDate() {
        return signatureExpiryDate;
    }

    public void setSignatureExpiryDate(Date signatureExpiryDate) {
        this.signatureExpiryDate = signatureExpiryDate;
    }

    public Boolean getSigned() {
        return signed;
    }

    public void setSigned(Boolean signed) {
        this.signed = signed;
    }
}
