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
import eu.europa.ec.empl.edci.issuer.entity.dataContainers.EntitlemSpecificationDCDAO;
import eu.europa.ec.empl.edci.issuer.entity.dataTypes.IdentifierDTDAO;
import eu.europa.ec.empl.edci.issuer.entity.dataTypes.NoteDTDAO;
import eu.europa.ec.empl.edci.issuer.entity.dataTypes.TextDTDAO;
import eu.europa.ec.empl.edci.repository.entity.*;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * The type Global history dao.
 */
@EntityListeners(value = {AuditListener.class, OCBIdentifiedListener.class})
@Entity(name = EntitlementSpecDAO.TABLE)
@Table(name = EntitlementSpecDAO.TABLE)
@Transactional(propagation = Propagation.REQUIRED)
@CustomizableEntity(identifierField = "OCBID", labelKey = "custom.entity.label.entitlement", position = 6, specClass = EntitlementSpecDAO.class, entityCode = EntitlementSpecDAO.ENTITY_CODE)
public class EntitlementSpecDAO implements IAuditedDAO, IGenericDAO, IMultilangDAO, Identifiable, IOCBIdentifiedDAO, ILabeledDAO {

    public static final String TABLE = "SPEC_ENTITLEMENT";
    public static final String TABLE_SHORT = "SPEC_ENTITL";
    public static final String TABLE_PK_REF = TABLE_SHORT + "_PK";
    public static final String TABLE_SEQ = TABLE + "_SEQ";
    public static final String ENTITY_CODE = "ENT";

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
    @CustomizableField(position = 1, labelKey = "custom.field.entitlement.title", fieldType = FieldType.TEXT, fieldPath = ENTITY_CODE + CustomizableEntity.dmPathEntityHolderBlock + CustomizableEntity.dmPathSeparator + "title" + CustomizableEntity.dmPathLangHolderBlock,
            validation = Customization.VALIDATION_LENGTH + Customization.Validation.parameter_separator_open + "255" + Customization.Validation.parameter_separator_close)
    private TextDTDAO title; //1

    @OneToOne(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinColumn(name = "DESCRIPTION_PK", referencedColumnName = "PK")
    @CustomizableField(position = 2, labelKey = "custom.field.entitlement.description", fieldType = FieldType.TEXT_AREA, fieldPath = ENTITY_CODE + CustomizableEntity.dmPathEntityHolderBlock + CustomizableEntity.dmPathSeparator + "description" + CustomizableEntity.dmPathLangHolderBlock,
            validation = Customization.VALIDATION_LENGTH + Customization.Validation.parameter_separator_open + "4000" + Customization.Validation.parameter_separator_close)
    private NoteDTDAO description; //0..1

    @Column(name = "ISSUED_DATE")
    @CustomizableField(position = 3, labelKey = "custom.field.entitlement.issuedDate", fieldType = FieldType.DATE, fieldPath = ENTITY_CODE + CustomizableEntity.dmPathEntityHolderBlock + CustomizableEntity.dmPathSeparator + "issuedDate",
            validation = Customization.VALIDATION_DATELOCALFORMAT, additionalInfo = EDCIConstants.DATE_LOCAL)
    private LocalDate issuedDate; //0..1

    @Column(name = "EXPIRY_DATE")
    @CustomizableField(position = 3, labelKey = "custom.field.entitlement.expiryDate", fieldType = FieldType.DATE, fieldPath = ENTITY_CODE + CustomizableEntity.dmPathEntityHolderBlock + CustomizableEntity.dmPathSeparator + "expiryDate",
            validation = Customization.VALIDATION_DATELOCALFORMAT, additionalInfo = EDCIConstants.DATE_LOCAL)
    private LocalDate expiryDate; //0..1

    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinTable(
            name = "FIELD_" + TABLE_SHORT + "_ADDI_NOTE",
            joinColumns = @JoinColumn(name = TABLE_PK_REF),
            inverseJoinColumns = @JoinColumn(name = NoteDTDAO.TABLE_PK_REF))
    @CustomizableField(position = 4, labelKey = "custom.field.entitlement.additionalNote", fieldPath = ENTITY_CODE + CustomizableEntity.dmPathEntityHolderBlock + CustomizableEntity.dmPathSeparator + "additionalNote" + CustomizableEntity.dmPathIdHolderBlock + CustomizableEntity.dmPathLangHolderBlock, fieldType = FieldType.TEXT_AREA,
            validation = Customization.VALIDATION_LENGTH + Customization.Validation.parameter_separator_open + "4000" + Customization.Validation.parameter_separator_close)
    //@CustomizableRelation(position = 5, labelKey = "custom.relation.entitlement.additionalNote", relPath = ENTITY_CODE + CustomizableEntity.dmPathEntityHolderBlock + "additionalNote" + CustomizableEntity.dmPathIdHolderBlock + CustomizableEntity.dmPathLangHolderBlock, hidden = true)
    private List<NoteDTDAO> additionalNote; //*

    @OneToOne(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinColumn(name = "SPECIFIED_PK", referencedColumnName = "PK")
    private EntitlemSpecificationDCDAO specifiedBy; //0..1


    /* *************
     *  Relations  *
     ***************/

    @ManyToMany(cascade = {CascadeType.REFRESH, CascadeType.DETACH})
    @JoinTable(
            name = "REL_" + TABLE_SHORT + "_HAS_PART",
            joinColumns = @JoinColumn(name = TABLE_PK_REF),
            inverseJoinColumns = @JoinColumn(name = EntitlementSpecDAO.TABLE_PK_REF + "_SUB"))
    @CustomizableRelation(position = 6, labelKey = "custom.relation.entitlement.hasPart", relPath = ENTITY_CODE + CustomizableEntity.dmPathEntityHolderBlock + CustomizableEntity.dmPathSeparator + "hasPart" + CustomizableEntity.dmPathIdHolderBlock,
            groupId = 51)
    private Set<EntitlementSpecDAO> hasPart;

    @ManyToMany(cascade = {CascadeType.REFRESH, CascadeType.DETACH})
    @JoinTable(
            name = "REL_" + TABLE_SHORT + "_WAS_DERIV_FR",
            joinColumns = @JoinColumn(name = TABLE_PK_REF),
            inverseJoinColumns = @JoinColumn(name = LearningAchievementSpecDAO.TABLE_PK_REF))
    private Set<LearningAchievementSpecDAO> wasDerivedFrom;

    @OneToOne(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinColumn(name = "AUDIT_PK", referencedColumnName = "PK")
    private AuditDAO auditDAO;

    public EntitlementSpecDAO() {

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

    public void setPk(Long pk) {
        this.pk = pk;
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

    public NoteDTDAO getDescription() {
        return description;
    }

    public void setDescription(NoteDTDAO description) {
        this.description = description;
    }

    public LocalDate getIssuedDate() {
        return issuedDate;
    }

    public void setIssuedDate(LocalDate issuedDate) {
        this.issuedDate = issuedDate;
    }

    public LocalDate getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDate expiryDate) {
        this.expiryDate = expiryDate;
    }

    public List<NoteDTDAO> getAdditionalNote() {
        return additionalNote;
    }

    public void setAdditionalNote(List<NoteDTDAO> additionalNote) {
        this.additionalNote = additionalNote;
    }

    public EntitlemSpecificationDCDAO getSpecifiedBy() {
        return specifiedBy;
    }

    public void setSpecifiedBy(EntitlemSpecificationDCDAO specifiedBy) {
        this.specifiedBy = specifiedBy;
    }

    public Set<EntitlementSpecDAO> getHasPart() {
        return hasPart;
    }

    public void setHasPart(Set<EntitlementSpecDAO> hasPart) {
        this.hasPart = hasPart;
    }

    public Set<LearningAchievementSpecDAO> getWasDerivedFrom() {
        return wasDerivedFrom;
    }

    public void setWasDerivedFrom(Set<LearningAchievementSpecDAO> wasDerivedFrom) {
        this.wasDerivedFrom = wasDerivedFrom;
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
        EntitlementSpecDAO that = (EntitlementSpecDAO) o;
        return pk.equals(that.pk);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getClass(), pk);
    }

}

