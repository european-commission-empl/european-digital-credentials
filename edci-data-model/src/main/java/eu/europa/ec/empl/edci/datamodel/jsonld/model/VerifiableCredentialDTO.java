package eu.europa.ec.empl.edci.datamodel.jsonld.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import eu.europa.ec.empl.edci.datamodel.jsonld.model.base.Identifiable;
import eu.europa.ec.empl.edci.datamodel.jsonld.model.base.JsonLdContextHolder;
import jakarta.json.JsonValue;

import javax.validation.constraints.NotNull;
import java.net.URI;
import java.time.ZonedDateTime;
import java.util.*;

@JsonSubTypes({
        @JsonSubTypes.Type(value = EuropeanDigitalCredentialDTO.class)
})
public abstract class VerifiableCredentialDTO implements JsonLdContextHolder, Identifiable {

    @NotNull
    private URI id;
    @NotNull
    private List<String> type = new ArrayList<>();
    //Could not get a Filter to work, for the time being just @JsonIgnore it and treat in on postDeserialize action
    @JsonIgnore
    private JsonValue jsonLdContext;
    @JsonIgnore
    private boolean valid = true;
    @JsonIgnore
    private List<String> validationErrors = new ArrayList<>();
    private List<ShaclValidator2017> credentialSchema = new ArrayList<>();
    private List<URI> credentialStatus = new ArrayList<>();
    private List<Evidence> evidence  = new ArrayList<>();
    private ZonedDateTime expirationDate;
    private List<AgentDTO> holder = new ArrayList<>();
    @NotNull
    private PersonDTO credentialSubject;
    private OrganisationDTO issuer;
    private ZonedDateTime issuanceDate;
    @NotNull
    private ZonedDateTime issued;
    private Map<String, String> proof = new HashMap<>();
    private List<URI> termsOfUse = new ArrayList<>();
    private ZonedDateTime validUntil;
    @NotNull
    private ZonedDateTime validFrom;

    @Override
    public URI getId() {
        return id;
    }

    @Override
    public void setId(URI id) {
        this.id = id;
    }

    public List<String> getType() {
        return type;
    }

    public void setType(List<String> type) {
        this.type = type;
    }

    @Override
    public JsonValue getJsonLdContext() {
        return jsonLdContext;
    }

    public void setJsonLdContext(JsonValue jsonLdContext) {
        this.jsonLdContext = jsonLdContext;
    }

    public ZonedDateTime getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(ZonedDateTime expirationDate) {
        this.expirationDate = expirationDate;
    }

    public ZonedDateTime getIssuanceDate() {
        return issuanceDate;
    }

    public void setIssuanceDate(ZonedDateTime issuanceDate) {
        this.issuanceDate = issuanceDate;
    }

    public PersonDTO getCredentialSubject() {
        return credentialSubject;
    }

    public void setCredentialSubject(PersonDTO credentialSubject) {
        this.credentialSubject = credentialSubject;
    }

    public OrganisationDTO getIssuer() {
        return issuer;
    }

    public void setIssuer(OrganisationDTO issuer) {
        this.issuer = issuer;
    }

    public ZonedDateTime getIssued() {
        return issued;
    }

    public void setIssued(ZonedDateTime issued) {
        this.issued = issued;
    }

    public ZonedDateTime getValidUntil() {
        return validUntil;
    }

    public void setValidUntil(ZonedDateTime validUntil) {
        this.validUntil = validUntil;
    }

    public ZonedDateTime getValidFrom() {
        return validFrom;
    }

    public void setValidFrom(ZonedDateTime validFrom) {
        this.validFrom = validFrom;
    }

    public Map<String, String> getProof() {
        return proof;
    }

    public void setProof(Map<String, String> proof) {
        this.proof = proof;
    }

    public List<URI> getURI() {
        return credentialStatus;
    }

    public List<Evidence> getEvidence() {
        return evidence;
    }

    public void setEvidence(List<Evidence> evidence) {
        this.evidence = evidence;
    }

    public List<AgentDTO> getHolder() {
        return holder;
    }

    public List<URI> getCredentialStatus() {
        return credentialStatus;
    }

    public List<ShaclValidator2017> getCredentialSchema() {
        return credentialSchema;
    }

    public List<URI> getTermsOfUse() {
        return termsOfUse;
    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public List<String> getValidationErrors() {
        return validationErrors;
    }

    public void setValidationErrors(List<String> validationErrors) {
        this.validationErrors = validationErrors;
    }

    public void setCredentialSchema(List<ShaclValidator2017> credentialSchema) {
        this.credentialSchema = credentialSchema;
    }

    public void setCredentialStatus(List<URI> credentialStatus) {
        this.credentialStatus = credentialStatus;
    }

    public void setHolder(List<AgentDTO> holder) {
        this.holder = holder;
    }

    public void setTermsOfUse(List<URI> termsOfUse) {
        this.termsOfUse = termsOfUse;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof VerifiableCredentialDTO)) return false;
        VerifiableCredentialDTO that = (VerifiableCredentialDTO) o;
        return valid == that.valid &&
                Objects.equals(jsonLdContext, that.jsonLdContext) &&
                Objects.equals(validationErrors, that.validationErrors) &&
                Objects.equals(credentialSchema, that.credentialSchema) &&
                Objects.equals(credentialStatus, that.credentialStatus) &&
                Objects.equals(evidence, that.evidence) &&
                Objects.equals(expirationDate, that.expirationDate) &&
                Objects.equals(holder, that.holder) &&
                Objects.equals(credentialSubject, that.credentialSubject) &&
                Objects.equals(issuanceDate, that.issuanceDate) &&
                Objects.equals(issued, that.issued) &&
                Objects.equals(issuer, that.issuer) &&
                Objects.equals(proof, that.proof) &&
                Objects.equals(termsOfUse, that.termsOfUse) &&
                Objects.equals(validUntil, that.validUntil) &&
                Objects.equals(validFrom, that.validFrom);
    }

    @Override
    public int hashCode() {
        return Objects.hash(jsonLdContext, valid, validationErrors, credentialSchema, credentialStatus, evidence, expirationDate, holder, credentialSubject, issuanceDate, issued, issuer, proof, termsOfUse, validUntil, validFrom);
    }
}
