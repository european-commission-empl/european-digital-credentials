package eu.europa.ec.empl.edci.datamodel.jsonld.model;

import eu.europa.ec.empl.edci.datamodel.jsonld.model.base.JsonLdCommonDTO;
import eu.europa.ec.empl.edci.datamodel.jsonld.model.dataTypes.*;

import javax.validation.constraints.NotNull;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public abstract class SpecificationDTO extends JsonLdCommonDTO {

    private LiteralMap altLabel;
    private LiteralMap category;
    private LiteralMap description;
    private List<WebResourceDTO> homepage = new ArrayList<>();
    private List<Identifier> identifier = new ArrayList<>();
    private ZonedDateTime dateModified;
    private List<NoteDTO> additionalNote = new ArrayList<>();
    private List<WebResourceDTO> supplementaryDocument = new ArrayList<>();
    @NotNull
    private LiteralMap title;
    private List<ConceptDTO> dcType = new ArrayList<>();

    public List<Identifier> getAllAvailableIdentifiers() {
        return new ArrayList<>(this.getIdentifier());
    }

    @Override
    public String getName() {
        return this.getNameFromFieldList(this, "title", "altLabel", "description", "identifier", "id");
    }

    public LiteralMap getAltLabel() {
        return altLabel;
    }

    public void setAltLabel(LiteralMap altLabel) {
        this.altLabel = altLabel;
    }

    public LiteralMap getCategory() {
        return category;
    }

    public void setCategory(LiteralMap category) {
        this.category = category;
    }

    public LiteralMap getDescription() {
        return description;
    }

    public void setDescription(LiteralMap description) {
        this.description = description;
    }

    public List<WebResourceDTO> getHomepage() {
        return homepage;
    }

    public List<Identifier> getIdentifier() {
        return identifier;
    }

    public ZonedDateTime getDateModified() {
        return dateModified;
    }

    public void setDateModified(ZonedDateTime dateModified) {
        this.dateModified = dateModified;
    }

    public List<NoteDTO> getAdditionalNote() {
        return additionalNote;
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

    public List<ConceptDTO> getDcType() {
        return dcType;
    }

    public void setHomepage(List<WebResourceDTO> homepage) {
        this.homepage = homepage;
    }

    public void setIdentifier(List<Identifier> identifier) {
        this.identifier = identifier;
    }

    public void setAdditionalNote(List<NoteDTO> additionalNote) {
        this.additionalNote = additionalNote;
    }

    public void setSupplementaryDocument(List<WebResourceDTO> supplementaryDocument) {
        this.supplementaryDocument = supplementaryDocument;
    }

    public void setDcType(List<ConceptDTO> dcType) {
        this.dcType = dcType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SpecificationDTO)) return false;
        if (!super.equals(o)) return false;
        SpecificationDTO that = (SpecificationDTO) o;
        return Objects.equals(altLabel, that.altLabel) &&
                Objects.equals(category, that.category) &&
                Objects.equals(description, that.description) &&
                Objects.equals(homepage, that.homepage) &&
                Objects.equals(identifier, that.identifier) &&
                Objects.equals(dateModified, that.dateModified) &&
                Objects.equals(additionalNote, that.additionalNote) &&
                Objects.equals(supplementaryDocument, that.supplementaryDocument) &&
                Objects.equals(title, that.title) &&
                Objects.equals(dcType, that.dcType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), altLabel, category, description, homepage, identifier, dateModified, additionalNote, supplementaryDocument, title, dcType);
    }
}
