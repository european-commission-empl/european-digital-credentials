package eu.europa.ec.empl.edci.datamodel.jsonld.model.dataTypes;

import eu.europa.ec.empl.edci.annotation.EDCIIdentifier;
import eu.europa.ec.empl.edci.datamodel.jsonld.model.base.JsonLdCommonDTO;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@EDCIIdentifier(prefix = "urn:epass:geometry:")
public class GeometryDTO extends JsonLdCommonDTO {

    private List<String> latitude = new ArrayList<>();
    private List<String> longitude = new ArrayList<>();

    public List<String> getLatitude() {
        return latitude;
    }

    public List<String> getLongitude() {
        return longitude;
    }

    public void setLatitude(List<String> latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(List<String> longitude) {
        this.longitude = longitude;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GeometryDTO)) return false;
        if (!super.equals(o)) return false;
        GeometryDTO that = (GeometryDTO) o;
        return Objects.equals(latitude, that.latitude) &&
                Objects.equals(longitude, that.longitude);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), latitude, longitude);
    }
}
