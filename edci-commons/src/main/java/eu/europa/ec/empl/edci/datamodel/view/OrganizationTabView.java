package eu.europa.ec.empl.edci.datamodel.view;

import java.util.List;
import java.util.Objects;

public class OrganizationTabView implements ITabView {

    private String id;
    private List<LinkFieldView> homepage;
    private String preferredName;
    private List<String> alternativeName;
    private List<LocationFieldView> location;
    private IdentifierFieldView legalIdentifier;
    private List<IdentifierFieldView> vatIdentifier;
    private List<IdentifierFieldView> taxIdentifier;
    private List<IdentifierFieldView> identifier;
    private OrganizationTabView parentOrganization;
    private MediaObjectFieldView logo;
    private List<ContactPointFieldView> contactPoint;
    private Integer depth;
    private List<AccreditationFieldView> hasAccreditation; //*

    public Integer getDepth() {
        return depth;
    }

    public void setDepth(Integer depth) {
        this.depth = depth;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<LinkFieldView> getHomepage() {
        return homepage;
    }

    public void setHomepage(List<LinkFieldView> homepage) {
        this.homepage = homepage;
    }

    public String getPreferredName() {
        return preferredName;
    }

    public void setPreferredName(String preferredName) {
        this.preferredName = preferredName;
    }

    public List<String> getAlternativeName() {
        return alternativeName;
    }

    public void setAlternativeName(List<String> alternativeName) {
        this.alternativeName = alternativeName;
    }

    public List<LocationFieldView> getLocation() {
        return location;
    }

    public void setLocation(List<LocationFieldView> location) {
        this.location = location;
    }

    public IdentifierFieldView getLegalIdentifier() {
        return legalIdentifier;
    }

    public void setLegalIdentifier(IdentifierFieldView legalIdentifier) {
        this.legalIdentifier = legalIdentifier;
    }

    public List<IdentifierFieldView> getVatIdentifier() {
        return vatIdentifier;
    }

    public void setVatIdentifier(List<IdentifierFieldView> vatIdentifier) {
        this.vatIdentifier = vatIdentifier;
    }

    public List<IdentifierFieldView> getTaxIdentifier() {
        return taxIdentifier;
    }

    public void setTaxIdentifier(List<IdentifierFieldView> taxIdentifier) {
        this.taxIdentifier = taxIdentifier;
    }

    public List<IdentifierFieldView> getIdentifier() {
        return identifier;
    }

    public void setIdentifier(List<IdentifierFieldView> identifier) {
        this.identifier = identifier;
    }

    public OrganizationTabView getParentOrganization() {
        return parentOrganization;
    }

    public void setParentOrganization(OrganizationTabView parentOrganization) {
        this.parentOrganization = parentOrganization;
    }

    public List<ContactPointFieldView> getContactPoint() {
        return contactPoint;
    }

    public void setContactPoint(List<ContactPointFieldView> contactPoint) {
        this.contactPoint = contactPoint;
    }

    public MediaObjectFieldView getLogo() {
        return logo;
    }

    public void setLogo(MediaObjectFieldView logo) {
        this.logo = logo;
    }

    public List<AccreditationFieldView> getHasAccreditation() {
        return hasAccreditation;
    }

    public void setHasAccreditation(List<AccreditationFieldView> hasAccreditation) {
        this.hasAccreditation = hasAccreditation;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrganizationTabView that = (OrganizationTabView) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
