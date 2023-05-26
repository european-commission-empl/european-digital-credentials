package eu.europa.ec.empl.edci.datamodel.jsonld.model;

import eu.europa.ec.empl.edci.annotation.EDCIIdentifier;
import eu.europa.ec.empl.edci.datamodel.jsonld.model.base.JsonLdCommonDTO;
import eu.europa.ec.empl.edci.datamodel.jsonld.model.dataTypes.LiteralMap;
import eu.europa.ec.empl.edci.datamodel.jsonld.model.dataTypes.NoteDTO;
import eu.europa.ec.empl.edci.datamodel.jsonld.model.dataTypes.WebResourceDTO;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@EDCIIdentifier(prefix = "urn:epass:contactPoint:")
public class ContactPointDTO extends JsonLdCommonDTO {

    private List<AddressDTO> address = new ArrayList<>();
    private List<WebResourceDTO> contactForm = new ArrayList<>();
    private LiteralMap description;
    @NotNull
    private List<MailboxDTO> emailAddress = new ArrayList<>();
    private List<NoteDTO> additionalNote = new ArrayList<>();
    private List<PhoneDTO> phone = new ArrayList<>();

    public List<AddressDTO> getAddress() {
        return address;
    }

    public List<WebResourceDTO> getContactForm() {
        return contactForm;
    }

    public LiteralMap getDescription() {
        return description;
    }

    public void setDescription(LiteralMap description) {
        this.description = description;
    }

    public List<MailboxDTO> getEmailAddress() {
        return emailAddress;
    }

    public List<NoteDTO> getAdditionalNote() {
        return additionalNote;
    }

    public List<PhoneDTO> getPhone() {
        return phone;
    }

    public void setAddress(List<AddressDTO> address) {
        this.address = address;
    }

    public void setContactForm(List<WebResourceDTO> contactForm) {
        this.contactForm = contactForm;
    }

    public void setEmailAddress(List<MailboxDTO> emailAddress) {
        this.emailAddress = emailAddress;
    }

    public void setAdditionalNote(List<NoteDTO> additionalNote) {
        this.additionalNote = additionalNote;
    }

    public void setPhone(List<PhoneDTO> phone) {
        this.phone = phone;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ContactPointDTO)) return false;
        if (!super.equals(o)) return false;
        ContactPointDTO that = (ContactPointDTO) o;
        return Objects.equals(address, that.address) &&
                Objects.equals(contactForm, that.contactForm) &&
                Objects.equals(description, that.description) &&
                Objects.equals(emailAddress, that.emailAddress) &&
                Objects.equals(additionalNote, that.additionalNote) &&
                Objects.equals(phone, that.phone);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), address, contactForm, description, emailAddress, additionalNote, phone);
    }
}
