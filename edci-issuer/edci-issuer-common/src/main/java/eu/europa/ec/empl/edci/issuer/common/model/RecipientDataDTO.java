package eu.europa.ec.empl.edci.issuer.common.model;

import eu.europa.ec.empl.edci.constants.ControlledList;
import eu.europa.ec.empl.edci.constants.EDCIConstants;
import eu.europa.ec.empl.edci.datamodel.jsonld.model.dataTypes.ConceptDTO;
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
    @CustomizableField(labelKey = "custom.field.person.givenName", shouldInstanceMethodName = "true", position = 1, fieldType = FieldType.TEXT, fieldPath = ENTITY_CODE + ".givenName", mandatory = true,
            validation = Customization.VALIDATION_LENGTH + "(" + "255" + ")"
                    + "," + Customization.VALIDATION_MANDATORY)
    private String firstName;
    @CustomizableField(labelKey = "custom.field.person.familyName", shouldInstanceMethodName = "true", position = 2, fieldType = FieldType.TEXT, fieldPath = ENTITY_CODE + ".familyName", mandatory = true,
            validation = Customization.VALIDATION_LENGTH + "(" + "255" + ")")
    private String familyName;

    @CustomizableField(labelKey = "custom.field.person.email", shouldInstanceMethodName = "true", position = 3, fieldType = FieldType.TEXT, fieldPath = ENTITY_CODE + ".contactPoint[0].emailAddress[0].id", mandatory = true,
            validation = Customization.VALIDATION_LENGTH + "(" + "255" + ")"
                    + "," + Customization.VALIDATION_EMAIL
                    + "," + Customization.VALIDATION_MANDATORY)
    private String emailAddress;
    //ToDo : What to Do?
    @CustomizableField(labelKey = "custom.field.person.wallet", shouldInstanceMethodName = "true", position = 4, fieldType = FieldType.TEXT, fieldPath = ENTITY_CODE + ".walletAddress", mandatory = true,
            validation = Customization.VALIDATION_LENGTH + "(" + "255" + ")")
    private String walletAddress;

    @CustomizableField(labelKey = "custom.field.person.dateOfBirth", shouldInstanceMethodName = "true", position = 5, fieldType = FieldType.DATE, fieldPath = ENTITY_CODE + ".dateOfBirth",
            validation = Customization.VALIDATION_DATELOCALFORMAT, additionalInfo = EDCIConstants.DATE_LOCAL)
    private LocalDate dateOfBirth;
    @CustomizableCLField(targetFramework = ControlledList.COUNTRY, descriptionLabelKey = "customization.description.cl.country")
    @CustomizableField(labelKey = "custom.field.person.birthCountry", shouldInstanceMethodName = "true", position = 6, fieldType = FieldType.CONTROLLED_LIST, fieldPath = ENTITY_CODE + ".placeOfBirth.address[0].countryCode")
    private ConceptDTO placeOfBirthCountry;

    @CustomizableCLField(targetFramework = ControlledList.COUNTRY, descriptionLabelKey = "customization.description.cl.country")
    @CustomizableField(labelKey = "custom.field.person.citizenshipCountry", shouldInstanceMethodName = "true", position = 7, fieldType = FieldType.CONTROLLED_LIST, fieldPath = ENTITY_CODE + ".citizenshipCountry", size = 3)
    private ArrayList<ConceptDTO> citizenshipCountry;

    @CustomizableCLField(targetFramework = ControlledList.HUMAN_SEX, descriptionLabelKey = "customization.description.cl.gender")
    @CustomizableField(labelKey = "custom.field.person.gender", shouldInstanceMethodName = "true", position = 8, fieldType = FieldType.CONTROLLED_LIST, fieldPath = ENTITY_CODE + ".gender")
    private ConceptDTO gender;

    @CustomizableCLField(targetFramework = ControlledList.COUNTRY, descriptionLabelKey = "customization.description.cl.country")
    @CustomizableField(labelKey = "custom.field.person.nationalIdentifier.spatialId", shouldInstanceMethodName = "true", position = 9, fieldType = FieldType.CONTROLLED_LIST, fieldPath = ENTITY_CODE + ".nationalID.spatial",
            validation = Customization.VALIDATION_LENGTH + "(" + "255" + ")" + ","
                    + Customization.VALIDATION_MANDATORYIF + "(" + Customization.FieldGroup.GROUP_IDENTIFIER + ")"
            , relatesTo = ENTITY_CODE + ".nationalID.notation")
    private ConceptDTO nationalIdentifierSpatialId;
    @CustomizableField(labelKey = "custom.field.person.nationalIdentifier.content", shouldInstanceMethodName = "true", position = 10, fieldType = FieldType.TEXT, fieldPath = ENTITY_CODE + ".nationalID.notation",
            validation = Customization.VALIDATION_LENGTH + "(" + "255" + ")" + "," + Customization.VALIDATION_MANDATORYIF + "(" + Customization.FieldGroup.GROUP_IDENTIFIER + ")"
            , relatesTo = ENTITY_CODE + ".nationalID.spatial")
    private String nationalIdentifier;

    @CustomizableField(labelKey = "custom.field.person.otherIdentifier.scheme", shouldInstanceMethodName = "true", position = 11, fieldType = FieldType.TEXT, fieldPath = ENTITY_CODE + ".identifier[0].schemeName",
            validation = Customization.VALIDATION_LENGTH + "(" + "255" + ")"
                    + Customization.VALIDATION_MANDATORYIF + "(" + Customization.FieldGroup.GROUP_OTHER_IDENTIFIER + ")"
            , relatesTo = ENTITY_CODE + ".identifier[0].notation")
    private String identifierScheme;
    @CustomizableField(labelKey = "custom.field.person.otherIdentifier.content", shouldInstanceMethodName = "true", position = 12, fieldType = FieldType.TEXT, fieldPath = ENTITY_CODE + ".identifier[0].notation",
            validation = Customization.VALIDATION_LENGTH + "(" + "255" + ")"
                    + Customization.VALIDATION_MANDATORYIF + "(" + Customization.FieldGroup.GROUP_OTHER_IDENTIFIER + ")"
            , relatesTo = ENTITY_CODE + ".identifier[0].schemeName")
    private String identifier;
    @CustomizableField(labelKey = "custom.field.person.address", shouldInstanceMethodName = "true", position = 13, fieldType = FieldType.TEXT, fieldPath = ENTITY_CODE + ".contactPoint[0].address[0].fullAddress.noteLiteral",
            validation = Customization.VALIDATION_LENGTH + "(" + "255" + ")" + "," + Customization.VALIDATION_MANDATORYIF + "(" + Customization.FieldGroup.GROUP_ADDRESS + ")"
            , relatesTo = ENTITY_CODE + ".contactPoint[0].address[0].countryCode")
    private String address;
    @CustomizableCLField(targetFramework = ControlledList.COUNTRY, descriptionLabelKey = "customization.description.cl.country")
    @CustomizableField(labelKey = "custom.field.person.address.country", shouldInstanceMethodName = "true", position = 14, fieldType = FieldType.CONTROLLED_LIST, fieldPath = ENTITY_CODE + ".contactPoint[0].address[0].countryCode",
            validation = Customization.VALIDATION_MANDATORYIF + "(" + Customization.FieldGroup.GROUP_ADDRESS + ")"
            , relatesTo = ENTITY_CODE + ".contactPoint[0].address[0].fullAddress.noteLiteral")
    private ConceptDTO addressCountry;
    @CustomizableField(labelKey = "custom.field.person.groupName", shouldInstanceMethodName = "true", position = 16, fieldType = FieldType.TEXT, fieldPath = ENTITY_CODE + ".groupMemberOf[0].prefLabel",
            validation = Customization.VALIDATION_LENGTH + "(" + "255" + ")")
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

    public ConceptDTO getPlaceOfBirthCountry() {
        return placeOfBirthCountry;
    }

    public void setPlaceOfBirthCountry(ConceptDTO placeOfBirthCountry) {
        this.placeOfBirthCountry = placeOfBirthCountry;
    }

    public ArrayList<ConceptDTO> getCitizenshipCountry() {
        return citizenshipCountry;
    }

    public void setCitizenshipCountry(ArrayList<ConceptDTO> citizenshipCountry) {
        this.citizenshipCountry = citizenshipCountry;
    }

    public ConceptDTO getGender() {
        return gender;
    }

    public void setGender(ConceptDTO gender) {
        this.gender = gender;
    }

    public ConceptDTO getNationalIdentifierSpatialId() {
        return nationalIdentifierSpatialId;
    }

    public void setNationalIdentifierSpatialId(ConceptDTO nationalIdentifierSpatialId) {
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

    public ConceptDTO getAddressCountry() {
        return addressCountry;
    }

    public void setAddressCountry(ConceptDTO addressCountry) {
        this.addressCountry = addressCountry;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }
}
