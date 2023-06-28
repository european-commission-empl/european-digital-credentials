package eu.europa.ec.empl.edci.model.view.base;

import eu.europa.ec.empl.edci.model.view.fields.AwardingProcessFieldView;
import eu.europa.ec.empl.edci.model.view.fields.IdentifierFieldView;
import eu.europa.ec.empl.edci.model.view.fields.LinkFieldView;
import eu.europa.ec.empl.edci.model.view.fields.NoteFieldView;

import java.util.ArrayList;
import java.util.List;

public class ClaimView {
    private AwardingProcessFieldView awardedBy;
    private List<String> description;
    private List<IdentifierFieldView> identifier = new ArrayList<>();
    private List<NoteFieldView> additionalNote = new ArrayList<>();
    private List<LinkFieldView> supplementaryDocument = new ArrayList<>();
    private String title;
    private List<String> dcType = new ArrayList<>();

    public AwardingProcessFieldView getAwardedBy() {
        return awardedBy;
    }

    public void setAwardedBy(AwardingProcessFieldView awardedBy) {
        this.awardedBy = awardedBy;
    }

    public List<String> getDescription() {
        return description;
    }

    public void setDescription(List<String> description) {
        this.description = description;
    }

    public List<IdentifierFieldView> getIdentifier() {
        return identifier;
    }

    public void setIdentifier(List<IdentifierFieldView> identifier) {
        this.identifier = identifier;
    }

    public List<NoteFieldView> getAdditionalNote() {
        return additionalNote;
    }

    public void setAdditionalNote(List<NoteFieldView> additionalNote) {
        this.additionalNote = additionalNote;
    }

    public List<LinkFieldView> getSupplementaryDocument() {
        return supplementaryDocument;
    }

    public void setSupplementaryDocument(List<LinkFieldView> supplementaryDocument) {
        this.supplementaryDocument = supplementaryDocument;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<String> getDcType() {
        return dcType;
    }

    public void setDcType(List<String> dcType) {
        this.dcType = dcType;
    }
}
