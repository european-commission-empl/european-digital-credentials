package eu.europa.ec.empl.edci.model.qmsaccreditation;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;

public class ContactPoint{
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
    @JsonProperty("additionalNote") 
    public ArrayList<AdditionalNote> getAdditionalNote() { 
		 return this.additionalNote; } 
    public void setAdditionalNote(ArrayList<AdditionalNote> additionalNote) { 
		 this.additionalNote = additionalNote; } 
    ArrayList<AdditionalNote> additionalNote;
    @JsonProperty("description") 
    public String getDescription() { 
		 return this.description; } 
    public void setDescription(String description) { 
		 this.description = description; } 
    String description;
    @JsonProperty("address") 
    public ArrayList<Address> getAddress() { 
		 return this.address; } 
    public void setAddress(ArrayList<Address> address) { 
		 this.address = address; } 
    ArrayList<Address> address;
    @JsonProperty("phone") 
    public ArrayList<Phone> getPhone() { 
		 return this.phone; } 
    public void setPhone(ArrayList<Phone> phone) { 
		 this.phone = phone; } 
    ArrayList<Phone> phone;
    @JsonProperty("mailbox") 
    public ArrayList<String> getMailbox() { 
		 return this.mailbox; } 
    public void setMailbox(ArrayList<String> mailbox) { 
		 this.mailbox = mailbox; } 
    ArrayList<String> mailbox;
    @JsonProperty("contactForm") 
    public ArrayList<ContactForm> getContactForm() { 
		 return this.contactForm; } 
    public void setContactForm(ArrayList<ContactForm> contactForm) { 
		 this.contactForm = contactForm; } 
    ArrayList<ContactForm> contactForm;
}
