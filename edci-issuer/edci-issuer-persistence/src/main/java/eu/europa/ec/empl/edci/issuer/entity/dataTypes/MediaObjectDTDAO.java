package eu.europa.ec.empl.edci.issuer.entity.dataTypes;

import eu.europa.ec.empl.edci.repository.entity.IGenericDAO;

import javax.persistence.*;
import java.net.URI;

@Entity(name = MediaObjectDTDAO.TABLE)
@Table(name = MediaObjectDTDAO.TABLE)
@Inheritance(
        strategy = InheritanceType.SINGLE_TABLE
)
public class MediaObjectDTDAO implements IGenericDAO {

    public static final String TABLE = "DT_MEDIA_OBJECT";
    public static final String TABLE_SHORT = "DT_MED_OBJ";
    public static final String TABLE_PK_REF = TABLE_SHORT + "_PK";
    public static final String TABLE_SEQ = TABLE + "_SEQ";

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = TABLE_SEQ)
    @SequenceGenerator(sequenceName = TABLE_SEQ, allocationSize = 1, name = TABLE_SEQ)
    @Column(name = "PK")
    private Long pk;

    @OneToOne(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinColumn(name = "CONTENT_TYPE_PK", referencedColumnName = "PK")
    private CodeDTDAO contentType; //1

    @OneToOne(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinColumn(name = "CONTENT_ENCODING_PK", referencedColumnName = "PK")
    private CodeDTDAO contentEncoding; //0..1

    @Column(name = "CONTENT_SIZE")
    private Integer contentSize; //0..1

    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(name = "CONTENT")
    private byte[] content; //1

    @Column(name = "CONTENT_URL", length = 1000)
    private URI contentUrl; //0..1

    public CodeDTDAO getContentType() {
        return contentType;
    }

    public void setContentType(CodeDTDAO contentType) {
        this.contentType = contentType;
    }

    public CodeDTDAO getContentEncoding() {
        return contentEncoding;
    }

    public void setContentEncoding(CodeDTDAO contentEncoding) {
        this.contentEncoding = contentEncoding;
    }

    public Integer getContentSize() {
        return contentSize;
    }

    public void setContentSize(Integer contentSize) {
        this.contentSize = contentSize;
    }

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }

    public URI getContentUrl() {
        return contentUrl;
    }

    public void setContentUrl(URI contentUrl) {
        this.contentUrl = contentUrl;
    }

    public Long getPk() {
        return pk;
    }

    public void setPk(Long pk) {
        this.pk = pk;
    }
}