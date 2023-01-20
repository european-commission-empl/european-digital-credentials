package eu.europa.ec.empl.edci.datamodel.view;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.net.URI;
import java.util.Date;
import java.util.List;
import java.util.Set;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class EuropassDiplomaDTO {
    private URI id;
    private List<byte[]> html;
    private Set<String> sanitizedHtmlTags;
    private String backgroundImage;
    private String logo;
    private String type;
    private Date expirationDate;
    private String primaryLanguage;
    private List<String> availableLanguages;

    public EuropassDiplomaDTO() {
    }

    public EuropassDiplomaDTO(List<byte[]> html, String backgroundImage, String logo) {
        this.setHtml(html);
        this.setBackgroundImage(backgroundImage);
        this.setLogo(logo);
    }

    public URI getId() {
        return id;
    }

    public void setId(URI id) {
        this.id = id;
    }

    public List<byte[]> getHtml() {
        return html;
    }

    public void setHtml(List<byte[]> html) {
        this.html = html;
    }

    public String getBackgroundImage() {
        return backgroundImage;
    }

    public void setBackgroundImage(String backgroundImage) {
        this.backgroundImage = backgroundImage;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public Date getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(Date expirationDate) {
        this.expirationDate = expirationDate;
    }

    public String getPrimaryLanguage() {
        return primaryLanguage;
    }

    public void setPrimaryLanguage(String primaryLanguage) {
        this.primaryLanguage = primaryLanguage;
    }

    public List<String> getAvailableLanguages() {
        return availableLanguages;
    }

    public void setAvailableLanguages(List<String> availableLanguages) {
        this.availableLanguages = availableLanguages;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Set<String> getSanitizedHtmlTags() {
        return sanitizedHtmlTags;
    }

    public void setSanitizedHtmlTags(Set<String> sanitizedHtmlTags) {
        this.sanitizedHtmlTags = sanitizedHtmlTags;
    }
}
