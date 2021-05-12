package eu.europa.ec.empl.edci.issuer.entity.dataContainers;

import eu.europa.ec.empl.edci.issuer.entity.dataTypes.CodeDTDAO;
import eu.europa.ec.empl.edci.issuer.entity.dataTypes.IdentifierDTDAO;
import eu.europa.ec.empl.edci.issuer.entity.specs.OrganizationSpecDAO;
import eu.europa.ec.empl.edci.repository.entity.IGenericDAO;

import javax.persistence.*;
import java.util.Date;
import java.util.List;
import java.util.Set;


@Entity(name = AwardingOpportunityDCDAO.TABLE)
@Table(name = AwardingOpportunityDCDAO.TABLE)
public class AwardingOpportunityDCDAO implements IGenericDAO {

    public static final String TABLE = "DC_AWARD_OPPORTUNITY";
    public static final String TABLE_SHORT = "DC_AW_OP";
    public static final String TABLE_PK_REF = TABLE_SHORT + "_PK";
    public static final String TABLE_SEQ = TABLE + "_SEQ";

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = TABLE_SEQ)
    @SequenceGenerator(sequenceName = TABLE_SEQ, allocationSize = 1, name = TABLE_SEQ)
    @Column(name = "PK")
    private Long pk;

    @OneToOne(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinColumn(name = "IDENTIFIER_PK", referencedColumnName = "PK")
    private List<IdentifierDTDAO> identifier; //*

    @OneToOne(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinColumn(name = "LOCATION_PK", referencedColumnName = "PK")
    private CodeDTDAO location; //0..1

    @Column(name = "STARTED_AT_TIME")
    private Date startedAtTime; //0..1

    @Column(name = "ENDED_AT_TIME")
    private Date endedAtTime; //0..1

    /* *************
     *  Relations  *
     ***************/

    @OneToOne(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinColumn(name = "AWARD_LEAR_SPECIF_PK", referencedColumnName = "PK")
    private LearningSpecificationDCDAO awardedLearningSpecification; //1

    @ManyToMany(cascade = {CascadeType.REFRESH, CascadeType.DETACH})
    @JoinTable(
            name = "FIELD_" + TABLE_SHORT + "_AWARDING_BODY",
            joinColumns = @JoinColumn(name = TABLE_PK_REF),
            inverseJoinColumns = @JoinColumn(name = OrganizationSpecDAO.TABLE_PK_REF))
    private Set<OrganizationSpecDAO> awardingBody; //*

    public AwardingOpportunityDCDAO() {

    }

    public Long getPk() {
        return pk;
    }

    public void setPk(Long pk) {
        this.pk = pk;
    }

    public void setAwardingBody(Set<OrganizationSpecDAO> awardingBody) {
        this.awardingBody = awardingBody;
    }

    public List<IdentifierDTDAO> getIdentifier() {
        return identifier;
    }

    public void setIdentifier(List<IdentifierDTDAO> identifier) {
        this.identifier = identifier;
    }

    public LearningSpecificationDCDAO getAwardedLearningSpecification() {
        return awardedLearningSpecification;
    }

    public void setAwardedLearningSpecification(LearningSpecificationDCDAO awardedLearningSpecification) {
        this.awardedLearningSpecification = awardedLearningSpecification;
    }

    public CodeDTDAO getLocation() {
        return location;
    }

    public void setLocation(CodeDTDAO location) {
        this.location = location;
    }

    public Date getStartedAtTime() {
        return startedAtTime;
    }

    public void setStartedAtTime(Date startedAtTime) {
        this.startedAtTime = startedAtTime;
    }

    public Date getEndedAtTime() {
        return endedAtTime;
    }

    public void setEndedAtTime(Date endedAtTime) {
        this.endedAtTime = endedAtTime;
    }
}