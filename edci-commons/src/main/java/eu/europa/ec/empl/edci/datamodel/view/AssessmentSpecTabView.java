package eu.europa.ec.empl.edci.datamodel.view;

import java.util.List;

public class AssessmentSpecTabView implements ITabView {

    private List<LinkFieldView> homePage;
    private String title;
    private List<IdentifierFieldView> identifier;
    private String description;
    private List<String> type;
    private String mode;
    private List<String> language;
    private List<LinkFieldView> supplementaryDocument;
    private List<NoteFieldView> moreInformation;

    private ScoringSchemeFieldView gradingScheme; //*

    private ResultDistributionFieldView resultDistribution;

    private ShortenedGradingFieldView shortenedGrading;

    public ResultDistributionFieldView getResultDistribution() {
        return resultDistribution;
    }

    public void setResultDistribution(ResultDistributionFieldView resultDistribution) {
        this.resultDistribution = resultDistribution;
    }

    public ShortenedGradingFieldView getShortenedGrading() {
        return shortenedGrading;
    }

    public void setShortenedGrading(ShortenedGradingFieldView shortenedGrading) {
        this.shortenedGrading = shortenedGrading;
    }

    public List<LinkFieldView> getHomePage() {
        return homePage;
    }

    public void setHomePage(List<LinkFieldView> homePage) {
        this.homePage = homePage;
    }

    public void setType(List<String> type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<IdentifierFieldView> getIdentifier() {
        return identifier;
    }

    public void setIdentifier(List<IdentifierFieldView> identifier) {
        this.identifier = identifier;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public List<String> getLanguage() {
        return language;
    }

    public void setLanguage(List<String> language) {
        this.language = language;
    }

    public List<LinkFieldView> getSupplementaryDocument() {
        return supplementaryDocument;
    }

    public void setSupplementaryDocument(List<LinkFieldView> supplementaryDocument) {
        this.supplementaryDocument = supplementaryDocument;
    }

    public List<NoteFieldView> getMoreInformation() {
        return moreInformation;
    }

    public void setMoreInformation(List<NoteFieldView> moreInformation) {
        this.moreInformation = moreInformation;
    }

    public ScoringSchemeFieldView getGradingScheme() {
        return gradingScheme;
    }

    public void setGradingScheme(ScoringSchemeFieldView gradingScheme) {
        this.gradingScheme = gradingScheme;
    }

    public List<String> getType() {
        return type;
    }
}
