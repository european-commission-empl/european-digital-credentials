package eu.europa.ec.empl.edci.issuer.web.model.dataContainers;

public class PhoneDCView extends DataContainerView {

    private String phoneNumber; //0..1

    private String countryDialing; //0..1

    private String areaDialing; //0..1

    private String dialNumber; //0..1

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getCountryDialing() {
        return countryDialing;
    }

    public void setCountryDialing(String countryDialing) {
        this.countryDialing = countryDialing;
    }

    public String getAreaDialing() {
        return areaDialing;
    }

    public void setAreaDialing(String areaDialing) {
        this.areaDialing = areaDialing;
    }

    public String getDialNumber() {
        return dialNumber;
    }

    public void setDialNumber(String dialNumber) {
        this.dialNumber = dialNumber;
    }
}