package eu.europa.ec.empl.edci.issuer.web.model.dataContainers;

import eu.europa.ec.empl.edci.issuer.web.model.dataTypes.*;

import java.util.List;

public class AssessmSpecificationDCView extends DataContainerView {

    private List<IdentifierDTView> identifier; //*

    private List<CodeDTView> assessmentType; //*

    private TextDTView title; //0..1

    private List<TextDTView> alternativeLabel; //*

    private NoteDTView description; //0..1

    private List<NoteDTView> additionalNote; //*

    private List<WebDocumentDCView> homePage; //*

    private List<WebDocumentDCView> supplementaryDocument; //*

    private List<CodeDTView> language; //*

    private CodeDTView mode; //0..1

    private ScoringSchemeDTView gradingSchemes; //*

    private List<LearningSpecificationDCView> proves; //*

    public List<IdentifierDTView> getIdentifier() {
        return identifier;
    }

    public void setIdentifier(List<IdentifierDTView> identifier) {
        this.identifier = identifier;
    }

    public List<CodeDTView> getAssessmentType() {
        return assessmentType;
    }

    public void setAssessmentType(List<CodeDTView> assessmentType) {
        this.assessmentType = assessmentType;
    }

    public TextDTView getTitle() {
        return title;
    }

    public void setTitle(TextDTView title) {
        this.title = title;
    }

    public List<TextDTView> getAlternativeLabel() {
        return alternativeLabel;
    }

    public void setAlternativeLabel(List<TextDTView> alternativeLabel) {
        this.alternativeLabel = alternativeLabel;
    }

    public NoteDTView getDescription() {
        return description;
    }

    public void setDescription(NoteDTView description) {
        this.description = description;
    }

    public List<NoteDTView> getAdditionalNote() {
        return additionalNote;
    }

    public void setAdditionalNote(List<NoteDTView> additionalNote) {
        this.additionalNote = additionalNote;
    }

    public List<WebDocumentDCView> getHomePage() {
        return homePage;
    }

    public void setHomePage(List<WebDocumentDCView> homePage) {
        this.homePage = homePage;
    }

    public List<WebDocumentDCView> getSupplementaryDocument() {
        return supplementaryDocument;
    }

    public void setSupplementaryDocument(List<WebDocumentDCView> supplementaryDocument) {
        this.supplementaryDocument = supplementaryDocument;
    }

    public List<CodeDTView> getLanguage() {
        return language;
    }

    public void setLanguage(List<CodeDTView> language) {
        this.language = language;
    }

    public CodeDTView getMode() {
        return mode;
    }

    public void setMode(CodeDTView mode) {
        this.mode = mode;
    }

    public ScoringSchemeDTView getGradingSchemes() {
        return gradingSchemes;
    }

    public void setGradingSchemes(ScoringSchemeDTView gradingSchemes) {
        this.gradingSchemes = gradingSchemes;
    }

    public List<LearningSpecificationDCView> getProves() {
        return proves;
    }

    public void setProves(List<LearningSpecificationDCView> proves) {
        this.proves = proves;
    }
}