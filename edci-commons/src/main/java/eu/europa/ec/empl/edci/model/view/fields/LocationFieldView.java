package eu.europa.ec.empl.edci.model.view.fields;

import java.util.List;

public class LocationFieldView {

    private List<String> spatialCode; //*
    private List<AddressFieldView> address;

    public LocationFieldView() {

    }

    public List<String> getSpatialCode() {
        return spatialCode;
    }

    public void setSpatialCode(List<String> spatialCode) {
        this.spatialCode = spatialCode;
    }

    public List<AddressFieldView> getAddress() {
        return address;
    }

    public void setAddress(List<AddressFieldView> address) {
        this.address = address;
    }

}