package eu.europa.ec.empl.edci.datamodel.view;

public class AddressFieldView {

    private String fullAddress;
    private String countryCode;

    public AddressFieldView() {

    }

    public AddressFieldView(String fullAddress, String countryCode) {
        this.fullAddress = fullAddress;
        this.countryCode = countryCode;
    }

    public String getFullAddress() {
        return fullAddress;
    }

    public void setFullAddress(String fullAddress) {
        this.fullAddress = fullAddress;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }
}
