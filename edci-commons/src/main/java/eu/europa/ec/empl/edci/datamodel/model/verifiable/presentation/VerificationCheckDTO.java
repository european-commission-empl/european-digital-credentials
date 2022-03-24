package eu.europa.ec.empl.edci.datamodel.model.verifiable.presentation;

import eu.europa.ec.empl.edci.constants.EDCIConstants;
import eu.europa.ec.empl.edci.datamodel.model.dataTypes.Code;
import eu.europa.ec.empl.edci.datamodel.model.dataTypes.Text;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.*;
import java.net.URI;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {"id", "type", "status", "description", "longDescription"})
public class VerificationCheckDTO {

    @XmlAttribute
    private URI id;

    @NotNull
    @XmlElement(namespace = EDCIConstants.NAMESPACE_VP_DEFAULT)
    private Code type;

    @NotNull
    @XmlElement(namespace = EDCIConstants.NAMESPACE_VP_DEFAULT)
    private Code status;

//    @XmlTransient
//    private int statusCode;
//
//    @XmlTransient
//    private Code verificationStep;

    @XmlElement(namespace = EDCIConstants.NAMESPACE_VP_DEFAULT)
    private Text longDescription;

    @XmlElement(namespace = EDCIConstants.NAMESPACE_VP_DEFAULT)
    private Text description;

    public URI getId() {
        return id;
    }

    public void setId(URI id) {
        this.id = id;
    }

    public Code getType() {
        return type;
    }

    public void setType(Code type) {
        this.type = type;
    }

    public Code getStatus() {
        return status;
    }

    public void setStatus(Code status) {
        this.status = status;
    }

    public Text getLongDescription() {
        return longDescription;
    }

    public void setLongDescription(Text longDescription) {
        this.longDescription = longDescription;
    }

    public Text getDescription() {
        return description;
    }

    public void setDescription(Text description) {
        this.description = description;
    }
}
