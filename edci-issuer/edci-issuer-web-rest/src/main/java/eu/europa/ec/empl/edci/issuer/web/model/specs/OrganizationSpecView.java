package eu.europa.ec.empl.edci.issuer.web.model.specs;

import eu.europa.ec.empl.edci.issuer.web.model.SubresourcesOids;
import eu.europa.ec.empl.edci.issuer.web.model.dataContainers.AccreditationDCView;
import eu.europa.ec.empl.edci.issuer.web.model.dataContainers.ContactPointDCView;
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

    private List<AccreditationDCView> hasAccreditation; //*

    private MediaObjectDTView logo; //0..1

    private Set<IdentifierDTView> identifier; //*

    private Set<CodeDTView> type; //*

    @NotNull
    private IdentifierDTView legalIdentifier; //1

    private Set<TextDTView> alternativeName; //*

    private Set<NoteDTView> note; //*

    private SubresourcesOids relUnitOf;

    @NotNull
    private Set<LocationDCView> hasLocation; //1..*

    private Set<ContactPointDCView> contactPoint; //*


    public SubresourcesOids getRelUnitOf() {
        relUnitOf = (relUnitOf == null ? new SubresourcesOids() : relUnitOf);
        return relUnitOf;
    }

    public void setRelUnitOf(SubresourcesOids relUnitOf) {
        this.relUnitOf = relUnitOf;
    }

    public IdentifierDTView getLegalIdentifier() {
        return legalIdentifier;
    }

    public void setLegalIdentifier(IdentifierDTView legalIdentifier) {
        this.legalIdentifier = legalIdentifier;
    }

    public Set<IdentifierDTView> getIdentifier() {
        return identifier;
    }

    public void setIdentifier(Set<IdentifierDTView> identifier) {
        this.identifier = identifier;
    }

    public Set<CodeDTView> getType() {
        return type;
    }

    public void setType(Set<CodeDTView> type) {
        this.type = type;
    }

    public Set<TextDTView> getAlternativeName() {
        return alternativeName;
    }

    public void setAlternativeName(Set<TextDTView> alternativeName) {
        this.alternativeName = alternativeName;
    }

    public Set<NoteDTView> getNote() {
        return note;
    }

    public void setNote(Set<NoteDTView> note) {
        this.note = note;
    }

    public Set<LocationDCView> getHasLocation() {
        return hasLocation;
    }

    public void setHasLocation(Set<LocationDCView> hasLocation) {
        this.hasLocation = hasLocation;
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

    public List<AccreditationDCView> getHasAccreditation() {
        return hasAccreditation;
    }

    public void setHasAccreditation(List<AccreditationDCView> hasAccreditation) {
        this.hasAccreditation = hasAccreditation;
    }

    public MediaObjectDTView getLogo() {
        return logo;
    }

    public void setLogo(MediaObjectDTView logo) {
        this.logo = logo;
    }
}