package eu.europa.ec.empl.edci.issuer.entity.specs;

import eu.europa.ec.empl.edci.issuer.entity.audit.AuditDAO;
import eu.europa.ec.empl.edci.issuer.entity.audit.AuditListener;
import eu.europa.ec.empl.edci.issuer.entity.dataContainers.AssessmSpecificationDCDAO;
import eu.europa.ec.empl.edci.issuer.entity.dataTypes.*;
import eu.europa.ec.empl.edci.repository.entity.IAuditDAO;
import eu.europa.ec.empl.edci.repository.entity.IAuditedDAO;
import eu.europa.ec.empl.edci.repository.entity.IGenericDAO;
import eu.europa.ec.empl.edci.repository.entity.IMultilangDAO;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.*;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@EntityListeners(AuditListener.class)
@Entity(name = AssessmentSpecDAO.TABLE)
@Table(name = AssessmentSpecDAO.TABLE)
@Transactional(propagation = Propagation.REQUIRED)
public class AssessmentSpecDAO implements IAuditedDAO, IGenericDAO, IMultilangDAO {

    public static final String TABLE = "SPEC_ASSESSMENT";
    public static final String TABLE_SHORT = "SPEC_ASS";
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
    private TextDTDAO description; //0..1

    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinTable(
            name = "FIELD_" + TABLE_SHORT + "_ADD_NOTE",
            joinColumns = @JoinColumn(name = TABLE_PK_REF),
            inverseJoinColumns = @JoinColumn(name = NoteDTDAO.TABLE_PK_REF))
    private List<NoteDTDAO> additionalNote; //*


    @OneToOne(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinColumn(name = "GRADE_PK", referencedColumnName = "PK")
    private ScoreDTDAO grade; //1

    @OneToOne(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinColumn(name = "SHORT_GRADING_PK", referencedColumnName = "PK")
    private ShortenedGradingDTDAO shortenedGrading; //0..1

    @OneToOne(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinColumn(name = "RESULT_DISTRIB_PK", referencedColumnName = "PK")
    private ResultDistributionDTDAO resultDistribution; //0..1

    @Column(name = "ISSUED_DATE")
    private Date issuedDate; //0..1

    @OneToOne(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinColumn(name = "ID_VERIFICATION_PK", referencedColumnName = "PK")
    private CodeDTDAO idVerification; //0..1

    @OneToOne(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinColumn(name = "SPECIFIED_BY_PK", referencedColumnName = "PK")
    private AssessmSpecificationDCDAO specifiedBy; //0..1

    /* *************
     *  Relations  *
     ***************/

    @ManyToMany(cascade = {CascadeType.REFRESH, CascadeType.DETACH})
    @JoinTable(
            name = "REL_" + TABLE_SHORT + "_HAS_PART",
            joinColumns = @JoinColumn(name = TABLE_PK_REF),
            inverseJoinColumns = @JoinColumn(name = AssessmentSpecDAO.TABLE_PK_REF + "_SUB"))
    private Set<AssessmentSpecDAO> hasPart;

    @ManyToMany(cascade = {CascadeType.REFRESH, CascadeType.DETACH})
    @JoinTable(
            name = "REL_" + TABLE_SHORT + "_SPEC_OF",
            joinColumns = @JoinColumn(name = TABLE_PK_REF),
            inverseJoinColumns = @JoinColumn(name = OrganizationSpecDAO.TABLE_PK_REF + "_SUB"))
    private Set<OrganizationSpecDAO> assessedBy;

    @OneToOne(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinColumn(name = "AUDIT_PK", referencedColumnName = "PK")
    private AuditDAO auditDAO;

    public AssessmentSpecDAO() {
    }

    @Override
    public Long getPk() {
        return pk;
    }

    @Override
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

    public TextDTDAO getDescription() {
        return description;
    }

    public void setDescription(TextDTDAO description) {
        this.description = description;
    }

    public List<NoteDTDAO> getAdditionalNote() {
        return additionalNote;
    }

    public void setAdditionalNote(List<NoteDTDAO> additionalNote) {
        this.additionalNote = additionalNote;
    }

    public ScoreDTDAO getGrade() {
        return grade;
    }

    public void setGrade(ScoreDTDAO grade) {
        this.grade = grade;
    }

    public ShortenedGradingDTDAO getShortenedGrading() {
        return shortenedGrading;
    }

    public void setShortenedGrading(ShortenedGradingDTDAO shortenedGrading) {
        this.shortenedGrading = shortenedGrading;
    }

    public ResultDistributionDTDAO getResultDistribution() {
        return resultDistribution;
    }

    public void setResultDistribution(ResultDistributionDTDAO resultDistribution) {
        this.resultDistribution = resultDistribution;
    }

    public Date getIssuedDate() {
        return issuedDate;
    }

    public void setIssuedDate(Date issuedDate) {
        this.issuedDate = issuedDate;
    }

    public CodeDTDAO getIdVerification() {
        return idVerification;
    }

    public void setIdVerification(CodeDTDAO idVerification) {
        this.idVerification = idVerification;
    }

    public AssessmSpecificationDCDAO getSpecifiedBy() {
        return specifiedBy;
    }

    public void setSpecifiedBy(AssessmSpecificationDCDAO specifiedBy) {
        this.specifiedBy = specifiedBy;
    }

    public Set<AssessmentSpecDAO> getHasPart() {
        return hasPart;
    }

    public void setHasPart(Set<AssessmentSpecDAO> hasPart) {
        this.hasPart = hasPart;
    }

    public Set<OrganizationSpecDAO> getAssessedBy() {
        return assessedBy;
    }

    public void setAssessedBy(Set<OrganizationSpecDAO> assessedBy) {
        this.assessedBy = assessedBy;
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
        AssessmentSpecDAO that = (AssessmentSpecDAO) o;
        return pk.equals(that.pk);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getClass(), pk);
    }
}