package eu.europa.ec.empl.edci.issuer.web.model.dataContainers;

import eu.europa.ec.empl.edci.issuer.web.model.dataTypes.*;

import java.net.URI;
import java.util.List;

public class AccreditationDCView extends DataContainerView {

    private URI id; //0..1

    private List<IdentifierDTView> identifier; //*

    private CodeDTView accreditationType; //1

    private TextDTView title; //0..1

    private NoteDTView description; //*

    private ScoreDTView decision; //0..1

    private WebDocumentDCView report; //0..1

//    private OrganizationSpecView organization; //1

    private List<CodeDTView> limitField; //*

    private List<CodeDTView> limitEqfLevel; //*

    private List<CodeDTView> limitJurisdiction; //*

//    private OrganizationSpecView accreditingAgent; //1

    private String issueDate; //0..1

    private String reviewDate; //0..1

    private String expiryDate; //0..1

    private List<NoteDTView> additionalNote; //*

    private List<WebDocumentDCView> homePage; //*

    private List<WebDocumentDCView> supplementaryDocument; //*

    public URI getId() {
        return id;
    }

    public void setId(URI id) {
        this.id = id;
    }

    public List<IdentifierDTView> getIdentifier() {
        return identifier;
    }

    public void setIdentifier(List<IdentifierDTView> identifier) {
        this.identifier = identifier;
    }

    public CodeDTView getAccreditationType() {
        return accreditationType;
    }

    public void setAccreditationType(CodeDTView accreditationType) {
        this.accreditationType = accreditationType;
    }

    public TextDTView getTitle() {
        return title;
    }

    public void setTitle(TextDTView title) {
        this.title = title;
    }

    public NoteDTView getDescription() {
        return description;
    }

    public void setDescription(NoteDTView description) {
        this.description = description;
    }

    public ScoreDTView getDecision() {
        return decision;
    }

    public void setDecision(ScoreDTView decision) {
        this.decision = decision;
    }

    public WebDocumentDCView getReport() {
        return report;
    }

    public void setReport(WebDocumentDCView report) {
        this.report = report;
    }

//    public OrganizationSpecView getOrganization() {
//        return organization;
//    }
//
//    public void setOrganization(OrganizationSpecView organization) {
//        this.organization = organization;
//    }

    public List<CodeDTView> getLimitField() {
        return limitField;
    }

    public void setLimitField(List<CodeDTView> limitField) {
        this.limitField = limitField;
    }

    public List<CodeDTView> getLimitEqfLevel() {
        return limitEqfLevel;
    }

    public void setLimitEqfLevel(List<CodeDTView> limitEqfLevel) {
        this.limitEqfLevel = limitEqfLevel;
    }

    public List<CodeDTView> getLimitJurisdiction() {
        return limitJurisdiction;
    }

    public void setLimitJurisdiction(List<CodeDTView> limitJurisdiction) {
        this.limitJurisdiction = limitJurisdiction;
    }

//    public OrganizationSpecView getAccreditingAgent() {
//        return accreditingAgent;
//    }
//
//    public void setAccreditingAgent(OrganizationSpecView accreditingAgent) {
//        this.accreditingAgent = accreditingAgent;
//    }

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

    public List<NoteDTView> getAdditionalNote() {
        return additionalNote;
    }

    public void setAdditionalNote(List<NoteDTView> additionalNote) {
        this.additionalNote = additionalNote;
    }

    public List<WebDocumentDCView> getHomePage() {
        return homePage;
    }

    public void setHomePage(List<WebDocumentDCView> homePage) {
        this.homePage = homePage;
    }

    public List<WebDocumentDCView> getSupplementaryDocument() {
        return supplementaryDocument;
    }

    public void setSupplementaryDocument(List<WebDocumentDCView> supplementaryDocument) {
        this.supplementaryDocument = supplementaryDocument;
    }
}