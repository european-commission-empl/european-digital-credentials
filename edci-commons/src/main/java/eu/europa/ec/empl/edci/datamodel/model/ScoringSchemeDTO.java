package eu.europa.ec.empl.edci.datamodel.model;

import eu.europa.ec.empl.edci.annotation.EDCIIdentifier;
import eu.europa.ec.empl.edci.constants.EDCIMessageKeys;
import eu.europa.ec.empl.edci.datamodel.model.base.Identifiable;
import eu.europa.ec.empl.edci.datamodel.model.base.Nameable;
import eu.europa.ec.empl.edci.datamodel.model.dataTypes.Identifier;
import eu.europa.ec.empl.edci.datamodel.model.dataTypes.Note;
import eu.europa.ec.empl.edci.datamodel.model.dataTypes.Text;
import org.eclipse.persistence.oxm.annotations.XmlIDExtension;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.*;
import java.net.URI;
import java.util.List;

@XmlRootElement
@EDCIIdentifier(prefix = "urn:epass:scoringschemespec:")
@XmlType(propOrder = {"identifier", "title", "description", "supplementaryDocument"})
@XmlAccessorType(XmlAccessType.FIELD)
public class ScoringSchemeDTO implements Identifiable, Nameable {

    @XmlAttribute
    @XmlID
    @XmlIDExtension
    @NotNull(message = EDCIMessageKeys.Validation.VALIDATION_SCORINGSCHEME_ID_NOTNULL)
    private URI id; //1
    @XmlTransient
    private String pk;
    @Valid
    private List<Identifier> identifier; //*
    @Valid
    private Text title; //0..1
    @Valid
    private Note description; //0..1
    @Valid
    @XmlElement(name = "supplementaryDoc")
    private List<WebDocumentDTO> supplementaryDocument; //*

    public ScoringSchemeDTO() {
        this.initIdentifiable();
    }

    @Override
    public String getIdentifiableName() {
        return this.getIdentifiableNameFromFieldList(this, "title", "identifier", "description", "id");
    }

    @Override
    public URI getId() {
        return id;
    }

    @Override
    public void setId(URI id) {
        this.id = id;
    }

    @Override
    public void setPk(String pk) {
        this.pk = pk;
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

    @Override
    public int hashCode() {
        return this.getHashCode();
    }

    @Override
    public boolean equals(Object object) {
        return this.isEquals(object);
    }


    public String getPk() {
        return pk;
    }
}