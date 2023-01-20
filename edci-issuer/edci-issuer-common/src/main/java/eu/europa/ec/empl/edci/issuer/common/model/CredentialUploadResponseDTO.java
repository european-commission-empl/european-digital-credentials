package eu.europa.ec.empl.edci.issuer.common.model;

public class CredentialUploadResponseDTO {
    private String uuid;
    private String viewerURL;
    private String title;
    private String description;

    public CredentialUploadResponseDTO() {
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getViewerURL() {
        return viewerURL;
    }

    public void setViewerURL(String viewerURL) {
        this.viewerURL = viewerURL;
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
}
