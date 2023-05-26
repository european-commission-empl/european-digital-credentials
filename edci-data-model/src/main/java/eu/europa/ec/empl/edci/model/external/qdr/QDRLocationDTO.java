package eu.europa.ec.empl.edci.model.external.qdr;

import eu.europa.ec.empl.edci.annotation.EDCIIdentifier;
import eu.europa.ec.empl.edci.annotation.MandatoryConceptScheme;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@EDCIIdentifier(prefix = "urn:epass:location:")
public class QDRLocationDTO extends QDRJsonLdCommonDTO {

    @NotNull
    private List<QDRAddressDTO> address = new ArrayList<>();
    private String description;
    private String geographicName;
    private List<QDRGeometryDTO> geometry = new ArrayList<>();
    private List<QDRIdentifier> identifier = new ArrayList<>();
    @MandatoryConceptScheme("http://publications.europa.eu/resource/authority/country")
    private List<QDRConceptDTO> spatialCode = new ArrayList<>();


    public void setAddress(List<QDRAddressDTO> address) {
        this.address = address;
    }

    public void setGeometry(List<QDRGeometryDTO> geometry) {
        this.geometry = geometry;
    }

    public void setQDRIdentifier(List<QDRIdentifier> identifier) {
        this.identifier = identifier;
    }

    public void setSpatialCode(List<QDRConceptDTO> spatialCode) {
        this.spatialCode = spatialCode;
    }

    public List<QDRAddressDTO> getAddress() {
        return address;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getGeographicName() {
        return geographicName;
    }

    public void setGeographicName(String geographicName) {
        this.geographicName = geographicName;
    }

    public List<QDRIdentifier> getQDRIdentifier() {
        return identifier;
    }

    public List<QDRConceptDTO> getSpatialCode() {
        return spatialCode;
    }

    public List<QDRGeometryDTO> getGeometry() {
        return geometry;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof QDRLocationDTO)) return false;
        if (!super.equals(o)) return false;
        QDRLocationDTO that = (QDRLocationDTO) o;
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
