package eu.europa.ec.empl.edci.model.qmsaccreditation;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;

public class VatIdentifier{
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
    @JsonProperty("creator") 
    public String getCreator() { 
		 return this.creator; } 
    public void setCreator(String creator) { 
		 this.creator = creator; } 
    String creator;
    @JsonProperty("schemeAgency") 
    public String getSchemeAgency() { 
		 return this.schemeAgency; } 
    public void setSchemeAgency(String schemeAgency) { 
		 this.schemeAgency = schemeAgency; } 
    String schemeAgency;
    @JsonProperty("issued") 
    public Issued getIssued() { 
		 return this.issued; } 
    public void setIssued(Issued issued) { 
		 this.issued = issued; } 
    Issued issued;
    @JsonProperty("notation") 
    public String getNotation() { 
		 return this.notation; } 
    public void setNotation(String notation) { 
		 this.notation = notation; } 
    String notation;
    @JsonProperty("schemeName") 
    public String getSchemeName() { 
		 return this.schemeName; } 
    public void setSchemeName(String schemeName) { 
		 this.schemeName = schemeName; } 
    String schemeName;
    @JsonProperty("schemeVersion") 
    public String getSchemeVersion() { 
		 return this.schemeVersion; } 
    public void setSchemeVersion(String schemeVersion) { 
		 this.schemeVersion = schemeVersion; } 
    String schemeVersion;
    @JsonProperty("type") 
    public ArrayList<Type> getType() { 
		 return this.type; } 
    public void setType(ArrayList<Type> type) { 
		 this.type = type; } 
    ArrayList<Type> type;
    @JsonProperty("spatial") 
    public Spatial getSpatial() { 
		 return this.spatial; } 
    public void setSpatial(Spatial spatial) { 
		 this.spatial = spatial; } 
    Spatial spatial;
}
