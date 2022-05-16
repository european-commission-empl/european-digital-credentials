package eu.europa.ec.empl.edci.datamodel.model;

import eu.europa.ec.empl.edci.annotation.EDCIIdentifier;
import eu.europa.ec.empl.edci.constants.EDCIConfig;
import eu.europa.ec.empl.edci.constants.EDCIMessageKeys;
import eu.europa.ec.empl.edci.datamodel.adapter.DateAdapter;
import eu.europa.ec.empl.edci.datamodel.model.base.AgentDTO;
import eu.europa.ec.empl.edci.datamodel.model.dataTypes.Code;
import eu.europa.ec.empl.edci.datamodel.model.dataTypes.LegalIdentifier;
import eu.europa.ec.empl.edci.datamodel.model.dataTypes.Text;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@XmlRootElement(name = "credentialSubject")

@XmlType(propOrder = {"preferredName", "alternativeName", "nationalId", "fullName", "givenNames", "familyName", "dateOfBirth", "placeOfBirth", "gender", "citizenshipCountry", "hasLocation", "achieved", "performed", "entitledTo"})
@XmlAccessorType(XmlAccessType.FIELD)
@EDCIIdentifier(prefix = "urn:epass:person:")
public class PersonDTO extends AgentDTO {

    @XmlElement(name = "prefLabel")
    @Valid
    private Text preferredName; //0..1
    @XmlElement(name = "altLabel")
    @Valid
    private List<Text> alternativeName = new ArrayList<>(); //*
    @Valid
    private List<LocationDTO> hasLocation; //*
    @Valid
    private LegalIdentifier nationalId; //0..1
    @Valid
    private Text fullName; //0..1
    @NotNull(message = EDCIMessageKeys.Validation.VALIDATION_PERSON_GIVENNAMES_NOTNULL)
    @Valid
    private Text givenNames; //1
    @NotNull(message = EDCIMessageKeys.Validation.VALIDATION_PERSON_FAMILYNAME_NOTNULL)
    @Valid
    private Text familyName; //1
    @XmlJavaTypeAdapter(DateAdapter.class)
//    @NotNull(message = MessageKeys.Validation.VALIDATION_PERSON_DATEOFBIRTH_NOTNULL)
    private Date dateOfBirth; //1
    @Valid
    private LocationDTO placeOfBirth; //0..1
    @Valid
    private Code gender; //0..1
    @Valid
    private List<Code> citizenshipCountry; //*
    @XmlElementWrapper(name = "activities")
    @XmlElement(name = "activity")
    @Valid
    private List<LearningActivityDTO> performed; //*
    @XmlElementWrapper(name = "achievements")
    @XmlElements({
            @XmlElement(name = "learningAchievement", type = LearningAchievementDTO.class),
    })
    @Valid
    private List<LearningAchievementDTO> achieved = new ArrayList<>(); //*
    @Valid
    @XmlElementWrapper(name = "entitlements")
    @XmlElement(name = "entitlement")
    private List<EntitlementDTO> entitledTo; //*

    public PersonDTO() {
        this.initIdentifiable();
    }

    @Override
    public String getIdentifiableName() {
        return this.getIdentifiableNameFromFieldList(this, "preferredName", "fullName", "givenNames", "familyName", "alternativeName", "identifier", "id");
    }

    @Override
    public boolean equals(Object personDTO) {
        if (personDTO == null) return false;
        if (!PersonDTO.class.isAssignableFrom(personDTO.getClass())) {
            return false;
        } else {
            PersonDTO person = (PersonDTO) personDTO;
            if (nationalId != null && nationalId.getContent() != null && person != null && person.getNationalId() != null && !nationalId.getContent().equals(person.getNationalId().getContent()))
                return false;
            if (fullName != null && fullName.getLocalizedStringOrAny(EDCIConfig.Defaults.DEFAULT_LOCALE) != null) {
                if (person.getFullName() != null && person.getFullName().getLocalizedStringOrAny(EDCIConfig.Defaults.DEFAULT_LOCALE) != null) {
                    if (!person.getFullName().getLocalizedStringOrAny(EDCIConfig.Defaults.DEFAULT_LOCALE).equals(fullName.getLocalizedStringOrAny(EDCIConfig.Defaults.DEFAULT_LOCALE))) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    public LegalIdentifier getNationalId() {
        return nationalId;
    }

    public void setNationalId(LegalIdentifier nationalId) {
        this.nationalId = nationalId;
    }

    public Text getFullName() {
        return fullName;
    }

    public void setFullName(Text fullName) {
        this.fullName = fullName;
    }

    public Text getGivenNames() {
        return givenNames;
    }

    public void setGivenNames(Text givenNames) {
        this.givenNames = givenNames;
    }

    public Text getFamilyName() {
        return familyName;
    }

    public void setFamilyName(Text familyName) {
        this.familyName = familyName;
    }

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public LocationDTO getPlaceOfBirth() {
        return placeOfBirth;
    }

    public void setPlaceOfBirth(LocationDTO placeOfBirth) {
        this.placeOfBirth = placeOfBirth;
    }

    public Code getGender() {
        return gender;
    }

    public void setGender(Code gender) {
        this.gender = gender;
    }

    public List<Code> getCitizenshipCountry() {
        return citizenshipCountry;
    }

    public void setCitizenshipCountry(List<Code> citizenshipCountry) {
        this.citizenshipCountry = citizenshipCountry;
    }

    public List<LearningActivityDTO> getPerformed() {
        return performed;
    }

    public void setPerformed(List<LearningActivityDTO> performed) {
        this.performed = performed;
    }

    public List<LearningAchievementDTO> getAchieved() {
        return achieved;
    }

    public void setAchieved(List<LearningAchievementDTO> achieved) {
        this.achieved = achieved;
    }

    public List<EntitlementDTO> getEntitledTo() {
        return entitledTo;
    }

    public void setEntitledTo(List<EntitlementDTO> entitledTo) {
        this.entitledTo = entitledTo;
    }

    public Text getPreferredName() {
        return preferredName;
    }

    public void setPreferredName(Text preferredName) {
        this.preferredName = preferredName;
    }

    public List<Text> getAlternativeName() {
        return alternativeName;
    }

    public void setAlternativeName(List<Text> alternativeName) {
        this.alternativeName = alternativeName;
    }

    public List<LocationDTO> getHasLocation() {
        return hasLocation;
    }

    public void setHasLocation(List<LocationDTO> hasLocation) {
        this.hasLocation = hasLocation;
    }

    //XML Getter
    public Text prefLabel() {
        return this.preferredName;
    }

    public List<Text> getAltLabel() {
        return this.alternativeName;
    }

    public List<Text> getAltLabels() {
        return this.alternativeName;
    }

    public List<LearningActivityDTO> getActivities() {
        return this.performed;
    }

    public List<LearningAchievementDTO> getAchievements() {
        return this.achieved;
    }

    public List<EntitlementDTO> getEntitlements() {
        return this.entitledTo;
    }
}