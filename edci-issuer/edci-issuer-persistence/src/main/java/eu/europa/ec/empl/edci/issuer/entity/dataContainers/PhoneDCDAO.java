package eu.europa.ec.empl.edci.issuer.entity.dataContainers;

import eu.europa.ec.empl.edci.repository.entity.IGenericDAO;

import javax.persistence.*;

@Entity(name = PhoneDCDAO.TABLE)
@Table(name = PhoneDCDAO.TABLE)
public class PhoneDCDAO implements IGenericDAO {

    public static final String TABLE = "DC_PHONE";
    public static final String TABLE_SHORT = "DC_PHONE";
    public static final String TABLE_PK_REF = TABLE_SHORT + "_PK";
    public static final String TABLE_SEQ = TABLE + "_SEQ";

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = TABLE_SEQ)
    @SequenceGenerator(sequenceName = TABLE_SEQ, allocationSize = 1, name = TABLE_SEQ)
    @Column(name = "PK")
    private Long pk;

    @Column(name = "PHONE_NUMBER")
    private String phoneNumber; //0..1

    @Column(name = "CONTRY_DIALING")
    private String countryDialing; //0..1

    @Column(name = "AREA_DIALING")
    private String areaDialingCode; //0..1

    @Column(name = "DIAL_NUMBER")
    private String dialNumber; //0..1

    public Long getPk() {
        return pk;
    }

    public void setPk(Long pk) {
        this.pk = pk;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getCountryDialing() {
        return countryDialing;
    }

    public void setCountryDialing(String countryDialing) {
        this.countryDialing = countryDialing;
    }

    public String getAreaDialingCode() {
        return areaDialingCode;
    }

    public void setAreaDialingCode(String areaDialingCode) {
        this.areaDialingCode = areaDialingCode;
    }

    public String getDialNumber() {
        return dialNumber;
    }

    public void setDialNumber(String dialNumber) {
        this.dialNumber = dialNumber;
    }
}