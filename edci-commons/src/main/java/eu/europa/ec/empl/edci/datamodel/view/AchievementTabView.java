package eu.europa.ec.empl.edci.datamodel.view;

import java.util.List;
import java.util.Objects;

public class AchievementTabView implements ITabView {

    private String id;
    private String title;
    private List<IdentifierFieldView> identifier;
    private String awardingDate;
    private OrganizationTabView awardingBody;
    private String description;
    private List<AssessmentTabView> provenBy; //wasDerivedFrom
    private List<ActivityTabView> influencedBy;
    private List<EntitlementTabView> entitledOwnerTo;
    private List<AchievementTabView> subAchievements;
    private List<NoteFieldView> moreInformation;

    private AchievementSpecTabView specifiedBy;
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

    public String getAwardingDate() {
        return awardingDate;
    }

    public void setAwardingDate(String awardingDate) {
        this.awardingDate = awardingDate;
    }

    public OrganizationTabView getAwardingBody() {
        return awardingBody;
    }

    public void setAwardingBody(OrganizationTabView awardingBody) {
        this.awardingBody = awardingBody;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public List<EntitlementTabView> getEntitledOwnerTo() {
        return entitledOwnerTo;
    }

    public void setEntitledOwnerTo(List<EntitlementTabView> entitledOwnerTo) {
        this.entitledOwnerTo = entitledOwnerTo;
    }

    public List<AchievementTabView> getSubAchievements() {
        return subAchievements;
    }

    public void setSubAchievements(List<AchievementTabView> subAchievements) {
        this.subAchievements = subAchievements;
    }

    public List<NoteFieldView> getMoreInformation() {
        return moreInformation;
    }

    public void setMoreInformation(List<NoteFieldView> moreInformation) {
        this.moreInformation = moreInformation;
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


