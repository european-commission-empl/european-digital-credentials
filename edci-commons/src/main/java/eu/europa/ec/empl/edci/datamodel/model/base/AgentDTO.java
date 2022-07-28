package eu.europa.ec.empl.edci.datamodel.model.base;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import eu.europa.ec.empl.edci.constants.EDCIMessageKeys;
import eu.europa.ec.empl.edci.datamodel.model.ContactPoint;
import eu.europa.ec.empl.edci.datamodel.model.OrganizationDTO;
import eu.europa.ec.empl.edci.datamodel.model.PersonDTO;
import eu.europa.ec.empl.edci.datamodel.model.dataTypes.Code;
import eu.europa.ec.empl.edci.datamodel.model.dataTypes.Identifier;
import eu.europa.ec.empl.edci.datamodel.model.dataTypes.Note;
import org.eclipse.persistence.oxm.annotations.XmlIDExtension;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.*;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlSeeAlso({OrganizationDTO.class, PersonDTO.class})
@XmlType(propOrder = {"identifier", "type", "note", "contactPoint"})
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY)
@JsonSubTypes(
        {
                @JsonSubTypes.Type(value = OrganizationDTO.class, name = "OrganizationDTO"),
                @JsonSubTypes.Type(value = PersonDTO.class, name = "PersonDTO")
        }
)
public abstract class AgentDTO implements Nameable, RootEntity {
    @XmlTransient
    private String pk;
    @XmlID
    @XmlIDExtension
    @XmlAttribute
    @NotNull(message = EDCIMessageKeys.Validation.VALIDATION_AGENT_ID_NOTNULL)
    private URI id; //1
    @Valid
    private List<Identifier> identifier; //*
    @Valid
    private List<Code> type; //*
    @XmlElement(name = "additionalNote")
    @Valid
    private List<Note> note = new ArrayList<>(); //*
    @Valid
    private List<ContactPoint> contactPoint; //*

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

    public List<Code> getType() {
        return type;
    }

    public void setType(List<Code> type) {
        this.type = type;
    }

    public List<Note> getNote() {
        return note;
    }

    public void setNote(List<Note> note) {
        this.note = note;
    }

    public List<ContactPoint> getContactPoint() {
        return contactPoint;
    }

    public void setContactPoint(List<ContactPoint> contactPoint) {
        this.contactPoint = contactPoint;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getHashCodeSeed());
    }

    @Override
    public void setHashCodeSeed(String pk) {
        this.pk = pk;
    }

    public String getHashCodeSeed() {
        return pk;
    }

    //Template XML Getters

    public List<Note> getAdditionalNote() {
        return this.note;
    }

    public List<Note> getAdditionalNotes() {
        return this.note;
    }

    public List<Identifier> getAllAvailableIdentifiers() {
        return this.getIdentifier();
    }
}