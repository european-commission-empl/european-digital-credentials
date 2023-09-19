package eu.europa.ec.empl.edci.datamodel.jsonld.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import eu.europa.ec.empl.edci.annotation.EDCIIdentifier;
import eu.europa.ec.empl.edci.datamodel.jsonld.model.base.JsonLdCommonDTO;
import eu.europa.ec.empl.edci.datamodel.jsonld.model.dataTypes.Identifier;
import eu.europa.ec.empl.edci.datamodel.jsonld.model.dataTypes.LiteralMap;
import eu.europa.ec.empl.edci.datamodel.jsonld.model.dataTypes.WebResourceDTO;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@EDCIIdentifier(prefix = "urn:epass:gradingScheme:")
public class GradingSchemeDTO extends JsonLdCommonDTO {

    private LiteralMap description;
    private List<Identifier> identifier = new ArrayList<>();
    private List<WebResourceDTO> supplementaryDocument = new ArrayList<>();
    private LiteralMap title;

    public GradingSchemeDTO() {
        super();
    }

    @JsonCreator
    public GradingSchemeDTO(String uri) {
        super(uri);
    }

    public LiteralMap getDescription() {
        return description;
    }

    public void setDescription(LiteralMap description) {
        this.description = description;
    }

    public List<Identifier> getIdentifier() {
        return identifier;
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

    public void setIdentifier(List<Identifier> identifier) {
        this.identifier = identifier;
    }

    public void setSupplementaryDocument(List<WebResourceDTO> supplementaryDocument) {
        this.supplementaryDocument = supplementaryDocument;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GradingSchemeDTO)) return false;
        if (!super.equals(o)) return false;
        GradingSchemeDTO that = (GradingSchemeDTO) o;
        return Objects.equals(description, that.description) &&
                Objects.equals(identifier, that.identifier) &&
                Objects.equals(supplementaryDocument, that.supplementaryDocument) &&
                Objects.equals(title, that.title);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), description, identifier, supplementaryDocument, title);
    }
}
