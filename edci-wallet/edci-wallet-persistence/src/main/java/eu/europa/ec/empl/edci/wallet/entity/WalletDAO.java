package eu.europa.ec.empl.edci.wallet.entity;

import eu.europa.ec.empl.edci.repository.entity.IGenericDAO;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity(name = "WALLET")
@Table(name = WalletDAO.TABLE, indexes = @Index(name = "wallet_address_index", columnList = "WALLET_ADDRESS"))
@Transactional(propagation = Propagation.REQUIRED)
public class WalletDAO implements IGenericDAO {

    public static final String TABLE = "WALLET_T";
    public static final String TABLE_SHORT = "WALLET";
    public static final String TABLE_PK_REF = TABLE_SHORT + "_PK";
    public static final String TABLE_SEQ = TABLE + "_SEQ";

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = TABLE_SEQ)
    @SequenceGenerator(sequenceName = TABLE_SEQ, allocationSize = 1, name = TABLE_SEQ)
    @Column(name = "ID")
    private Long pk;

    @Column(name = "USER_ID", nullable = false, unique = true)
    private String userId;

    @Column(name = "USER_EMAIL", nullable = false, unique = true)
    private String userEmail;

    @Column(name = "WALLET_ADDRESS", unique = true)
    private String walletAddress;

    @Column(name = "FOLDER_NAME", length = 20)
    private String folder;

    @Column(name = "TEMP", nullable = false)
    private Boolean temporary = false;

    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL, targetEntity = CredentialDAO.class, mappedBy = "wallet")
    private List<CredentialDAO> credentialDAOList = new ArrayList<CredentialDAO>();

    @Column(name = "CREATE_DATE")
    private Date createDate = new Date();

    public WalletDAO() {

    }

    public String getFolder() {
        return folder;
    }

    public void setFolder(String folder) {
        this.folder = folder;
    }

    @Override
    public Long getPk() {
        return pk;
    }

    @Override
    public void setPk(Long pk) {
        this.pk = pk;
    }

    public Boolean getTemporary() {
        return temporary;
    }

    public void setTemporary(Boolean temporary) {
        this.temporary = temporary;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public List<CredentialDAO> getCredentialDAOList() {
        return credentialDAOList;
    }

    public void setCredentialDAOList(List<CredentialDAO> credentialDAOList) {
        this.credentialDAOList = credentialDAOList;
    }

    public String getWalletAddress() {
        return walletAddress;
    }

    public void setWalletAddress(String walletAddress) {
        this.walletAddress = walletAddress;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }
}

