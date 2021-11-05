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

    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinTable(
            name = "FIELD_" + TABLE_SHORT + "_CATEGORY",
            joinColumns = @JoinColumn(name = TABLE_PK_REF),
            inverseJoinColumns = @JoinColumn(name = ResultCategoryDTDAO.TABLE_PK_REF))
    private List<ResultCategoryDTDAO> category;

    @OneToOne(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinColumn(name = "LABEL_PK", referencedColumnName = "PK")
    private TextDTDAO label; //1

    @OneToOne(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinColumn(name = "SCORE_FK", referencedColumnName = "PK")
    private ScoreDTDAO score; //0..1

    @OneToOne(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinColumn(name = "MIN_SCORE_FK", referencedColumnName = "PK")
    private ScoreDTDAO minScore; //0..1

    @OneToOne(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinColumn(name = "MAX_SCORE_FK", referencedColumnName = "PK")
    private ScoreDTDAO maxScore; //0..1

    @Column(name = "COUNT")
    private Integer count; //1

    @OneToOne(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinColumn(name = "DESCRIPTION_FK", referencedColumnName = "PK")
    private NoteDTDAO description;

    public ResultCategoryDTDAO() {

    }

    public List<ResultCategoryDTDAO> getCategory() {
        return category;
    }

    public void setCategory(List<ResultCategoryDTDAO> category) {
        this.category = category;
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

    public TextDTDAO getLabel() {
        return label;
    }

    public void setLabel(TextDTDAO label) {
        this.label = label;
    }

    public ScoreDTDAO getScore() {
        return score;
    }

    public void setScore(ScoreDTDAO score) {
        this.score = score;
    }

    public ScoreDTDAO getMinScore() {
        return minScore;
    }

    public void setMinScore(ScoreDTDAO minScore) {
        this.minScore = minScore;
    }

    public ScoreDTDAO getMaxScore() {
        return maxScore;
    }

    public void setMaxScore(ScoreDTDAO maxScore) {
        this.maxScore = maxScore;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }
}