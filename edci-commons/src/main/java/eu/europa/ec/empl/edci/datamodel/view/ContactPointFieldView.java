package eu.europa.ec.empl.edci.datamodel.view;

import java.util.List;

public class ContactPointFieldView {

    private List<AddressFieldView> address;
    private List<String> phone;
    private List<String> email;
    private List<String> walletAddress;

    public ContactPointFieldView() {

    }

    public List<AddressFieldView> getAddress() {
        return address;
    }

    public void setAddress(List<AddressFieldView> address) {
        this.address = address;
    }

    public List<String> getPhone() {
        return phone;
    }

    public void setPhone(List<String> phone) {
        this.phone = phone;
    }

    public List<String> getEmail() {
        return email;
    }

    public void setEmail(List<String> email) {
        this.email = email;
    }

    public List<String> getWalletAddress() {
        return walletAddress;
    }

    public void setWalletAddress(List<String> walletAddress) {
        this.walletAddress = walletAddress;
    }
}
