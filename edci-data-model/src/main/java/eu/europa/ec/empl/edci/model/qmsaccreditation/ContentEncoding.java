package eu.europa.ec.empl.edci.model.qmsaccreditation;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ContentEncoding{
    @JsonProperty("uri") 
    public String getUri() { 
		 return this.uri; } 
    public void setUri(String uri) { 
		 this.uri = uri; } 
    String uri;
    @JsonProperty("prefLabel") 
    public String getPrefLabel() { 
		 return this.prefLabel; } 
    public void setPrefLabel(String prefLabel) { 
		 this.prefLabel = prefLabel; } 
    String prefLabel;
}
