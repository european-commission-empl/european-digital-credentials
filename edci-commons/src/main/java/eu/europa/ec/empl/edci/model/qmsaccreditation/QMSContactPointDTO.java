package eu.europa.ec.empl.edci.model.qmsaccreditation;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class QMSContactPointDTO {
    private List<QMSLabelDTO> notes;
    private List<QMSLabelDTO> descriptions;
    private List<QMSAddressDTO> addresses;
    private List<QMSPhoneDTO> phones;
    private List<QMSMailBoxDTO> mailBoxes;
    private List<QMSWebDocumentDTO> webResources;

    public List<QMSLabelDTO> getNotes() {
        return notes;
    }

    public void setNotes(List<QMSLabelDTO> notes) {
        this.notes = notes;
    }

    public List<QMSLabelDTO> getDescriptions() {
        return descriptions;
    }

    public void setDescriptions(List<QMSLabelDTO> descriptions) {
        this.descriptions = descriptions;
    }

    public List<QMSAddressDTO> getAddresses() {
        return addresses;
    }

    public void setAddresses(List<QMSAddressDTO> addresses) {
        this.addresses = addresses;
    }

    public List<QMSPhoneDTO> getPhones() {
        return phones;
    }

    public void setPhones(List<QMSPhoneDTO> phones) {
        this.phones = phones;
    }

    public List<QMSMailBoxDTO> getMailBoxes() {
        return mailBoxes;
    }

    public void setMailBoxes(List<QMSMailBoxDTO> mailBoxes) {
        this.mailBoxes = mailBoxes;
    }

    public List<QMSWebDocumentDTO> getWebResources() {
        return webResources;
    }

    public void setWebResources(List<QMSWebDocumentDTO> webResources) {
        this.webResources = webResources;
    }
}
