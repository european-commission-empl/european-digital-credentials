package eu.europa.ec.empl.edci.datamodel.jsonld.model;

import eu.europa.ec.empl.edci.annotation.EDCIIdentifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@EDCIIdentifier(prefix = "urn:epass:presentation:")
public class EuropeanDigitalPresentationDTO extends VerifiablePresentationDTO {

    private List<VerificationCheckDTO> verificationCheck = new ArrayList<>();

    public List<VerificationCheckDTO> getVerificationCheck() {
        return verificationCheck;
    }

    public void setVerificationCheck(List<VerificationCheckDTO> verificationCheck) {
        this.verificationCheck = verificationCheck;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EuropeanDigitalPresentationDTO)) return false;
        if (!super.equals(o)) return false;
        EuropeanDigitalPresentationDTO that = (EuropeanDigitalPresentationDTO) o;
        return Objects.equals(verificationCheck, that.verificationCheck);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), verificationCheck);
    }
}
