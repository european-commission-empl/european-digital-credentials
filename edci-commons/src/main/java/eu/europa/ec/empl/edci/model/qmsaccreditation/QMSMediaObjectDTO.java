package eu.europa.ec.empl.edci.model.qmsaccreditation;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.net.URI;

@JsonIgnoreProperties(ignoreUnknown = true)
public class QMSMediaObjectDTO {
    private URI id;
    private QMSCodeDTO contentType;
    private URI contentUrl;

    public URI getId() {
        return id;
    }

    public void setId(URI id) {
        this.id = id;
    }

    public QMSCodeDTO getContentType() {
        return contentType;
    }

    public void setContentType(QMSCodeDTO contentType) {
        this.contentType = contentType;
    }

    public URI getContentUrl() {
        return contentUrl;
    }

    public void setContentUrl(URI contentUrl) {
        this.contentUrl = contentUrl;
    }
}
