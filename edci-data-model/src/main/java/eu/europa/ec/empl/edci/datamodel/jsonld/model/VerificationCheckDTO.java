package eu.europa.ec.empl.edci.datamodel.jsonld.model;

import eu.europa.ec.empl.edci.annotation.EDCIIdentifier;
import eu.europa.ec.empl.edci.datamodel.jsonld.model.base.JsonLdCommonDTO;
import eu.europa.ec.empl.edci.datamodel.jsonld.model.dataTypes.ConceptDTO;
import eu.europa.ec.empl.edci.datamodel.jsonld.model.dataTypes.LiteralMap;

import javax.validation.constraints.NotNull;
import java.util.Objects;

@EDCIIdentifier(prefix = "urn:epass:verificationCheck:")
public class VerificationCheckDTO extends JsonLdCommonDTO {

    private LiteralMap description;
    @NotNull
    private ConceptDTO verificationStatus;
    @NotNull
    private EuropeanDigitalCredentialDTO subject;
    @NotNull
    private ConceptDTO dcType;

    public LiteralMap getDescription() {
        return description;
    }

    public void setDescription(LiteralMap description) {
        this.description = description;
    }

    public ConceptDTO getVerificationStatus() {
        return verificationStatus;
    }

    public void setVerificationStatus(ConceptDTO verificationStatus) {
        this.verificationStatus = verificationStatus;
    }

    public EuropeanDigitalCredentialDTO getSubject() {
        return subject;
    }

    public void setSubject(EuropeanDigitalCredentialDTO subject) {
        this.subject = subject;
    }

    public ConceptDTO getDcType() {
        return dcType;
    }

    public void setDcType(ConceptDTO dcType) {
        this.dcType = dcType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof VerificationCheckDTO)) return false;
        if (!super.equals(o)) return false;
        VerificationCheckDTO that = (VerificationCheckDTO) o;
        return Objects.equals(description, that.description) &&
                Objects.equals(verificationStatus, that.verificationStatus) &&
                Objects.equals(subject, that.subject) &&
                Objects.equals(dcType, that.dcType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), description, verificationStatus, subject, dcType);
    }
}
