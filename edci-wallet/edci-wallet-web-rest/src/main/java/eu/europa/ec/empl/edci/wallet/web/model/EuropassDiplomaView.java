package eu.europa.ec.empl.edci.wallet.web.model;

import java.util.List;
import java.util.Set;

public class EuropassDiplomaView {

    private String id;
    private List<String> html;
    private Set<String> sanitizedHtmlTags;
    private String backgroundImage;
    private String logo;

    public EuropassDiplomaView() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<String> getHtml() {
        return html;
    }

    public void setHtml(List<String> html) {
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

    public Set<String> getSanitizedHtmlTags() {
        return sanitizedHtmlTags;
    }

    public void setSanitizedHtmlTags(Set<String> sanitizedHtmlTags) {
        this.sanitizedHtmlTags = sanitizedHtmlTags;
    }
}
