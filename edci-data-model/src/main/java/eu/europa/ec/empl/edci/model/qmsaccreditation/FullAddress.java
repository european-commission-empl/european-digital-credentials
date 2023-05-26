package eu.europa.ec.empl.edci.model.qmsaccreditation;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;

public class FullAddress{
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
    @JsonProperty("subject") 
    public Subject getSubject() { 
		 return this.subject; } 
    public void setSubject(Subject subject) { 
		 this.subject = subject; } 
    Subject subject;
    @JsonProperty("noteFormat") 
    public NoteFormat getNoteFormat() { 
		 return this.noteFormat; } 
    public void setNoteFormat(NoteFormat noteFormat) { 
		 this.noteFormat = noteFormat; } 
    NoteFormat noteFormat;
    @JsonProperty("noteLiteral") 
    public String getNoteLiteral() { 
		 return this.noteLiteral; } 
    public void setNoteLiteral(String noteLiteral) { 
		 this.noteLiteral = noteLiteral; } 
    String noteLiteral;
}
