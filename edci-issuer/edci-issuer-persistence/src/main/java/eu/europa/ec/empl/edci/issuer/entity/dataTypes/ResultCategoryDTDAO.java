package eu.europa.ec.empl.edci.issuer.entity.dataTypes;

import eu.europa.ec.empl.edci.repository.entity.IGenericDAO;

import javax.persistence.*;
import java.util.List;


@Entity(name = ResultCategoryDTDAO.TABLE)
@Table(name = ResultCategoryDTDAO.TABLE)
public class ResultCategoryDTDAO implements IGenericDAO {

    public static final String TABLE = "DT_RESULT_CATEGORY";
    public static final String TABLE_SHORT = "DT_SHO_GRAD";
    public static final String TABLE_PK_REF = TABLE_SHORT + "_PK";
    public static final String TABLE_SEQ = TABLE + "_SEQ";

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = TABLE_SEQ)
    @SequenceGenerator(sequenceName = TABLE_SEQ, allocationSize = 1, name = TABLE_SEQ)
    @Column(name = "PK")
    private Long pk;

    @Column(name = "LABEL")
    private String label; //1

    @Column(name = "SCORE")
    private String score; //0..1

    @Column(name = "MIN_SCORE")
    private String minScore; //0..1

    @Column(name = "MAX_SCORE")
    private String maxScore; //0..1

    @Column(name = "COUNT")
    private Integer count; //1

    @OneToOne(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinColumn(name = "DESCRIPTION_FK", referencedColumnName = "PK")
    private NoteDTDAO description;

    public ResultCategoryDTDAO() {

    }

    public NoteDTDAO getDescription() {
        return description;
    }

    public void setDescription(NoteDTDAO description) {
        this.description = description;
    }

    public Long getPk() {
        return pk;
    }

    public void setPk(Long pk) {
        this.pk = pk;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public String getMinScore() {
        return minScore;
    }

    public void setMinScore(String minScore) {
        this.minScore = minScore;
    }

    public String getMaxScore() {
        return maxScore;
    }

    public void setMaxScore(String maxScore) {
        this.maxScore = maxScore;
    }
}