package eu.europa.ec.empl.edci.issuer.entity.specs;

import eu.europa.ec.empl.edci.issuer.entity.audit.AuditDAO;
import eu.europa.ec.empl.edci.issuer.entity.audit.AuditListener;
import eu.europa.ec.empl.edci.issuer.entity.dataContainers.AwardingProcessDCDAO;
import eu.europa.ec.empl.edci.issuer.entity.dataContainers.LearningSpecificationDCDAO;
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
import java.util.List;
import java.util.Objects;
import java.util.Set;

@EntityListeners(AuditListener.class)
@Entity(name = LearningAchievementSpecDAO.TABLE)
@Table(name = LearningAchievementSpecDAO.TABLE)
@Transactional(propagation = Propagation.REQUIRED)
public class LearningAchievementSpecDAO implements IAuditedDAO, IGenericDAO, IMultilangDAO {

    public static final String TABLE = "SPEC_LEARNING_ACHIEVEMENT";
    public static final String TABLE_SHORT = "SPEC_L_ACHI";
    public static final String TABLE_PK_REF = TABLE_SHORT + "_PK";
    public static final String TABLE_SEQ = TABLE + "_SEQ";

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = TABLE_SEQ)
    @SequenceGenerator(sequenceName = TABLE_SEQ, allocationSize = 1, name = TABLE_SEQ)
    @Column(name = "PK")
    private Long pk;

    //    @Convert(converter = StringCollectionConverter.class)
    @Column(name = "LANGUAGES", columnDefinition = "VARCHAR2(4000)", nullable = false)
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

    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinTable(
            name = "FIELD_" + TABLE_SHORT + "_ADD_NOTE",
            joinColumns = @JoinColumn(name = TABLE_PK_REF),
            inverseJoinColumns = @JoinColumn(name = NoteDTDAO.TABLE_PK_REF))
    private List<NoteDTDAO> additionalNote; //*

    @OneToOne(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinColumn(name = "AWARDED_BY_PK", referencedColumnName = "PK")
    private AwardingProcessDCDAO wasAwardedBy; //0..1

    @OneToOne(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinColumn(name = "SPECIFIED_BY_PK", referencedColumnName = "PK")
    LearningSpecificationDCDAO specifiedBy; //1

//    @OneToOne(orphanRemoval = true, cascade = CascadeType.ALL)
//    @JoinColumn(name = "ASS_LEARN_OPPORT_BY_PK", referencedColumnName = "PK")
//    LearningSpecificationDCDAO associatedLearningOpportunity; //0..1

    /* *************
     *  Relations  *
     ***************/

    @ManyToMany(cascade = {CascadeType.REFRESH, CascadeType.DETACH})
    @JoinTable(
            name = "REL_" + TABLE_SHORT + "_HAS_PART_ACH",
            joinColumns = @JoinColumn(name = TABLE_PK_REF),
            inverseJoinColumns = @JoinColumn(name = TABLE_PK_REF + "_SUB"))
    private Set<LearningAchievementSpecDAO> hasPart; //*

    @ManyToMany(cascade = {CascadeType.REFRESH, CascadeType.DETACH})
    @JoinTable(
            name = "REL_" + TABLE_SHORT + "_INFLUE_BY",
            joinColumns = @JoinColumn(name = TABLE_PK_REF),
            inverseJoinColumns = @JoinColumn(name = LearningActivitySpecDAO.TABLE_PK_REF))
    private Set<LearningActivitySpecDAO> wasInfluencedBy; //*

    @ManyToMany(cascade = {CascadeType.REFRESH, CascadeType.DETACH})
    @JoinTable(
            name = "REL_" + TABLE_SHORT + "_DERIVED_FROM",
            joinColumns = @JoinColumn(name = TABLE_PK_REF),
            inverseJoinColumns = @JoinColumn(name = AssessmentSpecDAO.TABLE_PK_REF))
    private Set<AssessmentSpecDAO> wasDerivedFrom; //*

    @ManyToMany(cascade = {CascadeType.REFRESH, CascadeType.DETACH})
    @JoinTable(
            name = "REL_" + TABLE_SHORT + "_ENTITLES_TO",
            joinColumns = @JoinColumn(name = TABLE_PK_REF),
            inverseJoinColumns = @JoinColumn(name = EntitlementSpecDAO.TABLE_PK_REF))
    private Set<EntitlementSpecDAO> entitlesTo; //*

    @OneToOne(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinColumn(name = "AUDIT_PK", referencedColumnName = "PK")
    private AuditDAO auditDAO;

    public LearningAchievementSpecDAO() {
    }

    public Long getPk() {
        return pk;
    }

    public AwardingProcessDCDAO getWasAwardedBy() {
        return wasAwardedBy;
    }

    public void setWasAwardedBy(AwardingProcessDCDAO wasAwardedBy) {
        this.wasAwardedBy = wasAwardedBy;
    }


    public Set<EntitlementSpecDAO> getEntitlesTo() {
        return entitlesTo;
    }

    public void setEntitlesTo(Set<EntitlementSpecDAO> entitlesTo) {
        this.entitlesTo = entitlesTo;
    }

    public LearningSpecificationDCDAO getSpecifiedBy() {
        return specifiedBy;
    }

    public void setSpecifiedBy(LearningSpecificationDCDAO specifiedBy) {
        this.specifiedBy = specifiedBy;
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

    public Set<LearningAchievementSpecDAO> getHasPart() {
        return hasPart;
    }

    public void setHasPart(Set<LearningAchievementSpecDAO> hasPart) {
        this.hasPart = hasPart;
    }

    public Set<LearningActivitySpecDAO> getWasInfluencedBy() {
        return wasInfluencedBy;
    }

    public void setWasInfluencedBy(Set<LearningActivitySpecDAO> wasInfluencedBy) {
        this.wasInfluencedBy = wasInfluencedBy;
    }

    public Set<AssessmentSpecDAO> getWasDerivedFrom() {
        return wasDerivedFrom;
    }

    public void setWasDerivedFrom(Set<AssessmentSpecDAO> wasDerivedFrom) {
        this.wasDerivedFrom = wasDerivedFrom;
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
        LearningAchievementSpecDAO that = (LearningAchievementSpecDAO) o;
        return pk.equals(that.pk);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getClass(), pk);
    }
}