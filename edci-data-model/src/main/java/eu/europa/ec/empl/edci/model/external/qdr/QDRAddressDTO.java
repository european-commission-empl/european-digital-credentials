package eu.europa.ec.empl.edci.model.external.qdr;

import eu.europa.ec.empl.edci.annotation.EDCIIdentifier;
import eu.europa.ec.empl.edci.annotation.MandatoryConceptScheme;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@EDCIIdentifier(prefix = "urn:epass:address:")
public class QDRAddressDTO extends QDRJsonLdCommonDTO {

    @NotNull
    @MandatoryConceptScheme("http://publications.europa.eu/resource/authority/country")
    private QDRConceptDTO countryCode;
    private QDRNoteDTO fullAddress;
    private List<QDRIdentifier> identifier = new ArrayList<>();

    public QDRConceptDTO getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(QDRConceptDTO countryCode) {
        this.countryCode = countryCode;
    }

    public QDRNoteDTO getFullAddress() {
        return fullAddress;
    }

    public void setFullAddress(QDRNoteDTO fullAddress) {
        this.fullAddress = fullAddress;
    }

    public List<QDRIdentifier> getQDRIdentifier() {
        return identifier;
    }

    public void setQDRIdentifier(List<QDRIdentifier> identifier) {
        this.identifier = identifier;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof QDRAddressDTO)) return false;
        if (!super.equals(o)) return false;
        QDRAddressDTO that = (QDRAddressDTO) o;
        return Objects.equals(countryCode, that.countryCode) &&
                Objects.equals(fullAddress, that.fullAddress) &&
                Objects.equals(identifier, that.identifier);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), countryCode, fullAddress, identifier);
    }
}
