package eu.europa.ec.empl.edci.issuer.web.model.specs;

import eu.europa.ec.empl.edci.issuer.web.model.SubresourcesOids;
import eu.europa.ec.empl.edci.issuer.web.model.dataContainers.ContactPointDCView;
import eu.europa.ec.empl.edci.issuer.web.model.dataContainers.GroupDCView;
import eu.europa.ec.empl.edci.issuer.web.model.dataContainers.LocationDCView;
import eu.europa.ec.empl.edci.issuer.web.model.dataContainers.WebDocumentDCView;
import eu.europa.ec.empl.edci.issuer.web.model.dataTypes.*;
import eu.europa.ec.empl.edci.issuer.web.model.specs.lite.OrganizationSpecLiteView;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Set;

public class OrganizationSpecView extends OrganizationSpecLiteView {

    /* *************
     *   Fields    *
     ***************/

    private List<LegalIdentifierDTView> vatIdentifier; //*

    private List<LegalIdentifierDTView> taxIdentifier; //*

    private List<WebDocumentDCView> homePage; //*

    private MediaObjectDTView logo; //0..1

    private Set<IdentifierDTView> identifier; //*

    private LegalIdentifierDTView registration; //1

    private Set<CodeDTView> dcType; //*

    private String modified; //0..1

//    Deprecated
//    private IdentifierDTView legalIdentifier; //1

    private LegalIdentifierDTView eidasIdentifier;

    private TextDTView altLabel; //*

    private Set<NoteDTView> additionalNote; //*

    private SubresourcesOids relSubOrganizationOf;

    private SubresourcesOids relAccreditation;

    @NotNull
    private Set<LocationDCView> location; //1..*

    private Set<GroupDCView> groupMemberOf; //1..*

    private Set<ContactPointDCView> contactPoint; //*

    public SubresourcesOids getRelAccreditation() {
        relAccreditation = (relAccreditation == null ? new SubresourcesOids() : relAccreditation);
        return relAccreditation;
    }

    public void setRelAccreditation(SubresourcesOids relAccreditation) {
        this.relAccreditation = relAccreditation;
    }

    public SubresourcesOids getRelSubOrganizationOf() {
        relSubOrganizationOf = (relSubOrganizationOf == null ? new SubresourcesOids() : relSubOrganizationOf);
        return relSubOrganizationOf;
    }

    public void setRelSubOrganizationOf(SubresourcesOids relSubOrganizationOf) {
        this.relSubOrganizationOf = relSubOrganizationOf;
    }

    public Set<GroupDCView> getGroupMemberOf() {
        return groupMemberOf;
    }

    public void setGroupMemberOf(Set<GroupDCView> groupMemberOf) {
        this.groupMemberOf = groupMemberOf;
    }

    public Set<IdentifierDTView> getIdentifier() {
        return identifier;
    }

    public void setIdentifier(Set<IdentifierDTView> identifier) {
        this.identifier = identifier;
    }

    public Set<CodeDTView> getDcType() {
        return dcType;
    }

    public void setDcType(Set<CodeDTView> dcType) {
        this.dcType = dcType;
    }

    public String getModified() {
        return modified;
    }

    public void setModified(String modified) {
        this.modified = modified;
    }

    public TextDTView getAltLabel() {
        return altLabel;
    }

    public void setAltLabel(TextDTView altLabel) {
        this.altLabel = altLabel;
    }

    public LegalIdentifierDTView getRegistration() {
        return registration;
    }

    public void setRegistration(LegalIdentifierDTView registration) {
        this.registration = registration;
    }

    public LegalIdentifierDTView getEidasIdentifier() {
        return eidasIdentifier;
    }

    public void setEidasIdentifier(LegalIdentifierDTView eidasIdentifier) {
        this.eidasIdentifier = eidasIdentifier;
    }

    public Set<NoteDTView> getAdditionalNote() {
        return additionalNote;
    }

    public void setAdditionalNote(Set<NoteDTView> additionalNote) {
        this.additionalNote = additionalNote;
    }

    public Set<LocationDCView> getLocation() {
        return location;
    }

    public void setLocation(Set<LocationDCView> location) {
        this.location = location;
    }

    public Set<ContactPointDCView> getContactPoint() {
        return contactPoint;
    }

    public void setContactPoint(Set<ContactPointDCView> contactPoint) {
        this.contactPoint = contactPoint;
    }

    public List<LegalIdentifierDTView> getVatIdentifier() {
        return vatIdentifier;
    }

    public void setVatIdentifier(List<LegalIdentifierDTView> vatIdentifier) {
        this.vatIdentifier = vatIdentifier;
    }

    public List<LegalIdentifierDTView> getTaxIdentifier() {
        return taxIdentifier;
    }

    public void setTaxIdentifier(List<LegalIdentifierDTView> taxIdentifier) {
        this.taxIdentifier = taxIdentifier;
    }

    public List<WebDocumentDCView> getHomePage() {
        return homePage;
    }

    public void setHomePage(List<WebDocumentDCView> homePage) {
        this.homePage = homePage;
    }

    public MediaObjectDTView getLogo() {
        return logo;
    }

    public void setLogo(MediaObjectDTView logo) {
        this.logo = logo;
    }
}