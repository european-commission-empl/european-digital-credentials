package eu.europa.ec.empl.edci.model.external.qdr;

import com.fasterxml.jackson.annotation.JsonIgnore;
import eu.europa.ec.empl.edci.annotation.CustomizableCLFieldDTO;
import eu.europa.ec.empl.edci.annotation.EDCIIdentifier;
import eu.europa.ec.empl.edci.annotation.MandatoryConceptScheme;
import eu.europa.ec.empl.edci.constants.ControlledList;
import eu.europa.ec.empl.edci.datamodel.jsonld.model.ClaimDTO;
import eu.europa.ec.empl.edci.datamodel.jsonld.model.EuropeanDigitalCredentialDTO;
import eu.europa.ec.empl.edci.datamodel.jsonld.model.LocationDTO;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@EDCIIdentifier(prefix = "urn:epass:person:")
public class QDRPersonDTO extends QDRAgentDTO {

    private String birthName;
    @MandatoryConceptScheme("http://publications.europa.eu/resource/authority/country")
    private List<QDRConceptDTO> citizenshipCountry = new ArrayList<>();
    private QDRValue dateOfBirth;
    private String givenName;
    private String familyName;
    private String fullName;
    private QDRLegalIdentifier nationalID;
    @CustomizableCLFieldDTO(targetFramework= ControlledList.HUMAN_SEX)
    private QDRConceptDTO gender;
    @JsonIgnore
    private List<ClaimDTO> hasClaim = new ArrayList<>();
    @JsonIgnore
    private List<EuropeanDigitalCredentialDTO> hasCredential = new ArrayList<>();
    private List<QDROrganisationDTO> memberOf = new ArrayList<>();
    private String patronymicName;
    private LocationDTO placeOfBirth;

    /*
        1 - fullName
        2 - givenName !=null givenName + patronymicName + familyName
        3 - birthName
        4 - familyName
        5 - NationalIDNumber.notation + spatial.prefLabel
        6 - Anonymous
     */
    /*public PersonDTO() {

    }

    public PersonDTO(String id) {
        try {
            this.setId(new URI(id));
        } catch (URISyntaxException e) {
            throw new EDCIException();
        }
    }*/

    public void setCitizenshipCountry(List<QDRConceptDTO> citizenshipCountry) {
        this.citizenshipCountry = citizenshipCountry;
    }

    public void setHasClaim(List<ClaimDTO> hasClaim) {
        this.hasClaim = hasClaim;
    }

    public void setHasCredential(List<EuropeanDigitalCredentialDTO> hasCredential) {
        this.hasCredential = hasCredential;
    }

    public void setMemberOf(List<QDROrganisationDTO> memberOf) {
        this.memberOf = memberOf;
    }

    public String getGivenName() {
        return givenName;
    }

    public void setGivenName(String givenName) {
        this.givenName = givenName;
    }

    public String getFamilyName() {
        return familyName;
    }

    public void setFamilyName(String familyName) {
        this.familyName = familyName;
    }

    public QDRValue getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(QDRValue dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getBirthName() {
        return birthName;
    }

    public void setBirthName(String birthName) {
        this.birthName = birthName;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public QDRLegalIdentifier getNationalID() {
        return nationalID;
    }

    public void setNationalID(QDRLegalIdentifier nationalID) {
        this.nationalID = nationalID;
    }

    public List<@NotNull ClaimDTO> getHasClaim() {
        return hasClaim;
    }

    public List<QDRConceptDTO> getCitizenshipCountry() {
        return citizenshipCountry;
    }

    public QDRConceptDTO getGender() {
        return gender;
    }

    public void setGender(QDRConceptDTO gender) {
        this.gender = gender;
    }

    public List<EuropeanDigitalCredentialDTO> getHasCredential() {
        return hasCredential;
    }

    public String getPatronymicName() {
        return patronymicName;
    }

    public void setPatronymicName(String patronymicName) {
        this.patronymicName = patronymicName;
    }

    public LocationDTO getPlaceOfBirth() {
        return placeOfBirth;
    }

    public void setPlaceOfBirth(LocationDTO placeOfBirth) {
        this.placeOfBirth = placeOfBirth;
    }

    public List<QDROrganisationDTO> getMemberOf() {
        return memberOf;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), birthName, citizenshipCountry, dateOfBirth, givenName, familyName, fullName, nationalID, gender, hasClaim, hasCredential, memberOf, patronymicName, placeOfBirth);
    }
}
