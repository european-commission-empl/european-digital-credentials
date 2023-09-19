package eu.europa.ec.empl.edci.model.view.tabs;

import eu.europa.ec.empl.edci.model.view.base.AgentView;
import eu.europa.ec.empl.edci.model.view.base.ITabView;
import eu.europa.ec.empl.edci.model.view.fields.LocationFieldView;

import java.util.ArrayList;
import java.util.List;

public class CredentialSubjectTabView extends AgentView implements ITabView {

    private String id;
    private String givenName;
    private String familyName;
    private String patronymicName;
    private String birthName;
    private String dateOfBirth;

    private List<String> citizenshipCountry;
    private LocationFieldView placeOfBirth;


    private String gender;

    private List<OrganizationTabView> memberOf = new ArrayList<>();


    public CredentialSubjectTabView() {

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
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

    public String getPatronymicName() {
        return patronymicName;
    }

    public void setPatronymicName(String patronymicName) {
        this.patronymicName = patronymicName;
    }

    public String getBirthName() {
        return birthName;
    }

    public void setBirthName(String birthName) {
        this.birthName = birthName;
    }

    public List<OrganizationTabView> getMemberOf() {
        return memberOf;
    }

    public void setMemberOf(List<OrganizationTabView> memberOf) {
        this.memberOf = memberOf;
    }
    

}

