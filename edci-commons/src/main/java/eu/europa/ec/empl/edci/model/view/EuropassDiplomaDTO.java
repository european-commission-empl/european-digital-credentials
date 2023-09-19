package eu.europa.ec.empl.edci.model.view;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.net.URI;
import java.util.Date;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class EuropassDiplomaDTO {
    private URI id;
    private List<String> base64DiplomaImages;
    private String logo;
    private List<String> availableLanguages;
    private Date expirationDate;

    public EuropassDiplomaDTO() {
    }

    public EuropassDiplomaDTO(List<String> base64DiplomaImages, String logo) {
        this.setBase64DiplomaImages(base64DiplomaImages);
        this.setLogo(logo);
    }

    public URI getId() {
        return id;
    }

    public void setId(URI id) {
        this.id = id;
    }

    public List<String> getBase64DiplomaImages() {
        return base64DiplomaImages;
    }

    public void setBase64DiplomaImages(List<String> base64DiplomaImages) {
        this.base64DiplomaImages = base64DiplomaImages;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public List<String> getAvailableLanguages() {
        return availableLanguages;
    }

    public void setAvailableLanguages(List<String> availableLanguages) {
        this.availableLanguages = availableLanguages;
    }

    public Date getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(Date expirationDate) {
        this.expirationDate = expirationDate;
    }
}
