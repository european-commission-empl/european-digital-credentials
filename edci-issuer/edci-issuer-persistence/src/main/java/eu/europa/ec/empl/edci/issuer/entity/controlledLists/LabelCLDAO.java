package eu.europa.ec.empl.edci.issuer.entity.controlledLists;

import eu.europa.ec.empl.edci.repository.entity.IGenericDAO;

import javax.persistence.*;

@Entity(name = LabelCLDAO.TABLE)
@Table(name = LabelCLDAO.TABLE)
public class LabelCLDAO implements IGenericDAO {

    public static final String TABLE = "CL_LABEL";
    public static final String TABLE_SHORT = "CL_LABEL";
    public static final String TABLE_PK_REF = TABLE_SHORT + "_PK";
    public static final String TABLE_SEQ = TABLE + "_SEQ";

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = TABLE_SEQ)
    @SequenceGenerator(sequenceName = TABLE_SEQ, allocationSize = 1, name = TABLE_SEQ)
    @Column(name = "PK")
    private Long pk;

    @Column(name = "NAME", length = 4000)
    private String name; //1

    @Column(name = "LANG")
    private String lang; //0..1

    public LabelCLDAO() {
    }

    public LabelCLDAO(String name, String lang) {
        this.name = name;
        this.lang = lang;
    }

    @Override
    public Long getPk() {
        return pk;
    }

    @Override
    public void setPk(Long pk) {
        this.pk = pk;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }
}