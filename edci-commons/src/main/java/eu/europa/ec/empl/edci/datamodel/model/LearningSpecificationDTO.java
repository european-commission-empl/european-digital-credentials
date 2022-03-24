package eu.europa.ec.empl.edci.datamodel.model;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import eu.europa.ec.empl.edci.annotation.EDCIIdentifier;
import eu.europa.ec.empl.edci.constants.EDCIMessageKeys;
import eu.europa.ec.empl.edci.datamodel.adapter.DurationAdapter;
import eu.europa.ec.empl.edci.datamodel.model.base.Identifiable;
import eu.europa.ec.empl.edci.datamodel.model.base.Nameable;
import eu.europa.ec.empl.edci.datamodel.model.dataTypes.*;
import org.eclipse.persistence.oxm.annotations.XmlIDExtension;
import org.eclipse.persistence.oxm.annotations.XmlPath;
import org.joda.time.Period;
import org.springframework.util.StringUtils;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.net.URI;
import java.util.List;

@XmlRootElement(name = "learningSpecification")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlSeeAlso({QualificationDTO.class})
@XmlType(name = "learningSpecificationV2", propOrder = {"identifier", "learningOpportunityType", "title", "alternativeLabel", "definition", "learningOutcomeDescription"
        , "additionalNote", "homePage", "supplementaryDocument", "iscedFCode", "educationSubject", "volumeOfLearning", "ectsCreditPoints", "creditPoints", "educationLevel"
        , "language", "mode", "learningSetting", "maximumDuration", "targetGroup", "entryRequirementNote", "learningOutcome", "awardingOpportunity"})
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY)
@JsonSubTypes(
        {
                @JsonSubTypes.Type(value = QualificationDTO.class, name = "QualificationDTO")
        }
)
@EDCIIdentifier(prefix = "urn:epass:learningSpecification:")
public class LearningSpecificationDTO implements Identifiable, Nameable {
    @XmlID
    @XmlIDExtension
    @XmlAttribute
    @NotNull(message = EDCIMessageKeys.Validation.VALIDATION_LEARNINGSPEC_ID_NOTNULL)
    private URI id; //1
    @XmlTransient
    private String pk;
    @Valid
    private List<Identifier> identifier; //*
    @Valid
    @XmlElement(name = "type")
    private List<Code> learningOpportunityType; //*
    @Valid
//    @NotNull(message = MessageKeys.Validation.VALIDATION_LEARNINGSPEC_TITLE_NOTNULL)
    private Text title; //0..1
    @Valid
    @XmlElement(name = "altLabel")
    private List<Text> alternativeLabel; //*
    @Valid
    private Note definition; //0..1
    @Valid
    @XmlElement(name = "description")
    private Note learningOutcomeDescription;  //0..1
    @Valid
    private List<Note> additionalNote; //*
    @Valid
    @XmlElement(name = "homepage")
    private List<WebDocumentDTO> homePage; //*
    @Valid
    @XmlElement(name = "supplementaryDoc")
    private List<WebDocumentDTO> supplementaryDocument; //*
    @Valid
    @XmlElement(name = "hasISCED-FCode")
    private List<Code> iscedFCode;
    @Valid
    @XmlElement(name = "hasEducationSubject")
    private List<Code> educationSubject;
    @Valid
    @XmlJavaTypeAdapter(DurationAdapter.class)
    private Period volumeOfLearning;
    @Valid
    @XmlElement(name = "hasECTSCreditPoints")
    private Score ectsCreditPoints;
    @Valid
    @XmlElement(name = "hasCreditPoints")
    private List<Score> creditPoints;
    @Valid
    @XmlElement(name = "hasEducationLevel")
    private List<Code> educationLevel;
    @Valid
    private List<Code> language; //*
    @Valid
    private List<Code> mode; //*
    @Valid
    @XmlElement(name = "learningSettingType")
    private Code learningSetting; //0..1
    @XmlElement(name = "duration")
    @XmlJavaTypeAdapter(DurationAdapter.class)
    private Period maximumDuration; //0..1
    @Valid
    private List<Code> targetGroup; //*
    @Valid
    @XmlElement(name = "entryRequirementsNote")
    private Note entryRequirementNote; //0..1
    @Valid
    @XmlElementWrapper(name = "learningOutcomes")
    @XmlIDREF
    @XmlPath("learningOutcomes/learningOutcome/@idref")
    private List<LearningOutcomeDTO> learningOutcome;

    @XmlTransient
    private LearningActivitySpecificationDTO learningActivitySpecification; //0..1
    @XmlTransient
    private AssessmentSpecificationDTO assessmentSpecification; //0..1
    @XmlTransient
    private List<EntitlementSpecificationDTO> entitlementSpecification;
    @XmlElementWrapper(name = "awardingOpportunities")
    private List<AwardingOpportunityDTO> awardingOpportunity;
    @XmlIDREF
    @XmlPath("specializationOf/@idref")
    private List<LearningSpecificationDTO> specializationOf; //*

    public LearningSpecificationDTO() {
        this.initIdentifiable();
    }

    @Override
    public String getIdentifiableName() {
        return this.getIdentifiableNameFromFieldList(this, "title", "alternativeLabel", "identifier", "definition", "additionalNote", "id");
    }

    @Override
    public void setPk(String pk) {
        this.pk = pk;
    }

    public URI getId() {
        return id;
    }

    public void setId(URI id) {
        this.id = id;
    }

    public List<Identifier> getIdentifier() {
        return identifier;
    }

    public void setIdentifier(List<Identifier> identifier) {
        this.identifier = identifier;
    }

    public void parseVolumeOfLearning(Object hours) {
        if (!StringUtils.isEmpty(hours)) {
            volumeOfLearning = Period.parse("PT" + Double.valueOf(hours.toString()).intValue() + "H");
        }
    }

    public void parseMaximumDuration(Object months) {
        if (!StringUtils.isEmpty(months)) {
            maximumDuration = Period.parse("P" + Double.valueOf(months.toString()).intValue() + "M");
        }
    }

    public List<Code> getLearningOpportunityType() {
        return learningOpportunityType;
    }

    public void setLearningOpportunityType(List<Code> learningOpportunityType) {
        this.learningOpportunityType = learningOpportunityType;
    }

    public Text getTitle() {
        return title;
    }

    public void setTitle(Text title) {
        this.title = title;
    }

    public List<Text> getAlternativeLabel() {
        return alternativeLabel;
    }

    public void setAlternativeLabel(List<Text> alternativeLabel) {
        this.alternativeLabel = alternativeLabel;
    }

    public Note getDefinition() {
        return definition;
    }

    public void setDefinition(Note definition) {
        this.definition = definition;
    }

    public Note getLearningOutcomeDescription() {
        return learningOutcomeDescription;
    }

    public void setLearningOutcomeDescription(Note learningOutcomeDescription) {
        this.learningOutcomeDescription = learningOutcomeDescription;
    }

    public List<Note> getAdditionalNote() {
        return additionalNote;
    }

    public void setAdditionalNote(List<Note> additionalNote) {
        this.additionalNote = additionalNote;
    }

    public List<WebDocumentDTO> getHomePage() {
        return homePage;
    }

    public void setHomePage(List<WebDocumentDTO> homePage) {
        this.homePage = homePage;
    }

    public List<WebDocumentDTO> getSupplementaryDocument() {
        return supplementaryDocument;
    }

    public void setSupplementaryDocument(List<WebDocumentDTO> supplementaryDocument) {
        this.supplementaryDocument = supplementaryDocument;
    }

    public List<Code> getIscedFCode() {
        return iscedFCode;
    }

    public void setIscedFCode(List<Code> iscedFCode) {
        this.iscedFCode = iscedFCode;
    }

    public List<Code> getEducationSubject() {
        return educationSubject;
    }

    public void setEducationSubject(List<Code> educationSubject) {
        this.educationSubject = educationSubject;
    }

    public Period getVolumeOfLearning() {
        return volumeOfLearning;
    }

    public void setVolumeOfLearning(Period volumeOfLearning) {
        this.volumeOfLearning = volumeOfLearning;
    }

    public Score getEctsCreditPoints() {
        return ectsCreditPoints;
    }

    public void setEctsCreditPoints(Score ectsCreditPoints) {
        this.ectsCreditPoints = ectsCreditPoints;
    }

    public List<Score> getCreditPoints() {
        return creditPoints;
    }

    public void setCreditPoints(List<Score> creditPoints) {
        this.creditPoints = creditPoints;
    }

    public List<Code> getEducationLevel() {
        return educationLevel;
    }

    public void setEducationLevel(List<Code> educationLevel) {
        this.educationLevel = educationLevel;
    }

    public List<Code> getLanguage() {
        return language;
    }

    public void setLanguage(List<Code> language) {
        this.language = language;
    }

    public List<Code> getMode() {
        return mode;
    }

    public void setMode(List<Code> mode) {
        this.mode = mode;
    }

    public Code getLearningSetting() {
        return learningSetting;
    }

    public void setLearningSetting(Code learningSetting) {
        this.learningSetting = learningSetting;
    }

    public Period getMaximumDuration() {
        return maximumDuration;
    }

    public void setMaximumDuration(Period maximumDuration) {
        this.maximumDuration = maximumDuration;
    }

    public List<Code> getTargetGroup() {
        return targetGroup;
    }

    public void setTargetGroup(List<Code> targetGroup) {
        this.targetGroup = targetGroup;
    }

    public Note getEntryRequirementNote() {
        return entryRequirementNote;
    }

    public void setEntryRequirementNote(Note entryRequirementNote) {
        this.entryRequirementNote = entryRequirementNote;
    }

    public List<LearningOutcomeDTO> getLearningOutcome() {
        return learningOutcome;
    }

    public void setLearningOutcome(List<LearningOutcomeDTO> learningOutcome) {
        this.learningOutcome = learningOutcome;
    }

    public List<AwardingOpportunityDTO> getAwardingOpportunity() {
        return awardingOpportunity;
    }

    public void setAwardingOpportunity(List<AwardingOpportunityDTO> awardingOpportunity) {
        this.awardingOpportunity = awardingOpportunity;
    }

    public List<LearningSpecificationDTO> getSpecializationOf() {
        return specializationOf;
    }

    public void setSpecializationOf(List<LearningSpecificationDTO> specializationOf) {
        this.specializationOf = specializationOf;
    }

    @Override
    public int hashCode() {
        return this.getHashCode();
    }

    @Override
    public boolean equals(Object object) {
        return this.isEquals(object);
    }

    public String getPk() {
        return pk;
    }
}