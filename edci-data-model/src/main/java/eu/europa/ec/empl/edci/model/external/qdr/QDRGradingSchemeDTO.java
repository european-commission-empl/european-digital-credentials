package eu.europa.ec.empl.edci.model.external.qdr;

import eu.europa.ec.empl.edci.annotation.EDCIIdentifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@EDCIIdentifier(prefix = "urn:epass:gradingScheme:")
public class QDRGradingSchemeDTO extends QDRJsonLdCommonDTO {

    private String description;
    private List<QDRIdentifier> identifier = new ArrayList<>();
    private List<QDRWebResourceDTO> supplementaryDocument = new ArrayList<>();
    private String title;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<QDRIdentifier> getQDRIdentifier() {
        return identifier;
    }

    public List<QDRWebResourceDTO> getSupplementaryDocument() {
        return supplementaryDocument;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setQDRIdentifier(List<QDRIdentifier> identifier) {
        this.identifier = identifier;
    }

    public void setSupplementaryDocument(List<QDRWebResourceDTO> supplementaryDocument) {
        this.supplementaryDocument = supplementaryDocument;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof QDRGradingSchemeDTO)) return false;
        if (!super.equals(o)) return false;
        QDRGradingSchemeDTO that = (QDRGradingSchemeDTO) o;
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
