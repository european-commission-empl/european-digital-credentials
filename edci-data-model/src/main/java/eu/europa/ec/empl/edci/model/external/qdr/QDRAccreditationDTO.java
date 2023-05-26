package eu.europa.ec.empl.edci.model.external.qdr;

import eu.europa.ec.empl.edci.annotation.EDCIIdentifier;
import eu.europa.ec.empl.edci.annotation.MandatoryConceptScheme;
import eu.europa.ec.empl.edci.datamodel.jsonld.model.QualificationDTO;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@EDCIIdentifier(prefix = "urn:epass:accreditation:")
public class QDRAccreditationDTO extends QDRJsonLdCommonDTO {

    private QDRValue issued;
    @MandatoryConceptScheme("http://data.europa.eu/snb/eqf/25831c2")
    private List<QDRConceptDTO> limitEQFLevel = new ArrayList<>();
    @MandatoryConceptScheme("http://data.europa.eu/snb/isced-f/25831c2")
    private List<QDRConceptDTO> limitField = new ArrayList<>();
    @MandatoryConceptScheme("http://publications.europa.eu/resource/authority/atu")
    private List<QDRConceptDTO> limitJurisdiction = new ArrayList<>();
    @NotNull
    private QDROrganisationDTO accreditingAgent;
    private QDRConceptDTO decision;
    private String description;
    private QDRValue expiryDate;
    private List<QDRWebResourceDTO> homepage = new ArrayList<>();
    private List<QDRIdentifier> identifier = new ArrayList<>();
    private List<QDRWebResourceDTO> landingPage = new ArrayList<>();
    private QDRValue modified;
    @MandatoryConceptScheme("http://data.europa.eu/snb/credential/25831c2")
    private List<QDRConceptDTO> limitCredentialType = new ArrayList<>();
    private List<QDRNoteDTO> additionalNote = new ArrayList<>();
    @NotNull
    private QDROrganisationDTO organisation;
    private List<QDRWebResourceDTO> supplementaryDocument = new ArrayList<>();
    private QualificationDTO limitQualification;
    private QDRWebResourceDTO report;
    private QDRValue reviewDate;
    @NotNull
    private String title;
    @NotNull
    @MandatoryConceptScheme("http://data.europa.eu/snb/accreditation/25831c2")
    private QDRConceptDTO type;
    private QDRValue valid;
    private String status;
    private QDRMetadata metadata;

    public void setIssued(QDRValue issued) {
        this.issued = issued;
    }

    public QDRValue getIssued() {
        return issued;
    }

    public QDRValue getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(QDRValue expiryDate) {
        this.expiryDate = expiryDate;
    }

    public QDRValue getModified() {
        return modified;
    }

    public void setModified(QDRValue modified) {
        this.modified = modified;
    }

    public QDRValue getReviewDate() {
        return reviewDate;
    }

    public void setReviewDate(QDRValue reviewDate) {
        this.reviewDate = reviewDate;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public QDROrganisationDTO getAccreditingAgent() {
        return accreditingAgent;
    }

    public void setAccreditingAgent(QDROrganisationDTO accreditingAgent) {
        this.accreditingAgent = accreditingAgent;
    }

    public QDROrganisationDTO getOrganisation() {
        return organisation;
    }

    public void setOrganisation(QDROrganisationDTO organisation) {
        this.organisation = organisation;
    }

    public List<QDRConceptDTO> getLimitEQFLevel() {
        return limitEQFLevel;
    }

    public List<QDRConceptDTO> getLimitField() {
        return limitField;
    }

    public List<QDRConceptDTO> getLimitJurisdiction() {
        return limitJurisdiction;
    }

    public QDRConceptDTO getDecision() {
        return decision;
    }

    public void setDecision(QDRConceptDTO decision) {
        this.decision = decision;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<QDRWebResourceDTO> getHomepage() {
        return homepage;
    }

    public List<QDRIdentifier> getIdentifier() {
        return identifier;
    }

    public List<QDRWebResourceDTO> getLandingPage() {
        return landingPage;
    }

    public List<QDRNoteDTO> getAdditionalNote() {
        return additionalNote;
    }

    public List<QDRWebResourceDTO> getSupplementaryDocument() {
        return supplementaryDocument;
    }

    public QualificationDTO getLimitQualification() {
        return limitQualification;
    }

    public void setLimitQualification(QualificationDTO limitQualification) {
        this.limitQualification = limitQualification;
    }

    public QDRWebResourceDTO getReport() {
        return report;
    }

    public void setReport(QDRWebResourceDTO report) {
        this.report = report;
    }

    public QDRConceptDTO getType() {
        return type;
    }

    public void setType(QDRConceptDTO type) {
        this.type = type;
    }

    public List<QDRConceptDTO> getLimitCredentialType() {
        return limitCredentialType;
    }

    public QDRValue getValid() {
        return valid;
    }

    public void setValid(QDRValue valid) {
        this.valid = valid;
    }

    public void setLimitEQFLevel(List<QDRConceptDTO> limitEQFLevel) {
        this.limitEQFLevel = limitEQFLevel;
    }

    public void setLimitField(List<QDRConceptDTO> limitField) {
        this.limitField = limitField;
    }

    public void setLimitJurisdiction(List<QDRConceptDTO> limitJurisdiction) {
        this.limitJurisdiction = limitJurisdiction;
    }

    public void setHomepage(List<QDRWebResourceDTO> homepage) {
        this.homepage = homepage;
    }

    public void setIdentifier(List<QDRIdentifier> identifier) {
        this.identifier = identifier;
    }

    public void setLandingPage(List<QDRWebResourceDTO> landingPage) {
        this.landingPage = landingPage;
    }

    public void setLimitCredentialType(List<QDRConceptDTO> limitCredentialType) {
        this.limitCredentialType = limitCredentialType;
    }

    public void setAdditionalNote(List<QDRNoteDTO> additionalNote) {
        this.additionalNote = additionalNote;
    }

    public void setSupplementaryDocument(List<QDRWebResourceDTO> supplementaryDocument) {
        this.supplementaryDocument = supplementaryDocument;
    }

    public QDRMetadata getMetadata() {
        return metadata;
    }

    public void setMetadata(QDRMetadata metadata) {
        this.metadata = metadata;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof QDRAccreditationDTO)) return false;
        if (!super.equals(o)) return false;
        QDRAccreditationDTO that = (QDRAccreditationDTO) o;
        return Objects.equals(issued, that.issued) &&
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
                Objects.equals(modified, that.modified) &&
                Objects.equals(limitCredentialType, that.limitCredentialType) &&
                Objects.equals(additionalNote, that.additionalNote) &&
                Objects.equals(organisation, that.organisation) &&
                Objects.equals(supplementaryDocument, that.supplementaryDocument) &&
                Objects.equals(limitQualification, that.limitQualification) &&
                Objects.equals(report, that.report) &&
                Objects.equals(reviewDate, that.reviewDate) &&
                Objects.equals(title, that.title) &&
                Objects.equals(type, that.type) &&
                Objects.equals(status, that.status) &&
                Objects.equals(valid, that.valid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), status, issued, limitEQFLevel, limitField, limitJurisdiction, accreditingAgent, decision, description, expiryDate, homepage, identifier, landingPage, modified, limitCredentialType, additionalNote, organisation, supplementaryDocument, limitQualification, report, reviewDate, title, type, valid);
    }
}
