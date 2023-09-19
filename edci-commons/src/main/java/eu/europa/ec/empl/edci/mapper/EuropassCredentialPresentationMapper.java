package eu.europa.ec.empl.edci.mapper;

import eu.europa.ec.empl.edci.constants.ControlledListConcept;
import eu.europa.ec.empl.edci.constants.EDCIConstants;
import eu.europa.ec.empl.edci.datamodel.jsonld.model.*;
import eu.europa.ec.empl.edci.datamodel.jsonld.model.dataTypes.*;
import eu.europa.ec.empl.edci.model.view.EuropassCredentialPresentationLiteView;
import eu.europa.ec.empl.edci.model.view.EuropassCredentialPresentationView;
import eu.europa.ec.empl.edci.model.view.base.AgentView;
import eu.europa.ec.empl.edci.model.view.base.ITabView;
import eu.europa.ec.empl.edci.model.view.fields.*;
import eu.europa.ec.empl.edci.model.view.tabs.*;
import eu.europa.ec.empl.edci.service.ControlledListCommonsService;
import eu.europa.ec.empl.edci.util.CredentialUtil;
import eu.europa.ec.empl.edci.util.DiplomaUtil;
import eu.europa.ec.empl.edci.util.ResourcesUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", uses = {PresentationCommonsMapper.class, BaseMapper.class, EuropassCredentialDetailRestMapper.class}, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class EuropassCredentialPresentationMapper {

    @Autowired
    private ControlledListCommonsService controlledListcommonsService;
    @Autowired
    private DiplomaUtil diplomaUtil;

    @Autowired
    private CredentialUtil credentialUtil;

    public static final Logger logger = LogManager.getLogger(EuropassCredentialPresentationMapper.class);

    //ToDo -> USE NEW EuropeanDigitalCredentialPresentationView, SHOULD BE CREATED IN VIEWER, THEN MOVE THIS MAPPER TO VIEWER

    @Mappings({
            @Mapping(source = "europassCredentialDTO", target = "credentialMetadata", qualifiedByName = "toCredentialMetadataTabView"),
            @Mapping(source = "issuer", target = "issuerCredential")
    })
    public abstract EuropassCredentialPresentationView toEuropassCredentialPresentationView(EuropeanDigitalCredentialDTO europassCredentialDTO, @Context Boolean addLists);

    public EuropassCredentialPresentationView toEuropassCredentialPresentationView(EuropeanDigitalPresentationDTO credentialHolderDTO, @Context Boolean addLists) {
        EuropassCredentialPresentationView view = null;

        if (credentialHolderDTO != null) {
            if (credentialHolderDTO.getVerifiableCredential() != null && !credentialHolderDTO.getVerifiableCredential().isEmpty()) {
                view = toEuropassCredentialPresentationView((EuropeanDigitalCredentialDTO) credentialHolderDTO.getVerifiableCredential().get(0), addLists);
            }

            if (view != null) {
                if (credentialHolderDTO.getHolder() != null && !credentialHolderDTO.getHolder().isEmpty()) {
                    view.setIssuerPresentation(toOrganizationTabView((OrganisationDTO) credentialHolderDTO.getHolder().get(0)));
                }
                view.getCredentialMetadata().setFormatType(EDCIConstants.Certificate.CREDENTIAL_TYPE_EUROPASS_PRESENTATION);
            }

        }

        return view;
    }

    public Date least(Date a, Date b) {
        return a == null ? b : (b == null ? a : (a.before(b) ? a : b));
    }

    public <T> void doRemoveDuplicatedCollectionElements(Collection<T> source, Collection<T> list) {
        if (source != null && list != null) {
            source.stream().forEach(item -> {
                if (list.contains(item)) {
                    list.remove(item);
                }
            });
        }
    }

    @AfterMapping()
    public void afterEuropassCredentialPresentationView(EuropeanDigitalCredentialDTO europassCredentialDTO, @Context Boolean addLists, @MappingTarget EuropassCredentialPresentationView target) {

        if (target != null) {

            if (target.getCredentialMetadata() != null) {
                target.getCredentialMetadata().setFormatType(EDCIConstants.Certificate.CREDENTIAL_TYPE_EUROPASS_CREDENTIAL);
            }

            if (europassCredentialDTO.getCredentialSubject() != null && europassCredentialDTO.getCredentialSubject().getHasClaim() != null && !europassCredentialDTO.getCredentialSubject().getHasClaim().isEmpty()) {
                target.setAssessments(new ArrayList<>());
                target.setActivities(new ArrayList<>());
                target.setAchievements(new ArrayList<>());
                target.setEntitlements(new ArrayList<>());

                for (ClaimDTO claimDTO : europassCredentialDTO.getCredentialSubject().getHasClaim()) {
                    if (claimDTO instanceof LearningActivityDTO) {
                        target.getActivities().add(this.toActivityTabView((LearningActivityDTO) claimDTO));
                    } else if (claimDTO instanceof LearningAssessmentDTO) {
                        target.getAssessments().add(this.toAssessmentTabView((LearningAssessmentDTO) claimDTO));
                    } else if (claimDTO instanceof LearningAchievementDTO) {
                        target.getAchievements().add(this.toAchievementTabView((LearningAchievementDTO) claimDTO));
                    } else if (claimDTO instanceof LearningEntitlementDTO) {
                        target.getEntitlements().add(this.toEntitlementTabView((LearningEntitlementDTO) claimDTO));
                    }
                }
            }


            //We obtain all the elements of the same type in depth order
            Set<AchievementTabView> achList = new ResourcesUtil().findAllObjectsInRecursively(target, AchievementTabView.class, ITabView.class).stream().collect(Collectors.toSet());
            Set<ActivityTabView> actList = new ResourcesUtil().findAllObjectsInRecursively(target, ActivityTabView.class, ITabView.class).stream().collect(Collectors.toSet());
            Set<EntitlementTabView> entList = new ResourcesUtil().findAllObjectsInRecursively(target, EntitlementTabView.class, ITabView.class).stream().collect(Collectors.toSet());
            Set<OrganizationTabView> orgList = new ResourcesUtil().findAllObjectsInRecursively(target, OrganizationTabView.class, ITabView.class).stream().collect(Collectors.toSet());
            Set<AssessmentTabView> asmList = new ResourcesUtil().findAllObjectsInRecursively(target, AssessmentTabView.class, ITabView.class).stream().collect(Collectors.toSet());

            //We take out existing entries from the list to avoid duplication
            this.doRemoveDuplicatedCollectionElements(target.getAchievements(), achList);
            this.doRemoveDuplicatedCollectionElements(target.getActivities(), actList);
            this.doRemoveDuplicatedCollectionElements(target.getEntitlements(), entList);
            this.doRemoveDuplicatedCollectionElements(target.getOrganizationsList(), orgList);
            this.doRemoveDuplicatedCollectionElements(target.getAssessmentsList(), asmList);

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

            //We remove all the elements that are related directly to the credential
            if (target.getAchievements() != null && target.getAchievements().isEmpty()) {
                achList.removeAll(target.getAchievements());
            }
            if (target.getActivities() != null && target.getActivities().isEmpty()) {
                actList.removeAll(target.getActivities());
            }
            if (target.getEntitlements() != null && target.getEntitlements().isEmpty()) {
                entList.removeAll(target.getEntitlements());
            }
            if (target.getAssessments() != null && target.getAssessments().isEmpty()) {
                asmList.removeAll(target.getAssessments());
            }

            orgList.remove(target.getIssuerCredential());
            orgList.remove(target.getIssuerPresentation());

            if (target.getIssuerCredential() != null && !europassCredentialDTO.getEvidence().isEmpty()) {
                Evidence mandateEvidence = this.credentialUtil.getEvidenceByType(ControlledListConcept.EVIDENCE_TYPE_MANDATE, europassCredentialDTO.getEvidence());
                Evidence accreditationEvidence = this.credentialUtil.getEvidenceByType(ControlledListConcept.EVIDENCE_TYPE_ACCREDITATION, europassCredentialDTO.getEvidence());

                if (accreditationEvidence != null) {
                    target.getIssuerCredential().setAccreditationEvidence(toEvidenceFieldView(accreditationEvidence));
                }

                if (mandateEvidence != null) {
                    target.getIssuerCredential().setMandateEvidence(toEvidenceFieldView(mandateEvidence));
                }

            }

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
    public abstract EuropassCredentialPresentationLiteView toEuropassCredentialPresentationLiteView(EuropeanDigitalCredentialDTO europassCredentialDTO);

    @Mappings({
            @Mapping(source = "id", target = "uuid"),
            @Mapping(source = "displayParameter.primaryLanguage", target = "primaryLanguage", qualifiedByName = "toLanguageCodeString"),
            @Mapping(source = "displayParameter.language", target = "availableLanguages", qualifiedByName = "toLanguageCodeStringList"),
            @Mapping(source = "displayParameter.title", target = "title"),
            @Mapping(source = "credentialProfiles", target = "type"),
            @Mapping(source = "displayParameter.description", target = "description"),
            @Mapping(target = "validFrom", qualifiedByName = "toStringDateLocal"),
            @Mapping(target = "issued", qualifiedByName = "toStringDateLocal"),
            @Mapping(target = "expirationDate", qualifiedByName = "toStringDateLocal")
    })
    public abstract CredentialMetadataTabView toCredentialMetadataTabView(EuropeanDigitalCredentialDTO credentialDTO);

    // List<VerificationCheckFieldView> toVerificationCheckFieldViewList(List<VerificationCheckDTO> verificationCheckDTO);

    // VerificationCheckFieldView toVerificationCheckFieldView(VerificationCheckDTO verificationCheckDTO);

    @Mappings({
            @Mapping(target = "dateOfBirth", qualifiedByName = "toStringDateOwner")
    })
    public abstract CredentialSubjectTabView toCredentialSubjectTabView(PersonDTO personDTO);

    public abstract List<OrganizationTabView> toOrganizationTabViewList(List<OrganisationDTO> organizationDTOS);

    @Mappings({
            @Mapping(target = "locations", source = "location"),
            @Mapping(target = "childOrganisations", source = "hasSubOrganization"),
            @Mapping(target = "vatIdentifier", source = "vatIdentifier", qualifiedByName = "toFieldLegalIdentifierList"),
            @Mapping(target = "taxIdentifier", source = "taxIdentifier", qualifiedByName = "toFieldLegalIdentifierList"),
            @Mapping(target = "dateModified", qualifiedByName = "toStringDateLocal"),
    })
    public abstract OrganizationTabView toOrganizationTabView(OrganisationDTO organization);

    public abstract EvidenceFieldView toEvidenceFieldView(Evidence evidence);

    public abstract LearningOutcomeFieldView toLearningOutcomeFieldView(LearningOutcomeDTO organization);

    @Mappings({
            @Mapping(target = "dateIssued", qualifiedByName = "toStringDateLocal"),
            @Mapping(target = "expiryDate", qualifiedByName = "toStringDateLocal"),
            @Mapping(source = "hasPart", target = "subEntitlements")
    })
    public abstract EntitlementTabView toEntitlementTabView(LearningEntitlementDTO entitlement);

    public abstract EntitlementSpecTabView toEntitlementSpecTabView(LearningEntitlementSpecificationDTO entitlement);

    @Mappings({
            @Mapping(target = "dateIssued", qualifiedByName = "toStringDateFull"),
            @Mapping(source = "hasPart", target = "subAssessments")
    })
    public abstract AssessmentTabView toAssessmentTabView(LearningAssessmentDTO assessment);

    public abstract AssessmentSpecTabView toAssessmentSpecTabView(LearningAssessmentSpecificationDTO assessment);

    @AfterMapping()
    public void toAssessmentTabPlusView(LearningAssessmentDTO assessment, @MappingTarget AssessmentTabView assTab) {

        if (assessment != null && assessment.getShortenedGrading() != null && assessment.getResultDistribution() != null) {

            if (assTab.getSpecifiedBy() != null) {
                assTab.setSpecifiedBy(new AssessmentSpecTabView());
            }
            assTab.setShortenedGrading(shortenedGradingDTOToShortenedGradingFieldView(assessment.getShortenedGrading()));
            assTab.setResultDistribution(resultDistributionDTOToResultDistributionFieldView(assessment.getResultDistribution()));
        }

    }

    public abstract ResultDistributionFieldView resultDistributionDTOToResultDistributionFieldView(ResultDistributionDTO resultDistributionDTO);

    public abstract ShortenedGradingFieldView shortenedGradingDTOToShortenedGradingFieldView(ShortenedGradingDTO shortenedGradingDTO);

    @Mappings({
            @Mapping(target = "locations", source = "location"),
            @Mapping(source = "workload", target = "workload", qualifiedByName = "toStringHours"),
            @Mapping(source = "hasPart", target = "subActivities")
    })
    public abstract ActivityTabView toActivityTabView(LearningActivityDTO learningActivity);

    @Mappings({
            @Mapping(source = "volumeOfLearning", target = "volumeOfLearning", qualifiedByName = "toStringHours")
    })
    public abstract ActivitySpecTabView toActivitySpecTabView(LearningActivitySpecificationDTO learningActivity);

    @Mappings({
            @Mapping(source = "startDate", target = "startDate", qualifiedByName = "toStringDateLocal"),
            @Mapping(source = "endDate", target = "endDate", qualifiedByName = "toStringDateLocal")
    })
    public abstract PeriodOfTimeFieldView toPeriodOfTimeFieldView(PeriodOfTimeDTO periodOfTimeDTO);

    @Mappings({
            @Mapping(source = "awardedBy.awardingDate", target = "awardedBy.awardingDate", qualifiedByName = "toStringDateLocal"),
            @Mapping(source = "awardedBy.awardingBody", target = "awardedBy.awardingBody"),
            @Mapping(source = "hasPart", target = "subAchievements")
    })
    public abstract AchievementTabView toAchievementTabView(LearningAchievementDTO learningAchievement);

    public abstract List<String> toStringFromCreditPoint(List<CreditPointDTO> creditPointDTOS);

    public String toStringFromCreditPoint(CreditPointDTO creditPointDTO) {
        String framework = creditPointDTO.getFramework().getPrefLabel() != null ?
                creditPointDTO.getFramework().getPrefLabel().toString() : creditPointDTO.getId().toString();
        return framework + EDCIConstants.StringPool.STRING_SPACE + creditPointDTO.getPoint();
    }

    @AfterMapping()
    public void toAgentViewAfterMapping(AgentDTO agentDTO, @MappingTarget AgentView agentView) {
        if (agentDTO instanceof OrganisationDTO) {
            agentView.setLegalName(toStringFromLiteralMap(((OrganisationDTO) agentDTO).getLegalName()));
            agentView.setEidasIdentifier(toFieldIdentifier(((OrganisationDTO) agentDTO).geteIDASIdentifier()));
            agentView.setVatIdentifier(toFieldLegalIdentifierList(((OrganisationDTO) agentDTO).getVatIdentifier()));
            agentView.setTaxIdentifier(toFieldLegalIdentifierList(((OrganisationDTO) agentDTO).getTaxIdentifier()));
            agentView.setRegistration(toFieldLegalIdentifier(((OrganisationDTO) agentDTO).getRegistration()));
        } else if (agentDTO instanceof PersonDTO) {
            agentView.setNationalID(toFieldLegalIdentifier(((PersonDTO) agentDTO).getNationalID()));
            agentView.setFullName(toStringFromLiteralMap(((PersonDTO) agentDTO).getFullName()));
        }
    }

    @Mappings({
            @Mapping(source = "volumeOfLearning", target = "volumeOfLearning", qualifiedByName = "toStringHours"),
            @Mapping(source = "maximumDuration", target = "maximumDuration", qualifiedByName = "toStringMonths")
    })
    public abstract AchievementSpecTabView toAchievementSpecTabView(LearningAchievementSpecificationDTO learningAchievement);

    @Mappings({
            @Mapping(source = "eqfLevel", target = "EQFLevel"),
            @Mapping(source = "nqfLevel", target = "NQFLevel")
    })
    public abstract QualificationFieldView toQualificationFieldView(QualificationDTO qualificationDTO);

    @AfterMapping()
    public void toAchievementSpecQualificationTabView(LearningAchievementSpecificationDTO learningAchievement, @MappingTarget AchievementSpecTabView achTab) {

        if (learningAchievement != null && learningAchievement instanceof QualificationDTO) {
            achTab.setQualification(toQualificationFieldView((QualificationDTO) learningAchievement));
        }

    }

    @Mappings({
            @Mapping(target = "dateIssued", qualifiedByName = "toStringDateFull"),
            @Mapping(target = "reviewDate", qualifiedByName = "toStringDateFull"),
            @Mapping(target = "expiryDate", qualifiedByName = "toStringDateFull")
    })
    public abstract AccreditationFieldView toFieldAccreditation(AccreditationDTO accr);

    public String qualificationToString(QualificationDTO accr) {

        String returnValue = null;

        if (accr != null) {
            LiteralMap text = accr.getTitle();
            if (text != null) {
                returnValue = text.toString();
            }
        }

        return returnValue;
    }

    public abstract GradingSchemeFieldView toGradingSchemeFieldView(GradingSchemeDTO gradingSchemeDTO);

    @Mappings({
            @Mapping(target = "content", source = "media", qualifiedByName = "generateDataUriScheme")
    })
    public abstract MediaObjectFieldView toFieldMediaObject(MediaObjectDTO media);

    @Named("generateDataUriScheme")
    public String generateDataUriScheme(MediaObjectDTO mediaObjectDTO) {
        return this.diplomaUtil.getDataUriScheme(mediaObjectDTO);
    }

    @Named("toStringDateOwner")
    public String toStringDateOwner(ZonedDateTime date) {

        if (date == null) {
            return null;
        }

        return date.format(DateTimeFormatter.ofPattern(EDCIConstants.DATE_FRONT_LOCAL_OWNER));

    }

    @Named("toLanguageCodeString")
    public String toLanguageCodeString(ConceptDTO conceptDTO) {
        return this.controlledListcommonsService.searchLanguageISO639ByConcept(conceptDTO);
    }

    @Named("toLanguageCodeStringList")
    public List<String> toLanguageCodeStringList(List<ConceptDTO> conceptDTO) {
        return conceptDTO.stream().map(concept -> this.controlledListcommonsService.searchLanguageISO639ByConcept(concept)).collect(Collectors.toList());
    }

    /**
     * Required here for awardingBody afterMapping
     */

    public abstract IdentifierFieldView toFieldLegalIdentifier(LegalIdentifier identifier);

    public abstract IdentifierFieldView toFieldIdentifier(Identifier identifier);

    @Named("toFieldLegalIdentifierList")
    public abstract List<IdentifierFieldView> toFieldLegalIdentifierList(List<LegalIdentifier> identifier);

    public abstract List<IdentifierFieldView> toFieldIdentifierList(List<Identifier> identifier);


    public String toStringFromLiteralMap(LiteralMap map) {
        return map != null ? map.toString() : "";
    }

   /* public List<String> toStringListFromLiteralMap(LiteralMap map) {
        return map.toStringList();
    }*/

}
