package eu.europa.ec.empl.edci.datamodel.view;

import java.util.List;

public class LocationFieldView {

    private String geographicName; //0..1
    private List<String> spatialCode; //*
    private String description; //0..1
    private List<AddressFieldView> address;

    public LocationFieldView() {

    }

    public String getGeographicName() {
        return geographicName;
    }

    public void setGeographicName(String geographicName) {
        this.geographicName = geographicName;
    }

    public List<String> getSpatialCode() {
        return spatialCode;
    }

    public void setSpatialCode(List<String> spatialCode) {
        this.spatialCode = spatialCode;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<AddressFieldView> getAddress() {
        return address;
    }

    public void setAddress(List<AddressFieldView> address) {
        this.address = address;
    }
}