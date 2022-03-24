package eu.europa.ec.empl.edci.issuer.entity.specs;

import eu.europa.ec.empl.edci.issuer.entity.audit.AuditDAO;
import eu.europa.ec.empl.edci.issuer.entity.audit.AuditListener;
import eu.europa.ec.empl.edci.issuer.entity.dataContainers.EntitlemSpecificationDCDAO;
import eu.europa.ec.empl.edci.issuer.entity.dataTypes.IdentifierDTDAO;
import eu.europa.ec.empl.edci.issuer.entity.dataTypes.NoteDTDAO;
import eu.europa.ec.empl.edci.issuer.entity.dataTypes.TextDTDAO;
import eu.europa.ec.empl.edci.repository.entity.IAuditDAO;
import eu.europa.ec.empl.edci.repository.entity.IAuditedDAO;
import eu.europa.ec.empl.edci.repository.entity.IGenericDAO;
import eu.europa.ec.empl.edci.repository.entity.IMultilangDAO;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * The type Global history dao.
 */
@EntityListeners(AuditListener.class)
@Entity(name = EntitlementSpecDAO.TABLE)
@Table(name = EntitlementSpecDAO.TABLE)
@Transactional(propagation = Propagation.REQUIRED)
public class EntitlementSpecDAO implements IAuditedDAO, IGenericDAO, IMultilangDAO {

    public static final String TABLE = "SPEC_ENTITLEMENT";
    public static final String TABLE_SHORT = "SPEC_ENTITL";
    public static final String TABLE_PK_REF = TABLE_SHORT + "_PK";
    public static final String TABLE_SEQ = TABLE + "_SEQ";

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = TABLE_SEQ)
    @SequenceGenerator(sequenceName = TABLE_SEQ, allocationSize = 1, name = TABLE_SEQ)
    @Column(name = "PK")
    private Long pk;

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
    private TextDTDAO title; //1

    @OneToOne(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinColumn(name = "DESCRIPTION_PK", referencedColumnName = "PK")
    private NoteDTDAO description; //0..1

    @Column(name = "ISSUED_DATE")
    private LocalDate issuedDate; //0..1

    @Column(name = "EXPIRY_DATE")
    private LocalDate expiryDate; //0..1

    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinTable(
            name = "FIELD_" + TABLE_SHORT + "_ADDI_NOTE",
            joinColumns = @JoinColumn(name = TABLE_PK_REF),
            inverseJoinColumns = @JoinColumn(name = NoteDTDAO.TABLE_PK_REF))
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
    public Long getPk() {
        return pk;
    }

    public void setPk(Long pk) {
        this.pk = pk;
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

