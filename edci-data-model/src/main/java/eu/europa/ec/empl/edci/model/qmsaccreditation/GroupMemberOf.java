package eu.europa.ec.empl.edci.model.qmsaccreditation;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;

public class GroupMemberOf{
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
    @JsonProperty("prefLabel") 
    public String getPrefLabel() { 
		 return this.prefLabel; } 
    public void setPrefLabel(String prefLabel) { 
		 this.prefLabel = prefLabel; } 
    String prefLabel;
    @JsonProperty("altLabel") 
    public ArrayList<String> getAltLabel() { 
		 return this.altLabel; } 
    public void setAltLabel(ArrayList<String> altLabel) { 
		 this.altLabel = altLabel; } 
    ArrayList<String> altLabel;
    @JsonProperty("type") 
    public ArrayList<Type> getType() { 
		 return this.type; } 
    public void setType(ArrayList<Type> type) { 
		 this.type = type; } 
    ArrayList<Type> type;
    @JsonProperty("additionalNote") 
    public ArrayList<AdditionalNote> getAdditionalNote() { 
		 return this.additionalNote; } 
    public void setAdditionalNote(ArrayList<AdditionalNote> additionalNote) { 
		 this.additionalNote = additionalNote; } 
    ArrayList<AdditionalNote> additionalNote;
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
    @JsonProperty("member") 
    public ArrayList<String> getMember() { 
		 return this.member; } 
    public void setMember(ArrayList<String> member) { 
		 this.member = member; } 
    ArrayList<String> member;
}
