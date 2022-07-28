package eu.europa.ec.empl.edci.wallet.web.model;

import eu.europa.ec.empl.edci.datamodel.view.CredentialBaseView;

public class CredentialView extends CredentialBaseView {
    private String viewerURL;
    private String title;
    private String description;
    private String type;

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
