package eu.europa.ec.empl.edci.model.qmsaccreditation;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.net.URI;

@JsonIgnoreProperties(ignoreUnknown = true)
public class QMSLogoDTO {

    private URI id;
    private QMSCodeDTO contentType;

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
}
