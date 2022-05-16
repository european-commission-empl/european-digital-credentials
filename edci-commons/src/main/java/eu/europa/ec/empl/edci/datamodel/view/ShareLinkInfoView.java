package eu.europa.ec.empl.edci.datamodel.view;

public class ShareLinkInfoView extends ShareLinkView {

    private String shareHash;
    private String creationDate;
    private Boolean expired;

    public ShareLinkInfoView() {
    }

    public String getShareHash() {
        return shareHash;
    }

    public void setShareHash(String shareHash) {
        this.shareHash = shareHash;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }

    public Boolean getExpired() {
        return expired;
    }

    public void setExpired(Boolean expired) {
        this.expired = expired;
    }
}
