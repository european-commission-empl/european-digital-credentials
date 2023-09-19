package eu.europa.ec.empl.edci.issuer.web.model.specs;

import java.util.List;

public class PublicSealAndSendView {

    private List<String> viewerURL;

    public PublicSealAndSendView() {

    }

    public List<String> getViewerURL() {
        return viewerURL;
    }

    public void setViewerURL(List<String> viewerURL) {
        this.viewerURL = viewerURL;
    }

}
