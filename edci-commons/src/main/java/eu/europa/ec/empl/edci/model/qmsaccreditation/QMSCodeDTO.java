package eu.europa.ec.empl.edci.model.qmsaccreditation;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class QMSCodeDTO {

    private String uri;
    private List<QMSLabelDTO> targetName;
    private List<QMSLabelDTO> targetDescription;
    private String targetFrameworkURI;
    private String targetNotation;
    private List<QMSLabelDTO> targetFramework;

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public List<QMSLabelDTO> getTargetName() {
        return targetName;
    }

    public void setTargetName(List<QMSLabelDTO> targetName) {
        this.targetName = targetName;
    }

    public List<QMSLabelDTO> getTargetDescription() {
        return targetDescription;
    }

    public void setTargetDescription(List<QMSLabelDTO> targetDescription) {
        this.targetDescription = targetDescription;
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

    public List<QMSLabelDTO> getTargetFramework() {
        return targetFramework;
    }

    public void setTargetFramework(List<QMSLabelDTO> targetFramework) {
        this.targetFramework = targetFramework;
    }
}
