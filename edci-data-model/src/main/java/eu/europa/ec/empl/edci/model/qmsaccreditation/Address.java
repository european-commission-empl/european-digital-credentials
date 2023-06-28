package eu.europa.ec.empl.edci.model.qmsaccreditation;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;

public class Address{
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
    @JsonProperty("fullAddress") 
    public FullAddress getFullAddress() { 
		 return this.fullAddress; } 
    public void setFullAddress(FullAddress fullAddress) { 
		 this.fullAddress = fullAddress; } 
    FullAddress fullAddress;
    @JsonProperty("countryCode") 
    public CountryCode getCountryCode() { 
		 return this.countryCode; } 
    public void setCountryCode(CountryCode countryCode) { 
		 this.countryCode = countryCode; } 
    CountryCode countryCode;
}
