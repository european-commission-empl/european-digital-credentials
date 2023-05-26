package eu.europa.ec.empl.edci.model.qmsaccreditation;

import com.fasterxml.jackson.annotation.JsonProperty;

public class HasSubOrganization{
    @JsonProperty("uri") 
    public String getUri() { 
		 return this.uri; } 
    public void setUri(String uri) { 
		 this.uri = uri; } 
    String uri;
    @JsonProperty("legalName") 
    public String getLegalName() { 
		 return this.legalName; } 
    public void setLegalName(String legalName) { 
		 this.legalName = legalName; } 
    String legalName;
}
