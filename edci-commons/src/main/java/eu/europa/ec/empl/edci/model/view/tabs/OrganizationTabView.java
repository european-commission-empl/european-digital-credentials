package eu.europa.ec.empl.edci.model.view.tabs;

import eu.europa.ec.empl.edci.model.view.base.AgentView;
import eu.europa.ec.empl.edci.model.view.base.ITabView;
import eu.europa.ec.empl.edci.model.view.fields.AccreditationFieldView;
import eu.europa.ec.empl.edci.model.view.fields.EvidenceFieldView;
import eu.europa.ec.empl.edci.model.view.fields.LinkFieldView;
import eu.europa.ec.empl.edci.model.view.fields.MediaObjectFieldView;

import java.util.List;
import java.util.Objects;

public class OrganizationTabView extends AgentView implements ITabView {

    private String id;
    private List<LinkFieldView> homepage;
    private OrganizationTabView subOrganizationOf;
    private List<OrganizationTabView> childOrganisations;
    private MediaObjectFieldView logo;
    private Integer depth;
    private List<AccreditationFieldView> accreditation; //*
    private EvidenceFieldView mandateEvidence;
    private EvidenceFieldView accreditationEvidence;

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

    public OrganizationTabView getSubOrganizationOf() {
        return subOrganizationOf;
    }

    public void setSubOrganizationOf(OrganizationTabView subOrganizationOf) {
        this.subOrganizationOf = subOrganizationOf;
    }

    public MediaObjectFieldView getLogo() {
        return logo;
    }

    public void setLogo(MediaObjectFieldView logo) {
        this.logo = logo;
    }

    public List<AccreditationFieldView> getAccreditation() {
        return accreditation;
    }

    public void setAccreditation(List<AccreditationFieldView> accreditation) {
        this.accreditation = accreditation;
    }

    public List<OrganizationTabView> getChildOrganisations() {
        return childOrganisations;
    }

    public void setChildOrganisations(List<OrganizationTabView> childOrganisations) {
        this.childOrganisations = childOrganisations;
    }

    public EvidenceFieldView getMandateEvidence() {
        return mandateEvidence;
    }

    public void setMandateEvidence(EvidenceFieldView mandateEvidence) {
        this.mandateEvidence = mandateEvidence;
    }

    public EvidenceFieldView getAccreditationEvidence() {
        return accreditationEvidence;
    }

    public void setAccreditationEvidence(EvidenceFieldView accreditationEvidence) {
        this.accreditationEvidence = accreditationEvidence;
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
