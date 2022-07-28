package eu.europa.ec.empl.edci.datamodel.model;

import eu.europa.ec.empl.edci.annotation.EDCIIdentifier;
import eu.europa.ec.empl.edci.datamodel.model.base.Identifiable;
import eu.europa.ec.empl.edci.datamodel.model.dataTypes.Code;
import eu.europa.ec.empl.edci.datamodel.model.dataTypes.Identifier;
import eu.europa.ec.empl.edci.datamodel.model.dataTypes.Note;
import eu.europa.ec.empl.edci.datamodel.model.dataTypes.Text;

import javax.validation.Valid;
import javax.xml.bind.annotation.*;
import java.net.URI;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@EDCIIdentifier(prefix = "urn:epass:location:")
@XmlType(propOrder = {"id", "identifier", "geographicName", "description", "hasAddress", "spatialCode"})
public class LocationDTO implements Identifiable {

    private URI id; //0..1
    @XmlTransient
    private String pk;
    @Valid
    private List<Identifier> identifier; //*
    @Valid
    @XmlElement(name = "name")
    private Text geographicName; //0..1
    @Valid
    private List<Code> spatialCode; //*
    @Valid
    private Note description; //0..1
    @Valid
    private List<AddressDTO> hasAddress; //0..1

    public LocationDTO() {
        this.initIdentifiable();
    }

    @Override
    public String getIdentifiableName() {
        return this.getIdentifiableNameFromFieldList(this, "geographicName", "identifier", "description", "id");
    }

    @Override
    public void setHashCodeSeed(String pk) {
        this.pk = pk;
    }

    @Override
    public String getHashCodeSeed() {
        return pk;
    }

    @Override
    public int hashCode() {
        return this.getHashCode();
    }

    @Override
    public boolean equals(Object object) {
        return this.isEquals(object);
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

    public Text getGeographicName() {
        return geographicName;
    }

    public void setGeographicName(Text geographicName) {
        this.geographicName = geographicName;
    }

    public List<Code> getSpatialCode() {
        return spatialCode;
    }

    public void setSpatialCode(List<Code> spatialCode) {
        this.spatialCode = spatialCode;
    }

    public Note getDescription() {
        return description;
    }

    public void setDescription(Note description) {
        this.description = description;
    }

    public List<AddressDTO> getHasAddress() {
        return hasAddress;
    }

    public void setHasAddress(List<AddressDTO> hasAddress) {
        this.hasAddress = hasAddress;
    }

    //XML Getter
    public Text getName() {
        return this.geographicName;
    }
}