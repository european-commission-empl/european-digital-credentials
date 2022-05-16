package eu.europa.ec.empl.edci.issuer.web.model;

import java.util.ArrayList;
import java.util.List;

public class CredentialDownloadView {

    public List<String> uuid = new ArrayList<>();

    public List<String> getUuid() {
        return uuid;
    }

    public void setUuid(List<String> uuid) {
        this.uuid = uuid;
    }
}