package eu.europa.ec.empl.edci.issuer.entity.controlledLists;

import eu.europa.ec.empl.edci.repository.entity.IGenericDAO;

import javax.persistence.*;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Entity(name = ElementCLDAO.TABLE)
@Table(name = ElementCLDAO.TABLE)
public class ElementCLDAO implements IGenericDAO {

    public static final String TABLE = "CL_ELEMENT";
    public static final String TABLE_SHORT = "CL_ELEM";
    public static final String TABLE_PK_REF = TABLE_SHORT + "_PK";
    public static final String TABLE_SEQ = TABLE + "_SEQ";

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = TABLE_SEQ)
    @SequenceGenerator(sequenceName = TABLE_SEQ, allocationSize = 1, name = TABLE_SEQ)
    @Column(name = "PK")
    private Long pk;

    @Column(name = "URI", length = 4000, nullable = false)
    private String uri; //1

    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinTable(
            name = "FIELD_" + TABLE_SHORT + "_TARGET_NAME",
            joinColumns = @JoinColumn(name = TABLE_PK_REF),
            inverseJoinColumns = @JoinColumn(name = LabelCLDAO.TABLE_PK_REF))
    private List<LabelCLDAO> targetName; //1

    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinTable(
            name = "FIELD_" + TABLE_SHORT + "_TARGET_DESC",
            joinColumns = @JoinColumn(name = TABLE_PK_REF),
            inverseJoinColumns = @JoinColumn(name = LabelCLDAO.TABLE_PK_REF))
    private List<LabelCLDAO> targetDescription; //0..1

    @Column(name = "TARGET_FRAMEWORK_URI", nullable = false)
    private String targetFrameworkURI; //1

    @Column(name = "TARGET_NOTATION")
    private String targetNotation; //0..1

    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinTable(
            name = "FIELD_" + TABLE_SHORT + "_TARGET_FRWK",
            joinColumns = @JoinColumn(name = TABLE_PK_REF),
            inverseJoinColumns = @JoinColumn(name = LabelCLDAO.TABLE_PK_REF))
    private List<LabelCLDAO> targetFramework; //0..1

    @Column(name = "LAST_UPDATED")
    private Date lastUpdated; //0..1

    @Column(name = "DEPRECATED_SINCE")
    private Date deprecatedSince; //0..1

    @Column(name = "EXTERNAL_RESOURCE", nullable = true)
    private String external;

    public ElementCLDAO() {
    }

    @Override
    public Long getPk() {
        return pk;
    }

    @Override
    public void setPk(Long pk) {
        this.pk = pk;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public List<LabelCLDAO> getTargetName() {
        return targetName;
    }

    public void setTargetName(List<LabelCLDAO> targetName) {
        this.targetName = targetName;
    }

    public List<LabelCLDAO> getTargetDescription() {
        return targetDescription;
    }

    public void setTargetDescription(List<LabelCLDAO> targetDescription) {
        this.targetDescription = targetDescription;
    }

    public String getTargetFrameworkURI() {
        return targetFrameworkURI;
    }

    public void setTargetFrameworkURI(String targetFrameworkURI) {
        this.targetFrameworkURI = targetFrameworkURI;
    }

    public String getTargetNotation() {
        return targetNotation;
    }

    public void setTargetNotation(String targetNotation) {
        this.targetNotation = targetNotation;
    }

    public List<LabelCLDAO> getTargetFramework() {
        return targetFramework;
    }

    public void setTargetFramework(List<LabelCLDAO> targetFramework) {
        this.targetFramework = targetFramework;
    }

    public Date getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(Date lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public Date getDeprecatedSince() {
        return deprecatedSince;
    }

    public void setDeprecatedSince(Date deprecatedSince) {
        this.deprecatedSince = deprecatedSince;
    }

    public String getExternal() {
        return external;
    }

    public void setExternal(String external) {
        this.external = external;
    }

    public List<String> getTargetNames() {
        try {
            return this.getTargetName().stream().map(labelCLDAO -> labelCLDAO.getName()).collect(Collectors.toList());
        } catch (Exception e) {
            return null;
        }
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ElementCLDAO that = (ElementCLDAO) o;
        return Objects.equals(pk, that.pk);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getClass(), pk);
    }
}