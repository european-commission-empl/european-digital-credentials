package eu.europa.ec.empl.edci.datamodel.model;

import eu.europa.ec.empl.edci.annotation.EDCIIdentifier;
import eu.europa.ec.empl.edci.datamodel.model.base.Identifiable;
import eu.europa.ec.empl.edci.datamodel.model.base.Nameable;
import eu.europa.ec.empl.edci.datamodel.model.dataTypes.Amount;
import eu.europa.ec.empl.edci.datamodel.model.dataTypes.Identifier;
import eu.europa.ec.empl.edci.datamodel.model.dataTypes.Note;
import eu.europa.ec.empl.edci.datamodel.model.dataTypes.Text;

import javax.validation.Valid;
import javax.xml.bind.annotation.*;
import java.net.URI;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {"id", "identifier", "amount", "name", "description", "additionalNote"})
@EDCIIdentifier(prefix = "urn:epass:priceDetails:")
public class PriceDetailsDTO implements Identifiable, Nameable {

    @XmlAttribute
    private URI id; //0..1
    @XmlTransient
    private String pk;
    @Valid
    private List<Identifier> identifier; //*
    private Amount amount; //0..1
    @Valid
    private Text name; //0..1
    @Valid
    private Note description; //0..1
    @Valid
    private List<Note> additionalNote; //*

    public PriceDetailsDTO() {
        this.initIdentifiable();
    }

    @Override
    public String getIdentifiableName() {
        return this.getIdentifiableNameFromFieldList(this, "name", "identifier", "description", "id");
    }

    @Override
    public void setHashCodeSeed(String pk) {
        this.pk = pk;
    }

    @Override
    public int hashCode() {
        return this.getHashCode();
    }

    @Override
    public boolean equals(Object object) {
        return this.isEquals(object);
    }

    @Override
    public String getHashCodeSeed() {
        return pk;
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

    public Amount getAmount() {
        return amount;
    }

    public void setAmount(Amount amount) {
        this.amount = amount;
    }

    public Text getName() {
        return name;
    }

    public void setName(Text name) {
        this.name = name;
    }

    public Note getDescription() {
        return description;
    }

    public void setDescription(Note description) {
        this.description = description;
    }

    public List<Note> getAdditionalNote() {
        return additionalNote;
    }

    public void setAdditionalNote(List<Note> additionalNote) {
        this.additionalNote = additionalNote;
    }
}