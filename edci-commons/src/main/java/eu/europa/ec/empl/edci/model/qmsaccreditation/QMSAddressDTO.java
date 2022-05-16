package eu.europa.ec.empl.edci.model.qmsaccreditation;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.net.URI;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class QMSAddressDTO {

    private URI id;
    private List<QMSIdentifierDTO> identifiers;
    private QMSCodeDTO countryCode;
    private List<QMSLabelDTO> fullAddress;

    public URI getId() {
        return id;
    }

    public void setId(URI id) {
        this.id = id;
    }

    public List<QMSIdentifierDTO> getIdentifiers() {
        return identifiers;
    }

    public void setIdentifiers(List<QMSIdentifierDTO> identifiers) {
        this.identifiers = identifiers;
    }

    public QMSCodeDTO getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(QMSCodeDTO countryCode) {
        this.countryCode = countryCode;
    }

    public List<QMSLabelDTO> getFullAddress() {
        return fullAddress;
    }

    public void setFullAddress(List<QMSLabelDTO> fullAddress) {
        this.fullAddress = fullAddress;
    }


}
