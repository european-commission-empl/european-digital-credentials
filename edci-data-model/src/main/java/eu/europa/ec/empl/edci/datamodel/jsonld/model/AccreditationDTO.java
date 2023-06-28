package eu.europa.ec.empl.edci.datamodel.jsonld.model;

import eu.europa.ec.empl.edci.annotation.EDCIIdentifier;
import eu.europa.ec.empl.edci.datamodel.jsonld.model.base.JsonLdCommonDTO;
import eu.europa.ec.empl.edci.datamodel.jsonld.model.dataTypes.*;

import javax.validation.constraints.NotNull;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@EDCIIdentifier(prefix = "urn:epass:accreditation:")
public class AccreditationDTO extends JsonLdCommonDTO {

    private ZonedDateTime dateIssued;
    private List<ConceptDTO> limitEQFLevel = new ArrayList<>();
    private List<ConceptDTO> limitField = new ArrayList<>();
    private List<ConceptDTO> limitJurisdiction = new ArrayList<>();
    @NotNull
    private OrganisationDTO accreditingAgent;
    private ConceptDTO decision;
    private LiteralMap description;
    private ZonedDateTime expiryDate;
    private List<WebResourceDTO> homepage = new ArrayList<>();
    private List<Identifier> identifier = new ArrayList<>();
    private List<WebResourceDTO> landingPage = new ArrayList<>();
    private ZonedDateTime dateModified;
    private List<ConceptDTO> limitCredentialType = new ArrayList<>();
    private List<NoteDTO> additionalNote = new ArrayList<>();
    @NotNull
    private OrganisationDTO organisation;
    private List<WebResourceDTO> supplementaryDocument = new ArrayList<>();
    private QualificationDTO limitQualification;
    private WebResourceDTO report;
    private ZonedDateTime reviewDate;
    @NotNull
    private LiteralMap title;
    @NotNull
    private ConceptDTO dcType;
    private ZonedDateTime valid;


    @Override
    public String getName() {
        return this.getNameFromFieldList(this, "title", "description", "id", "organisation");
    }

    public void setDateIssued(ZonedDateTime dateIssued) {
        this.dateIssued = dateIssued;
    }

    public ZonedDateTime getDateIssued() {
        return dateIssued;
    }

    public ZonedDateTime getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(ZonedDateTime expiryDate) {
        this.expiryDate = expiryDate;
    }

    public ZonedDateTime getDateModified() {
        return dateModified;
    }

    public void setDateModified(ZonedDateTime dateModified) {
        this.dateModified = dateModified;
    }

    public ZonedDateTime getReviewDate() {
        return reviewDate;
    }

    public void setReviewDate(ZonedDateTime reviewDate) {
        this.reviewDate = reviewDate;
    }

    public LiteralMap getTitle() {
        return title;
    }

    public void setTitle(LiteralMap title) {
        this.title = title;
    }

    public OrganisationDTO getAccreditingAgent() {
        return accreditingAgent;
    }

    public void setAccreditingAgent(OrganisationDTO accreditingAgent) {
        this.accreditingAgent = accreditingAgent;
    }

    public OrganisationDTO getOrganisation() {
        return organisation;
    }

    public void setOrganisation(OrganisationDTO organisation) {
        this.organisation = organisation;
    }

    public List<ConceptDTO> getLimitEQFLevel() {
        return limitEQFLevel;
    }

    public List<ConceptDTO> getLimitField() {
        return limitField;
    }

    public List<ConceptDTO> getLimitJurisdiction() {
        return limitJurisdiction;
    }

    public ConceptDTO getDecision() {
        return decision;
    }

    public void setDecision(ConceptDTO decision) {
        this.decision = decision;
    }

    public LiteralMap getDescription() {
        return description;
    }

    public void setDescription(LiteralMap description) {
        this.description = description;
    }

    public List<WebResourceDTO> getHomepage() {
        return homepage;
    }

    public List<Identifier> getIdentifier() {
        return identifier;
    }

    public List<WebResourceDTO> getLandingPage() {
        return landingPage;
    }

    public List<NoteDTO> getAdditionalNote() {
        return additionalNote;
    }

    public List<WebResourceDTO> getSupplementaryDocument() {
        return supplementaryDocument;
    }

    public QualificationDTO getLimitQualification() {
        return limitQualification;
    }

    public void setLimitQualification(QualificationDTO limitQualification) {
        this.limitQualification = limitQualification;
    }

    public WebResourceDTO getReport() {
        return report;
    }

    public void setReport(WebResourceDTO report) {
        this.report = report;
    }

    public ConceptDTO getDcType() {
        return dcType;
    }

    public void setDcType(ConceptDTO dcType) {
        this.dcType = dcType;
    }

    public List<ConceptDTO> getLimitCredentialType() {
        return limitCredentialType;
    }

    public ZonedDateTime getValid() {
        return valid;
    }

    public void setValid(ZonedDateTime valid) {
        this.valid = valid;
    }

    public void setLimitEQFLevel(List<ConceptDTO> limitEQFLevel) {
        this.limitEQFLevel = limitEQFLevel;
    }

    public void setLimitField(List<ConceptDTO> limitField) {
        this.limitField = limitField;
    }

    public void setLimitJurisdiction(List<ConceptDTO> limitJurisdiction) {
        this.limitJurisdiction = limitJurisdiction;
    }

    public void setHomepage(List<WebResourceDTO> homepage) {
        this.homepage = homepage;
    }

    public void setIdentifier(List<Identifier> identifier) {
        this.identifier = identifier;
    }

    public void setLandingPage(List<WebResourceDTO> landingPage) {
        this.landingPage = landingPage;
    }

    public void setLimitCredentialType(List<ConceptDTO> limitCredentialType) {
        this.limitCredentialType = limitCredentialType;
    }

    public void setAdditionalNote(List<NoteDTO> additionalNote) {
        this.additionalNote = additionalNote;
    }

    public void setSupplementaryDocument(List<WebResourceDTO> supplementaryDocument) {
        this.supplementaryDocument = supplementaryDocument;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AccreditationDTO)) return false;
        if (!super.equals(o)) return false;
        AccreditationDTO that = (AccreditationDTO) o;
        return Objects.equals(dateIssued, that.dateIssued) &&
                Objects.equals(limitEQFLevel, that.limitEQFLevel) &&
                Objects.equals(limitField, that.limitField) &&
                Objects.equals(limitJurisdiction, that.limitJurisdiction) &&
                Objects.equals(accreditingAgent, that.accreditingAgent) &&
                Objects.equals(decision, that.decision) &&
                Objects.equals(description, that.description) &&
                Objects.equals(expiryDate, that.expiryDate) &&
                Objects.equals(homepage, that.homepage) &&
                Objects.equals(identifier, that.identifier) &&
                Objects.equals(landingPage, that.landingPage) &&
                Objects.equals(dateModified, that.dateModified) &&
                Objects.equals(limitCredentialType, that.limitCredentialType) &&
                Objects.equals(additionalNote, that.additionalNote) &&
                Objects.equals(organisation, that.organisation) &&
                Objects.equals(supplementaryDocument, that.supplementaryDocument) &&
                Objects.equals(limitQualification, that.limitQualification) &&
                Objects.equals(report, that.report) &&
                Objects.equals(reviewDate, that.reviewDate) &&
                Objects.equals(title, that.title) &&
                Objects.equals(dcType, that.dcType) &&
                Objects.equals(valid, that.valid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), dateIssued, limitEQFLevel, limitField, limitJurisdiction, accreditingAgent, decision, description, expiryDate, homepage, identifier, landingPage, dateModified, limitCredentialType, additionalNote, organisation, supplementaryDocument, limitQualification, report, reviewDate, title, dcType, valid);
    }
}
