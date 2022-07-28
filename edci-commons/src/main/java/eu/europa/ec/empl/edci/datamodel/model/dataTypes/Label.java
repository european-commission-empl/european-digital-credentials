package eu.europa.ec.empl.edci.datamodel.model.dataTypes;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

@XmlAccessorType(XmlAccessType.FIELD)
public class Label extends Text {

    public Label() {
        super();
    }

    @XmlAttribute(name = "key")
    private String key;

    public Label(String key, String contents, String lang) {
        super(contents, lang);
        this.key = key;
    }

    public Label(String contents, String lang) {
        super(contents, lang);
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

}