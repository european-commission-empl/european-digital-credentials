package eu.europa.ec.empl.edci.model.view.fields;

public class PhoneFieldView {

    private String areaDialingCode;
    private String countryDialing;
    private String phoneNumber;
    private String dialNumber;

    public String getAreaDialingCode() {
        return areaDialingCode;
    }

    public void setAreaDialingCode(String areaDialingCode) {
        this.areaDialingCode = areaDialingCode;
    }

    public String getCountryDialing() {
        return countryDialing;
    }

    public void setCountryDialing(String countryDialing) {
        this.countryDialing = countryDialing;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getDialNumber() {
        return dialNumber;
    }

    public void setDialNumber(String dialNumber) {
        this.dialNumber = dialNumber;
    }
}
