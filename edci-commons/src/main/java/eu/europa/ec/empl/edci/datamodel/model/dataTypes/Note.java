package eu.europa.ec.empl.edci.datamodel.model.dataTypes;

import eu.europa.ec.empl.edci.constants.EDCIMessageKeys;
import eu.europa.ec.empl.edci.datamodel.model.base.Localizable;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

@XmlRootElement(name = "note")
@XmlAccessorType(XmlAccessType.FIELD)
public class Note implements Localizable {

    public Note() {
        this.contents = new ArrayList<>();
    }

    @XmlElement(name = "text")
    @Valid
    @NotNull(message = EDCIMessageKeys.Validation.VALIDATION_NOTE_CONTENT_MIN)
    @Size(min = 1, message = EDCIMessageKeys.Validation.VALIDATION_NOTE_CONTENT_MIN)
    private List<Content> contents;
    @XmlElement(name = "subject")
    private String topic;

    public Note(String content, String language) {
        List<Content> contents = new ArrayList<>();
        contents.add(new Content(content, language));
        this.setContents(contents);
    }

    @Override
    public List<Content> getContents() {
        return contents;
    }

    public void setContents(List<Content> contents) {
        this.contents = contents;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    //XML Getters

    public String getSubject() {
        return this.topic;
    }

    @Override
    public String toString() {
        return this.getStringContent();
    }

}