package eu.europa.ec.empl.edci.model.qmsaccreditation;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.net.URI;

@JsonIgnoreProperties(ignoreUnknown = true)
public class QMSMailBoxDTO {
    private URI uri;

    public URI getUri() {
        return uri;
    }

    public void setUri(URI uri) {
        this.uri = uri;
    }
}
