package eu.europa.ec.empl.edci.model.view.tabs;

import eu.europa.ec.empl.edci.model.view.base.ClaimView;
import eu.europa.ec.empl.edci.model.view.base.ITabView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class EntitlementTabView extends ClaimView implements ITabView {

    private String id;
    private String dateIssued;
    private String expiryDate;
    private List<EntitlementTabView> subEntitlements;
    private List<EntitlementTabView> isPartOf;

    private EntitlementSpecTabView specifiedBy;

    private List<AchievementTabView> entitledBy = new ArrayList<>();
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

    public String getDateIssued() {
        return dateIssued;
    }

    public void setDateIssued(String dateIssued) {
        this.dateIssued = dateIssued;
    }

    public String getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(String expiryDate) {
        this.expiryDate = expiryDate;
    }

    public List<EntitlementTabView> getSubEntitlements() {
        return subEntitlements;
    }

    public void setSubEntitlements(List<EntitlementTabView> subEntitlements) {
        this.subEntitlements = subEntitlements;
    }

    public EntitlementSpecTabView getSpecifiedBy() {
        return specifiedBy;
    }

    public void setSpecifiedBy(EntitlementSpecTabView specifiedBy) {
        this.specifiedBy = specifiedBy;
    }

    public List<EntitlementTabView> getIsPartOf() {
        return isPartOf;
    }

    public void setIsPartOf(List<EntitlementTabView> isPartOf) {
        this.isPartOf = isPartOf;
    }

    public List<AchievementTabView> getEntitledBy() {
        return entitledBy;
    }

    public void setEntitledBy(List<AchievementTabView> entitledBy) {
        this.entitledBy = entitledBy;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EntitlementTabView that = (EntitlementTabView) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

}
