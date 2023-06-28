package eu.europa.ec.empl.edci.model.view.base;

import eu.europa.ec.empl.edci.model.view.fields.*;

import java.util.ArrayList;
import java.util.List;

public class AgentView {

    private List<String> altLabel;
    private List<ContactPointFieldView> contactPoint = new ArrayList<>();
    private List<GroupFieldView> groupMemberOf = new ArrayList<>();
    private List<IdentifierFieldView> identifier = new ArrayList<>();
    private String dateModified;
    private List<LocationFieldView> locations = new ArrayList<>();
    private List<NoteFieldView> additionalNote = new ArrayList<>();
    private List<String> prefLabel;
    /**
     * Field from Org/Person, required for using Agent in swagger (see Claim.awardedBy.awardingBody)
     */
    private String legalName;
    private IdentifierFieldView eidasIdentifier;
    private List<IdentifierFieldView> vatIdentifier;
    private List<IdentifierFieldView> taxIdentifier;
    private IdentifierFieldView nationalID;
    private String fullName;
    private IdentifierFieldView registration;

    public List<String> getAltLabel() {
        return altLabel;
    }

    public void setAltLabel(List<String> altLabel) {
        this.altLabel = altLabel;
    }

    public List<ContactPointFieldView> getContactPoint() {
        return contactPoint;
    }

    public void setContactPoint(List<ContactPointFieldView> contactPoint) {
        this.contactPoint = contactPoint;
    }

    public List<GroupFieldView> getGroupMemberOf() {
        return groupMemberOf;
    }

    public void setGroupMemberOf(List<GroupFieldView> groupMemberOf) {
        this.groupMemberOf = groupMemberOf;
    }

    public List<IdentifierFieldView> getIdentifier() {
        return identifier;
    }

    public void setIdentifier(List<IdentifierFieldView> identifier) {
        this.identifier = identifier;
    }

    public String getDateModified() {
        return dateModified;
    }

    public void setDateModified(String dateModified) {
        this.dateModified = dateModified;
    }

    public List<LocationFieldView> getLocations() {
        return locations;
    }

    public void setLocations(List<LocationFieldView> locations) {
        this.locations = locations;
    }

    public List<NoteFieldView> getAdditionalNote() {
        return additionalNote;
    }

    public void setAdditionalNote(List<NoteFieldView> additionalNote) {
        this.additionalNote = additionalNote;
    }

    public List<String> getPrefLabel() {
        return prefLabel;
    }

    public void setPrefLabel(List<String> prefLabel) {
        this.prefLabel = prefLabel;
    }

    public String getLegalName() {
        return legalName;
    }

    public void setLegalName(String legalName) {
        this.legalName = legalName;
    }

    public IdentifierFieldView getEidasIdentifier() {
        return eidasIdentifier;
    }

    public void setEidasIdentifier(IdentifierFieldView eidasIdentifier) {
        this.eidasIdentifier = eidasIdentifier;
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

    public IdentifierFieldView getRegistration() {
        return registration;
    }

    public void setRegistration(IdentifierFieldView registration) {
        this.registration = registration;
    }

    public IdentifierFieldView getNationalID() {
        return nationalID;
    }

    public void setNationalID(IdentifierFieldView nationalID) {
        this.nationalID = nationalID;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
}
