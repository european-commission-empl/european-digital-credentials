package eu.europa.ec.empl.edci.issuer.entity.specs;

import eu.europa.ec.empl.edci.constants.EDCIConstants;
import eu.europa.ec.empl.edci.exception.EDCIException;
import eu.europa.ec.empl.edci.issuer.common.annotation.CustomizableEntity;
import eu.europa.ec.empl.edci.issuer.common.annotation.CustomizableField;
import eu.europa.ec.empl.edci.issuer.common.annotation.CustomizableRelation;
import eu.europa.ec.empl.edci.issuer.common.constants.Customization;
import eu.europa.ec.empl.edci.issuer.common.model.customization.FieldType;
import eu.europa.ec.empl.edci.issuer.entity.audit.AuditDAO;
import eu.europa.ec.empl.edci.issuer.entity.audit.AuditListener;
import eu.europa.ec.empl.edci.issuer.entity.audit.OCBIdentifiedListener;
import eu.europa.ec.empl.edci.issuer.entity.dataContainers.AwardingProcessDCDAO;
import eu.europa.ec.empl.edci.issuer.entity.dataContainers.LearningAsmSpecificationDCDAO;
import eu.europa.ec.empl.edci.issuer.entity.dataContainers.LocationDCDAO;
import eu.europa.ec.empl.edci.issuer.entity.dataContainers.WebDocumentDCDAO;
import eu.europa.ec.empl.edci.issuer.entity.dataTypes.*;
import eu.europa.ec.empl.edci.repository.entity.*;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@EntityListeners(value = {AuditListener.class, OCBIdentifiedListener.class})
@Entity(name = LearningAssessmentSpecDAO.TABLE)
@Table(name = LearningAssessmentSpecDAO.TABLE)
@Transactional(propagation = Propagation.REQUIRED)
@CustomizableEntity(identifierField = "OCBID", labelKey = "custom.entity.label.assessment", position = 5, specClass = LearningAssessmentSpecDAO.class, entityCode = LearningAssessmentSpecDAO.ENTITY_CODE)
public class LearningAssessmentSpecDAO implements IAuditedDAO, IGenericDAO, IMultilangDAO, IOCBIdentifiedDAO, ILabeledDAO {

    public static final String TABLE = "SPEC_ASSESSMENT";
    public static final String TABLE_SHORT = "SPEC_ASS";
    public static final String TABLE_PK_REF = TABLE_SHORT + "_PK";
    public static final String TABLE_SEQ = TABLE + "_SEQ";
    public static final String ENTITY_CODE = "ASM";

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = TABLE_SEQ)
    @SequenceGenerator(sequenceName = TABLE_SEQ, allocationSize = 1, name = TABLE_SEQ)
    @Column(name = "PK")
    private Long pk;

    @Column(name = "OCBID")
    private String OCBID;

    @Column(name = "LANGUAGES", nullable = false, length = 4000)
    private Set<String> languages;

    @Column(name = "LABEL", nullable = true)
    private String label;

    @Column(name = "DEFAULT_LANGUAGE", nullable = false)
    private String defaultLanguage; //1

    /* *************
     *   Fields    *
     ***************/

    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinTable(
            name = "FIELD_" + TABLE_SHORT + "_IDENTIFIER",
            joinColumns = @JoinColumn(name = TABLE_PK_REF),
            inverseJoinColumns = @JoinColumn(name = IdentifierDTDAO.TABLE_PK_REF))
    private List<IdentifierDTDAO> identifier; //*

    @OneToOne(orphanRemoval = true, cascade = CascadeType.ALL, optional = false)
    @JoinColumn(name = "TITLE_PK", referencedColumnName = "PK")
    @CustomizableField(position = 1, labelKey = "custom.field.assessment.title"
            , fieldPath = ENTITY_CODE + CustomizableEntity.dmPathEntityHolderBlock + CustomizableEntity.dmPathSeparator + "title" + CustomizableEntity.dmPathLangHolderBlock
            , fieldType = FieldType.TEXT, validation = Customization.VALIDATION_LENGTH + Customization.Validation.parameter_separator_open + "255" + Customization.Validation.parameter_separator_close)
    private TextDTDAO title; //0..1


    @OneToOne(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinColumn(name = "DESCRIPTION_PK", referencedColumnName = "PK")
    @CustomizableField(position = 2, labelKey = "custom.field.assessment.description", fieldPath = ENTITY_CODE + CustomizableEntity.dmPathEntityHolderBlock + CustomizableEntity.dmPathSeparator + "description" + CustomizableEntity.dmPathLangHolderBlock,
            fieldType = FieldType.TEXT, validation = Customization.VALIDATION_LENGTH + Customization.Validation.parameter_separator_open + "4000" + Customization.Validation.parameter_separator_close)
    private TextDTDAO description; //0..1

    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinTable(
            name = "FIELD_" + TABLE_SHORT + "_ADD_NOTE",
            joinColumns = @JoinColumn(name = TABLE_PK_REF),
            inverseJoinColumns = @JoinColumn(name = NoteDTDAO.TABLE_PK_REF))
    @CustomizableField(position = 5, labelKey = "custom.field.assessment.additionalNote", dynamicMethodLabelKey = "getSubjectLabel", fieldPath = ENTITY_CODE + CustomizableEntity.dmPathEntityHolderBlock + CustomizableEntity.dmPathSeparator + "additionalNote" + CustomizableEntity.dmPathIdHolderBlock + CustomizableEntity.dmPathLangHolderBlock,
            fieldType = FieldType.TEXT_AREA, validation = Customization.VALIDATION_LENGTH + Customization.Validation.parameter_separator_open + "4000" + Customization.Validation.parameter_separator_close)
    private List<NoteDTDAO> additionalNote; //*

    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinTable(
            name = "FIELD_" + TABLE_SHORT + "_OTHER_DOCS",
            joinColumns = @JoinColumn(name = TABLE_PK_REF),
            inverseJoinColumns = @JoinColumn(name = WebDocumentDCDAO.TABLE_PK_REF))
    private Set<WebDocumentDCDAO> supplementaryDocument; //*

    @OneToOne(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinColumn(name = "GRADE_NOTE_PK", referencedColumnName = "PK")
    @CustomizableField(position = 4, labelKey = "custom.field.assessment.grade", shouldInstanceMethodName = "shouldIncludeGrade", mandatory = true, fieldPath = ENTITY_CODE + CustomizableEntity.dmPathEntityHolderBlock + CustomizableEntity.dmPathSeparator + "grade.noteLiteral"  + CustomizableEntity.dmPathLangHolderBlock,
            fieldType = FieldType.TEXT, validation = Customization.VALIDATION_MANDATORY + Customization.Validation.validator_separator +
            Customization.VALIDATION_LENGTH + Customization.Validation.parameter_separator_open + "255" + Customization.Validation.parameter_separator_close)
    private NoteDTDAO grade; //1

    @OneToOne(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinColumn(name = "SHORT_GRADING_PK", referencedColumnName = "PK")
    private ShortenedGradingDTDAO shortenedGrading; //0..1

    @OneToOne(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinColumn(name = "RESULT_DISTRIB_PK", referencedColumnName = "PK")
    private ResultDistributionDTDAO resultDistribution; //0..1

    @Column(name = "ISSUED_DATE")
    @CustomizableField(position = 3, labelKey = "custom.field.assessment.issuedDate", fieldPath = ENTITY_CODE + CustomizableEntity.dmPathEntityHolderBlock + CustomizableEntity.dmPathSeparator + "dateIssued",
            fieldType = FieldType.DATE, validation = Customization.VALIDATION_DATELOCALFORMAT, additionalInfo = EDCIConstants.DATE_LOCAL)
    private Date dateIssued; //0..1

    @OneToOne(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinColumn(name = "ID_VERIFICATION_PK", referencedColumnName = "PK")
    private CodeDTDAO idVerification; //0..1

    @OneToOne(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinColumn(name = "AWARDED_BY_PK", referencedColumnName = "PK")
    private AwardingProcessDCDAO awardedBy; //0..1

    @OneToOne(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinColumn(name = "AWARDING_LOCAT_PK", referencedColumnName = "PK")
    private LocationDCDAO location; //0..1

    @OneToOne(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinColumn(name = "SPECIFIED_BY_PK", referencedColumnName = "PK")
    private LearningAsmSpecificationDCDAO specifiedBy; //0..1

    /* *************
     *  Relations  *
     ***************/

    @ManyToMany(cascade = {CascadeType.REFRESH, CascadeType.DETACH})
    @JoinTable(
            name = "REL_" + TABLE_SHORT + "_HAS_PART",
            joinColumns = @JoinColumn(name = TABLE_PK_REF),
            inverseJoinColumns = @JoinColumn(name = LearningAssessmentSpecDAO.TABLE_PK_REF + "_SUB"))
    @CustomizableRelation(
            position = 6,
            labelKey = "custom.relation.assessment.hasPart",
            relPath = ENTITY_CODE + CustomizableEntity.dmPathEntityHolderBlock + CustomizableEntity.dmPathSeparator + "hasPart" + CustomizableEntity.dmPathIdHolderBlock,
            groupId = 41)
    private Set<LearningAssessmentSpecDAO> hasPart;

    @ManyToMany(cascade = {CascadeType.REFRESH, CascadeType.DETACH})
    @JoinTable(
            name = "REL_" + TABLE_SHORT + "_SPEC_OF",
            joinColumns = @JoinColumn(name = TABLE_PK_REF),
            inverseJoinColumns = @JoinColumn(name = OrganizationSpecDAO.TABLE_PK_REF + "_SUB"))
    private Set<OrganizationSpecDAO> assessedBy; //0..*

    @OneToOne(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinColumn(name = "AUDIT_PK", referencedColumnName = "PK")
    private AuditDAO auditDAO;

    public LearningAssessmentSpecDAO() {
    }

    @Override
    public void setId(URI id) {
        throw new EDCIException().addDescription("cannot be implemented");
    }

    @Override
    public URI getId() {
        try {
            return new URI(String.valueOf(pk));
        } catch (URISyntaxException e) {
            return null;
        }
    }

    public AwardingProcessDCDAO getAwardedBy() {
        return awardedBy;
    }

    public void setAwardedBy(AwardingProcessDCDAO awardedBy) {
        this.awardedBy = awardedBy;
    }

    @Override
    public String getName() {
        return this.getNameFromFieldList(this, false, "title/label", "description", "pk");
    }

    @Override
    public Long getPk() {
        return pk;
    }

    @Override
    public void setPk(Long pk) {
        this.pk = pk;
    }

    public LocationDCDAO getLocation() {
        return location;
    }

    public void setLocation(LocationDCDAO location) {
        this.location = location;
    }

    @Override
    public Set<String> getLanguages() {
        return languages;
    }

    @Override
    public void setLanguages(Set<String> languages) {
        this.languages = languages;
    }

    @Override
    public String getLabel() {
        return label;
    }

    @Override
    public void setLabel(String label) {
        this.label = label;
    }

    public String getDefaultLanguage() {
        return defaultLanguage;
    }

    public void setDefaultLanguage(String defaultLanguage) {
        this.defaultLanguage = defaultLanguage;
    }

    public List<IdentifierDTDAO> getIdentifier() {
        return identifier;
    }

    public void setIdentifier(List<IdentifierDTDAO> identifier) {
        this.identifier = identifier;
    }

    public TextDTDAO getTitle() {
        return title;
    }

    public void setTitle(TextDTDAO title) {
        this.title = title;
    }

    public TextDTDAO getDescription() {
        return description;
    }

    public void setDescription(TextDTDAO description) {
        this.description = description;
    }

    public List<NoteDTDAO> getAdditionalNote() {
        return additionalNote;
    }

    public void setAdditionalNote(List<NoteDTDAO> additionalNote) {
        this.additionalNote = additionalNote;
    }

    public NoteDTDAO getGrade() {
        return grade;
    }

    public void setGrade(NoteDTDAO grade) {
        this.grade = grade;
    }

    public ShortenedGradingDTDAO getShortenedGrading() {
        return shortenedGrading;
    }

    public void setShortenedGrading(ShortenedGradingDTDAO shortenedGrading) {
        this.shortenedGrading = shortenedGrading;
    }

    public ResultDistributionDTDAO getResultDistribution() {
        return resultDistribution;
    }

    public void setResultDistribution(ResultDistributionDTDAO resultDistribution) {
        this.resultDistribution = resultDistribution;
    }

    public Date getDateIssued() {
        return dateIssued;
    }

    public void setDateIssued(Date dateIssued) {
        this.dateIssued = dateIssued;
    }

    public CodeDTDAO getIdVerification() {
        return idVerification;
    }

    public void setIdVerification(CodeDTDAO idVerification) {
        this.idVerification = idVerification;
    }

    public LearningAsmSpecificationDCDAO getSpecifiedBy() {
        return specifiedBy;
    }

    public void setSpecifiedBy(LearningAsmSpecificationDCDAO specifiedBy) {
        this.specifiedBy = specifiedBy;
    }

    public Set<LearningAssessmentSpecDAO> getHasPart() {
        return hasPart;
    }

    public void setHasPart(Set<LearningAssessmentSpecDAO> hasPart) {
        this.hasPart = hasPart;
    }

    public Set<OrganizationSpecDAO> getAssessedBy() {
        return assessedBy;
    }

    public void setAssessedBy(Set<OrganizationSpecDAO> assessedBy) {
        this.assessedBy = assessedBy;
    }

    @Override
    public String getOCBID() {
        return OCBID;
    }

    @Override
    public void setOCBID(String OCBID) {
        this.OCBID = OCBID;
    }

    @Override
    public AuditDAO getAuditDAO() {
        return auditDAO;
    }

    public void setAuditDAO(AuditDAO auditDAO) {
        this.auditDAO = auditDAO;
    }

    @Override
    public void setAuditDAO(IAuditDAO auditDAO) {
        setAuditDAO((AuditDAO) auditDAO);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LearningAssessmentSpecDAO that = (LearningAssessmentSpecDAO) o;
        return pk.equals(that.pk);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getClass(), pk);
    }

    public boolean shouldIncludeGrade() {
        //Grade is always included. DM v2 says so
        return true;
//        return this.getSpecifiedBy() != null && this.getSpecifiedBy().getGradingSchemes() != null;
    }

    public Set<WebDocumentDCDAO> getSupplementaryDocument() {
        return supplementaryDocument;
    }

    public void setSupplementaryDocument(Set<WebDocumentDCDAO> supplementaryDocument) {
        this.supplementaryDocument = supplementaryDocument;
    }
}