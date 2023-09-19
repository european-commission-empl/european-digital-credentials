package eu.europa.ec.empl.edci.model.qmsaccreditation;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Language{
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
    @JsonProperty("value") 
    public String getValue() { 
		 return this.value; } 
    public void setValue(String value) { 
		 this.value = value; } 
    String value;
    @JsonProperty("label") 
    public String getLabel() { 
		 return this.label; } 
    public void setLabel(String label) { 
		 this.label = label; } 
    String label;
}
