package eu.europa.ec.empl.edci.model.view.fields;

import java.util.ArrayList;
import java.util.List;

public class ContactPointFieldView {

    private List<AddressFieldView> address;
    private List<PhoneFieldView> phone;
    private List<MailboxFieldView> emailAddress;
    private List<LinkFieldView> contactForm = new ArrayList<>();

    public ContactPointFieldView() {

    }

    public List<AddressFieldView> getAddress() {
        return address;
    }

    public void setAddress(List<AddressFieldView> address) {
        this.address = address;
    }

    public List<PhoneFieldView> getPhone() {
        return phone;
    }

    public void setPhone(List<PhoneFieldView> phone) {
        this.phone = phone;
    }

    public List<MailboxFieldView> getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(List<MailboxFieldView> emailAddress) {
        this.emailAddress = emailAddress;
    }

    public List<LinkFieldView> getContactForm() {
        return contactForm;
    }

    public void setContactForm(List<LinkFieldView> contactForm) {
        this.contactForm = contactForm;
    }
    
}
