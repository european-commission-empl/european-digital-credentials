package eu.europa.ec.empl.edci.parsers.rdf.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import java.io.Serializable;

@XmlAccessorType(XmlAccessType.FIELD)
public class RDFResource implements Serializable {

    @XmlAttribute(namespace = "http://www.w3.org/1999/02/22-rdf-syntax-ns#", name = "resource")
    private String value;

    public RDFResource() {
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}