package eu.europa.ec.empl.edci.wallet.entity;

import eu.europa.ec.empl.edci.repository.entity.IGenericDAO;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity(name = "AUX_MIGRATION_LOG")
@Table(name = ConversionLogDAO.TABLE)
@Transactional(propagation = Propagation.REQUIRED)
public class ConversionLogDAO implements IGenericDAO {

    public static final String TABLE = "AUX_MIGRATION_LOG";
    public static final String TABLE_SHORT = "AUX_MIGR_LOG";
    public static final String TABLE_PK_REF = TABLE_SHORT + "_PK";
    public static final String TABLE_SEQ = TABLE + "_SEQ";

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = TABLE_SEQ)
    @SequenceGenerator(sequenceName = TABLE_SEQ, allocationSize = 1, name = TABLE_SEQ)
    @Column(name = "ID")
    private Long pk;

    @Column(name = "USER_ID", nullable = false, length = 50)
    private String walletAddress;

    @Column(name = "WALLET_PK", nullable = false, length = 19)
    private Long walletPk;

    @Column(name = "EMAIL", nullable = false, length = 200)
    private String email;

    @Column(name = "CREDENTIAL_ID", nullable = false, length = 200)
    private String credentialId;

    @Column(name = "CREDENTIAL_PK", nullable = false, length = 19)
    private Long credentialPk;

    @Column(name = "INFORMATION", length = 4000)
    private String info;

    @Column(name = "ERROR_CODE", length = 10)
    private String errorCode;

    @Column(name = "EXECUTION_DATE", nullable = false)
    private Date executionDate = new Date();

    @Column(name = "END_EXECUTION_DATE", nullable = false)
    private Date endDate = new Date();

    public ConversionLogDAO() {

    }

    public ConversionLogDAO(Long walletPk, String walletAddress, String email, Long credentialPk, String credentialId) {
        this.setWalletAddress(walletAddress);
        this.setWalletPk(walletPk);
        this.setEmail(email);
        this.setCredentialId(credentialId);
        this.setCredentialPk(credentialPk);
    }

    @Override
    public Long getPk() {
        return pk;
    }

    @Override
    public void setPk(Long pk) {
        this.pk = pk;
    }

    public String getWalletAddress() {
        return walletAddress;
    }

    public void setWalletAddress(String walletAddress) {
        this.walletAddress = walletAddress;
    }

    public Long getWalletPk() {
        return walletPk;
    }

    public void setWalletPk(Long walletPk) {
        this.walletPk = walletPk;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCredentialId() {
        return credentialId;
    }

    public void setCredentialId(String credentialId) {
        this.credentialId = credentialId;
    }

    public Long getCredentialPk() {
        return credentialPk;
    }

    public void setCredentialPk(Long credentialPk) {
        this.credentialPk = credentialPk;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public Date getExecutionDate() {
        return executionDate;
    }

    public void setExecutionDate(Date executionDate) {
        this.executionDate = executionDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }
}

