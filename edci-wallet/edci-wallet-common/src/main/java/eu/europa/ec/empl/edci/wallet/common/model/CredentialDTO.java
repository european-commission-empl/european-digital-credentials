package eu.europa.ec.empl.edci.wallet.common.model;


import java.util.Date;
import java.util.List;

public class CredentialDTO {
    private Long pk;
    private String uuid;
    private WalletDTO wallet;
    @Deprecated
    private byte[] credential;
    private List<ShareLinkDTO> shareLinkList;
    private List<CredentialLocalizableInfoDTO> credentialLocalizableInfo;
    private String type;
    private String file;
    private Boolean signed;
    private Date signatureExpiryDate;

    public CredentialDTO() {
    }

    public Boolean getSigned() {
        return signed;
    }

    public void setSigned(Boolean signed) {
        this.signed = signed;
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

    @Deprecated
    public byte[] getCredential() {
        return credential;
    }

    @Deprecated
    public void setCredential(byte[] credential) {
        this.credential = credential;
    }

    public WalletDTO getWallet() {
        return wallet;
    }

    public void setWallet(WalletDTO wallet) {
        this.wallet = wallet;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public List<CredentialLocalizableInfoDTO> getCredentialLocalizableInfo() {
        return credentialLocalizableInfo;
    }

    public void setCredentialLocalizableInfo(List<CredentialLocalizableInfoDTO> credentialLocalizableInfo) {
        this.credentialLocalizableInfo = credentialLocalizableInfo;
    }

    public List<ShareLinkDTO> getShareLinkList() {
        return shareLinkList;
    }

    public void setShareLinkList(List<ShareLinkDTO> shareLinkList) {
        this.shareLinkList = shareLinkList;
    }

    public Date getSignatureExpiryDate() {
        return signatureExpiryDate;
    }

    public void setSignatureExpiryDate(Date signatureExpiryDate) {
        this.signatureExpiryDate = signatureExpiryDate;
    }
}
