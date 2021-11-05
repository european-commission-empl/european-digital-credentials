package eu.europa.ec.empl.edci.issuer.entity.dataContainers;

import eu.europa.ec.empl.edci.annotation.EmptiableIgnore;
import eu.europa.ec.empl.edci.issuer.entity.dataTypes.CodeDTDAO;
import eu.europa.ec.empl.edci.issuer.entity.dataTypes.IdentifierDTDAO;
import eu.europa.ec.empl.edci.issuer.entity.dataTypes.NoteDTDAO;
import eu.europa.ec.empl.edci.issuer.entity.dataTypes.TextDTDAO;
import eu.europa.ec.empl.edci.repository.entity.IGenericDAO;
import org.joda.time.Period;

import javax.persistence.*;
import java.util.List;
import java.util.Set;

@Entity(name = LearningActSpecificationDCDAO.TABLE)
@Table(name = LearningActSpecificationDCDAO.TABLE)
public class LearningActSpecificationDCDAO implements IGenericDAO {

    public static final String TABLE = "DC_LEARNING_ACT_SPEC";
    public static final String TABLE_SHORT = "DC_LE_ACT_S";
    public static final String TABLE_PK_REF = TABLE_SHORT + "_PK";
    public static final String TABLE_SEQ = TABLE + "_SEQ";

    /* *************
     *   Fields    *
     ***************/

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = TABLE_SEQ)
    @SequenceGenerator(sequenceName = TABLE_SEQ, allocationSize = 1, name = TABLE_SEQ)
    @Column(name = "PK")
    @EmptiableIgnore
    private Long pk;

    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinTable(
            name = "FIELD_" + TABLE_SHORT + "_IDENTIFIER",
            joinColumns = @JoinColumn(name = TABLE_PK_REF),
            inverseJoinColumns = @JoinColumn(name = IdentifierDTDAO.TABLE_PK_REF))
    private Set<IdentifierDTDAO> identifier; //*

    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinTable(
            name = "FIELD_" + TABLE_SHORT + "_TYPE",
            joinColumns = @JoinColumn(name = TABLE_PK_REF),
            inverseJoinColumns = @JoinColumn(name = CodeDTDAO.TABLE_PK_REF))
    private Set<CodeDTDAO> learningActivityType; //*

    @OneToOne(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinColumn(name = "TITLE_PK", referencedColumnName = "PK")
    private TextDTDAO title; //0..1

    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinTable(
            name = "FIELD_" + TABLE_SHORT + "_ALTERN_LABEL",
            joinColumns = @JoinColumn(name = TABLE_PK_REF),
            inverseJoinColumns = @JoinColumn(name = TextDTDAO.TABLE_PK_REF))
    private List<TextDTDAO> alternativeLabel; //*

    @OneToOne(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinColumn(name = "DESCRIPTION_PK", referencedColumnName = "PK")
    private NoteDTDAO description; //0..1

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

    @Column(name = "WORKLOAD")
    private Period workload; //0..1

    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinTable(
            name = "FIELD_" + TABLE_SHORT + "_LANGUAGE",
            joinColumns = @JoinColumn(name = TABLE_PK_REF),
            inverseJoinColumns = @JoinColumn(name = TextDTDAO.TABLE_PK_REF))
    private List<CodeDTDAO> language; //*

    @OneToOne(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinColumn(name = "MODE_PK", referencedColumnName = "PK")
    private CodeDTDAO mode; //0..1

    /* *************
     *  Relations  *
     ***************/

    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinTable(
            name = "FIELD_" + TABLE_SHORT + "_TEACHES",
            joinColumns = @JoinColumn(name = TABLE_PK_REF),
            inverseJoinColumns = @JoinColumn(name = LearningSpecificationDCDAO.TABLE_PK_REF))
    private Set<LearningSpecificationDCDAO> teaches; //*

    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinTable(
            name = "FIELD_" + TABLE_SHORT + "_HAS_PART",
            joinColumns = @JoinColumn(name = TABLE_PK_REF),
            inverseJoinColumns = @JoinColumn(name = LearningActSpecificationDCDAO.TABLE_PK_REF))
    private Set<LearningActSpecificationDCDAO> hasPart; //*

    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinTable(
            name = "FIELD_" + TABLE_SHORT + "_SPEC_OF",
            joinColumns = @JoinColumn(name = TABLE_PK_REF),
            inverseJoinColumns = @JoinColumn(name = LearningActSpecificationDCDAO.TABLE_PK_REF))
    private Set<LearningActSpecificationDCDAO> specialisationOf; //*

    public Long getPk() {
        return pk;
    }

    public void setPk(Long pk) {
        this.pk = pk;
    }

    public Set<IdentifierDTDAO> getIdentifier() {
        return identifier;
    }

    public void setIdentifier(Set<IdentifierDTDAO> identifier) {
        this.identifier = identifier;
    }

    public Set<CodeDTDAO> getLearningActivityType() {
        return learningActivityType;
    }

    public void setLearningActivityType(Set<CodeDTDAO> learningActivityType) {
        this.learningActivityType = learningActivityType;
    }

    public TextDTDAO getTitle() {
        return title;
    }

    public void setTitle(TextDTDAO title) {
        this.title = title;
    }

    public List<TextDTDAO> getAlternativeLabel() {
        return alternativeLabel;
    }

    public void setAlternativeLabel(List<TextDTDAO> alternativeLabel) {
        this.alternativeLabel = alternativeLabel;
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

    public Period getWorkload() {
        return workload;
    }

    public void setWorkload(Period workload) {
        this.workload = workload;
    }

    public List<CodeDTDAO> getLanguage() {
        return language;
    }

    public void setLanguage(List<CodeDTDAO> language) {
        this.language = language;
    }

    public CodeDTDAO getMode() {
        return mode;
    }

    public void setMode(CodeDTDAO mode) {
        this.mode = mode;
    }

    public Set<LearningSpecificationDCDAO> getTeaches() {
        return teaches;
    }

    public void setTeaches(Set<LearningSpecificationDCDAO> teaches) {
        this.teaches = teaches;
    }

    public Set<LearningActSpecificationDCDAO> getHasPart() {
        return hasPart;
    }

    public void setHasPart(Set<LearningActSpecificationDCDAO> hasPart) {
        this.hasPart = hasPart;
    }

    public Set<LearningActSpecificationDCDAO> getSpecialisationOf() {
        return specialisationOf;
    }

    public void setSpecialisationOf(Set<LearningActSpecificationDCDAO> specialisationOf) {
        this.specialisationOf = specialisationOf;
    }
}