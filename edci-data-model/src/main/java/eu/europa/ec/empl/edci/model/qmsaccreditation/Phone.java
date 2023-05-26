package eu.europa.ec.empl.edci.model.qmsaccreditation;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;

public class Phone{
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
    @JsonProperty("phoneNumber") 
    public String getPhoneNumber() { 
		 return this.phoneNumber; } 
    public void setPhoneNumber(String phoneNumber) { 
		 this.phoneNumber = phoneNumber; } 
    String phoneNumber;
    @JsonProperty("countryDialing") 
    public String getCountryDialing() { 
		 return this.countryDialing; } 
    public void setCountryDialing(String countryDialing) { 
		 this.countryDialing = countryDialing; } 
    String countryDialing;
    @JsonProperty("areaDialing") 
    public String getAreaDialing() { 
		 return this.areaDialing; } 
    public void setAreaDialing(String areaDialing) { 
		 this.areaDialing = areaDialing; } 
    String areaDialing;
    @JsonProperty("dialNumber") 
    public String getDialNumber() { 
		 return this.dialNumber; } 
    public void setDialNumber(String dialNumber) { 
		 this.dialNumber = dialNumber; } 
    String dialNumber;
}
