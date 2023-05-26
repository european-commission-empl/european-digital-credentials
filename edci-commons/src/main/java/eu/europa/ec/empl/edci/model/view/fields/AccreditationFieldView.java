package eu.europa.ec.empl.edci.model.view.fields;

import eu.europa.ec.empl.edci.model.view.tabs.OrganizationTabView;

import java.util.List;


public class AccreditationFieldView {

    private String id;
    private List<IdentifierFieldView> identifier;
    private LinkFieldView dcType;
    private String title;
    private String description;
    private String decision;
    private String status;
    private LinkFieldView report;
    private OrganizationTabView organisation;
    private QualificationFieldView limitQualification; //qualification.title
    private List<String> limitField;
    private List<String> limitEQFLevel;
    private List<String> limitCredentialType;
    private List<String> limitJurisdiction;
    private OrganizationTabView accreditingAgent;
    private String dateIssued;
    private String reviewDate;
    private String expiryDate;
    private String dateModified;
    private String valid;
    private List<NoteFieldView> additionalNote;
    private List<LinkFieldView> homepage;
    private List<LinkFieldView> supplementaryDocument;
    private List<LinkFieldView> landingPage;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<IdentifierFieldView> getIdentifier() {
        return identifier;
    }

    public void setIdentifier(List<IdentifierFieldView> identifier) {
        this.identifier = identifier;
    }

    public LinkFieldView getDcType() {
        return dcType;
    }

    public void setDcType(LinkFieldView dcType) {
        this.dcType = dcType;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDecision() {
        return decision;
    }

    public void setDecision(String decision) {
        this.decision = decision;
    }

    public LinkFieldView getReport() {
        return report;
    }

    public void setReport(LinkFieldView report) {
        this.report = report;
    }

    public OrganizationTabView getOrganisation() {
        return organisation;
    }

    public void setOrganisation(OrganizationTabView organisation) {
        this.organisation = organisation;
    }

    public List<String> getLimitField() {
        return limitField;
    }

    public void setLimitField(List<String> limitField) {
        this.limitField = limitField;
    }

    public List<String> getLimitEQFLevel() {
        return limitEQFLevel;
    }

    public void setLimitEQFLevel(List<String> limitEQFLevel) {
        this.limitEQFLevel = limitEQFLevel;
    }

    public List<String> getLimitJurisdiction() {
        return limitJurisdiction;
    }

    public void setLimitJurisdiction(List<String> limitJurisdiction) {
        this.limitJurisdiction = limitJurisdiction;
    }

    public OrganizationTabView getAccreditingAgent() {
        return accreditingAgent;
    }

    public void setAccreditingAgent(OrganizationTabView accreditingAgent) {
        this.accreditingAgent = accreditingAgent;
    }

    public String getDateIssued() {
        return dateIssued;
    }

    public void setDateIssued(String dateIssued) {
        this.dateIssued = dateIssued;
    }

    public String getReviewDate() {
        return reviewDate;
    }

    public void setReviewDate(String reviewDate) {
        this.reviewDate = reviewDate;
    }

    public String getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(String expiryDate) {
        this.expiryDate = expiryDate;
    }

    public QualificationFieldView getLimitQualification() {
        return limitQualification;
    }

    public void setLimitQualification(QualificationFieldView limitQualification) {
        this.limitQualification = limitQualification;
    }

    public List<NoteFieldView> getAdditionalNote() {
        return additionalNote;
    }

    public void setAdditionalNote(List<NoteFieldView> additionalNote) {
        this.additionalNote = additionalNote;
    }

    public List<LinkFieldView> getHomepage() {
        return homepage;
    }

    public void setHomepage(List<LinkFieldView> homepage) {
        this.homepage = homepage;
    }

    public List<LinkFieldView> getSupplementaryDocument() {
        return supplementaryDocument;
    }

    public void setSupplementaryDocument(List<LinkFieldView> supplementaryDocument) {
        this.supplementaryDocument = supplementaryDocument;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<String> getLimitCredentialType() {
        return limitCredentialType;
    }

    public void setLimitCredentialType(List<String> limitCredentialType) {
        this.limitCredentialType = limitCredentialType;
    }

    public String getDateModified() {
        return dateModified;
    }

    public void setDateModified(String dateModified) {
        this.dateModified = dateModified;
    }

    public String getValid() {
        return valid;
    }

    public void setValid(String valid) {
        this.valid = valid;
    }

    public List<LinkFieldView> getLandingPage() {
        return landingPage;
    }

    public void setLandingPage(List<LinkFieldView> landingPage) {
        this.landingPage = landingPage;
    }
}