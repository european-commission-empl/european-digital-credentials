package eu.europa.ec.empl.edci.issuer.mapper;

import eu.europa.ec.empl.edci.constants.EDCIConfig;
import eu.europa.ec.empl.edci.constants.EDCIConstants;
import eu.europa.ec.empl.edci.datamodel.model.*;
import eu.europa.ec.empl.edci.datamodel.model.dataTypes.LegalIdentifier;
import eu.europa.ec.empl.edci.datamodel.model.dataTypes.Note;
import eu.europa.ec.empl.edci.datamodel.model.dataTypes.Text;
import eu.europa.ec.empl.edci.exception.clientErrors.EDCIBadRequestException;
import eu.europa.ec.empl.edci.issuer.common.constants.IssuerConstants;
import eu.europa.ec.empl.edci.issuer.common.model.CredentialDTO;
import eu.europa.ec.empl.edci.issuer.common.model.RecipientDataDTO;
import eu.europa.ec.empl.edci.issuer.entity.dataTypes.ScoreDTDAO;
import eu.europa.ec.empl.edci.issuer.entity.specs.AssessmentSpecDAO;
import eu.europa.ec.empl.edci.issuer.entity.specs.DiplomaSpecDAO;
import eu.europa.ec.empl.edci.issuer.entity.specs.EuropassCredentialSpecDAO;
import eu.europa.ec.empl.edci.issuer.entity.specs.LearningAchievementSpecDAO;
import eu.europa.ec.empl.edci.issuer.mapper.datamodel.*;
import eu.europa.ec.empl.edci.mapper.commons.StringDateMapping;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.*;

import java.net.URI;
import java.util.*;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", uses = {VariousObjectsMapper.class, StringDateMapping.class, AgentOrganizationMapper.class,
        LearningAchievementMapper.class,
        EntitlementMapper.class,
        LearningActivityMapper.class,
        AssessmentMapper.class})
public interface CredentialMapper {

    @Mappings({
            @Mapping(source = "xmlCredentialDTO.id", target = "uuid", qualifiedByName = "getUuid"),
            @Mapping(source = "xmlCredentialDTO.credentialSubject", qualifiedByName = "getWalletAddress", target = "walletAddress"),
            @Mapping(source = "xmlCredentialDTO.credentialSubject.fullName", qualifiedByName = "getStudentName", target = "studentName"),
            @Mapping(source = "xmlCredentialDTO.title", qualifiedByName = "getCourseNamed", target = "course"),
            @Mapping(source = "xmlCredentialDTO.issuer", qualifiedByName = "getIssuerName", target = "issuerName"),
            @Mapping(source = "xmlCredentialDTO.credentialSubject", qualifiedByName = "getEmail", target = "email"),
            @Mapping(source = "xmlCredentialDTO.type.uri", target = "type")
    })
    CredentialDTO toDTO(EuropassCredentialDTO xmlCredentialDTO, @Context String locale);

    List<CredentialDTO> europassToDTOList(List<EuropassCredentialDTO> europassCredentialDTOS, @Context String locale);

    @Named("getUuid")
    default String getUuid(URI uri, @Context String locale) {
        return uri.toString();
    }

    @Named("getStudentName")
    default String getStudentName(Text fullName, @Context String locale) {
        return fullName != null ? fullName.getStringContent(locale) : null;
    }

    @Named("getCourseNamed")
    default String getTitleTranslation(Text title, @Context String locale/**/) {
        return title != null ? title.getStringContent(locale) : null;
    }

    @Named("getIssuerName")
    default String getIssuerName(OrganizationDTO organizationDTO, @Context String locale) {
        return organizationDTO == null ? "" : organizationDTO.getPreferredName().getStringContent(locale);
    }

    @Named("getEmail")
    default String getEmail(PersonDTO personDTO) {
        Optional<ContactPoint> contactPoint = Optional.empty();
        if (personDTO != null && personDTO.getContactPoint() != null) {
            contactPoint = personDTO.getContactPoint().stream().filter(cPoint -> cPoint.getEmail() != null && !cPoint.getEmail().isEmpty()).findFirst();
        }
        return contactPoint.isPresent() ? contactPoint.get().getEmail().get(0).getId().toString().replaceAll(EDCIConfig.Defaults.DEFAULT_MAILTO, "") : "";
    }

    @Named("getWalletAddress")
    default String getWalletAddress(PersonDTO personDTO) {
        Optional<ContactPoint> contactPoint = Optional.empty();
        if (personDTO != null && personDTO.getContactPoint() != null) {
            contactPoint = personDTO.getContactPoint().stream().filter(cPoint -> cPoint.getWalletAddress() != null && !cPoint.getWalletAddress().isEmpty()).findFirst();
        }
        return contactPoint.isPresent() ? contactPoint.get().getWalletAddress().get(0) : "";
    }

    @Mappings({
            @Mapping(target = "valid", expression = "java( true )"),
            @Mapping(source = "recipient.firstName", target = "credentialSubject.givenNames"),
            @Mapping(source = "recipient.lastName", target = "credentialSubject.familyName"),
            @Mapping(source = "recipient.dateOfBirth", target = "credentialSubject.dateOfBirth"),

            @Mapping(source = "recipient.citizenshipCountry", target = "credentialSubject.citizenshipCountry"),
            @Mapping(source = "recipient.gender", target = "credentialSubject.gender"),

            @Mapping(source = "credential.achieved", target = "credentialSubject.achieved"),
            @Mapping(source = "credential.performed", target = "credentialSubject.performed"),
            @Mapping(source = "credential.entitledTo", target = "credentialSubject.entitledTo"),
            @Mapping(source = "credential.defaultLanguage", target = "primaryLanguage"),
            @Mapping(source = "credential.languages", target = "availableLanguages"),
            @Mapping(source = "credential.languages", target = "languages", ignore = true)
    })
    EuropassCredentialDTO toDTO(EuropassCredentialSpecDAO credential, RecipientDataDTO recipient, Map<Long, String> assessments, @Context String locale);

    default LegalIdentifier recipientDataDTOToLegalIdentifier(RecipientDataDTO recipientDataDTO) {
        LegalIdentifier mappingTarget = null;
        if (recipientDataDTO.getNationalIdentifier() != null || recipientDataDTO.getNationalIdentifierSpatialId() != null) {
            mappingTarget = new LegalIdentifier();

            mappingTarget.setContent(recipientDataDTO.getNationalIdentifier());
            mappingTarget.setSpatialId(recipientDataDTO.getNationalIdentifierSpatialId());
        }

        return mappingTarget;
    }

    @BeforeMapping
    default void fillGrades(EuropassCredentialSpecDAO credential, RecipientDataDTO recipient, Map<Long, String> achievementAssGrades,
                            @Context String locale) {

        if (achievementAssGrades != null || !achievementAssGrades.isEmpty()) {

            Set<LearningAchievementSpecDAO> achList = new HashSet<>();
            for (LearningAchievementSpecDAO ach : credential.getAchieved()) {
                getAllSubAchievements(ach, achList);
            }
            Set<AssessmentSpecDAO> assList = new HashSet<>();
            for (LearningAchievementSpecDAO ach : achList) {
                for (AssessmentSpecDAO ass : ach.getWasDerivedFrom()) {
                    getAllSubAsessments(ass, assList);
                }
            }
            for (AssessmentSpecDAO ass : assList) {
                if (achievementAssGrades.keySet().contains(ass.getPk())) {
                    ass.setGrade(new ScoreDTDAO(achievementAssGrades.get(ass.getPk()), null));
                }
            }
        }
    }

    default void getAllSubAchievements(LearningAchievementSpecDAO ach, Set<LearningAchievementSpecDAO> achList) {

        for (LearningAchievementSpecDAO aux : ach.getHasPart()) {
            getAllSubAchievements(aux, achList);
        }

        achList.add(ach);

    }

    default void getAllSubAsessments(AssessmentSpecDAO assm, Set<AssessmentSpecDAO> assmList) {

        for (AssessmentSpecDAO aux : assm.getHasPart()) {
            getAllSubAsessments(aux, assmList);
        }

        assmList.add(assm);

    }


    @AfterMapping
    default void fillCredentialSubject(EuropassCredentialSpecDAO credential, RecipientDataDTO recipient, Map<Long, String> assessments,
                                       @Context String locale, @MappingTarget EuropassCredentialDTO result) {

        result.getCredentialSubject().setFullName(new Text(recipient.getFirstName() + EDCIConstants.StringPool.STRING_SPACE + recipient.getLastName(), locale));
        result.getCredentialSubject().setContactPoint(generateContactPoint(recipient, locale));

        if (recipient.getAddress() != null || recipient.getAddressCountry() != null) {
            LocationDTO loc = new LocationDTO();
            AddressDTO addr = new AddressDTO();
            if (recipient.getAddress() != null) {
                addr.setFullAddress(new Note(recipient.getAddress(), locale));
            }
            addr.setCountryCode(recipient.getAddressCountry());
            loc.setHasAddress(Arrays.asList(addr));
            result.getCredentialSubject().setHasLocation(Arrays.asList(loc));
        }

        if (recipient.getPlaceOfBirthCountry() != null) {
            LocationDTO placeOfBirth = new LocationDTO();
            AddressDTO placeOfBirthAddr = new AddressDTO();
            placeOfBirthAddr.setCountryCode(recipient.getPlaceOfBirthCountry());
            List<AddressDTO> placeOfBirthHasAddress = new ArrayList<>();
            placeOfBirthHasAddress.add(placeOfBirthAddr);
            placeOfBirth.setHasAddress(placeOfBirthHasAddress);
            result.getCredentialSubject().setPlaceOfBirth(placeOfBirth);
        }

        result.getCredentialSubject().setNationalId(recipientDataDTOToLegalIdentifier(recipient));
    }


    default List<MailboxDTO> toMailboxEmailList(String emailList, @Context String locale) {

        if (emailList == null) {
            return null;
        }

        List<MailboxDTO> mailboxDTO;

        try {
            mailboxDTO = Arrays.stream(emailList.split(",")).map(email -> new MailboxDTO() {{
                setId((org.springframework.util.StringUtils.hasLength(email) ? URI.create(email.trim()) : null));
            }}).collect(Collectors.toList());
        } catch (Exception e) {
            throw new EDCIBadRequestException().setCause(e).addDescription("EmailAddress field does not have a valid list of emails separated by comma");
        }

        return mailboxDTO;
    }

    default URI generateUri(String prefix) {

        try {
            return new java.net.URI(prefix.concat(UUID.randomUUID().toString()));
        } catch (Exception e) {
            return null;
        }
    }

    default List<ContactPoint> generateContactPoint(RecipientDataDTO recipientDataView, @Context String locale) {

        List<ContactPoint> contactPoint = new ArrayList<>();

        ContactPoint cp = new ContactPoint();
        if (StringUtils.isNotEmpty(recipientDataView.getEmailAddress())) {
            cp.setEmail(toMailboxEmailList(recipientDataView.getEmailAddress(), locale));
        }

        if (StringUtils.isNotEmpty(recipientDataView.getWalletAddress())) {

            List<String> walletAddr = new ArrayList<>();
            walletAddr.add(recipientDataView.getWalletAddress());

            cp.setWalletAddress(walletAddr);
        }

        contactPoint.add(cp);

        return contactPoint;
    }

    @Mappings({
            @Mapping(source = "diplomaSpecDAO", target = "html", qualifiedByName = "getDiplomaHTML"),
            @Mapping(source = "diplomaSpecDAO", target = "template", qualifiedByName = "getDiplomaTemplate")
    })
    DisplayParametersDTO toDiplomaDTO(DiplomaSpecDAO diplomaSpecDAO, @Context String locale);

    @Named("getDiplomaHTML")
    default String getDiplomaHTML(DiplomaSpecDAO diplomaSpecDAO) {
        String diplomaHTML = null;
        if (IssuerConstants.OCB_DIPLOMA_HTML.equals(diplomaSpecDAO.getFormat())) {
            diplomaHTML = diplomaSpecDAO.getHtml();
        }
        return diplomaHTML;
    }

    @Named("getDiplomaTemplate")
    default String getDiplomaTemplate(DiplomaSpecDAO diplomaSpecDAO) {
        String diplomaHTML = null;
        if (IssuerConstants.OCB_DIPLOMA_THYMELEAF.equals(diplomaSpecDAO.getFormat())) {
            diplomaHTML = diplomaSpecDAO.getHtml();
        }
        return diplomaHTML;
    }

}
