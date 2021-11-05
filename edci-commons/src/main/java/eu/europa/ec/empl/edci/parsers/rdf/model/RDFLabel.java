package eu.europa.ec.empl.edci.parsers.rdf.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlValue;
import java.io.Serializable;

@XmlAccessorType(XmlAccessType.FIELD)
public class RDFLabel implements Serializable {

    @XmlAttribute(name = "lang",
            namespace = javax.xml.XMLConstants.XML_NS_URI)
    private String lang;

    @XmlValue
    private String name;

    public RDFLabel() {
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}