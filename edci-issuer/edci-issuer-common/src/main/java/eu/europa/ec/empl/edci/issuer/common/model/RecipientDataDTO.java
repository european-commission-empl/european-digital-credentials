package eu.europa.ec.empl.edci.issuer.common.model;

import eu.europa.ec.empl.edci.constants.ControlledList;
import eu.europa.ec.empl.edci.constants.EDCIConstants;
import eu.europa.ec.empl.edci.datamodel.model.dataTypes.Code;
import eu.europa.ec.empl.edci.issuer.common.annotation.CustomizableCLField;
import eu.europa.ec.empl.edci.issuer.common.annotation.CustomizableEntity;
import eu.europa.ec.empl.edci.issuer.common.annotation.CustomizableField;
import eu.europa.ec.empl.edci.issuer.common.constants.Customization;
import eu.europa.ec.empl.edci.issuer.common.model.customization.FieldType;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.ArrayList;

@CustomizableEntity(labelKey = "custom.entity.label.person", identifierField = "nationalIdentifier", position = 1, entityCode = RecipientDataDTO.ENTITY_CODE, specClass = RecipientDataDTO.class)
public class RecipientDataDTO {
    public static final String ENTITY_CODE = "REC";

    @NotNull
    @CustomizableField(labelKey = "custom.field.person.firstName", shouldInstanceMethodName = "true", position = 1, fieldType = FieldType.TEXT, fieldPath = ENTITY_CODE + ".givenNames", mandatory = true,
            validation = Customization.VALIDATION_LENGTH + Customization.Validation.parameter_separator_open + "255" + Customization.Validation.parameter_separator_close
                    + Customization.Validation.validator_separator + Customization.VALIDATION_MANDATORY)
    private String firstName;
    @NotNull
    @CustomizableField(labelKey = "custom.field.person.lastName", shouldInstanceMethodName = "true", position = 2, fieldType = FieldType.TEXT, fieldPath = ENTITY_CODE + ".familyName", mandatory = true,
            validation = Customization.VALIDATION_LENGTH + Customization.Validation.parameter_separator_open + "255" + Customization.Validation.parameter_separator_close
                    + Customization.Validation.validator_separator + Customization.VALIDATION_MANDATORY)
    private String lastName;
    @CustomizableField(labelKey = "custom.field.person.dateOfBirth", shouldInstanceMethodName = "true", position = 3, fieldType = FieldType.DATE, fieldPath = ENTITY_CODE + ".dateOfBirth",
            validation = Customization.VALIDATION_DATELOCALFORMAT, additionalInfo = EDCIConstants.DATE_LOCAL)
    private LocalDate dateOfBirth;
    @CustomizableCLField(targetFramework = ControlledList.COUNTRY, descriptionLabelKey = "customization.description.cl.country")
    @CustomizableField(labelKey = "custom.field.person.citizenshipCountry", shouldInstanceMethodName = "true", position = 4, fieldType = FieldType.CONTROLLED_LIST, fieldPath = ENTITY_CODE + ".citizenshipCountry", size = 3)
    private ArrayList<Code> citizenshipCountry;
    @CustomizableField(labelKey = "custom.field.person.nationalIdentifier.content", shouldInstanceMethodName = "true", position = 5, fieldType = FieldType.TEXT, fieldPath = ENTITY_CODE + ".nationalId.content",
            validation = Customization.VALIDATION_LENGTH + Customization.Validation.parameter_separator_open + "255" + Customization.Validation.parameter_separator_close + Customization.Validation.validator_separator
                    + Customization.VALIDATION_MANDATORYIF + Customization.Validation.parameter_separator_open + Customization.FieldGroup.GROUP_IDENTIFIER + Customization.Validation.parameter_separator_close
            , relatesTo = ENTITY_CODE + ".nationalId.spatialId")
    private String nationalIdentifier;
    @CustomizableField(labelKey = "custom.field.person.otherIdentifier.content", shouldInstanceMethodName = "true", position = 7, fieldType = FieldType.TEXT, fieldPath = ENTITY_CODE + ".identifier[0].content",
            validation = Customization.VALIDATION_LENGTH + Customization.Validation.parameter_separator_open + "255" + Customization.Validation.parameter_separator_close)
    private String identifier;
    @CustomizableField(labelKey = "custom.field.person.nationalIdentifier.spatialId", shouldInstanceMethodName = "true", position = 6, fieldType = FieldType.TEXT, fieldPath = ENTITY_CODE + ".nationalId.spatialId",
            validation = Customization.VALIDATION_LENGTH + Customization.Validation.parameter_separator_open + "255" + Customization.Validation.parameter_separator_close + Customization.Validation.validator_separator
                    + Customization.VALIDATION_MANDATORYIF + Customization.Validation.parameter_separator_open + Customization.FieldGroup.GROUP_IDENTIFIER + Customization.Validation.parameter_separator_close
            , relatesTo = ENTITY_CODE + ".nationalId.content")
    private String nationalIdentifierSpatialId;
    //This is a CL, single to list, just push new element
    @CustomizableCLField(targetFramework = ControlledList.COUNTRY, descriptionLabelKey = "customization.description.cl.country")
    @CustomizableField(labelKey = "custom.field.person.placeOfBirthCounty", shouldInstanceMethodName = "true", position = 8, fieldType = FieldType.CONTROLLED_LIST, fieldPath = ENTITY_CODE + ".placeOfBirth.hasAddress[0].countryCode")
    private Code placeOfBirthCountry;
    @CustomizableField(labelKey = "custom.field.person.address", shouldInstanceMethodName = "true", position = 9, fieldType = FieldType.TEXT, fieldPath = ENTITY_CODE + ".hasLocation[0].hasAddress[0].fullAddress",
            validation = Customization.VALIDATION_LENGTH + Customization.Validation.parameter_separator_open + "255" + Customization.Validation.parameter_separator_close + Customization.Validation.validator_separator
             + Customization.VALIDATION_MANDATORYIF + Customization.Validation.parameter_separator_open + Customization.FieldGroup.GROUP_ADDRESS + Customization.Validation.parameter_separator_close
            , relatesTo = ENTITY_CODE + ".hasLocation[0].hasAddress[0].countryCode")
    private String address;
    //this is a CL
    @CustomizableCLField(targetFramework = ControlledList.COUNTRY, descriptionLabelKey = "customization.description.cl.country")
    @CustomizableField(labelKey = "custom.field.person.address.country", shouldInstanceMethodName = "true", position = 10, fieldType = FieldType.CONTROLLED_LIST, fieldPath = ENTITY_CODE + ".hasLocation[0].hasAddress[0].countryCode",
            validation = Customization.VALIDATION_MANDATORYIF + Customization.Validation.parameter_separator_open + Customization.FieldGroup.GROUP_ADDRESS + Customization.Validation.parameter_separator_close
            , relatesTo = ENTITY_CODE + ".hasLocation[0].hasAddress[0].fullAddress")
    private Code addressCountry;
    //This is a CL
    @CustomizableCLField(targetFramework = ControlledList.HUMAN_SEX, descriptionLabelKey = "customization.description.cl.gender")
    @CustomizableField(labelKey = "custom.field.person.gender", shouldInstanceMethodName = "true", position = 11, fieldType = FieldType.CONTROLLED_LIST, fieldPath = ENTITY_CODE + ".gender")
    private Code gender;
    @CustomizableField(labelKey = "custom.field.person.email", shouldInstanceMethodName = "true", position = 12, fieldType = FieldType.TEXT, fieldPath = ENTITY_CODE + ".contactPoint[0].email[0].id", mandatory = true,
            validation = Customization.VALIDATION_LENGTH + Customization.Validation.parameter_separator_open + "255" + Customization.Validation.parameter_separator_close
                    + Customization.Validation.validator_separator + Customization.VALIDATION_EMAIL
                    + Customization.Validation.validator_separator + Customization.VALIDATION_MANDATORYIFNOT + Customization.Validation.parameter_separator_open + Customization.FieldGroup.GROUP_CONTACT_POINT + Customization.Validation.parameter_separator_close)
    private String emailAddress;
    @CustomizableField(labelKey = "custom.field.person.wallet", shouldInstanceMethodName = "true", position = 13, fieldType = FieldType.TEXT, fieldPath = ENTITY_CODE + ".contactPoint[0].walletAddress", mandatory = true,
            validation = Customization.VALIDATION_LENGTH + Customization.Validation.parameter_separator_open + "255" + Customization.Validation.parameter_separator_close
                    + Customization.Validation.validator_separator + Customization.VALIDATION_MANDATORYIFNOT + Customization.Validation.parameter_separator_open + Customization.FieldGroup.GROUP_CONTACT_POINT + Customization.Validation.parameter_separator_close)
    private String walletAddress;

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

    public String getNationalIdentifier() {
        return nationalIdentifier;
    }

    public void setNationalIdentifier(String nationalIdentifier) {
        this.nationalIdentifier = nationalIdentifier;
    }

    public ArrayList<Code> getCitizenshipCountry() {
        return citizenshipCountry;
    }

    public void setCitizenshipCountry(ArrayList<Code> citizenshipCountry) {
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
