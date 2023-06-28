package eu.europa.ec.empl.edci.model.qmsaccreditation;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;

public class Location{
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
    @JsonProperty("geographicName") 
    public String getGeographicName() { 
		 return this.geographicName; } 
    public void setGeographicName(String geographicName) { 
		 this.geographicName = geographicName; } 
    String geographicName;
    @JsonProperty("spatialCode") 
    public ArrayList<SpatialCode> getSpatialCode() { 
		 return this.spatialCode; } 
    public void setSpatialCode(ArrayList<SpatialCode> spatialCode) { 
		 this.spatialCode = spatialCode; } 
    ArrayList<SpatialCode> spatialCode;
    @JsonProperty("description") 
    public String getDescription() { 
		 return this.description; } 
    public void setDescription(String description) { 
		 this.description = description; } 
    String description;
    @JsonProperty("address") 
    public ArrayList<Address> getAddress() { 
		 return this.address; } 
    public void setAddress(ArrayList<Address> address) { 
		 this.address = address; } 
    ArrayList<Address> address;
    @JsonProperty("geometry") 
    public ArrayList<Object> getGeometry() { 
		 return this.geometry; } 
    public void setGeometry(ArrayList<Object> geometry) { 
		 this.geometry = geometry; } 
    ArrayList<Object> geometry;
}
