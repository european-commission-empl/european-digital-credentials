package eu.europa.ec.empl.edci.model.qmsaccreditation;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;

public class Metadata{
    @JsonProperty("language") 
    public Language getLanguage() { 
		 return this.language; } 
    public void setLanguage(Language language) { 
		 this.language = language; } 
    Language language;
    @JsonProperty("availableLanguages") 
    public ArrayList<AvailableLanguage> getAvailableLanguages() { 
		 return this.availableLanguages; } 
    public void setAvailableLanguages(ArrayList<AvailableLanguage> availableLanguages) { 
		 this.availableLanguages = availableLanguages; } 
    ArrayList<AvailableLanguage> availableLanguages;
}
