package eu.europa.ec.empl.edci.issuer.mapper.datamodel;

import eu.europa.ec.empl.edci.datamodel.jsonld.model.*;
import eu.europa.ec.empl.edci.datamodel.jsonld.model.dataTypes.ConceptDTO;
import eu.europa.ec.empl.edci.issuer.common.model.RecipientDataDTO;
import eu.europa.ec.empl.edci.mapper.commons.StringDateMapping;
import eu.europa.ec.empl.edci.util.Validator;
import org.mapstruct.*;

@Mapper(componentModel = "spring", uses = {VariousObjectsMapper.class, BaseDAOMapper.class, StringDateMapping.class})
public interface RecipientPersonMapper {
    //TODO: Waiting for more information
    public static final Validator validator = new Validator();

    @Mappings({
            @Mapping(target = "dateOfBirth", source = "dateOfBirth"),
            @Mapping(target = "citizenshipCountry", source = "citizenshipCountry"),
            @Mapping(target = "identifier", ignore = true),
            @Mapping(target = "nationalIdentifier", source = "nationalID.notation"),
            @Mapping(target = "nationalIdentifierSpatialId", source = "nationalID.spatial"),

    })
    public RecipientDataDTO toRecipientDTO(PersonDTO personDTO, @Context String locale);

    @AfterMapping
    default void toRecipientDTO(PersonDTO personDTO, @MappingTarget RecipientDataDTO recipientDataDTO, @Context String locale) {

        ConceptDTO placeOfBirthCountryCoded = validator.getValueNullSafe(() -> personDTO.getPlaceOfBirth().getSpatialCode().stream().findFirst().orElse(null));
        ContactPointDTO contactPoint = validator.getValueNullSafe(() -> personDTO.getContactPoint().stream().findFirst().orElse(null));
        AddressDTO addressDTO = validator.getValueNullSafe(() -> contactPoint.getAddress().stream().findFirst().orElse(null));
        MailboxDTO email = validator.getValueNullSafe(() -> contactPoint.getEmailAddress().stream().findFirst().orElse(null));
        recipientDataDTO.setPlaceOfBirthCountry(placeOfBirthCountryCoded);
        recipientDataDTO.setAddress(validator.getValueNullSafe(() -> addressDTO.getFullAddress().toString()));
        recipientDataDTO.setAddressCountry(validator.getValueNullSafe(() -> addressDTO.getCountryCode()));
        recipientDataDTO.setEmailAddress(validator.getValueNullSafe(() -> email.getId().toString()));
        recipientDataDTO.setGroupName(validator.getValueNullSafe(() -> personDTO.getGroupMemberOf().stream().findFirst().orElse(null).getPrefLabel().toString()));
        recipientDataDTO.setIdentifier(validator.getValueNullSafe(() -> personDTO.getIdentifier().stream().findFirst().orElse(null).getNotation()));

    }
}
