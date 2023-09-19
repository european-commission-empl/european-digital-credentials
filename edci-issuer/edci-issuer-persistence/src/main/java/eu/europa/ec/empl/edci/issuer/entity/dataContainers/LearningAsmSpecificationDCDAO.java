package eu.europa.ec.empl.edci.issuer.entity.dataContainers;

import eu.europa.ec.empl.edci.annotation.EmptiableIgnore;
import eu.europa.ec.empl.edci.issuer.entity.dataTypes.*;
import eu.europa.ec.empl.edci.issuer.entity.specs.OrganizationSpecDAO;
import eu.europa.ec.empl.edci.model.Emptiable;
import eu.europa.ec.empl.edci.repository.entity.IGenericDAO;

import javax.persistence.*;
import java.util.List;
import java.util.Set;

@Entity(name = LearningAsmSpecificationDCDAO.TABLE)
@Table(name = LearningAsmSpecificationDCDAO.TABLE)
public class LearningAsmSpecificationDCDAO implements IGenericDAO, Emptiable {

    public static final String TABLE = "DC_ASSESS_SPEC";
    public static final String TABLE_SHORT = "DC_ASSESS_S";
    public static final String TABLE_PK_REF = TABLE_SHORT + "_PK";
    public static final String TABLE_SEQ = TABLE + "_SEQ";

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = TABLE_SEQ)
    @SequenceGenerator(sequenceName = TABLE_SEQ, allocationSize = 1, name = TABLE_SEQ)
    @Column(name = "PK")
    @EmptiableIgnore
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

    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinTable(
            name = "FIELD_" + TABLE_SHORT + "_ASS_TYPE",
            joinColumns = @JoinColumn(name = TABLE_PK_REF),
            inverseJoinColumns = @JoinColumn(name = CodeDTDAO.TABLE_PK_REF))
    private List<CodeDTDAO> dcType; //*

    @OneToOne(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinColumn(name = "TITLE_PK", referencedColumnName = "PK")
    private TextDTDAO title; //0..1

    @OneToOne(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinColumn(name = "ALTERNATIVE_NAME_PK", referencedColumnName = "PK")
    private TextDTDAO altLabel; //*

    @OneToOne(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinColumn(name = "DESCRIPTION_PK", referencedColumnName = "PK")
    private NoteDTDAO description; //0..*

    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinTable(
            name = "FIELD_" + TABLE_SHORT + "_ADD_NOTE",
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

    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinTable(
            name = "FIELD_" + TABLE_SHORT + "_LANGUAGE",
            joinColumns = @JoinColumn(name = TABLE_PK_REF),
            inverseJoinColumns = @JoinColumn(name = CodeDTDAO.TABLE_PK_REF))
    private List<CodeDTDAO> language; //*

    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinTable(
            name = "FIELD_" + TABLE_SHORT + "_MODE",
            joinColumns = @JoinColumn(name = TABLE_PK_REF),
            inverseJoinColumns = @JoinColumn(name = CodeDTDAO.TABLE_PK_REF))
    private List<CodeDTDAO> mode; //*

    @OneToOne(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinColumn(name = "GRADING_SCHEMES_PK", referencedColumnName = "PK")
    private ScoringSchemeDTDAO gradingScheme; //*


    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinTable(
            name = "REL_" + TABLE_SHORT + "_PROVES",
            joinColumns = @JoinColumn(name = TABLE_PK_REF),
            inverseJoinColumns = @JoinColumn(name = LearningAchSpecificationDCDAO.TABLE_PK_REF + "_SUB"))
    private List<LearningAchSpecificationDCDAO> proves; //*

    /* *************
     *  Relations  *
     ***************/

    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinTable(
            name = "REL_" + TABLE_SHORT + "_HAS_PART",
            joinColumns = @JoinColumn(name = TABLE_PK_REF),
            inverseJoinColumns = @JoinColumn(name = LearningAsmSpecificationDCDAO.TABLE_PK_REF + "_SUB"))
    private Set<LearningAsmSpecificationDCDAO> hasPart;

    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinTable(
            name = "REL_" + TABLE_SHORT + "_SPEC_OF",
            joinColumns = @JoinColumn(name = TABLE_PK_REF),
            inverseJoinColumns = @JoinColumn(name = OrganizationSpecDAO.TABLE_PK_REF + "_SUB"))
    private Set<LearningAsmSpecificationDCDAO> specialisationOf;

    public LearningAsmSpecificationDCDAO() {
    }

    @Override
    public Long getPk() {
        return pk;
    }

    @Override
    public void setPk(Long pk) {
        this.pk = pk;
    }

    public List<IdentifierDTDAO> getIdentifier() {
        return identifier;
    }

    public void setIdentifier(List<IdentifierDTDAO> identifier) {
        this.identifier = identifier;
    }

    public List<CodeDTDAO> getDcType() {
        return dcType;
    }

    public void setDcType(List<CodeDTDAO> dcType) {
        this.dcType = dcType;
    }

    public TextDTDAO getTitle() {
        return title;
    }

    public void setTitle(TextDTDAO title) {
        this.title = title;
    }

    public TextDTDAO getAltLabel() {
        return altLabel;
    }

    public void setAltLabel(TextDTDAO altLabel) {
        this.altLabel = altLabel;
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

    public List<CodeDTDAO> getLanguage() {
        return language;
    }

    public void setLanguage(List<CodeDTDAO> language) {
        this.language = language;
    }

    public List<CodeDTDAO> getMode() {
        return mode;
    }

    public void setMode(List<CodeDTDAO> mode) {
        this.mode = mode;
    }

    public ScoringSchemeDTDAO getGradingScheme() {
        return gradingScheme;
    }

    public void setGradingScheme(ScoringSchemeDTDAO gradingScheme) {
        this.gradingScheme = gradingScheme;
    }

    public List<LearningAchSpecificationDCDAO> getProves() {
        return proves;
    }

    public void setProves(List<LearningAchSpecificationDCDAO> proves) {
        this.proves = proves;
    }

    public Set<LearningAsmSpecificationDCDAO> getHasPart() {
        return hasPart;
    }

    public void setHasPart(Set<LearningAsmSpecificationDCDAO> hasPart) {
        this.hasPart = hasPart;
    }

    public Set<LearningAsmSpecificationDCDAO> getSpecialisationOf() {
        return specialisationOf;
    }

    public void setSpecialisationOf(Set<LearningAsmSpecificationDCDAO> specialisationOf) {
        this.specialisationOf = specialisationOf;
    }
}