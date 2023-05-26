package eu.europa.ec.empl.edci.model.qmsaccreditation;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;

public class Report{
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
    @JsonProperty("title") 
    public String getTitle() { 
		 return this.title; } 
    public void setTitle(String title) { 
		 this.title = title; } 
    String title;
    @JsonProperty("language") 
    public Language getLanguage() { 
		 return this.language; } 
    public void setLanguage(Language language) { 
		 this.language = language; } 
    Language language;
    @JsonProperty("contentUrl") 
    public String getContentUrl() { 
		 return this.contentUrl; } 
    public void setContentUrl(String contentUrl) { 
		 this.contentUrl = contentUrl; } 
    String contentUrl;
}
