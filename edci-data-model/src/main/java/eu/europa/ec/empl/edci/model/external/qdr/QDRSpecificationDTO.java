package eu.europa.ec.empl.edci.model.external.qdr;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public abstract class QDRSpecificationDTO extends QDRJsonLdCommonDTO {

    private String altLabel;
    private String category;
    private String description;
    private List<QDRWebResourceDTO> homepage = new ArrayList<>();
    private List<QDRIdentifier> identifier = new ArrayList<>();
    private QDRValue modified;
    private List<QDRNoteDTO> additionalNote = new ArrayList<>();
    private List<QDRWebResourceDTO> supplementaryDocument = new ArrayList<>();
    @NotNull
    private String title;
    private List<QDRConceptDTO> type = new ArrayList<>();

    public List<QDRIdentifier> getAllAvailableQDRIdentifiers() {
        return new ArrayList<>(this.getQDRIdentifier());
    }

    public String getAltLabel() {
        return altLabel;
    }

    public void setAltLabel(String altLabel) {
        this.altLabel = altLabel;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<QDRWebResourceDTO> getHomepage() {
        return homepage;
    }

    public List<QDRIdentifier> getQDRIdentifier() {
        return identifier;
    }

    public QDRValue getModified() {
        return modified;
    }

    public void setModified(QDRValue modified) {
        this.modified = modified;
    }

    public List<QDRNoteDTO> getAdditionalNote() {
        return additionalNote;
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

    public List<QDRConceptDTO> getType() {
        return type;
    }

    public void setHomepage(List<QDRWebResourceDTO> homepage) {
        this.homepage = homepage;
    }

    public void setQDRIdentifier(List<QDRIdentifier> identifier) {
        this.identifier = identifier;
    }

    public void setAdditionalNote(List<QDRNoteDTO> additionalNote) {
        this.additionalNote = additionalNote;
    }

    public void setSupplementaryDocument(List<QDRWebResourceDTO> supplementaryDocument) {
        this.supplementaryDocument = supplementaryDocument;
    }

    public void setType(List<QDRConceptDTO> type) {
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof QDRSpecificationDTO)) return false;
        if (!super.equals(o)) return false;
        QDRSpecificationDTO that = (QDRSpecificationDTO) o;
        return Objects.equals(altLabel, that.altLabel) &&
                Objects.equals(category, that.category) &&
                Objects.equals(description, that.description) &&
                Objects.equals(homepage, that.homepage) &&
                Objects.equals(identifier, that.identifier) &&
                Objects.equals(modified, that.modified) &&
                Objects.equals(additionalNote, that.additionalNote) &&
                Objects.equals(supplementaryDocument, that.supplementaryDocument) &&
                Objects.equals(title, that.title) &&
                Objects.equals(type, that.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), altLabel, category, description, homepage, identifier, modified, additionalNote, supplementaryDocument, title, type);
    }
}
