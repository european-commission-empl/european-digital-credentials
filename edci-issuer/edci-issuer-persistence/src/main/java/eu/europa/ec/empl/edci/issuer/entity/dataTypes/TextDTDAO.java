package eu.europa.ec.empl.edci.issuer.entity.dataTypes;

import eu.europa.ec.empl.edci.datamodel.model.base.ILocalizable;
import eu.europa.ec.empl.edci.repository.entity.IGenericDAO;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;


@Entity(name = TextDTDAO.TABLE)
@Table(name = TextDTDAO.TABLE)
public class TextDTDAO implements IGenericDAO, ILocalizable<ContentDTDAO> {

    public static final String TABLE = "DT_TEXT";
    public static final String TABLE_SHORT = "DT_TEXT";
    public static final String TABLE_PK_REF = TABLE_SHORT + "_PK";
    public static final String TABLE_SEQ = TABLE + "_SEQ";

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = TABLE_SEQ)
    @SequenceGenerator(sequenceName = TABLE_SEQ, allocationSize = 1, name = TABLE_SEQ)
    @Column(name = "PK")
    private Long pk;

    @Column(name = "EXTRA_FIELD", length = 1)
    private String extra;

    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinTable(
            name = "FIELD_" + TABLE_SHORT + "_CONTENTS",
            joinColumns = @JoinColumn(name = TABLE_PK_REF),
            inverseJoinColumns = @JoinColumn(name = ContentDTDAO.TABLE_PK_REF))
    private List<ContentDTDAO> contents = new ArrayList<>();

    public TextDTDAO() {

    }

    public List<ContentDTDAO> getContents() {
        return contents;
    }

    @Override
    public ContentDTDAO getContentsNewInstance() {
        return new ContentDTDAO();
    }

    public void setContents(List<ContentDTDAO> content) {
        this.contents = content;
    }

    public Long getPk() {
        return pk;
    }

    public void setPk(Long pk) {
        this.pk = pk;
    }

    public String getExtra() {
        return extra;
    }

    public void setExtra(String extra) {
        this.extra = extra;
    }
}