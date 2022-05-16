package eu.europa.ec.empl.edci.datamodel.model;

import eu.europa.ec.empl.edci.constants.EDCIMessageKeys;
import eu.europa.ec.empl.edci.datamodel.model.base.Nameable;
import eu.europa.ec.empl.edci.datamodel.model.dataTypes.Code;
import eu.europa.ec.empl.edci.datamodel.model.dataTypes.Text;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import java.net.URL;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {"id", "title", "language", "subject"})
public class WebDocumentDTO implements Nameable {

    @NotNull(message = EDCIMessageKeys.Validation.VALIDATION_WEBDOCUMENT_ID_NOTNULL)
    @XmlAttribute(name = "uri")
    private URL id; //1
    @Valid
    private Text title; //0..1
    @Valid
    private Code language; //0..1
    @Valid
    private List<Code> subject; //*

    @Override
    public String getIdentifiableName() {
        return this.getIdentifiableNameFromFieldList(this, "title", "language", "id");
    }

    public URL getId() {
        return id;
    }

    public void setId(URL id) {
        this.id = id;
    }

    public Text getTitle() {
        return title;
    }

    public void setTitle(Text title) {
        this.title = title;
    }

    public Code getLanguage() {
        return language;
    }

    public void setLanguage(Code language) {
        this.language = language;
    }

    public List<Code> getSubject() {
        return subject;
    }

    public void setSubject(List<Code> subject) {
        this.subject = subject;
    }

    //XML Getter
    public URL getUri() {
        return this.id;
    }

    @Override
    public String toString() {
        return this.id.toString();
    }
}