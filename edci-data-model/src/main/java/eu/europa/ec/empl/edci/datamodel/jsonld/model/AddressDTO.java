package eu.europa.ec.empl.edci.datamodel.jsonld.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import eu.europa.ec.empl.edci.annotation.CustomizableCLFieldDTO;
import eu.europa.ec.empl.edci.annotation.EDCIIdentifier;
import eu.europa.ec.empl.edci.constants.ControlledList;
import eu.europa.ec.empl.edci.datamodel.jsonld.model.base.JsonLdCommonDTO;
import eu.europa.ec.empl.edci.datamodel.jsonld.model.dataTypes.ConceptDTO;
import eu.europa.ec.empl.edci.datamodel.jsonld.model.dataTypes.Identifier;
import eu.europa.ec.empl.edci.datamodel.jsonld.model.dataTypes.NoteDTO;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@EDCIIdentifier(prefix = "urn:epass:address:")
public class AddressDTO extends JsonLdCommonDTO {

    @NotNull
    @CustomizableCLFieldDTO(targetFramework = ControlledList.COUNTRY)
    private ConceptDTO countryCode;
    private NoteDTO fullAddress;
    private List<Identifier> identifier = new ArrayList<>();

    public AddressDTO() {
        super();
    }

    @JsonCreator
    public AddressDTO(String uri) {
        super(uri);
    }

    public ConceptDTO getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(ConceptDTO countryCode) {
        this.countryCode = countryCode;
    }

    public NoteDTO getFullAddress() {
        return fullAddress;
    }

    public void setFullAddress(NoteDTO fullAddress) {
        this.fullAddress = fullAddress;
    }

    public List<Identifier> getIdentifier() {
        return identifier;
    }

    public void setIdentifier(List<Identifier> identifier) {
        this.identifier = identifier;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AddressDTO)) return false;
        if (!super.equals(o)) return false;
        AddressDTO that = (AddressDTO) o;
        return Objects.equals(countryCode, that.countryCode) &&
                Objects.equals(fullAddress, that.fullAddress) &&
                Objects.equals(identifier, that.identifier);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), countryCode, fullAddress, identifier);
    }
}
