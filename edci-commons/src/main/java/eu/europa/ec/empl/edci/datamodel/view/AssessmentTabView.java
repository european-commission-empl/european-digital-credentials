package eu.europa.ec.empl.edci.datamodel.view;

import java.util.List;
import java.util.Objects;

public class AssessmentTabView implements ITabView {

    private String id;
    private String title;
    private String grade;
    private List<IdentifierFieldView> identifier;
    private OrganizationTabView conductedBy;
    private String idVerification;
    private String description;
    private String issuedDate;
    private List<AssessmentTabView> subAssessments;
    private List<NoteFieldView> moreInformation;

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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public List<IdentifierFieldView> getIdentifier() {
        return identifier;
    }

    public void setIdentifier(List<IdentifierFieldView> identifier) {
        this.identifier = identifier;
    }

    public OrganizationTabView getConductedBy() {
        return conductedBy;
    }

    public void setConductedBy(OrganizationTabView conductedBy) {
        this.conductedBy = conductedBy;
    }

    public String getIdVerification() {
        return idVerification;
    }

    public void setIdVerification(String idVerification) {
        this.idVerification = idVerification;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getIssuedDate() {
        return issuedDate;
    }

    public void setIssuedDate(String issuedDate) {
        this.issuedDate = issuedDate;
    }

    public List<AssessmentTabView> getSubAssessments() {
        return subAssessments;
    }

    public void setSubAssessments(List<AssessmentTabView> subAssessments) {
        this.subAssessments = subAssessments;
    }

    public List<NoteFieldView> getMoreInformation() {
        return moreInformation;
    }

    public void setMoreInformation(List<NoteFieldView> moreInformation) {
        this.moreInformation = moreInformation;
    }

    public AssessmentSpecTabView getSpecifiedBy() {
        return specifiedBy;
    }

    public void setSpecifiedBy(AssessmentSpecTabView specifiedBy) {
        this.specifiedBy = specifiedBy;
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
