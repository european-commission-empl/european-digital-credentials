package eu.europa.ec.empl.edci.service;

import eu.europa.ec.empl.edci.constants.ControlledList;
import eu.europa.ec.empl.edci.constants.ControlledListConcept;
import eu.europa.ec.empl.edci.constants.EDCIMessageKeys;
import eu.europa.ec.empl.edci.datamodel.jsonld.model.*;
import eu.europa.ec.empl.edci.datamodel.jsonld.model.dataTypes.ConceptDTO;
import eu.europa.ec.empl.edci.datamodel.jsonld.model.dataTypes.Identifier;
import eu.europa.ec.empl.edci.datamodel.validation.ValidationResult;
import eu.europa.ec.empl.edci.util.CredentialUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service that validates the external accreditations from QDR inside the credentials.
 * This works exclusively with Java classes, no Credential RDF is required for it and can be executed before the credential is created.
 */
@Service
public class QDRAccreditationValidationService {
    private static final Logger logger = LogManager.getLogger(QDRAccreditationValidationService.class);
    @Autowired
    private CredentialUtil credentialUtil;
    @Autowired
    private ControlledListCommonsService controlledListCommonsService;
    @Autowired
    private QDRAccreditationExternalService qdrAccreditationExternalService;


    /**
     * Checks if a EuropeanDigitalCredential contains an accreditation, it is assumed to be accredited and will fail if mandatory fields are not found.
     *
     * @param credentialDTO the European Credential DTO
     * @return the untranslated validationResult
     * @link CredentialUtil.isAccreditedCredential
     * @link EDCIValidationUtil.loadLocalizedMessages
     */
    public ValidationResult isCredentialCovered(EuropeanDigitalCredentialDTO credentialDTO) {
        ValidationResult validationResult = new ValidationResult(true);
        ValidationResult elegibilityChecks = passEligibilityChecks(credentialDTO);
        if (elegibilityChecks.isValid()) {
            logger.info("[{}] Checking if the accreditation is present in the QDR service", () -> credentialDTO.getId());
            Evidence evidence = this.getCredentialUtil().getEvidenceByType(ControlledListConcept.EVIDENCE_TYPE_ACCREDITATION, credentialDTO.getEvidence());
            String lang = this.getControlledListCommonsService().searchLanguageISO639ByConcept(credentialDTO.getDisplayParameter().getPrimaryLanguage());
            AccreditationDTO accreditationDTO = this.getQdrAccreditationExternalService().retrieveAccreditationByUri(evidence.getAccreditation().getId().toString(), lang);
            if (accreditationDTO != null) {
                logger.info("[{}] The accreditation is present in the QDR service", () -> credentialDTO.getId());
                List<LearningAchievementDTO> learningAchievementDTOList = this.getCredentialUtil().getClaimsOfClass(credentialDTO, LearningAchievementDTO.class);
                ValidationResult generalChecks = passGeneralChecks(credentialDTO, learningAchievementDTOList, accreditationDTO, lang);
                validationResult.setValid(generalChecks.isValid());
                validationResult.addValidationErrors(generalChecks.getValidationErrors());
            } else {
                validationResult.setValid(false);
                validationResult.addValidationError(EDCIMessageKeys.Acreditation.QDR_ACCREDITATION_NOT_FOUND, evidence.getAccreditation().getId().toString());
            }
        } else {
            validationResult.setValid(false);
            validationResult.addValidationErrors(elegibilityChecks.getValidationErrors());
        }

        return validationResult;
    }

    /**
     * Performs elegibility (format) tests
     *
     * @param europeanDigitalCredentialDTO
     * @return the untranslated Validation Result
     * @link EDCIValidationUtil.loadLocalizedMessages
     */
    public ValidationResult passEligibilityChecks(EuropeanDigitalCredentialDTO europeanDigitalCredentialDTO) {
        ValidationResult validationResult = new ValidationResult(true);
        URI credentialID = europeanDigitalCredentialDTO.getId();

        //Check if the credential profile is correct, has an accreditation evidence and has a top level achievement
        logger.info("[{}] Checking credential Profile", () -> credentialID);
        boolean isAccreditation = europeanDigitalCredentialDTO.getCredentialProfiles() != null && !europeanDigitalCredentialDTO.getCredentialProfiles().stream().filter(p -> p.getId().toString().equals(ControlledListConcept.CREDENTIAL_TYPE_ACCREDITATION.getUrl())).findFirst().isEmpty();
        if (!isAccreditation) {
            logger.info("[{}] The credential is not an accreditation, Eligibility check IS INVALID", () -> credentialID);
            validationResult.addValidationError(EDCIMessageKeys.Acreditation.CREDENTIAL_NOT_ACCREDITED_CREDENTIAL);
            validationResult.setValid(false);
        }

        logger.info("[{}] Checking evidence and Accreditation existence", () -> credentialID);
        Evidence accreditationEvidence = this.getCredentialUtil().getEvidenceByType(ControlledListConcept.EVIDENCE_TYPE_ACCREDITATION, europeanDigitalCredentialDTO.getEvidence());
        boolean hasAccreditationEvidence = accreditationEvidence != null && accreditationEvidence.getAccreditation() != null;
        if (!hasAccreditationEvidence) {
            logger.info("[{}] evidence or accreditation was not found, Eligibility check IS INVALID", () -> credentialID);
            validationResult.addValidationError(EDCIMessageKeys.Acreditation.EVIDENCE_ACCREDITATION_NOT_FOUND);
            validationResult.setValid(false);
        }

        logger.info("[{}] Checking the existence of a top level achievement", () -> credentialID);
        boolean hasAchievement = !europeanDigitalCredentialDTO.getCredentialSubject().getHasClaim().stream().filter(c -> c.getType().equalsIgnoreCase("LearningAchievement")).findFirst().isEmpty();
        if (!hasAchievement) {
            logger.info("Top level achievement was not found, Eligibility check IS INVALID", () -> credentialID);
            validationResult.addValidationError(EDCIMessageKeys.Acreditation.NO_TOP_LEVEL_ACHIEVEMENT);
            validationResult.setValid(false);
        }

        return validationResult;
    }

    /**
     * Perform General (Buisness) checks
     *
     * @param europeanDigitalCredentialDTO The credential to be checked
     * @param learningAchievementDTOList   The top level achievments to be checked
     * @param accreditationDTO             the accreditationDTO to be checked against
     * @param lang                         the language
     * @return the untranslated validation result
     */
    public ValidationResult passGeneralChecks(EuropeanDigitalCredentialDTO europeanDigitalCredentialDTO, List<LearningAchievementDTO> learningAchievementDTOList, AccreditationDTO accreditationDTO, String lang) {
        ValidationResult validationResult = new ValidationResult();
        validationResult.setValid(true);
        URI credentialID = europeanDigitalCredentialDTO.getId();
        //Accreditation was valid at the time of issuing the credential
        ZonedDateTime issuanceDate = europeanDigitalCredentialDTO.getIssuanceDate();
        logger.info("[{}] Checking accrediting issuance Date covered by Accreditation", () -> credentialID);
        if ((accreditationDTO.getDateIssued() != null && accreditationDTO.getExpiryDate() != null) && (issuanceDate.isBefore(accreditationDTO.getDateIssued()) || issuanceDate.isAfter(accreditationDTO.getExpiryDate()))) {
            logger.info("[{}] Issuance Date of Credential not Covered by the Accreditation, general check IS INVALID", () -> credentialID);
            validationResult.addValidationError(EDCIMessageKeys.Acreditation.CREDENTIAL_ISSUANCE_DATE_NOT_COVERED);
            validationResult.setValid(false);
        }

        for (LearningAchievementDTO learningAchievementDTO : learningAchievementDTOList) {

            //Accrediting organisation matches with any of the achievement's awarding bodies
            logger.info("[{}] Checking Accrediting Agent identifiers vs Awarding Body Identifiers of [{}]", () -> credentialID, () -> learningAchievementDTO.getName());
            if (accreditationDTO.getOrganisation() != null && !checkAwardingBodies(learningAchievementDTO, accreditationDTO)) {
                logger.info("[{}] Check IS INVALID for Accrediting Agent identifiers vs Awarding Body Identifiers of [{}]", () -> credentialID, () -> learningAchievementDTO.getName());
                validationResult.addValidationError(EDCIMessageKeys.Acreditation.ACCREDITING_ORG_NO_COVER_AWARDING_BODY, learningAchievementDTO.getName());
                validationResult.setValid(false);
            }

            //Any of the achievement's identifiers matches with any of the qualification's identifiers in the accreditation
            logger.info("[{}] Checking Limit Qualification identifiers vs Learning Specification Identifiers of [{}]", () -> credentialID, () -> learningAchievementDTO.getName());
            if (accreditationDTO.getLimitQualification() != null && !checkQualifications(learningAchievementDTO.getSpecifiedBy(), accreditationDTO)) {
                logger.info("[{}] Check IS INVALID for Limit Qualification (identifiers vs Learning Specification Identifiers of [{}]", () -> credentialID, () -> learningAchievementDTO.getName());
                validationResult.addValidationError(EDCIMessageKeys.Acreditation.LIMIT_QUALIFICATION_NOT_COVER_LEARNING_SPECIFICATION, learningAchievementDTO.getName());
                validationResult.setValid(false);
            }

            //The EQF Level of the achievement match with any of the EQF Levels specified in the accreditation
            if (!accreditationDTO.getLimitEQFLevel().isEmpty()) {
                if (learningAchievementDTO.getSpecifiedBy() instanceof QualificationDTO) {
                    logger.info("[{}] Achievement [{}] contains a Qualification, performing Concept ID check", () -> credentialID, () -> learningAchievementDTO.getName());
                    if (!checkEQFLevel((QualificationDTO) learningAchievementDTO.getSpecifiedBy(), accreditationDTO)) {
                        logger.info("[{}] Check IS INVALID for LimitEQFLevel vs EQF Level of qualification in achievement [{}]", () -> credentialID, () -> learningAchievementDTO.getName());
                        validationResult.addValidationError(EDCIMessageKeys.Acreditation.EQF_LEVEL_NOT_COVERED, learningAchievementDTO.getSpecifiedBy().getName());
                        validationResult.setValid(false);
                    }
                } else {
                    logger.info("[{}] checking Achievement [{}] vs limitEQF, but not Qualification found");
                    validationResult.addValidationError(EDCIMessageKeys.Acreditation.EQF_LEVEL_NOT_COVERED, learningAchievementDTO.getSpecifiedBy().getName());
                    validationResult.setValid(false);
                }
            }

            //All the Thematic area of the achievement match with any the Thematic area (or their children) specified in the accreditation
            logger.info("[{}] Checking Limit field IDs vs Learning Specification Thematic Area IDs of [{}]", () -> credentialID, () -> learningAchievementDTO.getName());
            if (!accreditationDTO.getLimitField().isEmpty() && !checkISCEDFCode(learningAchievementDTO.getSpecifiedBy(), accreditationDTO, lang)) {
                logger.info("[{}] Check IS INVALID for Limit Field IDs vs Learning Specification Thematic Area IDs of [{}]", () -> credentialID, () -> learningAchievementDTO.getName());
                validationResult.addValidationError(EDCIMessageKeys.Acreditation.LIMIT_FIELD_NOT_COVER_THEMATIC_AREA, learningAchievementDTO.getSpecifiedBy().getName());
                validationResult.setValid(false);
            }

            //All the locations of the achievement match with any the jurisdictions (or their children) specified in the accreditation
            logger.info("[{}] Checking Limit Jurisdictions vs Awarded By locations of [{}]", () -> credentialID, () -> learningAchievementDTO.getName());
            if (!accreditationDTO.getLimitJurisdiction().isEmpty() && !checkJurisdiction(learningAchievementDTO.getAwardedBy(), accreditationDTO, lang)) {
                logger.info("[{}] Check IS INVALID for Limit Jurisdictions vs Awarded By locations of [{}]", () -> credentialID, () -> learningAchievementDTO.getName());
                validationResult.addValidationError(EDCIMessageKeys.Acreditation.LIMIT_JURISDICTION_NOT_COVER_AWARDEDBY, learningAchievementDTO.getName());
                validationResult.setValid(false);
            }

        }
        logger.info("[{}] All Checks are Valid ", () -> credentialID);
        return validationResult;
    }

    /**
     * Checks Awarding body Identifiers against the AccreditingAgent's Identifiers from Accreditation
     *
     * @param learningAchievementDTO the achievement with the Awarding body to be checked
     * @param accreditationDTO       the accreditation with AccreditingAgent
     * @return the true if covered
     */
    private boolean checkAwardingBodies(LearningAchievementDTO learningAchievementDTO, AccreditationDTO accreditationDTO) {
        boolean isCovered = false;

        for (AgentDTO awardingBody : learningAchievementDTO.getAwardedBy().getAwardingBody()) {
            //Any match between Identifier notations for an awarding body will consider that awarding body covered
            for (OrganisationDTO accOrganisation : accreditationDTO.getOrganisation()) {
                if (anyIdentifierMatch(awardingBody.getIdentifier(), accOrganisation.getIdentifier())) {
                    isCovered = true;
                }

                if (awardingBody instanceof OrganisationDTO) {
                    OrganisationDTO orgAwardingBody = (OrganisationDTO) awardingBody;
                    if (!isCovered && anyIdentifierMatch(orgAwardingBody.getTaxIdentifier(), accOrganisation.getTaxIdentifier())) {
                        isCovered = true;
                    }
                    if (!isCovered && anyIdentifierMatch(orgAwardingBody.getVatIdentifier(), accOrganisation.getVatIdentifier())) {
                        isCovered = true;
                    }
                    if (!isCovered && (orgAwardingBody.getRegistration() != null && accOrganisation.getRegistration() != null) &&
                            orgAwardingBody.getRegistration().getNotation().equals(accOrganisation.getRegistration().getNotation())) {
                        isCovered = true;
                    }
                    if (!isCovered && (orgAwardingBody.geteIDASIdentifier() != null && accOrganisation.geteIDASIdentifier() != null) &&
                            orgAwardingBody.geteIDASIdentifier().getNotation().equals(accOrganisation.geteIDASIdentifier().getNotation())) {
                        isCovered = true;
                    }

                }
            }
            //If one awarding Body is Covered by one Accreditation Organisation, credential is covered
            if (isCovered) break;
        }

        return isCovered;

    }

    /**
     * Checks Learning Specification identifiers vs the limitQualification field in the accreditation
     *
     * @param learningAchievementSpecificationDTO The learning Specification from the achivement
     * @param accreditationDTO                    the accreditation containing limitQualification
     * @return true if covered
     */
    protected boolean checkQualifications(LearningAchievementSpecificationDTO learningAchievementSpecificationDTO, AccreditationDTO accreditationDTO) {
        if (accreditationDTO.getLimitQualification() == null) {
            return true;
        }

        List<Identifier> learningSpecificationIdentifiers = learningAchievementSpecificationDTO.getIdentifier();
        List<Identifier> limitQualificationIdentifiers = accreditationDTO.getLimitQualification().getIdentifier();

        if (learningSpecificationIdentifiers == null || learningSpecificationIdentifiers.isEmpty() ||
                limitQualificationIdentifiers.isEmpty() ||
                anyIdentifierMatch(learningSpecificationIdentifiers, limitQualificationIdentifiers)) {
            return true;
        }

        return false;
    }

    /**
     * Checks EQF level from a Qualification against limitEQFLevel in the accreditation
     *
     * @param learningAchievementSpecificationDTO the Qualification to be checked
     * @param accreditationDTO                    the accreditation containing limitEQFLevel
     * @return true if covered
     */
    protected boolean checkEQFLevel(QualificationDTO learningAchievementSpecificationDTO, AccreditationDTO accreditationDTO) {

        if (learningAchievementSpecificationDTO.getEqfLevel() == null ||
                accreditationDTO.getLimitEQFLevel() == null ||
                anyIdMatch(Arrays.asList(learningAchievementSpecificationDTO.getEqfLevel()), accreditationDTO.getLimitEQFLevel())) {
            return true;
        }

        return false;
    }

    /**
     * Checks the thematic area of the learning specification against the Limit Field in the accreditation
     *
     * @param learningAchievementSpecificationDTO the learning achievement specification to be checked
     * @param accreditationDTO                    the accreditation containing the limit field
     * @param lang                                the language
     * @return true if covered
     */
    protected boolean checkISCEDFCode(LearningAchievementSpecificationDTO learningAchievementSpecificationDTO, AccreditationDTO accreditationDTO, String lang) {
        return this.checkTreeConcept(learningAchievementSpecificationDTO.getThematicArea(), accreditationDTO.getLimitField(), ControlledList.ISCED_F, lang);
    }

    /**
     * Checks the location of the Awarding Process' awarding bodies against the limitJurisdiction in the accreditation
     *
     * @param awardingProcessDTO The awarding process of the achievement to be checked
     * @param accreditationDTO   the accreditation containing the limitJurisdiction
     * @param lang               the language
     * @return true if covered
     */
    protected boolean checkJurisdiction(AwardingProcessDTO awardingProcessDTO, AccreditationDTO accreditationDTO, String lang) {
        List<ConceptDTO> credentialList = awardingProcessDTO.getAwardingBody().stream().flatMap(agentDTO -> agentDTO.getLocation().stream()
                .flatMap(locationDTO -> locationDTO.getSpatialCode().stream()))
                .collect(Collectors.toList());
        return this.checkTreeConcept(credentialList, accreditationDTO.getLimitJurisdiction(), ControlledList.ATU, lang);
    }

    /**
     * Check that a List of concepts is covered by at least one of the concepts in another list OR it's childs.
     *
     * @param credentialList    the Concept list to be checked
     * @param accreditationList the Concept list to be checked against
     * @param CL                the Controlled list where all concepts belong to
     * @param lang              the language
     * @return true if covered
     */
    protected boolean checkTreeConcept(List<ConceptDTO> credentialList, List<ConceptDTO> accreditationList, ControlledList CL, String lang) {
        if (credentialList == null || credentialList.isEmpty() || accreditationList == null || accreditationList.isEmpty()) {
            return true;
        }

        boolean isPresent;
        for (ConceptDTO credentialConcept : credentialList) {
            isPresent = false;
            for (ConceptDTO accreditationConcept : accreditationList) {
                List<ConceptDTO> childrenList = this.getControlledListCommonsService().searchConceptsTreeByUri(CL.getUrl(), accreditationConcept.getId().toString(), lang);
                if (childrenList.stream().anyMatch(conceptDTO -> conceptDTO.getId().toString().equals(credentialConcept.getId().toString()))) {
                    isPresent = true;
                    break;
                }
            }
            if (!isPresent) {
                return false;
            }
        }

        return true;
    }

    private boolean anyIdentifierMatch(List<? extends Identifier> identifiers, List<? extends Identifier> coveredIdentifiers) {
        logger.info("checking Identifiers [{}] against [{}]",
                () -> StringUtils.join(identifiers.stream().map(Identifier::getNotation).collect(Collectors.toList()), ","),
                () -> StringUtils.join(coveredIdentifiers.stream().map(Identifier::getNotation), ","));
        return identifiers.stream().anyMatch(
                identifier -> coveredIdentifiers.stream().anyMatch(coveredIdentifier -> coveredIdentifier.getNotation().equals(identifier.getNotation())));
    }

    private boolean anyIdMatch(List<ConceptDTO> concepts, List<ConceptDTO> coveredConcepts) {
        logger.info("checking ids [{}] against [{}]",
                () -> StringUtils.join(concepts.stream().map(conceptDTO -> conceptDTO.getId().toString()).collect(Collectors.toList()), ","),
                () -> StringUtils.join(coveredConcepts.stream().map(coveredConcept -> coveredConcept.getId().toString()).collect(Collectors.toList())));
        return concepts.stream().anyMatch(
                conceptDTO -> coveredConcepts.stream().anyMatch(coveredConcept -> coveredConcept.getId().equals(conceptDTO.getId())));
    }

    public CredentialUtil getCredentialUtil() {
        return credentialUtil;
    }

    public void setCredentialUtil(CredentialUtil credentialUtil) {
        this.credentialUtil = credentialUtil;
    }

    public ControlledListCommonsService getControlledListCommonsService() {
        return controlledListCommonsService;
    }

    public void setControlledListCommonsService(ControlledListCommonsService controlledListCommonsService) {
        this.controlledListCommonsService = controlledListCommonsService;
    }

    public QDRAccreditationExternalService getQdrAccreditationExternalService() {
        return qdrAccreditationExternalService;
    }

    public void setQdrAccreditationExternalService(QDRAccreditationExternalService qdrAccreditationExternalService) {
        this.qdrAccreditationExternalService = qdrAccreditationExternalService;
    }
}
