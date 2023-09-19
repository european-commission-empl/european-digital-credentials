package eu.europa.ec.empl.edci.model.qmsaccreditation;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;

public class Logo{
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
    @JsonProperty("description") 
    public String getDescription() { 
		 return this.description; } 
    public void setDescription(String description) { 
		 this.description = description; } 
    String description;
    @JsonProperty("contentType") 
    public ContentType getContentType() { 
		 return this.contentType; } 
    public void setContentType(ContentType contentType) { 
		 this.contentType = contentType; } 
    ContentType contentType;
    @JsonProperty("contentEncoding") 
    public ContentEncoding getContentEncoding() { 
		 return this.contentEncoding; } 
    public void setContentEncoding(ContentEncoding contentEncoding) { 
		 this.contentEncoding = contentEncoding; } 
    ContentEncoding contentEncoding;
    @JsonProperty("contentSize") 
    public double getContentSize() { 
		 return this.contentSize; } 
    public void setContentSize(double contentSize) { 
		 this.contentSize = contentSize; } 
    double contentSize;
    @JsonProperty("content") 
    public String getContent() { 
		 return this.content; } 
    public void setContent(String content) { 
		 this.content = content; } 
    String content;
}
