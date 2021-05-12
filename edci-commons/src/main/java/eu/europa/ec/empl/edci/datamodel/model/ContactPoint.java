package eu.europa.ec.empl.edci.datamodel.model;

import eu.europa.ec.empl.edci.datamodel.model.dataTypes.Note;

import javax.validation.Valid;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {"note", "description", "postalAddress", "phone", "email", "walletAddress", "contactForm"})
public class ContactPoint {
    @Valid
    private List<Note> note;
    @Valid
    private Note description;
    @Valid
    @XmlElement(name = "address")
    private List<AddressDTO> postalAddress;
    @Valid
    private List<PhoneDTO> phone;
    @Valid
    @XmlElement(name = "mailBox")
    private List<MailboxDTO> email;
    @Valid
    @XmlElement(name = "wallet")
    private List<String> walletAddress;
    @Valid
    @XmlElement(name = "webresource")
    private List<InteractiveWebResourceDTO> contactForm;

    public List<Note> getNote() {
        return note;
    }

    public void setNote(List<Note> note) {
        this.note = note;
    }

    public Note getDescription() {
        return description;
    }

    public void setDescription(Note description) {
        this.description = description;
    }

    public List<AddressDTO> getPostalAddress() {
        return postalAddress;
    }

    public void setPostalAddress(List<AddressDTO> postalAddress) {
        this.postalAddress = postalAddress;
    }

    public List<PhoneDTO> getPhone() {
        return phone;
    }

    public void setPhone(List<PhoneDTO> phone) {
        this.phone = phone;
    }

    public List<MailboxDTO> getEmail() {
        return email;
    }

    public void setEmail(List<MailboxDTO> email) {
        this.email = email;
    }

    public List<String> getWalletAddress() {
        return walletAddress;
    }

    public void setWalletAddress(List<String> walletAddress) {
        this.walletAddress = walletAddress;
    }

    public List<InteractiveWebResourceDTO> getContactForm() {
        return contactForm;
    }

    public void setContactForm(List<InteractiveWebResourceDTO> contactForm) {
        this.contactForm = contactForm;
    }
}
