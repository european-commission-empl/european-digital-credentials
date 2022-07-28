package eu.europa.ec.empl.edci.datamodel.view;

import java.util.List;
import java.util.Objects;

public class EntitlementTabView implements ITabView {

    private String id;
    private String title;
    private List<IdentifierFieldView> identifier;
    private String issuedDate;
    private String expiryDate;
    private String description;
    private List<EntitlementTabView> subEntitlements;
    private List<NoteFieldView> moreInformation;
    private EntitlementSpecTabView specifiedBy;
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

    public List<IdentifierFieldView> getIdentifier() {
        return identifier;
    }

    public void setIdentifier(List<IdentifierFieldView> identifier) {
        this.identifier = identifier;
    }

    public String getIssuedDate() {
        return issuedDate;
    }

    public void setIssuedDate(String issuedDate) {
        this.issuedDate = issuedDate;
    }

    public String getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(String expiryDate) {
        this.expiryDate = expiryDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<EntitlementTabView> getSubEntitlements() {
        return subEntitlements;
    }

    public void setSubEntitlements(List<EntitlementTabView> subEntitlements) {
        this.subEntitlements = subEntitlements;
    }

    public List<NoteFieldView> getMoreInformation() {
        return moreInformation;
    }

    public void setMoreInformation(List<NoteFieldView> moreInformation) {
        this.moreInformation = moreInformation;
    }

    public EntitlementSpecTabView getSpecifiedBy() {
        return specifiedBy;
    }

    public void setSpecifiedBy(EntitlementSpecTabView specifiedBy) {
        this.specifiedBy = specifiedBy;
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
