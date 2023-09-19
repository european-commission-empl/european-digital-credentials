package eu.europa.ec.empl.edci.model.qmsaccreditation;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;

public class Subject{
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
    @JsonProperty("prefLabel") 
    public String getPrefLabel() { 
		 return this.prefLabel; } 
    public void setPrefLabel(String prefLabel) { 
		 this.prefLabel = prefLabel; } 
    String prefLabel;
    @JsonProperty("altLabel") 
    public ArrayList<String> getAltLabel() { 
		 return this.altLabel; } 
    public void setAltLabel(ArrayList<String> altLabel) { 
		 this.altLabel = altLabel; } 
    ArrayList<String> altLabel;
    @JsonProperty("notation") 
    public String getNotation() { 
		 return this.notation; } 
    public void setNotation(String notation) { 
		 this.notation = notation; } 
    String notation;
    @JsonProperty("inScheme") 
    public ArrayList<String> getInScheme() { 
		 return this.inScheme; } 
    public void setInScheme(ArrayList<String> inScheme) { 
		 this.inScheme = inScheme; } 
    ArrayList<String> inScheme;
}
