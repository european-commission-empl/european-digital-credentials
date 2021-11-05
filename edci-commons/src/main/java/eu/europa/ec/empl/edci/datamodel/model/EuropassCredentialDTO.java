package eu.europa.ec.empl.edci.datamodel.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import eu.europa.ec.empl.edci.annotation.EDCIIdentifier;
import eu.europa.ec.empl.edci.constants.EDCIConstants;
import eu.europa.ec.empl.edci.constants.EDCIMessageKeys;
import eu.europa.ec.empl.edci.datamodel.model.base.CredentialHolderDTO;
import eu.europa.ec.empl.edci.datamodel.model.base.VerifiableCredentialDTO;
import eu.europa.ec.empl.edci.datamodel.model.dataTypes.*;
import org.eclipse.persistence.oxm.annotations.XmlCDATA;
import org.eclipse.persistence.oxm.annotations.XmlPath;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


@XmlRootElement(name = "europassCredential")
@XmlType(propOrder = {"identifier", "type", "primaryLanguage", "availableLanguages", "issuanceDate", "issued", "expirationDate", "issuer", "title", "description", "credentialSubject", "subCredentialsXML", "learningSpecificationReferences", "learningOutcomeReferences", "learningActivitySpecificationReferences"
        , "assessmentSpecificationReferences", "entitlementSpecificationReferences", "learningOpportunityReferences", "organisationReferences", "accreditationReferences", "awardingProcessReferences", "scoringSchemeReferences", "display", "proof"})
@XmlAccessorType(XmlAccessType.FIELD)
@EDCIIdentifier(prefix = "urn:credential:")
public class EuropassCredentialDTO extends VerifiableCredentialDTO implements CredentialHolderDTO {

    @Valid
    private List<Identifier> identifier = new ArrayList<>(); //*
    @NotNull(message = EDCIMessageKeys.Validation.VALIDATION_CREDENTIAL_TYPE_NOTNULL)
    @Valid
    private Code type;
    @NotNull(message = EDCIMessageKeys.Validation.VALIDATION_CREDENTIAL_TITLE_NOTNULL)
    @Valid
    private Text title; //1
    @Valid
    private Note description; //0..1
    @XmlIDREF
    @Valid
    @NotNull(message = EDCIMessageKeys.Validation.VALIDATION_CREDENTIAL_ISSUER_NOTNULl)
    @XmlPath("cred:issuer/@idref")
    @XmlElement(namespace = EDCIConstants.NAMESPACE_CRED_URI)
    private OrganizationDTO issuer; //1
    @Valid
    @NotNull(message = EDCIMessageKeys.Validation.VALIDATION_CREDENTIAL_CREDENTIALSUBJECT_NOTNULL)
    private PersonDTO credentialSubject; //1
    @XmlTransient
    @Valid
    private List<EuropassCredentialDTO> subCredentials; //*
    //@XmlTransient
    @XmlElement(name = "proof")
    private String proof = ""; //1
    @XmlElement(name = "displayParameters")
    private DisplayParametersDTO display; //0..1
    /**
     * Fill the mediaObjects with the bytes from the credentials, then those would be added to subCreds string on parsing
     */
    @XmlTransient
    private List<DownloadableObject> contains = new ArrayList<>();

    //    @XmlJavaTypeAdapter(CDATAAdapter.class)
    @XmlCDATA
    @XmlElement(name = "contains")
    private List<String> subCredentialsXML;
    //OUT OF SCOPE (DELETED??)
    @XmlTransient
    @XmlElementWrapper
    @XmlElement(name = "Attachment")
    private List<MediaObject> attachmentList; //*
    @XmlTransient
    private String originalXML;

    @XmlElementWrapper()
    @XmlElements({
            @XmlElement(name = "learningSpecification", type = LearningSpecificationDTO.class),
            @XmlElement(name = "qualification", type = QualificationDTO.class)
    })
    private Set<LearningSpecificationDTO> learningSpecificationReferences = new HashSet<>();
    @XmlElementWrapper
    @XmlElement(name = "learningOutcome")
    private Set<LearningOutcomeDTO> learningOutcomeReferences = new HashSet<>();
    @XmlElementWrapper()
    @XmlElement(name = "learningActivitySpecification")
    private Set<LearningActivitySpecificationDTO> learningActivitySpecificationReferences = new HashSet<LearningActivitySpecificationDTO>();
    @XmlElementWrapper
    @XmlElement(name = "assessmentSpecification")
    private Set<AssessmentSpecificationDTO> assessmentSpecificationReferences = new HashSet<AssessmentSpecificationDTO>();
    @XmlElementWrapper
    @XmlElement(name = "entitlementSpecification")
    private Set<EntitlementSpecificationDTO> entitlementSpecificationReferences = new HashSet<EntitlementSpecificationDTO>();
    @XmlElementWrapper
    @XmlElement(name = "learningOpportunity")
    private Set<LearningOpportunityDTO> learningOpportunityReferences = new HashSet<LearningOpportunityDTO>();
    @XmlElementWrapper(name = "agentReferences")
    @XmlElement(name = "organization")
    private Set<OrganizationDTO> organisationReferences = new HashSet<OrganizationDTO>();
    @XmlElementWrapper(name = "awardingProcessReferences")
    @XmlElement(name = "awardingProcess")
    private Set<AwardingProcessDTO> awardingProcessReferences = new HashSet<AwardingProcessDTO>();
    @XmlElementWrapper
    @XmlElement(name = "accreditation")
    private Set<AccreditationDTO> accreditationReferences = new HashSet<AccreditationDTO>();
    @XmlElementWrapper()
    @XmlElement(name = "scoringScheme")
    private Set<ScoringSchemeDTO> scoringSchemeReferences = new HashSet<ScoringSchemeDTO>();
    @Valid
    private String primaryLanguage;
    @Valid
    @XmlElementWrapper
    @XmlElement(name = "language")
    private List<String> availableLanguages;

    @Override
    public String getIdentifiableName() {
        return this.getIdentifiableNameFromFieldList(this, "title", "description", "type", "credentialSubject", "id");
    }

    public EuropassCredentialDTO() {
        this.initIdentifiable();
    }

    public List<Identifier> getIdentifier() {
        return identifier;
    }

    public void setIdentifier(List<Identifier> identifier) {
        this.identifier = identifier;
    }

    @Override
    public Code getType() {
        return type;
    }

    public void setType(Code type) {
        this.type = type;
    }

    public Text getTitle() {
        return title;
    }

    public void setTitle(Text title) {
        this.title = title;
    }

    public Note getDescription() {
        return description;
    }

    public void setDescription(Note description) {
        this.description = description;
    }

    public OrganizationDTO getIssuer() {
        return issuer;
    }

    public void setIssuer(OrganizationDTO issuer) {
        this.issuer = issuer;
    }

    public PersonDTO getCredentialSubject() {
        return credentialSubject;
    }

    public void setCredentialSubject(PersonDTO credentialSubject) {
        this.credentialSubject = credentialSubject;
    }

    public List<EuropassCredentialDTO> getSubCredentials() {
        return subCredentials;
    }

    public void setSubCredentials(List<EuropassCredentialDTO> subCredentials) {
        this.subCredentials = subCredentials;
    }

    public List<DownloadableObject> getContains() {
        return contains;
    }

    public void setContains(List<DownloadableObject> contains) {
        this.contains = contains;
    }

    public List<String> getSubCredentialsXML() {
        return subCredentialsXML;
    }

    public void setSubCredentialsXML(List<String> subCredentialsXML) {
        this.subCredentialsXML = subCredentialsXML;
    }

    public String getProof() {
        return proof;
    }

    public void setProof(String proof) {
        this.proof = proof;
    }

    public DisplayParametersDTO getDisplay() {
        return display;
    }

    public void setDisplay(DisplayParametersDTO display) {
        this.display = display;
    }

    public List<MediaObject> getAttachmentList() {
        return attachmentList;
    }

    public void setAttachmentList(List<MediaObject> attachmentList) {
        this.attachmentList = attachmentList;
    }

    public String getOriginalXML() {
        return originalXML;
    }

    public void setOriginalXML(String originalXML) {
        this.originalXML = originalXML;
    }

    public Set<LearningSpecificationDTO> getLearningSpecificationReferences() {
        return learningSpecificationReferences;
    }

    public void setLearningSpecificationReferences(Set<LearningSpecificationDTO> learningSpecificationReferences) {
        this.learningSpecificationReferences = learningSpecificationReferences;
    }

    public Set<LearningOutcomeDTO> getLearningOutcomeReferences() {
        return learningOutcomeReferences;
    }

    public void setLearningOutcomeReferences(Set<LearningOutcomeDTO> learningOutcomeReferences) {
        this.learningOutcomeReferences = learningOutcomeReferences;
    }

    public Set<LearningActivitySpecificationDTO> getLearningActivitySpecificationReferences() {
        return learningActivitySpecificationReferences;
    }

    public void setLearningActivitySpecificationReferences(Set<LearningActivitySpecificationDTO> learningActivitySpecificationReferences) {
        this.learningActivitySpecificationReferences = learningActivitySpecificationReferences;
    }

    public Set<AssessmentSpecificationDTO> getAssessmentSpecificationReferences() {
        return assessmentSpecificationReferences;
    }

    public void setAssessmentSpecificationReferences(Set<AssessmentSpecificationDTO> assessmentSpecificationReferences) {
        this.assessmentSpecificationReferences = assessmentSpecificationReferences;
    }

    public Set<EntitlementSpecificationDTO> getEntitlementSpecificationReferences() {
        return entitlementSpecificationReferences;
    }

    public void setEntitlementSpecificationReferences(Set<EntitlementSpecificationDTO> entitlementSpecificationReferences) {
        this.entitlementSpecificationReferences = entitlementSpecificationReferences;
    }

    public Set<LearningOpportunityDTO> getLearningOpportunityReferences() {
        return learningOpportunityReferences;
    }

    public void setLearningOpportunityReferences(Set<LearningOpportunityDTO> learningOpportunityReferences) {
        this.learningOpportunityReferences = learningOpportunityReferences;
    }

    public Set<OrganizationDTO> getOrganisationReferences() {
        return organisationReferences;
    }

    public void setOrganisationReferences(Set<OrganizationDTO> organisationReferences) {
        this.organisationReferences = organisationReferences;
    }

    public Set<AwardingProcessDTO> getAwardingProcessReferences() {
        return awardingProcessReferences;
    }

    public void setAwardingProcessReferences(Set<AwardingProcessDTO> awardingProcessReferences) {
        this.awardingProcessReferences = awardingProcessReferences;
    }

    public Set<AccreditationDTO> getAccreditationReferences() {
        return accreditationReferences;
    }

    public void setAccreditationReferences(Set<AccreditationDTO> accreditationReferences) {
        this.accreditationReferences = accreditationReferences;
    }

    public Set<ScoringSchemeDTO> getScoringSchemeReferences() {
        return scoringSchemeReferences;
    }

    public void setScoringSchemeReferences(Set<ScoringSchemeDTO> scoringSchemeReferences) {
        this.scoringSchemeReferences = scoringSchemeReferences;
    }

    public String getPrimaryLanguage() {
        return primaryLanguage;
    }

    public void setPrimaryLanguage(String primaryLanguage) {
        this.primaryLanguage = primaryLanguage;
    }

    public List<String> getAvailableLanguages() {
        return availableLanguages;
    }

    public void setAvailableLanguages(List<String> availableLanguages) {
        this.availableLanguages = availableLanguages;
    }

    @Override
    @XmlTransient
    @JsonIgnore
    public EuropassCredentialDTO getCredential() {
        return this;
    }
}