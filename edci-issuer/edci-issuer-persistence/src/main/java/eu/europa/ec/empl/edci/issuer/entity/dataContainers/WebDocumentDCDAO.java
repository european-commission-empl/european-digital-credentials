package eu.europa.ec.empl.edci.issuer.entity.dataContainers;

import eu.europa.ec.empl.edci.issuer.entity.dataTypes.CodeDTDAO;
import eu.europa.ec.empl.edci.issuer.entity.dataTypes.TextDTDAO;
import eu.europa.ec.empl.edci.repository.entity.IGenericDAO;

import javax.persistence.*;
import java.net.URI;

@Entity(name = WebDocumentDCDAO.TABLE)
@Table(name = WebDocumentDCDAO.TABLE)
public class WebDocumentDCDAO implements IGenericDAO {

    public static final String TABLE = "DC_WEB_DOCUMENT";
    public static final String TABLE_SHORT = "DC_WEB_DOC";
    public static final String TABLE_PK_REF = TABLE_SHORT + "_PK";
    public static final String TABLE_SEQ = TABLE + "_SEQ";

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = TABLE_SEQ)
    @SequenceGenerator(sequenceName = TABLE_SEQ, allocationSize = 1, name = TABLE_SEQ)
    @Column(name = "PK")
    private Long pk;

    /* *************
     *   Fields    *
     ***************/

    @Column(name = "CONTENT")
    private URI contentUrl; //1

    @OneToOne(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinColumn(name = "TITLE_PK", referencedColumnName = "PK")
    private TextDTDAO title; //0..1

    @OneToOne(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinColumn(name = "LANGUAGE_PK", referencedColumnName = "PK")
    private CodeDTDAO language; //0..1

    public URI getContentUrl() {
        return contentUrl;
    }

    public void setContentUrl(URI contentUrl) {
        this.contentUrl = contentUrl;
    }

    public TextDTDAO getTitle() {
        return title;
    }

    public void setTitle(TextDTDAO title) {
        this.title = title;
    }

    public CodeDTDAO getLanguage() {
        return language;
    }

    public void setLanguage(CodeDTDAO language) {
        this.language = language;
    }

    public Long getPk() {
        return pk;
    }

    public void setPk(Long pk) {
        this.pk = pk;
    }
}