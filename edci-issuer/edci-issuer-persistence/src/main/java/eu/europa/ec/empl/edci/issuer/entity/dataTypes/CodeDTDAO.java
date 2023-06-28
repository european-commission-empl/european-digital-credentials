package eu.europa.ec.empl.edci.issuer.entity.dataTypes;

import eu.europa.ec.empl.edci.repository.entity.IGenericDAO;

import javax.persistence.*;

@Entity(name = CodeDTDAO.TABLE)
@Table(name = CodeDTDAO.TABLE)
public class CodeDTDAO implements IGenericDAO { //EDCI-751 for more info

    public static final String TABLE = "DT_CODE";
    public static final String TABLE_SHORT = "DT_CODE";
    public static final String TABLE_PK_REF = TABLE_SHORT + "_PK";
    public static final String TABLE_SEQ = TABLE + "_SEQ";

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = TABLE_SEQ)
    @SequenceGenerator(sequenceName = TABLE_SEQ, allocationSize = 1, name = TABLE_SEQ)
    @Column(name = "PK")
    private Long pk;

    @Column(name = "CL_URI", length = 4000)
    private String uri; //1

    @OneToOne(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinColumn(name = "TARGET_NAME_PK", referencedColumnName = "PK")
    private TextDTDAO targetName; //0..1

    @OneToOne(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinColumn(name = "TARGET_DESC_PK", referencedColumnName = "PK")
    private TextDTDAO targetDescription; //0..1

    @Column(name = "TARGET_FRAMEWORK_URI")
    private String targetFrameworkURI; //0..1

    @Column(name = "TARGET_NOTATION")
    private String targetNotation; //0..1

    @OneToOne(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinColumn(name = "TARGET_FRAMEWORK_PK", referencedColumnName = "PK")
    private TextDTDAO targetFramework; //0..1

    public CodeDTDAO() {
    }

    public CodeDTDAO(String targetFrameworkURI, String uri, TextDTDAO targetName) {
        this.targetFrameworkURI = targetFrameworkURI;
        this.uri = uri;
        this.targetName = targetName;
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

    public TextDTDAO getTargetName() {
        return targetName;
    }

    public void setTargetName(TextDTDAO targetName) {
        this.targetName = targetName;
    }

    // TODO
    public TextDTDAO getTargetDescription() {
        if (targetDescription == null || targetDescription.getContents() == null || targetDescription.getContents().isEmpty()) {
            return null;
        }
        return targetDescription;
    }

    public void setTargetDescription(TextDTDAO targetDescription) {
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

    public TextDTDAO getTargetFramework() {
        return targetFramework;
    }

    public void setTargetFramework(TextDTDAO targetFramework) {
        this.targetFramework = targetFramework;
    }
}