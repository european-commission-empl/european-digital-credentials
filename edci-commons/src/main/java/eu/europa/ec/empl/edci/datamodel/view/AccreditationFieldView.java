package eu.europa.ec.empl.edci.datamodel.view;

import eu.europa.ec.empl.edci.datamodel.model.AccreditationDTO;

import java.util.List;


public class AccreditationFieldView {

    private String id;
    private List<IdentifierFieldView> identifier;
    private LinkFieldView accreditationType;
    private String title;
    private String description;
    private String decision;
    private LinkFieldView report;
    private OrganizationTabView organization;
    private String limitQualification; //qualification.title
    private List<String> limitField;
    private List<String> limitEqfLevel;
    private List<String> limitJurisdiction;
    private OrganizationTabView accreditingAgent;
    private String issueDate;
    private String reviewDate;
    private String expiryDate;
    private List<NoteFieldView> additionalNote;
    private List<LinkFieldView> homePage;
    private List<LinkFieldView> supplementaryDocument;

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

    public LinkFieldView getAccreditationType() {
        return accreditationType;
    }

    public void setAccreditationType(LinkFieldView accreditationType) {
        this.accreditationType = accreditationType;
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

    public OrganizationTabView getOrganization() {
        return organization;
    }

    public void setOrganization(OrganizationTabView organization) {
        this.organization = organization;
    }

    public List<String> getLimitField() {
        return limitField;
    }

    public void setLimitField(List<String> limitField) {
        this.limitField = limitField;
    }

    public List<String> getLimitEqfLevel() {
        return limitEqfLevel;
    }

    public void setLimitEqfLevel(List<String> limitEqfLevel) {
        this.limitEqfLevel = limitEqfLevel;
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

    public String getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(String issueDate) {
        this.issueDate = issueDate;
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

    public String getLimitQualification() {
        return limitQualification;
    }

    public void setLimitQualification(String limitQualification) {
        this.limitQualification = limitQualification;
    }

    public List<NoteFieldView> getAdditionalNote() {
        return additionalNote;
    }

    public void setAdditionalNote(List<NoteFieldView> additionalNote) {
        this.additionalNote = additionalNote;
    }

    public List<LinkFieldView> getHomePage() {
        return homePage;
    }

    public void setHomePage(List<LinkFieldView> homePage) {
        this.homePage = homePage;
    }

    public List<LinkFieldView> getSupplementaryDocument() {
        return supplementaryDocument;
    }

    public void setSupplementaryDocument(List<LinkFieldView> supplementaryDocument) {
        this.supplementaryDocument = supplementaryDocument;
    }
}