package eu.europa.ec.empl.edci.model.qmsaccreditation;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;

public class Organisation{
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
    @JsonProperty("altLabel") 
    public ArrayList<String> getAltLabel() { 
		 return this.altLabel; } 
    public void setAltLabel(ArrayList<String> altLabel) { 
		 this.altLabel = altLabel; } 
    ArrayList<String> altLabel;
    @JsonProperty("legalName") 
    public String getLegalName() { 
		 return this.legalName; } 
    public void setLegalName(String legalName) { 
		 this.legalName = legalName; } 
    String legalName;
    @JsonProperty("type") 
    public ArrayList<Type> getType() { 
		 return this.type; } 
    public void setType(ArrayList<Type> type) { 
		 this.type = type; } 
    ArrayList<Type> type;
    @JsonProperty("identifier") 
    public ArrayList<Identifier> getIdentifier() { 
		 return this.identifier; } 
    public void setIdentifier(ArrayList<Identifier> identifier) { 
		 this.identifier = identifier; } 
    ArrayList<Identifier> identifier;
    @JsonProperty("eidasLegalIdentifier") 
    public EidasLegalIdentifier getEidasLegalIdentifier() { 
		 return this.eidasLegalIdentifier; } 
    public void setEidasLegalIdentifier(EidasLegalIdentifier eidasLegalIdentifier) { 
		 this.eidasLegalIdentifier = eidasLegalIdentifier; } 
    EidasLegalIdentifier eidasLegalIdentifier;
    @JsonProperty("registration") 
    public Registration getRegistration() { 
		 return this.registration; } 
    public void setRegistration(Registration registration) { 
		 this.registration = registration; } 
    Registration registration;
    @JsonProperty("vatIdentifier") 
    public ArrayList<VatIdentifier> getVatIdentifier() { 
		 return this.vatIdentifier; } 
    public void setVatIdentifier(ArrayList<VatIdentifier> vatIdentifier) { 
		 this.vatIdentifier = vatIdentifier; } 
    ArrayList<VatIdentifier> vatIdentifier;
    @JsonProperty("taxIdentifier") 
    public ArrayList<TaxIdentifier> getTaxIdentifier() { 
		 return this.taxIdentifier; } 
    public void setTaxIdentifier(ArrayList<TaxIdentifier> taxIdentifier) { 
		 this.taxIdentifier = taxIdentifier; } 
    ArrayList<TaxIdentifier> taxIdentifier;
    @JsonProperty("homepage") 
    public ArrayList<Homepage> getHomepage() { 
		 return this.homepage; } 
    public void setHomepage(ArrayList<Homepage> homepage) { 
		 this.homepage = homepage; } 
    ArrayList<Homepage> homepage;
    @JsonProperty("additionalNote") 
    public ArrayList<AdditionalNote> getAdditionalNote() { 
		 return this.additionalNote; } 
    public void setAdditionalNote(ArrayList<AdditionalNote> additionalNote) { 
		 this.additionalNote = additionalNote; } 
    ArrayList<AdditionalNote> additionalNote;
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
    public ArrayList<ContactPoint> getContactPoint() { 
		 return this.contactPoint; } 
    public void setContactPoint(ArrayList<ContactPoint> contactPoint) { 
		 this.contactPoint = contactPoint; } 
    ArrayList<ContactPoint> contactPoint;
    @JsonProperty("hasSubOrganization") 
    public ArrayList<HasSubOrganization> getHasSubOrganization() { 
		 return this.hasSubOrganization; } 
    public void setHasSubOrganization(ArrayList<HasSubOrganization> hasSubOrganization) { 
		 this.hasSubOrganization = hasSubOrganization; } 
    ArrayList<HasSubOrganization> hasSubOrganization;
    @JsonProperty("groupMemberOf") 
    public ArrayList<GroupMemberOf> getGroupMemberOf() { 
		 return this.groupMemberOf; } 
    public void setGroupMemberOf(ArrayList<GroupMemberOf> groupMemberOf) { 
		 this.groupMemberOf = groupMemberOf; } 
    ArrayList<GroupMemberOf> groupMemberOf;
    @JsonProperty("logo") 
    public Logo getLogo() { 
		 return this.logo; } 
    public void setLogo(Logo logo) { 
		 this.logo = logo; } 
    Logo logo;
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
