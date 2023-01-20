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
import eu.europa.ec.empl.edci.issuer.entity.dataTypes.CodeDTDAO;
import eu.europa.ec.empl.edci.issuer.entity.dataTypes.IdentifierDTDAO;
import eu.europa.ec.empl.edci.issuer.entity.dataTypes.NoteDTDAO;
import eu.europa.ec.empl.edci.issuer.entity.dataTypes.TextDTDAO;
import eu.europa.ec.empl.edci.repository.entity.*;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Date;
import java.util.Objects;
import java.util.Set;

@EntityListeners(value = {AuditListener.class, OCBIdentifiedListener.class})
@Entity(name = EuropassCredentialSpecDAO.TABLE)
@Table(name = EuropassCredentialSpecDAO.TABLE)
@Transactional(propagation = Propagation.REQUIRED)
@CustomizableEntity(identifierField = "OCBID", labelKey = "custom.entity.label.credential", position = 2, specClass = EuropassCredentialSpecDAO.class, entityCode = EuropassCredentialSpecDAO.ENTITY_CODE)
public class EuropassCredentialSpecDAO implements IAuditedDAO, IGenericDAO, IMultilangDAO, Identifiable, IOCBIdentifiedDAO, ILabeledDAO {

    public static final String TABLE = "SPEC_EUROPASS_CREDENTIAL";
    public static final String TABLE_SHORT = "SPEC_EU_CRED";
    public static final String TABLE_PK_REF = TABLE_SHORT + "_PK";
    public static final String TABLE_SEQ = TABLE + "_SEQ";
    public static final String ENTITY_CODE = "#";

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

    @OneToOne(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinColumn(name = "TITLE_PK", referencedColumnName = "PK", nullable = false)
    @CustomizableField(position = 1, labelKey = "custom.field.credential.title", fieldType = FieldType.TEXT, fieldPath = "title" + CustomizableEntity.dmPathLangHolderBlock,
            validation = Customization.VALIDATION_LENGTH + Customization.Validation.parameter_separator_open + "255" + Customization.Validation.parameter_separator_close)
    private TextDTDAO title; //1

    @OneToOne(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinColumn(name = "DESCRIPTION_PK", referencedColumnName = "PK")
    @CustomizableField(position = 2, labelKey = "custom.field.credential.description", fieldType = FieldType.TEXT_AREA, fieldPath = "description" + CustomizableEntity.dmPathLangHolderBlock,
            validation = Customization.VALIDATION_LENGTH + Customization.Validation.parameter_separator_open + "4000" + Customization.Validation.parameter_separator_close)
    private NoteDTDAO description; //0..1

    @OneToOne(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinColumn(name = "TYPE_PK", referencedColumnName = "PK", nullable = false)
    private CodeDTDAO type; //1

    @Column(name = "ISSUANCE_DATE", nullable = false)
    @CustomizableField(position = 3, labelKey = "custom.field.credential.validFrom", fieldType = FieldType.DATE, fieldPath = "issuanceDate", //validFrom xml field is issuanceDate in DTO
            validation = Customization.VALIDATION_DATEFORMAT, additionalInfo = EDCIConstants.DATE_LOCAL)
    private Date issuanceDate; //1

    @Column(name = "EXPIRATION_DATE")
    @CustomizableField(position = 4, labelKey = "custom.field.credential.validUntil", fieldType = FieldType.DATE, fieldPath = "expirationDate", //validUntil xml field is expirationDate in DTO
            validation = Customization.VALIDATION_DATEFORMAT, additionalInfo = EDCIConstants.DATE_LOCAL)
    private Date expirationDate; //0..1

    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinTable(
            name = "FIELD_" + TABLE_SHORT + "_IDENTIFIER",
            joinColumns = @JoinColumn(name = TABLE_PK_REF),
            inverseJoinColumns = @JoinColumn(name = IdentifierDTDAO.TABLE_PK_REF))
    private Set<IdentifierDTDAO> identifier; //*

    /* *************
     *  Relations  *
     ***************/

    @OneToOne(cascade = {CascadeType.REFRESH, CascadeType.DETACH})
    @JoinColumn(name = "ISSUER_PK", referencedColumnName = "PK") //, nullable = false
    private OrganizationSpecDAO issuer; //1

    @OneToMany(cascade = {CascadeType.REFRESH, CascadeType.DETACH})
    @JoinTable(
            name = "REL_" + TABLE_SHORT + "_ACHIEV",
            joinColumns = @JoinColumn(name = TABLE_PK_REF),
            inverseJoinColumns = @JoinColumn(name = LearningAchievementSpecDAO.TABLE_PK_REF))
    @CustomizableRelation(position = 5, relPath = "REC.achievements" + CustomizableEntity.dmPathIdHolderBlock, labelKey = "custom.relation.credential.achievements",
            groupId = 11)
    private Set<LearningAchievementSpecDAO> achieved;

    @OneToMany(cascade = {CascadeType.REFRESH, CascadeType.DETACH})
    @JoinTable(
            name = "REL_" + TABLE_SHORT + "_PERFOR",
            joinColumns = @JoinColumn(name = TABLE_PK_REF),
            inverseJoinColumns = @JoinColumn(name = LearningActivitySpecDAO.TABLE_PK_REF))
    @CustomizableRelation(position = 6, relPath = "REC.activities" + CustomizableEntity.dmPathIdHolderBlock, labelKey = "custom.relation.credential.activities",
            groupId = 12)
    private Set<LearningActivitySpecDAO> performed;

    @OneToMany(cascade = {CascadeType.REFRESH, CascadeType.DETACH})
    @JoinTable(
            name = "REL_" + TABLE_SHORT + "_ENTITL",
            joinColumns = @JoinColumn(name = TABLE_PK_REF),
            inverseJoinColumns = @JoinColumn(name = EntitlementSpecDAO.TABLE_PK_REF))
    @CustomizableRelation(position = 7, relPath = "REC.entitlements" + CustomizableEntity.dmPathIdHolderBlock, labelKey = "custom.relation.credential.entitlements",
            groupId = 13)
    private Set<EntitlementSpecDAO> entitledTo;

    @ManyToOne(cascade = {CascadeType.REFRESH, CascadeType.DETACH})
    private DiplomaSpecDAO display; //0..1

    @OneToOne(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinColumn(name = "AUDIT_PK", referencedColumnName = "PK")
    private AuditDAO auditDAO;

    public EuropassCredentialSpecDAO() {
    }

    public DiplomaSpecDAO getDisplay() {
        return display;
    }

    public void setDisplay(DiplomaSpecDAO display) {
        this.display = display;
    }

    public OrganizationSpecDAO getIssuer() {
        return issuer;
    }

    public void setIssuer(OrganizationSpecDAO issuer) {
        this.issuer = issuer;
    }

    public Date getIssuanceDate() {
        return issuanceDate;
    }

    public void setIssuanceDate(Date issuanceDate) {
        this.issuanceDate = issuanceDate;
    }

    public Date getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(Date expirationDate) {
        this.expirationDate = expirationDate;
    }

    public Set<LearningAchievementSpecDAO> getAchieved() {
        return achieved;
    }

    public void setAchieved(Set<LearningAchievementSpecDAO> achieved) {
        this.achieved = achieved;
    }

    public Set<LearningActivitySpecDAO> getPerformed() {
        return performed;
    }

    public void setPerformed(Set<LearningActivitySpecDAO> performed) {
        this.performed = performed;
    }

    public Set<EntitlementSpecDAO> getEntitledTo() {
        return entitledTo;
    }

    public void setEntitledTo(Set<EntitlementSpecDAO> entitledTo) {
        this.entitledTo = entitledTo;
    }


    public CodeDTDAO getType() {
        return type;
    }

    public void setType(CodeDTDAO type) {
        this.type = type;
    }

    @Override
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
        return this.getIdentifiableNameFromFieldList(this, false, "title/label", "description", "pk");
    }

    public void setPk(Long pk) {
        this.pk = pk;
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

    public Set<IdentifierDTDAO> getIdentifier() {
        return identifier;
    }

    public void setIdentifier(Set<IdentifierDTDAO> identifier) {
        this.identifier = identifier;
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
        EuropassCredentialSpecDAO that = (EuropassCredentialSpecDAO) o;
        return pk.equals(that.pk);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getClass(), pk);
    }
}