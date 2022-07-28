package eu.europa.ec.empl.edci.issuer.entity.dataContainers;

import eu.europa.ec.empl.edci.issuer.common.annotation.CustomizableEntity;
import eu.europa.ec.empl.edci.issuer.entity.dataTypes.CodeDTDAO;
import eu.europa.ec.empl.edci.issuer.entity.dataTypes.IdentifierDTDAO;
import eu.europa.ec.empl.edci.issuer.entity.dataTypes.NoteDTDAO;
import eu.europa.ec.empl.edci.issuer.entity.dataTypes.TextDTDAO;
import eu.europa.ec.empl.edci.repository.entity.IGenericDAO;

import javax.persistence.*;
import java.util.List;


@Entity(name = LocationDCDAO.TABLE)
@Table(name = LocationDCDAO.TABLE)
//@CustomizableEntity(identifierField = "location", entityCode = "LOC")
public class LocationDCDAO implements IGenericDAO {

    public static final String TABLE = "DC_LOCATION";
    public static final String TABLE_SHORT = "DC_LOCAT";
    public static final String TABLE_PK_REF = TABLE_SHORT + "_PK";
    public static final String TABLE_SEQ = TABLE + "_SEQ";

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = TABLE_SEQ)
    @SequenceGenerator(sequenceName = TABLE_SEQ, allocationSize = 1, name = TABLE_SEQ)
    @Column(name = "PK")
    private Long pk;

    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinTable(
            name = "DT_LOCATION_DT_IDENTIFIER",
            joinColumns = @JoinColumn(name = "DT_LOCATION_PK"),
            inverseJoinColumns = @JoinColumn(name = "DT_IDENTIFIER_PK"))
    private List<IdentifierDTDAO> identifier; //*

    @OneToOne(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinColumn(name = "GEOGRAPHIC_NAME_PK", referencedColumnName = "PK")
    private TextDTDAO geographicName; //0..1

    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinTable(
            name = "DT_LOCATION_SPATIAL_CODE",
            joinColumns = @JoinColumn(name = "DT_LOCATION_PK"),
            inverseJoinColumns = @JoinColumn(name = "DT_CODE_PK"))
    private List<CodeDTDAO> spatialCode; //*

    @OneToOne(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinColumn(name = "DECRIPTION_PK", referencedColumnName = "PK")
    private NoteDTDAO description; //0..1

    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinTable(
            name = "REL_" + TABLE_SHORT + "_HAS_ADDR",
            joinColumns = @JoinColumn(name = TABLE_PK_REF),
            inverseJoinColumns = @JoinColumn(name = AddressDCDAO.TABLE_PK_REF))
    private List<AddressDCDAO> hasAddress; //0..1

    public LocationDCDAO() {

    }

    public Long getPk() {
        return pk;
    }

    public void setPk(Long pk) {
        this.pk = pk;
    }

    public List<AddressDCDAO> getHasAddress() {
        return hasAddress;
    }

    public void setHasAddress(List<AddressDCDAO> hasAddress) {
        this.hasAddress = hasAddress;
    }

    public List<IdentifierDTDAO> getIdentifier() {
        return identifier;
    }

    public void setIdentifier(List<IdentifierDTDAO> identifier) {
        this.identifier = identifier;
    }

    public TextDTDAO getGeographicName() {
        return geographicName;
    }

    public void setGeographicName(TextDTDAO geographicName) {
        this.geographicName = geographicName;
    }

    public List<CodeDTDAO> getSpatialCode() {
        return spatialCode;
    }

    public void setSpatialCode(List<CodeDTDAO> spatialCode) {
        this.spatialCode = spatialCode;
    }

    public NoteDTDAO getDescription() {
        return description;
    }

    public void setDescription(NoteDTDAO description) {
        this.description = description;
    }

//    public List<AddressDCDAO> getHasAddress() {
//        return hasAddress;
//    }
//
//    public void setHasAddress(List<AddressDCDAO> hasAddress) {
//        this.hasAddress = hasAddress;
//    }
}