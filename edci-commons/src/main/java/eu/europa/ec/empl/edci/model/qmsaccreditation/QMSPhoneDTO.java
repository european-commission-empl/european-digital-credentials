package eu.europa.ec.empl.edci.model.qmsaccreditation;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class QMSPhoneDTO {

    private String phoneNumber;
    private String countryDialing;
    private String areaDialing;
    private String dialNumber;

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
