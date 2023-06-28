package eu.europa.ec.empl.edci.datamodel.jsonld.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import eu.europa.ec.empl.edci.annotation.CustomizableEntityDTO;
import eu.europa.ec.empl.edci.annotation.EDCIIdentifier;
import eu.europa.ec.empl.edci.datamodel.jsonld.model.dataTypes.ConceptDTO;
import eu.europa.ec.empl.edci.datamodel.jsonld.model.dataTypes.NoteDTO;

import javax.validation.constraints.NotNull;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@EDCIIdentifier(prefix = "urn:epass:learningAssessment:")
@CustomizableEntityDTO(identifierField = "OCBID")
public class LearningAssessmentDTO extends ClaimDTO {

    @JsonIgnore
    private String OCBID;
    private List<AgentDTO> assessedBy = new ArrayList<>();
    private ZonedDateTime dateIssued;
    @NotNull
    private NoteDTO grade;
    private ConceptDTO gradeStatus;
    private List<LearningAssessmentDTO> hasPart = new ArrayList<>();
    @JsonIgnore
    private List<LearningAssessmentDTO> isPartOf = new ArrayList<>();
    private LocationDTO location;
    private ConceptDTO idVerification;
    private LearningAchievementDTO proves;
    private ResultDistributionDTO resultDistribution;
    private ShortenedGradingDTO shortenedGrading;
    private LearningAssessmentSpecificationDTO specifiedBy;

    public String getOCBID() {
        return OCBID;
    }

    public void setOCBID(String OCBID) {
        this.OCBID = OCBID;
    }
    public List<AgentDTO> getAssessedBy() {
        return assessedBy;
    }

    public ZonedDateTime getDateIssued() {
        return dateIssued;
    }

    public void setDateIssued(ZonedDateTime dateIssued) {
        this.dateIssued = dateIssued;
    }

    public NoteDTO getGrade() {
        return grade;
    }

    public void setGrade(NoteDTO grade) {
        this.grade = grade;
    }

    public ConceptDTO getGradeStatus() {
        return gradeStatus;
    }

    public void setGradeStatus(ConceptDTO gradeStatus) {
        this.gradeStatus = gradeStatus;
    }

    public List<LearningAssessmentDTO> getHasPart() {
        return hasPart;
    }

    public ConceptDTO getIdVerification() {
        return idVerification;
    }

    public void setIdVerification(ConceptDTO idVerification) {
        this.idVerification = idVerification;
    }

    public LearningAchievementDTO getProves() {
        return proves;
    }

    public void setProves(LearningAchievementDTO proves) {
        this.proves = proves;
    }

    public ResultDistributionDTO getResultDistribution() {
        return resultDistribution;
    }

    public void setResultDistribution(ResultDistributionDTO resultDistribution) {
        this.resultDistribution = resultDistribution;
    }

    public ShortenedGradingDTO getShortenedGrading() {
        return shortenedGrading;
    }

    public void setShortenedGrading(ShortenedGradingDTO shortenedGrading) {
        this.shortenedGrading = shortenedGrading;
    }

    public LearningAssessmentSpecificationDTO getSpecifiedBy() {
        return specifiedBy;
    }

    public void setSpecifiedBy(LearningAssessmentSpecificationDTO specifiedBy) {
        this.specifiedBy = specifiedBy;
    }

    public List<LearningAssessmentDTO> getIsPartOf() {
        return isPartOf;
    }

    public void setIsPartOf(List<LearningAssessmentDTO> isPartOf) {
        this.isPartOf = isPartOf;
    }

    public LocationDTO getLocation() {
        return location;
    }

    public void setLocation(LocationDTO location) {
        this.location = location;
    }

    public void setAssessedBy(List<AgentDTO> assessedBy) {
        this.assessedBy = assessedBy;
    }

    public void setHasPart(List<LearningAssessmentDTO> hasPart) {
        this.hasPart = hasPart;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LearningAssessmentDTO)) return false;
        if (!super.equals(o)) return false;
        LearningAssessmentDTO that = (LearningAssessmentDTO) o;
        return Objects.equals(assessedBy, that.assessedBy) &&
                Objects.equals(dateIssued, that.dateIssued) &&
                Objects.equals(grade, that.grade) &&
                Objects.equals(gradeStatus, that.gradeStatus) &&
                Objects.equals(hasPart, that.hasPart) &&
                Objects.equals(location, that.location) &&
                Objects.equals(idVerification, that.idVerification) &&
                Objects.equals(proves, that.proves) &&
                Objects.equals(resultDistribution, that.resultDistribution) &&
                Objects.equals(shortenedGrading, that.shortenedGrading) &&
                Objects.equals(specifiedBy, that.specifiedBy);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), assessedBy, dateIssued, grade, gradeStatus, hasPart, location, idVerification, proves, resultDistribution, shortenedGrading, specifiedBy);
    }
}
