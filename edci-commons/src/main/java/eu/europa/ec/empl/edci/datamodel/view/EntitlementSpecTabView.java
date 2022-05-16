package eu.europa.ec.empl.edci.datamodel.view;

import java.util.List;

public class EntitlementSpecTabView implements ITabView {

    private List<LinkFieldView> homePage;
    private String title;
    private List<IdentifierFieldView> identifier;
    private String description;
    private String entitlementType;
    private String status;
    private List<OrganizationTabView> validWith;
    private List<String> validWithin;
    private List<String> toWorkAs;
    private List<LinkFieldView> otherDocs;
    private List<NoteFieldView> moreInformation;

    public List<LinkFieldView> getHomePage() {
        return homePage;
    }

    public void setHomePage(List<LinkFieldView> homePage) {
        this.homePage = homePage;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getEntitlementType() {
        return entitlementType;
    }

    public void setEntitlementType(String entitlementType) {
        this.entitlementType = entitlementType;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<OrganizationTabView> getValidWith() {
        return validWith;
    }

    public void setValidWith(List<OrganizationTabView> validWith) {
        this.validWith = validWith;
    }

    public List<String> getValidWithin() {
        return validWithin;
    }

    public void setValidWithin(List<String> validWithin) {
        this.validWithin = validWithin;
    }

    public List<String> getToWorkAs() {
        return toWorkAs;
    }

    public void setToWorkAs(List<String> toWorkAs) {
        this.toWorkAs = toWorkAs;
    }

    public List<LinkFieldView> getOtherDocs() {
        return otherDocs;
    }

    public void setOtherDocs(List<LinkFieldView> otherDocs) {
        this.otherDocs = otherDocs;
    }

    public List<NoteFieldView> getMoreInformation() {
        return moreInformation;
    }

    public void setMoreInformation(List<NoteFieldView> moreInformation) {
        this.moreInformation = moreInformation;
    }
}
