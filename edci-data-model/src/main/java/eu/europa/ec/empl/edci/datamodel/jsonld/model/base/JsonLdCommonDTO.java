package eu.europa.ec.empl.edci.datamodel.jsonld.model.base;

import com.apicatalog.jsonld.lang.BlankNode;
import com.apicatalog.jsonld.uri.UriUtils;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import eu.europa.ec.empl.edci.constants.ErrorCode;
import eu.europa.ec.empl.edci.exception.EDCIException;

import javax.validation.constraints.NotNull;
import java.net.URI;
import java.util.Objects;
import java.util.UUID;

@JsonTypeInfo(use = JsonTypeInfo.Id.NONE, property = "type", visible = true)
public class JsonLdCommonDTO implements Identifiable {

    @NotNull
    public URI id;
    public String type;

    public JsonLdCommonDTO() {
        type = this.getClass().getSimpleName().replaceAll("DTO$", "");
    }

    public URI getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public void setType() {
    }

    @JsonCreator
    public JsonLdCommonDTO(String uri) {
        type = this.getClass().getSimpleName().replaceAll("DTO$", "");
        this.setId(URI.create(uri));
    }

    @JsonProperty
     /*
        Validate URIs to only accept well-formed subjects
        Validation extracted from JsonLdToRdf.class at com.apicatalog.jsonld.deseralization    
      */
    public void setId(URI id) {
        boolean valid = false;

        if (id == null) {
            id = URI.create(getIdPrefix(this).concat(UUID.randomUUID().toString()));
        } else {
            String subject = id.toString();
            if (BlankNode.isWellFormed(subject)) {
                valid = true;
            }

            if (!valid && UriUtils.isAbsoluteUri(subject, true)) {
                valid = true;
            }

            if (!valid) {
                if ("_blank".equals(id.toString())) {
                    id = URI.create(getIdPrefix(this).concat(UUID.randomUUID().toString()));
                } else {
                    throw new EDCIException(ErrorCode.CREDENTIAL_MALFORMED_SUBJECT, subject);
                }
            }
        }

        this.id = id;
    }

    @Override
    public String getName() {
        return this.getId().toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof JsonLdCommonDTO)) return false;
        JsonLdCommonDTO that = (JsonLdCommonDTO) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }


}
