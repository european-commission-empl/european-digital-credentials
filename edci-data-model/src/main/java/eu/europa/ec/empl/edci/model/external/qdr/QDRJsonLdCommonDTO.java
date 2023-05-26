package eu.europa.ec.empl.edci.model.external.qdr;

import com.fasterxml.jackson.annotation.JsonProperty;
import eu.europa.ec.empl.edci.datamodel.jsonld.model.base.JsonLdCommonDTO;

import javax.validation.constraints.NotNull;
import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class QDRJsonLdCommonDTO {
    @NotNull
    public URI uri;
    public List<String> rdfType;

    public QDRJsonLdCommonDTO() {
        rdfType = Arrays.asList(this.getClass().getSimpleName().replaceAll("DTO$", ""));
    }

    public URI getUri() {
        return uri;
    }

    public List<String> getRdfType() {
        return rdfType;
    }

    public void setRdfType() {
    }

    @JsonProperty
    public void setUri(URI uri) {
        this.uri = uri;
    }

    public String getName() {
        return this.getUri().toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof JsonLdCommonDTO)) return false;
        JsonLdCommonDTO that = (JsonLdCommonDTO) o;
        return Objects.equals(uri, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uri);
    }


}
