package eu.europa.ec.empl.edci.issuer.entity.specs;

import eu.europa.ec.empl.edci.datamodel.model.base.Identifiable;
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
import eu.europa.ec.empl.edci.issuer.entity.dataContainers.LearningSpecificationDCDAO;
import eu.europa.ec.empl.edci.issuer.entity.dataTypes.IdentifierDTDAO;
import eu.europa.ec.empl.edci.issuer.entity.dataTypes.NoteDTDAO;
import eu.europa.ec.empl.edci.issuer.entity.dataTypes.TextDTDAO;
import eu.europa.ec.empl.edci.repository.entity.*;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@EntityListeners(value = {AuditListener.class, OCBIdentifiedListener.class})
@Entity(name = LearningAchievementSpecDAO.TABLE)
@Table(name = LearningAchievementSpecDAO.TABLE)
@Transactional(propagation = Propagation.REQUIRED)
@CustomizableEntity(identifierField = "OCBID", labelKey = "custom.entity.label.achievement", position = 3, specClass = LearningAchievementSpecDAO.class, entityCode = LearningAchievementSpecDAO.ENTITY_CODE)
public class LearningAchievementSpecDAO implements IAuditedDAO, IGenericDAO, IMultilangDAO, Identifiable, IOCBIdentifiedDAO {

    public static final String TABLE = "SPEC_LEARNING_ACHIEVEMENT";
    public static final String TABLE_SHORT = "SPEC_L_ACHI";
    public static final String TABLE_PK_REF = TABLE_SHORT + "_PK";
    public static final String TABLE_SEQ = TABLE + "_SEQ";
    public static final String ENTITY_CODE = "ACH";

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = TABLE_SEQ)
    @SequenceGenerator(sequenceName = TABLE_SEQ, allocationSize = 1, name = TABLE_SEQ)
    @Column(name = "PK")
    private Long pk;

    @Column(name = "OCBID")
    private String OCBID;

    //    @Convert(converter = StringCollectionConverter.class)
    @Column(name = "LANGUAGES", nullable = false, length = 4000)
    private Set<String> languages;

    @Column(name = "DEFAULT_TITLE", nullable = false)
    private String defaultTitle;

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

    @OneToOne(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinColumn(name = "TITLE_PK", referencedColumnName = "PK", nullable = false)
    @CustomizableField(position = 1, labelKey = "custom.field.achievement.title"
            , fieldPath = ENTITY_CODE + CustomizableEntity.dmPathEntityHolderBlock + CustomizableEntity.dmPathSeparator + "title" + CustomizableEntity.dmPathLangHolderBlock
            , fieldType = FieldType.TEXT, validation = Customization.VALIDATION_LENGTH + Customization.Validation.parameter_separator_open + "255" + Customization.Validation.parameter_separator_close)
    private TextDTDAO title; //1

    @OneToOne(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinColumn(name = "DESCRIPTION_PK", referencedColumnName = "PK")
    @CustomizableField(position = 3, labelKey = "custom.field.achievement.description",
            fieldPath = ENTITY_CODE + CustomizableEntity.dmPathEntityHolderBlock + CustomizableEntity.dmPathSeparator + "description" + CustomizableEntity.dmPathLangHolderBlock,
            fieldType = FieldType.TEXT_AREA, validation = Customization.VALIDATION_LENGTH + Customization.Validation.parameter_separator_open + "4000" + Customization.Validation.parameter_separator_close)
    private NoteDTDAO description; //0..1

    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinTable(
            name = "FIELD_" + TABLE_SHORT + "_ADD_NOTE",
            joinColumns = @JoinColumn(name = TABLE_PK_REF),
            inverseJoinColumns = @JoinColumn(name = NoteDTDAO.TABLE_PK_REF))
    @CustomizableField(position = 4,
            labelKey = "custom.field.achievement.additionalNote",
            fieldPath = ENTITY_CODE + CustomizableEntity.dmPathEntityHolderBlock + CustomizableEntity.dmPathSeparator + "additionalNote" + CustomizableEntity.dmPathIdHolderBlock + CustomizableEntity.dmPathLangHolderBlock,
            fieldType = FieldType.TEXT_AREA, validation = Customization.VALIDATION_LENGTH + Customization.Validation.parameter_separator_open + "4000" + Customization.Validation.parameter_separator_close)
    /*@CustomizableRelation(position = 5,
            labelKey = "custom.relation.achievements.additionalNote",
            relPath = ENTITY_CODE + CustomizableEntity.dmPathEntityHolderBlock + CustomizableEntity.dmPathSeparator + "additionalNote" + CustomizableEntity.dmPathIdHolderBlock + CustomizableEntity.dmPathLangHolderBlock, hidden = true)*/
    private List<NoteDTDAO> additionalNote; //*

    @OneToOne(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinColumn(name = "AWARDED_BY_PK", referencedColumnName = "PK")
    @CustomizableField(position = 2,
            labelKey = "custom.field.achievement.awardingProcess.awardingDate",
            fieldPath = ENTITY_CODE + CustomizableEntity.dmPathEntityHolderBlock + CustomizableEntity.dmPathSeparator + "wasAwardedBy.awardingDate",
            fieldType = FieldType.DATE, validation = Customization.VALIDATION_DATEFORMAT)
    private AwardingProcessDCDAO wasAwardedBy; //0..1

    @OneToOne(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinColumn(name = "SPECIFIED_BY_PK", referencedColumnName = "PK")
    LearningSpecificationDCDAO specifiedBy; //1

//    @OneToOne(orphanRemoval = true, cascade = CascadeType.ALL)
//    @JoinColumn(name = "ASS_LEARN_OPPORT_BY_PK", referencedColumnName = "PK")
//    LearningSpecificationDCDAO associatedLearningOpportunity; //0..1

    /* *************
     *  Relations  *
     ***************/

    @ManyToMany(cascade = {CascadeType.REFRESH, CascadeType.DETACH})
    @JoinTable(
            name = "REL_" + TABLE_SHORT + "_HAS_PART_ACH",
            joinColumns = @JoinColumn(name = TABLE_PK_REF),
            inverseJoinColumns = @JoinColumn(name = TABLE_PK_REF + "_SUB"))
    @CustomizableRelation(
            position = 7,
            labelKey = "custom.relation.achievements.hasPart",
            relPath = ENTITY_CODE + CustomizableEntity.dmPathEntityHolderBlock + CustomizableEntity.dmPathSeparator + "hasPart" + CustomizableEntity.dmPathIdHolderBlock,
            groupId = 22)
    private Set<LearningAchievementSpecDAO> hasPart; //*

    @ManyToMany(cascade = {CascadeType.REFRESH, CascadeType.DETACH})
    @JoinTable(
            name = "REL_" + TABLE_SHORT + "_INFLUE_BY",
            joinColumns = @JoinColumn(name = TABLE_PK_REF),
            inverseJoinColumns = @JoinColumn(name = LearningActivitySpecDAO.TABLE_PK_REF))
    @CustomizableRelation(
            position = 6,
            labelKey = "custom.relation.achievements.influencedBy",
            relPath = ENTITY_CODE + CustomizableEntity.dmPathEntityHolderBlock + CustomizableEntity.dmPathSeparator + "wasInfluencedBy" + CustomizableEntity.dmPathIdHolderBlock,
            groupId = 21)
    private Set<LearningActivitySpecDAO> wasInfluencedBy; //*

    @ManyToMany(cascade = {CascadeType.REFRESH, CascadeType.DETACH})
    @JoinTable(
            name = "REL_" + TABLE_SHORT + "_DERIVED_FROM",
            joinColumns = @JoinColumn(name = TABLE_PK_REF),
            inverseJoinColumns = @JoinColumn(name = AssessmentSpecDAO.TABLE_PK_REF))
    private Set<AssessmentSpecDAO> wasDerivedFrom; //*

    @ManyToMany(cascade = {CascadeType.REFRESH, CascadeType.DETACH})
    @JoinTable(
            name = "REL_" + TABLE_SHORT + "_ENTITLES_TO",
            joinColumns = @JoinColumn(name = TABLE_PK_REF),
            inverseJoinColumns = @JoinColumn(name = EntitlementSpecDAO.TABLE_PK_REF))
    private Set<EntitlementSpecDAO> entitlesTo; //*

    @OneToOne(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinColumn(name = "AUDIT_PK", referencedColumnName = "PK")
    private AuditDAO auditDAO;

    public LearningAchievementSpecDAO() {
    }

    public Long getPk() {
        return pk;
    }

    @Override
    public String getHashCodeSeed() {
        return String.valueOf(pk);
    }

    @Override
    public URI getId() {
        try {
            return new URI(String.valueOf(pk));
        } catch (URISyntaxException e) {
            return null;
        }
    }

    @Override
    public void setId(URI id) {
        throw new EDCIException().addDescription("cannot be implemented");
    }

    @Override
    public void setHashCodeSeed(String pk) {
        this.setPk(Long.valueOf(pk));
    }

    @Override
    public String getIdentifiableName() {
        return this.getIdentifiableNameFromFieldList(this, false, "defaultTitle", "title", "description", "pk");
    }


    public AwardingProcessDCDAO getWasAwardedBy() {
        return wasAwardedBy;
    }

    public void setWasAwardedBy(AwardingProcessDCDAO wasAwardedBy) {
        this.wasAwardedBy = wasAwardedBy;
    }


    public Set<EntitlementSpecDAO> getEntitlesTo() {
        return entitlesTo;
    }

    public void setEntitlesTo(Set<EntitlementSpecDAO> entitlesTo) {
        this.entitlesTo = entitlesTo;
    }

    public LearningSpecificationDCDAO getSpecifiedBy() {
        return specifiedBy;
    }

    public void setSpecifiedBy(LearningSpecificationDCDAO specifiedBy) {
        this.specifiedBy = specifiedBy;
    }

    public void setPk(Long pk) {
        this.pk = pk;
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

    public Set<LearningAchievementSpecDAO> getHasPart() {
        return hasPart;
    }

    public void setHasPart(Set<LearningAchievementSpecDAO> hasPart) {
        this.hasPart = hasPart;
    }

    public Set<LearningActivitySpecDAO> getWasInfluencedBy() {
        return wasInfluencedBy;
    }

    public void setWasInfluencedBy(Set<LearningActivitySpecDAO> wasInfluencedBy) {
        this.wasInfluencedBy = wasInfluencedBy;
    }

    public Set<AssessmentSpecDAO> getWasDerivedFrom() {
        return wasDerivedFrom;
    }

    public void setWasDerivedFrom(Set<AssessmentSpecDAO> wasDerivedFrom) {
        this.wasDerivedFrom = wasDerivedFrom;
    }

    public NoteDTDAO getDescription() {
        return description;
    }

    public void setDescription(NoteDTDAO description) {
        this.description = description;
    }

    public List<NoteDTDAO> getAdditionalNote() {
        return additionalNote;
    }

    public void setAdditionalNote(List<NoteDTDAO> additionalNote) {
        this.additionalNote = additionalNote;
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
    public Set<String> getLanguages() {
        return languages;
    }

    @Override
    public void setLanguages(Set<String> languages) {
        this.languages = languages;
    }

    @Override
    public String getDefaultTitle() {
        return defaultTitle;
    }

    @Override
    public void setDefaultTitle(String defaultTitle) {
        this.defaultTitle = defaultTitle;
    }

    public String getDefaultLanguage() {
        return defaultLanguage;
    }

    public void setDefaultLanguage(String defaultLanguage) {
        this.defaultLanguage = defaultLanguage;
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LearningAchievementSpecDAO that = (LearningAchievementSpecDAO) o;
        return pk.equals(that.pk);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getClass(), pk);
    }
}