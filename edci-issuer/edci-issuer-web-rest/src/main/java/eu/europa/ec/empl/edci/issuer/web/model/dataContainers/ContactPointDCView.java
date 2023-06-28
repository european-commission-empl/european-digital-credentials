package eu.europa.ec.empl.edci.issuer.web.model.dataContainers;

import eu.europa.ec.empl.edci.issuer.web.model.dataTypes.NoteDTView;

import java.util.List;

public class ContactPointDCView extends DataContainerView {

    private List<NoteDTView> note;

    private NoteDTView description;

    private List<AddressDCView> address;

    private List<PhoneDCView> phone;

    private List<MailboxDCView> emailAddress;

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

    public List<AddressDCView> getAddress() {
        return address;
    }

    public void setAddress(List<AddressDCView> address) {
        this.address = address;
    }

    public List<PhoneDCView> getPhone() {
        return phone;
    }

    public void setPhone(List<PhoneDCView> phone) {
        this.phone = phone;
    }

    public List<MailboxDCView> getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(List<MailboxDCView> emailAddress) {
        this.emailAddress = emailAddress;
    }

    public List<WebDocumentDCView> getContactForm() {
        return contactForm;
    }

    public void setContactForm(List<WebDocumentDCView> contactForm) {
        this.contactForm = contactForm;
    }
}
