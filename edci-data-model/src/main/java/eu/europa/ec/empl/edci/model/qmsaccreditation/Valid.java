package eu.europa.ec.empl.edci.model.qmsaccreditation;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;

public class Valid{
    @JsonProperty("value") 
    public Date getValue() {
		 return this.value; } 
    public void setValue(Date value) { 
		 this.value = value; } 
    Date value;
    @JsonProperty("label") 
    public String getLabel() { 
		 return this.label; } 
    public void setLabel(String label) { 
		 this.label = label; } 
    String label;
}
