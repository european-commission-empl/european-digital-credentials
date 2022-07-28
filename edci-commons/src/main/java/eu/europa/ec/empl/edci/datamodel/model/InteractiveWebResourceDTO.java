package eu.europa.ec.empl.edci.datamodel.model;

import javax.xml.bind.annotation.XmlAttribute;
import java.net.URL;

public class InteractiveWebResourceDTO {

    @XmlAttribute(name = "uri")
    private URL id; //?

    public URL getId() {
        return id;
    }

    public void setId(URL id) {
        this.id = id;
    }

    //XML Getter
    public URL getUri() {
        return this.id;
    }
}