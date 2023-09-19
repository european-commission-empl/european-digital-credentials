package eu.europa.ec.empl.edci.model.view.tabs;

import eu.europa.ec.empl.edci.model.view.base.ITabView;
import eu.europa.ec.empl.edci.model.view.base.SpecificationView;

import java.util.ArrayList;
import java.util.List;

public class EntitlementSpecTabView extends SpecificationView implements ITabView {

    private String entitlementStatus;
    private List<OrganizationTabView> limitOrganisation;
    private List<String> limitJurisdiction;
    private List<String> limitNationalOccupation = new ArrayList<>();
    private List<String> limitOccupation = new ArrayList<>();


    public String getEntitlementStatus() {
        return entitlementStatus;
    }

    public void setEntitlementStatus(String entitlementStatus) {
        this.entitlementStatus = entitlementStatus;
    }

    public List<String> getLimitJurisdiction() {
        return limitJurisdiction;
    }

    public void setLimitJurisdiction(List<String> limitJurisdiction) {
        this.limitJurisdiction = limitJurisdiction;
    }

    public List<OrganizationTabView> getLimitOrganisation() {
        return limitOrganisation;
    }

    public void setLimitOrganisation(List<OrganizationTabView> limitOrganisation) {
        this.limitOrganisation = limitOrganisation;
    }

    public List<String> getLimitNationalOccupation() {
        return limitNationalOccupation;
    }

    public void setLimitNationalOccupation(List<String> limitNationalOccupation) {
        this.limitNationalOccupation = limitNationalOccupation;
    }

    public List<String> getLimitOccupation() {
        return limitOccupation;
    }

    public void setLimitOccupation(List<String> limitOccupation) {
        this.limitOccupation = limitOccupation;
    }
}
