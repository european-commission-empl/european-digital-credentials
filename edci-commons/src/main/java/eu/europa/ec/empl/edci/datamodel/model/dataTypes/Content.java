package eu.europa.ec.empl.edci.datamodel.model.dataTypes;

import eu.europa.ec.empl.edci.constants.EDCIMessageKeys;
import eu.europa.ec.empl.edci.datamodel.model.base.Nameable;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.*;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "content")
public class Content implements Nameable {

    @XmlValue
    @NotNull(message = EDCIMessageKeys.Validation.VALIDATION_TEXT_CONTENT_NOTNULL)
    private String content; //1
    @XmlAttribute(name = "lang")
    private String language; //0..1
    @XmlAttribute(name = "content-type")
    private String format = "text/plain"; //0..1


    public Content() {
    }

    @Override
    public String getIdentifiableName() {
        return this.getIdentifiableNameFromFieldList(this, "content", "language", "format");
    }

    public Content(String language) {
        this.setLanguage(language);
    }

    public Content(String content, String language) {
        this.setLanguage(language);
        this.setContent(content);
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    //XML Getter
    public String getLang() {
        return this.language;
    }

    public String getContent_type() {
        return this.format;
    }

    @Override
    public String toString() {
        return this.content;
    }
}
