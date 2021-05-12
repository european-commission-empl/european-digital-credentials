package eu.europa.ec.empl.edci.issuer.entity.specs;

import eu.europa.ec.empl.edci.issuer.entity.audit.AuditDAO;
import eu.europa.ec.empl.edci.issuer.entity.audit.AuditListener;
import eu.europa.ec.empl.edci.issuer.entity.dataContainers.AccreditationDCDAO;
import eu.europa.ec.empl.edci.issuer.entity.dataContainers.ContactPointDCDAO;
import eu.europa.ec.empl.edci.issuer.entity.dataContainers.LocationDCDAO;
import eu.europa.ec.empl.edci.issuer.entity.dataContainers.WebDocumentDCDAO;
import eu.europa.ec.empl.edci.issuer.entity.dataTypes.*;
import eu.europa.ec.empl.edci.repository.entity.IAuditDAO;
import eu.europa.ec.empl.edci.repository.entity.IAuditedDAO;
import eu.europa.ec.empl.edci.repository.entity.IGenericDAO;
import eu.europa.ec.empl.edci.repository.entity.IMultilangDAO;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.*;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@EntityListeners(AuditListener.class)
@Entity(name = OrganizationSpecDAO.TABLE)
@Table(name = OrganizationSpecDAO.TABLE)
@Transactional(propagation = Propagation.REQUIRED)
public class OrganizationSpecDAO implements IAuditedDAO, IGenericDAO, IMultilangDAO {

    public static final String TABLE = "SPEC_ORGANIZATION";
    public static final String TABLE_SHORT = "SPEC_ORGAN";
    public static final String TABLE_PK_REF = TABLE_SHORT + "_PK";
    public static final String TABLE_SEQ = TABLE + "_SEQ";

    /* *************
     *   Fields    *
     ***************/

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

    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinTable(
            name = "FIELD_" + TABLE_SHORT + "_IDENTIF",
            joinColumns = @JoinColumn(name = TABLE_PK_REF),
            inverseJoinColumns = @JoinColumn(name = IdentifierDTDAO.TABLE_PK_REF))
    private Set<IdentifierDTDAO> identifier; //*

    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinTable(
            name = "FIELD_" + TABLE_SHORT + "_TYPE",
            joinColumns = @JoinColumn(name = TABLE_PK_REF),
            inverseJoinColumns = @JoinColumn(name = CodeDTDAO.TABLE_PK_REF))
    private Set<CodeDTDAO> type; //*

    @OneToOne(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinColumn(name = "PREFERRED_NAME_PK", referencedColumnName = "PK")
    private TextDTDAO preferredName; //1 mandatory (Agent only stores Orgs)

    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinTable(
            name = "FIELD_" + TABLE_SHORT + "_ALTER_NAME",
            joinColumns = @JoinColumn(name = TABLE_PK_REF),
            inverseJoinColumns = @JoinColumn(name = TextDTDAO.TABLE_PK_REF))
    private Set<TextDTDAO> alternativeName; //*

    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinTable(
            name = "FIELD_" + TABLE_SHORT + "_NOTE",
            joinColumns = @JoinColumn(name = TABLE_PK_REF),
            inverseJoinColumns = @JoinColumn(name = NoteDTDAO.TABLE_PK_REF))
    private Set<NoteDTDAO> note; //*

    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinTable(
            name = "FIELD_" + TABLE_SHORT + "_HAS_LOCAT",
            joinColumns = @JoinColumn(name = TABLE_PK_REF, nullable = false),
            inverseJoinColumns = @JoinColumn(name = LocationDCDAO.TABLE_PK_REF))
    private Set<LocationDCDAO> hasLocation; //1..*

    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinTable(
            name = "FIELD_" + TABLE_SHORT + "_CONT_POI",
            joinColumns = @JoinColumn(name = TABLE_PK_REF),
            inverseJoinColumns = @JoinColumn(name = ContactPointDCDAO.TABLE_PK_REF))
    private Set<ContactPointDCDAO> contactPoint; //*

    @OneToOne(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinColumn(name = "LEGAL_IDENTIFIER_PK", referencedColumnName = "PK")
    private IdentifierDTDAO legalIdentifier; //1

    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinTable(
            name = "FIELD_" + TABLE_SHORT + "_VAT_IDENTIF",
            joinColumns = @JoinColumn(name = TABLE_PK_REF),
            inverseJoinColumns = @JoinColumn(name = LegalIdentifierDTDAO.TABLE_PK_REF))
    private List<LegalIdentifierDTDAO> vatIdentifier; //*

    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinTable(
            name = "FIELD_" + TABLE_SHORT + "_TAX_IDENTIF",
            joinColumns = @JoinColumn(name = TABLE_PK_REF),
            inverseJoinColumns = @JoinColumn(name = LegalIdentifierDTDAO.TABLE_PK_REF))
    private List<LegalIdentifierDTDAO> taxIdentifier; //*

    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinTable(
            name = "FIELD_" + TABLE_SHORT + "_HOME_PAGE",
            joinColumns = @JoinColumn(name = TABLE_PK_REF),
            inverseJoinColumns = @JoinColumn(name = WebDocumentDCDAO.TABLE_PK_REF))
    private List<WebDocumentDCDAO> homePage; //*

    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinTable(
            name = "FIELD_" + TABLE_SHORT + "_HAS_ACCRED",
            joinColumns = @JoinColumn(name = TABLE_PK_REF),
            inverseJoinColumns = @JoinColumn(name = AccreditationDCDAO.TABLE_PK_REF))
    private List<AccreditationDCDAO> hasAccreditation; //*

    @OneToOne(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinColumn(name = "LOGO_PK", referencedColumnName = "PK")
    private MediaObjectDTDAO logo; //0..1

    /* *************
     *  Relations  *
     ***************/

    @ManyToOne(cascade = {CascadeType.REFRESH, CascadeType.DETACH})
    private OrganizationSpecDAO unitOf; //0..1

    @OneToMany(cascade = {CascadeType.REFRESH, CascadeType.DETACH}, mappedBy = "unitOf")
    private Set<OrganizationSpecDAO> hasUnit; //*

    @OneToOne(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinColumn(name = "AUDIT_PK", referencedColumnName = "PK")
    private AuditDAO auditDAO;

    public OrganizationSpecDAO() {
    }

    @Override
    public Long getPk() {
        return pk;
    }

    @Override
    public void setPk(Long pk) {
        this.pk = pk;
    }

    public Set<IdentifierDTDAO> getIdentifier() {
        return identifier;
    }

    public void setIdentifier(Set<IdentifierDTDAO> identifier) {
        this.identifier = identifier;
    }

    public Set<CodeDTDAO> getType() {
        return type;
    }

    public void setType(Set<CodeDTDAO> type) {
        this.type = type;
    }

    public TextDTDAO getPreferredName() {
        return preferredName;
    }

    public void setPreferredName(TextDTDAO preferredName) {
        this.preferredName = preferredName;
    }

    public Set<TextDTDAO> getAlternativeName() {
        return alternativeName;
    }

    public void setAlternativeName(Set<TextDTDAO> alternativeName) {
        this.alternativeName = alternativeName;
    }

    public Set<NoteDTDAO> getNote() {
        return note;
    }

    public void setNote(Set<NoteDTDAO> note) {
        this.note = note;
    }

    public Set<LocationDCDAO> getHasLocation() {
        return hasLocation;
    }

    public void setHasLocation(Set<LocationDCDAO> hasLocation) {
        this.hasLocation = hasLocation;
    }

    public Set<ContactPointDCDAO> getContactPoint() {
        return contactPoint;
    }

    public void setContactPoint(Set<ContactPointDCDAO> contactPoint) {
        this.contactPoint = contactPoint;
    }

    public IdentifierDTDAO getLegalIdentifier() {
        return legalIdentifier;
    }

    public void setLegalIdentifier(IdentifierDTDAO legalIdentifier) {
        this.legalIdentifier = legalIdentifier;
    }

    public List<LegalIdentifierDTDAO> getVatIdentifier() {
        return vatIdentifier;
    }

    public void setVatIdentifier(List<LegalIdentifierDTDAO> vatIdentifier) {
        this.vatIdentifier = vatIdentifier;
    }

    public List<LegalIdentifierDTDAO> getTaxIdentifier() {
        return taxIdentifier;
    }

    public void setTaxIdentifier(List<LegalIdentifierDTDAO> taxIdentifier) {
        this.taxIdentifier = taxIdentifier;
    }

    public List<WebDocumentDCDAO> getHomePage() {
        return homePage;
    }

    public void setHomePage(List<WebDocumentDCDAO> homePage) {
        this.homePage = homePage;
    }

    public List<AccreditationDCDAO> getHasAccreditation() {
        return hasAccreditation;
    }

    public void setHasAccreditation(List<AccreditationDCDAO> hasAccreditation) {
        this.hasAccreditation = hasAccreditation;
    }

    public MediaObjectDTDAO getLogo() {
        return logo;
    }

    public void setLogo(MediaObjectDTDAO logo) {
        this.logo = logo;
    }

    public OrganizationSpecDAO getUnitOf() {
        return unitOf;
    }

    public void setUnitOf(OrganizationSpecDAO unitOf) {
        this.unitOf = unitOf;
    }

    public Set<OrganizationSpecDAO> getHasUnit() {
        return hasUnit;
    }

    public void setHasUnit(Set<OrganizationSpecDAO> hasUnit) {
        this.hasUnit = hasUnit;
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
        OrganizationSpecDAO that = (OrganizationSpecDAO) o;
        return getPk().equals(that.getPk());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getClass(), pk);
    }

}