package eu.europa.ec.empl.edci.issuer.entity.dataTypes;

import eu.europa.ec.empl.edci.repository.entity.IGenericDAO;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Set;

@Entity(name = IdentifierDTDAO.TABLE)
@Table(name = IdentifierDTDAO.TABLE)
@Inheritance(
        strategy = InheritanceType.SINGLE_TABLE
)
public class IdentifierDTDAO implements IGenericDAO {

    public static final String TABLE = "DT_IDENTIFIER";
    public static final String TABLE_SHORT = "DT_IDENT";
    public static final String TABLE_PK_REF = TABLE_SHORT + "_PK";
    public static final String TABLE_SEQ = TABLE + "_SEQ";

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = TABLE_SEQ)
    @SequenceGenerator(sequenceName = TABLE_SEQ, allocationSize = 1, name = TABLE_SEQ)
    @Column(name = "PK")
    private Long pk;

    @Column(name = "CONTENT", nullable = false)
    private String notation; //1

    @Column(name = "IDENT_SCHEME_ID")
    private String creator; //0..1

    @OneToOne(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinColumn(name = "IDENT_SCHEME_AGENCY_PK", referencedColumnName = "PK")
    private TextDTDAO schemeAgency; //0..1

    @Column(name = "IDENT_SCHEME_NAME")
    private String schemeName; //0..1

    @Column(name = "ISSUED_DATE")
    private LocalDate dateIssued; //0..1

    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinTable(
            name = "FIELD_" + TABLE_SHORT + "_TYPE",
            joinColumns = @JoinColumn(name = TABLE_PK_REF),
            inverseJoinColumns = @JoinColumn(name = CodeDTDAO.TABLE_PK_REF))
    private Set<CodeDTDAO> dcType; //*

    public String getNotation() {
        return notation;
    }

    public void setNotation(String notation) {
        this.notation = notation;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public TextDTDAO getSchemeAgency() {
        return schemeAgency;
    }

    public void setSchemeAgency(TextDTDAO schemeAgency) {
        this.schemeAgency = schemeAgency;
    }

    public LocalDate getDateIssued() {
        return dateIssued;
    }

    public void setDateIssued(LocalDate dateIssued) {
        this.dateIssued = dateIssued;
    }

    public Set<CodeDTDAO> getDcType() {
        return dcType;
    }

    public void setDcType(Set<CodeDTDAO> dcType) {
        this.dcType = dcType;
    }

    public Long getPk() {
        return pk;
    }

    public void setPk(Long pk) {
        this.pk = pk;
    }

    public String getSchemeName() {
        return schemeName;
    }

    public void setSchemeName(String schemeName) {
        this.schemeName = schemeName;
    }
}