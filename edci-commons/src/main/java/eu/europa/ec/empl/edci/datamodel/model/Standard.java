package eu.europa.ec.empl.edci.datamodel.model;

import eu.europa.ec.empl.edci.datamodel.model.base.Nameable;
import eu.europa.ec.empl.edci.datamodel.model.dataTypes.Identifier;
import eu.europa.ec.empl.edci.datamodel.model.dataTypes.Note;
import eu.europa.ec.empl.edci.datamodel.model.dataTypes.Text;

import javax.validation.Valid;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import java.net.URI;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {"id", "identifier", "title", "description", "supplementaryDocument"})
public class Standard implements Nameable {

    @XmlAttribute
    private URI id;
    @Valid
    private List<Identifier> identifier;
    @Valid
    private Text title;
    @Valid
    private Note description;
    @Valid
    private List<WebDocumentDTO> supplementaryDocument;

    @Override
    public String getIdentifiableName() {
        return this.getIdentifiableNameFromFieldList(this, "title", "identifier", "description", "id");
    }

    public URI getId() {
        return id;
    }

    public void setId(URI id) {
        this.id = id;
    }

    public List<Identifier> getIdentifier() {
        return identifier;
    }

    public void setIdentifier(List<Identifier> identifier) {
        this.identifier = identifier;
    }

    public Text getTitle() {
        return title;
    }

    public void setTitle(Text title) {
        this.title = title;
    }

    public Note getDescription() {
        return description;
    }

    public void setDescription(Note description) {
        this.description = description;
    }

    public List<WebDocumentDTO> getSupplementaryDocument() {
        return supplementaryDocument;
    }

    public void setSupplementaryDocument(List<WebDocumentDTO> supplementaryDocument) {
        this.supplementaryDocument = supplementaryDocument;
    }
}