package eu.europa.ec.empl.edci.util;

import eu.europa.ec.empl.edci.constants.ControlledList;
import eu.europa.ec.empl.edci.constants.ControlledListConcept;
import eu.europa.ec.empl.edci.datamodel.jsonld.model.*;
import eu.europa.ec.empl.edci.datamodel.jsonld.model.dataTypes.*;
import eu.europa.ec.empl.edci.service.ControlledListCommonsService;
import org.joda.time.Period;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
public class JsonLdFactoryUtil {
    private static String testUri = "http://data.europa.eu/snb/data-dev/accreditation/628c4d45-e715-4ec2-9efd-bcd159e803e8";

    private static ControlledListCommonsService controlledListCommonsService;
    @Autowired
    private ControlledListCommonsService controlledListCommonsServiceBean;

    @PostConstruct
    public void init() {
        this.controlledListCommonsService = this.controlledListCommonsServiceBean;
    }

    public static String generic_ttl = "http://dev.everisdx.io/datamodel/test/EDC-generic-full";
    public static String generic_full_URL = "http://data.europa.eu/snb/model/ap/edc-generic-full";
    public static String accredited_URL = "http://dev.everisdx.io/datamodel/test/EDC-accredited";
    public static String generic_no_cv = "http://dev.everisdx.io/datamodel/test/EDC-generic-no-cv";

    public static AccreditationDTO getDefaultAccreditation(AccreditationDTO accreditationDTO) {

        accreditationDTO.setAccreditingAgent(getDefaultOrganisation(new OrganisationDTO()));
        accreditationDTO.setOrganisation(Arrays.asList(getDefaultOrganisation(new OrganisationDTO())));
        accreditationDTO.setTitle(new LiteralMap(Locale.ENGLISH.toString(), "default value"));
        accreditationDTO.setDcType(getDefaultConcept(new ConceptDTO()));

        return accreditationDTO;
    }

    public static AddressDTO getDefaultAddress(AddressDTO addressDTO) {
        addressDTO.setCountryCode(getCountryConceptDTO());

        return addressDTO;
    }

    public static AmountDTO getDefaultAmount(AmountDTO amountDTO) {
        amountDTO.setUnit(getDefaultConcept(new ConceptDTO()));
        amountDTO.setValue(1L);

        return amountDTO;
    }

    public static AwardingOpportunityDTO getDefaultAwardingOpportunity(AwardingOpportunityDTO awardingOpportunityDTO) {
        awardingOpportunityDTO.setAwardingBody(null);
        awardingOpportunityDTO.setLearningAchievementSpecification(getDefaultLearningAchievementSpecification(new LearningAchievementSpecificationDTO()));
        awardingOpportunityDTO.getAwardingBody().addAll(Arrays.asList(getDefaultOrganisation(new OrganisationDTO())));

        return awardingOpportunityDTO;
    }

    public static AwardingProcessDTO getDefaultAwardingProcess(AwardingProcessDTO awardingProcessDTO) {
        awardingProcessDTO.getAwardingBody().addAll(Arrays.asList(getDefaultOrganisation(new OrganisationDTO())));

        return awardingProcessDTO;
    }

    public static ConceptDTO getDefaultConcept(ConceptDTO conceptDTO) {
        conceptDTO.setPrefLabel(new LiteralMap(Locale.ENGLISH.toString(), "default value"));

        return conceptDTO;
    }

    public static ContactPointDTO getDefaultContactPoint(ContactPointDTO contactPointDTO) {
        contactPointDTO.getEmailAddress().addAll(Arrays.asList(new MailboxDTO()));

        return contactPointDTO;
    }

    public static CreditPointDTO getDefaultCreditPoint(CreditPointDTO creditPointDTO) {
        creditPointDTO.setPoint("points");
        ConceptDTO conceptDTO = new ConceptDTO();
        conceptDTO.setPrefLabel(new LiteralMap(Locale.ENGLISH.toString(), "Not provided"));
        creditPointDTO.setFramework(conceptDTO);

        return creditPointDTO;
    }

    public static DisplayParameterDTO getDefaultDisplay(DisplayParameterDTO displayParameterDTO) {
        displayParameterDTO.setTitle(new LiteralMap(Locale.ENGLISH.toString(), "default value"));
        displayParameterDTO.getLanguage().addAll(Arrays.asList(getENLanguageConceptDTO()));
        displayParameterDTO.setPrimaryLanguage(getENLanguageConceptDTO());

        return displayParameterDTO;
    }

    public static EuropeanDigitalCredentialDTO getDefaultEuropeanDigitalCredential(EuropeanDigitalCredentialDTO europeanDigitalCredentialDTO) {
        //We assume that issuer and credentialSubject always comes
        europeanDigitalCredentialDTO.setDisplayParameter(getDefaultDisplay(new DisplayParameterDTO()));
        europeanDigitalCredentialDTO.setIssued(ZonedDateTime.now());
        europeanDigitalCredentialDTO.setValidFrom(ZonedDateTime.now());

        return europeanDigitalCredentialDTO;
    }

    public static GrantDTO getDefaultGrant(GrantDTO grantDTO) {
        grantDTO.setTitle(new LiteralMap(Locale.ENGLISH.toString(), "default value"));

        return grantDTO;
    }

    public static GroupDTO getDefaultGroup(GroupDTO groupDTO) {
        groupDTO.setPrefLabel(new LiteralMap(Locale.ENGLISH.toString(), "default value"));

        return groupDTO;
    }

    public static Identifier getDefaultIdentifier(Identifier identifier) {
        identifier.setNotation(new LiteralMap(Locale.ENGLISH.toString(), "default value").toString());

        return identifier;
    }

    public static LegalIdentifier getDefaultLegalIdentifier(LegalIdentifier identifier) {
        identifier.setNotation(new LiteralMap(Locale.ENGLISH.toString(), "default value").toString());
        identifier.setSpatial(getCountryConceptDTO());

        return identifier;
    }

    public static IndividualDisplayDTO getDefaultIndividualDisplay(IndividualDisplayDTO individualDisplayDTO) {
        individualDisplayDTO.getDisplayDetail().addAll(Arrays.asList(new DisplayDetailDTO()));

        return individualDisplayDTO;
    }

    public static LearningAchievementDTO getDefaultLearningAchievement(LearningAchievementDTO learningAchievementDTO) {
        learningAchievementDTO.setTitle(new LiteralMap(Locale.ENGLISH.toString(), "default value"));
        learningAchievementDTO.setAwardedBy(getDefaultAwardingProcess(new AwardingProcessDTO()));

        return learningAchievementDTO;
    }

    public static LearningAchievementSpecificationDTO getDefaultLearningAchievementSpecification(LearningAchievementSpecificationDTO learningAchievementSpecificationDTO) {
        learningAchievementSpecificationDTO.setTitle(new LiteralMap(Locale.ENGLISH.toString(), "default value"));

        return learningAchievementSpecificationDTO;
    }

    public static LearningActivityDTO getDefaultLearningActivity(LearningActivityDTO learningActivityDTO) {
        learningActivityDTO.setTitle(new LiteralMap(Locale.ENGLISH.toString(), "default value"));
        learningActivityDTO.setAwardedBy(getDefaultAwardingProcess(new AwardingProcessDTO()));

        return learningActivityDTO;
    }

    public static LearningActivitySpecificationDTO getDefaultLearningActivitySpecification(LearningActivitySpecificationDTO learningActivitySpecificationDTO) {
        learningActivitySpecificationDTO.setTitle(new LiteralMap(Locale.ENGLISH.toString(), "default value"));

        return learningActivitySpecificationDTO;
    }

    public static LearningAssessmentDTO getDefaultLearningAssessment(LearningAssessmentDTO learningAssessmentDTO) {
        learningAssessmentDTO.setTitle(new LiteralMap(Locale.ENGLISH.toString(), "default value"));
        learningAssessmentDTO.setAwardedBy(getDefaultAwardingProcess(new AwardingProcessDTO()));
        learningAssessmentDTO.setGrade(getDefaultNote(new NoteDTO()));

        return learningAssessmentDTO;
    }

    public static LearningAssessmentSpecificationDTO getDefaultLearningAssessmentSpecification(LearningAssessmentSpecificationDTO learningAssessmentSpecificationDTO) {
        learningAssessmentSpecificationDTO.setTitle(new LiteralMap(Locale.ENGLISH.toString(), "default value"));

        return learningAssessmentSpecificationDTO;
    }

    public static LearningEntitlementDTO getDefaultLearningEntitlement(LearningEntitlementDTO learningEntitlementDTO) {
        learningEntitlementDTO.setTitle(new LiteralMap(Locale.ENGLISH.toString(), "default value"));

        return learningEntitlementDTO;
    }

    public static LearningEntitlementSpecificationDTO getDefaultLearningEntitlementSpecification(LearningEntitlementSpecificationDTO learningEntitlementSpecificationDTO) {
        learningEntitlementSpecificationDTO.setTitle(new LiteralMap(Locale.ENGLISH.toString(), "default value"));
        learningEntitlementSpecificationDTO.setEntitlementStatus(getDefaultConcept(new ConceptDTO()));

        return learningEntitlementSpecificationDTO;
    }

    public static LearningOpportunityDTO getDefaultLearningOpportunity(LearningOpportunityDTO learningOpportunityDTO) {
        learningOpportunityDTO.getProvidedBy().addAll(Arrays.asList(getDefaultOrganisation(new OrganisationDTO())));
        learningOpportunityDTO.setTitle(new LiteralMap(Locale.ENGLISH.toString(), "default value"));

        return learningOpportunityDTO;
    }

    public static LearningOutcomeDTO getDefaultLearningOutcome(LearningOutcomeDTO learningOutcomeDTO) {
        learningOutcomeDTO.setTitle(new LiteralMap(Locale.ENGLISH.toString(), "default value"));

        return learningOutcomeDTO;
    }

    public static LocationDTO getDefaultLocation(LocationDTO locationDTO) {
        locationDTO.getAddress().addAll(Arrays.asList(getDefaultAddress(new AddressDTO())));

        return locationDTO;
    }

    public static MediaObjectDTO getDefaultMediaObject(MediaObjectDTO mediaObjectDTO) {
        mediaObjectDTO.setContentEncoding(getDefaultConcept(new ConceptDTO()));

        return mediaObjectDTO;
    }

    public static NoteDTO getDefaultNote(NoteDTO noteDTO) {
        noteDTO.setNoteLiteral(new LiteralMap(Locale.ENGLISH.toString(), "default value"));

        return noteDTO;
    }

    public static OrganisationDTO getDefaultOrganisation(OrganisationDTO organisationDTO) {
        organisationDTO.setLegalName(new LiteralMap(Locale.ENGLISH.toString(), "default value"));
        organisationDTO.getLocation().addAll(Arrays.asList(getDefaultLocation(new LocationDTO())));

        return organisationDTO;
    }

    public static QualificationDTO getDefaultQualification(QualificationDTO qualificationDTO) {
        qualificationDTO.setTitle(new LiteralMap(Locale.ENGLISH.toString(), "default value"));
        return qualificationDTO;
    }

    public static ResultCategoryDTO getDefaultResultCategory(ResultCategoryDTO resultCategoryDTO) {
        resultCategoryDTO.setCount(1);
        resultCategoryDTO.setLabel("Default Label");

        return resultCategoryDTO;
    }

    public static ShortenedGradingDTO getDefaultShortenedGrading(ShortenedGradingDTO shortenedGradingDTO) {
        shortenedGradingDTO.setPercentageEqual(0);
        shortenedGradingDTO.setPercentageHigher(0);
        shortenedGradingDTO.setPercentageLower(0);

        return shortenedGradingDTO;
    }

    public static VerificationCheckDTO getDefaultVerificationCheck(VerificationCheckDTO verificationCheckDTO) {
        verificationCheckDTO.setVerificationStatus(getDefaultConcept(new ConceptDTO()));
        verificationCheckDTO.setDcType(getDefaultConcept(new ConceptDTO()));

        return verificationCheckDTO;
    }

    public static WebResourceDTO getDefaultWebResource(WebResourceDTO webResourceDTO) {
        webResourceDTO.setContentURL(URI.create("http://example-default.org"));

        return webResourceDTO;
    }

    public static ConceptDTO getConcept(int id) throws URISyntaxException {
        ConceptDTO conceptDTO = new ConceptDTO();
        conceptDTO.setId(URI.create("http://publications.europa.eu/resource/authority/country/FRA"));
        conceptDTO.setPrefLabel(new LiteralMap("en", "Country"));
        conceptDTO.setNotation("Country");
        ConceptSchemeDTO conceptSchemeDTO = new ConceptSchemeDTO();
        conceptSchemeDTO.setId(URI.create("http://publications.europa.eu/resource/authority/country"));
        conceptDTO.setInScheme(conceptSchemeDTO);

        return conceptDTO;
    }

    public static LocationDTO getLocation(int id) throws URISyntaxException {
        LocationDTO locationDTO = new LocationDTO();
        locationDTO.setId(new URI("urn:loc:" + id));

        AddressDTO addressDTO = new AddressDTO();
        addressDTO.setId(new URI("urn:add:" + id));
        addressDTO.setCountryCode(getConcept(id));
        locationDTO.getAddress().add(addressDTO);

        return locationDTO;
    }

    public static OrganisationDTO getOrganisationDTO(int id) throws URISyntaxException {
        OrganisationDTO organisationDTO = new OrganisationDTO();
        organisationDTO.setLegalName(new LiteralMap(Locale.ENGLISH.toString(), "Legal Name S.L"));
        organisationDTO.setAltLabel(new LiteralMap(Locale.ENGLISH.toString(), "Alternative Name S.L"));
        organisationDTO.setPrefLabel(new LiteralMap(Locale.ENGLISH.toString(), "Preferred Acme"));
        organisationDTO.setDateModified(ZonedDateTime.now());
        organisationDTO.setId(new URI("urn:org:" + id));
        organisationDTO.setRegistration(getLegalIdentifier(id));
        organisationDTO.getLocation().add(getLocation(id));
        organisationDTO.getLocation().add(getLocation(id));
        organisationDTO.seteIDASIdentifier(getLegalIdentifier(id));
        organisationDTO.setGroupMemberOf(Arrays.asList(getGroup(id)));
        return organisationDTO;
    }

    public static GroupDTO getGroup(int id) throws URISyntaxException {
        GroupDTO groupDTO = new GroupDTO();
        groupDTO.setPrefLabel(new LiteralMap(Locale.ENGLISH.toString(), "prefLabel" + id));
        groupDTO.setId(new URI(groupDTO.getIdPrefix(groupDTO) + id));
        groupDTO.setContactPoint(Arrays.asList(getContactPoint(id)));

        return groupDTO;
    }

    public static ContactPointDTO getContactPoint(int id) throws URISyntaxException {
        ContactPointDTO contactPointDTO = new ContactPointDTO();
        contactPointDTO.setId(new URI(contactPointDTO.getIdPrefix(contactPointDTO) + id));
        MailboxDTO mailboxDTO = new MailboxDTO();
        mailboxDTO.setId(URI.create("test@test.com"));
        contactPointDTO.setEmailAddress(Arrays.asList(mailboxDTO, mailboxDTO));

        return contactPointDTO;
    }

    public static LegalIdentifier getLegalIdentifier(int id) throws URISyntaxException {
        LegalIdentifier legalIdentifier = new LegalIdentifier();
        legalIdentifier.setDateIssued(ZonedDateTime.now());
        legalIdentifier.setId(new URI("urn:leg:" + id));
        legalIdentifier.setNotation("Notation1");
        legalIdentifier.setSpatial(getConcept(id));
        legalIdentifier.setSchemeName("Scheme Name");
        legalIdentifier.setSchemeAgency(new LiteralMap(Locale.ENGLISH.toString(), "Scheme Agency"));
        legalIdentifier.setSchemeVersion("3.0");
        legalIdentifier.setCreator(new URI("https://example.org/creator"));

        return legalIdentifier;
    }

    public static EuropeanDigitalCredentialDTO getBaseUncompletedVerifiableCredential() throws URISyntaxException {
        EuropeanDigitalCredentialDTO europeanDigitalCredentialDTO = new EuropeanDigitalCredentialDTO();
        ZonedDateTime issueDate = ZonedDateTime.now();
        ZonedDateTime expirationDate = ZonedDateTime.now().minusMonths(5);
        europeanDigitalCredentialDTO.setId(new URI("urn:credential:0bd26efe-6605-468b-845b-5acf00fabb20"));
        europeanDigitalCredentialDTO.setCredentialSchema(Arrays.asList(new ShaclValidator2017(URI.create(generic_full_URL))));
        europeanDigitalCredentialDTO.setExpirationDate(expirationDate);
        europeanDigitalCredentialDTO.setIssuanceDate(issueDate);
        europeanDigitalCredentialDTO.setType(Arrays.asList("VerifiableCredential", "EuropeanDigitalCredential"));
        europeanDigitalCredentialDTO.setValidFrom(issueDate);
        europeanDigitalCredentialDTO.setValidUntil(expirationDate);
        europeanDigitalCredentialDTO.setIssued(ZonedDateTime.now());
        ConceptDTO conceptDTO = new ConceptDTO();
        conceptDTO.setId(URI.create("http://data.europa.eu/snb/credential/e34929035b"));
        ConceptSchemeDTO conceptSchemeDTO = new ConceptSchemeDTO();
        conceptSchemeDTO.setId(URI.create("http://data.europa.eu/snb/credential/25831c2"));
        conceptDTO.setInScheme(conceptSchemeDTO);
        europeanDigitalCredentialDTO.setCredentialProfiles(Arrays.asList(conceptDTO));

        OrganisationDTO issuer = getOrganisationDTO(1);
        europeanDigitalCredentialDTO.setIssuer(issuer);

        PersonDTO credentialSubject = new PersonDTO();

        credentialSubject.setId(new URI("urn:person:1"));
        credentialSubject.setGivenName(new LiteralMap(Locale.ENGLISH.toString(), "Max"));
        credentialSubject.setFamilyName(new LiteralMap(Locale.ENGLISH.toString(), "Power"));
        credentialSubject.setPatronymicName(new LiteralMap(Locale.ENGLISH.toString(), "Patronymic"));
        credentialSubject.setBirthName(new LiteralMap(Locale.ENGLISH.toString(), "Maxi"));
        credentialSubject.setFullName(new LiteralMap(Locale.ENGLISH.toString(), "Max Power"));
        credentialSubject.setDateOfBirth(issueDate);
        credentialSubject.setDateModified(issueDate);
        credentialSubject.setNationalID(getLegalIdentifier(1));

        LearningAchievementDTO learningAchievementDTO = new LearningAchievementDTO();
        learningAchievementDTO.setId(URI.create("urn:ach:1"));
        learningAchievementDTO.setTitle(new LiteralMap(Locale.ENGLISH.toString(), "TITLE"));
        LearningAchievementSpecificationDTO learningAchievementSpecificationDTO = new LearningAchievementSpecificationDTO();
        learningAchievementSpecificationDTO.setId(URI.create("urn:achs:1"));
        learningAchievementSpecificationDTO.setTitle(new LiteralMap(Locale.ENGLISH.toString(), "TITLE"));
        AwardingProcessDTO awardingProcessDTO = new AwardingProcessDTO();
        awardingProcessDTO.setAwardingBody(Arrays.asList(issuer));
        learningAchievementDTO.setAwardedBy(awardingProcessDTO);
        learningAchievementDTO.setSpecifiedBy(learningAchievementSpecificationDTO);

        credentialSubject.setHasClaim(Arrays.asList(learningAchievementDTO));
        europeanDigitalCredentialDTO.setCredentialSubject(credentialSubject);

        europeanDigitalCredentialDTO.setDisplayParameter(getDisplayParameter());

        return europeanDigitalCredentialDTO;
    }

    public static EuropeanDigitalCredentialDTO getCredentialWithLoops() throws URISyntaxException {
        int id = 0;
        ZonedDateTime issueDate = ZonedDateTime.now();
        ZonedDateTime expirationDate = ZonedDateTime.now().minusMonths(5);

        EuropeanDigitalCredentialDTO europeanDigitalCredentialDTO = new EuropeanDigitalCredentialDTO();
        europeanDigitalCredentialDTO.setId(new URI("urn:credential:0bd26efe-6605-468b-845b-5acf00fabb20"));
        europeanDigitalCredentialDTO.setCredentialSchema(Arrays.asList(new ShaclValidator2017(URI.create(generic_ttl))));
        europeanDigitalCredentialDTO.setExpirationDate(expirationDate);
        europeanDigitalCredentialDTO.setIssuanceDate(issueDate);
        europeanDigitalCredentialDTO.setType(Arrays.asList("VerifiableCredential", "EuropeanDigitalCredential"));
        europeanDigitalCredentialDTO.setValidFrom(issueDate);
        europeanDigitalCredentialDTO.setValidUntil(expirationDate);
        europeanDigitalCredentialDTO.setIssued(ZonedDateTime.now());
        europeanDigitalCredentialDTO.setDisplayParameter(getDisplayParameter());

        PersonDTO credentialSubject = new PersonDTO();

        credentialSubject.setId(new URI("urn:person:1"));
        credentialSubject.setGivenName(new LiteralMap(Locale.ENGLISH.toString(), "Max"));
        credentialSubject.setFamilyName(new LiteralMap(Locale.ENGLISH.toString(), "Power"));
        credentialSubject.setPatronymicName(new LiteralMap(Locale.ENGLISH.toString(), "Patronymic"));
        credentialSubject.setBirthName(new LiteralMap(Locale.ENGLISH.toString(), "Maxi"));
        credentialSubject.setFullName(new LiteralMap(Locale.ENGLISH.toString(), "Max Power"));
        credentialSubject.setDateOfBirth(issueDate);
        credentialSubject.setDateModified(issueDate);
        credentialSubject.setNationalID(getLegalIdentifier(id));

        ContactPointDTO contactPointDTO = new ContactPointDTO();
        contactPointDTO.setId(URI.create("urn:contact:1"));
        MailboxDTO mailboxDTO = new MailboxDTO();
        mailboxDTO.setId(URI.create("example@gmail.com"));
        contactPointDTO.getEmailAddress().add(mailboxDTO);

        credentialSubject.getContactPoint().add(contactPointDTO);

        LearningAchievementDTO claimDTO = getLearningAchievement(id);
        LearningAchievementDTO claimDTO2 = getLearningAchievement(id);

        QualificationDTO qualificationDTO = getQualificationDTO(id);
        claimDTO2.setSpecifiedBy(qualificationDTO);

        claimDTO.setHasPart(Arrays.asList(getLearningAchievement(id), getLearningAchievement(id)));
        claimDTO2.setHasPart(Arrays.asList(getLearningAchievement(id), getLearningAchievement(id)));

        credentialSubject.getHasClaim().add(claimDTO);
        credentialSubject.getHasClaim().add(claimDTO2);

        OrganisationDTO organisationDTO = getOrganisationDTO(id);

        AccreditationDTO accreditationDTO = getAccreditation(id);

        organisationDTO.getAccreditation().add(accreditationDTO);
        organisationDTO.getAccreditation().add(accreditationDTO);
        organisationDTO.getHasSubOrganization().add(getOrganisationDTO(id));

        europeanDigitalCredentialDTO.setCredentialSubject(credentialSubject);
        europeanDigitalCredentialDTO.setIssuer(organisationDTO);

        return europeanDigitalCredentialDTO;
    }

    public static DisplayParameterDTO getDisplayParameter() {
        DisplayParameterDTO displayParameterDTO = new DisplayParameterDTO();
        displayParameterDTO.setId(URI.create("urn:dis:1"));
        displayParameterDTO.setTitle(new LiteralMap(Locale.ENGLISH.toString(), "Credential title"));
        displayParameterDTO.setDescription(new LiteralMap(Locale.ENGLISH.toString(), "Credential Description"));
        displayParameterDTO.getLanguage().add(getENLanguageConceptDTO());
        displayParameterDTO.setPrimaryLanguage(getENLanguageConceptDTO());

        DisplayDetailDTO displayDetailDTO = new DisplayDetailDTO();
        MediaObjectDTO mediaObjectDTO = new MediaObjectDTO();
        ConceptDTO base64Encoding = new ConceptDTO();
        base64Encoding.setId(URI.create(ControlledListConcept.ENCODING_BASE64.getUrl()));
        base64Encoding.setPrefLabel(new LiteralMap(Locale.ENGLISH.toString(), "base64"));
        ConceptSchemeDTO encodingScheme = new ConceptSchemeDTO();
        encodingScheme.setId(URI.create(ControlledList.ENCODING.getUrl()));
        base64Encoding.setInScheme(encodingScheme);
        ConceptDTO jpegContentType = new ConceptDTO();
        jpegContentType.setId(URI.create(ControlledListConcept.FILE_TYPE_JPEG.getUrl()));
        jpegContentType.setPrefLabel(new LiteralMap(Locale.ENGLISH.toString(), "jpg"));
        ConceptSchemeDTO contentTypeScheme = new ConceptSchemeDTO();
        jpegContentType.setInScheme(contentTypeScheme);
        mediaObjectDTO.setContent("base64content");
        mediaObjectDTO.setContentEncoding(base64Encoding);
        mediaObjectDTO.setContentType(jpegContentType);
        displayDetailDTO.setImage(mediaObjectDTO);
        displayDetailDTO.setPage(1);
        IndividualDisplayDTO individualDisplayDTO = new IndividualDisplayDTO();
        individualDisplayDTO.setDisplayDetail(Arrays.asList(displayDetailDTO));
        individualDisplayDTO.setLanguage(getENLanguageConceptDTO());
        displayParameterDTO.setIndividualDisplay(Arrays.asList(individualDisplayDTO));
        return displayParameterDTO;
    }

    public static ConceptDTO getLanguageConceptDTO() {
        ConceptDTO conceptDTO = controlledListCommonsService.searchLanguageByLang(Locale.ENGLISH.toString());

        return conceptDTO;
    }

    public static ConceptDTO getCountryConceptDTO() {
        ConceptDTO conceptDTO = controlledListCommonsService.searchConceptByUri(ControlledList.COUNTRY.getUrl(),
                "http://publications.europa.eu/resource/authority/country/BEL", Arrays.asList(Locale.ENGLISH.toString()), Locale.ENGLISH.toString());

        return conceptDTO;
    }

    public static LearningAchievementDTO getLearningAchievement(int id) throws URISyntaxException {
        LearningAchievementDTO learningAchievementDTO = new LearningAchievementDTO();
        learningAchievementDTO.setId(new URI("urn:cla:" + id));
        learningAchievementDTO.setTitle(new LiteralMap(Locale.ENGLISH.toString(), "Claim Title" + " " + id));
        learningAchievementDTO.setDescription(new LiteralMap(Locale.ENGLISH.toString(), "Claim Description" + " " + id));
        learningAchievementDTO.setIdentifier(Arrays.asList(getLegalIdentifier(id)));
        //learningAchievementDTO.setAdditionalNote(Arrays.asList(getNote(id + 154)));

        LearningAssessmentDTO learningAssessmentDTO = getLearningAssessmentDTO(id);
        LearningAssessmentDTO learningAssessmentDTOCopy = getLearningAssessmentDTO(id);
        learningAssessmentDTO.setHasPart(Arrays.asList(learningAssessmentDTOCopy));
        learningAssessmentDTO.setIsPartOf(Arrays.asList(learningAssessmentDTOCopy));

        learningAchievementDTO.setProvenBy(Arrays.asList(learningAssessmentDTO));
        learningAchievementDTO.setEntitlesTo(Arrays.asList(getLearningEntitlementDTO(id)));
        learningAchievementDTO.setSpecifiedBy(getLearningAchievementSpecificationDTO(id));
        learningAchievementDTO.setLearningOpportunity(getLearningOpportunityDTO(id));
        learningAchievementDTO.setAwardedBy(getAwardingProcessDTO(id, learningAchievementDTO));

        return learningAchievementDTO;
    }

    public static LearningEntitlementDTO getLearningEntitlementDTO(int id) throws URISyntaxException {
        LearningEntitlementDTO learningEntitlementDTO = new LearningEntitlementDTO();
        learningEntitlementDTO.setId(new URI("urn:ent:" + id));
        learningEntitlementDTO.setIdentifier(Arrays.asList(getLegalIdentifier(id)));
        learningEntitlementDTO.setTitle(new LiteralMap(Locale.ENGLISH.toString(), "Entitlement title" + " " + id));

        LearningEntitlementSpecificationDTO learningEntitlementSpecificationDTO = getLearningEntitlementSpecificationDTO(id);

        learningEntitlementDTO.setSpecifiedBy(learningEntitlementSpecificationDTO);
        learningEntitlementDTO.setAwardedBy(getAwardingProcessDTO(id, learningEntitlementDTO));

        return learningEntitlementDTO;
    }

    public static LearningAssessmentDTO getLearningAssessmentDTO(int id) throws URISyntaxException {
        LearningAssessmentDTO learningAssessmentDTO = new LearningAssessmentDTO();
        learningAssessmentDTO.setId(URI.create("urn:assess:" + id));
        learningAssessmentDTO.setTitle(new LiteralMap(Locale.ENGLISH.toString(), "Assessment Title" + " " + id));
        learningAssessmentDTO.setGrade(getNote(id));
        learningAssessmentDTO.setDateIssued(ZonedDateTime.now());
        learningAssessmentDTO.setDescription(new LiteralMap(Locale.ENGLISH.toString(), "Assessment Description" + " " + id));
        learningAssessmentDTO.setAwardedBy(getAwardingProcessDTO(id, learningAssessmentDTO));

        return learningAssessmentDTO;
    }

    public static NoteDTO getNote(int id) throws URISyntaxException {
        NoteDTO noteDTO = new NoteDTO();
        noteDTO.setId(URI.create("urn:not:" + id));
        noteDTO.setNoteFormat(getConcept(id));
        noteDTO.setNoteLiteral(new LiteralMap(Locale.ENGLISH.toString(), "Note literal" + " " + id));
        noteDTO.setSubject(getConcept(id));

        return noteDTO;
    }

    public static AccreditationDTO getAccreditation(int id) throws URISyntaxException {
        AccreditationDTO accreditationDTO = new AccreditationDTO();
        accreditationDTO.setAccreditingAgent(getOrganisationDTO(id));
        accreditationDTO.setOrganisation(Arrays.asList(getOrganisationDTO(id)));
        accreditationDTO.setAdditionalNote(Arrays.asList(getNote(id), getNote(id)));
        accreditationDTO.setHomepage(Arrays.asList(getWebResourceDTO(id), getWebResourceDTO(id)));
        accreditationDTO.setDateIssued(ZonedDateTime.now());
        accreditationDTO.setExpiryDate(ZonedDateTime.now());
        accreditationDTO.setReviewDate(ZonedDateTime.now());
        accreditationDTO.setDateModified(ZonedDateTime.now());
        accreditationDTO.setDecision(getConcept(id));
        accreditationDTO.setDescription(new LiteralMap(Locale.ENGLISH.toString(), "Accreditation description " + id));
        accreditationDTO.setTitle(new LiteralMap(Locale.ENGLISH.toString(), "Accreditation Title " + id));
        accreditationDTO.setLimitJurisdiction(Arrays.asList(getConcept(id)));
        accreditationDTO.setLimitEQFLevel(Arrays.asList(getConcept(id)));
        accreditationDTO.setLimitField(Arrays.asList(getConcept(id)));
        accreditationDTO.setSupplementaryDocument(Arrays.asList(getWebResourceDTO(id), getWebResourceDTO(id)));
        accreditationDTO.setReport(getWebResourceDTO(id));
        accreditationDTO.setStatus("Accreditation status");
        accreditationDTO.setDcType(getConcept(id));
        accreditationDTO.setId(new URI(testUri));

        return accreditationDTO;
    }

    public static QualificationDTO getQualificationDTO(int id) throws URISyntaxException {
        QualificationDTO qualificationDTO = new QualificationDTO();
        qualificationDTO.setId(URI.create("urn:qua:" + id));
        qualificationDTO.setTitle(new LiteralMap(Locale.ENGLISH.toString(), "Qualification Title" + " " + id));
        qualificationDTO.setDescription(new LiteralMap(Locale.ENGLISH.toString(), "Qualification Description" + " " + id));
        qualificationDTO.setEqfLevel(getConcept(id));
        qualificationDTO.setNqfLevel(Arrays.asList(getConcept(id)));
        qualificationDTO.setQualificationCode(Arrays.asList(getConcept(id)));
        qualificationDTO.setAccreditation(Arrays.asList(getAccreditation(id)));

        return qualificationDTO;
    }

    public static LearningAchievementSpecificationDTO getLearningAchievementSpecificationDTO(int id) throws URISyntaxException {
        CreditPointDTO creditPointDTO = new CreditPointDTO();
        creditPointDTO.setId(URI.create("urn:cre:" + id));
        creditPointDTO.setPoint("Points");
        creditPointDTO.setFramework(getConcept(id));
        creditPointDTO.getFramework().setId(URI.create("https://publications.europa.eu/resource/authority/snb/education-credit/60b314e826"));

        NoteDTO noteDTO = getNote(id);
        noteDTO.getSubject().setPrefLabel(new LiteralMap(Locale.ENGLISH.toString(), "requirement"));

        LearningAchievementSpecificationDTO learningAchievementSpecificationDTO = new LearningAchievementSpecificationDTO();
        learningAchievementSpecificationDTO.setMaximumDuration(Period.years(15));
        learningAchievementSpecificationDTO.setIdentifier(Arrays.asList(getLegalIdentifier(id)));
        learningAchievementSpecificationDTO.setId(new URI("urn:las:" + id));
        learningAchievementSpecificationDTO.setTitle(new LiteralMap(Locale.ENGLISH.toString(), "Achievement Specification Title" + " " + id));
        learningAchievementSpecificationDTO.setDescription(new LiteralMap(Locale.ENGLISH.toString(), "Achievement Specification Description" + " " + id));
        learningAchievementSpecificationDTO.setCreditPoint(Arrays.asList(creditPointDTO));
        learningAchievementSpecificationDTO.setEducationSubject(Arrays.asList(getConcept(id)));
        learningAchievementSpecificationDTO.setEducationLevel(Arrays.asList(getConcept(id)));
        learningAchievementSpecificationDTO.setEntryRequirement(getNote(id));
        learningAchievementSpecificationDTO.setLanguage(Arrays.asList(getENLanguageConceptDTO()));
        learningAchievementSpecificationDTO.setMode(Arrays.asList(getConcept(id)));
        //learningAchievementSpecificationDTO.setAdditionalNote(Arrays.asList(noteDTO));
        learningAchievementSpecificationDTO.setSupplementaryDocument(Arrays.asList(getWebResourceDTO(id), getWebResourceDTO(id), getWebResourceDTO(id), getWebResourceDTO(id), getWebResourceDTO(id), getWebResourceDTO(id)));

        return learningAchievementSpecificationDTO;
    }

    public static LearningEntitlementSpecificationDTO getLearningEntitlementSpecificationDTO(int id) throws URISyntaxException {
        ConceptDTO conceptDTO = getConcept(id);
        conceptDTO.setId(URI.create("https://publications.europa.eu/resource/authority/snb/entitlement/64aad92881"));

        ConceptDTO conceptDTO2 = getConcept(id);
        conceptDTO.setId(URI.create("https://publications.europa.eu/resource/authority/snb/entitlement/bebd32e8e6"));

        LearningEntitlementSpecificationDTO learningEntitlementSpecificationDTO = new LearningEntitlementSpecificationDTO();
        learningEntitlementSpecificationDTO.setDcType(Arrays.asList(conceptDTO, conceptDTO2));
        learningEntitlementSpecificationDTO.setId(new URI("urn:ents:" + id));
        learningEntitlementSpecificationDTO.setTitle(new LiteralMap(Locale.ENGLISH.toString(), "Entitlement Specification Title" + " " + id));
        learningEntitlementSpecificationDTO.setDescription(new LiteralMap(Locale.ENGLISH.toString(), "Entitlement Specification Description" + " " + id));
        learningEntitlementSpecificationDTO.setEntitledBy(Arrays.asList(getLearningAchievementSpecificationDTO(id)));
        learningEntitlementSpecificationDTO.setLimitOrganisation(Arrays.asList(getOrganisationDTO(id)));
        learningEntitlementSpecificationDTO.setIdentifier(Arrays.asList(getLegalIdentifier(id)));
        learningEntitlementSpecificationDTO.setEntitlementStatus(getConcept(id));

        return learningEntitlementSpecificationDTO;
    }

    public static LearningOpportunityDTO getLearningOpportunityDTO(int id) throws URISyntaxException {
        LearningOpportunityDTO learningOpportunityDTO = new LearningOpportunityDTO();

        NoteDTO noteDTO = getNote(id);
        noteDTO.getSubject().setPrefLabel(new LiteralMap(Locale.ENGLISH.toString(), "status"));

        NoteDTO noteDTO2 = getNote(id);
        noteDTO2.getSubject().setPrefLabel(new LiteralMap(Locale.ENGLISH.toString(), "status"));

        NoteDTO noteDTO3 = getNote(id);
        noteDTO3.getSubject().setPrefLabel(new LiteralMap(Locale.ENGLISH.toString(), "status"));

        //learningOpportunityDTO.setAdditionalNote(Arrays.asList(noteDTO, noteDTO2, noteDTO3));
        learningOpportunityDTO.setTitle(new LiteralMap(Locale.ENGLISH.toString(), "Learning Opportunity Title" + " " + id));
        learningOpportunityDTO.setDescription(new LiteralMap(Locale.ENGLISH.toString(), "Learning Opportunity Description" + " " + id));
        learningOpportunityDTO.setProvidedBy(Arrays.asList(getOrganisationDTO(id)));
        learningOpportunityDTO.setId(URI.create("urn:oppo:" + id));

        return learningOpportunityDTO;
    }

    public static AwardingProcessDTO getAwardingProcessDTO(int id, ClaimDTO claimDTO) throws URISyntaxException {
        AwardingProcessDTO awardingProcessDTO = new AwardingProcessDTO();
        OrganisationDTO organisationDTO = getOrganisationDTO(id);
        organisationDTO.setAccreditation(Arrays.asList(getAccreditation(id), getAccreditation(id)));
        OrganisationDTO organisationDTO2 = getOrganisationDTO(id);
        organisationDTO2.setAccreditation(Arrays.asList(getAccreditation(id), getAccreditation(id)));
        awardingProcessDTO.setAwardingBody(Arrays.asList(organisationDTO, organisationDTO2));
        awardingProcessDTO.setId(URI.create("urn:awar:" + id));
        awardingProcessDTO.setAwards(Arrays.asList(claimDTO));
        awardingProcessDTO.setEducationalSystemNote(getConcept(id));
        //awardingProcessDTO.setAdditionalNote(Arrays.asList(getNote(id)));
        awardingProcessDTO.setDescription(new LiteralMap(Locale.ENGLISH.toString(), "Awarding Process Description" + " " + id));
        return awardingProcessDTO;
    }

    public static WebResourceDTO getWebResourceDTO(int id) {
        WebResourceDTO webResourceDTO = new WebResourceDTO();
        webResourceDTO.setContentURL(URI.create("https://example.org/" + id));
        webResourceDTO.setLanguage(getENLanguageConceptDTO());
        webResourceDTO.setTitle(new LiteralMap(Locale.ENGLISH.toString(), "Web Resource Title" + " " + id));
        webResourceDTO.setId(URI.create("https://example.org/" + id));

        return webResourceDTO;
    }

    public static EuropeanDigitalCredentialDTO getSimpleCredential() throws URISyntaxException {
        EuropeanDigitalCredentialDTO europeanDigitalCredentialDTO = new EuropeanDigitalCredentialDTO();
        europeanDigitalCredentialDTO.setId(new URI("urn:credential:0bd26efe-6605-468b-845b-5acf00fabb20"));
        europeanDigitalCredentialDTO.setCredentialSchema(Arrays.asList(new ShaclValidator2017(URI.create(generic_ttl))));
        europeanDigitalCredentialDTO.setExpirationDate(ZonedDateTime.now());
        europeanDigitalCredentialDTO.setIssuanceDate(ZonedDateTime.now());
        europeanDigitalCredentialDTO.setType(Arrays.asList("VerifiableCredential", "EuropeanDigitalCredential"));
        europeanDigitalCredentialDTO.setValidFrom(ZonedDateTime.now());
        europeanDigitalCredentialDTO.setValidUntil(ZonedDateTime.now());
        europeanDigitalCredentialDTO.setIssued(ZonedDateTime.now());
        PersonDTO credentialSubject = new PersonDTO();

        credentialSubject.setId(new URI("urn:person:1"));
        credentialSubject.setGivenName(new LiteralMap(Locale.ENGLISH.toString(), "Max"));
        credentialSubject.setFamilyName(new LiteralMap(Locale.ENGLISH.toString(), "Power"));
        credentialSubject.setPatronymicName(new LiteralMap(Locale.ENGLISH.toString(), "Patronymic"));
        credentialSubject.setBirthName(new LiteralMap(Locale.ENGLISH.toString(), "Maxi"));
        credentialSubject.setFullName(new LiteralMap(Locale.ENGLISH.toString(), "Max Power"));
        credentialSubject.setDateOfBirth(ZonedDateTime.now());
        credentialSubject.setDateModified(ZonedDateTime.now());

        LearningActivityDTO learningActivityDTO = new LearningActivityDTO();
        learningActivityDTO.setId(URI.create("urn:act:1"));
        learningActivityDTO.setTitle(new LiteralMap(Locale.ENGLISH.toString(), "TITLE"));

        LearningActivityDTO learningActivityDTO2 = new LearningActivityDTO();
        learningActivityDTO2.setId(URI.create("urn:act:2"));
        learningActivityDTO2.setTitle(new LiteralMap(Locale.ENGLISH.toString(), "TITLE"));
        learningActivityDTO2.setIsPartOf(Arrays.asList(learningActivityDTO));

        learningActivityDTO.setHasPart(Arrays.asList(learningActivityDTO2));

        LearningAchievementDTO learningAchievementDTO = new LearningAchievementDTO();
        learningAchievementDTO.setId(URI.create("urn:ach:1"));
        learningAchievementDTO.setTitle(new LiteralMap(Locale.ENGLISH.toString(), "TITLE"));

        LearningAchievementSpecificationDTO learningAchievementSpecificationDTO = new LearningAchievementSpecificationDTO();
        learningAchievementSpecificationDTO.setId(URI.create("urn:achs:1"));
        learningAchievementSpecificationDTO.setTitle(new LiteralMap(Locale.ENGLISH.toString(), "TITLE"));

        AwardingOpportunityDTO awardingOpportunityDTO = new AwardingOpportunityDTO();
        awardingOpportunityDTO.setId(URI.create("urn:awao:1"));
        awardingOpportunityDTO.setLearningAchievementSpecification(learningAchievementSpecificationDTO);

        learningAchievementSpecificationDTO.setAwardingOpportunity(Arrays.asList(awardingOpportunityDTO));

        learningAchievementDTO.setSpecifiedBy(learningAchievementSpecificationDTO);

        LearningAchievementDTO learningAchievementDTO2 = new LearningAchievementDTO();
        learningAchievementDTO2.setId(URI.create("urn:ach:2"));
        learningAchievementDTO2.setTitle(new LiteralMap(Locale.ENGLISH.toString(), "TITLE2"));
        learningAchievementDTO2.setIsPartOf(Arrays.asList(learningAchievementDTO));

        learningAchievementDTO.setHasPart(Arrays.asList(learningAchievementDTO2));

        LearningAssessmentDTO learningAssessmentDTO = new LearningAssessmentDTO();
        learningAssessmentDTO.setId(URI.create("urn:asm:1"));
        learningAssessmentDTO.setTitle(new LiteralMap(Locale.ENGLISH.toString(), "TITLE"));

        LearningAssessmentDTO learningAssessmentDTO2 = new LearningAssessmentDTO();
        learningAssessmentDTO2.setId(URI.create("urn:asm:1"));
        learningAssessmentDTO2.setTitle(new LiteralMap(Locale.ENGLISH.toString(), "TITLE"));
        learningAssessmentDTO2.setIsPartOf(Arrays.asList(learningAssessmentDTO));

        learningAssessmentDTO.setHasPart(Arrays.asList(learningAssessmentDTO2));

        LearningEntitlementDTO learningEntitlementDTO = new LearningEntitlementDTO();
        learningEntitlementDTO.setId(URI.create("urn:ent:1"));
        learningEntitlementDTO.setTitle(new LiteralMap(Locale.ENGLISH.toString(), "TITLE"));

        LearningEntitlementDTO learningEntitlementDTO2 = new LearningEntitlementDTO();
        learningEntitlementDTO2.setId(URI.create("urn:ent:2"));
        learningEntitlementDTO2.setTitle(new LiteralMap(Locale.ENGLISH.toString(), "TITLE"));
        learningEntitlementDTO2.setIsPartOf(Arrays.asList(learningEntitlementDTO));

        learningEntitlementDTO.setHasPart(Arrays.asList(learningEntitlementDTO2));

        LearningOpportunityDTO learningOpportunityDTO = new LearningOpportunityDTO();
        learningOpportunityDTO.setId(URI.create("urn:opp:1"));
        learningOpportunityDTO.setTitle(new LiteralMap(Locale.ENGLISH.toString(), "TITLE"));

        LearningOpportunityDTO learningOpportunityDTO2 = new LearningOpportunityDTO();
        learningOpportunityDTO2.setId(URI.create("urn:opp:2"));
        learningOpportunityDTO2.setTitle(new LiteralMap(Locale.ENGLISH.toString(), "TITLE"));
        learningOpportunityDTO2.setIsPartOf(Arrays.asList(learningOpportunityDTO));

        learningOpportunityDTO.setHasPart(Arrays.asList(learningOpportunityDTO2));

        AwardingProcessDTO awardingProcessDTO = new AwardingProcessDTO();
        awardingProcessDTO.setId(URI.create("urn:awa:1"));
        awardingProcessDTO.setAwards(Arrays.asList(learningAchievementDTO));

        OrganisationDTO organisationDTO = new OrganisationDTO();
        organisationDTO.setId(URI.create("urn:org:1"));
        organisationDTO.setLegalName(new LiteralMap(Locale.ENGLISH.toString(), "TITLE"));
        organisationDTO.seteIDASIdentifier(getLegalIdentifier(1));

        OrganisationDTO organisationDTO2 = new OrganisationDTO();
        organisationDTO2.setId(URI.create("urn:org:2"));
        organisationDTO2.setLegalName(new LiteralMap(Locale.ENGLISH.toString(), "TITLE"));
        organisationDTO2.seteIDASIdentifier(getLegalIdentifier(1));
        organisationDTO2.setSubOrganizationOf(organisationDTO);

        organisationDTO.setHasSubOrganization(Arrays.asList(organisationDTO2));

        LocationDTO locationDTO = new LocationDTO();
        locationDTO.setId(URI.create("urn:loc:1"));

        AddressDTO addressDTO = new AddressDTO();
        addressDTO.setId(URI.create("urn:add:1"));
        addressDTO.setCountryCode(getConcept(1));

        locationDTO.setAddress(Arrays.asList(addressDTO));
        organisationDTO.setLocation(Arrays.asList(locationDTO));
        awardingProcessDTO.setAwardingBody(Arrays.asList(organisationDTO));
        awardingOpportunityDTO.setAwardingBody(Arrays.asList(organisationDTO));

        learningAchievementDTO.setAwardedBy(awardingProcessDTO);
        learningAchievementDTO.setLearningOpportunity(learningOpportunityDTO);
        learningAchievementDTO2.setAwardedBy(awardingProcessDTO);
        learningActivityDTO.setAwardedBy(awardingProcessDTO);
        learningActivityDTO2.setAwardedBy(awardingProcessDTO);
        learningAssessmentDTO.setAwardedBy(awardingProcessDTO);
        learningAssessmentDTO2.setAwardedBy(awardingProcessDTO);
        learningEntitlementDTO.setAwardedBy(awardingProcessDTO);
        learningEntitlementDTO2.setAwardedBy(awardingProcessDTO);
        learningOpportunityDTO.setProvidedBy(Arrays.asList(organisationDTO));
        learningOpportunityDTO2.setProvidedBy(Arrays.asList(organisationDTO));
        learningAssessmentDTO.setGrade(getNote(1));

        credentialSubject.getHasClaim().add(learningAchievementDTO);
        credentialSubject.getHasClaim().add(learningActivityDTO);
        credentialSubject.getHasClaim().add(learningAssessmentDTO);
        credentialSubject.getHasClaim().add(learningEntitlementDTO);

        europeanDigitalCredentialDTO.setIssuer(organisationDTO);
        europeanDigitalCredentialDTO.setCredentialSubject(credentialSubject);
        europeanDigitalCredentialDTO.setDisplayParameter(getDisplayParameter());
        List<ConceptDTO> conceptDTOList = new ArrayList<>();
        conceptDTOList.add(getENLanguageConceptDTO());
        europeanDigitalCredentialDTO.setCredentialProfiles(conceptDTOList);


        return europeanDigitalCredentialDTO;
    }

    public static EuropeanDigitalCredentialDTO getMinimalCredential_NoBidirectionalRelations() throws Exception {
        EuropeanDigitalCredentialDTO europeanDigitalCredentialDTO = new EuropeanDigitalCredentialDTO();
        europeanDigitalCredentialDTO.setId(new URI("urn:credential:0bd26efe-6605-468b-845b-5acf00fabb20"));
        europeanDigitalCredentialDTO.setCredentialSchema(Arrays.asList(new ShaclValidator2017(URI.create(generic_ttl))));
        europeanDigitalCredentialDTO.setExpirationDate(ZonedDateTime.now());
        europeanDigitalCredentialDTO.setIssuanceDate(ZonedDateTime.now());
        europeanDigitalCredentialDTO.setType(Arrays.asList("VerifiableCredential", "EuropeanDigitalCredential"));
        europeanDigitalCredentialDTO.setValidFrom(ZonedDateTime.now());
        europeanDigitalCredentialDTO.setValidUntil(ZonedDateTime.now());
        europeanDigitalCredentialDTO.setIssued(ZonedDateTime.now());


        PersonDTO credentialSubject = new PersonDTO();
        credentialSubject.setId(new URI("urn:person:1"));
        credentialSubject.setGivenName(new LiteralMap(Locale.ENGLISH.toString(), "Max"));
        credentialSubject.setFamilyName(new LiteralMap(Locale.ENGLISH.toString(), "Power"));
        credentialSubject.setPatronymicName(new LiteralMap(Locale.ENGLISH.toString(), "Patronymic"));
        credentialSubject.setBirthName(new LiteralMap(Locale.ENGLISH.toString(), "Maxi"));
        credentialSubject.setFullName(new LiteralMap(Locale.ENGLISH.toString(), "Max Power"));
        credentialSubject.setDateOfBirth(ZonedDateTime.now());
        credentialSubject.setDateModified(ZonedDateTime.now());


        OrganisationDTO issuer = new OrganisationDTO();
        issuer.setId(URI.create("urn:org:1"));
        issuer.setLegalName(new LiteralMap(Locale.ENGLISH.toString(), "ISSUER TITLE"));
        issuer.seteIDASIdentifier(getLegalIdentifier(1));


        Identifier identifier1 = new Identifier();
        identifier1.setNotation("notation 1");
        identifier1.setSchemeName("scheme name 1");

        Identifier identifier2 = new Identifier();
        identifier2.setNotation("notation 2");
        identifier2.setSchemeName("scheme name 2");

        issuer.setIdentifier(Arrays.asList(identifier1, identifier2));

        OrganisationDTO issuerChild = new OrganisationDTO();
        issuerChild.setId(URI.create("urn:org:2"));
        issuerChild.setLegalName(new LiteralMap(Locale.ENGLISH.toString(), "ORG 2 TITLE"));
        issuerChild.seteIDASIdentifier(getLegalIdentifier(2));

        issuer.setHasSubOrganization(Arrays.asList(issuerChild));
        //issuerChild.setSubOrganizationOf(issuer);

        LocationDTO locationDTO = new LocationDTO();
        locationDTO.setId(URI.create("urn:loc:1"));
        AddressDTO addressDTO = new AddressDTO();
        addressDTO.setId(URI.create("urn:add:1"));
        addressDTO.setCountryCode(getConcept(1));
        locationDTO.setAddress(Arrays.asList(addressDTO));
        issuer.setLocation(Arrays.asList(locationDTO));


        LearningAchievementDTO learningAchievementDTO = new LearningAchievementDTO();
        learningAchievementDTO.setId(URI.create("urn:ach:1"));
        learningAchievementDTO.setTitle(new LiteralMap(Locale.ENGLISH.toString(), "TITLE"));
        LearningAchievementSpecificationDTO learningAchievementSpecificationDTO = new LearningAchievementSpecificationDTO();
        learningAchievementSpecificationDTO.setId(URI.create("urn:achs:1"));
        learningAchievementSpecificationDTO.setTitle(new LiteralMap(Locale.ENGLISH.toString(), "TITLE"));

        LearningAchievementDTO learningAchievementDTO2 = new LearningAchievementDTO();
        learningAchievementDTO2.setId(URI.create("urn:ach:2"));
        learningAchievementDTO2.setTitle(new LiteralMap(Locale.ENGLISH.toString(), "TITLE2"));
        //learningAchievementDTO2.setIsPartOf(Arrays.asList(learningAchievementDTO));

        AwardingOpportunityDTO awardingOpportunityDTO = new AwardingOpportunityDTO();
        awardingOpportunityDTO.setId(URI.create("urn:awao:1"));
        //awardingOpportunityDTO.setLearningAchievementSpecification(learningAchievementSpecificationDTO);
        awardingOpportunityDTO.setAwardingBody(Arrays.asList(issuer));
        learningAchievementSpecificationDTO.setAwardingOpportunity(Arrays.asList(awardingOpportunityDTO));

        AwardingProcessDTO awardingProcessDTO = new AwardingProcessDTO();
        awardingProcessDTO.setId(URI.create("urn:awa:1"));
        awardingProcessDTO.setAwardingBody(Arrays.asList(issuer));

        learningAchievementDTO.setAwardedBy(awardingProcessDTO);
        learningAchievementDTO2.setAwardedBy(awardingProcessDTO);

        learningAchievementDTO.setSpecifiedBy(learningAchievementSpecificationDTO);
        learningAchievementDTO.setHasPart(Arrays.asList(learningAchievementDTO2));

        learningAchievementDTO.setAwardedBy(awardingProcessDTO);

        LearningOpportunityDTO learningOpportunityDTO = new LearningOpportunityDTO();
        learningOpportunityDTO.setId(URI.create("urn:opp:1"));
        learningOpportunityDTO.setTitle(new LiteralMap(Locale.ENGLISH.toString(), "TITLE"));
        learningOpportunityDTO.setProvidedBy(Arrays.asList(issuer));

        LearningOpportunityDTO learningOpportunityDTO2 = new LearningOpportunityDTO();
        learningOpportunityDTO2.setId(URI.create("urn:opp:2"));
        learningOpportunityDTO2.setTitle(new LiteralMap(Locale.ENGLISH.toString(), "TITLE"));
        //learningOpportunityDTO2.setIsPartOf(Arrays.asList(learningOpportunityDTO));
        learningOpportunityDTO2.setProvidedBy(Arrays.asList(issuer));

        learningOpportunityDTO.setHasPart(Arrays.asList(learningOpportunityDTO2));

        LearningActivityDTO learningActivityDTO = new LearningActivityDTO();
        learningActivityDTO.setId(URI.create("urn:act:1"));
        learningActivityDTO.setTitle(new LiteralMap(Locale.ENGLISH.toString(), "TITLE"));

        LearningActivityDTO learningActivityDTO2 = new LearningActivityDTO();
        learningActivityDTO2.setId(URI.create("urn:act:2"));
        learningActivityDTO2.setTitle(new LiteralMap(Locale.ENGLISH.toString(), "TITLE"));

        learningActivityDTO.setAwardedBy(awardingProcessDTO);
        learningActivityDTO.setLearningOpportunity(learningOpportunityDTO);
        learningActivityDTO2.setAwardedBy(awardingProcessDTO);

        //awardingProcessDTO.setAwards(Arrays.asList(learningAchievementDTO, learningAchievementDTO2, learningActivityDTO, learningActivityDTO2));
        //learningActivityDTO2.setIsPartOf(Arrays.asList(learningActivityDTO));
        learningActivityDTO.setHasPart(Arrays.asList(learningActivityDTO2));
        learningActivityDTO.setHasPart(Arrays.asList(learningActivityDTO2));

        credentialSubject.getHasClaim().add(learningAchievementDTO);
        credentialSubject.getHasClaim().add(learningActivityDTO);

        europeanDigitalCredentialDTO.setIssuer(issuer);
        europeanDigitalCredentialDTO.setCredentialSubject(credentialSubject);
        europeanDigitalCredentialDTO.setDisplayParameter(getDisplayParameter());

        ShaclValidator2017 credentialSchema = new ShaclValidator2017(URI.create("http://dev.everisdx.io/datamodel/shacl/EDC-generic.ttl"));
        europeanDigitalCredentialDTO.setCredentialSchema(Arrays.asList(credentialSchema));
        return europeanDigitalCredentialDTO;
    }

    public static ConceptDTO getENLanguageConceptDTO() {
        ConceptDTO enLanguage = new ConceptDTO();
        enLanguage.setId(URI.create("http://publications.europa.eu/resource/authority/language/ENG"));
        ConceptSchemeDTO conceptSchemeDTO = new ConceptSchemeDTO();
        conceptSchemeDTO.setId(URI.create("http://publications.europa.eu/resource/authority/language"));
        enLanguage.setInScheme(conceptSchemeDTO);
        return enLanguage;
    }
}
