package eu.europa.ec.empl.edci.datamodel.model.verifiable.presentation;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

@XmlAccessorType(XmlAccessType.FIELD)
public class URIElementDTO {

    @XmlAttribute
    private String uri;

    public URIElementDTO() {
    }

    public URIElementDTO(String uri) {
        this.uri = uri;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }
}
