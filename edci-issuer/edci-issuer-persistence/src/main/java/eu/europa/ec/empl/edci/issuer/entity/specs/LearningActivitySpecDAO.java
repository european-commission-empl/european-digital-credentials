package eu.europa.ec.empl.edci.issuer.entity.specs;

import eu.europa.ec.empl.edci.constants.EDCIConstants;
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
import eu.europa.ec.empl.edci.issuer.entity.dataContainers.LearningActSpecificationDCDAO;
import eu.europa.ec.empl.edci.issuer.entity.dataContainers.LearningSpecificationDCDAO;
import eu.europa.ec.empl.edci.issuer.entity.dataContainers.LocationDCDAO;
import eu.europa.ec.empl.edci.issuer.entity.dataTypes.IdentifierDTDAO;
import eu.europa.ec.empl.edci.issuer.entity.dataTypes.NoteDTDAO;
import eu.europa.ec.empl.edci.issuer.entity.dataTypes.TextDTDAO;
import eu.europa.ec.empl.edci.repository.entity.*;
import org.joda.time.Period;
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
@Entity(name = LearningActivitySpecDAO.TABLE)
@Table(name = LearningActivitySpecDAO.TABLE)
@Transactional(propagation = Propagation.REQUIRED)
@CustomizableEntity(identifierField = "OCBID", labelKey = "custom.entity.label.activity", position = 4, specClass = LearningActivitySpecDAO.class, entityCode = LearningActivitySpecDAO.ENTITY_CODE)
public class LearningActivitySpecDAO implements IAuditedDAO, IGenericDAO, IMultilangDAO, Identifiable, IOCBIdentifiedDAO, ILabeledDAO {

    public static final String TABLE = "SPEC_ACTIVITY";
    public static final String TABLE_SHORT = "SPEC_ACTIV";
    public static final String TABLE_PK_REF = TABLE_SHORT + "_PK";
    public static final String TABLE_SEQ = TABLE + "_SEQ";
    public static final String ENTITY_CODE = "ACT";

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

    @OneToOne(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinColumn(name = "TITLE_PK", referencedColumnName = "PK", nullable = false)
    @CustomizableField(position = 1, labelKey = "custom.field.activity.title",
            fieldPath = ENTITY_CODE + CustomizableEntity.dmPathEntityHolderBlock + CustomizableEntity.dmPathSeparator + "title" + CustomizableEntity.dmPathLangHolderBlock,
            fieldType = FieldType.TEXT, validation = Customization.VALIDATION_LENGTH + Customization.Validation.parameter_separator_open + "255" + Customization.Validation.parameter_separator_close)
    private TextDTDAO title; //1

    @OneToOne(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinColumn(name = "DECRIPTION_PK", referencedColumnName = "PK")
    @CustomizableField(position = 2, labelKey = "custom.field.activity.description",
            fieldPath = ENTITY_CODE + CustomizableEntity.dmPathEntityHolderBlock + CustomizableEntity.dmPathSeparator + "description" + CustomizableEntity.dmPathLangHolderBlock,
            fieldType = FieldType.TEXT, validation = Customization.VALIDATION_LENGTH + Customization.Validation.parameter_separator_open + "4000" + Customization.Validation.parameter_separator_close)
    private NoteDTDAO description; //0..1

    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinTable(
            name = "FIELD_" + TABLE_SHORT + "_ADD_NOTE",
            joinColumns = @JoinColumn(name = TABLE_PK_REF),
            inverseJoinColumns = @JoinColumn(name = NoteDTDAO.TABLE_PK_REF))
    @CustomizableField(position = 8, labelKey = "custom.field.activity.additionalNote", fieldPath = ENTITY_CODE + CustomizableEntity.dmPathEntityHolderBlock + CustomizableEntity.dmPathSeparator + "additionalNote" + CustomizableEntity.dmPathIdHolderBlock + CustomizableEntity.dmPathLangHolderBlock,
            fieldType = FieldType.TEXT_AREA, validation = Customization.VALIDATION_LENGTH + Customization.Validation.parameter_separator_open + "4000" + Customization.Validation.parameter_separator_close)
    //@CustomizableRelation(position = 9, labelKey = "custom.relation.activity.additionalNote", relPath = ENTITY_CODE + CustomizableEntity.dmPathEntityHolderBlock + CustomizableEntity.dmPathSeparator + "additionalNote" + CustomizableEntity.dmPathIdHolderBlock + CustomizableEntity.dmPathLangHolderBlock, hidden = true)
    private List<NoteDTDAO> additionalNote; //*

    @Column(name = "WORKLOAD")
    @CustomizableField(position = 5, labelKey = "custom.field.activity.workload",
            fieldPath = ENTITY_CODE + CustomizableEntity.dmPathEntityHolderBlock + CustomizableEntity.dmPathSeparator + "workload", fieldType = FieldType.TEXT,
            validation = Customization.VALIDATION_NUMERIC + Customization.Validation.validator_separator
                    + Customization.VALIDATION_LENGTH + Customization.Validation.parameter_separator_open + "255" + Customization.Validation.parameter_separator_close)
    private Period workload; //0..1 in hours

    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinTable(
            name = "FIELD_" + TABLE_SHORT + "_LOCATION",
            joinColumns = @JoinColumn(name = TABLE_PK_REF),
            inverseJoinColumns = @JoinColumn(name = LocationDCDAO.TABLE_PK_REF))

    @CustomizableField(position = 6, labelKey = "custom.field.activity.location.name", fieldPath = ENTITY_CODE + CustomizableEntity.dmPathEntityHolderBlock + CustomizableEntity.dmPathSeparator + "location[0]" + CustomizableEntity.dmPathSeparator + "geographicName" + CustomizableEntity.dmPathLangHolderBlock, //geographicName is "name" in the datamodel
            fieldType = FieldType.TEXT, validation = Customization.VALIDATION_LENGTH + Customization.Validation.parameter_separator_open + "255" + Customization.Validation.parameter_separator_close)
   /* @CustomizableRelation(position = 7, labelKey = "custom.relation.activity.location.hasAddress",
            relPath = ENTITY_CODE + CustomizableEntity.dmPathEntityHolderBlock + CustomizableEntity.dmPathSeparator + "location.hasAddress" + CustomizableEntity.dmPathIdHolderBlock)*/
    private List<LocationDCDAO> location; //*

    @Column(name = "STARTED_AT_TIME")
    @CustomizableField(position = 3, labelKey = "custom.field.activity.startedAtTime",
            fieldPath = ENTITY_CODE + CustomizableEntity.dmPathEntityHolderBlock + CustomizableEntity.dmPathSeparator + "startedAtTime", fieldType = FieldType.DATE,
            validation = Customization.VALIDATION_DATEFORMAT, additionalInfo = EDCIConstants.DATE_LOCAL)
    private Date startedAtTime; //0..1

    @Column(name = "ENDED_AT_TIME")
    @CustomizableField(position = 4, labelKey = "custom.field.activity.endedAtTime",
            fieldPath = ENTITY_CODE + CustomizableEntity.dmPathEntityHolderBlock + CustomizableEntity.dmPathSeparator + "endedAtTime", fieldType = FieldType.DATE,
            validation = Customization.VALIDATION_DATEFORMAT, additionalInfo = EDCIConstants.DATE_LOCAL)
    private Date endedAtTime; //0..1

    @OneToOne(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinColumn(name = "USED_LEARN_OPPORT_PK", referencedColumnName = "PK")
    private LearningSpecificationDCDAO usedLearningOpportunity;

    @OneToOne(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinColumn(name = "SPECIFIED_BY", referencedColumnName = "PK")
    private LearningActSpecificationDCDAO specifiedBy;

    /* *************
     *  Relations  *
     ***************/

    @ManyToMany(cascade = {CascadeType.REFRESH, CascadeType.DETACH})
    @JoinTable(
            name = "REL_" + TABLE_SHORT + "_DIREC_BY_ORG",
            joinColumns = @JoinColumn(name = TABLE_PK_REF),
            inverseJoinColumns = @JoinColumn(name = OrganizationSpecDAO.TABLE_PK_REF))
    private Set<OrganizationSpecDAO> directedBy;

    @ManyToMany(cascade = {CascadeType.REFRESH, CascadeType.DETACH})
    @JoinTable(
            name = "REL_" + TABLE_SHORT + "_HAS_PART",
            joinColumns = @JoinColumn(name = TABLE_PK_REF),
            inverseJoinColumns = @JoinColumn(name = TABLE_PK_REF + "_SUB"))
    @CustomizableRelation(position = 10,
            labelKey = "custom.relation.activity.hasPart",
            relPath = ENTITY_CODE + CustomizableEntity.dmPathEntityHolderBlock + CustomizableEntity.dmPathSeparator + "hasPart" + CustomizableEntity.dmPathIdHolderBlock,
            groupId = 21)
    private Set<LearningActivitySpecDAO> hasPart;

    @ManyToMany(cascade = {CascadeType.REFRESH, CascadeType.DETACH})
    @JoinTable(
            name = "REL_" + TABLE_SHORT + "_INFLUENCED",
            joinColumns = @JoinColumn(name = TABLE_PK_REF),
            inverseJoinColumns = @JoinColumn(name = LearningAchievementSpecDAO.TABLE_PK_REF))
    private Set<LearningAchievementSpecDAO> influenced;

    @OneToOne(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinColumn(name = "AUDIT_PK", referencedColumnName = "PK")
    private AuditDAO auditDAO;

    public LearningActivitySpecDAO() {
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
        return this.getIdentifiableNameFromFieldList(this, false, "title/label", "description", "pk");
    }

    @Override
    public Long getPk() {
        return pk;
    }

    @Override
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

    public Period getWorkload() {
        return workload;
    }

    public void setWorkload(Period workload) {
        this.workload = workload;
    }

    public List<LocationDCDAO> getLocation() {
        return location;
    }

    public void setLocation(List<LocationDCDAO> location) {
        this.location = location;
    }

    public Date getStartedAtTime() {
        return startedAtTime;
    }

    public void setStartedAtTime(Date startedAtTime) {
        this.startedAtTime = startedAtTime;
    }

    public Date getEndedAtTime() {
        return endedAtTime;
    }

    public void setEndedAtTime(Date endedAtTime) {
        this.endedAtTime = endedAtTime;
    }

    public Set<OrganizationSpecDAO> getDirectedBy() {
        return directedBy;
    }

    public void setDirectedBy(Set<OrganizationSpecDAO> directedBy) {
        this.directedBy = directedBy;
    }

    public LearningSpecificationDCDAO getUsedLearningOpportunity() {
        return usedLearningOpportunity;
    }

    public void setUsedLearningOpportunity(LearningSpecificationDCDAO usedLearningOpportunity) {
        this.usedLearningOpportunity = usedLearningOpportunity;
    }

    public LearningActSpecificationDCDAO getSpecifiedBy() {
        return specifiedBy;
    }

    public void setSpecifiedBy(LearningActSpecificationDCDAO specifiedBy) {
        this.specifiedBy = specifiedBy;
    }

    public Set<LearningActivitySpecDAO> getHasPart() {
        return hasPart;
    }

    public void setHasPart(Set<LearningActivitySpecDAO> hasPart) {
        this.hasPart = hasPart;
    }

    public Set<LearningAchievementSpecDAO> getInfluenced() {
        return influenced;
    }

    public void setInfluenced(Set<LearningAchievementSpecDAO> influenced) {
        this.influenced = influenced;
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
        LearningActivitySpecDAO that = (LearningActivitySpecDAO) o;
        return pk.equals(that.pk);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getClass(), pk);
    }
}