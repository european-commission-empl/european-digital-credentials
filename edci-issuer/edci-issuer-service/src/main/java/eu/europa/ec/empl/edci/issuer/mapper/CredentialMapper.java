package eu.europa.ec.empl.edci.issuer.mapper;

import eu.europa.ec.empl.edci.constants.DataModelConstants;
import eu.europa.ec.empl.edci.constants.EDCIConstants;
import eu.europa.ec.empl.edci.datamodel.jsonld.model.*;
import eu.europa.ec.empl.edci.datamodel.jsonld.model.dataTypes.ConceptDTO;
import eu.europa.ec.empl.edci.datamodel.jsonld.model.dataTypes.LegalIdentifier;
import eu.europa.ec.empl.edci.datamodel.jsonld.model.dataTypes.LiteralMap;
import eu.europa.ec.empl.edci.exception.clientErrors.EDCIBadRequestException;
import eu.europa.ec.empl.edci.issuer.common.model.CredentialDTO;
import eu.europa.ec.empl.edci.issuer.common.model.EuropeanDigitalCredentialUploadDTO;
import eu.europa.ec.empl.edci.issuer.common.model.RecipientDataDTO;
import eu.europa.ec.empl.edci.issuer.entity.specs.EuropassCredentialSpecDAO;
import eu.europa.ec.empl.edci.issuer.entity.specs.LearningAchievementSpecDAO;
import eu.europa.ec.empl.edci.issuer.entity.specs.LearningAssessmentSpecDAO;
import eu.europa.ec.empl.edci.issuer.mapper.datamodel.*;
import eu.europa.ec.empl.edci.mapper.commons.StringDateMapping;
import eu.europa.ec.empl.edci.service.ControlledListCommonsService;
import eu.europa.ec.empl.edci.util.CredentialUtil;
import eu.europa.ec.empl.edci.util.MultilangFieldUtil;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.net.URI;
import java.util.*;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", uses = {BaseDAOMapper.class, VariousObjectsMapper.class, StringDateMapping.class, AgentOrganizationMapper.class,
        LearningAchievementMapper.class,
        EntitlementMapper.class,
        LearningActivityMapper.class,
        AssessmentMapper.class})
public abstract class CredentialMapper {

    @Autowired
    private AssessmentMapper assessmentMapper;

    @Autowired
    private EntitlementMapper entitlementMapper;

    @Autowired
    private LearningActivityMapper learningActivityMapper;

    @Autowired
    private LearningAchievementMapper learningAchievementMapper;

    @Autowired
    private ControlledListCommonsService controlledListCommonsService;

    @Autowired
    private CredentialUtil credentialUtil;

    @Mappings({
            @Mapping(source = "europeanDigitalCredentialDTO.id", target = "uuid", qualifiedByName = "getUuid"),
            @Mapping(source = "europeanDigitalCredentialDTO.credentialSubject", qualifiedByName = "getStudentName", target = "studentName"),
            @Mapping(source = "europeanDigitalCredentialDTO.issuer", qualifiedByName = "getIssuerName", target = "issuerName"),
            @Mapping(source = "europeanDigitalCredentialDTO.credentialSubject", qualifiedByName = "getEmail", target = "email"),
            @Mapping(source = "europeanDigitalCredentialDTO.displayParameter.title", target = "course"),
            @Mapping(source = "europeanDigitalCredentialDTO.displayParameter.primaryLanguage", qualifiedByName = "getLanguage", target = "primaryLanguage"),
            @Mapping(target = "type", ignore = true)
    })
    public abstract CredentialDTO toDTO(EuropeanDigitalCredentialDTO europeanDigitalCredentialDTO, @Context String locale);

    public CredentialDTO toDTO(EuropeanDigitalCredentialUploadDTO europeanDigitalCredentialUploadDTO, @Context String locale) {
        CredentialDTO credentialDTO = this.toDTO(europeanDigitalCredentialUploadDTO.getCredential(), locale);
        if (europeanDigitalCredentialUploadDTO.getDeliveryDetails() != null) {
            credentialDTO.setWalletAddress(europeanDigitalCredentialUploadDTO.getDeliveryDetails().getWalletAddress());
        }
        return credentialDTO;
    }

    public abstract List<CredentialDTO> toDTOList(List<EuropeanDigitalCredentialUploadDTO> europeanDigitalCredentialUploadDTOS, @Context String locale);

    public abstract List<CredentialDTO> europassToDTOList(List<EuropeanDigitalCredentialDTO> europassCredentialDTOS, @Context String locale);

    @Named("getUuid")
    public String getUuid(URI uri, @Context String locale) {
        return uri.toString();
    }

    @Named("getStudentName")
    public String getStudentName(PersonDTO credentialSubject, @Context String locale) {
        return this.credentialUtil.getAvailableName(credentialSubject, locale);
    }

    @Named("getIssuerName")
    public String getIssuerName(OrganisationDTO organizationDTO, @Context String locale) {
        return organizationDTO == null ? "" : MultilangFieldUtil.getLiteralString(organizationDTO.getLegalName(), locale).orElse(EDCIConstants.StringPool.STRING_EMPTY);
    }

    @Named("getEmail")
    public String getEmail(PersonDTO personDTO) {
        Optional<ContactPointDTO> contactPoint = Optional.empty();
        if (personDTO != null && personDTO.getContactPoint() != null) {
            contactPoint = personDTO.getContactPoint().stream().filter(cPoint -> cPoint.getEmailAddress() != null && !cPoint.getEmailAddress().isEmpty()).findFirst();
        }
        return contactPoint.isPresent() ? contactPoint.get().getEmailAddress().get(0).getId().toString().replaceAll(DataModelConstants.Defaults.DEFAULT_MAILTO, "") : "";
    }

    @Mappings({
            @Mapping(source = "credential", target = "credentialSubject.hasClaim", qualifiedByName = "getClaims"),
            @Mapping(source = "credential.defaultLanguage", target = "displayParameter.primaryLanguage", qualifiedByName = "getControlledList"),
            @Mapping(source = "credential.languages", target = "displayParameter.language", qualifiedByName = "getControlledList"),
            @Mapping(source = "validUntil", target = "expirationDate"),
            @Mapping(source = "validUntil", target = "validUntil"),
            @Mapping(source = "title", target = "displayParameter.title"),
            @Mapping(source = "description", target = "displayParameter.description"),
            @Mapping(source = "credentialLabel", target = "credentialProfiles"),
            @Mapping(target = "id", ignore = true)
    })
    public abstract EuropeanDigitalCredentialDTO toDTO(EuropassCredentialSpecDAO credential);

    @Named("getControlledList")
    public ConceptDTO getControlledList(String lang) {
        if (lang == null) lang = "";
        return controlledListCommonsService.searchLanguageByLang(lang);
    }

    @Named("getLanguage")
    public String getLanguage(ConceptDTO conceptDTO) {
        if (conceptDTO == null) return null;
        return controlledListCommonsService.searchLanguageISO639ByConcept(conceptDTO);
    }

    @Named("getClaims")
    public List<ClaimDTO> getClaims(EuropassCredentialSpecDAO credential) {
        List<ClaimDTO> claimDTOList = new ArrayList<>();

        if (credential.getAchieved() != null)
            claimDTOList.addAll(learningAchievementMapper.toDTOList(credential.getAchieved().stream().collect(Collectors.toList())));
        if (credential.getAssessedBy() != null)
            claimDTOList.addAll(assessmentMapper.toDTOList(credential.getAssessedBy().stream().collect(Collectors.toList())));
        if (credential.getPerformed() != null)
            claimDTOList.addAll(learningActivityMapper.toDTOList(credential.getPerformed().stream().collect(Collectors.toList())));
        if (credential.getEntitledTo() != null)
            claimDTOList.addAll(entitlementMapper.toDTOList(credential.getEntitledTo().stream().collect(Collectors.toList())));

        return claimDTOList;
    }

    public LegalIdentifier recipientDataDTOToLegalIdentifier(RecipientDataDTO recipientDataDTO) {
        LegalIdentifier mappingTarget = null;
        if (recipientDataDTO.getNationalIdentifier() != null || recipientDataDTO.getNationalIdentifierSpatialId() != null) {
            mappingTarget = new LegalIdentifier();
            mappingTarget.setId(URI.create(recipientDataDTO.getNationalIdentifier()));
            ConceptDTO conceptDTO = new ConceptDTO();
            conceptDTO.setId(URI.create(recipientDataDTO.getNationalIdentifier()));
            ConceptDTO spatial = recipientDataDTO.getNationalIdentifierSpatialId();
            mappingTarget.setSpatial(spatial);
        }

        return mappingTarget;
    }

    @BeforeMapping
    public void fillGrades(EuropassCredentialSpecDAO credential, RecipientDataDTO recipient, Map<Long, String> achievementAssGrades,
                           @Context String locale) {

        if (achievementAssGrades != null || !achievementAssGrades.isEmpty()) {

            Set<LearningAchievementSpecDAO> achList = new HashSet<>();
            for (LearningAchievementSpecDAO ach : credential.getAchieved()) {
                getAllSubAchievements(ach, achList);
            }
            Set<LearningAssessmentSpecDAO> assList = new HashSet<>();
            for (LearningAchievementSpecDAO ach : achList) {
                //TODO: new D.M
                /*for (LearningAssessmentSpecDAO ass : ach.getWasDerivedFrom()) {
                    getAllSubAsessments(ass, assList);
                }*/
            }
            for (LearningAssessmentSpecDAO ass : assList) {
                if (achievementAssGrades.keySet().contains(ass.getPk())) {
                    //TODO: new D.M
                    //ass.setGrade(new ScoreDTDAO(achievementAssGrades.get(ass.getPk()), null));
                }
            }
        }
    }

    public void getAllSubAchievements(LearningAchievementSpecDAO ach, Set<LearningAchievementSpecDAO> achList) {

        for (LearningAchievementSpecDAO aux : ach.getHasPart()) {
            getAllSubAchievements(aux, achList);
        }

        achList.add(ach);

    }

    public void getAllSubAsessments(LearningAssessmentSpecDAO assm, Set<LearningAssessmentSpecDAO> assmList) {

        for (LearningAssessmentSpecDAO aux : assm.getHasPart()) {
            getAllSubAsessments(aux, assmList);
        }

        assmList.add(assm);

    }


    @AfterMapping
    public void fillCredentialSubject(EuropassCredentialSpecDAO credential, RecipientDataDTO recipient, Map<Long, String> assessments,
                                      @Context String locale, @MappingTarget EuropeanDigitalCredentialDTO result) {

        result.getCredentialSubject().setFullName(new LiteralMap(recipient.getFirstName() + EDCIConstants.StringPool.STRING_SPACE + recipient.getFamilyName(), locale));
        result.getCredentialSubject().setContactPoint(generateContactPoint(recipient, locale));

        if (recipient.getAddress() != null || recipient.getAddressCountry() != null) {
            LocationDTO loc = new LocationDTO();
            AddressDTO addr = new AddressDTO();
            if (recipient.getAddress() != null) {
                //ToDo -> Review with new notes
                //addr.setFullAddress(new NoteDTO(locale, recipient.getAddress()));
            }
            addr.setCountryCode(recipient.getAddressCountry());
            loc.setAddress(Arrays.asList(addr));
            result.getCredentialSubject().setLocation(Arrays.asList(loc));
        }

        if (recipient.getPlaceOfBirthCountry() != null) {
            LocationDTO placeOfBirth = new LocationDTO();
            AddressDTO placeOfBirthAddr = new AddressDTO();
            placeOfBirthAddr.setCountryCode(recipient.getPlaceOfBirthCountry());
            List<AddressDTO> placeOfBirthHasAddress = new ArrayList<>();
            placeOfBirthHasAddress.add(placeOfBirthAddr);
            placeOfBirth.setAddress(placeOfBirthHasAddress);
            result.getCredentialSubject().setPlaceOfBirth(placeOfBirth);
        }

        result.getCredentialSubject().setNationalID(recipientDataDTOToLegalIdentifier(recipient));
    }


    public List<MailboxDTO> toMailboxEmailList(String emailList, @Context String locale) {

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

    public URI generateUri(String prefix) {

        try {
            return new java.net.URI(prefix.concat(UUID.randomUUID().toString()));
        } catch (Exception e) {
            return null;
        }
    }

    public List<ContactPointDTO> generateContactPoint(RecipientDataDTO recipientDataView, @Context String locale) {

        List<ContactPointDTO> contactPoint = new ArrayList<>();

        ContactPointDTO cp = new ContactPointDTO();
       /* if (StringUtils.isNotEmpty(recipientDataView.getEmailAddress())) {
            cp.setEmailAddress(toMailboxEmailList(recipientDataView.getEmailAddress(), locale));
        }*/

       /*if (StringUtils.isNotEmpty(recipientDataView.getWalletAddress())) {

            List<String> walletAddr = new ArrayList<>();
            walletAddr.add(recipientDataView.getWalletAddress());

            cp.setWalletAddress(walletAddr);
        }*/

        contactPoint.add(cp);

        return contactPoint;
    }

}
