package eu.europa.ec.empl.edci.model.view.base;

import eu.europa.ec.empl.edci.model.view.fields.IdentifierFieldView;
import eu.europa.ec.empl.edci.model.view.fields.LinkFieldView;
import eu.europa.ec.empl.edci.model.view.fields.NoteFieldView;

import java.util.ArrayList;
import java.util.List;

public class SpecificationView {
    private List<String> altLabel;
    private List<String> category;
    private List<String> description;
    private List<LinkFieldView> homepage = new ArrayList<>();
    private List<IdentifierFieldView> identifier = new ArrayList<>();
    private String dateModified;
    private List<NoteFieldView> additionalNote = new ArrayList<>();
    private List<LinkFieldView> supplementaryDocument = new ArrayList<>();
    private String title;
    private List<String> dcType = new ArrayList<>();


    public List<String> getDescription() {
        return description;
    }

    public void setDescription(List<String> description) {
        this.description = description;
    }

    public List<String> getAltLabel() {
        return altLabel;
    }

    public void setAltLabel(List<String> altLabel) {
        this.altLabel = altLabel;
    }

    public void setCategory(List<String> category) {
        this.category = category;
    }

    public void setHomepage(List<LinkFieldView> homepage) {
        this.homepage = homepage;
    }

    public void setIdentifier(List<IdentifierFieldView> identifier) {
        this.identifier = identifier;
    }

    public void setDateModified(String dateModified) {
        this.dateModified = dateModified;
    }

    public void setAdditionalNote(List<NoteFieldView> additionalNote) {
        this.additionalNote = additionalNote;
    }

    public void setSupplementaryDocument(List<LinkFieldView> supplementaryDocument) {
        this.supplementaryDocument = supplementaryDocument;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDcType(List<String> dcType) {
        this.dcType = dcType;
    }

    public List<String> getCategory() {
        return category;
    }

    public List<LinkFieldView> getHomepage() {
        return homepage;
    }

    public List<IdentifierFieldView> getIdentifier() {
        return identifier;
    }

    public String getDateModified() {
        return dateModified;
    }

    public List<NoteFieldView> getAdditionalNote() {
        return additionalNote;
    }

    public List<LinkFieldView> getSupplementaryDocument() {
        return supplementaryDocument;
    }

    public String getTitle() {
        return title;
    }

    public List<String> getDcType() {
        return dcType;
    }
}
