package eu.europa.ec.empl.edci.datamodel.view;

import java.util.List;

public class CredentialSubjectTabView implements ITabView {

    private String givenNames;
    private String fullName;
    private String familyName;
    private String dateOfBirth;

    private List<String> citizenshipCountry;
    private LocationFieldView placeOfBirth;

    private String nationalId; //TODO: Should be IdentifierFieldView?

    private List<ContactPointFieldView> contactPoint;
    private String gender;

    private List<String> additionalNote;
    private List<IdentifierFieldView> identifier;

    private List<LocationFieldView> hasLocation;

    public CredentialSubjectTabView() {

    }

    public List<LocationFieldView> getHasLocation() {
        return hasLocation;
    }

    public void setHasLocation(List<LocationFieldView> hasLocation) {
        this.hasLocation = hasLocation;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getGivenNames() {
        return givenNames;
    }

    public void setGivenNames(String givenNames) {
        this.givenNames = givenNames;
    }

    public String getFamilyName() {
        return familyName;
    }

    public void setFamilyName(String familyName) {
        this.familyName = familyName;
    }

    public LocationFieldView getPlaceOfBirth() {
        return placeOfBirth;
    }

    public void setPlaceOfBirth(LocationFieldView placeOfBirth) {
        this.placeOfBirth = placeOfBirth;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public List<String> getCitizenshipCountry() {
        return citizenshipCountry;
    }

    public void setCitizenshipCountry(List<String> citizenshipCountry) {
        this.citizenshipCountry = citizenshipCountry;
    }

    public List<ContactPointFieldView> getContactPoint() {
        return contactPoint;
    }

    public void setContactPoint(List<ContactPointFieldView> contactPoint) {
        this.contactPoint = contactPoint;
    }

    public String getNationalId() {
        return nationalId;
    }

    public void setNationalId(String nationalId) {
        this.nationalId = nationalId;
    }

    public List<IdentifierFieldView> getIdentifier() {
        return identifier;
    }

    public void setIdentifier(List<IdentifierFieldView> identifier) {
        this.identifier = identifier;
    }

    public List<String> getAdditionalNote() {
        return additionalNote;
    }

    public void setAdditionalNote(List<String> additionalNote) {
        this.additionalNote = additionalNote;
    }
}

