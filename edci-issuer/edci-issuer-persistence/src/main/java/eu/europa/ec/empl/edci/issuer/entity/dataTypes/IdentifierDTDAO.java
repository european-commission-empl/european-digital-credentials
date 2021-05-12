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
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = TABLE_SEQ)
    @SequenceGenerator(sequenceName = TABLE_SEQ, allocationSize = 1, name = TABLE_SEQ)
    @Column(name = "PK")
    private Long pk;

    @Column(name = "CONTENT", nullable = false)
    private String content; //1

    @Column(name = "IDENT_SCHEME_ID")
    private String identifierSchemeId; //0..1

    @Column(name = "IDENT_SCHEME_AGENCY_NAME")
    private String identifierSchemeAgencyName; //0..1

    @Column(name = "ISSUED_DATE")
    private LocalDate issuedDate; //0..1

    @Column(name = "IDENTIFIER_TYPE", columnDefinition = "VARCHAR2(4000)")
    private Set<String> identifierType; //*

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getIdentifierSchemeId() {
        return identifierSchemeId;
    }

    public void setIdentifierSchemeId(String identifierSchemeId) {
        this.identifierSchemeId = identifierSchemeId;
    }

    public String getIdentifierSchemeAgencyName() {
        return identifierSchemeAgencyName;
    }

    public void setIdentifierSchemeAgencyName(String identifierSchemeAgencyName) {
        this.identifierSchemeAgencyName = identifierSchemeAgencyName;
    }

    public LocalDate getIssuedDate() {
        return issuedDate;
    }

    public void setIssuedDate(LocalDate issuedDate) {
        this.issuedDate = issuedDate;
    }

    public Set<String> getIdentifierType() {
        return identifierType;
    }

    public void setIdentifierType(Set<String> identifierType) {
        this.identifierType = identifierType;
    }

    public Long getPk() {
        return pk;
    }

    public void setPk(Long pk) {
        this.pk = pk;
    }
}