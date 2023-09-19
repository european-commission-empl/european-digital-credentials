package eu.europa.ec.empl.edci.issuer.entity.specs;

import eu.europa.ec.empl.edci.issuer.entity.audit.AuditDAO;
import eu.europa.ec.empl.edci.issuer.entity.audit.AuditListener;
import eu.europa.ec.empl.edci.issuer.entity.dataContainers.ContactPointDCDAO;
import eu.europa.ec.empl.edci.issuer.entity.dataContainers.GroupDCDAO;
import eu.europa.ec.empl.edci.issuer.entity.dataContainers.LocationDCDAO;
import eu.europa.ec.empl.edci.issuer.entity.dataContainers.WebDocumentDCDAO;
import eu.europa.ec.empl.edci.issuer.entity.dataTypes.*;
import eu.europa.ec.empl.edci.repository.entity.*;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.*;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@EntityListeners(value = AuditListener.class)
@Entity(name = OrganizationSpecDAO.TABLE)
@Table(name = OrganizationSpecDAO.TABLE)
@Transactional(propagation = Propagation.REQUIRED)
public class OrganizationSpecDAO implements IAuditedDAO, IGenericDAO, IMultilangDAO, ILabeledDAO {
    public static final String TABLE = "SPEC_ORGANIZATION";
    public static final String TABLE_SHORT = "SPEC_ORGAN";
    public static final String TABLE_PK_REF = TABLE_SHORT + "_PK";
    public static final String TABLE_SEQ = TABLE + "_SEQ";

    /* *************
     *   Fields    *
     ***************/

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = TABLE_SEQ)
    @SequenceGenerator(sequenceName = TABLE_SEQ, allocationSize = 1, name = TABLE_SEQ)
    @Column(name = "PK")
    private Long pk;


    @Column(name = "LANGUAGES", nullable = false, length = 4000)
    private Set<String> languages;

    @Column(name = "LABEL", nullable = true)
    private String label;

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
    private Set<CodeDTDAO> dcType; //*

    @OneToOne(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinColumn(name = "PREFERRED_NAME_PK", referencedColumnName = "PK")
    private TextDTDAO legalName; //1 mandatory (Agent only stores Orgs)

    @OneToOne(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinColumn(name = "ALTERNATIVE_NAME_PK", referencedColumnName = "PK")
    private TextDTDAO altLabel; //*

    @Column(name = "MODIFIED_DATE")
    private Date modified; //0..1

    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinTable(
            name = "FIELD_" + TABLE_SHORT + "_NOTE",
            joinColumns = @JoinColumn(name = TABLE_PK_REF),
            inverseJoinColumns = @JoinColumn(name = NoteDTDAO.TABLE_PK_REF))
    private Set<NoteDTDAO> additionalNote; //*

    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinTable(
            name = "FIELD_" + TABLE_SHORT + "_GROUPS",
            joinColumns = @JoinColumn(name = TABLE_PK_REF),
            inverseJoinColumns = @JoinColumn(name = GroupDCDAO.TABLE_PK_REF))
    private Set<GroupDCDAO> groupMemberOf;

    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinTable(
            name = "FIELD_" + TABLE_SHORT + "_HAS_LOCAT",
            joinColumns = @JoinColumn(name = TABLE_PK_REF, nullable = false),
            inverseJoinColumns = @JoinColumn(name = LocationDCDAO.TABLE_PK_REF))
    private Set<LocationDCDAO> location; //1..*

    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinTable(
            name = "FIELD_" + TABLE_SHORT + "_CONT_POI",
            joinColumns = @JoinColumn(name = TABLE_PK_REF),
            inverseJoinColumns = @JoinColumn(name = ContactPointDCDAO.TABLE_PK_REF))
    private Set<ContactPointDCDAO> contactPoint; //*

    @OneToOne(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinColumn(name = "LEGAL_IDENTIFIER_PK", referencedColumnName = "PK")
    private IdentifierDTDAO legalIdentifier; //1

    @OneToOne(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinColumn(name = "REGISTRATION_PK", referencedColumnName = "PK")
    private LegalIdentifierDTDAO registration; //1

    @OneToOne(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinColumn(name = "EIDAS_IDENTIFIER_PK", referencedColumnName = "PK")
    private LegalIdentifierDTDAO eidasIdentifier;

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

    @OneToOne(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinColumn(name = "LOGO_PK", referencedColumnName = "PK")
    private MediaObjectDTDAO logo; //0..1

    /* *************
     *  Relations  *
     ***************/

    @OneToMany(cascade = {CascadeType.REFRESH, CascadeType.DETACH})
    @JoinTable(
            name = "FIELD_" + TABLE_SHORT + "_ACCREDITATION",
            joinColumns = @JoinColumn(name = TABLE_PK_REF),
            inverseJoinColumns = @JoinColumn(name = AccreditationSpecDAO.TABLE_PK_REF))
    private Set<AccreditationSpecDAO> accreditation; //*

    @ManyToOne(cascade = {CascadeType.REFRESH, CascadeType.DETACH})
    private OrganizationSpecDAO subOrganizationOf; //0..1

    @OneToMany(cascade = {CascadeType.REFRESH, CascadeType.DETACH}, mappedBy = "subOrganizationOf")
    private Set<OrganizationSpecDAO> childOrganisation; //*

    @OneToOne(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinColumn(name = "AUDIT_PK", referencedColumnName = "PK")
    private AuditDAO auditDAO;

    public OrganizationSpecDAO() {
    }

    public Date getModified() {
        return modified;
    }

    public void setModified(Date modified) {
        this.modified = modified;
    }

    public LegalIdentifierDTDAO getRegistration() {
        return registration;
    }

    public void setRegistration(LegalIdentifierDTDAO registration) {
        this.registration = registration;
    }

    public LegalIdentifierDTDAO getEidasIdentifier() {
        return eidasIdentifier;
    }

    public void setEidasIdentifier(LegalIdentifierDTDAO eidasIdentifier) {
        this.eidasIdentifier = eidasIdentifier;
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

    public Set<CodeDTDAO> getDcType() {
        return dcType;
    }

    public void setDcType(Set<CodeDTDAO> dcType) {
        this.dcType = dcType;
    }

    public TextDTDAO getLegalName() {
        return legalName;
    }

    public void setLegalName(TextDTDAO legalName) {
        this.legalName = legalName;
    }

    public TextDTDAO getAltLabel() {
        return altLabel;
    }

    public void setAltLabel(TextDTDAO altLabel) {
        this.altLabel = altLabel;
    }

    public Set<NoteDTDAO> getAdditionalNote() {
        return additionalNote;
    }

    public void setAdditionalNote(Set<NoteDTDAO> additionalNote) {
        this.additionalNote = additionalNote;
    }

    public Set<GroupDCDAO> getGroupMemberOf() {
        return groupMemberOf;
    }

    public void setGroupMemberOf(Set<GroupDCDAO> groupMemberOf) {
        this.groupMemberOf = groupMemberOf;
    }

    public Set<LocationDCDAO> getLocation() {
        return location;
    }

    public void setLocation(Set<LocationDCDAO> location) {
        this.location = location;
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

    public Set<AccreditationSpecDAO> getAccreditation() {
        return accreditation;
    }

    public void setAccreditation(Set<AccreditationSpecDAO> accreditation) {
        this.accreditation = accreditation;
    }

    public MediaObjectDTDAO getLogo() {
        return logo;
    }

    public void setLogo(MediaObjectDTDAO logo) {
        this.logo = logo;
    }

    public OrganizationSpecDAO getSubOrganizationOf() {
        return subOrganizationOf;
    }

    public void setSubOrganizationOf(OrganizationSpecDAO subOrganizationOf) {
        this.subOrganizationOf = subOrganizationOf;
    }

    public Set<OrganizationSpecDAO> getChildOrganisation() {
        return childOrganisation;
    }

    public void setChildOrganisation(Set<OrganizationSpecDAO> childOrganisation) {
        this.childOrganisation = childOrganisation;
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