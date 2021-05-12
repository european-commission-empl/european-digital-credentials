package eu.europa.ec.empl.edci.issuer.common.model;

import eu.europa.ec.empl.edci.datamodel.model.dataTypes.Code;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class RecipientDataDTO {

    @NotNull
    private String firstName;
    @NotNull
    private String lastName;
    private LocalDate dateOfBirth;
    private List<Code> citizenshipCountry;
    private String nationalIdentifier;
    private String nationalIdentifierSpatialId;
    private Code placeOfBirthCountry;
    private String address;
    private Code addressCountry;
    private Code gender;
    private String emailAddress;
    private String walletAddress;

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

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
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

    public String getNationalIdentifier() {
        return nationalIdentifier;
    }

    public void setNationalIdentifier(String nationalIdentifier) {
        this.nationalIdentifier = nationalIdentifier;
    }

    public List<Code> getCitizenshipCountry() {
        return citizenshipCountry;
    }

    public void setCitizenshipCountry(List<Code> citizenshipCountry) {
        this.citizenshipCountry = citizenshipCountry;
    }

    public String getNationalIdentifierSpatialId() {
        return nationalIdentifierSpatialId;
    }

    public void setNationalIdentifierSpatialId(String nationalIdentifierSpatialId) {
        this.nationalIdentifierSpatialId = nationalIdentifierSpatialId;
    }

    public Code getPlaceOfBirthCountry() {
        return placeOfBirthCountry;
    }

    public void setPlaceOfBirthCountry(Code placeOfBirthCountry) {
        this.placeOfBirthCountry = placeOfBirthCountry;
    }

    public Code getAddressCountry() {
        return addressCountry;
    }

    public void setAddressCountry(Code addressCountry) {
        this.addressCountry = addressCountry;
    }

    public Code getGender() {
        return gender;
    }

    public void setGender(Code gender) {
        this.gender = gender;
    }
}
