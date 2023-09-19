package eu.europa.ec.empl.edci.wallet.web.model;

import java.util.List;

public class CredentialView extends CredentialBaseView {
    private String viewerURL;
    private String title;
    private String description;
    private List<String> profile;

    public CredentialView() {
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

    public List<String> getProfile() {
        return profile;
    }

    public void setProfile(List<String> profile) {
        this.profile = profile;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
