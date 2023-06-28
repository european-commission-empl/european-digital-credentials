package eu.europa.ec.empl.edci.model.view.tabs;

import eu.europa.ec.empl.edci.model.view.base.ClaimView;
import eu.europa.ec.empl.edci.model.view.base.ITabView;
import eu.europa.ec.empl.edci.model.view.fields.CreditPointFieldView;
import eu.europa.ec.empl.edci.model.view.fields.LearningOpportunityFieldView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class AchievementTabView extends ClaimView implements ITabView {

    private String id;
    private List<AssessmentTabView> provenBy; //wasDerivedFrom
    private List<ActivityTabView> influencedBy;
    private List<EntitlementTabView> entitlesTo;
    private List<AchievementTabView> subAchievements;
    private List<AchievementTabView> isPartOf;
    private LearningOpportunityFieldView learningOpportunity;
    private AchievementSpecTabView specifiedBy;
    private List<CreditPointFieldView> receivedCredit = new ArrayList<>();


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

    public AchievementSpecTabView getSpecifiedBy() {
        return specifiedBy;
    }

    public void setSpecifiedBy(AchievementSpecTabView specifiedBy) {
        this.specifiedBy = specifiedBy;
    }

    public List<AssessmentTabView> getProvenBy() {
        return provenBy;
    }

    public void setProvenBy(List<AssessmentTabView> provenBy) {
        this.provenBy = provenBy;
    }

    public List<ActivityTabView> getInfluencedBy() {
        return influencedBy;
    }

    public void setInfluencedBy(List<ActivityTabView> influencedBy) {
        this.influencedBy = influencedBy;
    }

    public List<EntitlementTabView> getEntitlesTo() {
        return entitlesTo;
    }

    public void setEntitlesTo(List<EntitlementTabView> entitlesTo) {
        this.entitlesTo = entitlesTo;
    }

    public List<AchievementTabView> getSubAchievements() {
        return subAchievements;
    }

    public void setSubAchievements(List<AchievementTabView> subAchievements) {
        this.subAchievements = subAchievements;
    }

    public List<AchievementTabView> getIsPartOf() {
        return isPartOf;
    }

    public void setIsPartOf(List<AchievementTabView> isPartOf) {
        this.isPartOf = isPartOf;
    }

    public LearningOpportunityFieldView getLearningOpportunity() {
        return learningOpportunity;
    }

    public void setLearningOpportunity(LearningOpportunityFieldView learningOpportunity) {
        this.learningOpportunity = learningOpportunity;
    }

    public List<CreditPointFieldView> getReceivedCredit() {
        return receivedCredit;
    }

    public void setReceivedCredit(List<CreditPointFieldView> receivedCredit) {
        this.receivedCredit = receivedCredit;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AchievementTabView that = (AchievementTabView) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}


