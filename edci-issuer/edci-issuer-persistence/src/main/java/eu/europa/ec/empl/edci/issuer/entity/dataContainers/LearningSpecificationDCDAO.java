package eu.europa.ec.empl.edci.issuer.entity.dataContainers;

import eu.europa.ec.empl.edci.annotation.EmptiableIgnore;
import eu.europa.ec.empl.edci.datamodel.Emptiable;
import eu.europa.ec.empl.edci.issuer.entity.dataTypes.*;
import eu.europa.ec.empl.edci.issuer.entity.specs.AssessmentSpecDAO;
import eu.europa.ec.empl.edci.issuer.entity.specs.EntitlementSpecDAO;
import eu.europa.ec.empl.edci.issuer.entity.specs.LearningActivitySpecDAO;
import eu.europa.ec.empl.edci.issuer.entity.specs.LearningOutcomeSpecDAO;
import eu.europa.ec.empl.edci.repository.entity.IGenericDAO;
import org.joda.time.Period;

import javax.persistence.*;
import java.util.List;
import java.util.Set;

@Entity(name = LearningSpecificationDCDAO.TABLE)
@Table(name = LearningSpecificationDCDAO.TABLE)
public class LearningSpecificationDCDAO implements IGenericDAO, Emptiable {

    public static final String TABLE = "DC_LEARNING_SPECIFICATION";
    public static final String TABLE_SHORT = "DC_LEA_SPE";
    public static final String TABLE_PK_REF = TABLE_SHORT + "_PK";
    public static final String TABLE_SEQ = TABLE + "_SEQ";

    /* *************
     *   Fields    *
     ***************/

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = TABLE_SEQ)
    @SequenceGenerator(sequenceName = TABLE_SEQ, allocationSize = 1, name = TABLE_SEQ)
    @Column(name = "PK")
    @EmptiableIgnore
    private Long pk;

    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinTable(
            name = "FIELD_" + TABLE_SHORT + "_IDENTIFIER",
            joinColumns = @JoinColumn(name = TABLE_PK_REF),
            inverseJoinColumns = @JoinColumn(name = IdentifierDTDAO.TABLE_PK_REF))
    private Set<IdentifierDTDAO> identifier; //*

    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinTable(
            name = "FIELD_" + TABLE_SHORT + "_TYPE",
            joinColumns = @JoinColumn(name = TABLE_PK_REF),
            inverseJoinColumns = @JoinColumn(name = CodeDTDAO.TABLE_PK_REF))
    private Set<CodeDTDAO> learningOpportunityType; //*

    @OneToOne(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinColumn(name = "TITLE_PK", referencedColumnName = "PK")
    private TextDTDAO title; //0..1

    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinTable(
            name = "FIELD_" + TABLE_SHORT + "_ALT_LABEL",
            joinColumns = @JoinColumn(name = TABLE_PK_REF),
            inverseJoinColumns = @JoinColumn(name = TextDTDAO.TABLE_PK_REF))
    private Set<TextDTDAO> alternativeLabel; //*

    @OneToOne(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinColumn(name = "DEFINITION_PK", referencedColumnName = "PK")
    private NoteDTDAO definition; //0..1

    @OneToOne(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinColumn(name = "LEARN_OUTCOME_PK", referencedColumnName = "PK")
    private NoteDTDAO learningOutcomeDescription; //0..1

    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinTable(
            name = "FIELD_" + TABLE_SHORT + "_ADD_NOTE",
            joinColumns = @JoinColumn(name = TABLE_PK_REF),
            inverseJoinColumns = @JoinColumn(name = NoteDTDAO.TABLE_PK_REF))
    private Set<NoteDTDAO> additionalNote; //*

    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinTable(
            name = "FIELD_" + TABLE_SHORT + "_HOME_PAGE",
            joinColumns = @JoinColumn(name = TABLE_PK_REF),
            inverseJoinColumns = @JoinColumn(name = WebDocumentDCDAO.TABLE_PK_REF))
    private Set<WebDocumentDCDAO> homePage; //*

    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinTable(
            name = "FIELD_" + TABLE_SHORT + "_SUPPL_DOC",
            joinColumns = @JoinColumn(name = TABLE_PK_REF),
            inverseJoinColumns = @JoinColumn(name = WebDocumentDCDAO.TABLE_PK_REF))
    private Set<WebDocumentDCDAO> supplementaryDocument; //*

    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinTable(
            name = "FIELD_" + TABLE_SHORT + "_ISCED",
            joinColumns = @JoinColumn(name = TABLE_PK_REF),
            inverseJoinColumns = @JoinColumn(name = CodeDTDAO.TABLE_PK_REF))
    private Set<CodeDTDAO> ISCEDFCode; //*

    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinTable(
            name = "FIELD_" + TABLE_SHORT + "_EDU_SUBJ",
            joinColumns = @JoinColumn(name = TABLE_PK_REF),
            inverseJoinColumns = @JoinColumn(name = CodeDTDAO.TABLE_PK_REF))
    private Set<CodeDTDAO> educationSubject; //* EducationSubjectAssociationDTDAO

    @Column(name = "VOLUME_OF_LEARNING")
    private Period volumeOfLearning; //0..1

    @OneToOne(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinColumn(name = "ECTS_CRED_POINTS_PK", referencedColumnName = "PK")
    private ScoreDTDAO ECTSCreditPoints; //0..1

    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinTable(
            name = "FIELD_" + TABLE_SHORT + "_CRED_POIN",
            joinColumns = @JoinColumn(name = TABLE_PK_REF),
            inverseJoinColumns = @JoinColumn(name = ScoreDTDAO.TABLE_PK_REF))
    private Set<ScoreDTDAO> creditPoints; //*

    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinTable(
            name = "FIELD_" + TABLE_SHORT + "_EDU_LVL",
            joinColumns = @JoinColumn(name = TABLE_PK_REF),
            inverseJoinColumns = @JoinColumn(name = CodeDTDAO.TABLE_PK_REF))
    private Set<CodeDTDAO> educationLevel; //* EducationSubjectAssociationDTDAO

    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinTable(
            name = "FIELD_" + TABLE_SHORT + "_LANG",
            joinColumns = @JoinColumn(name = TABLE_PK_REF),
            inverseJoinColumns = @JoinColumn(name = CodeDTDAO.TABLE_PK_REF))
    private Set<CodeDTDAO> language; //*

    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinTable(
            name = "FIELD_" + TABLE_SHORT + "_MODE",
            joinColumns = @JoinColumn(name = TABLE_PK_REF),
            inverseJoinColumns = @JoinColumn(name = CodeDTDAO.TABLE_PK_REF))
    private Set<CodeDTDAO> mode; //*

    @OneToOne(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinColumn(name = "LEARNING_SETTING_PK", referencedColumnName = "PK")
    private CodeDTDAO learningSetting; //0..1

    @Column(name = "MAXIMUM_DURATION")
    private Period maximumDuration; //0..1

    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinTable(
            name = "FIELD_" + TABLE_SHORT + "_TARG_GRP",
            joinColumns = @JoinColumn(name = TABLE_PK_REF),
            inverseJoinColumns = @JoinColumn(name = CodeDTDAO.TABLE_PK_REF))
    private Set<CodeDTDAO> targetGroup; //*

    @OneToOne(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinColumn(name = "ENTRY_REQ_NOTE_PK", referencedColumnName = "PK")
    private NoteDTDAO entryRequirementsNote; //0..1

    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinTable(
            name = "FIELD_" + TABLE_SHORT + "_AWARD_OP",
            joinColumns = @JoinColumn(name = TABLE_PK_REF),
            inverseJoinColumns = @JoinColumn(name = AwardingOpportunityDCDAO.TABLE_PK_REF))
    private Set<AwardingOpportunityDCDAO> awardingOpportunity; //*

    @OneToOne(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinColumn(name = "EQF_LEVEL", referencedColumnName = "PK")
    private CodeDTDAO eqfLevel; //0..1

    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinTable(
            name = "FIELD_" + TABLE_SHORT + "_NQF_LVL",
            joinColumns = @JoinColumn(name = TABLE_PK_REF),
            inverseJoinColumns = @JoinColumn(name = CodeDTDAO.TABLE_PK_REF))
    private List<CodeDTDAO> nqfLevel; //*

    @OneToOne(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinColumn(name = "NQF_LEVEL_PARENT", referencedColumnName = "PK")
    private CodeDTDAO nqfLevelParent; //0..1

    @Column(name = "IS_PARTIAL_QUALIF")
    private Boolean isPartialQualification; //0..1

    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinTable(
            name = "FIELD_" + TABLE_SHORT + "_HAS_ACCRED",
            joinColumns = @JoinColumn(name = TABLE_PK_REF),
            inverseJoinColumns = @JoinColumn(name = AccreditationDCDAO.TABLE_PK_REF))
    private List<AccreditationDCDAO> hasAccreditation; //*

    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinTable(
            name = "FIELD_" + TABLE_SHORT + "_QUAL_CODE",
            joinColumns = @JoinColumn(name = TABLE_PK_REF),
            inverseJoinColumns = @JoinColumn(name = CodeDTDAO.TABLE_PK_REF))
    private List<CodeDTDAO> qualificationCode; //*


    /* *************
     *  Relations  *
     ***************/

    @ManyToMany(cascade = {CascadeType.REFRESH, CascadeType.DETACH})
    @JoinTable(
            name = "FIELD_" + TABLE_SHORT + "_LEA_OUTCOME",
            joinColumns = @JoinColumn(name = TABLE_PK_REF),
            inverseJoinColumns = @JoinColumn(name = LearningOutcomeSpecDAO.TABLE_PK_REF))
    private Set<LearningOutcomeSpecDAO> learningOutcome; //*

    @OneToOne(cascade = {CascadeType.REFRESH, CascadeType.DETACH})
    @JoinColumn(name = "LEARN_ACTIV_PK", referencedColumnName = "PK")
    private LearningActivitySpecDAO learningActivitySpecification; //0..1

    @OneToOne(cascade = {CascadeType.REFRESH, CascadeType.DETACH})
    @JoinColumn(name = "ASSESSMENT_PK", referencedColumnName = "PK")
    private AssessmentSpecDAO assessmentSpecification; //0..1

    @ManyToMany(cascade = {CascadeType.REFRESH, CascadeType.DETACH})
    @JoinTable(
            name = "FIELD_" + TABLE_SHORT + "_ENTI_SPEC",
            joinColumns = @JoinColumn(name = TABLE_PK_REF),
            inverseJoinColumns = @JoinColumn(name = EntitlementSpecDAO.TABLE_PK_REF))
    private Set<EntitlementSpecDAO> entitlementSpecification; //*

    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinTable(
            name = "FIELD_" + TABLE_SHORT + "_HAS_PART",
            joinColumns = @JoinColumn(name = TABLE_PK_REF),
            inverseJoinColumns = @JoinColumn(name = LearningSpecificationDCDAO.TABLE_PK_REF))
    private Set<LearningSpecificationDCDAO> hasPart; //*

    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinTable(
            name = "FIELD_" + TABLE_SHORT + "_SPEC_OF",
            joinColumns = @JoinColumn(name = TABLE_PK_REF),
            inverseJoinColumns = @JoinColumn(name = LearningSpecificationDCDAO.TABLE_PK_REF))
    private Set<LearningSpecificationDCDAO> specialisationOf; //*

    public Set<IdentifierDTDAO> getIdentifier() {
        return identifier;
    }

    public void setIdentifier(Set<IdentifierDTDAO> identifier) {
        this.identifier = identifier;
    }

    public Set<CodeDTDAO> getLearningOpportunityType() {
        return learningOpportunityType;
    }

    public void setLearningOpportunityType(Set<CodeDTDAO> learningOpportunityType) {
        this.learningOpportunityType = learningOpportunityType;
    }

    public TextDTDAO getTitle() {
        return title;
    }

    public void setTitle(TextDTDAO title) {
        this.title = title;
    }

    public Set<TextDTDAO> getAlternativeLabel() {
        return alternativeLabel;
    }

    public void setAlternativeLabel(Set<TextDTDAO> alternativeLabel) {
        this.alternativeLabel = alternativeLabel;
    }

    public NoteDTDAO getDefinition() {
        return definition;
    }

    public void setDefinition(NoteDTDAO definition) {
        this.definition = definition;
    }

    public CodeDTDAO getEqfLevel() {
        return eqfLevel;
    }

    public void setEqfLevel(CodeDTDAO eqfLevel) {
        this.eqfLevel = eqfLevel;
    }

    public List<CodeDTDAO> getNqfLevel() {
        return nqfLevel;
    }

    public void setNqfLevel(List<CodeDTDAO> nqfLevel) {
        this.nqfLevel = nqfLevel;
    }

    public CodeDTDAO getNqfLevelParent() {
        return nqfLevelParent;
    }

    public void setNqfLevelParent(CodeDTDAO nqfLevelParent) {
        this.nqfLevelParent = nqfLevelParent;
    }

    public Boolean getPartialQualification() {
        return isPartialQualification;
    }

    public void setPartialQualification(Boolean partialQualification) {
        isPartialQualification = partialQualification;
    }

    public Boolean getIsPartialQualification() {
        return getPartialQualification();
    }

    public void setIsPartialQualification(Boolean partialQualification) {
        setPartialQualification(partialQualification);
    }

    public List<AccreditationDCDAO> getHasAccreditation() {
        return hasAccreditation;
    }

    public void setHasAccreditation(List<AccreditationDCDAO> hasAccreditation) {
        this.hasAccreditation = hasAccreditation;
    }

    public List<CodeDTDAO> getQualificationCode() {
        return qualificationCode;
    }

    public void setQualificationCode(List<CodeDTDAO> qualificationCode) {
        this.qualificationCode = qualificationCode;
    }

    public NoteDTDAO getLearningOutcomeDescription() {
        return learningOutcomeDescription;
    }

    public void setLearningOutcomeDescription(NoteDTDAO learningOutcomeDescription) {
        this.learningOutcomeDescription = learningOutcomeDescription;
    }

    public Set<NoteDTDAO> getAdditionalNote() {
        return additionalNote;
    }

    public void setAdditionalNote(Set<NoteDTDAO> additionalNote) {
        this.additionalNote = additionalNote;
    }

    public Long getPk() {
        return pk;
    }

    public void setPk(Long pk) {
        this.pk = pk;
    }

    public void setEntitlementSpecification(Set<EntitlementSpecDAO> entitlementSpecification) {
        this.entitlementSpecification = entitlementSpecification;
    }

    public Set<WebDocumentDCDAO> getHomePage() {
        return homePage;
    }

    public void setHomePage(Set<WebDocumentDCDAO> homePage) {
        this.homePage = homePage;
    }

    public Set<WebDocumentDCDAO> getSupplementaryDocument() {
        return supplementaryDocument;
    }

    public void setSupplementaryDocument(Set<WebDocumentDCDAO> supplementaryDocument) {
        this.supplementaryDocument = supplementaryDocument;
    }

    public Set<CodeDTDAO> getISCEDFCode() {
        return ISCEDFCode;
    }

    public void setISCEDFCode(Set<CodeDTDAO> ISCEDFCode) {
        this.ISCEDFCode = ISCEDFCode;
    }

    public Period getVolumeOfLearning() {
        return volumeOfLearning;
    }

    public void setVolumeOfLearning(Period volumeOfLearning) {
        this.volumeOfLearning = volumeOfLearning;
    }

    public ScoreDTDAO getECTSCreditPoints() {
        return ECTSCreditPoints;
    }

    public void setECTSCreditPoints(ScoreDTDAO ECTSCreditPoints) {
        this.ECTSCreditPoints = ECTSCreditPoints;
    }

    public Set<ScoreDTDAO> getCreditPoints() {
        return creditPoints;
    }

    public void setCreditPoints(Set<ScoreDTDAO> creditPoints) {
        this.creditPoints = creditPoints;
    }

    public Set<CodeDTDAO> getEducationSubject() {
        return educationSubject;
    }

    public void setEducationSubject(Set<CodeDTDAO> educationSubject) {
        this.educationSubject = educationSubject;
    }

    public Set<CodeDTDAO> getEducationLevel() {
        return educationLevel;
    }

    public void setEducationLevel(Set<CodeDTDAO> educationLevel) {
        this.educationLevel = educationLevel;
    }

    public Set<CodeDTDAO> getLanguage() {
        return language;
    }

    public void setLanguage(Set<CodeDTDAO> language) {
        this.language = language;
    }

    public Set<CodeDTDAO> getMode() {
        return mode;
    }

    public void setMode(Set<CodeDTDAO> mode) {
        this.mode = mode;
    }

    public CodeDTDAO getLearningSetting() {
        return learningSetting;
    }

    public void setLearningSetting(CodeDTDAO learningSetting) {
        this.learningSetting = learningSetting;
    }

    public Period getMaximumDuration() {
        return maximumDuration;
    }

    public void setMaximumDuration(Period maximumDuration) {
        this.maximumDuration = maximumDuration;
    }

    public Set<CodeDTDAO> getTargetGroup() {
        return targetGroup;
    }

    public void setTargetGroup(Set<CodeDTDAO> targetGroup) {
        this.targetGroup = targetGroup;
    }

    public NoteDTDAO getEntryRequirementsNote() {
        return entryRequirementsNote;
    }

    public void setEntryRequirementsNote(NoteDTDAO entryRequirementsNote) {
        this.entryRequirementsNote = entryRequirementsNote;
    }

    public Set<AwardingOpportunityDCDAO> getAwardingOpportunity() {
        return awardingOpportunity;
    }

    public void setAwardingOpportunity(Set<AwardingOpportunityDCDAO> awardingOpportunity) {
        this.awardingOpportunity = awardingOpportunity;
    }

    public Set<LearningOutcomeSpecDAO> getLearningOutcome() {
        return learningOutcome;
    }

    public void setLearningOutcome(Set<LearningOutcomeSpecDAO> learningOutcome) {
        this.learningOutcome = learningOutcome;
    }

    public LearningActivitySpecDAO getLearningActivitySpecification() {
        return learningActivitySpecification;
    }

    public void setLearningActivitySpecification(LearningActivitySpecDAO learningActivitySpecification) {
        this.learningActivitySpecification = learningActivitySpecification;
    }

    public AssessmentSpecDAO getAssessmentSpecification() {
        return assessmentSpecification;
    }

    public void setAssessmentSpecification(AssessmentSpecDAO assessmentSpecification) {
        this.assessmentSpecification = assessmentSpecification;
    }

    public Set<EntitlementSpecDAO> getEntitlementSpecification() {
        return entitlementSpecification;
    }

    public Set<LearningSpecificationDCDAO> getHasPart() {
        return hasPart;
    }

    public void setHasPart(Set<LearningSpecificationDCDAO> hasPart) {
        this.hasPart = hasPart;
    }

    public Set<LearningSpecificationDCDAO> getSpecialisationOf() {
        return specialisationOf;
    }

    public void setSpecialisationOf(Set<LearningSpecificationDCDAO> specialisationOf) {
        this.specialisationOf = specialisationOf;
    }
}