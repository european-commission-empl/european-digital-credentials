package eu.europa.ec.empl.edci.issuer.web.model.data;

import eu.europa.ec.empl.edci.constants.ControlledList;
import eu.europa.ec.empl.edci.constants.EDCIConstants;
import eu.europa.ec.empl.edci.datamodel.jsonld.model.dataTypes.ConceptDTO;
import eu.europa.ec.empl.edci.issuer.common.annotation.CustomizableCLField;
import eu.europa.ec.empl.edci.issuer.common.annotation.CustomizableField;
import eu.europa.ec.empl.edci.issuer.common.constants.Customization;
import eu.europa.ec.empl.edci.issuer.common.model.customization.FieldType;
import eu.europa.ec.empl.edci.issuer.web.model.dataTypes.CodeDTView;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class RecipientDataView {

    private String firstName;
    private String familyName;
    private String emailAddress;
    private String walletAddress;
    private LocalDate dateOfBirth;
    private CodeDTView placeOfBirthCountry;
    private List<CodeDTView> citizenshipCountry;
    private CodeDTView gender;
    private CodeDTView nationalIdentifierSpatialId;
    private String nationalIdentifier;
    private String identifierScheme;
    private String identifier;
    private String address;
    private CodeDTView addressCountry;
    private CodeDTView addressSpatial;
    private String groupName;

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getFamilyName() {
        return familyName;
    }

    public void setFamilyName(String familyName) {
        this.familyName = familyName;
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

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public CodeDTView getPlaceOfBirthCountry() {
        return placeOfBirthCountry;
    }

    public void setPlaceOfBirthCountry(CodeDTView placeOfBirthCountry) {
        this.placeOfBirthCountry = placeOfBirthCountry;
    }

    public List<CodeDTView> getCitizenshipCountry() {
        return citizenshipCountry;
    }

    public void setCitizenshipCountry(List<CodeDTView> citizenshipCountry) {
        this.citizenshipCountry = citizenshipCountry;
    }

    public CodeDTView getGender() {
        return gender;
    }

    public void setGender(CodeDTView gender) {
        this.gender = gender;
    }

    public CodeDTView getNationalIdentifierSpatialId() {
        return nationalIdentifierSpatialId;
    }

    public void setNationalIdentifierSpatialId(CodeDTView nationalIdentifierSpatialId) {
        this.nationalIdentifierSpatialId = nationalIdentifierSpatialId;
    }

    public String getNationalIdentifier() {
        return nationalIdentifier;
    }

    public void setNationalIdentifier(String nationalIdentifier) {
        this.nationalIdentifier = nationalIdentifier;
    }

    public String getIdentifierScheme() {
        return identifierScheme;
    }

    public void setIdentifierScheme(String identifierScheme) {
        this.identifierScheme = identifierScheme;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public CodeDTView getAddressCountry() {
        return addressCountry;
    }

    public void setAddressCountry(CodeDTView addressCountry) {
        this.addressCountry = addressCountry;
    }

    public CodeDTView getAddressSpatial() {
        return addressSpatial;
    }

    public void setAddressSpatial(CodeDTView addressSpatial) {
        this.addressSpatial = addressSpatial;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }
}