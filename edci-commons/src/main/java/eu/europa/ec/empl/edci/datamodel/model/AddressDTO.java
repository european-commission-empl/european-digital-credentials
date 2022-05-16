package eu.europa.ec.empl.edci.datamodel.model;

import eu.europa.ec.empl.edci.constants.EDCIMessageKeys;
import eu.europa.ec.empl.edci.datamodel.model.base.Nameable;
import eu.europa.ec.empl.edci.datamodel.model.dataTypes.Code;
import eu.europa.ec.empl.edci.datamodel.model.dataTypes.Identifier;
import eu.europa.ec.empl.edci.datamodel.model.dataTypes.Note;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.net.URI;
import java.util.List;

@XmlType(propOrder = {"id", "identifier", "fullAddress", "countryCode"})
@XmlAccessorType(XmlAccessType.FIELD)
public class AddressDTO implements Nameable {

    private URI id; //0..1
    @Valid
    private List<Identifier> identifier; //*
    @Valid
    private Note fullAddress; //0..1
    @XmlElement(name = "country")
    @Valid
    @NotNull(message = EDCIMessageKeys.Validation.VALIDATION_ADDRESS_COUNTRYCODE_NOTNULL)
    private Code countryCode; //1

    @Override
    public String getIdentifiableName() {
        return this.getIdentifiableNameFromFieldList(this, "fullAddress", "identifier", "id", "countryCode");
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

    public Note getFullAddress() {
        return fullAddress;
    }

    public void setFullAddress(Note fullAddress) {
        this.fullAddress = fullAddress;
    }

    public Code getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(Code countryCode) {
        this.countryCode = countryCode;
    }

    //XML Getters
    public Code getCountry() {
        return this.countryCode;
    }
}