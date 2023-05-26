package eu.europa.ec.empl.edci.issuer.web.model.dataContainers;

import javax.validation.constraints.NotNull;
import java.net.URI;

public class MailboxDCView extends DataContainerView {

    @NotNull
    private URI id; //1

    public URI getId() {
        return id;
    }

    public void setId(URI id) {
        this.id = id;
    }
}