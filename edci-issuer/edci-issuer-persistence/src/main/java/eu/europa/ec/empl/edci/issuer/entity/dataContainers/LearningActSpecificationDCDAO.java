package eu.europa.ec.empl.edci.issuer.entity.dataContainers;

import eu.europa.ec.empl.edci.annotation.EmptiableIgnore;
import eu.europa.ec.empl.edci.issuer.entity.dataTypes.CodeDTDAO;
import eu.europa.ec.empl.edci.issuer.entity.dataTypes.IdentifierDTDAO;
import eu.europa.ec.empl.edci.issuer.entity.dataTypes.NoteDTDAO;
import eu.europa.ec.empl.edci.issuer.entity.dataTypes.TextDTDAO;
import eu.europa.ec.empl.edci.model.Emptiable;
import eu.europa.ec.empl.edci.repository.entity.IGenericDAO;
import org.joda.time.Period;

import javax.persistence.*;
import java.util.List;
import java.util.Set;

@Entity(name = LearningActSpecificationDCDAO.TABLE)
@Table(name = LearningActSpecificationDCDAO.TABLE)
public class LearningActSpecificationDCDAO implements IGenericDAO, Emptiable {

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

    @ElementCollection
    @CollectionTable(name = "FIELD_" + TABLE_SHORT + "_CONTA_HOURS")
    private List<String> contactHours; //0..*

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
    private Set<CodeDTDAO> dcType; //*

    @OneToOne(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinColumn(name = "TITLE_PK", referencedColumnName = "PK")
    private TextDTDAO title; //0..1

    @OneToOne(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinColumn(name = "ALTERNATIVE_NAME_PK", referencedColumnName = "PK")
    private TextDTDAO altLabel; //*

    @OneToOne(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinColumn(name = "DESCRIPTION_TEXT_PK", referencedColumnName = "PK")
    private TextDTDAO description; //0..*

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
    private List<WebDocumentDCDAO> homepage; //*

    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinTable(
            name = "FIELD_" + TABLE_SHORT + "_SUPPL_DOC",
            joinColumns = @JoinColumn(name = TABLE_PK_REF),
            inverseJoinColumns = @JoinColumn(name = WebDocumentDCDAO.TABLE_PK_REF))
    private List<WebDocumentDCDAO> supplementaryDocument; //*

    @Column(name = "WORKLOAD")
    private Period volumeOfLearning; //0..1

    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinTable(
            name = "FIELD_" + TABLE_SHORT + "_LANGUAGE",
            joinColumns = @JoinColumn(name = TABLE_PK_REF),
            inverseJoinColumns = @JoinColumn(name = TextDTDAO.TABLE_PK_REF))
    private List<CodeDTDAO> language; //*

    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinTable(
            name = "FIELD_" + TABLE_SHORT + "_MODE",
            joinColumns = @JoinColumn(name = TABLE_PK_REF),
            inverseJoinColumns = @JoinColumn(name = CodeDTDAO.TABLE_PK_REF))
    private List<CodeDTDAO> mode; //0..1

    /* *************
     *  Relations  *
     ***************/

    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinTable(
            name = "FIELD_" + TABLE_SHORT + "_TEACHES",
            joinColumns = @JoinColumn(name = TABLE_PK_REF),
            inverseJoinColumns = @JoinColumn(name = LearningAchSpecificationDCDAO.TABLE_PK_REF))
    private Set<LearningAchSpecificationDCDAO> teaches; //*

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

    public Set<CodeDTDAO> getDcType() {
        return dcType;
    }

    public void setDcType(Set<CodeDTDAO> dcType) {
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

    public List<WebDocumentDCDAO> getHomepage() {
        return homepage;
    }

    public void setHomepage(List<WebDocumentDCDAO> homepage) {
        this.homepage = homepage;
    }

    public List<WebDocumentDCDAO> getSupplementaryDocument() {
        return supplementaryDocument;
    }

    public void setSupplementaryDocument(List<WebDocumentDCDAO> supplementaryDocument) {
        this.supplementaryDocument = supplementaryDocument;
    }

    public Period getVolumeOfLearning() {
        return volumeOfLearning;
    }

    public void setVolumeOfLearning(Period volumeOfLearning) {
        this.volumeOfLearning = volumeOfLearning;
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

    public Set<LearningAchSpecificationDCDAO> getTeaches() {
        return teaches;
    }

    public void setTeaches(Set<LearningAchSpecificationDCDAO> teaches) {
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

    public List<String> getContactHours() {
        return contactHours;
    }

    public void setContactHours(List<String> contactHours) {
        this.contactHours = contactHours;
    }
}