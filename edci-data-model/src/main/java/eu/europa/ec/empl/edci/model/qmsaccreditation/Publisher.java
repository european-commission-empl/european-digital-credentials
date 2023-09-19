package eu.europa.ec.empl.edci.model.qmsaccreditation;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;

public class Publisher{
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
    @JsonProperty("legalName") 
    public String getLegalName() { 
		 return this.legalName; } 
    public void setLegalName(String legalName) { 
		 this.legalName = legalName; } 
    String legalName;
    @JsonProperty("type") 
    public ArrayList<Object> getType() { 
		 return this.type; } 
    public void setType(ArrayList<Object> type) { 
		 this.type = type; } 
    ArrayList<Object> type;
    @JsonProperty("identifier") 
    public ArrayList<Object> getIdentifier() { 
		 return this.identifier; } 
    public void setIdentifier(ArrayList<Object> identifier) { 
		 this.identifier = identifier; } 
    ArrayList<Object> identifier;
    @JsonProperty("vatIdentifier") 
    public ArrayList<Object> getVatIdentifier() { 
		 return this.vatIdentifier; } 
    public void setVatIdentifier(ArrayList<Object> vatIdentifier) { 
		 this.vatIdentifier = vatIdentifier; } 
    ArrayList<Object> vatIdentifier;
    @JsonProperty("taxIdentifier") 
    public ArrayList<Object> getTaxIdentifier() { 
		 return this.taxIdentifier; } 
    public void setTaxIdentifier(ArrayList<Object> taxIdentifier) { 
		 this.taxIdentifier = taxIdentifier; } 
    ArrayList<Object> taxIdentifier;
    @JsonProperty("homepage") 
    public ArrayList<Object> getHomepage() { 
		 return this.homepage; } 
    public void setHomepage(ArrayList<Object> homepage) { 
		 this.homepage = homepage; } 
    ArrayList<Object> homepage;
    @JsonProperty("additionalNote") 
    public ArrayList<Object> getAdditionalNote() { 
		 return this.additionalNote; } 
    public void setAdditionalNote(ArrayList<Object> additionalNote) { 
		 this.additionalNote = additionalNote; } 
    ArrayList<Object> additionalNote;
    @JsonProperty("accreditation") 
    public ArrayList<Object> getAccreditation() { 
		 return this.accreditation; } 
    public void setAccreditation(ArrayList<Object> accreditation) { 
		 this.accreditation = accreditation; } 
    ArrayList<Object> accreditation;
    @JsonProperty("location") 
    public ArrayList<Location> getLocation() { 
		 return this.location; } 
    public void setLocation(ArrayList<Location> location) { 
		 this.location = location; } 
    ArrayList<Location> location;
    @JsonProperty("contactPoint") 
    public ArrayList<Object> getContactPoint() { 
		 return this.contactPoint; } 
    public void setContactPoint(ArrayList<Object> contactPoint) { 
		 this.contactPoint = contactPoint; } 
    ArrayList<Object> contactPoint;
    @JsonProperty("hasSubOrganization") 
    public ArrayList<Object> getHasSubOrganization() { 
		 return this.hasSubOrganization; } 
    public void setHasSubOrganization(ArrayList<Object> hasSubOrganization) { 
		 this.hasSubOrganization = hasSubOrganization; } 
    ArrayList<Object> hasSubOrganization;
    @JsonProperty("groupMemberOf") 
    public ArrayList<Object> getGroupMemberOf() { 
		 return this.groupMemberOf; } 
    public void setGroupMemberOf(ArrayList<Object> groupMemberOf) { 
		 this.groupMemberOf = groupMemberOf; } 
    ArrayList<Object> groupMemberOf;
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
}
