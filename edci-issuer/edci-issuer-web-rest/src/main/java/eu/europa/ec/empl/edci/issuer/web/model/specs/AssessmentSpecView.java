package eu.europa.ec.empl.edci.issuer.web.model.specs;

import eu.europa.ec.empl.edci.issuer.web.model.SubresourcesOids;
import eu.europa.ec.empl.edci.issuer.web.model.dataContainers.AssessmSpecificationDCView;
import eu.europa.ec.empl.edci.issuer.web.model.dataTypes.*;
import eu.europa.ec.empl.edci.issuer.web.model.specs.lite.AssessmentSpecLiteView;

import javax.validation.constraints.NotNull;
import java.util.List;

public class AssessmentSpecView extends AssessmentSpecLiteView {

    private List<IdentifierDTView> identifier; //*

    @NotNull
    private TextDTView title; //1

    private TextDTView description; //0..1

    private List<NoteDTView> additionalNote; //*

    private ScoreDTView grade; //1

    private ShortenedGradingDTView shortenedGrading; //0..1

    private ResultDistributionDTView resultDistribution; //0..1

    //    @JsonFormat(pattern = EuropassConstants.DATE_ISO_8601)
    private String issuedDate; //0..1

    private CodeDTView idVerification; //0..1

    private AssessmSpecificationDCView specifiedBy; //0..1

    private SubresourcesOids relHasPart;

    private SubresourcesOids relAssessedBy;

    public SubresourcesOids getRelHasPart() {
        relHasPart = (relHasPart == null ? new SubresourcesOids() : relHasPart);
        return relHasPart;
    }

    public void setRelHasPart(SubresourcesOids relHasPart) {
        this.relHasPart = relHasPart;
    }

    public SubresourcesOids getRelAssessedBy() {
        relAssessedBy = (relAssessedBy == null ? new SubresourcesOids() : relAssessedBy);
        return relAssessedBy;
    }

    public void setRelAssessedBy(SubresourcesOids relAssessedBy) {
        this.relAssessedBy = relAssessedBy;
    }

    public List<IdentifierDTView> getIdentifier() {
        return identifier;
    }

    public void setIdentifier(List<IdentifierDTView> identifier) {
        this.identifier = identifier;
    }

    public TextDTView getTitle() {
        return title;
    }

    public void setTitle(TextDTView title) {
        this.title = title;
    }

    public TextDTView getDescription() {
        return description;
    }

    public void setDescription(TextDTView description) {
        this.description = description;
    }

    public List<NoteDTView> getAdditionalNote() {
        return additionalNote;
    }

    public void setAdditionalNote(List<NoteDTView> additionalNote) {
        this.additionalNote = additionalNote;
    }

    public ScoreDTView getGrade() {
        return grade;
    }

    public void setGrade(ScoreDTView grade) {
        this.grade = grade;
    }

    public ShortenedGradingDTView getShortenedGrading() {
        return shortenedGrading;
    }

    public void setShortenedGrading(ShortenedGradingDTView shortenedGrading) {
        this.shortenedGrading = shortenedGrading;
    }

    public ResultDistributionDTView getResultDistribution() {
        return resultDistribution;
    }

    public void setResultDistribution(ResultDistributionDTView resultDistribution) {
        this.resultDistribution = resultDistribution;
    }

    public String getIssuedDate() {
        return issuedDate;
    }

    public void setIssuedDate(String issuedDate) {
        this.issuedDate = issuedDate;
    }

    public CodeDTView getIdVerification() {
        return idVerification;
    }

    public void setIdVerification(CodeDTView idVerification) {
        this.idVerification = idVerification;
    }

    public AssessmSpecificationDCView getSpecifiedBy() {
        return specifiedBy;
    }

    public void setSpecifiedBy(AssessmSpecificationDCView specifiedBy) {
        this.specifiedBy = specifiedBy;
    }
}