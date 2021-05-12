package eu.europa.ec.empl.edci.issuer.entity.specs;

import eu.europa.ec.empl.edci.issuer.entity.audit.AuditDAO;
import eu.europa.ec.empl.edci.issuer.entity.audit.AuditListener;
import eu.europa.ec.empl.edci.issuer.entity.dataTypes.CodeDTDAO;
import eu.europa.ec.empl.edci.issuer.entity.dataTypes.NoteDTDAO;
import eu.europa.ec.empl.edci.issuer.entity.dataTypes.TextDTDAO;
import eu.europa.ec.empl.edci.repository.entity.IAuditDAO;
import eu.europa.ec.empl.edci.repository.entity.IAuditedDAO;
import eu.europa.ec.empl.edci.repository.entity.IGenericDAO;
import eu.europa.ec.empl.edci.repository.entity.IMultilangDAO;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.*;
import java.util.Date;
import java.util.Objects;
import java.util.Set;

@EntityListeners(AuditListener.class)
@Entity(name = EuropassCredentialSpecDAO.TABLE)
@Table(name = EuropassCredentialSpecDAO.TABLE)
@Transactional(propagation = Propagation.REQUIRED)
public class EuropassCredentialSpecDAO implements IAuditedDAO, IGenericDAO, IMultilangDAO {

    public static final String TABLE = "SPEC_EUROPASS_CREDENTIAL";
    public static final String TABLE_SHORT = "SPEC_EU_CRED";
    public static final String TABLE_PK_REF = TABLE_SHORT + "_PK";
    public static final String TABLE_SEQ = TABLE + "_SEQ";

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = TABLE_SEQ)
    @SequenceGenerator(sequenceName = TABLE_SEQ, allocationSize = 1, name = TABLE_SEQ)
    @Column(name = "PK")
    private Long pk;

    @Column(name = "LANGUAGES", columnDefinition = "VARCHAR2(4000)", nullable = false)
    private Set<String> languages;

    @Column(name = "DEFAULT_TITLE", nullable = false)
    private String defaultTitle;

    @Column(name = "DEFAULT_LANGUAGE", nullable = false)
    private String defaultLanguage; //1

    /* *************
     *   Fields    *
     ***************/

    @OneToOne(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinColumn(name = "TITLE_PK", referencedColumnName = "PK", nullable = false)
    private TextDTDAO title; //1

    @OneToOne(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinColumn(name = "DESCRIPTION_PK", referencedColumnName = "PK")
    private NoteDTDAO description; //0..1

    @OneToOne(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinColumn(name = "TYPE_PK", referencedColumnName = "PK", nullable = false)
    private CodeDTDAO type; //1

    @Column(name = "ISSUANCE_DATE", nullable = false)
    private Date issuanceDate; //1

    @Column(name = "EXPIRATION_DATE")
    private Date expirationDate; //0..1

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
    private Set<LearningAchievementSpecDAO> achieved;

    @OneToMany(cascade = {CascadeType.REFRESH, CascadeType.DETACH})
    @JoinTable(
            name = "REL_" + TABLE_SHORT + "_PERFOR",
            joinColumns = @JoinColumn(name = TABLE_PK_REF),
            inverseJoinColumns = @JoinColumn(name = LearningActivitySpecDAO.TABLE_PK_REF))
    private Set<LearningActivitySpecDAO> performed;

    @OneToMany(cascade = {CascadeType.REFRESH, CascadeType.DETACH})
    @JoinTable(
            name = "REL_" + TABLE_SHORT + "_ENTITL",
            joinColumns = @JoinColumn(name = TABLE_PK_REF),
            inverseJoinColumns = @JoinColumn(name = EntitlementSpecDAO.TABLE_PK_REF))
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

    @Override
    public Long getPk() {
        return pk;
    }

    public CodeDTDAO getType() {
        return type;
    }

    public void setType(CodeDTDAO type) {
        this.type = type;
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