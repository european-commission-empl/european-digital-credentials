package eu.europa.ec.empl.edci.datamodel.model.verifiable.presentation;

import eu.europa.ec.empl.edci.annotation.EDCIIdentifier;
import eu.europa.ec.empl.edci.constants.EDCIMessageKeys;
import eu.europa.ec.empl.edci.datamodel.model.base.Nameable;
import eu.europa.ec.empl.edci.datamodel.model.base.RootEntity;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlTransient;
import java.net.URI;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlTransient
@EDCIIdentifier(prefix = "urn:verifiable:")
public abstract class VerifiablePresentationDTO implements RootEntity, Nameable {

    @NotNull(message = EDCIMessageKeys.Validation.VALIDATION_CREDENTIAL_ID_NOTNULL)
    @XmlAttribute
    private URI id;

    public URI getId() {
        return id;
    }

    public void setId(URI id) {
        this.id = id;
    }
}
