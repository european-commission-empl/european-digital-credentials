package eu.europa.ec.empl.edci.issuer.web.model.dataTypes;

import java.util.Objects;

public class CodeDTView extends DataTypeView {

    private String uri; //1

    private TextDTView targetName; //1

    private TextDTView targetDescription; //0..1

    private String targetFrameworkURI; //1

    private String targetNotation; //0..1

    private TextDTView targetFramework; //0..1

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public TextDTView getTargetName() {
        return targetName;
    }

    public void setTargetName(TextDTView targetName) {
        this.targetName = targetName;
    }

    public TextDTView getTargetDescription() {
        return targetDescription;
    }

    public void setTargetDescription(TextDTView targetDescription) {
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

    public TextDTView getTargetFramework() {
        return targetFramework;
    }

    public void setTargetFramework(TextDTView targetFramework) {
        this.targetFramework = targetFramework;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CodeDTView that = (CodeDTView) o;
        return Objects.equals(uri, that.uri) && Objects.equals(targetFrameworkURI, that.targetFrameworkURI);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uri, targetFrameworkURI);
    }
}