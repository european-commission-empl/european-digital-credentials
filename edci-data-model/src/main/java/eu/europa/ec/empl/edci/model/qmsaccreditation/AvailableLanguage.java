package eu.europa.ec.empl.edci.model.qmsaccreditation;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AvailableLanguage{
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
