package eu.europa.ec.empl.edci.issuer.entity.dataTypes;

import eu.europa.ec.empl.edci.repository.entity.IGenericDAO;

import javax.persistence.*;
import java.util.List;

@Entity(name = NoteDTDAO.TABLE)
@Table(name = NoteDTDAO.TABLE)
public class NoteDTDAO implements IGenericDAO {

    public static final String TABLE = "DT_NOTE";
    public static final String TABLE_SHORT = "DT_NOTE";
    public static final String TABLE_PK_REF = TABLE_SHORT + "_PK";
    public static final String TABLE_SEQ = TABLE + "_SEQ";

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = TABLE_SEQ)
    @SequenceGenerator(sequenceName = TABLE_SEQ, allocationSize = 1, name = TABLE_SEQ)
    @Column(name = "PK")
    private Long pk;

    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinTable(
            name = "FIELD_" + TABLE_SHORT + "_CONTENTS",
            joinColumns = @JoinColumn(name = TABLE_PK_REF),
            inverseJoinColumns = @JoinColumn(name = ContentDTDAO.TABLE_PK_REF))
    private List<ContentDTDAO> contents; //?

    @Column(name = "TOPIC")
    private String topic; //?

    public List<ContentDTDAO> getContents() {
        return contents;
    }

    public void setContents(List<ContentDTDAO> contents) {
        this.contents = contents;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public Long getPk() {
        return pk;
    }

    public void setPk(Long pk) {
        this.pk = pk;
    }
}