package eu.europa.ec.empl.edci.issuer.entity.dataContainers;

import eu.europa.ec.empl.edci.annotation.EmptiableIgnore;
import eu.europa.ec.empl.edci.issuer.entity.dataTypes.CodeDTDAO;
import eu.europa.ec.empl.edci.issuer.entity.dataTypes.CreditPointDTDAO;
import eu.europa.ec.empl.edci.issuer.entity.dataTypes.NoteDTDAO;
import eu.europa.ec.empl.edci.issuer.entity.dataTypes.TextDTDAO;
import eu.europa.ec.empl.edci.issuer.entity.specs.*;
import eu.europa.ec.empl.edci.model.Emptiable;
import eu.europa.ec.empl.edci.repository.entity.IGenericDAO;
import org.joda.time.Period;

import javax.persistence.*;
import java.util.List;
import java.util.Set;

@Entity(name = LearningAchSpecificationDCDAO.TABLE)
@Table(name = LearningAchSpecificationDCDAO.TABLE)
public class LearningAchSpecificationDCDAO implements IGenericDAO, Emptiable {

    public static final String TABLE = "DC_LEARNING_SPECIFICATION";
    public static final String TABLE_SHORT = "DC_LEA_SPE";
    public static final String TABLE_PK_REF = TABLE_SHORT + "_PK";
    public static final String TABLE_SEQ = TABLE + "_SEQ";

    /* *************
     *   Fields    *
     ***************/

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = TABLE_SEQ)
    @SequenceGenerator(sequenceName = TABLE_SEQ, allocationSize = 1, name = TABLE_SEQ)
    @Column(name = "PK")
    @EmptiableIgnore
    private Long pk;

    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinTable(
            name = "FIELD_" + TABLE_SHORT + "_TYPE",
            joinColumns = @JoinColumn(name = TABLE_PK_REF),
            inverseJoinColumns = @JoinColumn(name = CodeDTDAO.TABLE_PK_REF))
    private Set<CodeDTDAO> learningOpportunityType; //*

    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinTable(
            name = "FIELD_" + TABLE_SHORT + "_TYPE",
            joinColumns = @JoinColumn(name = TABLE_PK_REF),
            inverseJoinColumns = @JoinColumn(name = CodeDTDAO.TABLE_PK_REF))
    private Set<CodeDTDAO> dcType; //*

    @OneToOne(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinColumn(name = "TITLE_PK", referencedColumnName = "PK")
    private TextDTDAO title; //0..1

    @OneToOne(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinColumn(name = "ALTERNATIVE_NAME_PK", referencedColumnName = "PK")
    private TextDTDAO altLabel; //*

    @OneToOne(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinColumn(name = "CATEGORY_PK", referencedColumnName = "PK")
    private TextDTDAO category; //*

    @OneToOne(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinColumn(name = "DEFINITION_PK", referencedColumnName = "PK")
    private NoteDTDAO definition; //0..1

    @OneToOne(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinColumn(name = "LEARN_OUTCOME_PK", referencedColumnName = "PK")
    private NoteDTDAO learningOutcomeSummary; //0..1

    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinTable(
            name = "FIELD_" + TABLE_SHORT + "_ADD_NOTE",
            joinColumns = @JoinColumn(name = TABLE_PK_REF),
            inverseJoinColumns = @JoinColumn(name = NoteDTDAO.TABLE_PK_REF))
    private Set<NoteDTDAO> additionalNote; //*

    @OneToOne(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinColumn(name = "DESCRIPTION_PK", referencedColumnName = "PK")
    private NoteDTDAO description; //0..*

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
    private Set<CodeDTDAO> thematicArea; //*

    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinTable(
            name = "FIELD_" + TABLE_SHORT + "_EDUCA_SUBJ",
            joinColumns = @JoinColumn(name = TABLE_PK_REF),
            inverseJoinColumns = @JoinColumn(name = CodeDTDAO.TABLE_PK_REF))
    private Set<CodeDTDAO> educationSubject; //* EducationSubjectAssociationDTDAO

    @Column(name = "VOLUME_OF_LEARNING")
    private Period volumeOfLearning; //0..1

    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinTable(
            name = "FIELD_" + TABLE_SHORT + "_EDUCA_LVL",
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

    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinTable(
            name = "FIELD_" + TABLE_SHORT + "_CRED_PO",
            joinColumns = @JoinColumn(name = TABLE_PK_REF),
            inverseJoinColumns = @JoinColumn(name = CreditPointDTDAO.TABLE_PK_REF))
    private Set<CreditPointDTDAO> creditPoints; //*

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
    private NoteDTDAO entryRequirement; //0..1

    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinTable(
            name = "FIELD_" + TABLE_SHORT + "_AWARD_OP",
            joinColumns = @JoinColumn(name = TABLE_PK_REF),
            inverseJoinColumns = @JoinColumn(name = AwardingOpportunityDCDAO.TABLE_PK_REF))
    private Set<AwardingOpportunityDCDAO> awardingOpportunity; //*

    @OneToOne(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinColumn(name = "EQF_LEVEL", referencedColumnName = "PK")
    private CodeDTDAO EQFLevel; //0..1

    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinTable(
            name = "FIELD_" + TABLE_SHORT + "_NQF_LVL",
            joinColumns = @JoinColumn(name = TABLE_PK_REF),
            inverseJoinColumns = @JoinColumn(name = CodeDTDAO.TABLE_PK_REF))
    private List<CodeDTDAO> NQFLevel; //*

    @OneToOne(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinColumn(name = "NQF_LEVEL_PARENT", referencedColumnName = "PK")
    private CodeDTDAO nqfLevelParent; //0..1

    @Column(name = "IS_PARTIAL_QUALIF")
    private Boolean partialQualification; //0..1

    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinTable(
            name = "FIELD_" + TABLE_SHORT + "_QUA_CODE",
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
    private LearningAssessmentSpecDAO assessmentSpecification; //0..1

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
            inverseJoinColumns = @JoinColumn(name = LearningAchSpecificationDCDAO.TABLE_PK_REF))
    private Set<LearningAchSpecificationDCDAO> hasPart; //*

    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinTable(
            name = "FIELD_" + TABLE_SHORT + "_ACCREDITATION",
            joinColumns = @JoinColumn(name = TABLE_PK_REF),
            inverseJoinColumns = @JoinColumn(name = AccreditationSpecDAO.TABLE_PK_REF))
    private Set<AccreditationSpecDAO> accreditation; //*

    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinTable(
            name = "FIELD_" + TABLE_SHORT + "_SPEC_OF",
            joinColumns = @JoinColumn(name = TABLE_PK_REF),
            inverseJoinColumns = @JoinColumn(name = LearningAchSpecificationDCDAO.TABLE_PK_REF))
    private Set<LearningAchSpecificationDCDAO> specialisationOf; //*

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

    public TextDTDAO getAltLabel() {
        return altLabel;
    }

    public void setAltLabel(TextDTDAO altLabel) {
        this.altLabel = altLabel;
    }

    public NoteDTDAO getDefinition() {
        return definition;
    }

    public void setDefinition(NoteDTDAO definition) {
        this.definition = definition;
    }

    public CodeDTDAO getEQFLevel() {
        return EQFLevel;
    }

    public void setEQFLevel(CodeDTDAO EQFLevel) {
        this.EQFLevel = EQFLevel;
    }

    public List<CodeDTDAO> getNQFLevel() {
        return NQFLevel;
    }

    public void setNQFLevel(List<CodeDTDAO> NQFLevel) {
        this.NQFLevel = NQFLevel;
    }

    public CodeDTDAO getNqfLevelParent() {
        return nqfLevelParent;
    }

    public void setNqfLevelParent(CodeDTDAO nqfLevelParent) {
        this.nqfLevelParent = nqfLevelParent;
    }

    public Boolean getPartialQualification() {
        return partialQualification;
    }

    public void setPartialQualification(Boolean partialQualification) {
        this.partialQualification = partialQualification;
    }

    public List<CodeDTDAO> getQualificationCode() {
        return qualificationCode;
    }

    public void setQualificationCode(List<CodeDTDAO> qualificationCode) {
        this.qualificationCode = qualificationCode;
    }

    public NoteDTDAO getLearningOutcomeSummary() {
        return learningOutcomeSummary;
    }

    public void setLearningOutcomeSummary(NoteDTDAO learningOutcomeSummary) {
        this.learningOutcomeSummary = learningOutcomeSummary;
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

    public Set<CodeDTDAO> getThematicArea() {
        return thematicArea;
    }

    public void setThematicArea(Set<CodeDTDAO> thematicArea) {
        this.thematicArea = thematicArea;
    }

    public Period getVolumeOfLearning() {
        return volumeOfLearning;
    }

    public void setVolumeOfLearning(Period volumeOfLearning) {
        this.volumeOfLearning = volumeOfLearning;
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

    public NoteDTDAO getEntryRequirement() {
        return entryRequirement;
    }

    public void setEntryRequirement(NoteDTDAO entryRequirement) {
        this.entryRequirement = entryRequirement;
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

    public LearningAssessmentSpecDAO getAssessmentSpecification() {
        return assessmentSpecification;
    }

    public void setAssessmentSpecification(LearningAssessmentSpecDAO assessmentSpecification) {
        this.assessmentSpecification = assessmentSpecification;
    }

    public Set<EntitlementSpecDAO> getEntitlementSpecification() {
        return entitlementSpecification;
    }

    public Set<LearningAchSpecificationDCDAO> getHasPart() {
        return hasPart;
    }

    public void setHasPart(Set<LearningAchSpecificationDCDAO> hasPart) {
        this.hasPart = hasPart;
    }

    public Set<LearningAchSpecificationDCDAO> getSpecialisationOf() {
        return specialisationOf;
    }

    public void setSpecialisationOf(Set<LearningAchSpecificationDCDAO> specialisationOf) {
        this.specialisationOf = specialisationOf;
    }

    public Set<CodeDTDAO> getDcType() {
        return dcType;
    }

    public void setDcType(Set<CodeDTDAO> dcType) {
        this.dcType = dcType;
    }

    public TextDTDAO getCategory() {
        return category;
    }

    public void setCategory(TextDTDAO category) {
        this.category = category;
    }

    public NoteDTDAO getDescription() {
        return description;
    }

    public void setDescription(NoteDTDAO description) {
        this.description = description;
    }

    public Set<CreditPointDTDAO> getCreditPoints() {
        return creditPoints;
    }

    public void setCreditPoints(Set<CreditPointDTDAO> creditPoints) {
        this.creditPoints = creditPoints;
    }

    public Set<AccreditationSpecDAO> getAccreditation() {
        return accreditation;
    }

    public void setAccreditation(Set<AccreditationSpecDAO> accreditation) {
        this.accreditation = accreditation;
    }
}