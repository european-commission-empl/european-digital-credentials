package eu.europa.ec.empl.edci.issuer.entity.dataTypes;

import eu.europa.ec.empl.edci.repository.entity.IGenericDAO;

import javax.persistence.*;

@Entity(name = ScoreDTDAO.TABLE)
@Table(name = ScoreDTDAO.TABLE)
public class ScoreDTDAO implements IGenericDAO {


    public static final String TABLE = "DT_TEXT_SCORE";
    public static final String TABLE_SHORT = "DT_TEX_SCO";
    public static final String TABLE_PK_REF = TABLE_SHORT + "_PK";
    public static final String TABLE_SEQ = TABLE + "DT_SCORE_SEQ";

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = TABLE_SEQ)
    @SequenceGenerator(sequenceName = TABLE_SEQ, allocationSize = 1, name = TABLE_SEQ)
    @Column(name = "PK")
    private Long pk;

    @Column(name = "CONTENT", length = 4000)
    private String content; //1

    @Column(name = "SCORING_SCHEME_ID")
    private String scoringSchemeId; //1

    public ScoreDTDAO() {
    }

    public ScoreDTDAO(String content, String scoringSchemeId) {
        this.content = content;
        this.scoringSchemeId = scoringSchemeId;
    }

    public String getScoringSchemeId() {
        return scoringSchemeId;
    }

    public void setScoringSchemeId(String scoringSchemeId) {
        this.scoringSchemeId = scoringSchemeId;
    }

    public Long getPk() {
        return pk;
    }

    public void setPk(Long pk) {
        this.pk = pk;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}