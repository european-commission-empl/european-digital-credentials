package eu.europa.ec.empl.edci.model.external.mapper;

import eu.europa.ec.empl.edci.datamodel.jsonld.model.*;
import eu.europa.ec.empl.edci.datamodel.jsonld.model.dataTypes.*;
import eu.europa.ec.empl.edci.model.external.qdr.*;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.time.ZonedDateTime;
import java.util.List;

@Mapper(componentModel = "spring", uses = {QDRBaseMapper.class}/*, unmappedTargetPolicy = ReportingPolicy.IGNORE*/)
public interface QDRAccreditationMapper {

    default LiteralMap toLiteralMap(String content, @Context String language) {
        return new LiteralMap(language, content);
    }

    default LiteralMap toLiteralMap(List<String> content, @Context String language) {
        return new LiteralMap(language, content);
    }

    default ZonedDateTime toZonedDateTime(QDRValue value, @Context String language) {
        if(value == null) {
            return null;
        }

        try {
            return ZonedDateTime.parse(value.getValue());
        } catch (Exception e) {
            return ZonedDateTime.parse(value.getValue().concat("Z"));
        }
    }

    @Mappings({
            @Mapping(target = "id", source = "uri"),
            @Mapping(target = "dcType", source = "type"),
            @Mapping(target = "dateModified", source = "modified"),
            @Mapping(target = "dateIssued", source = "issued"),
            @Mapping(target = "type", ignore = true)
    })
    AccreditationDTO toAccreditationDTO(QDRAccreditationDTO qdr, @Context String language);

    @Mappings({
            @Mapping(target = "id", source = "uri"),
            @Mapping(target = "type", ignore = true)
    })
    AddressDTO toAddressDTO(QDRAddressDTO qdr, @Context String language);

    @Mappings({
            @Mapping(target = "id", source = "uri"),
            @Mapping(target = "type", ignore = true)
    })
    ConceptDTO toConceptDTO(QDRConceptDTO qdr, @Context String language);

    @Mappings({
            @Mapping(target = "id", source = "uri"),
            @Mapping(target = "type", ignore = true)
    })
    ConceptSchemeDTO toConceptSchemeDTO(QDRConceptSchemeDTO qdr, @Context String language);

    default ConceptSchemeDTO toConceptSchemeDTOFromList(List<QDRConceptSchemeDTO> qdr, @Context String language) {
        if(qdr != null && !qdr.isEmpty()) {
            return toConceptSchemeDTO(qdr.get(0), language);
        }

        return null;
    }

    @Mappings({
            @Mapping(target = "id", source = "uri"),
            @Mapping(target = "emailAddress", source = "mailbox"),
            @Mapping(target = "type", ignore = true)
    })
    ContactPointDTO toContactPointDTO(QDRContactPointDTO qdr, @Context String language);

    @Mappings({
            @Mapping(target = "id", source = "uri"),
            @Mapping(target = "type", ignore = true)
    })
    CreditPointDTO toCreditPointDTO(QDRCreditPointDTO qdr, @Context String language);

    @Mappings({
            @Mapping(target = "id", source = "uri"),
            @Mapping(target = "type", ignore = true)
    })
    GeometryDTO toGeometry(QDRGeometryDTO qdr, @Context String language);

    @Mappings({
            @Mapping(target = "id", source = "uri"),
            @Mapping(target = "type", ignore = true)
    })
    GradingSchemeDTO toGradingSchemeDTO(QDRGradingSchemeDTO qdr, @Context String language);

    @Mappings({
            @Mapping(target = "id", source = "uri"),
            @Mapping(target = "dcType", source = "type"),
            @Mapping(target = "member", ignore = true),
            @Mapping(target = "type", ignore = true)
    })
    GroupDTO toGroupDTO(QDRGroupDTO qdr, @Context String language);

    @Mappings({
            @Mapping(target = "id", source = "uri"),
            @Mapping(target = "dcType", source = "type"),
            @Mapping(target = "dateIssued", source = "issued"),
            @Mapping(target = "type", ignore = true)
    })
    Identifier toIdentifier(QDRIdentifier qdr, @Context String language);

    @Mappings({
            @Mapping(target = "id", source = "uri"),
            @Mapping(target = "dcType", source = "type"),
            @Mapping(target = "dateModified", source = "modified"),
            @Mapping(target = "type", ignore = true)
    })
    LearningAchievementSpecificationDTO toLearningAchievementSpecificationDTO(QDRLearningAchievementSpecificationDTO qdr, @Context String language);

    @Mappings({
            @Mapping(target = "id", source = "uri"),
            @Mapping(target = "dcType", source = "type"),
            @Mapping(target = "dateModified", source = "modified"),
            @Mapping(target = "type", ignore = true)
    })
    LearningActivitySpecificationDTO toLearningActivitySpecification(QDRLearningActivitySpecificationDTO qdr, @Context String language);

    @Mappings({
            @Mapping(target = "id", source = "uri"),
            @Mapping(target = "dcType", source = "type"),
            @Mapping(target = "dateModified", source = "modified"),
            @Mapping(target = "type", ignore = true)
    })
    LearningAssessmentSpecificationDTO toLearningAssessmentSpecificationDTO(QDRLearningAssessmentSpecificationDTO qdr, @Context String language);

    @Mappings({
            @Mapping(target = "id", source = "uri"),
            @Mapping(target = "dcType", source = "type"),
            @Mapping(target = "dateModified", source = "modified"),
            @Mapping(target = "type", ignore = true)
    })
    LearningEntitlementSpecificationDTO toLearningEntitlementSpecificationDTO(QDRLearningEntitlementSpecificationDTO qdr, @Context String language);

    @Mappings({
            @Mapping(target = "id", source = "uri"),
            @Mapping(target = "dcType", source = "type"),
            @Mapping(target = "type", ignore = true)
    })
    LearningOutcomeDTO toLearningOutcomeDTO(QDRLearningOutcomeDTO qdr, @Context String language);

    @Mappings({
            @Mapping(target = "id", source = "uri"),
            @Mapping(target = "dcType", source = "type"),
            @Mapping(target = "dateIssued", source = "issued"),
            @Mapping(target = "type", ignore = true)
    })
    LegalIdentifier toLegalIdentifier(QDRLegalIdentifier qdr, @Context String language);

    @Mappings({
            @Mapping(target = "id", source = "uri"),
            @Mapping(target = "type", ignore = true)
    })
    LocationDTO toLocationDTO(QDRLocationDTO qdr, @Context String language);

    @Mappings({
            @Mapping(target = "id", source = "uri"),
            @Mapping(target = "type", ignore = true)
    })
    MailboxDTO toMailboxDTO(QDRMailboxDTO qdr, @Context String language);

    @Mappings({
            @Mapping(target = "id", source = "uri"),
            @Mapping(target = "type", ignore = true)
    })
    MediaObjectDTO toMediaObjectDTO(QDRMediaObjectDTO qdr, @Context String language);

    @Mappings({
            @Mapping(target = "id", source = "uri"),
            @Mapping(target = "type", ignore = true)
    })
    NoteDTO toNoteDTO(QDRNoteDTO qdr, @Context String language);

    @Mappings({
            @Mapping(target = "id", source = "uri"),
            @Mapping(target = "dateModified", source = "modified"),
            @Mapping(target = "eIDASIdentifier", source = "eidasLegalIdentifier"),
            @Mapping(target = "type", ignore = true)
    })
    OrganisationDTO toOrganisationDTO(QDROrganisationDTO qdr, @Context String language);

    @Mappings({
            @Mapping(target = "id", source = "uri"),
            @Mapping(target = "type", ignore = true)
    })
    PersonDTO toPersonDTO(QDRPersonDTO qdr, @Context String language);

    @Mappings({
            @Mapping(target = "id", source = "uri"),
            @Mapping(target = "type", ignore = true)
    })
    PhoneDTO toPhoneDTO(QDRPhoneDTO qdr, @Context String language);

    @Mappings({
            @Mapping(target = "id", source = "uri"),
            @Mapping(target = "dcType", source = "type"),
            @Mapping(target = "type", ignore = true)
    })
    QualificationDTO toQualificationDTO(QDRQualificationDTO qdr, @Context String language);

    @Mappings({
            @Mapping(target = "id", source = "uri"),
            @Mapping(target = "type", ignore = true),
            @Mapping(target = "contentURL", source = "contentUrl")
    })
    WebResourceDTO toWebResourceDTO(QDRWebResourceDTO qdr, @Context String language);
}
