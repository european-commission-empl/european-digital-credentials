package eu.europa.ec.empl.edci.issuer.entity.specs;

import eu.europa.ec.empl.edci.exception.EDCIException;
import eu.europa.ec.empl.edci.issuer.entity.audit.AuditDAO;
import eu.europa.ec.empl.edci.issuer.entity.audit.AuditListener;
import eu.europa.ec.empl.edci.issuer.entity.audit.OCBIdentifiedListener;
import eu.europa.ec.empl.edci.issuer.entity.dataContainers.WebDocumentDCDAO;
import eu.europa.ec.empl.edci.issuer.entity.dataTypes.CodeDTDAO;
import eu.europa.ec.empl.edci.issuer.entity.dataTypes.NoteDTDAO;
import eu.europa.ec.empl.edci.issuer.entity.dataTypes.TextDTDAO;
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
@Entity(name = AccreditationSpecDAO.TABLE)
@Table(name = AccreditationSpecDAO.TABLE)
@Transactional(propagation = Propagation.REQUIRED)
public class AccreditationSpecDAO implements IAuditedDAO, IGenericDAO, IMultilangDAO, IOCBIdentifiedDAO, ILabeledDAO {

    public static final String TABLE = "SPEC_ACCREDITATION";
    public static final String TABLE_SHORT = "SPEC_ACCR";
    public static final String TABLE_PK_REF = TABLE_SHORT + "_PK";
    public static final String TABLE_SEQ = TABLE + "_SEQ";
    public static final String ENTITY_CODE = "ACC";

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

    @Column(name = "LABEL", nullable = true)
    private String label;

    @Column(name = "DEFAULT_LANGUAGE", nullable = false)
    private String defaultLanguage; //1

    /* *************
     *   Fields    *
     ***************/

    @OneToOne(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinColumn(name = "TITLE_PK", referencedColumnName = "PK", nullable = false)
    private TextDTDAO title; //1..*

    @OneToOne(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinColumn(name = "DESCRIPTION_PK", referencedColumnName = "PK", nullable = true)
    private TextDTDAO description; //1..*

    @OneToOne(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinColumn(name = "DC_TYPE_PK", referencedColumnName = "PK", nullable = true)
    private CodeDTDAO dcType; //1..1

    @OneToOne(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinColumn(name = "REPORT_PK", referencedColumnName = "PK", nullable = true)
    private WebDocumentDCDAO report; //0..1

    @OneToOne(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinColumn(name = "STATUS_PK", referencedColumnName = "PK", nullable = true)
    private CodeDTDAO status; //0..1

    @OneToOne(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinColumn(name = "DECISION_PK", referencedColumnName = "PK", nullable = true)
    private CodeDTDAO decision; //0..1

    @Column(name = "ISSUED_DATE")
    private Date dateIssued; //0..1

    @Column(name = "EXPIRY_DATE")
    private Date expiryDate; //0..1

    @Column(name = "REVIEW_DATE")
    private Date reviewDate; //0..1

    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinTable(
            name = "FIELD_" + TABLE_SHORT + "_EQF_LEVEL",
            joinColumns = @JoinColumn(name = TABLE_PK_REF),
            inverseJoinColumns = @JoinColumn(name = CodeDTDAO.TABLE_PK_REF))
    private List<CodeDTDAO> limitEQFLevel; //0..*

    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinTable(
            name = "FIELD_" + TABLE_SHORT + "_THEMATIC_AREA",
            joinColumns = @JoinColumn(name = TABLE_PK_REF),
            inverseJoinColumns = @JoinColumn(name = CodeDTDAO.TABLE_PK_REF))
    private List<CodeDTDAO> limitField; //0..*

    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinTable(
            name = "FIELD_" + TABLE_SHORT + "_JURISDI",
            joinColumns = @JoinColumn(name = TABLE_PK_REF),
            inverseJoinColumns = @JoinColumn(name = CodeDTDAO.TABLE_PK_REF))
    private List<CodeDTDAO> limitJurisdiction; //0..*

    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinTable(
            name = "FIELD_" + TABLE_SHORT + "_OTHER_DOC",
            joinColumns = @JoinColumn(name = TABLE_PK_REF),
            inverseJoinColumns = @JoinColumn(name = WebDocumentDCDAO.TABLE_PK_REF))
    private Set<WebDocumentDCDAO> supplementaryDocument; //0..*

    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinTable(
            name = "FIELD_" + TABLE_SHORT + "_HOMEPAGE",
            joinColumns = @JoinColumn(name = TABLE_PK_REF),
            inverseJoinColumns = @JoinColumn(name = WebDocumentDCDAO.TABLE_PK_REF))
    private Set<WebDocumentDCDAO> homepage; //0..*

    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinTable(
            name = "FIELD_" + TABLE_SHORT + "_ADD_NOTE",
            joinColumns = @JoinColumn(name = TABLE_PK_REF),
            inverseJoinColumns = @JoinColumn(name = NoteDTDAO.TABLE_PK_REF))
    private List<NoteDTDAO> additionalNote; //*

    /* *************
     *  Relations  *
     ***************/

    @OneToOne(cascade = {CascadeType.REFRESH, CascadeType.DETACH})
    @JoinColumn(name = "AGENT_PK", referencedColumnName = "PK")
    private OrganizationSpecDAO accreditingAgent; //1..1

    /*@OneToOne(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinColumn(name = "ORGANISATION_PK", referencedColumnName = "PK")
    private OrganizationSpecDAO CATION_PK", referencedColumnName = "PK"organisation; //1..1*/
//
////    Learning specification should be referenced? How to display dropdown
////    @OneToOne(orphanRemoval = true, cascade = CascadeType.ALL)
////    @JoinColumn(name = "QUALIFI)
//    private LearningSpecificationDCDAO qualificationAccredited; //0..1

    @OneToOne(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinColumn(name = "AUDIT_PK", referencedColumnName = "PK")
    private AuditDAO auditDAO;

    public AccreditationSpecDAO() {
    }

    public Long getPk() {
        return pk;
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

    @Override
    public void setPk(Long pk) {
        this.pk = pk;
    }

    public TextDTDAO getTitle() {
        return title;
    }

    public void setTitle(TextDTDAO title) {
        this.title = title;
    }

    public CodeDTDAO getDcType() {
        return dcType;
    }

    public void setDcType(CodeDTDAO dcType) {
        this.dcType = dcType;
    }

    public WebDocumentDCDAO getReport() {
        return report;
    }

    public void setReport(WebDocumentDCDAO report) {
        this.report = report;
    }

    public CodeDTDAO getStatus() {
        return status;
    }

    public void setStatus(CodeDTDAO status) {
        this.status = status;
    }

    public Date getDateIssued() {
        return dateIssued;
    }

    public void setDateIssued(Date dateIssued) {
        this.dateIssued = dateIssued;
    }

    public Date getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(Date expiryDate) {
        this.expiryDate = expiryDate;
    }

    public List<CodeDTDAO> getLimitEQFLevel() {
        return limitEQFLevel;
    }

    public void setLimitEQFLevel(List<CodeDTDAO> limitEQFLevel) {
        this.limitEQFLevel = limitEQFLevel;
    }

    public List<CodeDTDAO> getLimitField() {
        return limitField;
    }

    public void setLimitField(List<CodeDTDAO> limitField) {
        this.limitField = limitField;
    }

    public List<CodeDTDAO> getLimitJurisdiction() {
        return limitJurisdiction;
    }

    public void setLimitJurisdiction(List<CodeDTDAO> limitJurisdiction) {
        this.limitJurisdiction = limitJurisdiction;
    }

    public Set<WebDocumentDCDAO> getSupplementaryDocument() {
        return supplementaryDocument;
    }

    public void setSupplementaryDocument(Set<WebDocumentDCDAO> supplementaryDocument) {
        this.supplementaryDocument = supplementaryDocument;
    }

    public OrganizationSpecDAO getAccreditingAgent() {
        return accreditingAgent;
    }

    public void setAccreditingAgent(OrganizationSpecDAO accreditingAgent) {
        this.accreditingAgent = accreditingAgent;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AccreditationSpecDAO that = (AccreditationSpecDAO) o;
        return pk.equals(that.pk);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getClass(), pk);
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
    public String getName() {
        return null;
    }

    public CodeDTDAO getDecision() {
        return decision;
    }

    public void setDecision(CodeDTDAO decision) {
        this.decision = decision;
    }

    public Date getReviewDate() {
        return reviewDate;
    }

    public void setReviewDate(Date reviewDate) {
        this.reviewDate = reviewDate;
    }

    public TextDTDAO getDescription() {
        return description;
    }

    public void setDescription(TextDTDAO description) {
        this.description = description;
    }

    public Set<WebDocumentDCDAO> getHomepage() {
        return homepage;
    }

    public void setHomepage(Set<WebDocumentDCDAO> homepage) {
        this.homepage = homepage;
    }

    public List<NoteDTDAO> getAdditionalNote() {
        return additionalNote;
    }

    public void setAdditionalNote(List<NoteDTDAO> additionalNote) {
        this.additionalNote = additionalNote;
    }
}