package eu.europa.ec.empl.edci.datamodel.model.verifiable.presentation;

import eu.europa.ec.empl.edci.annotation.EDCIIdentifier;
import eu.europa.ec.empl.edci.constants.EDCIConstants;
import eu.europa.ec.empl.edci.constants.EDCIMessageKeys;
import eu.europa.ec.empl.edci.datamodel.adapter.DateTimeAdapter;
import eu.europa.ec.empl.edci.datamodel.model.EuropassCredentialDTO;
import eu.europa.ec.empl.edci.datamodel.model.OrganizationDTO;
import eu.europa.ec.empl.edci.datamodel.model.base.CredentialHolderDTO;
import eu.europa.ec.empl.edci.datamodel.model.dataTypes.Code;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@XmlRootElement(name = "verifiablePresentation", namespace = EDCIConstants.NAMESPACE_VP_DEFAULT)
@XmlAccessorType(XmlAccessType.FIELD)
@EDCIIdentifier(prefix = "urn:verifiable:")
@XmlType(propOrder = {"type", "expirationDate", "issuer", "verifications", "verifiableCredential", "proof"})
public class EuropassPresentationDTO extends VerifiablePresentationDTO implements CredentialHolderDTO {

    @Valid
    @NotNull
    @XmlElement(name = "europassCredential")
    private EuropassCredentialDTO verifiableCredential;

    @Valid
    @XmlElement(namespace = EDCIConstants.NAMESPACE_VP_DEFAULT)
    private OrganizationDTO issuer; //1

    @XmlJavaTypeAdapter(DateTimeAdapter.class)
    @XmlElement(namespace = EDCIConstants.NAMESPACE_VP_DEFAULT)
    private Date expirationDate; //0..1

    @NotNull(message = EDCIMessageKeys.Validation.VALIDATION_CREDENTIAL_TYPE_NOTNULL)
    @Valid
    @XmlElement(namespace = EDCIConstants.NAMESPACE_VP_DEFAULT)
    private Code type;

    @XmlElementWrapper(name = "verificationChecks", namespace = EDCIConstants.NAMESPACE_VP_DEFAULT)
    @XmlElement(name = "verificationCheck", namespace = EDCIConstants.NAMESPACE_VP_DEFAULT)
    private List<VerificationCheckDTO> verifications;

    @XmlElement(name = "proof", namespace = EDCIConstants.NAMESPACE_VP_DEFAULT)
    private String proof = "";

    @XmlTransient
    private List<String> validationErrors = new ArrayList<String>();

    public EuropassCredentialDTO getVerifiableCredential() {
        if (verifiableCredential != null) {
            verifiableCredential.setProof(null);
        }
        return (EuropassCredentialDTO) verifiableCredential;
    }

    public void setVerifiableCredential(EuropassCredentialDTO verifiableCredential) {
        this.verifiableCredential = verifiableCredential;
    }

    public List<String> getValidationErrors() {
        return validationErrors;
    }

    public void setValidationErrors(List<String> validationErrors) {
        this.validationErrors = validationErrors;
    }

    public List<VerificationCheckDTO> getVerifications() {
        return verifications;
    }

    public void setVerifications(List<VerificationCheckDTO> verifications) {
        this.verifications = verifications;
    }

    public String getProof() {
        return proof;
    }

    public void setProof(String proof) {
        this.proof = proof;
    }

    public Date getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(Date expirationDate) {
        this.expirationDate = expirationDate;
    }

    public OrganizationDTO getIssuer() {
        return issuer;
    }

    public void setIssuer(OrganizationDTO issuer) {
        this.issuer = issuer;
    }

    public Code getType() {
        return type;
    }

    public void setType(Code type) {
        this.type = type;
    }

    @Override
    @XmlTransient
    public EuropassCredentialDTO getCredential() {
        return getVerifiableCredential();
    }

    @Override
    public void setPk(String pk) {

    }

    @Override
    public String getPk() {
        return null;
    }

    @Override
    public String getIdentifiableName() {
        return this.getIdentifiableNameFromFieldList(this.getCredential(), "title", "description", "type", "credentialSubject", "id");
    }

    //XML Getters
    public EuropassCredentialDTO getEuropassCredential() {
        return this.verifiableCredential;
    }

    public List<VerificationCheckDTO> getVerificationCheck() {
        return this.verifications;
    }

    public List<VerificationCheckDTO> getVerificationChecks() {
        return this.verifications;
    }
}

