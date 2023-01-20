package eu.europa.ec.empl.edci.model.qmsaccreditation;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class QMSLocationDTO {
    private List<QMSIdentifierDTO> identifiers;
    private List<QMSLabelDTO> names;
    private List<QMSLabelDTO> descriptions;
    private List<QMSAddressDTO> addresses;
    private List<QMSAddressDTO> fullAddress;

    public List<QMSIdentifierDTO> getIdentifiers() {
        return identifiers;
    }

    public void setIdentifiers(List<QMSIdentifierDTO> identifiers) {
        this.identifiers = identifiers;
    }

    public List<QMSLabelDTO> getNames() {
        return names;
    }

    public void setNames(List<QMSLabelDTO> names) {
        this.names = names;
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

    public List<QMSAddressDTO> getFullAddress() {
        return fullAddress;
    }

    public void setFullAddress(List<QMSAddressDTO> fullAddress) {
        this.fullAddress = fullAddress;
    }
}
