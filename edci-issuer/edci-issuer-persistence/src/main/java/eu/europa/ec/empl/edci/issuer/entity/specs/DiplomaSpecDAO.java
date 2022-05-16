package eu.europa.ec.empl.edci.issuer.entity.specs;

import eu.europa.ec.empl.edci.issuer.entity.audit.AuditDAO;
import eu.europa.ec.empl.edci.issuer.entity.audit.AuditListener;
import eu.europa.ec.empl.edci.issuer.entity.dataTypes.LabelDTDAO;
import eu.europa.ec.empl.edci.issuer.entity.dataTypes.MediaObjectDTDAO;
import eu.europa.ec.empl.edci.repository.entity.IAuditDAO;
import eu.europa.ec.empl.edci.repository.entity.IAuditedDAO;
import eu.europa.ec.empl.edci.repository.entity.IGenericDAO;
import eu.europa.ec.empl.edci.repository.entity.IMultilangDAO;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.*;
import java.util.Objects;
import java.util.Set;

@EntityListeners(AuditListener.class)
@Entity(name = DiplomaSpecDAO.TABLE)
@Table(name = DiplomaSpecDAO.TABLE)
@Transactional(propagation = Propagation.REQUIRED)
public class DiplomaSpecDAO implements IAuditedDAO, IGenericDAO, IMultilangDAO {

    public static final String TABLE = "SPEC_DIPLOMA";
    public static final String TABLE_SHORT = "SPEC_DIPLOMA";
    public static final String TABLE_PK_REF = TABLE_SHORT + "_PK";
    public static final String TABLE_SEQ = TABLE + "_SEQ";

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = TABLE_SEQ)
    @SequenceGenerator(sequenceName = TABLE_SEQ, allocationSize = 1, name = TABLE_SEQ)
    @Column(name = "PK")
    private Long pk;

    @Column(name = "LANGUAGES", nullable = false, length = 4000)
    private Set<String> languages;

    @Column(name = "DEFAULT_TITLE", nullable = false)
    private String defaultTitle;

    @Column(name = "DEFAULT_LANGUAGE", nullable = false)
    private String defaultLanguage; //1

    /* *************
     *   Fields    *
     ***************/

    @Lob
    @Column(name = "HTML_TEMPLATE", nullable = false, length = 20000)
    private String html;

    @Column(name = "FORMAT", nullable = false, length = 20)
    private String format; //1

    @OneToOne(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinColumn(name = "BACKGROUND_PK", referencedColumnName = "PK")
    private MediaObjectDTDAO background; //0..1

    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinTable(
            name = "FIELD_" + TABLE_SHORT + "_IDENTIF",
            joinColumns = @JoinColumn(name = TABLE_PK_REF),
            inverseJoinColumns = @JoinColumn(name = LabelDTDAO.TABLE_PK_REF))
    private Set<LabelDTDAO> labels; //*

    @OneToOne(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinColumn(name = "AUDIT_PK", referencedColumnName = "PK")
    private AuditDAO auditDAO;

    public DiplomaSpecDAO() {
    }

    @Override
    public Long getPk() {
        return pk;
    }

    @Override
    public void setPk(Long pk) {
        this.pk = pk;
    }

    @Override
    public String getDefaultTitle() {
        return defaultTitle;
    }

    @Override
    public void setDefaultTitle(String defaultTitle) {
        this.defaultTitle = defaultTitle;
    }

    public MediaObjectDTDAO getBackground() {
        return background;
    }

    public void setBackground(MediaObjectDTDAO background) {
        this.background = background;
    }

    public Set<LabelDTDAO> getLabels() {
        return labels;
    }

    public void setLabels(Set<LabelDTDAO> labels) {
        this.labels = labels;
    }

    @Override
    public Set<String> getLanguages() {
        return languages;
    }

    @Override
    public void setLanguages(Set<String> languages) {
        this.languages = languages;
    }

    public String getDefaultLanguage() {
        return defaultLanguage;
    }

    public void setDefaultLanguage(String defaultLanguage) {
        this.defaultLanguage = defaultLanguage;
    }

    @Override
    public AuditDAO getAuditDAO() {
        return auditDAO;
    }

    public void setAuditDAO(AuditDAO auditDAO) {
        this.auditDAO = auditDAO;
    }

    @Override
    public void setAuditDAO(IAuditDAO auditDAO) {
        setAuditDAO((AuditDAO) auditDAO);
    }

    public String getHtml() {
        return html;
    }

    public void setHtml(String html) {
        this.html = html;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DiplomaSpecDAO that = (DiplomaSpecDAO) o;
        return getPk().equals(that.getPk());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getClass(), pk);
    }

}