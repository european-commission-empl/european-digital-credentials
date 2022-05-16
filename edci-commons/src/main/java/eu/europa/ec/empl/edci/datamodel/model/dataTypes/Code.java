package eu.europa.ec.empl.edci.datamodel.model.dataTypes;

import eu.europa.ec.empl.edci.constants.EDCIMessageKeys;
import eu.europa.ec.empl.edci.datamodel.model.base.Nameable;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.*;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {"targetFrameworkURI", "targetNotation", "uri", "targetName", "targetDescription", "targetFramework"})
public class Code implements Nameable { //See EDCI-751 for any doubts here

    @XmlAttribute
    @NotNull(message = EDCIMessageKeys.Validation.VALIDATION_CODE_URI_NOTNULL)
    private String uri; //1
    @XmlElement(name = "targetName")
    @NotNull(message = EDCIMessageKeys.Validation.VALIDATION_CODE_TARGETNAME_NOTNULL)
    private Text targetName; //1
    @XmlElement(name = "targetDescription")
    private Text targetDescription; //0..1

    @XmlAttribute(name = "targetFrameworkUrl")
    private String targetFrameworkURI; //1
    @XmlAttribute
    private String targetNotation; //0..1
    @XmlElement(name = "targetFrameworkName")
    private Text targetFramework; //0..1

    public Code() {

    }

    public Code(String uri) {
        this.setUri(uri);
    }

    public Code(String uri, Text targetName, Text targetDescription, String targetFrameworkURI, String targetNotation, Text targetFramework) {
        this.setUri(uri);
        this.setTargetName(targetName);
        this.setTargetDescription(targetDescription);
        this.setTargetFrameworkURI(targetFrameworkURI);
        this.setTargetNotation(targetNotation);
        this.setTargetFramework(targetFramework);
    }

    @Override
    public String getIdentifiableName() {
        return this.getIdentifiableNameFromFieldList(this, "targetName", "targetDescription", "uri");
    }

    public Text getTargetName() {
        return targetName;
    }

    public void setTargetName(Text targetName) {
        this.targetName = targetName;
    }

    public Text getTargetDescription() {
        return targetDescription;
    }

    public void setTargetDescription(Text targetDescription) {
        this.targetDescription = targetDescription;
    }

    public String getTargetFrameworkURI() {
        return targetFrameworkURI;
    }

    public void setTargetFrameworkURI(String targetFrameworkURI) {
        this.targetFrameworkURI = targetFrameworkURI;
    }

    public Text getTargetFramework() {
        return targetFramework;
    }

    public void setTargetFramework(Text targetFramework) {
        this.targetFramework = targetFramework;
    }

    public String getTargetNotation() {
        return targetNotation;
    }

    public void setTargetNotation(String targetNotation) {
        this.targetNotation = targetNotation;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    //XML Getters
    public String getTargetFrameworkUrl() {
        return this.targetFrameworkURI;
    }

    public Text getTargetFrameworkName() {
        return this.targetFramework;
    }

    @Override
    public String toString() {
        return this.targetName.getStringContent();
    }
}