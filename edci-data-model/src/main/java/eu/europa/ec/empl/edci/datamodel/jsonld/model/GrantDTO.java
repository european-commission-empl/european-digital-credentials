package eu.europa.ec.empl.edci.datamodel.jsonld.model;

import eu.europa.ec.empl.edci.annotation.EDCIIdentifier;
import eu.europa.ec.empl.edci.datamodel.jsonld.model.base.JsonLdCommonDTO;
import eu.europa.ec.empl.edci.datamodel.jsonld.model.dataTypes.ConceptDTO;
import eu.europa.ec.empl.edci.datamodel.jsonld.model.dataTypes.LiteralMap;
import eu.europa.ec.empl.edci.datamodel.jsonld.model.dataTypes.WebResourceDTO;

import javax.validation.constraints.NotNull;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@EDCIIdentifier(prefix = "urn:epass:grant:")
public class GrantDTO extends JsonLdCommonDTO {

    private URI contentUrl;
    private LiteralMap description;
    private List<WebResourceDTO> supplementaryDocument = new ArrayList<>();
    @NotNull
    private LiteralMap title;
    private ConceptDTO dcType;

    public URI getContentUrl() {
        return contentUrl;
    }

    public void setContentUrl(URI contentUrl) {
        this.contentUrl = contentUrl;
    }

    public LiteralMap getDescription() {
        return description;
    }

    public void setDescription(LiteralMap description) {
        this.description = description;
    }

    public List<WebResourceDTO> getSupplementaryDocument() {
        return supplementaryDocument;
    }

    public LiteralMap getTitle() {
        return title;
    }

    public void setTitle(LiteralMap title) {
        this.title = title;
    }

    public ConceptDTO getDcType() {
        return dcType;
    }

    public void setDcType(ConceptDTO dcType) {
        this.dcType = dcType;
    }

    public void setSupplementaryDocument(List<WebResourceDTO> supplementaryDocument) {
        this.supplementaryDocument = supplementaryDocument;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GrantDTO)) return false;
        if (!super.equals(o)) return false;
        GrantDTO grantDTO = (GrantDTO) o;
        return Objects.equals(contentUrl, grantDTO.contentUrl) &&
                Objects.equals(description, grantDTO.description) &&
                Objects.equals(supplementaryDocument, grantDTO.supplementaryDocument) &&
                Objects.equals(title, grantDTO.title) &&
                Objects.equals(dcType, grantDTO.dcType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), contentUrl, description, supplementaryDocument, title, dcType);
    }
}
