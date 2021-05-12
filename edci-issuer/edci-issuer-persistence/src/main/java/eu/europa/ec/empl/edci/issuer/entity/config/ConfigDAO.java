package eu.europa.ec.empl.edci.issuer.entity.config;

import eu.europa.ec.empl.edci.constants.Defaults;
import eu.europa.ec.empl.edci.repository.entity.IGenericDAO;

import javax.persistence.*;

@Entity(name = ConfigDAO.TABLE)
@Table(name = ConfigDAO.TABLE)
public class ConfigDAO implements IGenericDAO {
    public static final String TABLE = "CONFIG";
    public static final String TABLE_SHORT = "CONFIG";
    public static final String TABLE_PK_REF = TABLE_SHORT + "_PK";
    public static final String TABLE_SEQ = TABLE + "_SEQ";

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = TABLE_SEQ)
    @SequenceGenerator(sequenceName = TABLE_SEQ, allocationSize = 1, name = TABLE_SEQ)
    @Column(name = "PK")
    private Long pk;

    @Column(name = "ENVIRONMENT", nullable = false)
    private Defaults.Environment environment;

    @Column(name = "KEY", nullable = false, unique = true)
    private String key;

    @Column(name = "VALUE", nullable = false)
    private String value;

    public ConfigDAO() {


    }

    public Long getPk() {
        return pk;
    }

    public void setPk(Long pk) {
        this.pk = pk;
    }

    public Defaults.Environment getEnvironment() {
        return environment;
    }

    public void setEnvironment(Defaults.Environment environment) {
        this.environment = environment;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
