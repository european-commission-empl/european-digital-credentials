package eu.europa.ec.empl.edci.issuer.common.model.open;

import java.util.ArrayList;
import java.util.List;

public class PublicSealAndSendDTO {

    private List<String> viewerURL = new ArrayList<>();

    public PublicSealAndSendDTO() {

    }

    public List<String> getViewerURL() {
        return viewerURL;
    }

    public void setViewerURL(List<String> viewerURL) {
        this.viewerURL = viewerURL;
    }

}
