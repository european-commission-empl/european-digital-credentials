package eu.europa.ec.empl.edci.model.view.fields;

import java.util.List;

public class AddressFieldView {

    private NoteFieldView fullAddress;
    private String countryCode;
    private List<IdentifierFieldView> identifier;

    public AddressFieldView() {

    }

    public AddressFieldView(NoteFieldView fullAddress, String countryCode) {
        this.fullAddress = fullAddress;
        this.countryCode = countryCode;
    }

    public NoteFieldView getFullAddress() {
        return fullAddress;
    }

    public void setFullAddress(NoteFieldView fullAddress) {
        this.fullAddress = fullAddress;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public List<IdentifierFieldView> getIdentifier() {
        return identifier;
    }

    public void setIdentifier(List<IdentifierFieldView> identifier) {
        this.identifier = identifier;
    }
}
