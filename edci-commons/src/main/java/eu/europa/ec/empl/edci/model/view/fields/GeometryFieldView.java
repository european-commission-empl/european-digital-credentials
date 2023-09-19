package eu.europa.ec.empl.edci.model.view.fields;

import java.util.ArrayList;
import java.util.List;

public class GeometryFieldView {
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
}
