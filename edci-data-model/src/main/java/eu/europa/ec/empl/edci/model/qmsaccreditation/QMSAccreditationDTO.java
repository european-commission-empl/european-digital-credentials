package eu.europa.ec.empl.edci.model.qmsaccreditation;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;

public class QMSAccreditationDTO {
    @JsonProperty("uri")
    public String getUri() { 
		 return this.uri; } 
    public void setUri(String uri) { 
		 this.uri = uri; } 
    String uri;
    @JsonProperty("rdfType") 
    public ArrayList<String> getRdfType() {
		 return this.rdfType; } 
    public void setRdfType(ArrayList<String> rdfType) { 
		 this.rdfType = rdfType; } 
    ArrayList<String> rdfType;
    @JsonProperty("identifier") 
    public ArrayList<Identifier> getIdentifier() { 
		 return this.identifier; } 
    public void setIdentifier(ArrayList<Identifier> identifier) { 
		 this.identifier = identifier; } 
    ArrayList<Identifier> identifier;
    @JsonProperty("type") 
    public Type getType() { 
		 return this.type; } 
    public void setType(Type type) { 
		 this.type = type; } 
    Type type;
    @JsonProperty("title") 
    public String getTitle() { 
		 return this.title; } 
    public void setTitle(String title) { 
		 this.title = title; } 
    String title;
    @JsonProperty("description") 
    public String getDescription() { 
		 return this.description; } 
    public void setDescription(String description) { 
		 this.description = description; } 
    String description;
    @JsonProperty("decision") 
    public Decision getDecision() { 
		 return this.decision; } 
    public void setDecision(Decision decision) { 
		 this.decision = decision; } 
    Decision decision;
    @JsonProperty("report") 
    public Report getReport() { 
		 return this.report; } 
    public void setReport(Report report) { 
		 this.report = report; } 
    Report report;
    @JsonProperty("organisation") 
    public Organisation getOrganisation() { 
		 return this.organisation; } 
    public void setOrganisation(Organisation organisation) { 
		 this.organisation = organisation; } 
    Organisation organisation;
    @JsonProperty("limitField") 
    public ArrayList<LimitField> getLimitField() { 
		 return this.limitField; } 
    public void setLimitField(ArrayList<LimitField> limitField) { 
		 this.limitField = limitField; } 
    ArrayList<LimitField> limitField;
    @JsonProperty("limitEQFLevel") 
    public ArrayList<LimitEQFLevel> getLimitEQFLevel() { 
		 return this.limitEQFLevel; } 
    public void setLimitEQFLevel(ArrayList<LimitEQFLevel> limitEQFLevel) { 
		 this.limitEQFLevel = limitEQFLevel; } 
    ArrayList<LimitEQFLevel> limitEQFLevel;
    @JsonProperty("limitJurisdiction") 
    public ArrayList<LimitJurisdiction> getLimitJurisdiction() { 
		 return this.limitJurisdiction; } 
    public void setLimitJurisdiction(ArrayList<LimitJurisdiction> limitJurisdiction) { 
		 this.limitJurisdiction = limitJurisdiction; } 
    ArrayList<LimitJurisdiction> limitJurisdiction;
    @JsonProperty("limitCredentialType") 
    public ArrayList<LimitCredentialType> getLimitCredentialType() { 
		 return this.limitCredentialType; } 
    public void setLimitCredentialType(ArrayList<LimitCredentialType> limitCredentialType) { 
		 this.limitCredentialType = limitCredentialType; } 
    ArrayList<LimitCredentialType> limitCredentialType;
    @JsonProperty("accreditingAgent") 
    public AccreditingAgent getAccreditingAgent() { 
		 return this.accreditingAgent; } 
    public void setAccreditingAgent(AccreditingAgent accreditingAgent) { 
		 this.accreditingAgent = accreditingAgent; } 
    AccreditingAgent accreditingAgent;
    @JsonProperty("issued") 
    public Issued getIssued() { 
		 return this.issued; } 
    public void setIssued(Issued issued) { 
		 this.issued = issued; } 
    Issued issued;
    @JsonProperty("reviewDate") 
    public ReviewDate getReviewDate() { 
		 return this.reviewDate; } 
    public void setReviewDate(ReviewDate reviewDate) { 
		 this.reviewDate = reviewDate; } 
    ReviewDate reviewDate;
    @JsonProperty("expiryDate") 
    public ExpiryDate getExpiryDate() { 
		 return this.expiryDate; } 
    public void setExpiryDate(ExpiryDate expiryDate) { 
		 this.expiryDate = expiryDate; } 
    ExpiryDate expiryDate;
    @JsonProperty("valid") 
    public Valid getValid() { 
		 return this.valid; } 
    public void setValid(Valid valid) { 
		 this.valid = valid; } 
    Valid valid;
    @JsonProperty("additionalNote") 
    public ArrayList<AdditionalNote> getAdditionalNote() { 
		 return this.additionalNote; } 
    public void setAdditionalNote(ArrayList<AdditionalNote> additionalNote) { 
		 this.additionalNote = additionalNote; } 
    ArrayList<AdditionalNote> additionalNote;
    @JsonProperty("homepage") 
    public ArrayList<Homepage> getHomepage() { 
		 return this.homepage; } 
    public void setHomepage(ArrayList<Homepage> homepage) { 
		 this.homepage = homepage; } 
    ArrayList<Homepage> homepage;
    @JsonProperty("landingPage") 
    public ArrayList<LandingPage> getLandingPage() { 
		 return this.landingPage; } 
    public void setLandingPage(ArrayList<LandingPage> landingPage) { 
		 this.landingPage = landingPage; } 
    ArrayList<LandingPage> landingPage;
    @JsonProperty("supplementaryDocument") 
    public ArrayList<SupplementaryDocument> getSupplementaryDocument() { 
		 return this.supplementaryDocument; } 
    public void setSupplementaryDocument(ArrayList<SupplementaryDocument> supplementaryDocument) { 
		 this.supplementaryDocument = supplementaryDocument; } 
    ArrayList<SupplementaryDocument> supplementaryDocument;
    @JsonProperty("status") 
    public String getStatus() { 
		 return this.status; } 
    public void setStatus(String status) { 
		 this.status = status; } 
    String status;
    @JsonProperty("modified") 
    public Modified getModified() { 
		 return this.modified; } 
    public void setModified(Modified modified) { 
		 this.modified = modified; } 
    Modified modified;
    @JsonProperty("publisher") 
    public Publisher getPublisher() { 
		 return this.publisher; } 
    public void setPublisher(Publisher publisher) { 
		 this.publisher = publisher; } 
    Publisher publisher;
    @JsonProperty("metadata") 
    public Metadata getMetadata() { 
		 return this.metadata; } 
    public void setMetadata(Metadata metadata) { 
		 this.metadata = metadata; } 
    Metadata metadata;
}
