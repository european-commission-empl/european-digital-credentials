package eu.europa.ec.empl.edci.issuer.entity.dataTypes;

import eu.europa.ec.empl.edci.datamodel.MultilangText;
import eu.europa.ec.empl.edci.model.dataTypes.IContent;
import eu.europa.ec.empl.edci.repository.entity.IGenericDAO;

import javax.persistence.*;
import javax.ws.rs.core.MediaType;

@Entity(name = ContentDTDAO.TABLE)
@Table(name = ContentDTDAO.TABLE)
public class ContentDTDAO implements IGenericDAO, IContent, MultilangText {

    public static final String TABLE = "DT_CONTENT";
    public static final String TABLE_SHORT = "DT_CONT";
    public static final String TABLE_PK_REF = TABLE_SHORT + "_PK";
    public static final String TABLE_SEQ = TABLE + "_SEQ";

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = TABLE_SEQ)
    @SequenceGenerator(sequenceName = TABLE_SEQ, allocationSize = 1, name = TABLE_SEQ)
    @Column(name = "PK")
    private Long pk;

    @Column(name = "CONTENT", length = 4000, nullable = false)
    private String content; //1

    @Column(name = "LANGUAGE", nullable = false)
    private String language; //0..1

    @Column(name = "FORMAT")
    private String format = MediaType.TEXT_PLAIN; //0..1

    public ContentDTDAO() {
    }

    public ContentDTDAO(String content) {
        this.content = content;
    }

    public ContentDTDAO(String content, String language, String format) {
        this.content = content;
        this.language = language;
        this.format = format;
    }

    public ContentDTDAO(String content, String language) {
        this.content = content;
        this.language = language;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public Long getPk() {
        return pk;
    }

    public void setPk(Long pk) {
        this.pk = pk;
    }

}
