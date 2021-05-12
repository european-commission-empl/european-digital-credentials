package eu.europa.ec.empl.edci.issuer.entity.dataTypes;

import eu.europa.ec.empl.edci.repository.entity.IGenericDAO;

import javax.persistence.*;
import java.util.List;


@Entity(name = ResultDistributionDTDAO.TABLE)
@Table(name = ResultDistributionDTDAO.TABLE)
public class ResultDistributionDTDAO implements IGenericDAO {

    public static final String TABLE = "DT_RESULT_DISTRIB";
    public static final String TABLE_SHORT = "DT_RES_DIST";
    public static final String TABLE_PK_REF = TABLE_SHORT + "_PK";
    public static final String TABLE_SEQ = TABLE + "_SEQ";

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = TABLE_SEQ)
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
    @JoinColumn(name = "DESCRIPTION_PK", referencedColumnName = "PK")
    private NoteDTDAO description;

    public ResultDistributionDTDAO() {

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
}