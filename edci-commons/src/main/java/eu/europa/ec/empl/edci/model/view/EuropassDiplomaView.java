package eu.europa.ec.empl.edci.model.view;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.net.URI;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class EuropassDiplomaView {
    private URI id;
    private List<String> base64DiplomaImages;
    private String expirationDate;
    private String logo;
    private List<String> availableLanguages;

    public EuropassDiplomaView() {
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

    public String getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(String expirationDate) {
        this.expirationDate = expirationDate;
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
}
