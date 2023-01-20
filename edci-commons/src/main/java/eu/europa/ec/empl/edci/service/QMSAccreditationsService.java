package eu.europa.ec.empl.edci.service;

import com.google.gson.Gson;
import eu.europa.ec.empl.edci.config.service.IConfigService;
import eu.europa.ec.empl.edci.constants.EDCIConfig;
import eu.europa.ec.empl.edci.constants.EDCIMessageKeys;
import eu.europa.ec.empl.edci.constants.EDCIParameter;
import eu.europa.ec.empl.edci.datamodel.model.*;
import eu.europa.ec.empl.edci.datamodel.model.base.CredentialHolderDTO;
import eu.europa.ec.empl.edci.datamodel.model.dataTypes.Code;
import eu.europa.ec.empl.edci.datamodel.model.dataTypes.Identifier;
import eu.europa.ec.empl.edci.datamodel.validation.ValidationError;
import eu.europa.ec.empl.edci.datamodel.validation.ValidationResult;
import eu.europa.ec.empl.edci.mapper.accreditation.QMSAccreditationsMapper;
import eu.europa.ec.empl.edci.model.qmsaccreditation.QMSAccreditationDTO;
import eu.europa.ec.empl.edci.model.qmsaccreditation.QMSCodeDTO;
import eu.europa.ec.empl.edci.model.qmsaccreditation.QMSIdentifierDTO;
import eu.europa.ec.empl.edci.util.*;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import javax.annotation.Nullable;
import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class QMSAccreditationsService {

    private static final Logger logger = LogManager.getLogger(QMSAccreditationsService.class);

    @Autowired
    private IConfigService iConfigService;

    @Autowired
    private ReflectiveUtil reflectiveUtil;

    @Autowired
    private Validator validator;

    @Autowired
    private JsonUtil jsonUtil;

    @Autowired
    private QMSAccreditationsMapper qmsAccreditationsMapper;

    @Autowired
    private EDCICredentialModelUtil edciCredentialModelUtil;

    @Autowired
    private ResourceLoader resourceLoader;

    /**
     * Inspects a credential, fetching all Accreditation data and checking the coverage
     *
     * @param xmlBytes the bytes of the credential
     * @return the validation result
     * @throws JAXBException If a parsing/mapping exception occurs
     * @throws IOException   If an error reading the bytes occurs
     */

    public ValidationResult isCoveredCredential(byte[] xmlBytes) throws JAXBException, IOException {
        CredentialHolderDTO credentialHolderDTO = this.getEdciCredentialModelUtil().fromByteArray(xmlBytes);
        return this.isCoveredCredential(credentialHolderDTO);
    }

    /**
     * Inspects a credential, fetching all Accreditation data and checking the coverage
     *
     * @param credentialHolderDTO the CredentialHolder containing the credential
     * @return the validation result
     */
    public ValidationResult isCoveredCredential(CredentialHolderDTO credentialHolderDTO) {
        if (this.getValidator().isEmpty(credentialHolderDTO.getCredential())) {
            ValidationResult validationResult = new ValidationResult();
            validationResult.setValid(false);
            return validationResult;
        }
        return this.isCoveredCredential(credentialHolderDTO.getCredential());
    }

    /**
     * Inspects a credential, fetching all Accreditation data and checking the coverage
     *
     * @param europassCredentialDTO the credential containing the accreditations
     * @return the validation result, or null if no accreditations are found
     */
    public ValidationResult isCoveredCredential(EuropassCredentialDTO europassCredentialDTO) {
        logger.info("Start isCoveredCredential");
        logger.info("Checking coverage for credential {}/{}", () -> europassCredentialDTO.getId().toString(), () -> europassCredentialDTO.getIdentifiableName());
        List<AccreditationDTO> accreditationDTOS = this.getReflectiveUtil().getInnerObjectsOfType(AccreditationDTO.class, europassCredentialDTO);
        logger.info("[{}] Found {} accreditations", () -> europassCredentialDTO.getId().toString(), () -> accreditationDTOS.size());
        if (accreditationDTOS.isEmpty()) {
            //If no accreditations can be checked, return null
            logger.info("[{}] Skipping accreditation, no accreditations found. RESULT: SKIPPED", () -> europassCredentialDTO.getId().toString());
            return null;
        } else {
            ValidationResult validationResult = new ValidationResult();
            validationResult.setValid(true);
            //Check for the Achievements coverage
            ValidationResult achievementsCoverage = this.areAchievementsCovered(europassCredentialDTO);
            logger.info("[{}] First accreditation check result: {} ", () -> europassCredentialDTO.getId(), () -> achievementsCoverage != null ? achievementsCoverage.isValid() : null);
            if (achievementsCoverage == null || !achievementsCoverage.isValid()) {
                //If the achievements coverage is false, run Issuer and Awarding Body coverage
                if (achievementsCoverage != null) {
                    validationResult.addValidationErrors(achievementsCoverage.getValidationErrors());
                }
                ValidationResult organizationsCoverage = this.areIssuerAndAwardingBodiesCovered(europassCredentialDTO.getIssuer(), europassCredentialDTO.getIssuanceDate(), europassCredentialDTO.getCredentialSubject().getAchieved());
                logger.info("[{}] Second accreditation check result: {}", () -> europassCredentialDTO.getId().toString(), () -> organizationsCoverage != null ? organizationsCoverage.isValid() : null);
                //if second step is invalid, then coverage is invalid, if second step is null and achievements coverage is invalid, it also is invalid
                if ((organizationsCoverage != null && !organizationsCoverage.isValid()) || (organizationsCoverage == null && !achievementsCoverage.isValid())) {
                    //If both coverages are false, credential is not covered
                    if (organizationsCoverage != null) {
                        validationResult.addValidationErrors(organizationsCoverage.getValidationErrors());
                    }
                    validationResult.setValid(false);
                }
            }
            logger.info("[{}] End Checking coverage. Result : {}", () -> europassCredentialDTO.getId(), () -> validationResult.isValid());
            logger.info("End isCoveredCredential");
            return validationResult;
        }
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
            qmsAccreditationDTO = this.getMockedAccreditation();
        } else if (uri.toString().equals("https://data.deqar.eu/report/48198") || uri.toString().equals("2525224f-5edd-45e9-92a7-f06c87688c54")) {
            qmsAccreditationDTO = this.getMockedAccreditation2();
        } else {
            try {
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
            qmsAccreditationDTO = this.getJsonUtil().fromJSON(jsonString, QMSAccreditationDTO.class);
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
            qmsAccreditationDTO = this.getJsonUtil().fromJSON(jsonString, QMSAccreditationDTO.class);
        } catch (Exception e) {
            logger.info("error mocking acccreditation", e);
        }
        return qmsAccreditationDTO;
    }

    /**
     * Checks if all of the achievements of the credential have covered accreditations, one achievement that is not covered will result in the entire credential evaluated as non covered.
     * Also, if an achievement contains multiple accreditations, only one needs to be covered for the achivement to be covered.
     *
     * @param europassCredentialDTO the credential to be checked
     * @return Validation Result of the check
     */
    private ValidationResult areAchievementsCovered(EuropassCredentialDTO europassCredentialDTO) {
        logger.info("Start areAchievementsCovered");
        logger.info("[{}] Checking achievement (Coverage Check 1)", () -> europassCredentialDTO.getId().toString());
        ValidationResult areAchievementsCovered = new ValidationResult();
        areAchievementsCovered.setValid(true);
        if (this.getValidator().isEmpty(europassCredentialDTO)) return null;
        //Get all achievements with accreditations
        Set<LearningAchievementDTO> accreditedAchievements = this.getReflectiveUtil().getUniqueInnerObjectsOfType(LearningAchievementDTO.class, europassCredentialDTO)
                .stream().filter(achievementDTO -> achievementDTO.getSpecifiedBy() instanceof QualificationDTO && this.getValidator().notEmpty(((QualificationDTO) achievementDTO.getSpecifiedBy()).getHasAccreditation())).collect(Collectors.toSet());
        logger.info("[{}] Found {} accredited achievements", () -> europassCredentialDTO.getId().toString(), () -> accreditedAchievements.size());
        //If no Qualifications are found, then return null
        if (this.getValidator().isEmpty(accreditedAchievements)) {
            logger.info("[{}] No accredited achievements found, Result is null until second check", () -> europassCredentialDTO.getId().toString());
            return null;
        }
        //Check coverage for achievements
        for (LearningAchievementDTO achievementDTO : accreditedAchievements) {
            logger.info("[{}] Checking coverage for accredited achievement {}", () -> europassCredentialDTO.getId(), () -> achievementDTO.getIdentifiableName());
            ValidationResult isAchievementCovered = new ValidationResult();
            isAchievementCovered.setValid(false);
            for (AccreditationDTO accreditationDTO : ((QualificationDTO) achievementDTO.getSpecifiedBy()).getHasAccreditation()) {
                boolean isAccreditationCovered = this.isAchievementAccreditationCovered(accreditationDTO, achievementDTO, europassCredentialDTO);
                if (isAccreditationCovered) {
                    //If one accreditation is covered, then achievement is covered
                    logger.info("[{}] Found coverage for accredited achievement {} ", () -> europassCredentialDTO.getId(), () -> achievementDTO.getIdentifiableName());
                    isAchievementCovered.setValid(true);
                    break;
                } else {
                    //Add errors for non covered accreditations
                    logger.info("[{}] Accreditation [{}] is not covered by this credential", () -> europassCredentialDTO.getId(), () -> accreditationDTO.getIdentifiableName());
                    isAchievementCovered.setValid(false);
                    ValidationError validationError = new ValidationError();
                    validationError.setErrorKey(EDCIMessageKeys.Acreditation.INVALID_ACHIEVEMENT_ACCREDITATION_SPECIFIED);
                    validationError.addAffectedAsset(accreditationDTO);
                    validationError.addAffectedAsset(achievementDTO);
                    isAchievementCovered.addValidationError(validationError);
                }
            }
            if (!isAchievementCovered.isValid()) {
                //If one achievement is not covered, then the credential is not covered, add errors for non covered accreditations
                logger.info("[{}] achievement {} is not covered, first step result will be false ", () -> europassCredentialDTO.getId(), () -> achievementDTO.getIdentifiableName());
                areAchievementsCovered.setValid(false);
                areAchievementsCovered.setValidationErrors(isAchievementCovered.getValidationErrors());
                break;
            }
        }
        logger.info("End areAchievementsCovered");
        return areAchievementsCovered;
    }

    /**
     * Check if an accreditation within an achievement is covered
     *
     * @param accreditationDTO      the accreditation to be checked
     * @param achievementDTO        the achievement in which the accreditation resides
     * @param europassCredentialDTO the europass Credential in which the accreditation resides
     * @return
     */
    private boolean isAchievementAccreditationCovered(AccreditationDTO accreditationDTO, LearningAchievementDTO achievementDTO, EuropassCredentialDTO europassCredentialDTO) {
        boolean isCovered = true;
        logger.info("[{}] Checking coverage for accreditation {}/{}", () -> accreditationDTO.getId().toString(), () -> accreditationDTO.getId(), () -> accreditationDTO.getIdentifiableName());
        //Check accreditation Coverage
        QMSAccreditationDTO qmsAccreditationDTO = this.getAccreditation(accreditationDTO.getId());
        if (this.getValidator().isEmpty(qmsAccreditationDTO)) {
            logger.info("[{}] accreditation {}/{} could not be downloaded from service", () -> europassCredentialDTO.getId(), () -> accreditationDTO.getId(), () -> accreditationDTO.getIdentifiableName());
            return false;
        }
        logger.info("[{}] - Checking Identifier coverage for achievement {}", () -> europassCredentialDTO.getId(), () -> achievementDTO.getIdentifiableName());
        ValidationResult isAchievementCovered = this.isAchievementCoveredByAccreditationIdentifiers(achievementDTO, europassCredentialDTO.getIssuer(), qmsAccreditationDTO);
        //check that accreditation was valid at credential's issuance Date
        Date issuanceDate = europassCredentialDTO.getIssuanceDate();
        //If expiry Date is not
        boolean isAccreditationInTime = qmsAccreditationDTO.getExpiryDate() != null ? issuanceDate.before(qmsAccreditationDTO.getExpiryDate()) && issuanceDate.after(qmsAccreditationDTO.getIssuedDate()) : issuanceDate.after(qmsAccreditationDTO.getIssuedDate());
        //Check if it is within limit qualification
        boolean isWithinLimitQualification = this.isWithinLimitQualification(qmsAccreditationDTO, (QualificationDTO) achievementDTO.getSpecifiedBy());
        logger.info("[{}] First step results for accreditation {} in achievement {}: [isAchievementCovered: {}, isAccreditationInTime: {}, isWithinLimitQualification: {}]"
                , () -> europassCredentialDTO.getId(), () -> accreditationDTO.getId(), () -> achievementDTO.getIdentifiableName(), () -> isAchievementCovered.isValid(), () -> isAccreditationInTime, () -> isWithinLimitQualification);
        //If one of the achievements does not pass the coverage, then it is invalid, ALL achievements must pass the check
        if (!isAchievementCovered.isValid() || !isAccreditationInTime || !isWithinLimitQualification) {
            logger.info("[{}] First Step is not covered, accreditation {} failed coverage check", () -> europassCredentialDTO.getId(), () -> qmsAccreditationDTO.getId().toString());
            isCovered = false;

        }
        return isCovered;
    }

    /**
     * Checks if an accreditation covers an achievement, providing the issuer of the credential
     *
     * @param achievementDTO      the achievement to be checked
     * @param issuer              the issuer of the achievement
     * @param qmsAccreditationDTO the accreditation
     * @return Validation Result of the check
     */
    private ValidationResult isAchievementCoveredByAccreditationIdentifiers(LearningAchievementDTO achievementDTO, OrganizationDTO issuer, QMSAccreditationDTO qmsAccreditationDTO) {
        logger.info("Start doesAccreditationCoverAchievement");
        ValidationResult isCovered = new ValidationResult();
        //Only learningachievements with QualificationDTOs must be checked, others are assumed true
        if (this.getValidator().notEmpty(achievementDTO.getSpecifiedBy()) && achievementDTO.getSpecifiedBy() instanceof QualificationDTO) {
            QualificationDTO qualificationDTO = (QualificationDTO) achievementDTO.getSpecifiedBy();
            if (this.getValidator().notEmpty(qualificationDTO.getHasAccreditation())) {
                List<AccreditationDTO> accreditationDTOS = qualificationDTO.getHasAccreditation();
                //is achievement contains accreditation,check for coverage
                if (this.getValidator().notEmpty(accreditationDTOS)) {
                    logger.info("Checking coverage of achievement {} with accreditation {} and issuer {}", () -> achievementDTO.getId(), () -> achievementDTO.getIdentifiableName(), () -> issuer.getIdentifiableName());
                    //Get identifiers from the issuer organization
                    List<Identifier> issuerIdentifiers = issuer.getAllAvailableIdentifiers();
                    logger.info("Issuer identifiers: {}", () -> new Gson().toJson(issuerIdentifiers));
                    //Get identifiers from the achievement's awarding body
                    List<Identifier> achievementIdentifiers = new ArrayList<>();
                    if (this.getValidator().notEmpty(achievementDTO.getWasAwardedBy()) && this.getValidator().notEmpty(achievementDTO.getWasAwardedBy().getAwardingBody())) {
                        achievementIdentifiers = achievementDTO.getWasAwardedBy().getAwardingBody().getAllAvailableIdentifiers();
                        logger.info(String.format("Achievement identifiers: %s", new Gson().toJson(achievementIdentifiers)));
                    }
                    //Add all of the credential Identifiers
                    List<Identifier> credentialIdentifiers = new ArrayList<>();
                    credentialIdentifiers.addAll(issuerIdentifiers);
                    credentialIdentifiers.addAll(achievementIdentifiers);
                    //Get Accreditation Identifiers
                    List<QMSIdentifierDTO> accreditationIdentifiers = qmsAccreditationDTO.getAllAvailableIdentifiers();
                    logger.info(String.format("Accreditation Identifiers: %s", new Gson().toJson(accreditationIdentifiers)));
                    //check if any of the accreditation Identifiers matches any of the credential Identifiers, only ONE must pass the check
                    isCovered.setValid(accreditationIdentifiers.stream().anyMatch(accId -> credentialIdentifiers.stream().anyMatch(credId -> accId.getValue().toString().equals(credId.getContent()))));
                    logger.info("Accreditation {} in achievement {} coverage check result is {}", () -> qmsAccreditationDTO.getId(), () -> achievementDTO.getIdentifiableName(), () -> isCovered.isValid());
                }
            }
        }
        logger.info("End doesAccreditationCoverAchievement");
        return isCovered;
    }

    /**
     * Check is a qualificationDTO is within the limit qualification
     *
     * @param qmsAccreditationDTO the qmsAccreditation from service
     * @param qualificationDTO    the qualificationDTO
     * @return true if it is within limit qualification
     */
    private boolean isWithinLimitQualification(QMSAccreditationDTO qmsAccreditationDTO, QualificationDTO qualificationDTO) {
        boolean isWithinLimitQualification = false;
        if (qmsAccreditationDTO.getLimitQualification() != null) {
            isWithinLimitQualification = qmsAccreditationDTO.getLimitQualification().getAllAvailableIdentifiers().stream().anyMatch(accQ -> qualificationDTO.getAllAvailableIdentifiers().stream().anyMatch(credQ -> credQ.getContent().equals(accQ.getValue().toString())));
        } else {
            isWithinLimitQualification = true;
        }
        return isWithinLimitQualification;
    }

    /**
     * Check if the accreditation contained in a Organization is covered
     *
     * @param issuerDTO    the issuer of the credentials
     * @param issuanceDate the issuance date of the credential that contained the organization
     * @return Validation Result of the check
     */
    private ValidationResult areIssuerAndAwardingBodiesCovered(OrganizationDTO issuerDTO, Date issuanceDate, List<LearningAchievementDTO> topLevelAchievements) {
        logger.info("Start areIssuerAndAwardingBodiesCovered");
        ValidationResult validationResult = new ValidationResult();
        validationResult.setValid(true);
        logger.info("Checking {} top level achevements and issuer {} ", () -> topLevelAchievements.size(), () -> issuerDTO.getIdentifiableName());
        //filter achievements with awarding bodies and Accreditations
        List<LearningAchievementDTO> awardedAchievements = topLevelAchievements.stream().filter(achievement ->
                this.getValidator().notEmpty(achievement.getWasAwardedBy()) &&
                        this.getValidator().notEmpty(achievement.getWasAwardedBy().getAwardingBody()) &&
                        this.getValidator().notEmpty(achievement.getWasAwardedBy().getAwardingBody().getHasAccreditation())).collect(Collectors.toList());
        logger.info("found {} accredited achievements", () -> awardedAchievements.size());
        //Check issuer accreditations
        ValidationResult issuerCoverage = this.isAccreditedIssuerOrAwardingBodyCovered(issuerDTO, null, issuanceDate);
        if (issuerCoverage != null && !issuerCoverage.isValid()) {
            //If the issuer coverage is not valid, then check is false
            logger.info("issuer [{}] is not covered, second step is false", () -> issuerDTO.getIdentifiableName());
            validationResult.setValid(false);
            validationResult.addValidationErrors(issuerCoverage.getValidationErrors());
        } else if (issuerCoverage == null && awardedAchievements.isEmpty()) {
            //If no issuer coverage is found and awarded achievements is empty, return null
            return null;
        }
        //If one of the achievements is not covered, coverage is failed
        for (LearningAchievementDTO awardedAchievement : awardedAchievements) {
            ValidationResult awardedAchievementCoverage = this.isAccreditedIssuerOrAwardingBodyCovered(issuerDTO, awardedAchievement, issuanceDate);
            if (!awardedAchievementCoverage.isValid()) {
                logger.info("Top level awarded achievement [{}] is not covered, second step is false", () -> awardedAchievement.getIdentifiableName());
                validationResult.setValid(false);
                validationResult.addValidationErrors(awardedAchievementCoverage.getValidationErrors());
                logger.info("Second step: issuer {} does not cover top level achievements", () -> issuerDTO.getIdentifiableName());
            }
        }
        logger.info("End areIssuerAndAwardingBodiesCovered");
        return validationResult;
    }

    /**
     * Check if an issuer and awarded top level achievements cover their accreditations
     *
     * @param issuerDTO                the issuer of the credential
     * @param awardedTopLvlAchievement the top level achievements of the credentials, or null if it does not have
     * @param issuanceDate             the issuance date of the credential
     * @return Validation Result of the check
     */
    private ValidationResult isAccreditedIssuerOrAwardingBodyCovered(OrganizationDTO issuerDTO, @Nullable LearningAchievementDTO awardedTopLvlAchievement, Date issuanceDate) {
        logger.info("Start isIssuerOrAwardingBodyAccreditationCovered");
        List<AccreditationDTO> accreditationDTOS = new ArrayList<>();
        List<AccreditationDTO> issuerAccreditations = issuerDTO.getHasAccreditation();
        OrganizationDTO awardingBody = null;
        List<Code> iscefCodes = null;
        Code eqfCode = null;
        //If achievement is included, add awarding body accreditations, iscefCodes and the awarding body itself
        if (this.getValidator().notEmpty(awardedTopLvlAchievement)) {
            List<AccreditationDTO> awardingBodiesAccreditations = awardedTopLvlAchievement.getWasAwardedBy().getAwardingBody().getHasAccreditation();
            if (this.getValidator().notEmpty(awardingBodiesAccreditations)) {
                accreditationDTOS.addAll(awardingBodiesAccreditations);
            }
            awardingBody = awardedTopLvlAchievement.getWasAwardedBy().getAwardingBody();
            iscefCodes = awardedTopLvlAchievement.getSpecifiedBy().getIscedFCode();
            eqfCode = awardedTopLvlAchievement.getSpecifiedBy() instanceof QualificationDTO ? ((QualificationDTO) awardedTopLvlAchievement.getSpecifiedBy()).getEqfLevel() : null;
            logger.info(String.format("Found awarded achievement [%s], with awarding body [%s], iscefCodes [%s], eqfCode [%s]"
                    , awardedTopLvlAchievement.getIdentifiableName(), awardingBody.getIdentifiableName(), iscefCodes, eqfCode));
        }
        if (this.getValidator().notEmpty(issuerAccreditations)) {
            accreditationDTOS.addAll(issuerAccreditations);
        }
        ValidationResult isAccredited = new ValidationResult();
        //If no accreditations are present at this point, return null
        if (this.getValidator().isEmpty(accreditationDTOS)) {
            String achievementName = awardedTopLvlAchievement != null ? awardedTopLvlAchievement.getIdentifiableName() : null;
            logger.info("No accreditations were found for issuer [{}] and awarded achievement [{}] second step is null", () -> issuerDTO.getIdentifiableName(), () -> achievementName);
            return null;
        }
        //If one of the accreditations is covered, then all acreditations are covered
        String achievementName = awardedTopLvlAchievement != null ? awardedTopLvlAchievement.getIdentifiableName() : null;
        for (AccreditationDTO accreditationDTO : accreditationDTOS) {
            if (this.isIssuerOrAwardingBodyAccreditationCovered(issuerDTO, awardingBody, eqfCode, iscefCodes, issuanceDate, accreditationDTO)) {
                //If one Accreditation is covered, then the accredited issuer/awardingBody is covered
                logger.info("Found accredited accreditation for issuer [{}] and achievement [{}]", () -> issuerDTO.getIdentifiableName(), () -> achievementName);
                isAccredited.setValid(true);
            } else {
                //If a acreditation is not covered, add to error list
                logger.info("Accreditation [{}] is not covered for issuer [{}] and achievement [{}]", () -> accreditationDTO.getIdentifiableName(), () -> issuerDTO.getIdentifiableName(), () -> achievementName);
                isAccredited.setValid(false);
                ValidationError validationError = new ValidationError();
                validationError.setErrorKey(EDCIMessageKeys.Acreditation.INVALID_ORGANIZATIONAL_ACCREDITATION_SPECIFIED);
                validationError.addAffectedAsset(accreditationDTO);
                if (this.getValidator().notEmpty(awardingBody)) {
                    validationError.addAffectedAsset(awardingBody);
                } else {
                    validationError.addAffectedAsset(issuerDTO);
                }
                isAccredited.addValidationError(validationError);
            }
        }
        logger.info("End isIssuerOrAwardingBodyAccreditationCovered");
        return isAccredited;
    }

    /**
     * Check if an accreditation is covered either by the issuer or the awarding body of the achievement in which the accreditation resides
     *
     * @param issuerDTO        the issuer of the credential
     * @param awardingBody     the awarding body of the achievement that contains the accreditation
     * @param eqfCode          the eqf of the achievement that contains the accreditation
     * @param iscedfCodes      the iscedfCodes of the achievement that contains the accreditation
     * @param issuanceDate     the issuance date of the credential
     * @param accreditationDTO the accreditation to be checked
     * @return true if it is covered
     */
    private Boolean isIssuerOrAwardingBodyAccreditationCovered(OrganizationDTO issuerDTO, OrganizationDTO awardingBody, Code eqfCode, List<Code> iscedfCodes, Date issuanceDate, AccreditationDTO accreditationDTO) {
        boolean isCovered = false;
        QMSAccreditationDTO qmsAccreditationDTO = this.getAccreditation(accreditationDTO.getId());
        if (this.getValidator().notEmpty(accreditationDTO) && this.getValidator().notEmpty(qmsAccreditationDTO)) {
            String awardingBodyName = awardingBody != null ? awardingBody.getIdentifiableName() : null;
            logger.info("checking accreditations {}/{} for issuer {} and awardingBody {}", () -> accreditationDTO.getId(), () -> accreditationDTO.getIdentifiableName(), () -> issuerDTO.getIdentifiableName(), () -> awardingBodyName);
            boolean isIdentifierCovered = this.isIssuerOrAwardingBodyCoveredByAccreditationIdentifiers(qmsAccreditationDTO, issuerDTO, awardingBody);
            //Check that the accreditation still applies (credential issued after accreditation issuance date and before accreditation expiry date)
            boolean isInTime = qmsAccreditationDTO.getExpiryDate() != null ? issuanceDate.after(qmsAccreditationDTO.getIssuedDate()) && issuanceDate.before(qmsAccreditationDTO.getExpiryDate()) : issuanceDate.after(qmsAccreditationDTO.getIssuedDate());
            //Check that any of the ISCEDF codes of the achievement is covered by any of the limitFields in accreditation
            boolean isInsideISCEDF = this.isAnyISCEDFCodeCovered(iscedfCodes, qmsAccreditationDTO.getLimitFields());
            boolean isWithinLimitEQF = isWithinLimitEQF(eqfCode, qmsAccreditationDTO.getLimitEQFLevels());
            logger.info("Second step for issuer {}, results: isIdentifierCovered {}, isInsideISCEDF {}, isWithinLimitEQF {}", () -> issuerDTO.getIdentifiableName(), () -> isInTime, () -> isInsideISCEDF, () -> isWithinLimitEQF);
            if (isIdentifierCovered && isInTime && isInsideISCEDF && isWithinLimitEQF) {
                isCovered = true;
            }
        }
        return isCovered;
    }

    /**
     * Check if a EQF code is covered by the list of the accreditation service response
     *
     * @param coveredEqf the eqf to be checked
     * @param coverage   the coverage from the accreditation service response ("limitEQF" field)
     * @return true if it is covered
     */
    private Boolean isWithinLimitEQF(Code coveredEqf, List<QMSCodeDTO> coverage) {
        if (this.getValidator().isEmpty(coveredEqf)) return true;
        return coverage.stream().anyMatch(coverageItem -> coverageItem.getUri().equals(coveredEqf.getUri()));
    }

    /**
     * Checks if the identifiers of an accreditation is covered by issuer or awardingBody identifiers
     *
     * @param qmsAccreditationDTO the accreditation from service
     * @param issuerDTO           the issuer of the credential
     * @param awardingBody        the awarding body that awarded the accreditation
     * @return true if it is covered
     */
    private boolean isIssuerOrAwardingBodyCoveredByAccreditationIdentifiers(QMSAccreditationDTO qmsAccreditationDTO, OrganizationDTO issuerDTO, OrganizationDTO awardingBody) {
        List<QMSIdentifierDTO> accreditationIdentifiers = qmsAccreditationDTO.getAllAvailableIdentifiers();
        List<Identifier> organizationalIdentifiers = issuerDTO.getAllAvailableIdentifiers();
        //Include the awarding body identifiers for achievement validations
        if (this.getValidator().notEmpty(awardingBody)) {
            organizationalIdentifiers.addAll(awardingBody.getAllAvailableIdentifiers());
        }
        logger.info("organizational identifiers : %s", () -> new Gson().toJson(organizationalIdentifiers));
        logger.info("accreditation identifiers : %s", () -> new Gson().toJson(accreditationIdentifiers));
        //Check that any of the identifiers in the issuer OR awarding body matches any Accreditation identifier
        return accreditationIdentifiers.stream().anyMatch(accId -> organizationalIdentifiers.stream().anyMatch(issId -> accId.getValue().toString().equals(issId.getContent())));
    }

    /**
     * Checks if any of the ISCEDF Codes is covered by a coverage list
     *
     * @param covered  The list of ISCEDF Codes to be checked for coverage
     * @param coverage the coverage list
     * @return true if it is covered
     */
    private Boolean isAnyISCEDFCodeCovered(List<Code> covered, List<QMSCodeDTO> coverage) {
        if (this.getValidator().isEmpty(covered)) return true;
        return covered.stream().anyMatch(coveredItem -> this.isCoveredISCEDFCode(coveredItem, coverage));
    }

    /**
     * Checks if an ISCEDF Code is covered by a coverage list
     *
     * @param covered  The code to be checked for coverage
     * @param coverage the coverage list
     * @return true if it is covered
     */
    private Boolean isCoveredISCEDFCode(Code covered, List<QMSCodeDTO> coverage) {
        if (this.getValidator().isEmpty(coverage)) return true;
        return coverage.stream().anyMatch(coverageItem -> this.isCoveredISCEDFCode(covered, coverageItem));
    }

    /**
     * Checks if an ISCDED-F Code is covered by a QMSCode limitField code
     *
     * @param covered  The code to be checked for coverage
     * @param coverage the coverage list
     * @return true if it is covered
     */
    private Boolean isCoveredISCEDFCode(Code covered, QMSCodeDTO coverage) {
        boolean isCovered = true;
        if (this.getValidator().notEmpty(covered) && this.getValidator().notEmpty(coverage)) {
            String coveredSuffix = covered.getUri().substring(covered.getUri().lastIndexOf("/"));
            String coverageSuffix = coverage.getUri().substring(covered.getUri().lastIndexOf("/"));
            if (coverageSuffix.length() > coveredSuffix.length()) {
                isCovered = false;
            } else {
                isCovered = coveredSuffix.startsWith(coverageSuffix);
            }
        }
        return isCovered;
    }


    public IConfigService getiConfigService() {
        return iConfigService;
    }

    public void setiConfigService(IConfigService iConfigService) {
        this.iConfigService = iConfigService;
    }

    public JsonUtil getJsonUtil() {
        return jsonUtil;
    }

    public void setJsonUtil(JsonUtil jsonUtil) {
        this.jsonUtil = jsonUtil;
    }

    public ReflectiveUtil getReflectiveUtil() {
        return reflectiveUtil;
    }

    public void setReflectiveUtil(ReflectiveUtil reflectiveUtil) {
        this.reflectiveUtil = reflectiveUtil;
    }

    public Validator getValidator() {
        return validator;
    }

    public void setValidator(Validator validator) {
        this.validator = validator;
    }

    public QMSAccreditationsMapper getQmsAccreditationsMapper() {
        return qmsAccreditationsMapper;
    }

    public void setQmsAccreditationsMapper(QMSAccreditationsMapper qmsAccreditationsMapper) {
        this.qmsAccreditationsMapper = qmsAccreditationsMapper;
    }

    public EDCICredentialModelUtil getEdciCredentialModelUtil() {
        return edciCredentialModelUtil;
    }

    public void setEdciCredentialModelUtil(EDCICredentialModelUtil edciCredentialModelUtil) {
        this.edciCredentialModelUtil = edciCredentialModelUtil;
    }
}
