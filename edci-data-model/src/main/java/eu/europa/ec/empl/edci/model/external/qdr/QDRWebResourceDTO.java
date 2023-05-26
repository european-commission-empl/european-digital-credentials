package eu.europa.ec.empl.edci.model.external.qdr;

import eu.europa.ec.empl.edci.annotation.EDCIIdentifier;
import eu.europa.ec.empl.edci.annotation.MandatoryConceptScheme;

import javax.validation.constraints.NotNull;
import java.net.URI;
import java.util.Objects;

@EDCIIdentifier(prefix = "urn:epass:webResource:")
public class QDRWebResourceDTO extends QDRJsonLdCommonDTO {

    @NotNull
    private URI contentUrl;
    @MandatoryConceptScheme("http://publications.europa.eu/resource/authority/language")
    private QDRConceptDTO language;
    private String title;

    public QDRConceptDTO getLanguage() {
        return language;
    }

    public void setLanguage(QDRConceptDTO language) {
        this.language = language;
    }

    public URI getContentUrl() {
        return contentUrl;
    }

    public void setContentUrl(URI contentUrl) {
        this.contentUrl = contentUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String toString() {
        return contentUrl.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof QDRWebResourceDTO)) return false;
        if (!super.equals(o)) return false;
        QDRWebResourceDTO that = (QDRWebResourceDTO) o;
        return Objects.equals(contentUrl, that.contentUrl) &&
                Objects.equals(language, that.language) &&
                Objects.equals(title, that.title);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), contentUrl, language, title);
    }
}
