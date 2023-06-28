package eu.europa.ec.empl.edci.datamodel.jsonld.model.dataTypes;

import eu.europa.ec.empl.edci.annotation.EDCIIdentifier;
import eu.europa.ec.empl.edci.datamodel.jsonld.model.base.JsonLdCommonDTO;

import javax.validation.constraints.NotNull;
import java.net.URI;
import java.util.Objects;

@EDCIIdentifier(prefix = "urn:epass:webResource:")
public class WebResourceDTO extends JsonLdCommonDTO {

    @NotNull
    private URI contentURL;
    private ConceptDTO language;
    private LiteralMap title;

    public ConceptDTO getLanguage() {
        return language;
    }

    public void setLanguage(ConceptDTO language) {
        this.language = language;
    }

    public URI getContentURL() {
        return contentURL;
    }

    public void setContentURL(URI contentURL) {
        this.contentURL = contentURL;
    }

    public LiteralMap getTitle() {
        return title;
    }

    public void setTitle(LiteralMap title) {
        this.title = title;
    }

    @Override
    public String toString() {
        return contentURL.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof WebResourceDTO)) return false;
        if (!super.equals(o)) return false;
        WebResourceDTO that = (WebResourceDTO) o;
        return Objects.equals(contentURL, that.contentURL) &&
                Objects.equals(language, that.language) &&
                Objects.equals(title, that.title);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), contentURL, language, title);
    }
}
