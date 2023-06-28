package eu.europa.ec.empl.edci.model.view.fields;

import java.util.List;

public class LearningOutcomeFieldView {

    private String title;
    private String dcType;
    private String reusabilityLevel;
    private List<LinkFieldView> relatedESCOSkill; //*
    private List<LinkFieldView> relatedSkill; //*
    private List<IdentifierFieldView> identifier;
    private List<NoteFieldView> additionalNote;

    public String getDcType() {
        return dcType;
    }

    public void setDcType(String dcType) {
        this.dcType = dcType;
    }

    public String getReusabilityLevel() {
        return reusabilityLevel;
    }

    public List<LinkFieldView> getRelatedSkill() {
        return relatedSkill;
    }

    public void setRelatedSkill(List<LinkFieldView> relatedSkill) {
        this.relatedSkill = relatedSkill;
    }

    public void setReusabilityLevel(String reusabilityLevel) {
        this.reusabilityLevel = reusabilityLevel;
    }

    public List<LinkFieldView> getRelatedESCOSkill() {
        return relatedESCOSkill;
    }

    public void setRelatedESCOSkill(List<LinkFieldView> relatedESCOSkill) {
        this.relatedESCOSkill = relatedESCOSkill;
    }

    public List<IdentifierFieldView> getIdentifier() {
        return identifier;
    }

    public void setIdentifier(List<IdentifierFieldView> identifier) {
        this.identifier = identifier;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<NoteFieldView> getAdditionalNote() {
        return additionalNote;
    }

    public void setAdditionalNote(List<NoteFieldView> additionalNote) {
        this.additionalNote = additionalNote;
    }
}

