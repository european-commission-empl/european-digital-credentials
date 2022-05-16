package eu.europa.ec.empl.edci.datamodel.model.dataTypes;

import eu.europa.ec.empl.edci.datamodel.model.base.Localizable;

import javax.validation.Valid;
import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

@XmlRootElement(name = "text", namespace = "")
@XmlType(namespace = "")
@XmlAccessorType(XmlAccessType.FIELD)
public class Text implements Localizable {

    public Text() {
        this.contents = new ArrayList<>();
    }

    @XmlElement(name = "text")
    @Valid
    private List<Content> contents;

    public Text(String contents, String lang) {
        this.contents = new ArrayList<>();
        this.contents.add(new Content(contents, lang));
    }

    public void addContent(String contents, String lang) {
        this.contents.add(new Content(contents, lang));
    }

    @Override
    public List<Content> getContents() {
        return contents;
    }

    public void setContents(List<Content> contents) {
        this.contents = contents;
    }

    @Override
    public String toString() {
        return getStringContent();
    }
}