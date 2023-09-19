package eu.europa.ec.empl.edci.datamodel.jsonld.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import eu.europa.ec.empl.edci.annotation.CustomizableCLFieldDTO;
import eu.europa.ec.empl.edci.annotation.EDCIIdentifier;
import eu.europa.ec.empl.edci.constants.ControlledList;
import eu.europa.ec.empl.edci.datamodel.jsonld.model.base.JsonLdCommonDTO;
import eu.europa.ec.empl.edci.datamodel.jsonld.model.dataTypes.ConceptDTO;
import eu.europa.ec.empl.edci.datamodel.jsonld.model.dataTypes.GeometryDTO;
import eu.europa.ec.empl.edci.datamodel.jsonld.model.dataTypes.Identifier;
import eu.europa.ec.empl.edci.datamodel.jsonld.model.dataTypes.LiteralMap;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@EDCIIdentifier(prefix = "urn:epass:location:")
public class LocationDTO extends JsonLdCommonDTO {

    @NotNull
    private List<AddressDTO> address = new ArrayList<>();
    private LiteralMap description;
    private LiteralMap geographicName;
    private List<GeometryDTO> geometry = new ArrayList<>();
    private List<Identifier> identifier = new ArrayList<>();
    @CustomizableCLFieldDTO(targetFramework = ControlledList.ATU)
    private List<ConceptDTO> spatialCode = new ArrayList<>();

    public LocationDTO() {
        super();
    }

    @JsonCreator
    public LocationDTO(String uri) {
        super(uri);
    }


    public void setAddress(List<AddressDTO> address) {
        this.address = address;
    }

    public void setGeometry(List<GeometryDTO> geometry) {
        this.geometry = geometry;
    }

    public void setIdentifier(List<Identifier> identifier) {
        this.identifier = identifier;
    }

    public void setSpatialCode(List<ConceptDTO> spatialCode) {
        this.spatialCode = spatialCode;
    }

    public List<AddressDTO> getAddress() {
        return address;
    }

    public LiteralMap getDescription() {
        return description;
    }

    public void setDescription(LiteralMap description) {
        this.description = description;
    }

    public LiteralMap getGeographicName() {
        return geographicName;
    }

    public void setGeographicName(LiteralMap geographicName) {
        this.geographicName = geographicName;
    }

    public List<Identifier> getIdentifier() {
        return identifier;
    }

    public List<ConceptDTO> getSpatialCode() {
        return spatialCode;
    }

    public List<GeometryDTO> getGeometry() {
        return geometry;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LocationDTO)) return false;
        if (!super.equals(o)) return false;
        LocationDTO that = (LocationDTO) o;
        return Objects.equals(address, that.address) &&
                Objects.equals(description, that.description) &&
                Objects.equals(geographicName, that.geographicName) &&
                Objects.equals(geometry, that.geometry) &&
                Objects.equals(identifier, that.identifier) &&
                Objects.equals(spatialCode, that.spatialCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), address, description, geographicName, geometry, identifier, spatialCode);
    }
}
