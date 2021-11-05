package eu.europa.ec.empl.edci.datamodel.model;

import eu.europa.ec.empl.edci.constants.EDCIMessageKeys;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAttribute;
import java.net.URI;

public class MailboxDTO {

    @XmlAttribute(name = "uri")
    @NotNull(message = EDCIMessageKeys.Validation.VALIDATION_MAILBOX_ID_NOTNULL)
    // @Email(message = Message.VALIDATION_MAILBOX_ID_EMAILFORMAT)
    private URI id; //1

    public URI getId() {
        return id;
    }

    public void setId(URI id) {
        this.id = id;
    }
}