package eu.europa.ec.empl.edci.datamodel.controlledList;

import java.util.HashMap;
import java.util.Map;

public class RDFConceptScheme {

    public String targetFrameworkUri;
    public String targetNotation;
    public Map<String, String> targetFramework = new HashMap<>(); //lang, content

    public String getTargetFrameworkUri() {
        return targetFrameworkUri;
    }

    public void setTargetFrameworkUri(String targetFrameworkUri) {
        this.targetFrameworkUri = targetFrameworkUri;
    }

    public String getTargetNotation() {
        return targetNotation;
    }

    public void setTargetNotation(String targetNotation) {
        this.targetNotation = targetNotation;
    }

    public Map<String, String> getTargetFramework() {
        return targetFramework;
    }

    public void setTargetFramework(Map<String, String> targetFramework) {
        this.targetFramework = targetFramework;
    }

    public void addTargetFramework(String lang, String name) {
        this.targetFramework.put(lang, name);
    }
}
