package eu.europa.ec.empl.edci.issuer.entity.dataTypes;

import eu.europa.ec.empl.edci.issuer.entity.dataContainers.WebDocumentDCDAO;
import eu.europa.ec.empl.edci.repository.entity.IGenericDAO;

import javax.persistence.*;
import java.util.List;
import java.util.Set;

@Entity(name = GradingSchemeDTDAO.TABLE)
@Table(name = GradingSchemeDTDAO.TABLE)
public class GradingSchemeDTDAO implements IGenericDAO {

    public static final String TABLE = "DT_GRADING_SCHEME";
    public static final String TABLE_SHORT = "DT_GRADING";
    public static final String TABLE_PK_REF = TABLE_SHORT + "_PK";
    public static final String TABLE_SEQ = TABLE + "_SEQ";

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = TABLE_SEQ)
    @SequenceGenerator(sequenceName = TABLE_SEQ, allocationSize = 1, name = TABLE_SEQ)
    @Column(name = "PK")
    private Long pk;

    /* *************
     *   Fields    *
     ***************/

    @OneToOne(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinColumn(name = "DESCRIPTION_PK", referencedColumnName = "PK")
    private TextDTDAO description; //0..*

    @OneToOne(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinColumn(name = "TITLE_PK", referencedColumnName = "PK")
    private TextDTDAO title; //0..1

    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinTable(
            name = "FIELD_" + TABLE_SHORT + "_SUPPL_DOC",
            joinColumns = @JoinColumn(name = TABLE_PK_REF),
            inverseJoinColumns = @JoinColumn(name = WebDocumentDCDAO.TABLE_PK_REF))
    private Set<WebDocumentDCDAO> supplementaryDocument; //*

    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinTable(
            name = "FIELD_" + TABLE_SHORT + "_IDENTIF",
            joinColumns = @JoinColumn(name = TABLE_PK_REF),
            inverseJoinColumns = @JoinColumn(name = WebDocumentDCDAO.TABLE_PK_REF))
    private List<IdentifierDTDAO> identifier; //0..1

    public Long getPk() {
        return pk;
    }

    public void setPk(Long pk) {
        this.pk = pk;
    }

    public TextDTDAO getDescription() {
        return description;
    }

    public void setDescription(TextDTDAO description) {
        this.description = description;
    }

    public TextDTDAO getTitle() {
        return title;
    }

    public void setTitle(TextDTDAO title) {
        this.title = title;
    }

    public Set<WebDocumentDCDAO> getSupplementaryDocument() {
        return supplementaryDocument;
    }

    public void setSupplementaryDocument(Set<WebDocumentDCDAO> supplementaryDocument) {
        this.supplementaryDocument = supplementaryDocument;
    }

    public List<IdentifierDTDAO> getIdentifier() {
        return identifier;
    }

    public void setIdentifier(List<IdentifierDTDAO> identifier) {
        this.identifier = identifier;
    }
}
