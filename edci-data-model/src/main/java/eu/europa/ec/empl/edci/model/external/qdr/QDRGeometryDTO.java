package eu.europa.ec.empl.edci.model.external.qdr;

import eu.europa.ec.empl.edci.annotation.EDCIIdentifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@EDCIIdentifier(prefix = "urn:epass:geometry:")
public class QDRGeometryDTO extends QDRJsonLdCommonDTO {

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
        if (!(o instanceof QDRGeometryDTO)) return false;
        if (!super.equals(o)) return false;
        QDRGeometryDTO that = (QDRGeometryDTO) o;
        return Objects.equals(latitude, that.latitude) &&
                Objects.equals(longitude, that.longitude);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), latitude, longitude);
    }
}
