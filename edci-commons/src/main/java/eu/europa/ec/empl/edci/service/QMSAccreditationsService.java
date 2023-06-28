package eu.europa.ec.empl.edci.service;

import eu.europa.ec.empl.edci.config.service.BaseConfigService;
import eu.europa.ec.empl.edci.constants.ControlledListConcept;
import eu.europa.ec.empl.edci.constants.EDCIConfig;
import eu.europa.ec.empl.edci.constants.EDCIMessageKeys;
import eu.europa.ec.empl.edci.constants.EDCIParameter;
import eu.europa.ec.empl.edci.datamodel.jsonld.model.*;
import eu.europa.ec.empl.edci.datamodel.jsonld.model.dataTypes.ConceptDTO;
import eu.europa.ec.empl.edci.datamodel.jsonld.model.dataTypes.Identifier;
import eu.europa.ec.empl.edci.datamodel.validation.ValidationError;
import eu.europa.ec.empl.edci.datamodel.validation.ValidationResult;
import eu.europa.ec.empl.edci.model.qmsaccreditation.QMSAccreditationDTO;
import eu.europa.ec.empl.edci.util.CredentialUtil;
import eu.europa.ec.empl.edci.util.EDCIRestRequestBuilder;
import eu.europa.ec.empl.edci.util.JsonLdUtil;
import eu.europa.ec.empl.edci.util.ReflectiveUtil;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class QMSAccreditationsService {

    private static final Logger logger = LogManager.getLogger(QMSAccreditationsService.class);

    @Autowired
    private BaseConfigService iConfigService;

    @Autowired
    private ReflectiveUtil reflectiveUtil;

    @Autowired
    private JsonLdUtil jsonLdUtil;

    @Autowired
    private ResourceLoader resourceLoader;

    @Autowired
    private CredentialUtil credentialUtil;

    //TODO -> RESTORE WITH  NEW DATAMODEL

    /**
     * Inspects a credential, fetching all Accreditation data and checking the coverage
     *
     * @return the validation result
     * @throws JAXBException If a parsing/mapping exception occurs
     * @throws IOException   If an error reading the bytes occurs
     */


    public ValidationResult isCoveredCredential(EuropeanDigitalCredentialDTO europeanDigitalCredentialDTO) {
        if (!this.getCredentialUtil().isAccreditedCredential(europeanDigitalCredentialDTO)) {
            return null;
        } else {
            ValidationResult validationResult = new ValidationResult();
            validationResult.setValid(true);
            logger.info("Checking coverage for credential {}/{}", () -> europeanDigitalCredentialDTO.getId().toString(), () -> europeanDigitalCredentialDTO.getName());
            ValidationResult generalChecks = this.doRunGeneralChecks(europeanDigitalCredentialDTO);
            logger.info("[{}] General accreditation check result: {} ", () -> europeanDigitalCredentialDTO.getId(), () -> generalChecks.isValid());
            if (!generalChecks.isValid()) {
                logger.info("[{}] General check is NOT VALID", () -> europeanDigitalCredentialDTO.getId());
                return generalChecks;
            } else {
                List<LearningAchievementDTO> achievements = this.getCredentialUtil().getClaimsOfClass(europeanDigitalCredentialDTO, LearningAchievementDTO.class);
                for (LearningAchievementDTO achievementDTO : achievements) {
                    Evidence accreditationEvidence = this.credentialUtil.getEvidenceByType(ControlledListConcept.EVIDENCE_TYPE_ACCREDITATION, europeanDigitalCredentialDTO.getEvidence());
                    ValidationResult achievementCheck = this.doRunAchievementChecks(accreditationEvidence != null ? accreditationEvidence.getAccreditation() : null, achievementDTO, europeanDigitalCredentialDTO.getId());
                    if (!achievementCheck.isValid()) {
                        logger.info("[{}] Check for Achievement {} is NOT VALID ", () -> europeanDigitalCredentialDTO.getId(), () -> achievementDTO.getName());
                        //ToDo -> localize?
                        return validationResult;
                    }
                }
            }
            logger.info("[{}] All Checks are Valid ", () -> europeanDigitalCredentialDTO.getId());
            return validationResult;
        }
    }


    private ValidationResult doRunAchievementChecks(AccreditationDTO accreditationDTO, LearningAchievementDTO learningAchievementDTO, URI credentialID) {
        ValidationResult validationResult = new ValidationResult();
        validationResult.setValid(true);

        List<Identifier> accreditingAgentIdentifiers = accreditationDTO.getAccreditingAgent().getAllAvailableIdentifiers();
        List<Identifier> awardingBodyIdentifiers = learningAchievementDTO.getAwardedBy().getAwardingBody().stream()
                .map(AgentDTO::getAllAvailableIdentifiers).collect(ArrayList::new, ArrayList::addAll, ArrayList::addAll);

        logger.info("[{}] Checking Accrediting Agent identifiers vs Awarding Body Identifiers of [{}]", () -> credentialID, () -> learningAchievementDTO.getName());
        if (!anyIdentifierMatch(accreditingAgentIdentifiers, awardingBodyIdentifiers)) {
            validationResult.setValid(false);
            logger.info("[{}] Check IS INVALID for Accrediting Agent identifiers vs Awarding Body Identifiers of [{}]", () -> credentialID, () -> learningAchievementDTO.getName());
            validationResult.addValidationError(new ValidationError(
                    EDCIMessageKeys.Acreditation.ACCREDITING_ORG_NO_COVER_AWARDING_BODY,
                    accreditationDTO.getAccreditingAgent(), learningAchievementDTO
            ));
            return validationResult;
        }

        List<Identifier> learningSpecificationIdentifiers = learningAchievementDTO.getSpecifiedBy().getAllAvailableIdentifiers();
        List<Identifier> limitQualificationIdentifiers = accreditationDTO.getLimitQualification().getAllAvailableIdentifiers();
        logger.info("[{}] Checking Limit Qualification identifiers vs Learning Specification Identifiers of [{}]", () -> credentialID, () -> learningAchievementDTO.getName());

        if (!anyIdentifierMatch(learningSpecificationIdentifiers, limitQualificationIdentifiers)) {
            validationResult.setValid(false);
            logger.info("[{}] Check IS INVALID for Limit Qualification identifiers vs Learning Specification Identifiers of [{}]", () -> credentialID, () -> learningAchievementDTO.getName());
            validationResult.addValidationError(new ValidationError(
                    EDCIMessageKeys.Acreditation.LIMIT_QUALIFICATION_NOT_COVER_LEARNING_SPECIFICATION,
                    accreditationDTO.getLimitQualification(), learningAchievementDTO
            ));
            return validationResult;
        }

        logger.info("[{}] Checking Limit field IDs vs Learning Specification Thematic Area IDs of [{}]", () -> credentialID, () -> learningAchievementDTO.getName());
        List<ConceptDTO> iscedfCodes = learningAchievementDTO.getSpecifiedBy().getThematicArea();
        List<ConceptDTO> limitFields = accreditationDTO.getLimitField();
        if (iscedfCodes != null && !iscedfCodes.isEmpty()) {
            if (!this.isAnyISCEDFCodeCovered(iscedfCodes, limitFields)) {
                logger.info("[{}] Check IS INVALID for Limit Field IDs vs Learning Specification Thematic Area IDs of [{}]", () -> credentialID, () -> learningAchievementDTO.getName());
                validationResult.setValid(false);
                validationResult.addValidationError(new ValidationError(
                        EDCIMessageKeys.Acreditation.ISCEDF_CODE_NOT_COVERED,
                        learningAchievementDTO
                ));
            }
        }

        if (learningAchievementDTO.getSpecifiedBy() instanceof QualificationDTO) {
            QualificationDTO qualificationDTO = (QualificationDTO) learningAchievementDTO.getSpecifiedBy();
            if (qualificationDTO.getEqfLevel() != null)
                logger.info("[{}] Achievement [{}] contains a Qualification and EQF level, performing Concept ID check", () -> credentialID, () -> learningAchievementDTO.getName());
            {
                if (!anyIdMatch(Arrays.asList(qualificationDTO.getEqfLevel()), accreditationDTO.getLimitEQFLevel())) {
                    validationResult.setValid(false);
                    logger.info("[{}] Check IS INVALID for LimitEQFLevel vs EQF Level of qualification in achievement [{}]", () -> credentialID, () -> learningAchievementDTO.getName());
                    validationResult.addValidationError(new ValidationError(
                            EDCIMessageKeys.Acreditation.EQF_LEVEL_NOT_COVERED,
                            learningAchievementDTO
                    ));
                    return validationResult;
                }
            }
        }
        return validationResult;
    }

    private boolean anyIdentifierMatch(List<Identifier> identifiers, List<Identifier> coveredIdentifiers) {
        logger.info("checking Identifiers [{}] against [{}]",
                () -> StringUtils.join(identifiers.stream().map(identifier -> identifier.getId().toString()).collect(Collectors.toList()), ","),
                () -> StringUtils.join(coveredIdentifiers.stream().map(coveredIdentifier -> coveredIdentifier.getId().toString()), ","));
        return identifiers.stream().anyMatch(
                identifier -> coveredIdentifiers.stream().anyMatch(coveredIdentifier -> coveredIdentifier.getId().equals(identifier.getId())));
    }

    private boolean anyIdMatch(List<ConceptDTO> concepts, List<ConceptDTO> coveredConcepts) {
        logger.info("checking ids [{}] against [{}]",
                () -> StringUtils.join(concepts.stream().map(conceptDTO -> conceptDTO.getId().toString()).collect(Collectors.toList()), ","),
                () -> StringUtils.join(coveredConcepts.stream().map(coveredConcept -> coveredConcept.getId().toString()).collect(Collectors.toList())));
        return concepts.stream().anyMatch(
                conceptDTO -> coveredConcepts.stream().anyMatch(coveredConcept -> coveredConcept.getId().equals(conceptDTO.getId())));
    }

    private ValidationResult doRunGeneralChecks(EuropeanDigitalCredentialDTO europeanDigitalCredentialDTO) {
        ValidationResult validationResult = new ValidationResult();
        validationResult.setValid(true);
        logger.info("[{}] Checking evidence and Accreditation existence", () -> europeanDigitalCredentialDTO.getId());
        Evidence accreditationEvidence = this.credentialUtil.getEvidenceByType(ControlledListConcept.EVIDENCE_TYPE_ACCREDITATION, europeanDigitalCredentialDTO.getEvidence());

        if (europeanDigitalCredentialDTO.getEvidence().isEmpty()
                || accreditationEvidence == null) {
            //If Accredited credential contains no accreditation, is  invalid
            logger.info("[{}] evidence or accreditation was not found, general check IS INVALID", () -> europeanDigitalCredentialDTO.getId());
            validationResult.setValid(false);
            validationResult.addValidationError(EDCIMessageKeys.Acreditation.ACCREDITED_CRED_NO_ACCREDITATION_FOUND);
            return validationResult;
        }
        logger.info("[{}] Checking dcType existence", () -> europeanDigitalCredentialDTO.getId());
        AccreditationDTO accreditationDTO = accreditationEvidence.getAccreditation();
        if (accreditationDTO.getDcType() == null) {
            //If Accreditation contains no dcType, is invalid
            logger.info("[{}] dcType was not found, general check IS INVALID", () -> europeanDigitalCredentialDTO.getId());
            validationResult.setValid(false);
            validationResult.addValidationError(EDCIMessageKeys.Acreditation.ACCREDITATION_TYPE_NOT_FOUND);
            return validationResult;
        }
        OrganisationDTO accreditingOrganisation = accreditationDTO.getAccreditingAgent();
        logger.info("[{}] Checking accrediting Organisation existence", () -> europeanDigitalCredentialDTO.getId());
        if (accreditingOrganisation == null) {
            //If no accrediting organisation found, is invalid
            logger.info("[{}] accrediting Organisation was not found, general check IS INVALID", () -> europeanDigitalCredentialDTO.getId());
            validationResult.setValid(false);
            validationResult.addValidationError(EDCIMessageKeys.Acreditation.ACCREDITING_AGENT_NOT_FOUND);
            return validationResult;
        }
        ZonedDateTime issuanceDate = europeanDigitalCredentialDTO.getIssuanceDate();
        logger.info("[{}] Checking accrediting issuance Date covered by Accreditation", () -> europeanDigitalCredentialDTO.getId());
        if (issuanceDate.isBefore(accreditationDTO.getDateIssued()) || issuanceDate.isAfter(accreditationDTO.getExpiryDate())) {
            //If issuanceDate of credential not covered by accreditation, is invalid
            logger.info("[{}] Issuance Date of Credential not Covered by the Accreditation, general check IS INVALID", () -> europeanDigitalCredentialDTO.getId());
            validationResult.setValid(false);
            validationResult.addValidationError(EDCIMessageKeys.Acreditation.CREDENTIAL_ISSUANCE_DATE_NOT_COVERED);
            return validationResult;
        }
        List<ConceptDTO> credentialProfiles = europeanDigitalCredentialDTO.getCredentialProfiles();
        List<ConceptDTO> accreditationProfiles = accreditationDTO.getLimitCredentialType();
        logger.info("[{}] Checking Limit Credential types vs Credential Profiles", () -> europeanDigitalCredentialDTO.getId());
        if (!anyIdMatch(credentialProfiles, accreditationProfiles)) {
            //If credential profile is not covered by acredditation limitCredentialTypes, is invalid
            logger.info("[{}] No Limit Credential Types covers any of the Credential Profiles", () -> europeanDigitalCredentialDTO.getId());
            validationResult.setValid(false);
            validationResult.addValidationError(EDCIMessageKeys.Acreditation.CREDENTIAL_PROFILE_NOT_COVERED);
        }
        return validationResult;
    }


    /**
     * Downloads an accreditation based on a ID, also adds the item to the cache named AC_Accreditation
     *
     * @param uri the ID of the accreditation
     * @return the QMSAccreditationDTO received from service.
     */

    @Cacheable("AC_Accreditation")
    public QMSAccreditationDTO getAccreditation(URI uri) {
        QMSAccreditationDTO qmsAccreditationDTO = null;
        if (uri.toString().equals("be844fe0-6e3a-11ec-90d6-0242ac120003")) {
            qmsAccreditationDTO = new QMSAccreditationDTO();//this.getMockedAccreditation();
        } else if (uri.toString().equals("https://data.deqar.eu/report/48198") || uri.toString().equals("2525224f-5edd-45e9-92a7-f06c87688c54")) {
            qmsAccreditationDTO = new QMSAccreditationDTO();//this.getMockedAccreditation2();
        } else {
            try {
                //TODO update with https://esco-qdr-dev-searchapi.cogni.zone/webjars/swagger-ui/index.html#/accreditation/getAccreditationById call
                String accreditationsURL = this.getiConfigService().getString(EDCIConfig.QMSAccreditation.QMS_ACCREDITATION_URi).replace(EDCIParameter.ACCREDITATION_URI, uri.toString());
                EDCIRestRequestBuilder edciRestRequestBuilder = new EDCIRestRequestBuilder(HttpMethod.GET, accreditationsURL);
                qmsAccreditationDTO = edciRestRequestBuilder.addHeaderRequestedWith()
                        .addHeaders(null, MediaType.APPLICATION_JSON)
                        .buildRequest(QMSAccreditationDTO.class)
                        .execute();
            } catch (Exception e) {
                logger.error(String.format("Could not download accreditation %s", uri.toString()), e);
            }
        }
        return qmsAccreditationDTO;
    }


    public QMSAccreditationDTO getMockedAccreditation() {
        QMSAccreditationDTO qmsAccreditationDTO = null;
        try {
            InputStream inputStream = new ClassPathResource("accreditations/MockedSampleAccreditation.json").getInputStream();
            String jsonString = IOUtils.toString(inputStream);
            qmsAccreditationDTO = this.getJsonLdUtil().unMarshall(jsonString, QMSAccreditationDTO.class);
        } catch (Exception e) {
            logger.info("error mocking acccreditation", e);
        }
        return qmsAccreditationDTO;
    }

    public QMSAccreditationDTO getMockedAccreditation2() {
        QMSAccreditationDTO qmsAccreditationDTO = null;
        try {
            InputStream inputStream = new ClassPathResource("accreditations/MockedSampleAccreditation_2.json").getInputStream();
            String jsonString = IOUtils.toString(inputStream);
            qmsAccreditationDTO = this.getJsonLdUtil().unMarshall(jsonString, QMSAccreditationDTO.class);
        } catch (Exception e) {
            logger.info("error mocking acccreditation", e);
        }
        return qmsAccreditationDTO;
    }


    /* Checks if an ISCEDF Code is covered by a coverage list
     *
     * @param covered  The code to be checked for coverage
     * @param coverage the coverage list
     * @return true if it is covered
     */
    private Boolean isAnyISCEDFCodeCovered(List<ConceptDTO> covered, List<ConceptDTO> coverage) {
        logger.info("[{}] Checking ANY ISCEDF Codes [{}] against coverage [{}]",
                () -> StringUtils.join(covered.stream().map(conceptDTO -> conceptDTO.getId().toString()).collect(Collectors.toList()), ","),
                () -> StringUtils.join(coverage.stream().map(conceptDTO -> conceptDTO.getId().toString()).collect(Collectors.toList()), ",")
        );
        return covered.stream().anyMatch(conceptDTO -> coverage.stream().anyMatch(coverageConcept -> this.isCoveredISCEDFCode(conceptDTO, coverageConcept)));
    }

    private Boolean areAllISCEFCodeCovered(List<ConceptDTO> covered, List<ConceptDTO> coverage) {
        logger.info("[{}] Checking ALL ISCEDF Codes [{}] against coverage [{}]",
                () -> StringUtils.join(covered.stream().map(conceptDTO -> conceptDTO.getId().toString()).collect(Collectors.toList()), ","),
                () -> StringUtils.join(coverage.stream().map(conceptDTO -> conceptDTO.getId().toString()).collect(Collectors.toList()), ",")
        );
        return covered.stream().noneMatch(conceptDTO -> coverage.stream().anyMatch(coverageDTO -> !this.isCoveredISCEDFCode(conceptDTO, coverageDTO)));
    }

    /**
     * Checks if an ISCDED-F Code is covered by a QMSCode limitField code
     *
     * @param covered  The code to be checked for coverage
     * @param coverage the coverage list
     * @return true if it is covered
     */
    private Boolean isCoveredISCEDFCode(ConceptDTO covered, ConceptDTO coverage) {
        boolean isCovered = true;
        String coveredSuffix = covered.getId().toString().substring(covered.getId().toString().lastIndexOf("/"));
        String coverageSuffix = coverage.getId().toString().substring(covered.getId().toString().lastIndexOf("/"));
        if (coverageSuffix.length() > coveredSuffix.length()) {
            isCovered = false;
        } else {
            isCovered = coveredSuffix.startsWith(coverageSuffix);
        }
        return isCovered;
    }

    public BaseConfigService getiConfigService() {
        return iConfigService;
    }

    public void setiConfigService(BaseConfigService iConfigService) {
        this.iConfigService = iConfigService;
    }

    public ReflectiveUtil getReflectiveUtil() {
        return reflectiveUtil;
    }

    public void setReflectiveUtil(ReflectiveUtil reflectiveUtil) {
        this.reflectiveUtil = reflectiveUtil;
    }

    public JsonLdUtil getJsonLdUtil() {
        return jsonLdUtil;
    }

    public void setJsonLdUtil(JsonLdUtil jsonLdUtil) {
        this.jsonLdUtil = jsonLdUtil;
    }

    public CredentialUtil getCredentialUtil() {
        return credentialUtil;
    }

    public void setCredentialUtil(CredentialUtil credentialUtil) {
        this.credentialUtil = credentialUtil;
    }
}
