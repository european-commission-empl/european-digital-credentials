package eu.europa.ec.empl.edci.mapper;

import eu.europa.ec.empl.edci.constants.EDCIConstants;
import eu.europa.ec.empl.edci.datamodel.model.*;
import eu.europa.ec.empl.edci.datamodel.model.base.CredentialHolderDTO;
import eu.europa.ec.empl.edci.datamodel.model.verifiable.presentation.EuropassPresentationDTO;
import eu.europa.ec.empl.edci.datamodel.model.verifiable.presentation.URIElementDTO;
import eu.europa.ec.empl.edci.datamodel.model.verifiable.presentation.VerificationCheckDTO;
import eu.europa.ec.empl.edci.datamodel.view.*;
import eu.europa.ec.empl.edci.util.EDCICredentialModelUtil;
import eu.europa.ec.empl.edci.util.ResourcesUtil;
import org.mapstruct.*;
import org.springframework.context.i18n.LocaleContextHolder;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", uses = {PresentationCommonsMapper.class})
public interface EuropassCredentialPresentationMapper {

    public static final org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(EuropassCredentialPresentationMapper.class);

    static SimpleDateFormat formatterDateOnlyOwner = new SimpleDateFormat(EDCIConstants.DATE_FRONT_LOCAL_OWNER);
    static SimpleDateFormat formatterDateFull = new SimpleDateFormat(EDCIConstants.DATE_FRONT_GMT);


    @Mappings({
            @Mapping(source = "europassCredentialDTO", target = "credentialMetadata", qualifiedByName = "toCredentialMetadataTabView"),
            @Mapping(source = "credentialSubject.performed", target = "activities"),
            @Mapping(source = "credentialSubject.achieved", target = "achievements"),
            @Mapping(source = "credentialSubject.entitledTo", target = "entitlements"),
            @Mapping(source = "credential.issuer", target = "issuerCredential")
    })
    EuropassCredentialPresentationView toEuropassCredentialPresentationFromCredentialView(EuropassCredentialDTO europassCredentialDTO, @Context Boolean addLists);

    default EuropassCredentialPresentationView toEuropassCredentialPresentationView(CredentialHolderDTO credentialHolderDTO, @Context Boolean addLists) {
        EuropassCredentialPresentationView view = toEuropassCredentialPresentationFromCredentialView(credentialHolderDTO.getCredential(), addLists);
        if (credentialHolderDTO != null && credentialHolderDTO instanceof EuropassPresentationDTO) {
            view.setIssuerPresentation(toOrganizationTabView(credentialHolderDTO.getIssuer()));

            //TODO vp, put in Jira this: VP in the viewer: The type shown is the one from the VP (if EC, the EC obviously)
            if (credentialHolderDTO.getType() != null && credentialHolderDTO.getType().getTargetName() != null) {
                view.getCredentialMetadata().setType(credentialHolderDTO.getType().getTargetName().getStringContent(LocaleContextHolder.getLocale().getLanguage()));
            }

            view.getCredentialMetadata().setExpirationDate(toStringDateFull(least(credentialHolderDTO.getExpirationDate(), credentialHolderDTO.getCredential().getExpirationDate())));

        }
        return view;
    }

    default String toStringDateFull(Date date) {

        if (date == null) {
            return null;
        }

        return formatterDateFull.format(date);

    }

    default Date least(Date a, Date b) {
        return a == null ? b : (b == null ? a : (a.before(b) ? a : b));
    }

    @BeforeMapping()
    default void beforeEuropassCredentialPresentationView(EuropassCredentialDTO europassCredentialDTO, @Context Boolean addLists, @MappingTarget EuropassCredentialPresentationView target) {

        if (europassCredentialDTO != null && europassCredentialDTO.getSubCredentialsXML() != null) {
            try {
                europassCredentialDTO.setSubCredentials(new EDCICredentialModelUtil().parseSubCredentials(europassCredentialDTO.getSubCredentialsXML()));
            } catch (Exception e) {
                logger.error("Error parsing sub credentials. Leaving the list empty");
            }
        }

    }

    default <T> void doRemoveDuplicatedCollectionElements(Collection<T> source, Collection<T> list) {
        if (source != null && list != null) {
            source.stream().forEach(item -> {
                if (list.contains(item)) {
                    list.remove(item);
                }
            });
        }
    }

    @AfterMapping()
    default void afterEuropassCredentialPresentationView(EuropassCredentialDTO europassCredentialDTO, @Context Boolean addLists, @MappingTarget EuropassCredentialPresentationView target) {

        if (target != null) {

            //We obtain all the elements of the same type in depth order
            Set<AchievementTabView> achList = new ResourcesUtil().findAllObjectsInRecursivily(target, AchievementTabView.class, ITabView.class).stream().collect(Collectors.toSet());
            Set<ActivityTabView> actList = new ResourcesUtil().findAllObjectsInRecursivily(target, ActivityTabView.class, ITabView.class).stream().collect(Collectors.toSet());
            Set<EntitlementTabView> entList = new ResourcesUtil().findAllObjectsInRecursivily(target, EntitlementTabView.class, ITabView.class).stream().collect(Collectors.toSet());
            Set<OrganizationTabView> orgList = new ResourcesUtil().findAllObjectsInRecursivily(target, OrganizationTabView.class, ITabView.class).stream().collect(Collectors.toSet());
            Set<AssessmentTabView> asmList = new ResourcesUtil().findAllObjectsInRecursivily(target, AssessmentTabView.class, ITabView.class).stream().collect(Collectors.toSet());

            //We take out existing entries from the list to avoid duplication
            this.doRemoveDuplicatedCollectionElements(target.getAchievements(), achList);
            this.doRemoveDuplicatedCollectionElements(target.getActivities(), actList);
            this.doRemoveDuplicatedCollectionElements(target.getEntitlements(), entList);
            this.doRemoveDuplicatedCollectionElements(target.getOrganizationsList(), orgList);

            if (addLists) {
                target.setAchievementsList(achList);
                target.setActivitiesList(actList);
                target.setEntitlementsList(entList);
                target.setOrganizationsList(orgList);
                target.setAssessmentsList(asmList);
            } else {
                target.setActivitiesList(null);
                target.setAchievementsList(null);
                target.setAssessmentsList(null);
                target.setEntitlementsList(null);
                target.setOrganizationsList(null);
            }

            //            We remove all the elements that are related directly to the credential
            if (target.getAchievements() != null && target.getAchievements().isEmpty()) {
                achList.removeAll(target.getAchievements());
            }
            if (target.getActivities() != null && target.getActivities().isEmpty()) {
                actList.removeAll(target.getActivities());
            }
            if (target.getEntitlements() != null && target.getEntitlements().isEmpty()) {
                entList.removeAll(target.getEntitlements());
            }
            orgList.remove(target.getIssuerCredential());
            orgList.remove(target.getIssuerPresentation());

        }

    }

//    @Deprecated
//    default <T extends ITabView> SortedSet<T> orderByDepth(Map<T, Integer> map) {
//
//        SortedSet<T> set = new TreeSet<T>((it1, it2) -> it1.getDepth().compareTo(it2.getDepth()));
//
//        for (T tab : map.keySet()) {
//            tab.setDepth(map.get(tab));
//            set.add(tab);
//        }
//
//        Integer ans = null;
//        Integer counter = 1;
//        for (T tab : map.keySet()) {
//
//            if (ans != null) {
//
//            }
//
//            ans = tab.getDepth();
//        }
//
//        return set;
//    }

    @Mappings({
            @Mapping(source = "europassCredentialDTO", target = "credentialMetadata", qualifiedByName = "toCredentialMetadataTabView")
    })
    EuropassCredentialPresentationLiteView toEuropassCredentialPresentationLiteView(EuropassCredentialDTO europassCredentialDTO);

    @BeforeMapping()
    default void beforeEuropassCredentialPresentationLiteView(EuropassCredentialDTO europassCredentialDTO, @MappingTarget EuropassCredentialPresentationLiteView target) {

        if (europassCredentialDTO != null && europassCredentialDTO.getSubCredentialsXML() != null) {
            try {
                europassCredentialDTO.setSubCredentials(new EDCICredentialModelUtil().parseSubCredentials(europassCredentialDTO.getSubCredentialsXML()));
            } catch (Exception e) {
                logger.error("Error parsing sub credentials. Leaving the list empty");
            }
        }

    }

    @Mappings({
            @Mapping(source = "id", target = "uuid"),
            @Mapping(source = "originalXML", target = "xml"),
            @Mapping(target = "issuanceDate", qualifiedByName = "toStringDateFull"),
            @Mapping(target = "expirationDate", qualifiedByName = "toStringDateFull")
    })
    CredentialMetadataTabView toCredentialMetadataTabView(EuropassCredentialDTO credentialDTO);

    List<VerificationCheckFieldView> toVerificationCheckFieldViewList(List<VerificationCheckDTO> verificationCheckDTO);

    @Mappings({
            @Mapping(source = "description", target = "descrAvailableLangs"),
            @Mapping(source = "longDescription", target = "longDescrAvailableLangs"),
            @Mapping(source = "description", target = "description"),
            @Mapping(source = "longDescription", target = "longDescription")
    })
    VerificationCheckFieldView toVerificationCheckFieldView(VerificationCheckDTO verificationCheckDTO);


    @Mappings({
            @Mapping(target = "dateOfBirth", qualifiedByName = "toStringDateOwner"),
            @Mapping(source = "note", target = "additionalNote")
    })
    CredentialSubjectTabView toCredentialSubjectTabView(PersonDTO personDTO);

    @Mappings({
            @Mapping(source = "hasLocation", target = "location"),
            @Mapping(source = "unitOf", target = "parentOrganization"),
    })
    OrganizationTabView toOrganizationTabView(OrganizationDTO organization);

    @Mappings({
            @Mapping(source = "learningOutcomeType", target = "type")
    })
    LearningOutcomeFieldView toLearningOutcomeFieldView(LearningOutcomeDTO organization);

    @Mappings({
            @Mapping(source = "additionalNote", target = "moreInformation"),
            @Mapping(source = "hasPart", target = "subEntitlements"),
            @Mapping(target = "issuedDate", qualifiedByName = "toStringDateLocal"),
            @Mapping(target = "expiryDate", qualifiedByName = "toStringDateLocal")
    })
    EntitlementTabView toEntitlementTabView(EntitlementDTO entitlement);

    @Mappings({
            @Mapping(source = "limitOrganization", target = "validWith"),
            @Mapping(source = "limitOccupation", target = "toWorkAs"),
            @Mapping(source = "limitJurisdiction", target = "validWithin"),
            @Mapping(source = "supplementaryDocument", target = "otherDocs"),
            @Mapping(source = "additionalNote", target = "moreInformation")
    })
    EntitlementSpecTabView toEntitlementSpecTabView(EntitlementSpecificationDTO entitlement);

    @Mappings({
            @Mapping(source = "assessedBy", target = "conductedBy"),
            @Mapping(target = "issuedDate", qualifiedByName = "toStringDateFull"),
            @Mapping(source = "hasPart", target = "subAssessments"),
            @Mapping(source = "additionalNote", target = "moreInformation")
    })
    AssessmentTabView toAssessmentTabView(AssessmentDTO assessment);

    @Mappings({
            @Mapping(source = "assessmentType", target = "type"),
            @Mapping(source = "additionalNote", target = "moreInformation")
    })
    AssessmentSpecTabView toAssessmentSpecTabView(AssessmentSpecificationDTO assessment);

    @AfterMapping()
    default void toAssessmentTabPlusView(AssessmentDTO assessment, @MappingTarget AssessmentTabView assTab) {

        if (assessment != null && assessment.getShortenedGrading() != null && assessment.getResultDistribution() != null) {

            if (assTab.getSpecifiedBy() != null) {
                assTab.setSpecifiedBy(new AssessmentSpecTabView());
            }
            assTab.getSpecifiedBy().setShortenedGrading(shortenedGradingDTOToShortenedGradingFieldView(assessment.getShortenedGrading()));
            assTab.getSpecifiedBy().setResultDistribution(resultDistributionDTOToResultDistributionFieldView(assessment.getResultDistribution()));
        }

    }

    ResultDistributionFieldView resultDistributionDTOToResultDistributionFieldView(ResultDistributionDTO resultDistributionDTO);

    ShortenedGradingFieldView shortenedGradingDTOToShortenedGradingFieldView(ShortenedGradingDTO shortenedGradingDTO);

    @Mappings({
            @Mapping(source = "hasPart", target = "subActivities"),
            @Mapping(source = "additionalNote", target = "moreInformation"),
            @Mapping(source = "workload", target = "workloadInHours", qualifiedByName = "toStringHours"),
            @Mapping(source = "startedAtTime", target = "startDate", qualifiedByName = "toStringDateFull"),
            @Mapping(source = "endedAtTime", target = "endDate", qualifiedByName = "toStringDateFull")
    })
    ActivityTabView toActivityTabView(LearningActivityDTO learningActivity);

    @Mappings({
            @Mapping(source = "mode", target = "modeOfLearning"),
            @Mapping(source = "language", target = "instructionLanguage"),
            @Mapping(source = "supplementaryDocument", target = "otherDocuments"),
            @Mapping(source = "workload", target = "workloadInHours", qualifiedByName = "toStringHours"),
            @Mapping(source = "additionalNote", target = "moreInformation")
    })
    ActivitySpecTabView toActivitySpecTabView(LearningActivitySpecificationDTO learningActivity);

    @Mappings({
            @Mapping(source = "wasAwardedBy.awardingDate", target = "awardingDate", qualifiedByName = "toStringDateFull"),
            @Mapping(source = "wasAwardedBy.awardingBody", target = "awardingBody"),
            @Mapping(source = "wasDerivedFrom", target = "provenBy"),
            @Mapping(source = "wasInfluencedBy", target = "influencedBy"),
            @Mapping(source = "entitlesTo", target = "entitledOwnerTo"),
            @Mapping(source = "hasPart", target = "subAchievements"),
            @Mapping(source = "additionalNote", target = "moreInformation")
    })
    AchievementTabView toAchievementTabView(LearningAchievementDTO learningAchievement);

    @Mappings({
            @Mapping(source = "volumeOfLearning", target = "workloadInHours", qualifiedByName = "toStringHours"),
            @Mapping(source = "maximumDuration", target = "maximumDurationInMonths", qualifiedByName = "toStringMonths"),
            @Mapping(source = "iscedFCode", target = "thematicArea"),
            @Mapping(source = "mode", target = "learningMode"),
            @Mapping(source = "language", target = "instructionLanguage"),
            @Mapping(source = "entryRequirementNote", target = "entryRequirements"),
            @Mapping(source = "supplementaryDocument", target = "otherDocuments"),
            @Mapping(source = "additionalNote", target = "moreInformation")
    })
    AchievementSpecTabView toAchievementSpecTabView(LearningSpecificationDTO learningAchievement);

    QualificationFieldView toQualificationFieldView(QualificationDTO qualificationDTO);

    @AfterMapping()
    default void toAchievementSpecQualificationTabView(LearningSpecificationDTO learningAchievement, @MappingTarget AchievementSpecTabView achTab) {

        if (learningAchievement != null && learningAchievement instanceof QualificationDTO) {
            achTab.setQualification(toQualificationFieldView((QualificationDTO) learningAchievement));
        }

    }

    @Named("toStringDateOwner")
    default String toStringDateOwner(Date date) {

        if (date == null) {
            return null;
        }

        return formatterDateOnlyOwner.format(date);

    }

    default String toStringURIElement(URIElementDTO uri) {

        if (uri == null) {
            return null;
        }

        return uri.getUri();

    }

}
