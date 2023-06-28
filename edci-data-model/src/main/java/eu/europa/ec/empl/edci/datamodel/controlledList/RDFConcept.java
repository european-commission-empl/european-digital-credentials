package eu.europa.ec.empl.edci.datamodel.controlledList;

import java.util.HashMap;
import java.util.Map;

public class RDFConcept {

    public String targetFrameworkUri;
    public String uri;
    public String externalResource;
    public Map<String, String> targetName = new HashMap<>();

    public String getTargetFrameworkUri() {
        return targetFrameworkUri;
    }

    public void setTargetFrameworkUri(String targetFrameworkUri) {
        this.targetFrameworkUri = targetFrameworkUri;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getExternalResource() {
        return externalResource;
    }

    public void setExternalResource(String externalResource) {
        this.externalResource = externalResource;
    }

    public Map<String, String> getTargetName() {
        return targetName;
    }

    public void setTargetName(Map<String, String> targetName) {
        this.targetName = targetName;
    }

    public void addTargetName(String lang, String name) {
        this.targetName.put(lang, name);
    }
}