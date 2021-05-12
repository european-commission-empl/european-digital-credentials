package eu.europa.ec.empl.edci.issuer.web.model.data;

import eu.europa.ec.empl.edci.issuer.web.model.dataTypes.CodeDTView;

import java.util.List;
import java.util.Map;

public class RecipientDataView {

    private String firstName;
    private String lastName;
    private String dateOfBirth;
    private List<CodeDTView> citizenshipCountry;
    private String nationalIdentifier;
    private String nationalIdentifierSpatialId;
    private CodeDTView placeOfBirthCountry;
    private String address;
    private CodeDTView addressCountry;
    private CodeDTView gender;
    private String emailAddress;
    private String walletAddress;

    //Oids del Achievement que contentenen UN assessement i els grades d'aquest assessment
    private Map<Long, String> assessmentGrades;

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getWalletAddress() {
        return walletAddress;
    }

    public void setWalletAddress(String walletAddress) {
        this.walletAddress = walletAddress;
    }

    public Map<Long, String> getAssessmentGrades() {
        return assessmentGrades;
    }

    public void setAssessmentGrades(Map<Long, String> assessmentGrades) {
        this.assessmentGrades = assessmentGrades;
    }

    public List<CodeDTView> getCitizenshipCountry() {
        return citizenshipCountry;
    }

    public void setCitizenshipCountry(List<CodeDTView> citizenshipCountry) {
        this.citizenshipCountry = citizenshipCountry;
    }

    public String getNationalIdentifier() {
        return nationalIdentifier;
    }

    public void setNationalIdentifier(String nationalIdentifier) {
        this.nationalIdentifier = nationalIdentifier;
    }

    public String getNationalIdentifierSpatialId() {
        return nationalIdentifierSpatialId;
    }

    public void setNationalIdentifierSpatialId(String nationalIdentifierSpatialId) {
        this.nationalIdentifierSpatialId = nationalIdentifierSpatialId;
    }

    public CodeDTView getPlaceOfBirthCountry() {
        return placeOfBirthCountry;
    }

    public void setPlaceOfBirthCountry(CodeDTView placeOfBirthCountry) {
        this.placeOfBirthCountry = placeOfBirthCountry;
    }

    public CodeDTView getAddressCountry() {
        return addressCountry;
    }

    public void setAddressCountry(CodeDTView addressCountry) {
        this.addressCountry = addressCountry;
    }

    public CodeDTView getGender() {
        return gender;
    }

    public void setGender(CodeDTView gender) {
        this.gender = gender;
    }
}