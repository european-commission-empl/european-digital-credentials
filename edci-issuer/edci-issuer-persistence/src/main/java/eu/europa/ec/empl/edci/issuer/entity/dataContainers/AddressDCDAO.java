package eu.europa.ec.empl.edci.issuer.entity.dataContainers;

import eu.europa.ec.empl.edci.issuer.entity.dataTypes.CodeDTDAO;
import eu.europa.ec.empl.edci.issuer.entity.dataTypes.IdentifierDTDAO;
import eu.europa.ec.empl.edci.issuer.entity.dataTypes.NoteDTDAO;
import eu.europa.ec.empl.edci.repository.entity.IGenericDAO;

import javax.persistence.*;
import java.util.List;

@Entity(name = AddressDCDAO.TABLE)
@Table(name = AddressDCDAO.TABLE)
public class AddressDCDAO implements IGenericDAO {

    public static final String TABLE = "DC_ADDRESS";
    public static final String TABLE_SHORT = "DC_ADDR";
    public static final String TABLE_PK_REF = TABLE_SHORT + "_PK";
    public static final String TABLE_SEQ = TABLE + "_SEQ";

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = TABLE_SEQ)
    @SequenceGenerator(sequenceName = TABLE_SEQ, allocationSize = 1, name = TABLE_SEQ)
    @Column(name = "PK")
    private Long pk;

    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinTable(
            name = "FIELD_" + TABLE_SHORT + "_IDENTIFIER",
            joinColumns = @JoinColumn(name = TABLE_PK_REF),
            inverseJoinColumns = @JoinColumn(name = IdentifierDTDAO.TABLE_PK_REF))
    private List<IdentifierDTDAO> identifier; //*

    @OneToOne(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinColumn(name = "FULL_ADDRESS_PK", referencedColumnName = "PK")
    private NoteDTDAO fullAddress; //0..1

    @OneToOne(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinColumn(name = "COUNTRY_CODE_PK", referencedColumnName = "PK")
    private CodeDTDAO countryCode; //1

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

    public NoteDTDAO getFullAddress() {
        return fullAddress;
    }

    public void setFullAddress(NoteDTDAO fullAddress) {
        this.fullAddress = fullAddress;
    }

    public CodeDTDAO getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(CodeDTDAO countryCode) {
        this.countryCode = countryCode;
    }
}