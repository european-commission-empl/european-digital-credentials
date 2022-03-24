package eu.europa.ec.empl.edci.issuer.entity.dataTypes;

import eu.europa.ec.empl.edci.repository.entity.IGenericDAO;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;


@Entity(name = LabelDTDAO.TABLE)
@Table(name = LabelDTDAO.TABLE)
public class LabelDTDAO implements IGenericDAO {

    public static final String TABLE = "DT_LABELS";
    public static final String TABLE_SHORT = "DT_LABELS";
    public static final String TABLE_PK_REF = TABLE_SHORT + "_PK";
    public static final String TABLE_SEQ = TABLE + "_SEQ";

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = TABLE_SEQ)
    @SequenceGenerator(sequenceName = TABLE_SEQ, allocationSize = 1, name = TABLE_SEQ)
    @Column(name = "PK")
    private Long pk;

    @Column(name = "LABEL_KEY", nullable = false)
    private String key; //1

    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinTable(
            name = "FIELD_" + TABLE_SHORT + "_CONTENTS",
            joinColumns = @JoinColumn(name = TABLE_PK_REF),
            inverseJoinColumns = @JoinColumn(name = ContentDTDAO.TABLE_PK_REF))
    private List<ContentDTDAO> contents = new ArrayList<>();

    public LabelDTDAO() {

    }

    public List<ContentDTDAO> getContents() {
        return contents;
    }

    public void setContents(List<ContentDTDAO> content) {
        this.contents = content;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Long getPk() {
        return pk;
    }

    public void setPk(Long pk) {
        this.pk = pk;
    }
}