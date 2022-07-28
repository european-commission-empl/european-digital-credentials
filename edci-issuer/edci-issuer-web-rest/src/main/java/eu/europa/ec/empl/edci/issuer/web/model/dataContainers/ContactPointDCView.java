package eu.europa.ec.empl.edci.issuer.web.model.dataContainers;

import eu.europa.ec.empl.edci.issuer.web.model.dataTypes.NoteDTView;

import java.util.List;

public class ContactPointDCView extends DataContainerView {

    private List<NoteDTView> note;

    private NoteDTView description;

    private List<AddressDCView> postalAddress;

    private List<PhoneDCView> phone;

    private List<MailboxDCView> email;

    private List<WebDocumentDCView> contactForm;

    public List<NoteDTView> getNote() {
        return note;
    }

    public void setNote(List<NoteDTView> note) {
        this.note = note;
    }

    public NoteDTView getDescription() {
        return description;
    }

    public void setDescription(NoteDTView description) {
        this.description = description;
    }

    public List<AddressDCView> getPostalAddress() {
        return postalAddress;
    }

    public void setPostalAddress(List<AddressDCView> postalAddress) {
        this.postalAddress = postalAddress;
    }

    public List<PhoneDCView> getPhone() {
        return phone;
    }

    public void setPhone(List<PhoneDCView> phone) {
        this.phone = phone;
    }

    public List<MailboxDCView> getEmail() {
        return email;
    }

    public void setEmail(List<MailboxDCView> email) {
        this.email = email;
    }

    public List<WebDocumentDCView> getContactForm() {
        return contactForm;
    }

    public void setContactForm(List<WebDocumentDCView> contactForm) {
        this.contactForm = contactForm;
    }
}
