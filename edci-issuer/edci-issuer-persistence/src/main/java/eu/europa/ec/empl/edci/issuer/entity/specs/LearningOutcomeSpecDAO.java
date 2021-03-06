package eu.europa.ec.empl.edci.issuer.entity.specs;

import eu.europa.ec.empl.edci.issuer.entity.audit.AuditDAO;
import eu.europa.ec.empl.edci.issuer.entity.audit.AuditListener;
import eu.europa.ec.empl.edci.issuer.entity.dataTypes.CodeDTDAO;
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

/**
 * The type Global history dao.
 */
@EntityListeners(AuditListener.class)
@Entity(name = LearningOutcomeSpecDAO.TABLE)
@Table(name = LearningOutcomeSpecDAO.TABLE)
@Transactional(propagation = Propagation.REQUIRED)
public class LearningOutcomeSpecDAO implements IAuditedDAO, IGenericDAO, IMultilangDAO {

    public static final String TABLE = "SPEC_LEARNING_OUTCOME";
    public static final String TABLE_SHORT = "SPEC_LEAR_OUTC";
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
    @JoinColumn(name = "NAME_PK", referencedColumnName = "PK", nullable = false)
    private TextDTDAO name; //1

    @OneToOne(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinColumn(name = "DESCRIPTION_PK", referencedColumnName = "PK")
    private NoteDTDAO description; //0..1

    @OneToOne(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinColumn(name = "LERNING_OUTCOME_TYPE_PK", referencedColumnName = "PK")
    private CodeDTDAO learningOutcomeType; //0..1

    @OneToOne(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinColumn(name = "REUSABILITY_LEVEL_PK", referencedColumnName = "PK")
    private CodeDTDAO reusabilityLevel; //0..1

    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinTable(
            name = "REL_" + TABLE_SHORT + "_ESCO_SKILL",
            joinColumns = @JoinColumn(name = TABLE_PK_REF),
            inverseJoinColumns = @JoinColumn(name = CodeDTDAO.TABLE_PK_REF))
    private List<CodeDTDAO> relatedESCOSkill; //*

    /* *************
     *  Relations  *
     ***************/

    @OneToOne(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinColumn(name = "AUDIT_PK", referencedColumnName = "PK")
    private AuditDAO auditDAO;

    public LearningOutcomeSpecDAO() {

    }

    @Override
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

    public TextDTDAO getName() {
        return name;
    }

    public void setName(TextDTDAO name) {
        this.name = name;
    }

    public NoteDTDAO getDescription() {
        return description;
    }

    public void setDescription(NoteDTDAO description) {
        this.description = description;
    }

    public CodeDTDAO getLearningOutcomeType() {
        return learningOutcomeType;
    }

    public void setLearningOutcomeType(CodeDTDAO learningOutcomeType) {
        this.learningOutcomeType = learningOutcomeType;
    }

    public CodeDTDAO getReusabilityLevel() {
        return reusabilityLevel;
    }

    public void setReusabilityLevel(CodeDTDAO reusabilityLevel) {
        this.reusabilityLevel = reusabilityLevel;
    }

    public List<CodeDTDAO> getRelatedESCOSkill() {
        return relatedESCOSkill;
    }

    public void setRelatedESCOSkill(List<CodeDTDAO> relatedESCOSkill) {
        this.relatedESCOSkill = relatedESCOSkill;
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
        LearningOutcomeSpecDAO that = (LearningOutcomeSpecDAO) o;
        return pk.equals(that.pk);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getClass(), pk);
    }
}

