package eu.europa.ec.empl.edci.issuer.mapper.datamodel;

import eu.europa.ec.empl.edci.datamodel.model.AddressDTO;
import eu.europa.ec.empl.edci.datamodel.model.ContactPoint;
import eu.europa.ec.empl.edci.datamodel.model.MailboxDTO;
import eu.europa.ec.empl.edci.datamodel.model.PersonDTO;
import eu.europa.ec.empl.edci.datamodel.model.dataTypes.Code;
import eu.europa.ec.empl.edci.issuer.common.model.RecipientDataDTO;
import eu.europa.ec.empl.edci.mapper.commons.StringDateMapping;
import eu.europa.ec.empl.edci.util.Validator;
import org.mapstruct.*;

@Mapper(componentModel = "spring", uses = {VariousObjectsMapper.class, StringDateMapping.class})
public interface RecipientPersonMapper {

    public static final Validator validator = new Validator();

    @Mappings({
            @Mapping(target = "firstName", source = "givenNames"),
            @Mapping(target = "lastName", source = "familyName"),
            @Mapping(target = "dateOfBirth", source = "dateOfBirth"),

            @Mapping(target = "citizenshipCountry", source = "citizenshipCountry"),
            @Mapping(target = "nationalIdentifier", source = "nationalId.content"),
            @Mapping(target = "nationalIdentifierSpatialId", source = "nationalId.spatialId"),

    })
    public RecipientDataDTO toRecipientDTO(PersonDTO personDTO, @Context String locale);

    @AfterMapping
    default void toRecipientDTO(PersonDTO personDTO, @MappingTarget RecipientDataDTO recipientDataDTO, @Context String locale) {

        Code placeOfBirthCountryCoded = validator.getValueNullSafe(() -> personDTO.getPlaceOfBirth().getSpatialCode().stream().findFirst().orElse(null));
        ContactPoint contactPoint = validator.getValueNullSafe(() -> personDTO.getContactPoint().stream().findFirst().orElse(null));
        AddressDTO addressDTO = validator.getValueNullSafe(() -> contactPoint.getPostalAddress().stream().findFirst().orElse(null));
        MailboxDTO email = validator.getValueNullSafe(() -> contactPoint.getEmail().stream().findFirst().orElse(null));
        String walletAddress = validator.getValueNullSafe(() -> contactPoint.getWalletAddress().stream().findFirst().orElse(null));
        recipientDataDTO.setPlaceOfBirthCountry(placeOfBirthCountryCoded);
        recipientDataDTO.setAddress(validator.getValueNullSafe(() -> addressDTO.getFullAddress().getLocalizedStringOrAny(locale.toString())));
        recipientDataDTO.setAddressCountry(validator.getValueNullSafe(() -> addressDTO.getCountryCode()));
        recipientDataDTO.setEmailAddress(validator.getValueNullSafe(() -> email.getId().toString()));
        recipientDataDTO.setWalletAddress(walletAddress);


    }
}
