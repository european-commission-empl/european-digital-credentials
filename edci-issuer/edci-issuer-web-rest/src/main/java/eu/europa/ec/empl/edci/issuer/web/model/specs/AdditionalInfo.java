package eu.europa.ec.empl.edci.issuer.web.model.specs;

import java.util.Set;

public class AdditionalInfo {

    //    @JsonFormat(pattern = EuropassConstants.DATE_ISO_8601)
    private String createdOn; //1

    //    @JsonFormat(pattern = EuropassConstants.DATE_ISO_8601)
    private String updatedOn; //1

    private Set<String> languages; //1..*

    public Set<String> getLanguages() {
        return languages;
    }

    public void setLanguages(Set<String> languages) {
        this.languages = languages;
    }

    public String getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(String createdOn) {
        this.createdOn = createdOn;
    }

    public String getUpdatedOn() {
        return updatedOn;
    }

    public void setUpdatedOn(String updatedOn) {
        this.updatedOn = updatedOn;
    }

}