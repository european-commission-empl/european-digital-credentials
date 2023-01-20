package eu.europa.ec.empl.edci.wallet.common.model;

import java.util.Date;

public class ShareLinkDTO {
    private Long id;
    private Date creationDate;
    private String shareHash;
    private Date expirationDate;
    private boolean expired;
    private CredentialDTO credentialDTO;

    public ShareLinkDTO() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public boolean isExpired() {
        return expired;
    }

    public void setExpired(boolean expired) {
        this.expired = expired;
    }

    public CredentialDTO getCredentialDTO() {
        return credentialDTO;
    }

    public void setCredentialDTO(CredentialDTO credentialDTO) {
        this.credentialDTO = credentialDTO;
    }

}
