package eu.europa.ec.empl.edci.issuer.utils.ecso;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "className",
        "classId",
        "uri",
        "preferredLabel"
})
public class EscoElementPayload {

    @JsonProperty("uri")
    private String uri;

    @JsonProperty("preferredLabel")
    private Map<String, String> targetName; //targetFramework on Root level

    @JsonProperty("classId")
    private String targetFrameworkURI;

    @JsonProperty("className")
    private String targetNotation;

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public Map<String, String> getTargetName() {
        return targetName;
    }

    public void setTargetName(Map<String, String> targetName) {
        this.targetName = targetName;
    }

    public String getTargetFrameworkURI() {
        return targetFrameworkURI;
    }

    public void setTargetFrameworkURI(String targetFrameworkURI) {
        this.targetFrameworkURI = targetFrameworkURI;
    }

    public String getTargetNotation() {
        return targetNotation;
    }

    public void setTargetNotation(String targetNotation) {
        this.targetNotation = targetNotation;
    }
}