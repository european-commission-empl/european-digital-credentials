package eu.europa.ec.empl.edci.datamodel.model.verifiable.presentation;

import eu.europa.ec.empl.edci.constants.EuropassConstants;
import eu.europa.ec.empl.edci.constants.MessageKeys;
import eu.europa.ec.empl.edci.datamodel.model.EuropassCredentialDTO;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import java.net.URI;

@XmlAccessorType(XmlAccessType.FIELD)
@Deprecated
public class VerifiablePresentationCredentialDTO {

    @NotNull(message = MessageKeys.Validation.VALIDATION_CREDENTIAL_ID_NOTNULL)
    @XmlAttribute(namespace = EuropassConstants.NAMESPACE_CRED_URI)
    private URI id; //1

    //@JacksonXmlCData
    private EuropassCredentialDTO europassCredential;

    public URI getId() {
        return id;
    }

    public void setId(URI id) {
        this.id = id;
    }

    public EuropassCredentialDTO getEuropassCredential() {
        return europassCredential;
    }

    public void setEuropassCredential(EuropassCredentialDTO europassCredential) {
        this.europassCredential = europassCredential;
    }


}

