package eu.europa.ec.empl.edci.model.external.qdr;

import eu.europa.ec.empl.edci.annotation.EDCIIdentifier;

import java.util.Objects;

@EDCIIdentifier(prefix = "urn:epass:phone:")
public class QDRPhoneDTO extends QDRJsonLdCommonDTO {

    private String areaDialingCode;
    private String countryDialing;
    private String phoneNumber;
    private String dialNumber;

    public String getAreaDialingCode() {
        return areaDialingCode;
    }

    public void setAreaDialingCode(String areaDialingCode) {
        this.areaDialingCode = areaDialingCode;
    }

    public String getCountryDialing() {
        return countryDialing;
    }

    public void setCountryDialing(String countryDialing) {
        this.countryDialing = countryDialing;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getDialNumber() {
        return dialNumber;
    }

    public void setDialNumber(String dialNumber) {
        this.dialNumber = dialNumber;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof QDRPhoneDTO)) return false;
        if (!super.equals(o)) return false;
        QDRPhoneDTO phoneDTO = (QDRPhoneDTO) o;
        return Objects.equals(areaDialingCode, phoneDTO.areaDialingCode) &&
                Objects.equals(countryDialing, phoneDTO.countryDialing) &&
                Objects.equals(phoneNumber, phoneDTO.phoneNumber) &&
                Objects.equals(dialNumber, phoneDTO.dialNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), areaDialingCode, countryDialing, phoneNumber, dialNumber);
    }
}
