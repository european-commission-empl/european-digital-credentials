package eu.europa.ec.empl.edci.model.external.qdr;

import eu.europa.ec.empl.edci.annotation.EDCIIdentifier;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@EDCIIdentifier(prefix = "urn:epass:contactPoint:")
public class QDRContactPointDTO extends QDRJsonLdCommonDTO {

    private List<QDRAddressDTO> address = new ArrayList<>();
    private List<QDRWebResourceDTO> contactForm = new ArrayList<>();
    private String description;
    @NotNull
    private List<QDRMailboxDTO> mailbox = new ArrayList<>();
    private List<QDRNoteDTO> additionalNote = new ArrayList<>();
    private List<QDRPhoneDTO> phone = new ArrayList<>();

    public List<QDRAddressDTO> getAddress() {
        return address;
    }

    public List<QDRWebResourceDTO> getContactForm() {
        return contactForm;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<QDRMailboxDTO> getMailbox() {
        return mailbox;
    }

    public List<QDRNoteDTO> getAdditionalNote() {
        return additionalNote;
    }

    public List<QDRPhoneDTO> getPhone() {
        return phone;
    }

    public void setAddress(List<QDRAddressDTO> address) {
        this.address = address;
    }

    public void setContactForm(List<QDRWebResourceDTO> contactForm) {
        this.contactForm = contactForm;
    }

    public void setMailbox(List<QDRMailboxDTO> mailbox) {
        this.mailbox = mailbox;
    }

    public void setAdditionalNote(List<QDRNoteDTO> additionalNote) {
        this.additionalNote = additionalNote;
    }

    public void setPhone(List<QDRPhoneDTO> phone) {
        this.phone = phone;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof QDRContactPointDTO)) return false;
        if (!super.equals(o)) return false;
        QDRContactPointDTO that = (QDRContactPointDTO) o;
        return Objects.equals(address, that.address) &&
                Objects.equals(contactForm, that.contactForm) &&
                Objects.equals(description, that.description) &&
                Objects.equals(mailbox, that.mailbox) &&
                Objects.equals(additionalNote, that.additionalNote) &&
                Objects.equals(phone, that.phone);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), address, contactForm, description, mailbox, additionalNote, phone);
    }
}
