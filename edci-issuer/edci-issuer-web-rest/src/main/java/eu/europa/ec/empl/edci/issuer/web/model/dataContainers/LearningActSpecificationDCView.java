package eu.europa.ec.empl.edci.issuer.web.model.dataContainers;

import eu.europa.ec.empl.edci.issuer.web.model.dataTypes.CodeDTView;
import eu.europa.ec.empl.edci.issuer.web.model.dataTypes.IdentifierDTView;
import eu.europa.ec.empl.edci.issuer.web.model.dataTypes.NoteDTView;
import eu.europa.ec.empl.edci.issuer.web.model.dataTypes.TextDTView;

import java.util.List;
import java.util.Set;

public class LearningActSpecificationDCView extends DataContainerView {

    private Set<IdentifierDTView> identifier; //*

    private Set<CodeDTView> learningActivityType; //*

    private TextDTView title; //0..1

    private List<TextDTView> alternativeLabel; //*

    private NoteDTView description; //0..1

    private List<NoteDTView> additionalNote; //*

    private List<WebDocumentDCView> homePage; //*

    private List<WebDocumentDCView> supplementaryDocument; //*

    private String workload; //0..1

    private List<CodeDTView> language; //*

    private CodeDTView mode; //0..1


    public Set<IdentifierDTView> getIdentifier() {
        return identifier;
    }

    public void setIdentifier(Set<IdentifierDTView> identifier) {
        this.identifier = identifier;
    }

    public Set<CodeDTView> getLearningActivityType() {
        return learningActivityType;
    }

    public void setLearningActivityType(Set<CodeDTView> learningActivityType) {
        this.learningActivityType = learningActivityType;
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

    public String getWorkload() {
        return workload;
    }

    public void setWorkload(String workload) {
        this.workload = workload;
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

}