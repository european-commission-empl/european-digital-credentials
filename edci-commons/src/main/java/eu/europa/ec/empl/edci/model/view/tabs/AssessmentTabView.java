package eu.europa.ec.empl.edci.model.view.tabs;

import eu.europa.ec.empl.edci.model.view.base.AgentView;
import eu.europa.ec.empl.edci.model.view.base.ClaimView;
import eu.europa.ec.empl.edci.model.view.base.ITabView;
import eu.europa.ec.empl.edci.model.view.fields.LocationFieldView;
import eu.europa.ec.empl.edci.model.view.fields.NoteFieldView;
import eu.europa.ec.empl.edci.model.view.fields.ResultDistributionFieldView;
import eu.europa.ec.empl.edci.model.view.fields.ShortenedGradingFieldView;

import java.util.List;
import java.util.Objects;

public class AssessmentTabView extends ClaimView implements ITabView {

    private String id;
    private NoteFieldView grade;
    private String gradeStatus;
    private LocationFieldView location;
    private AchievementTabView proves;
    private ResultDistributionFieldView resultDistribution;
    private ShortenedGradingFieldView shortenedGrading;
    private List<AgentView> assessedBy;
    private String idVerification;
    private String dateIssued;
    private List<AssessmentTabView> subAssessments;
    private List<AssessmentTabView> isPartOf;

    private AssessmentSpecTabView specifiedBy;


//    private Integer depth;
//
//    public Integer getDepth() {
//        return depth;
//    }
//
//    public void setDepth(Integer depth) {
//        this.depth = depth;
//    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<AgentView> getAssessedBy() {
        return assessedBy;
    }

    public void setAssessedBy(List<AgentView> assessedBy) {
        this.assessedBy = assessedBy;
    }

    public String getIdVerification() {
        return idVerification;
    }

    public void setIdVerification(String idVerification) {
        this.idVerification = idVerification;
    }

    public String getDateIssued() {
        return dateIssued;
    }

    public void setDateIssued(String dateIssued) {
        this.dateIssued = dateIssued;
    }

    public List<AssessmentTabView> getSubAssessments() {
        return subAssessments;
    }

    public void setSubAssessments(List<AssessmentTabView> subAssessments) {
        this.subAssessments = subAssessments;
    }
    
    public AssessmentSpecTabView getSpecifiedBy() {
        return specifiedBy;
    }

    public void setSpecifiedBy(AssessmentSpecTabView specifiedBy) {
        this.specifiedBy = specifiedBy;
    }

    public NoteFieldView getGrade() {
        return grade;
    }

    public void setGrade(NoteFieldView grade) {
        this.grade = grade;
    }

    public String getGradeStatus() {
        return gradeStatus;
    }

    public void setGradeStatus(String gradeStatus) {
        this.gradeStatus = gradeStatus;
    }

    public LocationFieldView getLocation() {
        return location;
    }

    public void setLocation(LocationFieldView location) {
        this.location = location;
    }

    public AchievementTabView getProves() {
        return proves;
    }

    public void setProves(AchievementTabView proves) {
        this.proves = proves;
    }

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

    public List<AssessmentTabView> getIsPartOf() {
        return isPartOf;
    }

    public void setIsPartOf(List<AssessmentTabView> isPartOf) {
        this.isPartOf = isPartOf;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AssessmentTabView that = (AssessmentTabView) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

}
