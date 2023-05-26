package eu.europa.ec.empl.edci.model.view.tabs;

import eu.europa.ec.empl.edci.model.view.base.AgentView;
import eu.europa.ec.empl.edci.model.view.base.ClaimView;
import eu.europa.ec.empl.edci.model.view.base.ITabView;
import eu.europa.ec.empl.edci.model.view.fields.LearningOpportunityFieldView;
import eu.europa.ec.empl.edci.model.view.fields.LocationFieldView;
import eu.europa.ec.empl.edci.model.view.fields.PeriodOfTimeFieldView;

import java.util.List;
import java.util.Objects;

public class ActivityTabView extends ClaimView implements ITabView {

    private String id;
    private List<PeriodOfTimeFieldView> temporal;
    private String workload;
    private List<AgentView> directedBy;
    private Integer levelOfCompletion;
    private List<LocationFieldView> locations;
    private List<AchievementTabView> influences;
    private List<ActivityTabView> isPartOf;
    private List<ActivityTabView> subActivities;
    private ActivitySpecTabView specifiedBy;
    private LearningOpportunityFieldView learningOpportunity;

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

    public String getWorkload() {
        return workload;
    }

    public void setWorkload(String workload) {
        this.workload = workload;
    }

    public List<AgentView> getDirectedBy() {
        return directedBy;
    }

    public void setDirectedBy(List<AgentView> directedBy) {
        this.directedBy = directedBy;
    }

    public List<LocationFieldView> getLocations() {
        return locations;
    }

    public void setLocations(List<LocationFieldView> locations) {
        this.locations = locations;
    }

    public List<AchievementTabView> getInfluences() {
        return influences;
    }

    public void setInfluences(List<AchievementTabView> influences) {
        this.influences = influences;
    }

    public List<ActivityTabView> getIsPartOf() {
        return isPartOf;
    }

    public void setIsPartOf(List<ActivityTabView> isPartOf) {
        this.isPartOf = isPartOf;
    }

    public ActivitySpecTabView getSpecifiedBy() {
        return specifiedBy;
    }

    public void setSpecifiedBy(ActivitySpecTabView specifiedBy) {
        this.specifiedBy = specifiedBy;
    }

    public List<PeriodOfTimeFieldView> getTemporal() {
        return temporal;
    }

    public void setTemporal(List<PeriodOfTimeFieldView> temporal) {
        this.temporal = temporal;
    }

    public Integer getLevelOfCompletion() {
        return levelOfCompletion;
    }

    public void setLevelOfCompletion(Integer levelOfCompletion) {
        this.levelOfCompletion = levelOfCompletion;
    }

    public List<ActivityTabView> getSubActivities() {
        return subActivities;
    }

    public void setSubActivities(List<ActivityTabView> subActivities) {
        this.subActivities = subActivities;
    }

    public LearningOpportunityFieldView getLearningOpportunity() {
        return learningOpportunity;
    }

    public void setLearningOpportunity(LearningOpportunityFieldView learningOpportunity) {
        this.learningOpportunity = learningOpportunity;
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
