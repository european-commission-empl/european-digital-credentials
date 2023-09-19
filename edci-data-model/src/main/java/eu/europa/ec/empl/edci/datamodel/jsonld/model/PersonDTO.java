package eu.europa.ec.empl.edci.datamodel.jsonld.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import eu.europa.ec.empl.edci.annotation.CustomizableCLFieldDTO;
import eu.europa.ec.empl.edci.annotation.EDCIIdentifier;
import eu.europa.ec.empl.edci.constants.ControlledList;
import eu.europa.ec.empl.edci.datamodel.jsonld.model.dataTypes.ConceptDTO;
import eu.europa.ec.empl.edci.datamodel.jsonld.model.dataTypes.LegalIdentifier;
import eu.europa.ec.empl.edci.datamodel.jsonld.model.dataTypes.LiteralMap;

import javax.validation.constraints.NotNull;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@EDCIIdentifier(prefix = "urn:epass:person:")
public class PersonDTO extends AgentDTO {

    private LiteralMap birthName;
    @CustomizableCLFieldDTO(targetFramework = ControlledList.COUNTRY)
    private List<ConceptDTO> citizenshipCountry = new ArrayList<>();
    private ZonedDateTime dateOfBirth;
    private LiteralMap givenName;
    private LiteralMap familyName;
    private LiteralMap fullName;
    private LegalIdentifier nationalID;
    @CustomizableCLFieldDTO(targetFramework = ControlledList.HUMAN_SEX)
    private ConceptDTO gender;
    @NotNull
    private List<ClaimDTO> hasClaim = new ArrayList<>();
    private List<EuropeanDigitalCredentialDTO> hasCredential = new ArrayList<>();
    private List<OrganisationDTO> memberOf = new ArrayList<>();
    private LiteralMap patronymicName;
    private LocationDTO placeOfBirth;

    public PersonDTO() {
        super();
    }

    @JsonCreator
    public PersonDTO(String uri) {
        super(uri);
    }

    public void setCitizenshipCountry(List<ConceptDTO> citizenshipCountry) {
        this.citizenshipCountry = citizenshipCountry;
    }

    public void setHasClaim(List<ClaimDTO> hasClaim) {
        this.hasClaim = hasClaim;
    }

    public void setHasCredential(List<EuropeanDigitalCredentialDTO> hasCredential) {
        this.hasCredential = hasCredential;
    }

    public void setMemberOf(List<OrganisationDTO> memberOf) {
        this.memberOf = memberOf;
    }

    public LiteralMap getGivenName() {
        return givenName;
    }

    public void setGivenName(LiteralMap givenName) {
        this.givenName = givenName;
    }

    public LiteralMap getFamilyName() {
        return familyName;
    }

    public void setFamilyName(LiteralMap familyName) {
        this.familyName = familyName;
    }

    public ZonedDateTime getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(ZonedDateTime dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public LiteralMap getBirthName() {
        return birthName;
    }

    public void setBirthName(LiteralMap birthName) {
        this.birthName = birthName;
    }

    public LiteralMap getFullName() {
        return fullName;
    }

    public void setFullName(LiteralMap fullName) {
        this.fullName = fullName;
    }

    public LegalIdentifier getNationalID() {
        return nationalID;
    }

    public void setNationalID(LegalIdentifier nationalID) {
        this.nationalID = nationalID;
    }

    public List<@NotNull ClaimDTO> getHasClaim() {
        return hasClaim;
    }

    public List<ConceptDTO> getCitizenshipCountry() {
        return citizenshipCountry;
    }

    public ConceptDTO getGender() {
        return gender;
    }

    public void setGender(ConceptDTO gender) {
        this.gender = gender;
    }

    public List<EuropeanDigitalCredentialDTO> getHasCredential() {
        return hasCredential;
    }

    public LiteralMap getPatronymicName() {
        return patronymicName;
    }

    public void setPatronymicName(LiteralMap patronymicName) {
        this.patronymicName = patronymicName;
    }

    public LocationDTO getPlaceOfBirth() {
        return placeOfBirth;
    }

    public void setPlaceOfBirth(LocationDTO placeOfBirth) {
        this.placeOfBirth = placeOfBirth;
    }

    public List<OrganisationDTO> getMemberOf() {
        return memberOf;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PersonDTO)) return false;
        if (!super.equals(o)) return false;
        PersonDTO personDTO = (PersonDTO) o;
        return Objects.equals(birthName, personDTO.birthName) &&
                Objects.equals(citizenshipCountry, personDTO.citizenshipCountry) &&
                Objects.equals(dateOfBirth, personDTO.dateOfBirth) &&
                Objects.equals(givenName, personDTO.givenName) &&
                Objects.equals(familyName, personDTO.familyName) &&
                Objects.equals(fullName, personDTO.fullName) &&
                Objects.equals(nationalID, personDTO.nationalID) &&
                Objects.equals(gender, personDTO.gender) &&
                Objects.equals(hasClaim, personDTO.hasClaim) &&
                Objects.equals(hasCredential, personDTO.hasCredential) &&
                Objects.equals(memberOf, personDTO.memberOf) &&
                Objects.equals(patronymicName, personDTO.patronymicName) &&
                Objects.equals(placeOfBirth, personDTO.placeOfBirth);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), birthName, citizenshipCountry, dateOfBirth, givenName, familyName, fullName, nationalID, gender, hasClaim, hasCredential, memberOf, patronymicName, placeOfBirth);
    }
}
