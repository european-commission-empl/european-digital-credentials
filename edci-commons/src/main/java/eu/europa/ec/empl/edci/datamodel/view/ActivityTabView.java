package eu.europa.ec.empl.edci.datamodel.view;

import java.util.List;
import java.util.Objects;

public class ActivityTabView implements ITabView {

    private String id;
    private String title;
    private List<IdentifierFieldView> identifier;
    private String description;
    private String startDate;
    private String endDate;
    private String workloadInHours;
    private OrganizationTabView directedBy;
    private List<LocationFieldView> location;
    private List<AchievementTabView> influenced;
    private List<ActivityTabView> subActivities;
    private List<NoteFieldView> moreInformation;

    private ActivitySpecTabView specifiedBy;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getWorkloadInHours() {
        return workloadInHours;
    }

    public void setWorkloadInHours(String workloadInHours) {
        this.workloadInHours = workloadInHours;
    }

    public OrganizationTabView getDirectedBy() {
        return directedBy;
    }

    public void setDirectedBy(OrganizationTabView directedBy) {
        this.directedBy = directedBy;
    }

    public List<IdentifierFieldView> getIdentifier() {
        return identifier;
    }

    public void setIdentifier(List<IdentifierFieldView> identifier) {
        this.identifier = identifier;
    }

    public List<LocationFieldView> getLocation() {
        return location;
    }

    public void setLocation(List<LocationFieldView> location) {
        this.location = location;
    }

    public List<AchievementTabView> getInfluenced() {
        return influenced;
    }

    public void setInfluenced(List<AchievementTabView> influenced) {
        this.influenced = influenced;
    }

    public List<ActivityTabView> getSubActivities() {
        return subActivities;
    }

    public void setSubActivities(List<ActivityTabView> subActivities) {
        this.subActivities = subActivities;
    }

    public List<NoteFieldView> getMoreInformation() {
        return moreInformation;
    }

    public void setMoreInformation(List<NoteFieldView> moreInformation) {
        this.moreInformation = moreInformation;
    }

    public ActivitySpecTabView getSpecifiedBy() {
        return specifiedBy;
    }

    public void setSpecifiedBy(ActivitySpecTabView specifiedBy) {
        this.specifiedBy = specifiedBy;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ActivityTabView that = (ActivityTabView) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

}
