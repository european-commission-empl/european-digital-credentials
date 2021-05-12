package eu.europa.ec.empl.edci.issuer.entity.dataContainers;

import eu.europa.ec.empl.edci.issuer.entity.dataTypes.*;
import eu.europa.ec.empl.edci.issuer.entity.specs.OrganizationSpecDAO;
import eu.europa.ec.empl.edci.repository.entity.IGenericDAO;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity(name = AccreditationDCDAO.TABLE)
@Table(name = AccreditationDCDAO.TABLE)
@Transactional(propagation = Propagation.REQUIRED)
public class AccreditationDCDAO implements IGenericDAO {

    public static final String TABLE = "DC_ACCREDITATION";
    public static final String TABLE_SHORT = "SPEC_ACCRED";
    public static final String TABLE_PK_REF = TABLE_SHORT + "_PK";
    public static final String TABLE_SEQ = TABLE + "_SEQ";

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = TABLE_SEQ)
    @SequenceGenerator(sequenceName = TABLE_SEQ, allocationSize = 1, name = TABLE_SEQ)
    @Column(name = "PK")
    private Long pk;

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
    @JoinColumn(name = "ACCRED_TYPE_PK", referencedColumnName = "PK")
    private CodeDTDAO accreditationType; //1

    @OneToOne(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinColumn(name = "TITLE_PK", referencedColumnName = "PK")
    private TextDTDAO title; //0..1

    @OneToOne(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinColumn(name = "DESCRIPTION_PK", referencedColumnName = "PK")
    private NoteDTDAO description; //*

    @OneToOne(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinColumn(name = "DECISION_PK", referencedColumnName = "PK")
    private ScoreDTDAO decision; //0..1

    @OneToOne(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinColumn(name = "REPORT_PK", referencedColumnName = "PK")
    private WebDocumentDCDAO report; //0..1

    @OneToOne(cascade = {CascadeType.REFRESH, CascadeType.DETACH})
    @JoinColumn(name = "ORGANIZATION_PK", referencedColumnName = "PK")
    private OrganizationSpecDAO organization; //1

//    @OneToOne(cascade = CascadeType.ALL)
//    @JoinColumn(name = "LIMIT_QUALIFICATION_PK", referencedColumnName = "PK")
//    private QualificationDCDAO limitQualification; //*

    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinTable(
            name = "FIELD_" + TABLE_SHORT + "_LIMIT_FIELD",
            joinColumns = @JoinColumn(name = TABLE_PK_REF),
            inverseJoinColumns = @JoinColumn(name = CodeDTDAO.TABLE_PK_REF))
    private List<CodeDTDAO> limitField; //*

    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinTable(
            name = "FIELD_" + TABLE_SHORT + "_LIMIT_EQF_LV",
            joinColumns = @JoinColumn(name = TABLE_PK_REF),
            inverseJoinColumns = @JoinColumn(name = CodeDTDAO.TABLE_PK_REF))
    private List<CodeDTDAO> limitEqfLevel; //*

    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinTable(
            name = "FIELD_" + TABLE_SHORT + "_LIMIT_JURISD",
            joinColumns = @JoinColumn(name = TABLE_PK_REF),
            inverseJoinColumns = @JoinColumn(name = CodeDTDAO.TABLE_PK_REF))
    private List<CodeDTDAO> limitJurisdiction; //*

    @OneToOne(cascade = {CascadeType.REFRESH, CascadeType.DETACH})
    @JoinColumn(name = "ACCREDITING_AGENT_PK", referencedColumnName = "PK")
    private OrganizationSpecDAO accreditingAgent; //1

    @Column(name = "ISSUE_DATE")
    private Date issueDate; //0..1

    @Column(name = "REVIEW_DATE")
    private Date reviewDate; //0..1

    @Column(name = "EXPIRIY_DATE")
    private Date expiryDate; //0..1

    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinTable(
            name = "FIELD_" + TABLE_SHORT + "_ADDIT_NOTE",
            joinColumns = @JoinColumn(name = TABLE_PK_REF),
            inverseJoinColumns = @JoinColumn(name = NoteDTDAO.TABLE_PK_REF))
    private List<NoteDTDAO> additionalNote; //*

    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinTable(
            name = "FIELD_" + TABLE_SHORT + "_HOME_PAGE",
            joinColumns = @JoinColumn(name = TABLE_PK_REF),
            inverseJoinColumns = @JoinColumn(name = WebDocumentDCDAO.TABLE_PK_REF))
    private List<WebDocumentDCDAO> homePage; //*

    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinTable(
            name = "FIELD_" + TABLE_SHORT + "_SUPPL_DOC",
            joinColumns = @JoinColumn(name = TABLE_PK_REF),
            inverseJoinColumns = @JoinColumn(name = WebDocumentDCDAO.TABLE_PK_REF))
    private List<WebDocumentDCDAO> supplementaryDocument; //*

    public CodeDTDAO getAccreditationType() {
        return accreditationType;
    }

    public void setAccreditationType(CodeDTDAO accreditationType) {
        this.accreditationType = accreditationType;
    }

    public Long getPk() {
        return pk;
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

    public NoteDTDAO getDescription() {
        return description;
    }

    public void setDescription(NoteDTDAO description) {
        this.description = description;
    }

    public ScoreDTDAO getDecision() {
        return decision;
    }

    public void setDecision(ScoreDTDAO decision) {
        this.decision = decision;
    }

    public WebDocumentDCDAO getReport() {
        return report;
    }

    public void setReport(WebDocumentDCDAO report) {
        this.report = report;
    }

    public OrganizationSpecDAO getOrganization() {
        return organization;
    }

    public void setOrganization(OrganizationSpecDAO organization) {
        this.organization = organization;
    }

//    public QualificationDCDAO getLimitQualification() {
//        return limitQualification;
//    }
//
//    public void setLimitQualification(QualificationDCDAO limitQualification) {
//        this.limitQualification = limitQualification;
//    }

    public List<CodeDTDAO> getLimitField() {
        return limitField;
    }

    public void setLimitField(List<CodeDTDAO> limitField) {
        this.limitField = limitField;
    }

    public List<CodeDTDAO> getLimitEqfLevel() {
        return limitEqfLevel;
    }

    public void setLimitEqfLevel(List<CodeDTDAO> limitEqfLevel) {
        this.limitEqfLevel = limitEqfLevel;
    }

    public List<CodeDTDAO> getLimitJurisdiction() {
        return limitJurisdiction;
    }

    public void setLimitJurisdiction(List<CodeDTDAO> limitJurisdiction) {
        this.limitJurisdiction = limitJurisdiction;
    }

    public OrganizationSpecDAO getAccreditingAgent() {
        return accreditingAgent;
    }

    public void setAccreditingAgent(OrganizationSpecDAO accreditingAgent) {
        this.accreditingAgent = accreditingAgent;
    }

    public Date getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(Date issueDate) {
        this.issueDate = issueDate;
    }

    public Date getReviewDate() {
        return reviewDate;
    }

    public void setReviewDate(Date reviewDate) {
        this.reviewDate = reviewDate;
    }

    public Date getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(Date expiryDate) {
        this.expiryDate = expiryDate;
    }

    public List<NoteDTDAO> getAdditionalNote() {
        return additionalNote;
    }

    public void setAdditionalNote(List<NoteDTDAO> additionalNote) {
        this.additionalNote = additionalNote;
    }

    public List<WebDocumentDCDAO> getHomePage() {
        return homePage;
    }

    public void setHomePage(List<WebDocumentDCDAO> homePage) {
        this.homePage = homePage;
    }

    public List<WebDocumentDCDAO> getSupplementaryDocument() {
        return supplementaryDocument;
    }

    public void setSupplementaryDocument(List<WebDocumentDCDAO> supplementaryDocument) {
        this.supplementaryDocument = supplementaryDocument;
    }
}